package com.blindtigergames.werescrewed.screens;

import java.util.Iterator;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.CannonLaunchAction;
import com.blindtigergames.werescrewed.entity.action.SetRobotStateAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Enemy;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.hazard.MouthFire;
import com.blindtigergames.werescrewed.entity.hazard.Spikes;
import com.blindtigergames.werescrewed.entity.hazard.builders.HazardBuilder;
import com.blindtigergames.werescrewed.entity.mover.DirectionFlipMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateByDegree;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzlePistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.particles.EntityParticleEmitter;
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.util.Util;

public class PhysicsTestScreen extends Screen {

	public ScreenType screenType;
	private Camera cam;
	private World world;
	private ProgressManager progressManager;
	@SuppressWarnings( "unused" )
	private TiledPlatform tiledPlat, ground, movingTP, singTile, rectile,
			ropePlatform;
	private PlatformBuilder platBuilder;
	private Skeleton skeleton;
	private Skeleton oldRootSkeleton;
	RootSkeleton rootSkeleton;
	public Steam testSteam;
	public SpriteBatch particleBatch;
	TiledPlatform test2;
	
	EntityParticleEmitter fireballEmitter;

	private Skeleton dynSkel2;
	private Skeleton s;
	
	private TextureAtlas dragonParts; 

	StructureScrew limit;
	
	MouthFire mouthFire;

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public PhysicsTestScreen( ) {
		super( );
		// Initialize world and variables to allow adding entities
		level = new Level( );
		world = level.world;

		super.setClearColor( 51, 102, 102, 255 );

		// Initialize camera
		initCamera( );
		level.camera = cam;

		skeleton = new Skeleton( "skeleton", new Vector2( 500, 0 ), null,
				world, BodyType.KinematicBody );
		skeleton.setFgFade( false );
		platBuilder = new PlatformBuilder( world );
		dragonParts = new TextureAtlas("data/levels/dragon/dragon_objects.pack"); 

		// Uncomment for test anchor
		// anchor = new Anchor( new Vector2( 7 * Util.BOX_TO_PIXEL,
		// Util.BOX_TO_PIXEL ), world, 5f );
		// anchor.deactivate( );
		// AnchorList.getInstance( ).addAnchor( anchor );

		// Initialize ground platformbb

		level.player1 = new PlayerBuilder( ).name( "player1" )
				.definition( "red_male" ).world( world )
				.position( 1400.0f, 100f ).buildPlayer( );
		level.player2 = new PlayerBuilder( ).name( "player2" )
				.definition( "red_female" ).world( world )
				.position( 1400f, 100f ).buildPlayer( );

		level.root = new SkeletonBuilder( level.world ).buildRoot( );
		rootSkeleton = level.root;

		rootSkeleton.addSkeleton( skeleton );
		rootSkeleton.setFgFade( false );
		// debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		initCheckPoints( );
		initGround( );
		// connectedRoom( );
		movingSkeleton( );

		//buildCannon( new Vector2( 1600, 50 ), 160, 350 );
		
		//buildCannon( new Vector2( 1900, 30 ), 200, 200 );
		
		//buildCannon( new Vector2( -1900, 30 ), 200, 200 );

		PowerSwitch pswitch = new PowerSwitch( "pwsstsf",
				new Vector2( 512, 200 ), world );
		rootSkeleton.addEventTrigger( pswitch );

		//createFire( );
		initFireballEnemy(new Vector2(1600,200));
		
		initEyebrow(new Vector2(0,0));
		
	
		mouthFire = new MouthFire( "mouth-fire", new Vector2(0,0), 3, world );
		skeleton.addHazard( mouthFire );
	}

