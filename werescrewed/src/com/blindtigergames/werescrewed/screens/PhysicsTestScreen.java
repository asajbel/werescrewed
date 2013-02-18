package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.mover.RotateByDegree;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzlePistonTweenMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.rope.Rope;
import com.blindtigergames.werescrewed.screws.BossScrew;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;


public class PhysicsTestScreen implements com.badlogic.gdx.Screen {

	// FIELDS

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
		world = new World( new Vector2( 0, -35 ), true );
		// Initialize camera
		initCamera( );
		// entityManager = new EntityManager( );
		skeleton = new Skeleton( "skeleton", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );
		rootSkeleton.mover = new RockingMover( -0.02f, 1.0f );
		// entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );
		platBuilder = new PlatformBuilder( world );
		testTexture = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/TilesetTest.png", Texture.class );

		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );
		// Uncomment for test anchor
		// anchor = new Anchor( new Vector2( 7 * Util.BOX_TO_PIXEL,
		// Util.BOX_TO_PIXEL ), world, 5f );
		// anchor.deactivate( );
		// AnchorList.getInstance( ).addAnchor( anchor );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// Initialize platforms
		initTiledPlatforms( );

		// Initialize screws
		initStructureScrews( );
		initPuzzleScrews( );
		initClimbingScrews( );
		initPulley( );

		// rope = new Rope( "rope", new Vector2 (2000.0f * Util.PIXEL_TO_BOX,
		// 400.0f* Util.PIXEL_TO_BOX), null, world );
		// Add players
		// First player has to have the name "player1"
		// Second player has to have the name "player2"
		// Otherwise input handler breaks

		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 1.0f, 1.0f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 1.5f, 1.5f ).buildPlayer( );

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
		cam = new Camera( width, height, world );
	}

	/**
	 * Initializes tiled platforms' settings, and adds them to the skeleton
	 */
	private void initTiledPlatforms( ) {
		// Tiled Platform
		tiledPlat = platBuilder.position( 700.0f, 125.0f ).dimensions( 10, 1 )
				.texture( testTexture ).dynamic( ).name( "tp" )
				.resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addDynamicPlatform( tiledPlat );

		// Tiled Single Platform
		singTile = platBuilder.position( -1.0f, 1000.0f ).dimensions( 1, 1 )
				.texture( testTexture ).dynamic( ).name( "Single Tiled" )
				.resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatform( singTile );
		singTile.body.setFixedRotation( false );

		// Moving platform
		movingTP = platBuilder.position( 0.0f, 120.0f ).dimensions( 10, 1 )
				.texture( testTexture ).name( "movingTP" ).resitituion( 0.0f )
				.kinematic( ).buildTilePlatform( );
		skeleton.addKinematicPlatform( movingTP );

		buildMoverPlatforms( );

		// TODO : FIX ONESIDED BUG,
		// Ground: SHOULD NEVER BE ONESIDED
		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addKinematicPlatform( ground );
	}

	/**
	 * Initialize the platform screws' settings and add them to the entity
	 * manager and skeleton
	 */
	private void initStructureScrews( ) {
		/*
		 * StructureScrew leftPlatScrew = new StructureScrew( "", new Vector2(
		 * tiledPlat.body.getPosition( ).x - 0.5f, tiledPlat.body.getPosition(
		 * ).y ), 50, tiledPlat, skeleton, world );
		 */
		StructureScrew leftPlatScrew = new ScrewBuilder( )
				.position(
						tiledPlat.body.getPosition( ).x * Util.BOX_TO_PIXEL
								- ( tiledPlat.sprite.getWidth( ) ),
						tiledPlat.body.getPosition( ).y * Util.BOX_TO_PIXEL )
				.entity( tiledPlat ).skeleton( skeleton ).world( world )
				.buildStructureScrew( );
		// StructureScrew rightPlatScrew = new StructureScrew( "", new Vector2(
		// tiledPlat.body.getPosition( ).x + 0.5f,
		// tiledPlat.body.getPosition( ).y ), 50, tiledPlat, skeleton,
		// world );
		BossScrew bossBolt = new BossScrew( "", new Vector2(
				tiledPlat.body.getPosition( ).x * Util.BOX_TO_PIXEL
						+ ( tiledPlat.sprite.getWidth( ) ),
				tiledPlat.body.getPosition( ).y * Util.BOX_TO_PIXEL ), 50,
				tiledPlat, skeleton, world );
		tiledPlat.addScrew( bossBolt );
		tiledPlat.addScrew( leftPlatScrew );
		// tiledPlat.addScrew( rightPlatScrew );
	}

	/**
	 * Initializes settings for puzzle screws
	 */
	private void initPuzzleScrews( ) {
		// two fliping platforms
		TiledPlatform flipPlat1 = platBuilder.position( 0.0f, 370f )
				.dimensions( 5, 1 ).texture( testTexture ).name( "001_flip1" )
				.resitituion( 0.0f ).kinematic( ).buildTilePlatform( );
		skeleton.addKinematicPlatform( flipPlat1 );

		// two fliping platforms
		TiledPlatform flipPlat2 = platBuilder.position( 200.0f, 475f )
				.dimensions( 5, 2 ).texture( testTexture ).name( "001_flip2" )
				.resitituion( 0.0f ).kinematic( ).buildTilePlatform( );
		flipPlat2.setLocalRot( -90 * Util.DEG_TO_RAD );
		skeleton.addKinematicPlatform( flipPlat2 );

		// rotate puzzle screw control
		RotateByDegree rm = new RotateByDegree( 0.0f, -90.0f, 0, 0.5f );
		PuzzleScrew puzzleScrew = new PuzzleScrew( "001",
				new Vector2( 32f, 32f ), 50, skeleton, world, 0, false );
		puzzleScrew.puzzleManager.addEntity( flipPlat1 );
		puzzleScrew.puzzleManager.addMover( flipPlat1.name, rm );
		// also add a up mover to movingTP
		LerpMover lm2 = new LerpMover( movingTP.body.getPosition( ).mul(
				Util.BOX_TO_PIXEL ),
				new Vector2( movingTP.body.getPosition( ).x, movingTP.body
						.getPosition( ).y + 0.3f ).mul( Util.BOX_TO_PIXEL ),
				1f, true, PuzzleType.PUZZLE_SCREW_CONTROL, LinearAxis.VERTICAL );
		puzzleScrew.puzzleManager.addEntity( movingTP );
		puzzleScrew.puzzleManager.addMover( movingTP.name, lm2 );

		rm = new RotateByDegree( -90.0f, 0.0f, 0, 0.5f );
		PuzzlePistonTweenMover pptm = new PuzzlePistonTweenMover( flipPlat2,
				new Vector2( 0, 100 ), 1, 1, 0, 0 );
		// PistonTweenMover ptm = new PistonTweenMover( flipPlat2, new
		// Vector2(100,0), 1, 1, 0, 0, 0 );
		// flipPlat2.setMover( pptm );
		puzzleScrew.puzzleManager.addEntity( flipPlat2 );
		puzzleScrew.puzzleManager.addMover( flipPlat2.name, pptm );
		skeleton.addScrewForDraw( puzzleScrew );

		// lerp puzzle screw control
		PuzzleScrew puzzleScrew2 = new PuzzleScrew( "002", new Vector2( 150f,
				32f ), 50, skeleton, world, 0, false );
		LerpMover lm = new LerpMover( movingTP.body.getPosition( ).mul(
				Util.BOX_TO_PIXEL ), new Vector2(
				movingTP.body.getPosition( ).x + 1.75f,
				movingTP.body.getPosition( ).y ).mul( Util.BOX_TO_PIXEL ), 1f,
				true, PuzzleType.PUZZLE_SCREW_CONTROL, LinearAxis.HORIZONTAL );
		puzzleScrew2.puzzleManager.addEntity( movingTP );
		puzzleScrew2.puzzleManager.addMover( movingTP.name, lm );
		skeleton.addScrewForDraw( puzzleScrew2 );

	}

	/**
	 * Initializes stripped screws for climbing, and adds them to the skeleton.
	 */
	private void initClimbingScrews( ) {
		climbingScrews = new ArrayList< StrippedScrew >( );
		float x1 = 420f;
		float x2 = 600f;
		float y1 = 256f;
		float dy = 160f;
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
		TiledPlatform slidingPlatform = platBuilder.width( 10 ).height( 1 )
				.oneSided( true ).position( -1000, 200 ).texture( testTexture )
				.friction( 1f ).dynamic( ).buildTilePlatform( );

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
				.friction( 1f ).oneSided( false ).position( 500, 250 )
				.texture( testTexture ).kinematic( ).buildTilePlatform( );
		skeleton.addKinematicPlatform( skeletonTest1 );

		rope = new Rope( "rope", new Vector2( 8f, 1.5f ), new Vector2( 16.0f,
				64.0f ), 5, null, world );


		TiledPlatform pathPlatform = platBuilder.dimensions( 4, 1 )
				.position( 1600, 100 ).friction( 1f ).kinematic( )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( pathPlatform );
		// build path. TODO: make building paths easier!!
		pathPlatform
				.setMover( new TimelineTweenMover( Timeline
						.createSequence( )
						.push( Tween
								.to( pathPlatform,
										PlatformAccessor.LOCAL_POS_XY, 2 )
								.target( 300, 0 )
								.ease( TweenEquations.easeNone ).start( ) )
						.push( Tween
								.to( pathPlatform,
										PlatformAccessor.LOCAL_POS_XY, 2 )
								.target( 300, 300 )
								.ease( TweenEquations.easeNone ).start( ) )
						.push( Tween
								.to( pathPlatform,
										PlatformAccessor.LOCAL_POS_XY, 2 )
								.target( 0, 300 )
								.ease( TweenEquations.easeNone ).start( ) )
						.push( Tween
								.to( pathPlatform,
										PlatformAccessor.LOCAL_POS_XY, 2 )
								.target( 0, 0 ).ease( TweenEquations.easeNone )
								.start( ) ).repeat( Tween.INFINITY, 0 ).start( ) ) );


		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( ).world( world );

		// for building dynamic pistons (they don't work very well)
		PlatformBuilder builder = platBuilder.width( 1 ).height( 3 )
				.oneSided( false ).dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );

		/*//old way of building dynamic pistons
		 * for ( int i = 0; i < 10; ++i ) { TiledPlatform piston =
		 * builder.position( ( -100f - i * 40 ), 220f ) .buildTilePlatform( );
		 * 
		 * PrismaticJoint pistonJoint = jointBuilder.bodyB( ( Entity ) piston )
		 * .anchor( piston.body.getWorldCenter( ) ).build( ); // Something is
		 * still not quite right with this, try replacing 3 // with 0.
		 * piston.setMover( new PistonMover( pistonJoint, 3f, i / 10.0f + 2f )
		 * ); // piston.body.setSleepingAllowed( false );
		 * skeleton.addDynamicPlatform( piston ); }
		 */

		// BUILD ROW OF PISTONS with new kinematic way
		builder = platBuilder.width( 1 ).height( 3 ).oneSided( false )
				.kinematic( ).setScale( 1 ).texture( testTexture )
				.friction( 1f );
		// TODO: bug - these are placed in the incorrect position!
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform pistonKin = builder.name( "pistonKin" + i )
					.position( -200f - i * 40, 500f ).buildTilePlatform( );
			skeleton.addKinematicPlatform( pistonKin );
			pistonKin.setMover( new PistonTweenMover( pistonKin, new Vector2(
					0, 300 ), 1f, 3f, 1f, 0f, i / 10.0f + 1 ) );
			//System.out.println( "Piston" + i + ": " + pistonKin.getPosition( ) );
		}

		builder = platBuilder.width( 20 ).height( 1 ).oneSided( true )
				.dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( testTexture ).friction( 1f );

		// TiledPlatform elevator = builder.position( -1500, 150 ).moveable(
		// true )
		// .buildTilePlatform( );
		//
		// //PrismaticJoint pistonJ = jointBuilder.bodyB( ( Entity ) elevator )
		// // .anchor( elevator.body.getWorldCenter( ) ).build( );
		//
		// //elevator.setMover( new PistonMover( pistonJ, 0f, 2f ) );
		// elevator.body.setSleepingAllowed( false );

		// 1000 - 1219 for perfect gears
		Platform gear = builder.name( "gear" )
				.position( 1219 * Util.PIXEL_TO_BOX, 320 * Util.PIXEL_TO_BOX )
				.texture( null ).setScale( 3f ).type( "gearSmall" )
				.buildComplexPlatform( );
		skeleton.addPlatformRotatingCenterWithMot( gear, 1f );
		Platform gear2 = builder.name( "gear2" )
				.position( 1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX )
				.texture( null ).setScale( 3f ).type( "gearSmall" )
				.buildComplexPlatform( );
		skeleton.addPlatformRotatingCenter( gear2 );
		gear2.quickfixCollisions( );
	}

	public void initPulley( ) {
		TiledPlatform singTile = platBuilder.position( -1200.0f, 400.0f )
				.dimensions( 1, 1 ).texture( testTexture ).dynamic( )
				.name( "Single Tiled" ).resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatform( singTile );
		singTile.body.setFixedRotation( false );

		TiledPlatform singTile2 = platBuilder.position( -1300.0f, 400.0f )
				.dimensions( 1, 1 ).texture( testTexture ).dynamic( )
				.name( "Single Tiled" ).resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatform( singTile2 );
		singTile2.body.setFixedRotation( false );

		Vector2 g1 = new Vector2( singTile.body.getWorldCenter( ).x,
				singTile.body.getWorldCenter( ).y - 200.0f * Util.PIXEL_TO_BOX );
		Vector2 g2 = new Vector2( singTile2.body.getWorldCenter( ).x,
				singTile2.body.getWorldCenter( ).y - 200.0f * Util.PIXEL_TO_BOX );
		PulleyJointDef pjd = new PulleyJointDef( );
		pjd.initialize( singTile.body, singTile2.body, g1, g2,
				singTile.body.getWorldCenter( ),
				singTile2.body.getWorldCenter( ), 1.0f );

		world.createJoint( pjd );

		skeleton.addStrippedScrew( new StrippedScrew( "", world, new Vector2(
				singTile.body.getPosition( ).x * Util.BOX_TO_PIXEL,
				singTile.body.getPosition( ).y * Util.BOX_TO_PIXEL ), singTile ) );

		skeleton.addStrippedScrew( new StrippedScrew( "", world, new Vector2(
				singTile2.body.getPosition( ).x * Util.BOX_TO_PIXEL,
				singTile2.body.getPosition( ).y * Util.BOX_TO_PIXEL ),
				singTile2 ) );
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

		// System.out.println( "skele:" + skeleton.getPosition( ) );

		if ( Gdx.input.isKeyPressed( Input.Keys.Z ) ) {
			rootSkeleton.translateBy( 0.0f, 0.01f );
			//Gdx.app.log( "BANG:", "BANG" );
			// rootSkeleton.body.setLinearVelocity( new Vector2(0,1f) );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			rootSkeleton.translateBy( 0.0f, -0.01f );
			// rootSkeleton.body.setLinearVelocity( new Vector2(0,-1f) );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			rootSkeleton.rotateBy( -0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			rootSkeleton.rotateBy( 0.01f );
		}
		
		//Gdx.app.log( "Root:", rootSkeleton.toString( ) );
		//Gdx.app.log( "Skele:", skeleton.toString( ) );

		player1.update( deltaTime );
		player2.update( deltaTime );
		rootSkeleton.update( deltaTime );
		rope.update( deltaTime );
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		rootSkeleton.draw( batch );
		rope.draw( batch );
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