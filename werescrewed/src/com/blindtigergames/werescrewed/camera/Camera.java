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
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
	public static final float DEGTORAD = 0.0174532925199432957f;
	public static final float RADTODEG = 57.295779513082320876f;
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
	private Vector2 avgOutside;
	private boolean insideTargetBuffer;
	private float targetToBufferRatio;

	// zoom
	private static final float ZOOM_ACCELERATION = 1f;
	private static final float ZOOM_MAX_SPEED = .02f;
	private float zoomSpeed;

	private AnchorList anchorList;

	// debug
	private boolean debugInput;
	private boolean debugRender;
	private ShapeRenderer shapeRenderer;

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
		zoomSpeed = .01f;
		targetToBufferRatio = 1f;

		anchorList = AnchorList.getInstance( camera );
		anchorList.clear( );
		if ( ANCHOR_TEST_MODE ) {
			// createTestAnchors( world );
		}

		// debug
		debugInput = false;
		debugRender = false;
		shapeRenderer = new ShapeRenderer( );
		avgOutside = new Vector2( 0f, 0f );
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
		debugInput = false;
		debugRender = false;
		// debugMode = true;
		// check debug
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
	 * set focus of camera to the midpoint of all anchors
	 */
	private Vector2 setTranslateTarget( ) {
		translateTarget.x = anchorList.getMidpoint( ).x;
		translateTarget.y = anchorList.getMidpoint( ).y;

		translateTarget3D.x = translateTarget.x;
		translateTarget3D.y = translateTarget.y;
		translateTarget3D.z = 0f;
		return this.translateTarget;
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
	private boolean rectOutsideRect( Rectangle rect1, Rectangle rect2 ) {
		boolean returnValue = false;

		if ( rect1.x > rect2.x && rect1.y > rect2.y
				&& ( rect1.x + rect1.width ) < rect2.x + rect2.width
				&& ( rect1.y + rect1.height ) < rect2.y + rect2.height )
			returnValue = false;
		else {
			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * Adjust the camera by translating and zooming when necessary
	 */
	private void adjustCamera( ) {
		avgOutside.x = 0f;
		avgOutside.y = 0f;
		boolean outsideTrue = false;

		// get vectors from the translateTarget to all anchors outside of
		// the bounds of the screen, normalizes it, then adds then all
		// together to come up with a pseudo average
		for ( Anchor curAnchor : anchorList.anchorList ) {
			if ( ( curAnchor.activated || curAnchor.special )
					&& rectOutsideRect( curAnchor.getBufferRectangle( ),
							screenBounds ) ) {
				outsideTrue = true;
				Vector2.tmp.x = curAnchor.position.x - translateTarget.x;
				Vector2.tmp.y = curAnchor.position.y - translateTarget.y;
				Vector2.tmp.nor( );
				avgOutside.add( Vector2.tmp );
				if ( insideTargetBuffer )
					break;
			}
		}

		if ( outsideTrue ) {
			if ( !insideTargetBuffer ) {
				translateState = true;;
			}
		}
		translateLogic( );
		zoom( );
	}

	/**
	 * Either translate, lock, or do nothing based on various buffers and
	 * positions
	 */
	private void translateLogic( ) {
		// determine whether to translate, lock, or do nothing
		if ( !debugInput && translateState ) {
			if ( insideTargetBuffer ) {

				// center of camera is inside of buffer circle around target
				float tempAngle = 0f;
				tempAngle = anchorList.getMidpointVelocity( ).angle( )
						- translateVelocity.angle( );
				tempAngle = Math.abs( tempAngle );
				camera.position.x = translateTarget.x;
				camera.position.y = translateTarget.y;
				if ( anchorList.getMidpointVelocity( ).len( ) < MINIMUM_FOLLOW_SPEED
						|| tempAngle > MAX_ANGLE_DIFF ) {
					translateState = false;
					translateVelocity.x = 0f;
					translateVelocity.y = 0f;
					translateAcceleration = 0f;
					translateSpeed = 0f;
				}
			} else
				translate( );
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
	private void translate( ) {
		Vector2.tmp.x = translateTarget.x;
		Vector2.tmp.y = translateTarget.y;
		Vector2.tmp.sub( center2D );

		if ( Vector2.tmp.len( ) > accelerationBuffer ) {
			translateAcceleration = ( Vector2.tmp.len( ) * ACCELERATION_RATIO );
		} else {
			translateAcceleration = -1f * Vector2.tmp.len( )
					* DECELERATION_RATIO;
		}

		if ( ( translateSpeed + translateAcceleration ) < ( Vector2.tmp.len( ) - 5f ) )
			translateSpeed += translateAcceleration;
		else
			translateSpeed = Vector2.tmp.len( ) - 5f;

		if ( translateSpeed < anchorList.getMidpointVelocity( ).len( ) )
			translateSpeed = anchorList.getMidpointVelocity( ).len( );

		Vector2.tmp.nor( );
		translateVelocity.x = Vector2.tmp.x;
		translateVelocity.y = Vector2.tmp.y;
		translateVelocity.mul( translateSpeed );
		camera.translate( translateVelocity );
	}

	/**
	 * zoom
	 * 
	 * @param modifier
	 *            modifies zoom rate
	 */
	private void zoom( ) {
		// camera.zoom += modifier * zoomSpeed;
		// if ( zoomSpeed < ZOOM_MAX_SPEED )
		// zoomSpeed += ZOOM_ACCELERATION;
		float newZoom = 1f;

		Vector2 longestDist = anchorList.getLongestXYDist( );
		if ( longestDist.x > longestDist.y ) {
			newZoom = longestDist.x / viewportWidth;
		} else if ( longestDist.y > longestDist.x ) {
			newZoom = longestDist.y / viewportHeight;
		}
		if ( newZoom > 1f ) {
			camera.zoom = newZoom;
			translateBuffer.width = screenBounds.width * BUFFER_RATIO;
			translateBuffer.height = screenBounds.height * BUFFER_RATIO;
		}
	}

	@SuppressWarnings( "unused" )
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
}