package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.player.Player;
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
	// private static final int LISTEN_BUFFER = 300;
	private static final float ACCELERATION_RATIO = .005f;
	private static final float DECELERATION_RATIO = .03f;
	private static final float ACCELERATION_BUFFER_RATIO = .5f;
	private static final float TARGET_BUFFER_RATIO = .05f;
	private static final float MINIMUM_FOLLOW_SPEED = 3f;
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

	// might take these out when no longer required
	private Player player1;
	private Player player2;
	private AnchorList anchorList;
	private int player1Anchor;
	private int player2Anchor;

	// debug
	private boolean debugInput;
	private boolean debugRender;
	private ShapeRenderer shapeRenderer;

	public Camera( float viewportWidth, float viewportHeight ) {
		this( viewportWidth, viewportHeight, null, null );
	}

	public Camera( float viewportWidth, float viewportHeight, Player player ) {
		this( viewportWidth, viewportHeight, player, null );
	}

	public Camera( float viewportWidth, float viewportHeight, Player player1,
			Player player2 ) {
		initializeVars( viewportWidth, viewportHeight );
		this.player1 = player1;
		this.player2 = player2;

		if ( player1 != null ) {
			player1Anchor = anchorList.addAnchor( true, player1.getPosition( ) );
		}

		if ( player2 != null ) {
			player2Anchor = anchorList.addAnchor( true, player2.getPosition( ) );
		}
		camera.update( );
	}

	private void initializeVars( float viewportWidth, float viewportHeight ) {
		camera = new OrthographicCamera( 1, viewportHeight / viewportWidth );
		this.viewportHeight = Gdx.graphics.getHeight( );
		this.viewportWidth = Gdx.graphics.getWidth( );
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set( this.viewportWidth * .5f,
				this.viewportHeight * .5f, 0f );
		position = camera.position;
		center2D = new Vector2( position.x, position.y );
		screenBounds = new Rectangle( position.x, position.y, viewportHeight,
				viewportWidth );

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

		player1Anchor = -1;
		player2Anchor = -1;
		anchorList = AnchorList.getInstance( camera );
		anchorList.clear( );
		if ( ANCHOR_TEST_MODE ) {
			createTestAnchors( );
		}

		// debug
		debugInput = false;
		debugRender = false;
		shapeRenderer = new ShapeRenderer( );
	}

	public Rectangle getBounds( ) {
		return screenBounds;
	}

	public Matrix4 combined( ) {
		return camera.combined;
	}

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

		// update player anchors
		if ( player1Anchor > -1 ) {
			anchorList.setAnchorPos( player1Anchor, player1.getPosition( ) );
		}
		if ( player2Anchor > -1 ) {
			anchorList.setAnchorPos( player2Anchor, player2.getPosition( ) );
		}

		// update all positions and dimensions
		position = camera.position;
		center2D.x = position.x;
		center2D.y = position.y;
		screenBounds.x = position.x;
		screenBounds.y = position.y;
		screenBounds.width = camera.zoom * viewportWidth;
		screenBounds.height = camera.zoom * viewportHeight;

		translateBuffer.x = position.x - translateBuffer.width * .5f;
		translateBuffer.y = position.y - translateBuffer.height * .5f;
		setTranslateTarget( );

		// Do the actual translation and zooming
		adjustCamera( );

		// render buffers areas anchors
		if ( debugRender ) {
			renderBuffers( );
		}

		camera.update( );

		if ( debugInput ) {
			System.out.println( "Zoom: " + camera.zoom );
		}

		// also render anchors if debugRender == true
		anchorList.update( debugRender );
	}

	/**
	 * set focus of camera to the midpoint of all anchors
	 */
	private Vector2 setTranslateTarget( ) {
		translateTarget.x = anchorList.midpoint( ).x;
		translateTarget.y = anchorList.midpoint( ).y;

		translateTarget3D.x = translateTarget.x;
		translateTarget3D.y = translateTarget.y;
		translateTarget3D.z = 0f;
		return this.translateTarget;
	}

	/**
	 * Adjust the camera by translating and zooming when necessary
	 */
	private void adjustCamera( ) {
		translateLogic( );
	}

	/**
	 * Either translate, lock, or do nothing based on various buffers and
	 * positions
	 */
	private void translateLogic( ) {
		// determine whether to translate, lock, or do nothing
		if ( !debugInput && translateState ) {
			if ( center2D.dst( translateTarget ) < targetBuffer ) {

				// center of camera is inside of buffer circle around target
				float tempAngle = 0f;
				tempAngle = anchorList.getMidpointVelocity( ).angle( )
						- translateVelocity.angle( );
				tempAngle = Math.abs( tempAngle );
				translate( );
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
     * 
     */
	private void translate( ) {
		// camera.position.set(translateTarget3D);
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
	 * zoom camera to keep anchors on screen
	 */
	@SuppressWarnings( "unused" )
	private void zoom( ) {

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
	@SuppressWarnings( "unused" )
	private boolean rectOutsideRect( Rectangle rect1, Rectangle rect2 ) {
		boolean returnValue = false;

		return returnValue;
	}

	private void createTestAnchors( ) {
		// anchorList.addAnchor( false, new Vector2(0f, 0f) );
		anchorList.addAnchor( false, new Vector2( -128f, 128f ) );
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