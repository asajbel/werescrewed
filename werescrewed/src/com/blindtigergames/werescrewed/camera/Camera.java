package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

/*******************************************************************************
 * Camera class. Zooms and translates based on anchors. Max 30 anchors.
 * 
 * @author Edward Ramirez and Dan Malear
 ******************************************************************************/
public class Camera {
	public OrthographicCamera camera;
	public float viewportHeight;
	public float viewportWidth;
	public Vector3 position;
	private Vector2 center2D;
	private Rectangle screenBounds;

	// translation
	// private static final float ACCELERATION_BUFFER_RATIO = .2f;
	/**
	 * A ratio of some sort to determine targetBuffer size
	 */
	private static final float TARGET_BUFFER_RATIO = .005f;
	private static final float MINIMUM_FOLLOW_SPEED = .1f;
	private static final float MAX_ANGLE_DIFF = 100f;
	/**
	 * Time to get to ideal camera position/zoom per hundred pixels of distance
	 */
	private static final float TIME_PER_PIX = .1f;
	private float timeLeft;

	// private float accelerationBuffer;
	private Vector2 translateVelocity;
	private Vector2 translateTarget;
	private float targetBuffer;
	private boolean translating;

	// zoom
	private static final float ZOOM_ACCELERATION = .001f;
	private static final float ZOOM_SIG_DIFF = .00005f;
	private static final float ZOOM_IN_FACTOR = .5f;
	public static final float MIN_ZOOM = 1f;
	public static final float SCREEN_TO_ZOOM = 1468.6f;

	// globals for calculating screen space
	protected static Vector3 CURRENT_CAMERA;
	public static Rectangle CAMERA_RECT;
	static {
		CURRENT_CAMERA = new Vector3( );
		CAMERA_RECT = new Rectangle( );
	}

	// Reading the camera data is fine, but we don't want anyone to write to it.
	public static Vector3 getCurrentCameraCoords( ) {
		return CURRENT_CAMERA.cpy( );
	}

	private enum RectDirection {
		X, Y, BOTH, NONE
	};

	private float zoomSpeed;

	private AnchorList anchorList;

	// debug
	private boolean debugInput;
	private boolean debugRender;
	private ShapeRenderer shapeRenderer;

	public Camera( Vector2 position, float viewportWidth, float viewportHeight,
			World world ) {
		initializeVars( position, viewportWidth, viewportHeight, world );
		camera.update( );
	}

	private void initializeVars( Vector2 position, float viewportWidth,
			float viewportHeight, World world ) {
		camera = new OrthographicCamera( 1, viewportHeight / viewportWidth );
		this.viewportHeight = Gdx.graphics.getHeight( );
		this.viewportWidth = Gdx.graphics.getWidth( );
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set( position.x, position.y, 0f );
		this.position = camera.position;
		center2D = new Vector2( position.x, position.y );
		screenBounds = new Rectangle( position.x - viewportWidth / 2,
				position.y - viewportHeight / 2, viewportWidth, viewportHeight );

		this.translateTarget = new Vector2( center2D );
		translateVelocity = new Vector2( 0f, 0f );
		targetBuffer = ( this.viewportWidth + this.viewportHeight )
				* TARGET_BUFFER_RATIO;
		// accelerationBuffer = ( ( this.viewportWidth * BUFFER_RATIO / 2 +
		// this.viewportHeight
		// * BUFFER_RATIO / 2 ) / 2 )
		// * ACCELERATION_BUFFER_RATIO;
		translating = false;
		zoomSpeed = 0f;
		this.timeLeft = 0;
		camera.zoom = MIN_ZOOM;
		anchorList = AnchorList.getInstance( camera );
		anchorList.clear( );

		// debug
		debugInput = false;
		debugRender = false;
		shapeRenderer = new ShapeRenderer( );
	}

	/**
	 * Sets the camera's position manually (no translation)
	 * 
	 * @param position
	 */
	public void setPosition( Vector2 position ) {
		camera.position.set( position.x, position.y, 0f );
		this.position = camera.position;
		center2D = new Vector2( position.x, position.y );
		screenBounds = new Rectangle( position.x - viewportWidth / 2,
				position.y - viewportHeight / 2, viewportWidth, viewportHeight );
	}

