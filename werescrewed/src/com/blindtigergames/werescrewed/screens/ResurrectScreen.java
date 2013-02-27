package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class ResurrectScreen implements com.badlogic.gdx.Screen {

	// FIELDS

	// Variables

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private TiledPlatform ground;
	private PlatformBuilder platBuilder;
	private boolean debug = true;
	private boolean debugTest = true;
	private ProgressManager progressManager;

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public ResurrectScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );

		// Initialize camera
		initCamera( );
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		// entityManager = new EntityManager( );
		skeleton = new Skeleton( "skeleton", new Vector2( 500, 0 ), null, world );
		// skeleton.body.setType( BodyType.DynamicBody );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );

		// testTexture = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		// + "/common/tilesetTest.png", Texture.class );

		platBuilder = new PlatformBuilder( world );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( -1024.0f, 100.0f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( -1015f, 110.5f ).buildPlayer( );

		initTiledPlatforms( );
		initCheckPoints( );

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
				-512f, 32f ), skeleton, world, progressManager, "levelStage_0_0" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				0f, 32f ), skeleton, world, progressManager, "levelStage_0_1" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				512f, 32f ), skeleton, world, progressManager, "levelStage_0_2" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				1024f, 32f ), skeleton, world, progressManager, "levelStage_0_3" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				1512f, 32f ), skeleton, world, progressManager, "levelStage_0_4" ) );
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

		if ( Gdx.input.isKeyPressed( Input.Keys.Z ) ) {
			player2.killPlayer( );
			//rootSkeleton.translateBy( 0.0f, 0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			rootSkeleton.translateBy( 0.0f, -0.01f );
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
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		rootSkeleton.draw( batch );
		progressManager.draw( batch );
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