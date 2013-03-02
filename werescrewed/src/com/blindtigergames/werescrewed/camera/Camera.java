package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.blindtigergames.werescrewed.screens.GameScreen;
//import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/*******************************************************************************
 * Camera class. Zooms and translates based on anchors. Max 30 anchors.
 * 
 * @author Edward Ramirez
 ******************************************************************************/
public class Camera {
	private static final boolean ANCHOR_TEST_MODE = false;
	// private static final boolean ANCHOR_TEST_MODE= true;
	public OrthographicCamera camera;
	public float viewportHeight;
	public float viewportWidth;
	public Vector3 position;
	private Vector2 center2D;
	private Rectangle screenBounds;

	// translation
	// private static final float SPEED_TARGET_MODIFIER = 5f;
	private static final float BUFFER_RATIO = .5f;
	private static final float ACCELERATION_RATIO = .001f;
	private static final float DECELERATION_RATIO = .002f;
	private static final float ACCELERATION_BUFFER_RATIO = .1f;
	private static final float TARGET_BUFFER_RATIO = .02f;
	private static final float MINIMUM_FOLLOW_SPEED = .1f;
	private static final float MAX_ANGLE_DIFF = 45f;
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
	private float targetToBufferRatio;

	// zoom
	private static final float ZOOM_ACCELERATION = .0001f;
	private static final float ZOOM_MAX_SPEED = 100f;
	private static final float ZOOM_SIG_DIFF = .0005f;
	private static final float ZOOM_IN_FACTOR = .5f;

	private enum RectDirection {
		X, Y, BOTH, NONE
	};

	private float zoomSpeed;

	private AnchorList anchorList;

	// debug
	private boolean debugInput;
	private boolean debugRender;
	private ShapeRenderer shapeRenderer;
	private boolean debugTurnOffZoom;

	public Camera( float viewportWidth, float viewportHeight, World world ) {
		initializeVars( viewportWidth, viewportHeight, world );
		camera.update( );
	}