	/**
	 * get boundaries of the screen in a Rectangle
	 * 
	 * @return
	 */
	public Rectangle getBounds( ) {
		return screenBounds;
	}

	public Matrix4 combined( ) {
		return camera.combined;
	}

	/**
	 * update the camera
	 */
	public void update( ) {
		// Tracks player holding "B"
		debugInput = false;
		// Tracks player holding "N"
		debugRender = false;
		// debugMode = true;
		// check debug keys
		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			debugInput = true;// now camera is a toggle
		}
		if ( Gdx.input.isKeyPressed( Keys.N ) ) {
			debugRender = true;
		}

		// update all positions and dimensions
		position = camera.position;
		center2D.x = position.x;
		center2D.y = position.y;
		screenBounds.width = camera.zoom * viewportWidth;
		screenBounds.height = camera.zoom * viewportHeight;
		screenBounds.x = position.x - screenBounds.width / 2;
		screenBounds.y = position.y - screenBounds.height / 2;

		if ( debugInput ) {
			handleInput( );
		} else {
			setTranslateTarget( );
			// Do the actual translation and zooming
			adjustCamera( );
		}

		// render buffers areas anchors
		if ( debugRender ) {
			renderBuffers( );
		}

		camera.update( );

		// also render anchors if debugRender == true
		anchorList.update( debugRender );

		CURRENT_CAMERA.x = position.x;
		CURRENT_CAMERA.y = position.y;
		CURRENT_CAMERA.z = camera.zoom;

