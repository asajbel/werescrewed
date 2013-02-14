package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Rope;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.PistonMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class PhysicsTestScreen implements com.badlogic.gdx.Screen {

	// FIELDS

	// Static Constants

	/***
	 * Box2D to pixels conversion.
	 * 
	 * This number means 1 meter equals 256 pixels. That means the biggest
	 * in-game object (10 meters) we can use is 2560 pixels wide, which is much
	 * bigger than our max screen resolution so it should be enough.
	 */
	// public static final float BOX_TO_PIXEL = 256f;
	// public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
	// public static final float DEG_TO_RAD = 0.0174532925199432957f;
	// public static final float RAD_TO_DEG = 57.295779513082320876f;

	// Variables

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	@SuppressWarnings( "unused" )
	private TiledPlatform tiledPlat, ground, movingTP, singTile, rectile;
	private PlatformBuilder platBuilder;
	private PuzzleScrew puzzleScrew;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private ArrayList< StrippedScrew > climbingScrews;
	private boolean debug = true;
	private boolean debugTest = true;
	Rope rope;

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public PhysicsTestScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -45 ), true );
		//entityManager = new EntityManager( );
		skeleton = new Skeleton( "skeleton", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );
		//entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );
		platBuilder = new PlatformBuilder( world );
		testTexture = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle + "/common/TilesetTest.png",Texture.class);

		// Initialize camera
		initCamera( );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// Initialize platforms
		initTiledPlatforms( );

		// Initialize screws
		initStructureScrews( );
		initPuzzleScrews( );
		initClimbingScrews( );

		//rope = new Rope( "rope", new Vector2 (2000.0f * Util.PIXEL_TO_BOX, 400.0f* Util.PIXEL_TO_BOX), null, world );
		// Add players
		// First player has to have the name "player1"
		// Second player has to have the name "player2"
		// Otherwise input handler breaks
		
		player1 = new PlayerBuilder()
					.name( "player1" )
					.world( world )
					.position( 1.0f, 1.0f )
					.buildPlayer();
		player2 = new PlayerBuilder()
					.name( "player2" )
					.world( world )
					.position( 1.5f, 1.5f )
					.buildPlayer();
		
		// Add screws

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
		cam = new Camera( width, height );
	}

	/**
	 * Initializes tiled platforms' settings, and adds them to the skeleton
	 */
	private void initTiledPlatforms( ) {
		// Tiled Platform
		tiledPlat = platBuilder.position( 700.0f, 100.0f )
				.dimensions( 10, 1 ).texture( testTexture )
				.dynamic( )
				.name( "tp" ).resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addDynamicPlatform( tiledPlat );
		
		// Tiled Single Platform
		singTile = platBuilder.position( -1.0f, 1000.0f )
				.dimensions( 1, 1 ).texture( testTexture )
				.dynamic( )
				.name( "Single Tiled" ).resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatform( singTile );
		singTile.body.setFixedRotation( false );


		// Moving platform
		movingTP = platBuilder.position( 0.0f, 120.0f )
				.dimensions( 10, 1 ).texture( testTexture )
				.name( "movingTP" ).resitituion( 0.0f )
				.kinematic( )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( movingTP );
		
		buildMoverPlatforms( );

		//TODO : FIX ONESIDED BUG, 
		// Ground: SHOULD NEVER BE ONESIDED
		ground = platBuilder.position( 0.0f, -75.0f ).name( "ground" )
				.dimensions( 200, 4 ).texture( testTexture )
				.kinematic( )
				.oneSided( false )
				.resitituion( 0.0f ).buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_GROUND, Util.CATEGORY_EVERYTHING );
		skeleton.addKinematicPlatform( ground );
	}

	/**
	 * Initialize the platform screws' settings and add them to the entity
	 * manager and skeleton
	 */
	private void initStructureScrews( ) {
		StructureScrew leftPlatScrew = new StructureScrew( "", new Vector2(
				tiledPlat.body.getPosition( ).x - 0.5f,
				tiledPlat.body.getPosition( ).y ), 50, tiledPlat, skeleton,
				world );

		StructureScrew rightPlatScrew = new StructureScrew( "", new Vector2(
				tiledPlat.body.getPosition( ).x + 0.5f,
				tiledPlat.body.getPosition( ).y ), 50, tiledPlat, skeleton,
				world );
		tiledPlat.addScrew( leftPlatScrew );
		tiledPlat.addScrew( rightPlatScrew );
	}

	/**
	 * Initializes settings for puzzle screws
	 */
	private void initPuzzleScrews( ) {
		puzzleScrew = new PuzzleScrew( "001", new Vector2( 0.0f, 0.2f ), 50,
				skeleton, world );
		puzzleScrew.puzzleManager.addEntity( movingTP );
		LerpMover lm = new LerpMover(
				new Vector2( movingTP.body.getPosition( ).x,
						movingTP.body.getPosition( ).y ), new Vector2(
						movingTP.body.getPosition( ).x + 1.75f,
						movingTP.body.getPosition( ).y ), 1f, true );
		puzzleScrew.puzzleManager.addMover( movingTP.name, lm );
		skeleton.addScrewForDraw( puzzleScrew );
	}

	/**
	 * Initializes stripped screws for climbing, and adds them to the skeleton.
	 */
	private void initClimbingScrews( ) {
		climbingScrews = new ArrayList< StrippedScrew >( );
		float x1 = 1.75f;
		float x2 = 2.4f;
		float y1 = 0.6f;
		float dy = 0.7f;
		for ( int i = 0; i < 10; i++ ) {
			if ( i % 2 == 0 ) {
				climbingScrews.add( new StrippedScrew( "", world, new Vector2(
						x1, y1 ), skeleton ) );
			} else {
				climbingScrews.add( new StrippedScrew( "", world, new Vector2(
						x2, y1 ), skeleton ) );
			}
			y1 += dy;
		}

		for ( StrippedScrew climbingScrew : climbingScrews ) {
			skeleton.addStrippedScrew( climbingScrew );
		}
	}

	/**
	 * Initializes settings for moving platforms and adds them to the skeleton
	 */
	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = platBuilder.width( 10 )
				.height( 1 ).oneSided( true ).position( -1000, 200 )
				.texture( testTexture ).friction( 1f )
				.dynamic( )
				.buildTilePlatform( );

		PrismaticJointDef prismaticJointDef = JointFactory
				.constructSlidingJointDef( skeleton.body, slidingPlatform.body,
						slidingPlatform.body.getWorldCenter( ), new Vector2( 1,
								0 ), 1.0f, 1f );
		PrismaticJoint j = ( PrismaticJoint ) world
				.createJoint( prismaticJointDef );
		slidingPlatform.setMover( new SlidingMotorMover(
				PuzzleType.PRISMATIC_SLIDER, j ) );
		skeleton.addDynamicPlatform( slidingPlatform );

		TiledPlatform skeletonTest1 = platBuilder.width( 10 ).height( 1 )
				.friction( 1f ).oneSided( false )
				.position( 500, 200 ).texture( testTexture )
				.kinematic( )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( skeletonTest1 );
		
		rope = new Rope( "rope", new Vector2 ( 8f, 1.5f), new Vector2 ( 8.0f, 32.0f ), 10, null, world );

		
		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( );

		PlatformBuilder builder = platBuilder.width( 1 ).height( 3 )
				.oneSided( false )
				.dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.position( ( -100f - i * 40 ),
					220f ).buildTilePlatform( );

			PrismaticJoint pistonJoint = jointBuilder.bodyB( ( Entity ) piston )
					.anchor( piston.body.getWorldCenter( ) ).build( );
			// Something is still not quite right with this, try replacing 3
			// with 0.
			piston.setMover( new PistonMover( pistonJoint, 3f, i / 10.0f + 2f ) );
			//piston.body.setSleepingAllowed( false );
			skeleton.addDynamicPlatform( piston );
		}

		
		builder = platBuilder.width( 20 ).height( 1 )
				.oneSided( true )
				.dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );
		// .buildTilePlatform( world );

		TiledPlatform elevator = builder.position( -1500, 150 )
				.moveable( true ).buildTilePlatform( );
		PrismaticJoint pistonJ = jointBuilder.bodyB( ( Entity ) elevator )
		.anchor( elevator.body.getWorldCenter( ) ).build( );
		
		elevator.setMover( new PistonMover( pistonJ, 0f,  2f ) );
		elevator.body.setSleepingAllowed( false );
		
		Platform gear = builder.name( "gear" )
								.position( 1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX )
								.texture( null )
								.setScale( 3f )
								.type( "gearSmall" )
								.buildComplexPlatform( );

		skeleton.addPlatformRotatingCenterWithMot( gear, 1f );
		Filter filter;
		for ( Fixture f : gear.body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			// move player to another category so other objects stop
			// colliding
			filter.categoryBits = Util.DYNAMIC_OBJECTS;
			// player still collides with sensor of screw
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}
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
			rootSkeleton.translate( 0.0f, 0.01f );
			//rootSkeleton.body.setLinearVelocity( new Vector2(0,1f) );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			rootSkeleton.translate( 0.0f, -0.01f );
			//rootSkeleton.body.setLinearVelocity( new Vector2(0,-1f) );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			rootSkeleton.rotate( -0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			rootSkeleton.rotate( 0.01f );
		}
		

		player1.update( deltaTime );
		player2.update( deltaTime );
		puzzleScrew.update( deltaTime );
		rootSkeleton.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		puzzleScrew.draw( batch );
		rootSkeleton.draw( batch );
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