	private void initializeVars( float viewportWidth, float viewportHeight,
			World world ) {
		camera = new OrthographicCamera( 1, viewportHeight / viewportWidth );
		this.viewportHeight = Gdx.graphics.getHeight( );
		this.viewportWidth = Gdx.graphics.getWidth( );
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set( this.viewportWidth * .5f,
				this.viewportHeight * .5f, 0f );
		position = camera.position;
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
		targetToBufferRatio = 1f;

		anchorList = AnchorList.getInstance( camera );
		anchorList.clear( );
		if ( ANCHOR_TEST_MODE ) {
			createTestAnchors( world );
		}

		// debug
		debugInput = false;
		debugRender = false;
		shapeRenderer = new ShapeRenderer( );
		debugTurnOffZoom = false;
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
			debugInput = true;
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
		screenBounds.x = position.x - viewportWidth / 2;
		screenBounds.y = position.y - viewportHeight / 2;
		screenBounds.width = camera.zoom * viewportWidth;
		screenBounds.height = camera.zoom * viewportHeight;

		translateBuffer.x = position.x - translateBuffer.width * .5f;
		translateBuffer.y = position.y - translateBuffer.height * .5f;
		setTranslateTarget( );

		// check if center is inside target buffer
		targetToBufferRatio = center2D.dst( translateTarget ) / targetBuffer;
		if ( targetToBufferRatio < 1f )
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
	 * determine if any part of rect1 is outside of rect2
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
		boolean outside_x = false;
		boolean outside_y = false;

		// get vectors from the translateTarget to all anchors outside of
		// the bounds of the screen, normalizes it, then adds then all
		// together to come up with a pseudo average
		for ( Anchor curAnchor : anchorList.anchorList ) {
			if ( curAnchor.activated || curAnchor.special ) {
				RectDirection dir = rectOutsideRect(
						curAnchor.getBufferRectangle( ), screenBounds );

				// only do stuff if a buffer anchor is outside the screen
				if ( dir != RectDirection.NONE ) {

					// find whether buffer is outside in x, y or both directions
					if ( dir == RectDirection.BOTH ) {
						outside_x = true;
						outside_y = true;
					} else if ( dir == RectDirection.X )
						outside_x = true;
					else
						outside_y = true;

					if ( insideTargetBuffer )
						break;
				}
			}
		}

		if ( outside_x || outside_y ) {
			if ( !insideTargetBuffer ) {
				translateState = true;
			}
		}
		translateLogic( outside_x, outside_y );
		if(!debugTurnOffZoom)
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
			// - camera center is really close to target
			// - camera center is really close to target.x when only translating
			// 		on x axis
			// - camera center is really close to target.y when only translating
			// 		on y axis
			if ( insideTargetBuffer )
				lock = true;
			else if ( trans_x
					&& Math.abs( translateTarget.x - center2D.x ) < targetBuffer
					&& !trans_y )
				lock = true;
			else if ( trans_y
					&& Math.abs( translateTarget.y - center2D.y ) < targetBuffer
					&& !trans_x )
				lock = true;

			// center of camera is within buffer from target, so camera
			// locks to target
			if ( lock ) {
				// find angle between midpoint velocity and translate velocity
				// if player stops moving or changes direction abruptly, disable
				// lock
				float tempAngle = 0f;
				tempAngle = anchorList.getMidpointVelocity( ).angle( )
						- translateVelocity.angle( );
				tempAngle = Math.abs( tempAngle );

				if ( anchorList.getMidpointVelocity( ).len( ) < MINIMUM_FOLLOW_SPEED
						|| tempAngle > MAX_ANGLE_DIFF ) {
					translateState = false;
					translateVelocity.x = 0f;
					translateVelocity.y = 0f;
					translateAcceleration = 0f;
					translateSpeed = 0f;
				} else {
					if ( trans_x )
						camera.position.x = translateTarget.x;
					if ( trans_y )
						camera.position.y = translateTarget.y;
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
		// only account for translate target on axis which is being translated
		// on
		if ( trans_x )
			Vector2.tmp.x = translateTarget.x;
		else
			Vector2.tmp.x = center2D.x;

		if ( trans_y )
			Vector2.tmp.y = translateTarget.y;
		else
			Vector2.tmp.y = center2D.y;

		Vector2.tmp.sub( center2D );

		if ( Vector2.tmp.len( ) > accelerationBuffer ) {
			translateAcceleration = ( Vector2.tmp.len( ) * ACCELERATION_RATIO );
		} else {
			translateAcceleration = -1f * Vector2.tmp.len( )
					* DECELERATION_RATIO;
		}

		if ( ( translateSpeed + translateAcceleration ) < Vector2.tmp.len( ) - 1f )
			translateSpeed += translateAcceleration;
		else
			translateSpeed = Vector2.tmp.len( ) - 1f;

		// make sure camera never moves faster than the anchor midpoint
		if ( trans_x && trans_y ) {
			if ( translateSpeed < anchorList.getMidpointVelocity( ).len( ) )
				translateSpeed = anchorList.getMidpointVelocity( ).len( );
		} else if ( trans_x ) {
			if ( translateSpeed < Math.cos( anchorList.getMidpointVelocity( )
					.len( ) ) )
				translateSpeed = ( float ) Math.cos( anchorList
						.getMidpointVelocity( ).len( ) );
		} else if ( trans_y ) {
			if ( translateSpeed < Math.sin( anchorList.getMidpointVelocity( )
					.len( ) ) )
				translateSpeed = ( float ) Math.sin( anchorList
						.getMidpointVelocity( ).len( ) );
		}

		Vector2.tmp.nor( );
		translateVelocity.x = Vector2.tmp.x;
		translateVelocity.y = Vector2.tmp.y;
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

		if ( newZoom > 1f && zoomSpeed < ZOOM_MAX_SPEED ) {
			zoomSteer( newZoom );
			translateBuffer.width = screenBounds.width * BUFFER_RATIO;
			translateBuffer.height = screenBounds.height * BUFFER_RATIO;
		}
	}

	/**
	 * steer zoom to the new zoom
	 * 
	 * @param newZoom
	 */
	private void zoomSteer( float newZoom ) {
		// if difference is small enough, set speed to zero
		if ( Math.abs( camera.zoom - newZoom ) < ZOOM_SIG_DIFF )
			zoomSpeed = 0;

		// accelerate zoom
		zoomSpeed += ZOOM_ACCELERATION;

		// use speed to zoom out
		if ( newZoom > camera.zoom ) {
			if ( ( camera.zoom + zoomSpeed ) < ( newZoom - .001f ) )
				camera.zoom += zoomSpeed;
			else
				camera.zoom = newZoom;
		}

		// if zooming in, use slower (half maybe) speed
		if ( newZoom < camera.zoom ) {
			if ( ( camera.zoom - zoomSpeed * ZOOM_IN_FACTOR ) > ( newZoom + .001f ) )
				camera.zoom -= zoomSpeed * ZOOM_IN_FACTOR;
			else
				camera.zoom = newZoom;
		}
	}

	private void createTestAnchors( World world ) {
		// anchorList.addAnchor( false, new Vector2(0f, 0f) );
		// anchorList.addAnchor( false, new Vector2( -128f, 128f ), world, 3f );
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
			camera.zoom += 0.02;
			translateBuffer.width = screenBounds.width * BUFFER_RATIO;
			translateBuffer.height = screenBounds.height * BUFFER_RATIO;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.Q ) ) {
			camera.zoom -= 0.02;
			translateBuffer.width = camera.zoom * viewportWidth * BUFFER_RATIO;
			translateBuffer.height = camera.zoom * viewportHeight
					* BUFFER_RATIO;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.LEFT ) ) {
			if ( camera.position.x > 0 )
				camera.translate( -3, 0, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) ) {
			if ( camera.position.x < 1024 )
				camera.translate( 3, 0, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.DOWN ) ) {
			if ( camera.position.y > 0 )
				camera.translate( 0, -3, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.UP ) ) {
			if ( camera.position.y < 1024 )
				camera.translate( 0, 3, 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_0 ) ) {
			camera.zoom = .5f;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_1 ) ) {
			camera.zoom = 1f;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_2 ) ) {
			camera.zoom = 2f;
		}
	}

	public void turnOffZoom( ) {
		debugTurnOffZoom = true;
	}
}