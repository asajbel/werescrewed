package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.hazard.Spikes;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

public class ResurrectScreen implements com.badlogic.gdx.Screen {

	// FIELDS

	// Variables

	private Camera cam;
	private OrthographicCamera bgCam;
	private SpriteBatch batch;
	private SpriteBatch bgBatch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private RootSkeleton bgRootSkel;
	private TiledPlatform ground;
	private PlatformBuilder platBuilder;
	private boolean debug = true;
	private boolean debugTest = true;
	private ProgressManager progressManager;
	private Spikes spikes;

	// timeout for killing player input
	// wont need this as there wont be a button to kill the player
	// eventually
	private int killTimeout = 0;

	// DEBUG CONTROLS
	// 'z' kill player 1
	// 'x' kill player 2
	// 'r' player 1's re-spawn button
	// 'y' player 2's re-spawn button

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public ResurrectScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		bgBatch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );

		// Initialize camera
		initCamera( );
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		// entityManager = new EntityManager( );
		skeleton = new Skeleton( "skeleton", new Vector2( 500, 0 ), null, world );
		// skeleton.body.setType( BodyType.DynamicBody );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );
		bgRootSkel = new RootSkeleton( "root", Vector2.Zero, null, world );

		initParallaxBackground( );

		// testTexture = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		// + "/common/tilesetTest.png", Texture.class );

		platBuilder = new PlatformBuilder( world );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 0, 8f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 0, 8.5f ).buildPlayer( );
		initTiledPlatforms( );
		initCheckPoints( );
		initHazards( );

		rootSkeleton.addSkeleton( skeleton );

		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );

		debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );
	}

	/**
	 * Initializes camera settings
	 */
	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( width, height, world );

		bgCam = new OrthographicCamera( 1, width / height );
		bgCam.viewportWidth = width;
		bgCam.viewportHeight = height;
		bgCam.position.set( width * .5f, height * .5f, 0f );
		bgCam.update( );
	}

	private void initParallaxBackground( ) {
		BodyDef screwBodyDef;
		Body body;
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity bg_1_0 = new Entity( "bg_1_0", new Vector2( -264, 512 ),
				WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/parallax_layer1_0.png",
						Texture.class ), null, false );
		Entity bg_1_1 = new Entity( "bg_1_0", new Vector2( 1920, 512 ),
				WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/parallax_layer1_1.png", Texture.class ),
				null, false );
		Entity bg_2_0 = new Entity( "bg_1_0", new Vector2( 1920, 512 ),
				WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/parallax_layer1_0.png", Texture.class ),
				null, false );
		Entity bg_2_1 = new Entity( "bg_1_0", new Vector2( 1920, 512 ),
				WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/parallax_layer1_0.png", Texture.class ),
				null, false );
		for ( int i = 0; i < 4; i++ ) {
			screwBodyDef = new BodyDef( );
			screwBodyDef.type = BodyType.KinematicBody;
			screwBodyDef.position.set( 0, 0 );
			screwBodyDef.fixedRotation = true;
			body = world.createBody( screwBodyDef );
			screwShape = new CircleShape( );
			screwShape.setRadius( 64 * Util.PIXEL_TO_BOX );
			screwFixture = new FixtureDef( );
			screwFixture.filter.categoryBits = Util.CATEGORY_IGNORE;
			screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
			screwFixture.shape = screwShape;
			screwFixture.isSensor = true;
			body.createFixture( screwFixture );
			body.setUserData( this );
			switch ( i ) {
			case 0:
				bg_1_0 = new Entity( "bg_1_0", new Vector2( -264, 512 ),
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/parallax_layer1_0.png",
								Texture.class ), body, false );
				break;
			case 1:
				bg_1_1 = new Entity( "bg_1_1", new Vector2( 0, 0 ),
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/parallax_layer1_1.png",
								Texture.class ), body, false );
				break;
			case 2:
				bg_2_0 = new Entity( "bg_2_0", new Vector2( 0, 0 ),
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/parallax_layer2_0.png",
								Texture.class ), body, false );
				break;
			case 3:
				bg_2_1 = new Entity( "bg_2_1", new Vector2( 0, 0 ),
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/parallax_layer2_1.png",
								Texture.class ), body, false );
				break;
			default:
				break;
			}
		}
		// bg_1_0.sprite.setScale( 1.9f );
		// bg_1_1.sprite.setScale( 1.9f );
		// bg_2_0.sprite.setScale( 1.9f );
		// bg_2_1.sprite.setScale( 1.9f );

		bg_1_0.setMoverAtCurrentState( new ParallaxMover( new Vector2( 2304,
				512 ), new Vector2( -264, 512 ), 0.0002f, .5f ) );
		bg_1_0.setActive( true );
		bg_1_0.setVisible( true );
		bg_1_1.setMoverAtCurrentState( new ParallaxMover( new Vector2( 2304,
				512 ), new Vector2( -264, 512 ), 0.0002f, 0f ) );
		bg_1_1.setActive( true );
		bg_1_1.setVisible( true );
		bg_2_0.setMoverAtCurrentState( new ParallaxMover( new Vector2( 2304,
				512 ), new Vector2( -264, 512 ), 0.0001f, .5f ) );
		bg_2_0.setActive( true );
		bg_2_0.setVisible( true );
		bg_2_1.setMoverAtCurrentState( new ParallaxMover( new Vector2( 2304,
				512 ), new Vector2( -264, 512 ), 0.0001f, 0f ) );
		bg_2_1.setActive( true );
		bg_2_1.setVisible( true );
		bgRootSkel.addLooseEntity( bg_2_0 );
		bgRootSkel.addLooseEntity( bg_2_1 );
		bgRootSkel.addLooseEntity( bg_1_0 );
		bgRootSkel.addLooseEntity( bg_1_1 );
	}

	private void initHazards( ) {
		spikes = new Spikes( "Spikes1", new Vector2( -1250.0f, -10.0f ), 1, 4,
				world, true, false, true );
	}

	private void initTiledPlatforms( ) {
		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		// THIS SHOULD BE SET IN EVERYTHING START USING THEM
		// AND THINGS WILL STOP FALLING THROUGH OTHER THINGS
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS,
				Util.CATEGORY_EVERYTHING );
		skeleton.addKinematicPlatform( ground );
	}

	private void initCheckPoints( ) {
		progressManager = new ProgressManager( player1, player2, world );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				-512f, 32f ), skeleton, world, progressManager,
				"levelStage_0_0" ) );
		progressManager
				.addCheckPoint( new CheckPoint( "check_01", new Vector2( 0f,
						32f ), skeleton, world, progressManager,
						"levelStage_0_1" ) );
		progressManager
				.addCheckPoint( new CheckPoint( "check_01", new Vector2( 512f,
						32f ), skeleton, world, progressManager,
						"levelStage_0_2" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				1024f, 32f ), skeleton, world, progressManager,
				"levelStage_0_3" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				1512f, 32f ), skeleton, world, progressManager,
				"levelStage_0_4" ) );
	}

	@Override
	public void render( float deltaTime ) {
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		cam.update( );
		// update background camera zoom
		// float zoomRatio = ( ( ( 1.1f - 1f ) * ( cam.camera.zoom-1 ) ) / ( 3f
		// - 1f ) ) + 1;
		// bgCam.zoom = Math.min( 1.1f, zoomRatio );
		bgCam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_1 ) ) {
			ScreenManager.getInstance( ).show( ScreenType.WIN );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest )
				debug = !debug;
			debugTest = false;
		} else
			debugTest = true;

		if ( killTimeout > 0 ) {
			killTimeout--;
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.Z ) && killTimeout == 0 ) {
			player1.killPlayer( );
			killTimeout = 15;
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) && killTimeout == 0 ) {
			player2.killPlayer( );
			killTimeout = 15;
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			rootSkeleton.rotateBy( -0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			rootSkeleton.translateBy( 0.0f, 0.01f );
		}

		player1.update( deltaTime );
		player2.update( deltaTime );
		rootSkeleton.update( deltaTime );
		progressManager.update( deltaTime );
		spikes.update( deltaTime );

		// update background skeletons
		bgRootSkel.update( deltaTime );

		// update background stuff which uses different transformation matrices
		bgBatch.setProjectionMatrix( bgCam.combined );
		bgBatch.begin( );
		bgRootSkel.draw( bgBatch );
		bgBatch.end( );

		// use the camera matrix for things not in the background
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );
		rootSkeleton.draw( batch );
		progressManager.draw( batch );
		spikes.draw( batch );
		player1.draw( batch );
		player2.draw( batch );
		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 6 );
	}

	@Override
	public void resize( int width, int height ) {
	}

	@Override
	public void show( ) {
	}

	@Override
	public void hide( ) {
	}

	@Override
	public void pause( ) {
	}

	@Override
	public void resume( ) {
	}

	@Override
	public void dispose( ) {
	}
}