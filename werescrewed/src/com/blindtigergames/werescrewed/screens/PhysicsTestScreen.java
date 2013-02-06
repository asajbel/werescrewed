package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.PistonMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
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
	private EntityManager entityManager;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private TiledPlatform tiledPlat, ground, movingTP, singTile, rectile;
	private PlatformBuilder platBuilder;
	private PuzzleScrew puzzleScrew;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private ArrayList< StrippedScrew > climbingScrews;
	private boolean debug = true;
	private boolean debugTest = true;

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public PhysicsTestScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -45 ), true );
		entityManager = new EntityManager( );
		skeleton = new Skeleton( "", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "", Vector2.Zero, null, world );
		entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );
		platBuilder = new PlatformBuilder( world );
		testTexture = new Texture( Gdx.files.internal( "data/TilesetTest.png" ) );

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

		// Add players
		// First player has to have the name "player1"
		// Second player has to have the name "player2"
		// Otherwise input handler breaks
		player1 = new Player( "player1", world, new Vector2( 1.0f, 1.0f ) );
		player2 = new Player( "player2", world, new Vector2( 1.5f, 1.5f ) );

		// Add screws

		rootSkeleton.addSkeleton( skeleton );

		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		new FPSLogger( );

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
		tiledPlat = platBuilder.setPosition( 700.0f, 100.0f )
				.setDimensions( 10, 1 ).setTexture( testTexture )
				.setName( "tp" ).setResitituion( 0.0f ).buildTilePlatform( );
		tiledPlat.body.setType( BodyType.DynamicBody );
		tiledPlat.body.setFixedRotation( false );
		skeleton.addPlatform( tiledPlat );
		
		// Tiled Rectangle Platform
		rectile = platBuilder.setPosition( -200.0f, 600.f )
				.setDimensions( 20,3 ).setTexture( testTexture )
				.setName( "rectangle tiled" ).setResitituion( 0.0f ).buildTilePlatform( );
		rectile.body.setType( BodyType.DynamicBody );
		rectile.body.setFixedRotation( false );
		skeleton.addPlatform( rectile );
		
		// Tiled Single Platform
		singTile = platBuilder.setPosition( -1.0f, 1000.0f )
				.setDimensions( 1, 1 ).setTexture( testTexture )
				.setName( "Single Tiled" ).setResitituion( 0.0f ).buildTilePlatform( );
		singTile.body.setType( BodyType.DynamicBody );
		singTile.body.setFixedRotation( false );
		skeleton.addPlatform( singTile ); 

		// Moving platform
		movingTP = platBuilder.setPosition( 0.0f, 120.0f )
				.setDimensions( 10, 1 ).setTexture( testTexture )
				.setName( "movingTP" ).setResitituion( 0.0f )
				.buildTilePlatform( );
		movingTP.body.setType( BodyType.KinematicBody );
		buildMoverPlatforms( );

		// Ground
		ground = platBuilder.setPosition( 0.0f, 0.0f ).setName( "ground" )
				.setDimensions( 200, 1 ).setTexture( testTexture )
				.setResitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatformFixed( ground );
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

		Vector2 axis = new Vector2( 1, 0 );
		PrismaticJointDef jointDef = new PrismaticJointDef( );
		jointDef.initialize( movingTP.body, skeleton.body,
				movingTP.body.getPosition( ), axis );
		jointDef.enableMotor = true;
		jointDef.enableLimit = true;
		jointDef.lowerTranslation = -2.5f;
		jointDef.upperTranslation = 3.0f;
		jointDef.motorSpeed = 7.0f;
		puzzleScrew = new PuzzleScrew( "001", new Vector2( 0.0f, 0.2f ), 50,
				skeleton, world );
		puzzleScrew.puzzleManager.addEntity( movingTP );
		LerpMover lm = new LerpMover(
				new Vector2( movingTP.body.getPosition( ).x,
						movingTP.body.getPosition( ).y ), new Vector2(
						movingTP.body.getPosition( ).x + 1.75f,
						movingTP.body.getPosition( ).y ), 1f );
		puzzleScrew.puzzleManager.addMover( lm );
	}

	/**
	 * Initializes stripped screws for climbing, and adds them to the skeleton.
	 */
	private void initClimbingScrews( ) {
		climbingScrews = new ArrayList< StrippedScrew >( );
		float x1 = 1.75f;
		float x2 = 2.0f;
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

	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = platBuilder.setWidth( 10 )
				.setHeight( 1 ).setOneSided( true ).setPosition( -1000, 200 )
				.setTexture( testTexture ).setFriction( 1f )
				.buildTilePlatform( );
		slidingPlatform.body.setType( BodyType.DynamicBody );

		PrismaticJointDef prismaticJointDef = JointFactory
				.constructSlidingJointDef( skeleton.body, slidingPlatform.body,
						slidingPlatform.body.getWorldCenter( ), new Vector2( 1,
								0 ), 1.0f, 1f );
		PrismaticJoint j = ( PrismaticJoint ) world
				.createJoint( prismaticJointDef );
		skeleton.addBoneAndJoint( slidingPlatform, j );
		slidingPlatform.setMover( new SlidingMotorMover(
				PuzzleType.PRISMATIC_SLIDER, j ) );

		TiledPlatform skeletonTest1 = platBuilder.setWidth( 10 ).setHeight( 1 )
				.setFriction( 1f ).setOneSided( false )
				.setPosition( -500, -200 ).setTexture( testTexture )
				.buildTilePlatform( );
		skeletonTest1.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformFixed( skeletonTest1 );

		TiledPlatform skeletonTest2 = platBuilder.setWidth( 10 ).setHeight( 1 )
				.setOneSided( false ).setPosition( 500, 300 )
				.setTexture( testTexture ).setFriction( 1f )
				.buildTilePlatform( );
		skeletonTest2.setOneSided( true );
		skeletonTest2.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenter( skeletonTest2 );

		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( );

		PlatformBuilder builder = platBuilder.setWidth( 1 ).setHeight( 2 )
				.setOneSided( false )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.setTexture( testTexture ).setFriction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.setPosition( ( -100f - i * 40 ),
					220f ).setHeight( 2 + i ).buildTilePlatform( );

			piston.body.setType( BodyType.DynamicBody );
			PrismaticJoint pistonJoint = jointBuilder.bodyB( ( Entity ) piston )
					.anchor( piston.body.getWorldCenter( ) ).build( );
			// Something is still not quite right with this, try replacing 3
			// with 0.
			piston.setMover( new PistonMover( pistonJoint, 0f, i / 10.0f + 2f ) );
			piston.body.setSleepingAllowed( false );
			skeleton.addBoneAndJoint( piston, pistonJoint );
		}

		
		builder = platBuilder.setWidth( 20 ).setHeight( 1 )
				.setOneSided( true )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.setTexture( testTexture ).setFriction( 1f );
		// .buildTilePlatform( world );

		TiledPlatform elevator = builder.setPosition( -1500, 150 ).buildTilePlatform( );
		elevator.body.setType( BodyType.DynamicBody );
		PrismaticJoint pistonJ =  jointBuilder.bodyB( ( Entity ) elevator )
				.anchor( elevator.body.getWorldCenter( ) ).build( );
		
		elevator.setMover( new PistonMover( pistonJ, 0f,  2f ) );
		elevator.body.setSleepingAllowed( false );
		skeleton.addBoneAndJoint( elevator, pistonJ );
		
		ComplexPlatform gear = new ComplexPlatform( "gear", new Vector2(
				1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX ), null, 3,
				world, "gearSmall" );
		gear.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenterWithRot( gear, 1f );
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

		player1.update( deltaTime );
		player2.update( deltaTime );
		puzzleScrew.update( deltaTime );
		entityManager.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		puzzleScrew.draw( batch );
		rootSkeleton.draw( batch );
		player1.draw( batch );
		player2.draw( batch );
		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 2 );
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