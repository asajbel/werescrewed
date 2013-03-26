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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzlePistonTweenMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.particles.Steam;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.util.Util;

public class PhysicsTestScreen implements com.badlogic.gdx.Screen {


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

		// entityManager = new EntityManager( );
		skeleton = new Skeleton( "skeleton", new Vector2( 500, 0 ), null, world );
		// skeleton.body.setType( BodyType.DynamicBody );
		//oldRootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );

		platBuilder = new PlatformBuilder( world );

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


		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 400.0f, 100f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 300f, 100f ).buildPlayer( );

		initCheckPoints( );


		
		rootSkeleton = new RootSkeleton( "Root Skeleton", new Vector2( 0, 0 ),
				null, world );

		rootSkeleton.addSkeleton( skeleton );

				
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );

		//debugRenderer.setDrawJoints( false );
		
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		connectedRoom();
		movingSkeleton();
	}

	//This is how you make a whole room fall, by welding everything together
	void connectedRoom(){
		test2 = platBuilder.name( "strucTest9" ).kinematic( )
				.position( 800, 100 ).dimensions( 5, 5).oneSided( false )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform(  test2 );

		
		
		StrippedScrew strScrew2 = new StrippedScrew( "strScrew4", new Vector2(500, 500 ), rootSkeleton, 
				world );
		rootSkeleton.addStrippedScrew( strScrew2 );
		
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( rootSkeleton.body, strScrew2.body,
				rootSkeleton.body.getPosition( ) );
		world.createJoint( revoluteJointDef );
		
		StrippedScrew strScrew4 = new StrippedScrew( "strScrew5", new Vector2(500, 700 ), rootSkeleton, 
				world );
		rootSkeleton.addStrippedScrew( strScrew4 );
		RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef( );
		revoluteJointDef2.initialize( rootSkeleton.body, strScrew4.body,
				rootSkeleton.body.getPosition( ) );
		world.createJoint( revoluteJointDef2 );
		
		dynSkel2 = new SkeletonBuilder( world ).position( 800, 500 )
				.build( );
		dynSkel2.body.setType( BodyType.DynamicBody );
		dynSkel2.body.setGravityScale( 0.1f );
		dynSkel2.setDensity( 100f );
		rootSkeleton.addSkeleton( dynSkel2 );
		
		StrippedScrew strScrew = new StrippedScrew( "strScrew3", new Vector2(700, 500 ), dynSkel2, 
				world );
		dynSkel2.addStrippedScrew( strScrew );
		strScrew.body.setFixedRotation( false );
		
//		WeldJointDef s1 = new WeldJointDef();
//		s1.initialize( strScrew.body, dynSkel2.body, strScrew.getPosition( ) );
//		world.createJoint( s1 );
		
//		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
//		revoluteJointDef.initialize( strScrew.body, dynSkel2.body, strScrew.getPosition( ) );
//		world.createJoint( revoluteJointDef );

		
		TiledPlatform plat6 = platBuilder.name( "weld1" ).dynamic( )
				.position( 600, 600 ).dimensions( 1, 6).oneSided( false )
				.buildTilePlatform( );
		plat6.body.setFixedRotation( false );
		plat6.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat6 );
		
		WeldJointDef s2 = new WeldJointDef();
		s2.initialize( strScrew.body, plat6.body, plat6.getPosition( ) );
		world.createJoint( s2 );
		
