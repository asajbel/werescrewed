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
	private static final float TARGET_BUFFER_RATIO = .0025f;
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
	private boolean translateState;
	private boolean insideTargetBuffer;

	// zoom
	private static final float ZOOM_ACCELERATION = .001f;
	private static final float ZOOM_MAX_SPEED = 15f;
	private static final float ZOOM_SIG_DIFF = .00005f;
	private static final float ZOOM_IN_FACTOR = .5f;
	private static final float MIN_ZOOM = 1f;

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
		translateState = false;
		insideTargetBuffer = false;
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
		if ( debugInput )
			handleInput( );

		// update all positions and dimensions
		position = camera.position;
		center2D.x = position.x;
		center2D.y = position.y;
		screenBounds.width = camera.zoom * viewportWidth;
		screenBounds.height = camera.zoom * viewportHeight;
		screenBounds.x = position.x - screenBounds.width / 2;
		screenBounds.y = position.y - screenBounds.height / 2;

		setTranslateTarget( );

		// check if center is inside target buffer
		float dist = Math.abs( center2D.dst( translateTarget ) );
		if ( dist < targetBuffer ) {
			insideTargetBuffer = true;
		} else {
			insideTargetBuffer = false;
		}

		// Do the actual translation and zooming
		adjustCamera( );

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
	 * Adjust the camera by translating and zooming when necessary
	 */
	private void adjustCamera( ) {
		// Track the status of buffers
		boolean outside_x = false;
		boolean outside_y = false;

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

					// Find whether buffer is outside in x, y or both directions
					if ( dir == RectDirection.BOTH ) {
						outside_x = true;
						outside_y = true;
					} else if ( dir == RectDirection.X )
						outside_x = true;
					else
						outside_y = true;

					// If we know buffers have left in both x and y directions,
					// we don't need to check anymore.
					if ( outside_x && outside_y )
						break;
				}
			}
		}

		// If a buffer has left the screen
		if ( outside_x || outside_y ) {
			// And if the center of the camera is outside of the center of the
			// midpoint (Not just opposite sides)
			if ( !insideTargetBuffer ) {
				// Then we translate
				translateState = true;
			}
		}
		// If the button to debug input isn't being held, translate and zoom
		if ( !debugInput ) {
			translateLogic(
					outside_x
							|| Math.abs( translateVelocity.x ) > MINIMUM_FOLLOW_SPEED,
					outside_y
							|| Math.abs( translateVelocity.y ) > MINIMUM_FOLLOW_SPEED );
			zoom( );
		}
	}

	/**
	 * Either translate, lock, or do nothing based on various buffers and
	 * positions
	 */
	private void translateLogic( boolean trans_x, boolean trans_y ) {
		// determine whether to translate, lock, or do nothing
		if ( translateState ) {

			boolean lockX = false;
			boolean lockY = false;

			// lock when:
			if ( trans_x
					&& Math.abs( translateTarget.x - center2D.x ) < targetBuffer ) {
				// camera center is really close to target.x when only
				// translating on x axis
				lockX = true;
			} else if ( trans_y
					&& Math.abs( translateTarget.y - center2D.y ) < targetBuffer ) {
				// camera center is really close to target.y when only
				// translating on y axis
				lockY = true;
			}

			// center of camera is within buffer from target, so camera
			// locks to target
			if ( lockX || lockY ) {
				this.timeLeft = 0;

				if ( lockX )
					camera.position.x = translateTarget.x;
				if ( lockY )
					camera.position.y = translateTarget.y;

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
					translateState = false;
					translateVelocity.x = 0f;
					translateVelocity.y = 0f;
				}
			} else {
				translate( trans_x, trans_y );
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
	private void translate( boolean trans_x, boolean trans_y ) {
		// only account for translate target on relevant axes

		Vector2 relevantDist = new Vector2( );
		if ( trans_x || trans_y ) {
			// If a buffer has exited the screen
			if ( trans_x ) {
				// In the x direction
				relevantDist.x = translateTarget.x;
			} else {
				// Only in the y direction
				relevantDist.x = center2D.x;
			}
			if ( trans_y ) {
				// In the y direction
				relevantDist.y = translateTarget.y;
			} else {
				// Only in the x direction
				relevantDist.y = center2D.y;
			}
		} else {
			// All buffers in screen
			if ( Math.abs( translateVelocity.x ) > 0 ) {
				// If we were moving in the x-direction, keep moving
				relevantDist.x = translateTarget.x;
			} else {
				relevantDist.x = center2D.x;
			}
			if ( Math.abs( translateVelocity.y ) > 0 ) {
				// If we were moving in the y-direction, keep moving
				relevantDist.y = translateTarget.y;
			} else {
				relevantDist.y = center2D.y;
			}
		}

		// the difference between the translate target and the
		// center of the camera on only the relevant axes
		relevantDist = new Vector2( relevantDist.x - center2D.x, relevantDist.y
				- center2D.y );

		if ( Math.abs( relevantDist.x ) < targetBuffer
				&& Math.abs( relevantDist.y ) < targetBuffer ) {
			translateVelocity.x = 0f;
			translateVelocity.y = 0f;
			timeLeft = 0;
			translateState = false;
			return;
		}

		// Manage time

		if ( timeLeft <= 0 ) {
			// timeLeft being zero while still translating means we've just
			// started moving
			timeLeft = TIME_PER_PIX * Math.abs( relevantDist.len( ) );
		} else {
			// Otherwise, just add the time to cover the new distance
			timeLeft += Math.abs( AnchorList.getInstance( )
					.getMidpointVelocity( ).len( ) )
					* TIME_PER_PIX;
		}

		Vector2 acceleration = calcAcceleration( relevantDist );
		if ( Math.abs( this.translateVelocity.x + acceleration.x ) > Math
				.abs( relevantDist.x ) ) {
			this.translateVelocity.x = relevantDist.x;
			acceleration.x = 0;
		}
		if ( Math.abs( this.translateVelocity.y + acceleration.y ) > Math
				.abs( relevantDist.y ) ) {
			this.translateVelocity.y = relevantDist.y;
			acceleration.y = 0;
		}
		this.translateVelocity.add( acceleration );
		this.camera.translate( this.translateVelocity );
	}

	/**
	 * Get the acceleration for this step, based on timeLeft
	 * 
	 * @param relevantDist
	 *            the distance between the camera's current position and the
	 *            target position on only relevant axes
	 * @return acceleration for this step
	 */
	private Vector2 calcAcceleration( Vector2 relevantDist ) {
		Vector2 acceleration = new Vector2( 0, 0 );
		acceleration.x = relevantDist.x;
		acceleration.y = relevantDist.y;
		acceleration.div( this.timeLeft );
		acceleration.sub( this.translateVelocity );
		acceleration.div( this.timeLeft );
		return acceleration;
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

		if ( zoomSpeed < ZOOM_MAX_SPEED ) {
			zoomSteer( newZoom );
		}
	}

	/**
	 * steer zoom to the new zoom
	 * 
	 * @param targetZoom
	 */
	private void zoomSteer( float targetZoom ) {
		// if difference is small enough, set speed to zero
		float newZoom = camera.zoom;
		if ( Math.abs( camera.zoom - targetZoom ) < ZOOM_SIG_DIFF )
			zoomSpeed = 0;

		// accelerate zoom
		zoomSpeed += ZOOM_ACCELERATION;

		// use speed to zoom out
		if ( targetZoom > camera.zoom ) {
			if ( ( camera.zoom + zoomSpeed ) < ( targetZoom - .001f ) )
				newZoom += zoomSpeed;
			else
				newZoom = targetZoom;
		}

		// if zooming in, use slower (half maybe) speed
		if ( targetZoom < camera.zoom ) {
			if ( ( camera.zoom - zoomSpeed * ZOOM_IN_FACTOR ) > ( targetZoom + .001f ) )
				newZoom -= zoomSpeed * ZOOM_IN_FACTOR;
			else
				newZoom = targetZoom;
		}

		if ( newZoom > MIN_ZOOM )
			camera.zoom = newZoom;
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