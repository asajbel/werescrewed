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
	// private static final float SPEED_TARGET_MODIFIER = 5f;
	private static final float BUFFER_RATIO = .5f;
	// private static final float ACCELERATION_RATIO = .0005f;
	// private static final float DECELERATION_RATIO = .004f;
	private static final float ACCELERATION_BUFFER_RATIO = .2f;
	private static final float TARGET_BUFFER_RATIO = .03f;
	private static final float MINIMUM_FOLLOW_SPEED = .1f;
	private static final float MAX_ANGLE_DIFF = 100f;
	private static final float MAX_SPEED = 100f;
	private static final float MIN_SPEED = 10f;
	private static final float ACCELERATION = .015f;
	private static final float DECELERATION = -.5f;
	private float accelerationBuffer;
	private Vector2 translateVelocity;
	private float translateSpeed;
	private float translateAcceleration;
	private Rectangle translateBuffer;
	private Vector2 translateTarget;
	private Vector3 translateTarget3D;
	private float targetBuffer;
	private boolean translateState;
	private boolean insideTargetBuffer;

	// zoom
	private static final float ZOOM_ACCELERATION = .001f;
	private static final float ZOOM_MAX_SPEED = 15f;
	private static final float ZOOM_SIG_DIFF = .00005f;
	private static final float ZOOM_IN_FACTOR = .5f;
	private static final float MIN_ZOOM = 1f;

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

		this.translateBuffer = new Rectangle( camera.position.x,
				camera.position.y, this.viewportWidth * BUFFER_RATIO,
				this.viewportHeight * BUFFER_RATIO );

		this.translateTarget = new Vector2( center2D );
		this.translateTarget3D = new Vector3( translateTarget.x,
				translateTarget.y, 0f );
		translateVelocity = new Vector2( 0f, 0f );
		translateSpeed = 0f;
		translateAcceleration = 0f;
		targetBuffer = ( ( translateBuffer.width + translateBuffer.height ) / 2 )
				* TARGET_BUFFER_RATIO;
		accelerationBuffer = ( ( translateBuffer.width / 2 + translateBuffer.height / 2 ) / 2 )
				* ACCELERATION_BUFFER_RATIO;
		translateState = true;
		insideTargetBuffer = false;
		zoomSpeed = 0f;
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

		translateBuffer.x = position.x - translateBuffer.width * .5f;
		translateBuffer.y = position.y - translateBuffer.height * .5f;
		setTranslateTarget( );

		// check if center is inside target buffer
		if ( center2D.dst( translateTarget ) < targetBuffer )
			insideTargetBuffer = true;
		else
			insideTargetBuffer = false;

		// Do the actual translation and zooming
		adjustCamera( );

		// render buffers areas anchors
		if ( debugRender ) {
			renderBuffers( );
		}

		camera.update( );

		// also render anchors if debugRender == true
		anchorList.update( debugRender );
	}

	/**
	 * Set focus of camera to the midpoint of all anchors
	 */
	private void setTranslateTarget( ) {
		translateTarget.x = anchorList.getMidpoint( ).x;
		translateTarget.y = anchorList.getMidpoint( ).y;

		translateTarget3D.x = translateTarget.x;
		translateTarget3D.y = translateTarget.y;
		translateTarget3D.z = 0f;
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
		// Does translate stuff
		translateLogic( outside_x, outside_y );
		// If the button to debug input isn't being held, zoom
		if ( !debugInput )
			zoom( );
	}

	/**
	 * Either translate, lock, or do nothing based on various buffers and
	 * positions
	 */
	private void translateLogic( boolean trans_x, boolean trans_y ) {
		// determine whether to translate, lock, or do nothing
		if ( !debugInput && translateState ) {
			boolean lock = false;

			// lock when:
			if ( insideTargetBuffer )
				// camera center is really close to target
				lock = true;
			else if ( trans_x
					// camera center is really close to target.x when only
					// translating on x axis
					&& Math.abs( translateTarget.x - center2D.x ) < targetBuffer
					&& !trans_y )
				lock = true;
			else if ( trans_y
					&& Math.abs( translateTarget.y - center2D.y ) < targetBuffer
					&& !trans_x )
				// camera center is really close to target.y when only
				// translating on y axis
				lock = true;

			// center of camera is within buffer from target, so camera
			// locks to target
			if ( lock ) {
				// find angle between midpoint velocity and translate velocity
				float tempAngle = 0f;
				tempAngle = anchorList.getMidpointVelocity( ).angle( )
						- translateVelocity.angle( );
				tempAngle = Math.abs( tempAngle );
				if ( tempAngle > 180 ) {
					tempAngle = 360 - tempAngle;
				}
				if ( trans_x || trans_y ) {
					if ( trans_x )
						camera.position.x = translateTarget.x;
					if ( trans_y )
						camera.position.y = translateTarget.y;
				} else {
					camera.position.x = translateTarget.x;
					camera.position.y = translateTarget.y;
				}
				// if player stops moving or changes direction abruptly, disable
				// lock
				if ( anchorList.getMidpointVelocity( ).len( ) < MINIMUM_FOLLOW_SPEED
						|| tempAngle > MAX_ANGLE_DIFF ) {
					translateState = false;
					translateVelocity.x = 0f;
					translateVelocity.y = 0f;
					translateAcceleration = 0f;
					translateSpeed = 0f;
				}
			} else
				translate( trans_x, trans_y );
		} else if ( !translateState ) {
			translateVelocity.x = 0f;
			translateVelocity.y = 0f;
			translateAcceleration = 0f;
			translateSpeed = 0f;
			if ( !translateBuffer.contains( translateTarget.x,
					translateTarget.y ) )
				translateState = true;
		}
	}

	/**
	 * translate the camera towards the translate target
	 */
	private void translate( boolean trans_x, boolean trans_y ) {
		// only account for translate target on relevant axes

		Vector2 temp = new Vector2( );
		if ( trans_x || trans_y ) {
			// If a buffer has exited the screen
			if ( trans_x ) {
				// In the x direction
				temp.x = translateTarget.x;
			} else {
				// Only in the y direction
				temp.x = center2D.x;
			}
			if ( trans_y ) {
				// In the y direction
				temp.y = translateTarget.y;
			} else {
				// Only in the x direction
				temp.y = center2D.y;
			}
		} else {
			// Buffer still in screen
			temp.x = translateTarget.x;
			temp.y = translateTarget.y;
		}

		// temp is now the difference between the translate target and the
		// center of the camera on only the relevant axes
		temp = new Vector2( temp.x - center2D.x, temp.y - center2D.y );

		// Handles acceleration - accelerate when not close, decelerate when
		// close
		if ( temp.len( ) > accelerationBuffer ) {
			// translateAcceleration = ( Vector2.tmp.len( ) * ACCELERATION_RATIO
			// );
			translateAcceleration = ACCELERATION;

		} else {
			// translateAcceleration = -1f * Vector2.tmp.len( )
			// * DECELERATION_RATIO;
			translateAcceleration = DECELERATION;
		}

		float nextSpeed = translateSpeed + translateAcceleration;
		if ( nextSpeed < temp.len( ) ) {
			// If the speed wouldn't take the camera past the target
			if ( nextSpeed > MIN_SPEED && nextSpeed < MAX_SPEED ) {
				// If we're in range, go ahead and ac/decelerate
				translateSpeed = nextSpeed;
			} else if ( nextSpeed < MIN_SPEED ) {
				// If we would go under the min, set to min
				translateSpeed = MIN_SPEED;
			} else {
				// If we would go over max, set to max
				translateSpeed = MAX_SPEED;
			}
		} else {
			// If the speed takes us past the target, just make it go to the
			// target
			translateSpeed = temp.len( );
		}

		// Make sure camera never moves slower than the anchor midpoint
		// Causes problems, but keeping in comments (for now) in case commenting
		// causes more

		// if ( trans_x && trans_y ) {
		// // If we're translating in both directions
		// // if ( translateSpeed < anchorList.getMidpointVelocity( ).len( ) )
		// // {
		// // // And translate speed is slower than the midpoint
		// // translateSpeed = anchorList.getMidpointVelocity( ).len( );
		// // }
		// } else if ( trans_x ) {
		// if ( translateSpeed < Math.cos( anchorList.getMidpointVelocity( )
		// .angle( ) ) * anchorList.getMidpointVelocity( ).len( ) )
		// translateSpeed = ( float ) Math.cos( anchorList
		// .getMidpointVelocity( ).angle( ) )
		// * anchorList.getMidpointVelocity( ).len( );
		// } else if ( trans_y ) {
		// if ( translateSpeed < Math.sin( anchorList.getMidpointVelocity( )
		// .angle( ) ) * anchorList.getMidpointVelocity( ).len( ) )
		// translateSpeed = ( float ) Math.sin( anchorList
		// .getMidpointVelocity( ).angle( ) )
		// * anchorList.getMidpointVelocity( ).len( );
		// }

		temp.nor( );
		translateVelocity.x = temp.x;
		translateVelocity.y = temp.y;
		translateVelocity.mul( translateSpeed );
		camera.translate( translateVelocity );
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
			translateBuffer.width = screenBounds.width * BUFFER_RATIO;
			translateBuffer.height = screenBounds.height * BUFFER_RATIO;
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
	private void renderBuffers( ) {
		// render the translation buffer
		shapeRenderer.setProjectionMatrix( camera.combined );
		shapeRenderer.begin( ShapeType.Rectangle );
		shapeRenderer.identity( );
		shapeRenderer.rect( translateBuffer.x, translateBuffer.y,
				translateBuffer.width, translateBuffer.height );
		shapeRenderer.end( );
		shapeRenderer.begin( ShapeType.Line );
		shapeRenderer.line( translateBuffer.x, translateBuffer.y,
				translateBuffer.x + translateBuffer.width, translateBuffer.y
						+ translateBuffer.height );
		shapeRenderer.line( translateBuffer.x, translateBuffer.y
				+ translateBuffer.height, translateBuffer.x
				+ translateBuffer.width, translateBuffer.y );
		shapeRenderer.end( );

		// render the translation target buffer
		shapeRenderer.begin( ShapeType.Circle );
		shapeRenderer.identity( );
		shapeRenderer.circle( translateTarget.x, translateTarget.y,
				targetBuffer );
		shapeRenderer.end( );

		// render the acceleration target buffer
		shapeRenderer.begin( ShapeType.Circle );
		shapeRenderer.identity( );
		shapeRenderer.circle( translateTarget.x, translateTarget.y,
				accelerationBuffer );
		shapeRenderer.end( );
	}

	/**
	 * Logic for handling debug input while "B" is pressed
	 */
	private void handleInput( ) {
		if ( Gdx.input.isKeyPressed( Input.Keys.E ) ) {
			camera.zoom += 0.2;
			translateBuffer.width = screenBounds.width * BUFFER_RATIO;
			translateBuffer.height = screenBounds.height * BUFFER_RATIO;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.Q ) ) {
			camera.zoom -= 0.2;
			translateBuffer.width = camera.zoom * viewportWidth * BUFFER_RATIO;
			translateBuffer.height = camera.zoom * viewportHeight
					* BUFFER_RATIO;
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