//		RevoluteJointDef r2 = new RevoluteJointDef( );
//		r2.initialize( strScrew.body, plat6.body, plat6.getPosition( ) );
//		world.createJoint( r2 );
		
		TiledPlatform plat7 = platBuilder.name( "weld2" ).dynamic( )
				.position( 800, 300 ).dimensions( 12, 1).oneSided( false )
				.buildTilePlatform( );
		plat7.body.setFixedRotation( false );
		plat7.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat7 );
		
		TiledPlatform plat8 = platBuilder.name( "weld3" ).dynamic( )
				.position( 800, 700 ).dimensions( 12, 1).oneSided( false )
				.buildTilePlatform( );
		plat8.body.setFixedRotation( false );
		plat8.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat8 );
		
		TiledPlatform test = platBuilder.name( "strucTest" ).kinematic( )
				.position( 800, 900 ).dimensions( 1, 5).oneSided( false )
				.buildTilePlatform( );
		skeleton.addKinematicPlatform(  test );
		
		StructureScrew s = new StructureScrew( "ss", new Vector2(800, 750), 100, test,
				world );
		s.addStructureJoint( plat8 );
		dynSkel2.addScrewForDraw( s );
		
		
		TiledPlatform plat9 = platBuilder.name( "weld4" ).dynamic( )
				.position( 1000, 500 ).dimensions( 1, 12).oneSided( false )
				.buildTilePlatform( );
		plat9.body.setFixedRotation( false );
		plat9.quickfixCollisions( );
		dynSkel2.addDynamicPlatform( plat9 );
		
		WeldJointDef wj1 = new WeldJointDef();
		wj1.initialize( plat6.body, plat7.body, plat6.getPosition( ) );
		world.createJoint( wj1 );
		
		WeldJointDef wj2 = new WeldJointDef();
		wj2.initialize( plat7.body, plat9.body, plat7.getPosition( ) );
		world.createJoint( wj2 );
		
		WeldJointDef wj3 = new WeldJointDef();
		wj3.initialize( plat6.body, plat8.body, plat8.getPosition( ) );
		world.createJoint( wj3 );
		
		WeldJointDef wj8 = new WeldJointDef();
		wj8.initialize( plat9.body, plat8.body, plat9.getPosition( ) );
		world.createJoint( wj8 );
		
		WeldJointDef wj4 = new WeldJointDef();
		wj4.initialize( dynSkel2.body, plat9.body, dynSkel2.getPosition( ) );
		world.createJoint( wj4 );
		
		WeldJointDef wj5 = new WeldJointDef();
		wj5.initialize( dynSkel2.body, plat8.body, dynSkel2.getPosition( ) );
		world.createJoint( wj5 );
		
		WeldJointDef wj6 = new WeldJointDef();
		wj6.initialize( dynSkel2.body, plat7.body, dynSkel2.getPosition( ) );
		world.createJoint( wj6 );
		
		WeldJointDef wj7 = new WeldJointDef();
		wj7.initialize( dynSkel2.body, plat6.body, dynSkel2.getPosition( ) );
		world.createJoint( wj7 );

	}
	
	void movingSkeleton(){
		s =  new Skeleton( "skeleton7", new Vector2( -500, 200 ), null, world );
		rootSkeleton.addSkeleton( s );
//		PathBuilder pb = new PathBuilder();
//		s.addMover( pb.begin( s ).target( 100, 0, 1 )
//				.target( 100, 100, 1 )
//				.target( 100, 0, 1 )
//				.target( 0, 0, 1 ).build( ), RobotState.IDLE);
		
		TiledPlatform test = platBuilder.name( "movetest" ).kinematic( )
				.position( -300, 300 ).dimensions( 1, 5).oneSided( false )
				.buildTilePlatform( );
		s.addKinematicPlatform(  test );
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
		skeleton = new SkeletonBuilder( world ).position( -2000, 0 )
				.texBackground( WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_skin.png",Texture.class )).name( "dynamicSkeleton" )
				.vert( -100, -100 ).vert( 100, -100 ).vert( 100, 100 ).vert( -100,100 )
				.texForeground( WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_tux.png",Texture.class ))
				.fg( ).vert( 200,0 ).vert( 300,100 ).vert( 200,200 ).hasDeactiveTrigger( true )
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
		cam = new Camera( width, height, world );
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
		// THIS SHOULD BE SET IN EVERYTHING START USING THEM
		// AND THINGS WILL STOP FALLING THROUGH OTHER THINGS
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS,
				Util.CATEGORY_EVERYTHING );
		skeleton.addKinematicPlatform( ground );

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

		// 1000 - 1219 for perfect gears
		Platform gear = builder.name( "gear" ).position( -1800, 320 )
				.texture( null ).setScale( 3f ).type( "gearSmall" )
				.buildComplexPlatform( );
		skeleton.addPlatformRotatingCenterWithMot( gear, 1f );
		Platform gear2 = builder.name( "gear2" ).position( -1500, 300 )
				.texture( null ).setScale( 3f ).type( "gearSmall" )
				.buildComplexPlatform( );
		skeleton.addPlatformRotatingCenter( gear2 );
		gear2.quickfixCollisions( );
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
		progressManager
				.addCheckPoint( new CheckPoint( "check_01", new Vector2( 0f,
						64f ), skeleton, world, progressManager,
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
			s.translateBy( 0.0f, 0.01f );
//			dynSkel2.body.applyLinearImpulse( new Vector2(0, .1f),
//					dynSkel2.body.getPosition( ));
			
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			s.translateBy( 0.0f, -0.01f );
//			dynSkel2.body.applyLinearImpulse( new Vector2(0, -.1f),
//					dynSkel2.body.getPosition( ));
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			s.rotateBy( -0.01f );
			//dynSkel2.body.applyAngularImpulse(  0.1f ) ;
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			s.rotateBy( 0.01f );
			//dynSkel2.body.applyAngularImpulse(  -0.1f ) ;
		}


		player1.update( deltaTime );
		player2.update( deltaTime );
		rootSkeleton.update( deltaTime );
		progressManager.update( deltaTime );
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );


		progressManager.draw( batch );
		rootSkeleton.draw( batch );
		player1.draw( batch );
		player2.draw( batch );

		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 3 );
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