	// width & height in pixels
	// pos is position of bottom axis
	void buildCannon( Vector2 pos, int widthPix, int heightPix ) {
		if ( widthPix <= 64 )
			throw new RuntimeException(
					"Cannon width needs to be greater than 64 (2tiles) to work properly" );
		PlatformBuilder pb = new PlatformBuilder( world )
				.tileSet( "TilesetTest" );
		SkeletonBuilder sb = new SkeletonBuilder( world );

		Vector2 dim = new Vector2( ( ( int ) ( widthPix / 32 ) ) - 2,
				( ( int ) ( heightPix / 32 ) ) );
		Vector2 left = new Vector2( pos.x - dim.x / 2 * 32 - 16, pos.y - 16
				+ dim.y * 16 );
		Vector2 right = new Vector2( pos.x + dim.x / 2 * 32 + 16, pos.y - 16
				+ dim.y * 16 );

		Skeleton s = sb.position( pos.cpy( ) ).build( );
		skeleton.addSkeleton( s );
		s.setFgFade( false );
		Sprite can = dragonParts.createSprite( "cannon-small" );
		can.setOrigin( can.getWidth( )/2, can.getHeight( )/2 ); 
		s.addFGDecal( can, new Vector2(-can.getWidth( ),-can.getHeight( )*2/3) );
		addFGEntityToBack(s); 

		// base
		s.addPlatform( pb.name( "cannon-base" ).dimensions( dim.x, 1 )
				.position( pos.cpy( ) ).buildTilePlatform( ) );
		// left
		s.addPlatform( pb.name( "cannon-left" ).dimensions( 1, dim.y )
				.position( left.cpy( ) ).buildTilePlatform( ) );
		// right
		s.addPlatform( pb.name( "cannon-right" ).dimensions( 1, dim.y )
				.position( right.cpy( ) ).buildTilePlatform( ) );

		EventTriggerBuilder etb = new EventTriggerBuilder( world );

		int quarter = ( int ) ( dim.y * 32 / 4 );
		Vector2 eventPos = new Vector2( pos.x, pos.y + 16 + quarter );

		Array< Vector2 > triggerVerts = new Array< Vector2 >( 4 );
		// triggerVerts.add( new Vector2 )

		triggerVerts.add( new Vector2( quarter, -quarter ) );
		triggerVerts.add( new Vector2( quarter, quarter ) );
		triggerVerts.add( new Vector2( -quarter, quarter ) );
		triggerVerts.add( new Vector2( -quarter, -quarter ) );
		triggerVerts.add( new Vector2( -quarter, -quarter ) );

		s.setLocalRot( -Util.PI / 7 );
		EventTrigger et = etb.name( "cannon-trigger" ).setVerts( triggerVerts )
				.extraBorder( 0 ).position( eventPos )
				// .addEntity( s )
				.beginAction( new CannonLaunchAction( s, .3f, 1 ) )
				.repeatable( ).build( );
		s.addEventTrigger( et );

	}
	
	

	// This is how you make a whole room fall, by welding everything together
	void connectedRoom( ) {

		StrippedScrew strScrew2 = new StrippedScrew( "strScrew4", new Vector2(
				500, 500 ), rootSkeleton, world, Vector2.Zero );
		rootSkeleton.addStrippedScrew( strScrew2 );

		StrippedScrew strScrew6 = new StrippedScrew( "strScrew422",
				new Vector2( 2000, 300 ), rootSkeleton, world, Vector2.Zero );
		rootSkeleton.addStrippedScrew( strScrew6 );
		StrippedScrew strScrew5 = new StrippedScrew( "strScrew442",
				new Vector2( 2200, 100 ), rootSkeleton, world, Vector2.Zero );
		rootSkeleton.addStrippedScrew( strScrew5 );

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( rootSkeleton.body, strScrew2.body,
				rootSkeleton.body.getPosition( ) );
		world.createJoint( revoluteJointDef );

		StrippedScrew strScrew4 = new StrippedScrew( "strScrew5", new Vector2(
				500, 700 ), rootSkeleton, world, Vector2.Zero );
		rootSkeleton.addStrippedScrew( strScrew4 );
		RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef( );
		revoluteJointDef2.initialize( rootSkeleton.body, strScrew4.body,
				rootSkeleton.body.getPosition( ) );
		world.createJoint( revoluteJointDef2 );

		dynSkel2 = new SkeletonBuilder( world ).position( 800, 500 ).build( );
		dynSkel2.body.setType( BodyType.DynamicBody );
		dynSkel2.body.setGravityScale( 0.1f );
		dynSkel2.setDensity( 100f );
		dynSkel2.setFgFade( false );
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
		dynSkel2.setFgFade( false );

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
				.add( plat8.getPixelWidth( ) / 2, 0 ), 100, world, Vector2.Zero );
		s.addWeldJoint( plat8 );
		s.addWeldJoint( plat9 );
		dynSkel2.addScrewForDraw( s );

