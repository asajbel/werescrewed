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

	// Translation
	private static final float TARGET_BUFFER_RATIO = .003f;
	private static final int MILLISECONDS = 1500;
	private int timeLeft;

	private Vector2 translateVelocity;
	private Vector2 translateTarget;
	private float targetBuffer;
	private int prevActiveAnchors;
	private int currActiveAnchors;
	private boolean steering;

	// Zoom
	private static final float ZOOM_SIG_DIFF = 0.003f;
	public static final float MIN_ZOOM = 1f;
	public static final float MAX_ZOOM = 16f;
	public static final float SCREEN_TO_ZOOM = 1468.6f;
	private int fps = 60;

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
	private Vector2 distance;
	private float targetZoom;
	private float zoomChange;
	private boolean close;
	private Vector2 initialDistance;

	public Camera( Vector2 position, float viewportWidth, float viewportHeight,
			World world ) {
		initializeVars( position, viewportWidth, viewportHeight, world );
		camera.update( );
	}

	private void initializeVars( Vector2 position, float viewportWidth,
			float viewportHeight, World world ) {
		this.camera = new OrthographicCamera( 1, viewportHeight / viewportWidth );
		this.viewportHeight = Gdx.graphics.getHeight( );
		this.viewportWidth = Gdx.graphics.getWidth( );
		this.camera.viewportWidth = this.viewportWidth;
		this.camera.viewportHeight = this.viewportHeight;
		this.camera.position.set( position.x, position.y, 0f );
		this.position = camera.position;
		this.center2D = new Vector2( position.x, position.y );
		this.screenBounds = new Rectangle( position.x - viewportWidth / 2,
				position.y - viewportHeight / 2, viewportWidth, viewportHeight );

		this.translateTarget = new Vector2( center2D );
		this.translateVelocity = new Vector2( 0f, 0f );
		this.targetBuffer = ( this.viewportWidth + this.viewportHeight )
				* TARGET_BUFFER_RATIO;
		this.steering = false;
		this.zoomSpeed = 0f;
		this.timeLeft = 0;
		this.camera.zoom = MIN_ZOOM;
		this.targetZoom = MIN_ZOOM;
		this.anchorList = AnchorList.getInstance( camera );
		this.anchorList.clear( );

		this.prevActiveAnchors = 0;
		this.currActiveAnchors = 0;

		this.distance = new Vector2( 0, 0 );
		this.zoomChange = 0;
		this.initialDistance = new Vector2( 0, 0 );

		// debug
		this.debugInput = false;
		this.debugRender = false;
		this.shapeRenderer = new ShapeRenderer( );
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
//		screenBounds.x = screenBounds.x + 5;
//		screenBounds.width = screenBounds.width -5f;
		return screenBounds;
	}

	public Matrix4 combined( ) {
		return camera.combined;
	}

	/**
	 * @param deltaTime
	 *            The amount of time between this frame and the last
	 */
	public void update( float deltaTime ) {
		// Tracks player holding "B"
		debugInput = false;
		// Tracks player holding "N"
		debugRender = false;
		// check debug keys
		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			debugInput = true;// now camera is a toggle
		}
		if ( Gdx.input.isKeyPressed( Keys.N ) ) {
			debugRender = true;
		}

		anchorList.update( debugRender );

		if ( deltaTime != 0 ) {
			fps = ( int ) ( 1 / deltaTime );
			if ( fps == 0 )
				fps = 1;

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
				// Set the target camera state
				setTranslateTarget( );
				// Check anchor differences
				currActiveAnchors = AnchorList.getInstance( )
						.getNumActiveAnchors( );
				// Do the actual translation and zooming
				adjustCamera( deltaTime );
				prevActiveAnchors = currActiveAnchors;
			}

			// render buffers areas anchors
			if ( debugRender ) {
				renderBuffers( );
			}

			camera.update( );

			CURRENT_CAMERA.x = position.x;
			CURRENT_CAMERA.y = position.y;
			CURRENT_CAMERA.z = camera.zoom;

			CAMERA_RECT = screenBounds;
		}
	}

	/**
	 * Set focus of camera to the midpoint of all anchors
	 */
	private void setTranslateTarget( ) {
		translateTarget.x = anchorList.getMidpoint( ).x;
		translateTarget.y = anchorList.getMidpoint( ).y;

		targetZoom = 1f;
		Vector2 longestDist = anchorList.getLongestXYDist( );
		Vector2 distFromEdge = new Vector2( longestDist.x - screenBounds.width,
				longestDist.y - screenBounds.height );

		if ( distFromEdge.x > distFromEdge.y ) {
			targetZoom = longestDist.x / viewportWidth;
		} else if ( distFromEdge.y > distFromEdge.x ) {
			targetZoom = longestDist.y / viewportHeight;
		}

		// If targetZoom is too zoomed-in/out, set to MIN/MAX_ZOOM
		if ( targetZoom < MIN_ZOOM ) {
			targetZoom = MIN_ZOOM;
		}

		if ( targetZoom > MAX_ZOOM ) {
			targetZoom = MAX_ZOOM;
		}
	}

	/**
	 * Movement and zooming
	 */
	private void adjustCamera( float deltaTime ) {
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
		if ( !steering && currActiveAnchors != prevActiveAnchors
				&& ( outside || camera.zoom > targetZoom + ZOOM_SIG_DIFF ) ) {
			startSteering( );
		}

		if ( steering ) {
			steer( );
			timeLeft -= deltaTime * 1000;
		} else {
			camera.position.x = translateTarget.x;
			camera.position.y = translateTarget.y;
			camera.zoom = targetZoom;
		}
	}

	/**
	 * Initialize movement
	 */
	private void startSteering( ) {
		// Set state to true
		steering = true;
		timeLeft = MILLISECONDS;
		close = false;
		initialDistance = new Vector2( translateTarget.x, translateTarget.y )
				.sub( center2D );
	}

	/**
	 * Do movement stuff.
	 */
	private void steer( ) {
		// UPDATE DISTANCE //

		// distance is difference between current position and target position
		distance = new Vector2( translateTarget.x, translateTarget.y );
		distance.sub( center2D );

		// If it's close enough, just set it to the center
		if ( distance.len( ) < targetBuffer ) {
			distance.x = distance.y = 0;
		} else if ( distance.len( ) < initialDistance.len( ) * .6
				&& close == false ) {
			timeLeft += MILLISECONDS / 2;
			close = true;
		} else if ( distance.len( ) > initialDistance.len( ) * .6
				&& close == true ) {
			timeLeft += MILLISECONDS / 2;
			close = false;
		}

		// UPDATE TARGET ZOOM //

		// zoomChange is difference between current zoom and target zoom
		zoomChange = targetZoom - camera.zoom;

		// TRANSLATE AND ZOOM //

		if ( Math.abs( distance.len( ) ) > targetBuffer ) {
			translate( );
		} else {
			camera.position.x = translateTarget.x;
			camera.position.y = translateTarget.y;
		}

		if ( ( camera.zoom ) < ( targetZoom - ZOOM_SIG_DIFF )
				|| ( camera.zoom ) > ( targetZoom + ZOOM_SIG_DIFF ) ) {
			zoom( );
		} else {
			camera.zoom = targetZoom;
		}

		if ( camera.position.x == translateTarget.x
				&& camera.position.y == translateTarget.y
				&& camera.zoom == targetZoom ) {
			stopSteering( );
		}
	}

	/**
	 * End all movement
	 */
	private void stopSteering( ) {
		translateVelocity.x = 0f;
		translateVelocity.y = 0f;
		zoomSpeed = 0;
		timeLeft = 0;
		steering = false;
	}

	/**
	 * Translate the camera towards the translate target
	 */
	private void translate( ) {

		Vector2 acceleration = calcAcceleration( distance );

		// If the acceleration would take the camera past its destination,
		// adjust velocity to take it exactly to the target
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
	 * Zoom out or in depending on anchor buffer rectangles
	 */
	private void zoom( ) {
		boolean zoomOut = false;

		if ( camera.zoom < targetZoom ) {
			zoomOut = true;
		}

		// Accelerate zoom
		float zoomAccel = 0;
		if ( zoomChange != 0 ) {
			zoomAccel = calcZoomAcc( zoomChange );
			zoomSpeed += zoomAccel;
		} else {
			zoomSpeed = 0;
			return;
		}

		float newZoom = camera.zoom;

		if ( ( zoomOut && newZoom + zoomSpeed > targetZoom )
				|| ( !zoomOut && newZoom + zoomSpeed < targetZoom ) ) {
			zoomSpeed = zoomChange;
		}
		newZoom += zoomSpeed;

		if ( newZoom < MIN_ZOOM ) {
			newZoom = MIN_ZOOM;
		}

		if ( newZoom > MAX_ZOOM ) {
			newZoom = MAX_ZOOM;
		}

		if ( Math.abs( zoomSpeed ) > ZOOM_SIG_DIFF * 2
				&& Math.abs( zoomChange ) < ZOOM_SIG_DIFF * 1.5 ) {
			newZoom = targetZoom;
		}

		camera.zoom = newZoom;
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
		int framesLeft = fps * timeLeft / 1000;
		Vector2 acceleration = new Vector2( 0, 0 );
		acceleration.x = dist.x;
		acceleration.y = dist.y;
		Vector2.tmp.x = this.translateVelocity.x;
		Vector2.tmp.y = this.translateVelocity.y;
		Vector2.tmp.mul( framesLeft );
		acceleration.sub( Vector2.tmp );
		acceleration.div( framesLeft * framesLeft );
		acceleration.mul( 2 );
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
		int framesLeft = fps * timeLeft / 1000;
		float zoomAccel = 0;
		if ( framesLeft != 0 ) {
			zoomAccel = zoomChange;
			zoomAccel -= zoomSpeed * framesLeft;
			zoomAccel /= framesLeft * framesLeft;
			zoomAccel *= 2;
		}
		return zoomAccel;
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

		if ( rect1.x < ( rect2.x - targetBuffer )
				|| ( rect1.x + rect1.width ) > ( rect2.x + rect2.width + targetBuffer ) )
			returnDir = RectDirection.X;
		if ( rect1.y < ( rect2.y - targetBuffer )
				|| ( rect1.y + rect1.height ) > ( rect2.y + rect2.height + targetBuffer ) ) {
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

		// bounding box
		shapeRenderer.begin( ShapeType.Line );
		shapeRenderer.line( getBounds( ).x + 20, getBounds( ).y + 20,
				getBounds( ).x + getBounds( ).width - 20, getBounds( ).y + 20 );
		shapeRenderer.line( getBounds( ).x + 20, getBounds( ).y
				+ getBounds( ).height - 20, getBounds( ).x + getBounds( ).width
				- 20, getBounds( ).y + getBounds( ).height - 20 );

		shapeRenderer.line( getBounds( ).x + 20, getBounds( ).y + 20,
				getBounds( ).x + 20, getBounds( ).y + getBounds( ).height - 20 );
		shapeRenderer.line( getBounds( ).x + getBounds( ).width - 20,
				getBounds( ).y + 20, getBounds( ).x + getBounds( ).width - 20,
				getBounds( ).y + getBounds( ).height - 20 );
		shapeRenderer.end( );

		// renders a cross through the square
		shapeRenderer.begin( ShapeType.Line );
		shapeRenderer.line( screenBounds.x, screenBounds.y, screenBounds.x
				+ screenBounds.width, screenBounds.y + screenBounds.height );
		shapeRenderer.line( screenBounds.x, screenBounds.y
				+ screenBounds.height, screenBounds.x + screenBounds.width,
				screenBounds.y );
		shapeRenderer.end( );

		// render the translation target buffer
		shapeRenderer.begin( ShapeType.Circle );
		shapeRenderer.identity( );
		shapeRenderer.circle( translateTarget.x, translateTarget.y,
				targetBuffer );
		shapeRenderer.end( );

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
			camera.translate( -30, 0, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) ) {
			camera.translate( 30, 0, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.DOWN ) ) {
			camera.translate( 0, -30, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.UP ) ) {
			camera.translate( 0, 30, 0 );
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