		CAMERA_RECT = screenBounds;
	}

	/**
	 * Set focus of camera to the midpoint of all anchors
	 */
	private void setTranslateTarget( ) {
		translateTarget.x = anchorList.getMidpoint( ).x;
		translateTarget.y = anchorList.getMidpoint( ).y;
	}

	/**
	 * Adjust the camera by translating and zooming when necessary
	 */
	private void adjustCamera( ) {
		// Translate and zoom
		translateLogic( );
		zoom( );

	}

	/**
	 * Either translate, lock, or do nothing based on various buffers and
	 * positions
	 */
	private void translateLogic( ) {

		// DETERMINE IF BUFFERS HAVE LEFT THE SCREEN //

		// Track the status of buffers
		boolean outside = false;

		// Iterate through each anchor
		for ( Anchor curAnchor : anchorList.anchorList ) {
			// Only consider active anchors
			if ( curAnchor.activated ) {
				// Find the direction in which the buffer of the current anchor
				// has exited the screen
				RectDirection dir = rectOutsideRect(
						curAnchor.getBufferRectangle( ), screenBounds );

				// Check that a buffer has indeed exited the screen
				if ( dir != RectDirection.NONE ) {

					outside = true;

					// If we know buffers have left the screen we don't need to
					// check anymore.
					if ( outside )
						break;
				}
			}
		}

		// If a buffer has left the screen
		if ( !translating && outside ) {
			translating = true;
		}

		// DETERMINE WHETHER TO LOCK, TRANSLATE, OR DO NOTHING //

		if ( translating ) {

			boolean lock = false;

			// Lock when:
			if ( Math.abs( translateTarget.x - center2D.x ) < targetBuffer
					&& Math.abs( translateTarget.y - center2D.y ) < targetBuffer ) {
				// camera center is really close to target
				lock = true;
			}

			// Center of camera is within buffer from target, so camera
			// locks to target
			if ( lock ) {
				this.timeLeft = 0;

				if ( lock ) {
					camera.position.x = translateTarget.x;
					camera.position.y = translateTarget.y;
				}

				// find angle between midpoint velocity and translate velocity
				float tempAngle = 0f;
				tempAngle = anchorList.getMidpointVelocity( ).angle( )
						- translateVelocity.angle( );
				tempAngle = Math.abs( tempAngle );
				if ( tempAngle > 180 ) {
					tempAngle = 360 - tempAngle;
				}
				// if target stops moving or changes direction abruptly, disable
				// lock
				if ( anchorList.getMidpointVelocity( ).len( ) < MINIMUM_FOLLOW_SPEED
						|| tempAngle > MAX_ANGLE_DIFF ) {
					translating = false;
					translateVelocity.x = 0f;
					translateVelocity.y = 0f;
				}
			} else {
				translate( );
			}
			timeLeft--;
		} else {
			translateVelocity.x = 0f;
			translateVelocity.y = 0f;
		}
	}

	/**
	 * Translate the camera towards the translate target
	 */
	private void translate( ) {
		// only account for translate target on relevant axes

		Vector2 distance = new Vector2( );
		distance.x = translateTarget.x;
		distance.y = translateTarget.y;

		// the difference between the translate target and the
		// center of the camera on only the relevant axes
		distance = new Vector2( distance.x - center2D.x, distance.y
				- center2D.y );

		if ( Math.abs( distance.x ) < targetBuffer
				&& Math.abs( distance.y ) < targetBuffer ) {
			translateVelocity.x = 0f;
			translateVelocity.y = 0f;
			timeLeft = 0;
			translating = false;
			return;
		}

		// Manage time

		if ( distance.len( ) > targetBuffer * 1.25 ) {
			timeLeft = TIME_PER_PIX * Math.abs( distance.len( ) );
		} else {
			timeLeft = TIME_PER_PIX * Math.abs( distance.len( ) ) * 3;
		}

		Vector2 acceleration = calcAcceleration( distance );
		if ( Math.abs( this.translateVelocity.x + acceleration.x ) > Math
				.abs( distance.x ) ) {
			this.translateVelocity.x = distance.x;
			acceleration.x = 0;
		}
		if ( Math.abs( this.translateVelocity.y + acceleration.y ) > Math
				.abs( distance.y ) ) {
			this.translateVelocity.y = distance.y;
			acceleration.y = 0;
		}
		this.translateVelocity.add( acceleration );
		this.camera.translate( this.translateVelocity );
	}

	/**
	 * Get the acceleration for this step, based on timeLeft
	 * 
	 * @param dist
	 *            the distance between the camera's current position and the
	 *            target position
	 * @return acceleration for this step
	 */
	private Vector2 calcAcceleration( Vector2 dist ) {
		Vector2 acceleration = new Vector2( 0, 0 );
		acceleration.x = dist.x;
		acceleration.y = dist.y;
		acceleration.div( this.timeLeft );
		acceleration.sub( this.translateVelocity );
		acceleration.div( this.timeLeft );
		return acceleration;
	}

	/**
	 * Get the zoom acceleration for this step, based on timeLeft
	 * 
	 * @param zoomChange
	 *            the difference between the target zoom and the current zoom
	 * @return zoom acceleration for this step
	 */
	private float calcZoomAcc( float zoomChange ) {
		float zoomAccel = 0;
		zoomAccel = zoomChange;
		zoomAccel /= this.timeLeft;
		zoomAccel -= this.zoomSpeed;
		zoomAccel /= this.timeLeft;
		return zoomAccel;
	}

	/**
	 * zoom out or in depending on anchor buffer rectangles
	 * 
	 * @param modifier
	 *            modifies zoom rate
	 */
	private void zoom( ) {
		float newZoom = 1f;
		Vector2 longestDist = anchorList.getLongestXYDist( );
		Vector2 distFromEdge = new Vector2( longestDist.x - screenBounds.width,
				longestDist.y - screenBounds.height );

		if ( distFromEdge.x > distFromEdge.y ) {
			newZoom = longestDist.x / viewportWidth;
		} else if ( distFromEdge.y > distFromEdge.x ) {
			newZoom = longestDist.y / viewportHeight;
		}

		// If targetZoom is too zoomed-in, set to MIN_ZOOM
		if ( newZoom < MIN_ZOOM ) {
			newZoom = MIN_ZOOM;
		}

		zoomSteer( newZoom );
	}

	/**
	 * steer zoom to the new zoom
	 * 
	 * @param targetZoom
	 */
	private void zoomSteer( float targetZoom ) {
		// if difference is small enough, set speed to zero
		float newZoom = camera.zoom;
		if ( Math.abs( newZoom - targetZoom ) < ZOOM_SIG_DIFF ) {
			zoomSpeed = 0;
			return;
		}

		float zoomChange = newZoom = targetZoom;

		// Accelerate zoom
		if ( zoomChange != 0 ) {
			zoomSpeed += calcZoomAcc( zoomChange );
		} else {
			zoomSpeed = 0;
		}

		// Zoom out
		if ( targetZoom > camera.zoom ) {
			if ( ( camera.zoom + zoomSpeed ) < ( targetZoom - ZOOM_SIG_DIFF ) )
				newZoom += zoomSpeed;
			else
				newZoom = targetZoom;
		}

		// Zoom in
		if ( targetZoom < camera.zoom ) {
			if ( ( camera.zoom - zoomSpeed ) > ( targetZoom + ZOOM_SIG_DIFF ) )
				newZoom -= zoomSpeed;
			else
				newZoom = targetZoom;
		}

		camera.zoom = newZoom;
	}

	/**
	 * Determine if any part of rect1 is outside of rect2
	 * 
	 * @param rect1
	 *            inner Rectangle
	 * @param rect2
	 *            outer Rectangle
	 * @return true if any part of rect1 is outside of rect2, false otherwise
	 */
	private RectDirection rectOutsideRect( Rectangle rect1, Rectangle rect2 ) {
		RectDirection returnDir = RectDirection.NONE;

		if ( rect1.x < rect2.x
				|| ( rect1.x + rect1.width ) > ( rect2.x + rect2.width ) )
			returnDir = RectDirection.X;
		if ( rect1.y < rect2.y
				|| ( rect1.y + rect1.height ) > ( rect2.y + rect2.height ) ) {
			if ( returnDir == RectDirection.X )
				returnDir = RectDirection.BOTH;
			else
				returnDir = RectDirection.Y;
		}

		return returnDir;
	}

	/**
	 * Render rectangles and circles representing various buffers. Use for
	 * camera debugging.
	 */
	public void renderBuffers( ) {
		shapeRenderer.setProjectionMatrix( camera.combined );

		// renders a cross through the square
		shapeRenderer.begin( ShapeType.Line );
		shapeRenderer.line( screenBounds.x, screenBounds.y, screenBounds.x
				+ screenBounds.width, screenBounds.y + screenBounds.height );
		shapeRenderer.line( screenBounds.x, screenBounds.y
				+ screenBounds.height, screenBounds.x + screenBounds.width,
				screenBounds.y );
		shapeRenderer.end( );

		// bounding box
		// shapeRenderer.begin( ShapeType.Line );
		// shapeRenderer.line( getBounds( ).x + 20, getBounds( ).y + 20,
		// getBounds( ).x + getBounds( ).width - 20, getBounds( ).y + 20 );
		// shapeRenderer.line( getBounds( ).x + 20, getBounds( ).y
		// + getBounds( ).height,
		// getBounds( ).x + getBounds( ).width - 20, getBounds( ).y
		// + getBounds( ).height );
		//
		// shapeRenderer.line( getBounds( ).x + 20, getBounds( ).y + 20,
		// getBounds( ).x + 20, getBounds( ).y + getBounds( ).height - 20 );
		// shapeRenderer.line( getBounds( ).x + getBounds( ).width - 20,
		// getBounds( ).y + 20, getBounds( ).x + getBounds( ).width - 20,
		// getBounds( ).y + getBounds( ).height - 20 );
		// shapeRenderer.end( );

		// render the translation target buffer
		shapeRenderer.begin( ShapeType.Circle );
		shapeRenderer.identity( );
		shapeRenderer.circle( translateTarget.x, translateTarget.y,
				targetBuffer );
		shapeRenderer.end( );

		// render the acceleration target buffer
		// shapeRenderer.begin( ShapeType.Circle );
		// shapeRenderer.identity( );
		// shapeRenderer.circle( translateTarget.x, translateTarget.y,
		// accelerationBuffer );
		// shapeRenderer.end( );
	}

	/**
	 * Logic for handling debug input while "B" is pressed
	 */
	private void handleInput( ) {
		if ( Gdx.input.isKeyPressed( Input.Keys.E ) ) {
			camera.zoom += 0.2;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.Q ) ) {
			camera.zoom -= 0.2;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.LEFT ) ) {
			camera.translate( -10, 0, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) ) {
			camera.translate( 10, 0, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.DOWN ) ) {
			camera.translate( 0, -10, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.UP ) ) {
			camera.translate( 0, 10, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_3 ) ) {
			camera.zoom = .5f;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_1 ) ) {
			camera.zoom = 1f;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_2 ) ) {
			camera.zoom = 2f;
		}
	}
}