		StructureScrew s1 = new StructureScrew( "s1", plat7.getPositionPixel( )
				.add( plat7.getPixelWidth( ) / 2, 0 ), 100, world, Vector2.Zero );
		s1.addWeldJoint( plat7 );
		s1.addWeldJoint( plat9 );
		dynSkel2.addScrewForDraw( s1 );

		StructureScrew s3 = new StructureScrew( "s2", plat8.getPositionPixel( )
				.sub( plat8.getPixelWidth( ) / 2, 0 ), 100, world, Vector2.Zero );
		s3.addWeldJoint( plat6 );
		s3.addWeldJoint( plat8 );
		dynSkel2.addScrewForDraw( s3 );

		StructureScrew s5 = new StructureScrew( "s23", plat7.getPositionPixel( )
				.sub( plat7.getPixelWidth( ) / 2, 0 ), 100, world, Vector2.Zero );
		s5.addWeldJoint( plat6 );
		s5.addWeldJoint( plat7 );
		dynSkel2.addScrewForDraw( s5 );

		TiledPlatform box = platBuilder.name( "box" ).dynamic( )
				.position( 2500, 250 ).dimensions( 3, 3 ).oneSided( false )
				.buildTilePlatform( );
		box.body.setFixedRotation( false );
		box.quickfixCollisions( );
		rootSkeleton.addDynamicPlatform( box );

