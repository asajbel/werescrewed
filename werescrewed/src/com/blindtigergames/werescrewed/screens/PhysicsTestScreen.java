package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateByDegree;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzlePistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

public class PhysicsTestScreen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	private Camera cam;
	private SpriteBatch batch;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private ProgressManager progressManager;
	private Player player1, player2;
	@SuppressWarnings( "unused" )
	private TiledPlatform tiledPlat, ground, movingTP, singTile, rectile,
			ropePlatform;
	private PlatformBuilder platBuilder;
	private Skeleton skeleton;
	private Skeleton oldRootSkeleton;
	RootSkeleton rootSkeleton;
	private boolean debug = true;
	private boolean debugTest = true;
	public Steam testSteam;
	public SpriteBatch particleBatch;
	TiledPlatform test2;

	private Skeleton dynSkel2;
	private Skeleton s;
	private Hazard saw;

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
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		skeleton = new Skeleton( "skeleton", new Vector2( 500, 0 ), null,
				world, BodyType.KinematicBody );

		platBuilder = new PlatformBuilder( world );

		// Uncomment for test anchor
		// anchor = new Anchor( new Vector2( 7 * Util.BOX_TO_PIXEL,
		// Util.BOX_TO_PIXEL ), world, 5f );
		// anchor.deactivate( );
		// AnchorList.getInstance( ).addAnchor( anchor );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// Initialize ground platformbb

		player1 = new PlayerBuilder( ).name( "player1" ).definition( "red_male" ).world( world )
				.position( -700.0f, 100f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).definition( "red_female" ).world( world )
				.position( -700f, 100f ).buildPlayer( );

		rootSkeleton = new RootSkeleton( "Root Skeleton", new Vector2( 0, 0 ),
				null, world );

		rootSkeleton.addSkeleton( skeleton );

		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );

		// debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		boolean stew = false;
		if ( stew ) {
			stewTest( );
		} else {
			initCheckPoints( );
			initTiledPlatforms( );
			connectedRoom( );
			movingSkeleton( );
		}
		//
	}

	void stewTest( ) {
		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 )
				// .texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0.0f )
				.buildTilePlatform( );

		ground.setCategoryMask( Util.KINEMATIC_OBJECTS,
				Util.CATEGORY_EVERYTHING );
		ground.body.getFixtureList( ).get( 0 ).getShape( ).setRadius( 0 );
		skeleton.addKinematicPlatform( ground );

		Skeleton dynSkel = new SkeletonBuilder( world ).position( 800, 500 )
				.dynamic( ).name( "dynSkele" ).build( );
		// dynSkel.quickfixCollisions( );
		rootSkeleton.addSkeleton( dynSkel );

		// platforms on dynamic skeleton
		TiledPlatform plat6 = platBuilder.name( "dynPlat1" ).dynamic( )
				.position( 600, 500 ).dimensions( 1, 12 ).oneSided( false )
				.buildTilePlatform( );
		plat6.body.setFixedRotation( false );
		plat6.quickfixCollisions( );
		dynSkel.addDynamicPlatformFixed( plat6 );

	}

	// This is how you make a whole room fall, by welding everything together
	void connectedRoom( ) {
		test2 = platBuilder.name( "strucTest9" ).kinematic( )
				.position( 800, 100 ).dimensions( 5, 5 ).oneSided( false )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( test2 );

		StrippedScrew strScrew2 = new StrippedScrew( "strScrew4", new Vector2(
				500, 500 ), rootSkeleton, world );
		rootSkeleton.addStrippedScrew( strScrew2 );

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( rootSkeleton.body, strScrew2.body,
				rootSkeleton.body.getPosition( ) );
		world.createJoint( revoluteJointDef );

		StrippedScrew strScrew4 = new StrippedScrew( "strScrew5", new Vector2(
				500, 700 ), rootSkeleton, world );
		rootSkeleton.addStrippedScrew( strScrew4 );
		RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef( );
		revoluteJointDef2.initialize( rootSkeleton.body, strScrew4.body,
				rootSkeleton.body.getPosition( ) );
		world.createJoint( revoluteJointDef2 );

		dynSkel2 = new SkeletonBuilder( world ).position( 800, 500 ).build( );
		dynSkel2.body.setType( BodyType.DynamicBody );
		dynSkel2.body.setGravityScale( 0.1f );
		dynSkel2.setDensity( 100f );
		rootSkeleton.addSkeleton( dynSkel2 );

		StrippedScrew strScrew = new StrippedScrew( "strScrew3", new Vector2(
				700, 500 ), world );
		strScrew.addWeldJoint( dynSkel2 );
		dynSkel2.addStrippedScrew( strScrew );
		strScrew.body.setFixedRotation( false );

		// WeldJointDef s1 = new WeldJointDef();
		// s1.initialize( strScrew.body, dynSkel2.body, strScrew.getPosition( )
		// );
		// world.createJoint( s1 );

		// RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		// revoluteJointDef.initialize( strScrew.body, dynSkel2.body,
		// strScrew.getPosition( ) );
		// world.createJoint( revoluteJointDef );

		TiledPlatform plat6 = platBuilder.name( "weld1" ).dynamic( )
				.position( 600, 500 ).dimensions( 1, 12 ).oneSided( false )
				.buildTilePlatform( );
		plat6.body.setFixedRotation( false );
		plat6.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat6 );

		RevoluteJointDef r2 = new RevoluteJointDef( );
		r2.initialize( strScrew.body, plat6.body, plat6.getPosition( ) );
		world.createJoint( r2 );

		TiledPlatform plat7 = platBuilder.name( "weld2" ).dynamic( )
				.position( 800, 300 ).dimensions( 12, 1 ).oneSided( false )
				.buildTilePlatform( );
		plat7.body.setFixedRotation( false );
		plat7.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat7 );

		TiledPlatform plat8 = platBuilder.name( "weld3" ).dynamic( )
				.position( 800, 700 ).dimensions( 12, 1 ).oneSided( false )
				.buildTilePlatform( );
		plat8.body.setFixedRotation( false );
		plat8.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat8 );

		TiledPlatform test = platBuilder.name( "strucTest" ).kinematic( )
				.position( 800, 900 ).dimensions( 1, 5 ).oneSided( false )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( test );

		// StructureScrew s = new StructureScrew( "ss", new Vector2(800, 750),
		// 100, test,
		// world );
		// s.addStructureJoint( plat8 );
		// dynSkel2.addScrewForDraw( s );

		TiledPlatform plat9 = platBuilder.name( "weld4" ).dynamic( )
				.position( 1000, 500 ).dimensions( 1, 12 ).oneSided( false )
				.buildTilePlatform( );
		plat9.body.setFixedRotation( false );
		plat9.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat9 );

		StructureScrew s = new StructureScrew( "ss", plat8.getPositionPixel( )
				.add( plat8.getPixelWidth( ) / 2, 0 ), 100, world );
		s.addWeldJoint( plat8 );
		s.addWeldJoint( plat9 );
		dynSkel2.addScrewForDraw( s );

		StructureScrew s1 = new StructureScrew( "s1", plat7.getPositionPixel( )
				.add( plat7.getPixelWidth( ) / 2, 0 ), 100, world );
		s1.addWeldJoint( plat7 );
		s1.addWeldJoint( plat9 );
		dynSkel2.addScrewForDraw( s1 );

		StructureScrew s3 = new StructureScrew( "s2", plat8.getPositionPixel( )
				.sub( plat8.getPixelWidth( ) / 2, 0 ), 100, world );
		s3.addWeldJoint( plat6 );
		s3.addWeldJoint( plat8 );
		dynSkel2.addScrewForDraw( s3 );

		StructureScrew s5 = new StructureScrew( "s23", plat7.getPositionPixel( )
				.sub( plat7.getPixelWidth( ) / 2, 0 ), 100, world );
		s5.addWeldJoint( plat6 );
		s5.addWeldJoint( plat7 );
		dynSkel2.addScrewForDraw( s5 );

	}

	void movingSkeleton( ) {

		Skeleton top = new Skeleton( "skeleton7", new Vector2( -700, 1200 ),
				null, world, BodyType.KinematicBody );
		top.addMover( new RotateTweenMover( top, 3f, -Util.PI / 2, 1f, true ),
				RobotState.IDLE );
		rootSkeleton.addSkeleton( top );

		s = new Skeleton( "skeleton7", new Vector2( -700, 700 ), null, world,
				BodyType.KinematicBody );

		TiledPlatform ttt = platBuilder.name( "ttt" ).kinematic( )
				.position( -700, 1000 ).dimensions( 1, 5 ).oneSided( false )
				.buildTilePlatform( );
		s.addPlatform( ttt );

		// rootSkeleton.addSkeleton( s );

		top.addKinematicPlatform( s );
		// StructureScrew screw = new StructureScrew( "sdfasdf",
		// new Vector2(-700f, 500f),
		// 100, world );
		// screw.addStructureJoint( s );
		// screw.addWeldJoint( ttt );
		// dynSkel2.addScrewForDraw( screw );

		// RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		// revoluteJointDef.initialize( s.body, ttt.body, ttt.getPosition( ) );
		// world.createJoint( revoluteJointDef );

		Skeleton middleHang = new Skeleton( "middleHang", new Vector2( -700,
				400 ), null, world, BodyType.KinematicBody );
		// rootSkeleton.addSkeleton( middleHang );
		s.addKinematicPlatform( middleHang );

		// TiledPlatform test2 = platBuilder.name( "movetest2" ).kinematic( )
		// .position( -900, 500 ).dimensions( 1, 5).oneSided( false )
		// .buildTilePlatform( );
		// middleHang.addKinematicPlatform( test2 );

		// RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef( );
		// revoluteJointDef2.initialize( s.body, middleHang.body, s.getPosition(
		// ) );
		// world.createJoint( revoluteJointDef2 );

		PathBuilder pb = new PathBuilder( );
		// ttt.addMover( pb.begin( ttt ).ease( TweenEquations.easeInOutExpo
		// ).target( -1000, 0, 20 )
		// .target( 0, 0, 20 ).build( ), RobotState.IDLE);

		// TiledPlatform test = platBuilder.name( "movetest" ).kinematic( )
		// .position( -300, 500 ).dimensions( 1, 5).oneSided( false )
		// .buildTilePlatform( );
		// s.addKinematicPlatform( test );

		// saw = new Hazard( "sawblade", EntityDef.getDefinition( "sawBlade" ),
		// world,
		// new Vector2(-600.0f, 150.0f));
		// saw.body.setType( BodyType.DynamicBody );
		// s.addPlatformRotatingCenterWithMot( saw, 2f );

		// 1000 - 1219 for perfect gears
		// Platform gear = platBuilder.name( "gear" ).position( -1400, 320 )
		// .texture( null ).setScale( 3f ).type( "gearSmall" ).dynamic( )
		// .buildComplexPlatform( );
		// s.addPlatformRotatingCenterWithMot( gear, 1f );
		// Platform gear2 = platBuilder.name( "gear2" ).position( -1165, 320 )
		// .texture( null ).setScale( 3f ).type( "gearSmall" ).dynamic( )
		// .buildComplexPlatform( );
		// s.addPlatformRotatingCenter( gear2 );
		// gear2.quickfixCollisions( );

		// TiledPlatform piston = platBuilder.name( "piston" ).kinematic( )
		// .position( -100, 700 ).dimensions( 2, 5).oneSided( false )
		// .buildTilePlatform( );
		// piston.addMover( new PistonTweenMover( piston, new Vector2(
		// 0, -350 ), 0.5f, 3f, 1f, 0f, 1f ), RobotState.IDLE );
		// s.addKinematicPlatform( piston );
		// piston.setCrushing( true );

		// TiledPlatform pivot = platBuilder.position( 100.0f, 75f ).name( "rev"
		// )
		// .dimensions( 1, 2 )
		// .kinematic( ).oneSided( false ).restitution(0.0f )
		// .buildTilePlatform( );
		// s.addKinematicPlatform( pivot );

		// rfd.maxMotorTorque = 100.0f;

		// TiledPlatform crank = platBuilder.position( 100f, 125f).name( "crank"
		// )
		// .dimensions( 15, 1 )
		// .dynamic( ).oneSided( false ).restitution( 0.0f )
		// .buildTilePlatform( );
		// crank.quickfixCollisions( );
		// crank.setDensity( 0 );
		// crank.setCrushing( true );
		// s.addPlatform( crank );
		//
		// RevoluteJointDef rfd = new RevoluteJointDef( );
		// rfd.initialize( crank.body, pivot.body,
		// pivot.body.getPosition( ).add( new Vector2(0f, pivot.getMeterHeight(
		// )/2) ) );
		// world.createJoint( rfd );

		// TiledPlatform dynPlatTest = platBuilder.position( -500,200 ).dynamic(
		// ).dimensions( 5, 1 ).buildTilePlatform( );
		// s.addDynamicPlatformFixed( dynPlatTest );
		// dynPlatTest.quickfixCollisions( );

		TiledPlatform test = platBuilder.name( "movetest" ).kinematic( )
				.position( -300, 300 ).dimensions( 5, 1 ).oneSided( false )
				.buildTilePlatform( );
		rootSkeleton.addKinematicPlatform( test );

		PuzzleScrew puzzleScrew = new PuzzleScrew( "006", new Vector2( -300,
				400 ), 100, world, 0, false );
		rootSkeleton.addScrewForDraw( puzzleScrew );
		puzzleScrew.addStructureJoint( test );

		LerpMover lm = new LerpMover( test.getPositionPixel( ),
				new Vector2( test.getPositionPixel( ).x, test
						.getPositionPixel( ).y - 300f ),
				LinearAxis.VERTICAL );

		puzzleScrew.puzzleManager.addEntity( test );
		puzzleScrew.puzzleManager.addMover( lm );
		rootSkeleton.addScrewForDraw( puzzleScrew );

	}

	@SuppressWarnings( "unused" )
	void buildPolySprite( ) {
		Array< Vector2 > verts = new Array< Vector2 >( );
		verts.add( new Vector2( 0, 0 ) );
		verts.add( new Vector2( 1, 0 ) );
		verts.add( new Vector2( 0, 1 ) );
		verts.add( new Vector2( -1, .25f ) );

		PolySprite polySprite = new PolySprite( WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( )
						+ "/common/robot/alphabot_texture_skin.png",
				Texture.class ), verts );
	}

	@SuppressWarnings( "unused" )
	private void buildOptimizeSkele( ) {
		Skeleton skeleton;// = new Skeleton( "dynamicSkeleton", new Vector2(
							// 0,
		// 200 ), testTexture, world );
		skeleton = new SkeletonBuilder( world )
				.position( -2000, 0 )
				.texBackground(
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/robot/alphabot_texture_skin.png",
								Texture.class ) )
				.name( "dynamicSkeleton" )
				.vert( -100, -100 )
				.vert( 100, -100 )
				.vert( 100, 100 )
				.vert( -100, 100 )
				.texForeground(
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/robot/alphabot_texture_tux.png",
								Texture.class ) ).fg( ).vert( 200, 0 )
				.vert( 300, 100 ).vert( 200, 200 ).hasDeactiveTrigger( true )
				.build( );
		// dynSkeleton.body.createFixture( , density )

		oldRootSkeleton.addSkeleton( skeleton );

	}

	/**
	 * Initializes camera settings
	 */
	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( new Vector2( Gdx.graphics.getWidth( ) * 5f,
				Gdx.graphics.getHeight( ) * 5f ), width, height, world );
	}

	/**
	 * Initializes tiled platforms' settings, and adds them to the skeleton
	 */
	private void initTiledPlatforms( ) {

		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 )
				// .texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0.0f )
				.buildTilePlatform( );

		ground.setCategoryMask( Util.KINEMATIC_OBJECTS,
				Util.CATEGORY_EVERYTHING );
		ground.body.getFixtureList( ).get( 0 ).getShape( ).setRadius( 0 );
		skeleton.addKinematicPlatform( ground );
		ground.setCrushing( true );

	}

	/**
	 * Initializes settings for puzzle screws
	 * 
	 * NECESSARY EXAMPLE OF PUZZLE SCREWS, DONT GET RID OF THIS FUNC
	 */
	@SuppressWarnings( "unused" )
	private void initPuzzleScrews( ) {
		// two fliping platforms
		TiledPlatform flipPlat1 = platBuilder.position( 0.0f, 370f )
				.dimensions( 5, 1 )
				// .texture( testTexture )
				.name( "001_flip1" ).restitution( 0.0f ).kinematic( )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform( flipPlat1 );

		// two fliping platforms
		TiledPlatform flipPlat2 = platBuilder.position( 200.0f, 475f )
				.dimensions( 5, 2 )
				// .texture( testTexture )
				.name( "001_flip2" ).restitution( 0.0f ).kinematic( )
				.buildTilePlatform( );
		flipPlat2.setLocalRot( -90 * Util.DEG_TO_RAD );
		skeleton.addKinematicPlatform( flipPlat2 );

		// rotate puzzle screw control
		RotateByDegree rm = new RotateByDegree( 0.0f, -90.0f, 0, 0.5f );
		PuzzleScrew puzzleScrew = new PuzzleScrew( "001",
				new Vector2( 32f, 32f ), 50, skeleton, world, 0, false );
		flipPlat1.setActive( true );
		puzzleScrew.puzzleManager.addEntity( flipPlat1 );
		puzzleScrew.puzzleManager.addMover( rm );

		// also add a up mover to movingTP
		LerpMover lm2 = new LerpMover( movingTP.body.getPosition( ).mul(
				Util.BOX_TO_PIXEL ),
				new Vector2( movingTP.body.getPosition( ).x, movingTP.body
						.getPosition( ).y + 0.3f ).mul( Util.BOX_TO_PIXEL ),
				0.001f, false, LinearAxis.VERTICAL, 0 );
		movingTP.setActive( true );
		puzzleScrew.puzzleManager.addEntity( movingTP );
		puzzleScrew.puzzleManager.addMover( lm2 );

		rm = new RotateByDegree( -90.0f, 0.0f, 0, 0.5f );
		PuzzlePistonTweenMover pptm = new PuzzlePistonTweenMover( flipPlat2,
				new Vector2( 0, 100 ), 1, 1, 0, 0 );
		flipPlat2.setActive( true );
		puzzleScrew.puzzleManager.addEntity( flipPlat2 );
		puzzleScrew.puzzleManager.addMover( pptm );
		skeleton.addScrewForDraw( puzzleScrew );

		// lerp puzzle screw control
		PuzzleScrew puzzleScrew2 = new PuzzleScrew( "002", new Vector2( 150f,
				32f ), 50, skeleton, world, 0, false );
		LerpMover lm = new LerpMover( movingTP.body.getPosition( ).mul(
				Util.BOX_TO_PIXEL ), new Vector2(
				movingTP.body.getPosition( ).x + 1.75f,
				movingTP.body.getPosition( ).y ).mul( Util.BOX_TO_PIXEL ),
				LinearAxis.HORIZONTAL );
		movingTP.setActive( true );
		puzzleScrew2.puzzleManager.addEntity( movingTP );
		puzzleScrew2.puzzleManager.addMover( lm );
		skeleton.addScrewForDraw( puzzleScrew2 );

	}

	/**
	 * Initializes settings for moving platforms and adds them to the skeleton
	 */
	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = platBuilder.name( "othe5" ).width( 10 )
				.height( 1 ).oneSided( true ).position( -1000, 200 )// .texture(
																	// testTexture
																	// )
				.friction( 1f ).dynamic( ).buildTilePlatform( );

		PrismaticJointDef prismaticJointDef = JointFactory
				.constructSlidingJointDef( skeleton.body, slidingPlatform.body,
						slidingPlatform.body.getWorldCenter( ), new Vector2( 1,
								0 ), 1.0f, 1f );
		PrismaticJoint j = ( PrismaticJoint ) world
				.createJoint( prismaticJointDef );
		slidingPlatform.setActive( true );
		slidingPlatform.addMover( new SlidingMotorMover(
				PuzzleType.PRISMATIC_SLIDER, j ), RobotState.IDLE );
		skeleton.addDynamicPlatform( slidingPlatform );

		TiledPlatform skeletonTest1 = platBuilder.name( "othe6" ).width( 10 )
				.height( 1 ).friction( 1f ).oneSided( false )
				.position( 500, 250 )
				// .texture( testTexture )
				// .name( "yea!" )
				.kinematic( ).buildTilePlatform( );
		skeleton.addKinematicPlatform( skeletonTest1 );
		// Gdx.app.log( "name:", skeletonTest1.name );

		TiledPlatform pathPlatform = platBuilder.dimensions( 4, 1 )
				.position( 1600, 100 ).friction( 1f ).kinematic( )
				.name( "othe7" ).buildTilePlatform( );
		skeleton.addKinematicPlatform( pathPlatform );
		// build path. TODO: make building paths easier!!
		PathBuilder pb = new PathBuilder( );
		pathPlatform.setActive( true );
		pathPlatform.addMover( pb.begin( pathPlatform ).target( 300, 0, 5 )
				.target( 300, 300, 5 ).target( 0, 300, 5 ).target( 0, 0, 5 )
				.build( ), RobotState.IDLE );

		platBuilder.reset( ).world( world );

		// for building dynamic pistons (they don't work very well)
		PlatformBuilder builder = platBuilder.width( 1 ).height( 3 )
				.name( "othe8" ).oneSided( false ).dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				// .texture( testTexture )
				.friction( 1f );

		// BUILD ROW OF PISTONS with new kinematic way
		builder = platBuilder.width( 1 ).height( 3 ).oneSided( false )
				.kinematic( ).setScale( 1 ).name( "othe9" )// .texture(
															// testTexture )
				.friction( 1f );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform pistonKin = builder.name( "pistonKin" + i )
					.position( -200f - i * 40, 500f ).buildTilePlatform( );
			skeleton.addKinematicPlatform( pistonKin );
			pistonKin.setActive( true );
			pistonKin.addMover( new PistonTweenMover( pistonKin, new Vector2(
					0, 300 ), 1f, 3f, 1f, 0f, i / 10.0f + 1 ), RobotState.IDLE );
		}

		builder = platBuilder.width( 20 ).height( 1 ).oneSided( true )
				.name( "othe0" ).dynamic( )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				// .texture( testTexture )
				.friction( 1f );

	}

	public void initPulley( ) {
		TiledPlatform singTile = platBuilder.position( -1200.0f, 400.0f )
				.dimensions( 1, 1 )
				// .texture( testTexture )
				.dynamic( ).name( "Single Tiled" ).restitution( 0.0f )
				.buildTilePlatform( );
		skeleton.addPlatform( singTile );
		singTile.body.setFixedRotation( false );

		TiledPlatform singTile2 = platBuilder.position( -1300.0f, 400.0f )
				.dimensions( 1, 1 )
				// .texture( testTexture )
				.dynamic( ).name( "Single Tiled" ).restitution( 0.0f )
				.buildTilePlatform( );
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

		skeleton.addStrippedScrew( new StrippedScrew( "", new Vector2(
				singTile.body.getPosition( ).x * Util.BOX_TO_PIXEL,
				singTile.body.getPosition( ).y * Util.BOX_TO_PIXEL ), singTile,
				world ) );

		skeleton.addStrippedScrew( new StrippedScrew( "", new Vector2(
				singTile2.body.getPosition( ).x * Util.BOX_TO_PIXEL,
				singTile2.body.getPosition( ).y * Util.BOX_TO_PIXEL ),
				singTile2, world ) );
	}

	private void initCheckPoints( ) {
		progressManager = new ProgressManager( player1, player2, world );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				-170f, 64f ), skeleton, world, progressManager,
				"levelStage_0_0" ) );
		progressManager
				.addCheckPoint( new CheckPoint( "check_01", new Vector2( 512,
						64 ), skeleton, world, progressManager,
						"levelStage_0_1" ) );
	}

	@Override
	public void render( float deltaTime ) {
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.2f, 0.2f, 0.2f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_1 ) ) {
			ScreenManager.getInstance( ).show( ScreenType.WIN );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest ) {
				debug = !debug;
			}
			debugTest = false;
		} else
			debugTest = true;

		if ( Gdx.input.isKeyPressed( Input.Keys.Z ) ) {
			if ( s != null )
				s.translateBy( 0.0f, 0.01f );
			// dynSkel2.body.applyLinearImpulse( new Vector2(0, .1f),
			// dynSkel2.body.getPosition( ));

		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			if ( s != null )
				s.translateBy( 0.0f, -0.01f );
			// dynSkel2.body.applyLinearImpulse( new Vector2(0, -.1f),
			// dynSkel2.body.getPosition( ));
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			if ( s != null )
				s.rotateBy( -0.01f );
			// dynSkel2.body.applyAngularImpulse( 0.1f ) ;
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			if ( s != null )
				s.rotateBy( 0.01f );
			// dynSkel2.body.applyAngularImpulse( -0.1f ) ;
		}

		player1.update( deltaTime );
		player2.update( deltaTime );
		rootSkeleton.update( deltaTime );
		if ( progressManager != null )
			progressManager.update( deltaTime );
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		if ( progressManager != null )
			progressManager.draw( batch, deltaTime );
		rootSkeleton.draw( batch, deltaTime );
		player1.draw( batch, deltaTime );
		player2.draw( batch, deltaTime );

		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 3 );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			if ( !ScreenManager.escapeHeld ) {
				ScreenManager.getInstance( ).show( ScreenType.PAUSE );
			}
		} else
			ScreenManager.escapeHeld = false;

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