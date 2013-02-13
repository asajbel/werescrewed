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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
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
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class EntityDefTestScreen implements com.badlogic.gdx.Screen {

	// FIELDS

	// Variables

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private EntityManager entityManager;
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

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public EntityDefTestScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -45 ), true );
		entityManager = new EntityManager( );
		skeleton = new Skeleton( "", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "", Vector2.Zero, null, world );
		entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );
		platBuilder = new PlatformBuilder( world );
		testTexture = WereScrewedGame.manager.get(
				"assets/common/data/TilesetTest.png", Texture.class);

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
		tiledPlat = platBuilder.position( 700.0f, 100.0f )
				.dimensions( 10, 1 ).texture( testTexture )
				.name( "tp" ).resitituion( 0.0f ).buildTilePlatform( );
		tiledPlat.body.setType( BodyType.DynamicBody );
		tiledPlat.body.setFixedRotation( false );
		skeleton.addPlatform( tiledPlat );

		// Tiled Rectangle Platform
		/*
		rectile = platBuilder.setPosition( -200.0f, 600.f )
				.setDimensions( 20,3 ).setTexture( testTexture )
				.setName( "rectangle tiled" ).setResitituion( 0.0f ).buildTilePlatform( );
		rectile.body.setType( BodyType.DynamicBody );
		rectile.body.setFixedRotation( false );
		skeleton.addPlatform( rectile );
		*/
		
		// Tiled Single Platform
		singTile = platBuilder.position( -1.0f, 1000.0f )
				.dimensions( 1, 1 ).texture( testTexture )
				.name( "Single Tiled" ).resitituion( 0.0f ).buildTilePlatform( );
		singTile.body.setType( BodyType.DynamicBody );
		singTile.body.setFixedRotation( false );
		skeleton.addPlatform( singTile );

		// Moving platform
		movingTP = platBuilder.position( 0.0f, 120.0f )
				.dimensions( 10, 1 ).texture( testTexture )
				.name( "movingTP" ).resitituion( 0.0f )
				.buildTilePlatform( );
		movingTP.body.setType( BodyType.KinematicBody );
		buildMoverPlatforms( );

		// Ground
		ground = platBuilder.position( 0.0f, 0.0f ).name( "ground" )
				.dimensions( 200, 1 ).texture( testTexture )
				.kinematic( ).oneSided( false )
				.resitituion( 0.0f ).buildTilePlatform( );
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
		puzzleScrew.puzzleManager.addMover( movingTP.name, lm );
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

	/**
	 * Initializes settings for moving platforms and adds them to the skeleton
	 */
	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = platBuilder.width( 10 )
				.height( 1 ).oneSided( true ).position( -1000, 200 )
				.texture( testTexture ).friction( 1f )
				.buildTilePlatform( );
		slidingPlatform.body.setType( BodyType.DynamicBody );

		PrismaticJointDef prismaticJointDef = JointFactory
				.constructSlidingJointDef( skeleton.body, slidingPlatform.body,
						slidingPlatform.body.getWorldCenter( ), new Vector2( 1,
								0 ), 1.0f, 1f );
		PrismaticJoint j = ( PrismaticJoint ) world
				.createJoint( prismaticJointDef );
		skeleton.addDynamicPlatform( slidingPlatform );
		slidingPlatform.setMover( new SlidingMotorMover(
				PuzzleType.PRISMATIC_SLIDER, j ) );

		TiledPlatform skeletonTest1 = platBuilder.width( 10 ).height( 1 )
				.friction( 1f ).oneSided( false )
				.position( -500, -200 ).texture( testTexture )
				.buildTilePlatform( );
		skeletonTest1.body.setType( BodyType.DynamicBody );
		skeleton.addDynamicPlatform( skeletonTest1 );

		TiledPlatform skeletonTest2 = platBuilder.width( 10 ).height( 1 )
				.oneSided( false ).position( 500, 300 )
				.texture( testTexture ).friction( 1f )
				.buildTilePlatform( );
		skeletonTest2.setOneSided( true );
		skeletonTest2.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenter( skeletonTest2 );

		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( );

		PlatformBuilder builder = platBuilder.width( 1 ).height( 3 )
				.oneSided( false )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.position( ( -100f - i * 40 ),
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

		
		builder = platBuilder.width( 20 ).height( 1 )
				.oneSided( true )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );
		// .buildTilePlatform( world );

		TiledPlatform elevator = builder.position( -1500, 150 )
				.moveable( true ).buildTilePlatform( );
		elevator.body.setType( BodyType.DynamicBody );
		PrismaticJoint pistonJ = jointBuilder.bodyB( ( Entity ) elevator )
		.anchor( elevator.body.getWorldCenter( ) ).build( );
		
		elevator.setMover( new PistonMover( pistonJ, 0f,  2f ) );
		elevator.body.setSleepingAllowed( false );
		skeleton.addDynamicPlatform( elevator );
		
		ComplexPlatform gear = new ComplexPlatform( "gear", new Vector2(
				1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX ), null, 3,
				world, "gearSmall" );
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
		gear.setSolid( true );
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