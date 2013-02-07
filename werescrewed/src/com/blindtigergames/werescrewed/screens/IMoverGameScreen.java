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

public class IMoverGameScreen implements com.badlogic.gdx.Screen {

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private EntityManager entityManager;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private TiledPlatform tiledPlat, ground, movingTP;
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
	public IMoverGameScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -45 ), true );
		entityManager = new EntityManager( );
		skeleton = new Skeleton( "", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "", Vector2.Zero, null, world );
		entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );
		platBuilder = new PlatformBuilder( world );
		testTexture = new Texture( Gdx.files.internal( "data/rletter.png" ) );

		// Initialize camera
		initCamera( );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// Initialize platforms
		initTiledPlatforms( );

		// Initialize screws
		//initStructureScrews( );
		//initPuzzleScrews( );
		//initClimbingScrews( );

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
				.setKinematic( )
				.setName( "tp" ).setResitituion( 0.0f ).buildTilePlatform( );
		tiledPlat.body.setFixedRotation( false );
		skeleton.addKinematicPlatform( tiledPlat );

		// Moving platform
		movingTP = platBuilder.setPosition( 0.0f, 350.0f )
				.setDimensions( 10, 1 ).setTexture( testTexture )
				.setName( "movingTP" ).setResitituion( 0.0f )
				.buildTilePlatform( );
		movingTP.body.setType( BodyType.KinematicBody );
		skeleton.addKinematicPlatform( movingTP );
		
		//buildMoverPlatforms( );
		TiledPlatform skeletonTest2 = platBuilder.setWidth( 10 ).setHeight( 1 )
				.setOneSided( false ).setPosition( 500, 300 )
				.setTexture( testTexture ).setFriction( 1f )
				.setDynamic( )
				
				//.setOneSided( true )
				.setName( "dynamicTiledPlat1" )
				.buildTilePlatform( );
		skeleton.addDynamicPlatform( skeletonTest2 );
		skeletonTest2.body.setFixedRotation( false );//WHY!?
		

		// Ground
		ground = platBuilder.setPosition( 0.0f, 0.0f ).setName( "ground" )
				.setDimensions( 200, 1 ).setTexture( testTexture )
				.setKinematic( )
				.setResitituion( 0.0f ).buildTilePlatform( );
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

		ground = platBuilder.setPosition( 0.0f, 0.0f ).setName( "ground" )
				.setDimensions( 100, 1 ).setTexture( testTexture )
				.setKinematic( )
				.setResitituion( 0.0f ).buildTilePlatform( );
		skeleton.addKinematicPlatform( ground );
		//skeleton.addPlatform( tp ); // Tp already has a structureScrew holding
									// it up

		/*
		 * Comment if you don't want stew's moving platforms in your way!
		 */
		buildMoverPlatforms( );
		rootSkeleton.addSkeleton( skeleton );

		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		new FPSLogger( );

	}

	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = platBuilder.setWidth( 10 )
				.setHeight( 1 ).setOneSided( true ).setPosition( -1000, 200 )
				.setTexture( testTexture ).setFriction( 1f )
				.setDynamic( )
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

		TiledPlatform skeletonTest1 = platBuilder.setWidth( 10 ).setHeight( 1 )
				.setFriction( 1f ).setOneSided( false )
				.setPosition( -500, -200 ).setTexture( testTexture )
				.setKinematic()
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( skeletonTest1 );

		

		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( );

		PlatformBuilder builder = platBuilder.setWidth( 1 ).setHeight( 3 )
				.setOneSided( false )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.setTexture( testTexture ).setFriction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.setPosition( ( -100f - i * 40 ),
					220f ).buildTilePlatform( );

			piston.body.setType( BodyType.DynamicBody );
			PrismaticJoint pistonJoint = jointBuilder.bodyB( ( Entity ) piston )
					.anchor( piston.body.getWorldCenter( ) ).build( );
			// Something is still not quite right with this, try replacing 3
			// with 0.
			piston.setMover( new PistonMover( pistonJoint, 0f, i / 10.0f + 2f ) );
			piston.body.setSleepingAllowed( false );
			skeleton.addDynamicPlatform( piston );
		}

		ComplexPlatform gear = new ComplexPlatform( "gear", new Vector2(
				1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX ), null, 3, world,
				"gearSmall" );
		gear.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenterWithMot( gear, 1f );
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
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			rootSkeleton.translate( 0.0f, -0.01f );
		}
		
		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			rootSkeleton.rotate( -0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			rootSkeleton.rotate( 0.01f );
		}

		player1.update( deltaTime );
		player2.update( deltaTime );
		//puzzleScrew.update( deltaTime );
		//entityManager.update( deltaTime );
		rootSkeleton.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		//puzzleScrew.draw( batch );
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