		limit = new StructureScrew( "box", new Vector2( 2700, 250 ), 100,
				world, Vector2.Zero );
		limit.addStructureJoint( box, 45f );
		limit.addStructureJoint( rootSkeleton );
		rootSkeleton.addScrewForDraw( limit );

	}

	void movingSkeleton( ) {

		Skeleton top = new Skeleton( "skeleton7", new Vector2( -700, 1200 ),
				null, world, BodyType.KinematicBody );
		// top.addMover( new RotateTweenMover( top, 3f, -Util.PI / 2, 1f, true
		// ),
		// RobotState.IDLE );
		rootSkeleton.addSkeleton( top );

		s = new Skeleton( "skeleton7", new Vector2( -700, 700 ), null, world,
				BodyType.KinematicBody );
		s.setFgFade( false );
		TiledPlatform ttt = platBuilder.name( "ttt" ).kinematic( )
				.position( -700, 1000 ).dimensions( 1, 5 ).oneSided( false )
				.buildTilePlatform( );
		s.addPlatform( ttt );

		// rootSkeleton.addSkeleton( s );

		top.addSkeleton( s );
		top.setFgFade( false );
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
		s.addSkeleton( middleHang );
		middleHang.setFgFade( false );
		// TiledPlatform test2 = platBuilder.name( "movetest2" ).kinematic( )
		// .position( -900, 500 ).dimensions( 1, 5).oneSided( false )
		// .buildTilePlatform( );
		// middleHang.addKinematicPlatform( test2 );

		// RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef( );
		// revoluteJointDef2.initialize( s.body, middleHang.body, s.getPosition(
		// ) );
		// world.createJoint( revoluteJointDef2 );

		@SuppressWarnings( "unused" )
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

		TiledPlatform piston = platBuilder.name( "piston" ).kinematic( )
				.position( 2700, 100 ).dimensions( 7, 2 ).oneSided( false )
				.buildTilePlatform( );
		piston.addMover( new PistonTweenMover( piston, new Vector2( 0, 500 ),
				0.5f, 3f, 1f, 0f, 1f ), RobotState.IDLE );
		s.addKinematicPlatform( piston );
		piston.setCrushing( true );

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

		TiledPlatform test2 = platBuilder.name( "movetest2" ).kinematic( )
				.position( -100, 300 ).dimensions( 5, 1 ).oneSided( false )
				.buildTilePlatform( );
		rootSkeleton.addKinematicPlatform( test2 );

		PuzzleScrew puzzleScrew = new PuzzleScrew( "006", new Vector2( -300,
				100 ), 100, world, 0, false );
		rootSkeleton.addScrewForDraw( puzzleScrew );
		puzzleScrew.addStructureJoint( rootSkeleton );

		// LerpMover lm = new LerpMover(test.getPositionPixel( ),
		// new Vector2( test.getPositionPixel( ).x, test
		// .getPositionPixel( ).y - 300f ),
		// LinearAxis.VERTICAL );

		PuzzleRotateTweenMover ptm = new PuzzleRotateTweenMover( 1,
				Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );

		PuzzleRotateTweenMover ptm2 = new PuzzleRotateTweenMover( 1,
				Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );

		puzzleScrew.puzzleManager.addEntity( test );
		puzzleScrew.puzzleManager.addEntity( test2 );
		puzzleScrew.puzzleManager.addMover( ptm );
		puzzleScrew.puzzleManager.addMover( ptm2 );
		rootSkeleton.addScrewForDraw( puzzleScrew );

		TiledPlatform spikePlat = platBuilder.name( "spikePlat" ).dynamic( )
				.position( 1100, 200 ).dimensions( 6, 1 ).oneSided( false )
				.buildTilePlatform( );
		rootSkeleton.addDynamicPlatform( spikePlat );
		spikePlat.addJointToSkeleton( rootSkeleton );

		 HazardBuilder spikesBuilder = new HazardBuilder( world );
		 Spikes spikes = spikesBuilder.position( 800.0f, 550f ).dimensions( 6,
		 1 )
		 .up( ).active( ).buildSpikes( );
		//
		//
		// spikes.body.setType( BodyType.DynamicBody );
		// spikes.body.setFixedRotation( false );
		 rootSkeleton.addHazard( spikes );
		//
		// RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		// revoluteJointDef.initialize( spikes.body, spikePlat.body,
		// spikePlat.getPosition( ) );
		// revoluteJointDef.enableMotor = true;
		// revoluteJointDef.maxMotorTorque = 1.0f;
		// revoluteJointDef.motorSpeed = 0.0f;
		// Joint screwJoint = (Joint) world.createJoint( revoluteJointDef );

		TiledPlatform struc1 = platBuilder.name( "stuc1" ).dynamic( )
				.position( 500.0f, 450f ).dimensions( 30, 1 ).oneSided( false )
				.buildTilePlatform( );
		rootSkeleton.addDynamicPlatform( struc1 );

		StructureScrew s = new StructureScrew( "ss", struc1.getPositionPixel( )
				.add( struc1.getPixelWidth( ) / 2, 100 ), 100, world,
				Vector2.Zero );
		s.addStructureJoint( struc1 );
		s.addStructureJoint( rootSkeleton );
		rootSkeleton.addScrewForDraw( s );

		StructureScrew s2 = new StructureScrew( "ss2", struc1
				.getPositionPixel( ).sub( struc1.getPixelWidth( ) / 2, -100 ),
				100, world, Vector2.Zero );
		s2.addStructureJoint( struc1 );
		s2.addStructureJoint( rootSkeleton );
		rootSkeleton.addScrewForDraw( s2 );

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
		skeleton.setFgFade( false );
		// dynSkeleton.body.createFixture( , density )

		oldRootSkeleton.addSkeleton( skeleton );
		oldRootSkeleton.setFgFade( false );
	}

	/**
	 * Initializes camera settings
	 */
	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( new Vector2( 1400.0f, 100f ), width, height, world );
	}

	/**
	 * Initializes tiled platforms' settings, and adds them to the skeleton
	 */
	private void initGround( ) {

		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 )
				// .texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0.0f )
				.buildTilePlatform( );

		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		ground.body.getFixtureList( ).get( 0 ).getShape( ).setRadius( 0 );
		skeleton.addKinematicPlatform( ground );
		ground.setCrushing( true );

		//build a little walls at edge
		TiledPlatform wall = platBuilder.position( ground.getPixelWidth( )/2f,0f ).name("wall1")
				.dimensions( 1,3 ).kinematic( ).buildTilePlatform( );
		skeleton.addKinematicPlatform( wall );
		wall = platBuilder.position( -ground.getPixelWidth( )/2f,0 ).name("wall2").buildTilePlatform( );
		skeleton.addKinematicPlatform( wall );
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
				new Vector2( 32f, 32f ), 50, skeleton, world, 0, false,
				Vector2.Zero );
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
				32f ), 50, skeleton, world, 0, false, Vector2.Zero );
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
				world, Vector2.Zero ) );

		skeleton.addStrippedScrew( new StrippedScrew( "", new Vector2(
				singTile2.body.getPosition( ).x * Util.BOX_TO_PIXEL,
				singTile2.body.getPosition( ).y * Util.BOX_TO_PIXEL ),
				singTile2, world, Vector2.Zero ) );
	}

	private void initCheckPoints( ) {
		progressManager = level.progressManager = new ProgressManager(
				level.player1, level.player2, world );
		skeleton.addCheckPoint( new CheckPoint( "check_01", new Vector2( -170f,
				64f ), skeleton, world, progressManager, "levelStage_0_0" ) );
		skeleton.addCheckPoint( new CheckPoint( "check_02", new Vector2( 512,
				64 ), skeleton, world, progressManager, "levelStage_0_1" ) );
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

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
		
		
		if ( Gdx.input.isKeyPressed( Keys.T ) ) {
			ScreenManager.getInstance( ).show( ScreenType.TROPHY);
		}
		
		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			if ( !ScreenManager.escapeHeld ) {
				ScreenManager.getInstance( ).show( ScreenType.PAUSE );
			}
		} else
			ScreenManager.escapeHeld = false;
		
		if ( Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT ) && Gdx.input.isKeyPressed( Input.Keys.M ) ) {
			mouthFire.setActiveHazard( true );
		}

	}

	@Override
	public void dispose( ) {
		super.dispose( );
		resetPhysicsWorld( );
		// world.dispose( );
		// world = null;
	}

	public void resetPhysicsWorld( ) {
		world.clearForces( );

		for ( Iterator< Body > iter = world.getBodies( ); iter.hasNext( ); ) {
			Body body = iter.next( );
			if ( body != null )
				world.destroyBody( body );
		}
		for ( Iterator< Joint > iter = world.getJoints( ); iter.hasNext( ); ) {
			Joint joint = iter.next( );
			if ( joint != null )
				world.destroyJoint( joint );
		}

	}

	private void createFire( ) {
		Skeleton fireSkele = new SkeletonBuilder( world ).name( "fireSkeleton" )
				.position( 100, 200 ).build( );
		Fire f = new Fire( "fire", fireSkele.getPositionPixel( ).add( 0, 100 ),
				100, 200, world, true );
		fireSkele.addHazard( f );
		rootSkeleton.addSkeleton( fireSkele );
		fireSkele.setFgFade( false );
		fireSkele.setMoverAtCurrentState( new RotateTweenMover( fireSkele ) );

		Steam s = new Steam( "steam", fireSkele.getPositionPixel( ).add( 0,
				-100 ), 100, 100, world );
		fireSkele.addSteam( s );
	}
	
	private void initFireballEnemy(Vector2 pos){
		
		int w = 15, n= 10, h = 140;
		int cageWidth = 700, cageHeight = n*140;
		//build a little cage for the fireball
		TiledPlatform wall = platBuilder.position( pos.x+cageWidth/2,cageHeight/2 ).name("wall1")
				.dimensions( 1,60 ).kinematic( ).buildTilePlatform( );
		skeleton.addKinematicPlatform( wall );
		wall = platBuilder.position( pos.x-cageWidth/2,cageHeight/2 ).name("wall2").buildTilePlatform( );
		skeleton.addKinematicPlatform( wall );
		
		
		platBuilder.dimensions( 15, 1 ); //21
		for(int i = 1; i < n; ++i ){
			int x = i%2 == 0?0:230 + cageWidth/2;
			skeleton.addPlatform(platBuilder.position( pos.x-w*16+x, i*h ).buildTilePlatform( ));
		}
		
		fireballEmitter = new EntityParticleEmitter( "bolt emitter",
				new Vector2( pos.cpy( ).add(0,n*h) ),
				new Vector2(.1f,0),
				 world, true );
		for(int i =0; i < 5; ++i ){
			fireballEmitter.addParticle( createBoltEnemy( pos.cpy( ).add(0,n*h), i ), 10, 0, i*2 );
		}
		rootSkeleton.addLooseEntity( fireballEmitter );
	}
	
	Enemy createBoltEnemy(Vector2 pos, int index){
		Enemy hotbolt = new Enemy( "hot-bolt"+index, pos,25, world, true );
		hotbolt.addMover( new DirectionFlipMover( false, 0.001f, hotbolt, 1.5f, .03f ) );
		addBGEntity( hotbolt );
		return hotbolt;
	}
	
	void initEyebrow(Vector2 pos){
		TiledPlatform brow = platBuilder.name( "eyebrow" ).dimensions( 2,2 ).position( pos.cpy() ).buildTilePlatform( );
		skeleton.addPlatform( brow );
		brow.noCollide( );
		brow.setVisible( true );
		
		TextureAtlas browAtlas = new TextureAtlas(
				Gdx.files.internal( "data/levels/dragon/head_top_right.pack" ) );
		//At rest the eyebrow is unrotated at 0,0 local position.
		brow.addFGDecal( browAtlas.createSprite( "eyebrow" ));//, new Vector2(-393,-161) );
		addFGEntity( brow );
		
		//angry mover
		Timeline browSequence = Timeline.createSequence( );
		//begin the mover by moving it to the starting position quickly
		browSequence.beginParallel( );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, .5f )
				.target( 0,0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, .5f )
				.target( 0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.end( );
		
		browSequence.beginParallel( );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, .5f )
				.target( 100, -100f ).ease( TweenEquations.easeInOutQuad ).start( ) );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, .5f )
					.target( -Util.FOURTH_PI/2 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.end( );
		
		browSequence.beginParallel( );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 2f )
					.target( 110, -130 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 3, 0 ).start( ) );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 2f )
				.target( -Util.FOURTH_PI/2-Util.FOURTH_PI/6 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 3, 0 ).start( ) );
		browSequence.end( );
		
		
		browSequence.beginParallel( );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 1.5f )
					.target( 0,0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 1.5f )
					.target( 0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
			browSequence.end( );
		browSequence = browSequence.repeat( Tween.INFINITY, 0f );
		
		//brow.addMover( new TimelineTweenMover( angry.start( ) ) );
		brow.addMover( new TimelineTweenMover( browSequence.start( ) ), RobotState.HOSTILE );
		
		//IDLE sequence
		browSequence = Timeline.createSequence( );
		browSequence.beginParallel( );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, .5f )
				.target( 0,0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, .5f )
				.target( 0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.end( );
		
		browSequence.beginParallel( );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 5f )
			.target( 20, -20f ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 5, 0 ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 5f )
				.target( -Util.FOURTH_PI/6 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 5, 0 ).start( ) );
		browSequence.end( );
		browSequence = browSequence.repeat( Tween.INFINITY, 0f );
		
		brow.addMover( new TimelineTweenMover( browSequence.start( ) ), RobotState.IDLE );
		
		//((TimelineTweenMover)brow.currentMover( )).timeline.start( );
		
		RobotState[] states = {RobotState.IDLE,RobotState.HOSTILE};
		PowerSwitch pSwitch;
		for(int i=0;i<states.length;++i){
			pSwitch = new PowerSwitch( "switch"+i, new Vector2(1300+150*i,30), world );
			pSwitch.setBeginIAction( new SetRobotStateAction( states[i] ) );
			pSwitch.setRepeatable( true );
			pSwitch.setActingOnEntity( true );
			pSwitch.addEntityToTrigger( brow );
			skeleton.addEventTrigger( pSwitch );
		}
		
		
	}

}