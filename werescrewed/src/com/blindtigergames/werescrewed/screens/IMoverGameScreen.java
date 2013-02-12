package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.PistonMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
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

/**
 * Screen to test out moving platforms and skeletons
 * 
 * Debug Keys in use: z - move skeleton down x - move skeleton up c - rotate
 * skeleton left v - rotate skeleton right
 * 
 * @author stew
 * 
 */
public class IMoverGameScreen implements com.badlogic.gdx.Screen {

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private EntityManager entityManager;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private TiledPlatform tiledPlat, ground, kinPlat1;
	TiledPlatform skeletonTest2;
	private PlatformBuilder platBuilder;
	private PuzzleScrew puzzleScrew;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private ArrayList< StrippedScrew > climbingScrews;
	private boolean debug = true;
	private boolean debugTest = true;
	
	private TweenManager tweenManager;
	
	private Tween testTween;

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
		rootSkeleton.addSkeleton( skeleton );
		entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );
		platBuilder = new PlatformBuilder( world );
		testTexture = WereScrewedGame.manager.get("assets/data/common/TilesetTest.png", Texture.class);
		
		
		Tween.registerAccessor(Entity.class, new EntityAccessor());
		Tween.registerAccessor(Entity.class, new PlatformAccessor());
		tweenManager = new TweenManager( );
		Tween.setWaypointsLimit( 1 );
		
		// Initialize camera
		initCamera( );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// tp = platBuilder.position( 350.0f, 100.0f ).dimensions( 10, 1 )
		// .texture( testTexture ).name( "tp" ).resitituion( 0.0f )
		// .buildTilePlatform( );


		kinPlat1 = platBuilder.position( 350.0f, 170.0f ).dimensions( 10, 1 )
				.texture( testTexture ).name( "kinPlat1" ).resitituion( 0.0f )
				.kinematic( )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( kinPlat1 );
		
		testTween = Tween.to( kinPlat1, PlatformAccessor.LOCAL_POS_XY, 1.0f )
			 .target( kinPlat1.getLocalPos( ).x+100, kinPlat1.getLocalPos( ).y )
			 .repeatYoyo( Tween.INFINITY, 0 )
			 .start(tweenManager);
		
		

		// buildMoverPlatforms( );
		skeletonTest2 = platBuilder.width( 10 ).height( 1 )
				.oneSided( false ).position( 500, 300 ).texture( testTexture )
				.friction( 1f ).dynamic( )
				// .setOneSided( true )
				.name( "dynamicTiledPlat1" ).buildTilePlatform( );
		skeleton.addDynamicPlatform( skeletonTest2 );
		//skeleton.addPlatform( skeletonTest2 );
		skeletonTest2.body.setFixedRotation( false );// WHY!?

		// Ground
		ground = platBuilder.position( 0.0f, 0.0f ).name( "ground" )
				.dimensions( 200, 1 ).texture( testTexture ).kinematic( )
				.resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addKinematicPlatform( ground );

		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 1.0f, 1.0f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 1.5f, 1.5f ).buildPlayer( );
		
		initStructureScrews();
		initPuzzleScrews();
		initClimbingScrews();

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
	 * Initialize the platform screws' settings and add them to the entity
	 * manager and skeleton
	 */
	private void initStructureScrews( ) {
		StructureScrew leftPlatScrew = new StructureScrew( "", new Vector2(
				skeletonTest2.body.getPosition( ).x - 0.5f,
				skeletonTest2.body.getPosition( ).y ), 50, skeletonTest2, skeleton,
				world );

		StructureScrew rightPlatScrew = new StructureScrew( "", new Vector2(
				skeletonTest2.body.getPosition( ).x + 0.5f,
				skeletonTest2.body.getPosition( ).y ), 50, skeletonTest2, skeleton,
				world );
		StrippedScrew hanginScrew = new StrippedScrew( "", world, new Vector2(
				skeletonTest2.body.getPosition( ).x + 0.03f, skeletonTest2.body.getPosition( ).y ), skeletonTest2 );
		skeletonTest2.addScrew( hanginScrew );
		skeletonTest2.addScrew( leftPlatScrew );
		skeletonTest2.addScrew( rightPlatScrew );
	}

	/**
	 * Initializes settings for puzzle screws
	 */
	private void initPuzzleScrews( ) {

		Vector2 axis = new Vector2( 1, 0 );
		PrismaticJointDef jointDef = new PrismaticJointDef( );
		jointDef.initialize( kinPlat1.body, skeleton.body,
				kinPlat1.body.getPosition( ), axis );
		jointDef.enableMotor = true;
		jointDef.enableLimit = true;
		jointDef.lowerTranslation = -2.5f;
		jointDef.upperTranslation = 3.0f;
		jointDef.motorSpeed = 7.0f;
		puzzleScrew = new PuzzleScrew( "001", new Vector2( 0.0f, 0.2f ), 50,
				skeleton, world );
		puzzleScrew.puzzleManager.addEntity( kinPlat1 );
		LerpMover lm = new LerpMover(
				new Vector2( kinPlat1.body.getPosition( ).x,
						kinPlat1.body.getPosition( ).y ), new Vector2(
						kinPlat1.body.getPosition( ).x + 1.75f,
						kinPlat1.body.getPosition( ).y ), 0.003f );
		puzzleScrew.puzzleManager.addMover( lm );
		skeleton.addScrewForDraw( puzzleScrew );
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
		TiledPlatform slidingPlatform = platBuilder.width( 10 ).height( 1 )
				.oneSided( true ).position( -1000, 200 ).texture( testTexture )
				.friction( 1f ).buildTilePlatform( );
		slidingPlatform.body.setType( BodyType.DynamicBody );

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
				.friction( 1f ).oneSided( false ).position( -500, -200 )
				.texture( testTexture ).buildTilePlatform( );
		skeleton.addKinematicPlatform( skeletonTest1 );

		TiledPlatform skeletonTest2 = platBuilder.width( 10 ).height( 1 )
				.oneSided( false ).position( 500, 300 ).texture( testTexture )
				.friction( 1f ).buildTilePlatform( );
		skeletonTest2.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenter( skeletonTest2 );

		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( );

		PlatformBuilder builder = platBuilder.width( 1 ).height( 3 )
				.oneSided( false ).dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.position( ( -100f - i * 40 ), 220f )
					.buildTilePlatform( );

			PrismaticJoint pistonJoint = jointBuilder.bodyB( ( Entity ) piston )
					.anchor( piston.body.getWorldCenter( ) ).build( );
			// Something is still not quite right with this, try replacing 3
			// with 0.
			piston.setMover( new PistonMover( pistonJoint, 0f, i / 10.0f + 2f ) );
			piston.body.setSleepingAllowed( false );
			skeleton.addDynamicPlatform( piston );
		}

		Platform gear = builder.name( "gear" )
				.position( 1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX )
				.texture( null )
				.setScale( 3f )
				.type( "gearSmall" )
				.dynamic( )
				.buildComplexPlatform( );
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
			// Gdx.app.log( "derP", "yerp" );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			rootSkeleton.rotate( -0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			rootSkeleton.rotate( 0.01f );
		}
		
		tweenManager.update( deltaTime );

		player1.update( deltaTime );
		player2.update( deltaTime );
		
		
		// puzzleScrew.update( deltaTime );
		// entityManager.update( deltaTime );
		rootSkeleton.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		// puzzleScrew.draw( batch );
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