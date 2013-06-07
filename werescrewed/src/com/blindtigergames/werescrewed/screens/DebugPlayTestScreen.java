package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.MetricsStartTimeAction;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.RopeBuilder;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.hazard.builders.HazardBuilder;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.mover.RotateByDegree;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.rope.Rope;
import com.blindtigergames.werescrewed.entity.screws.BossScrew;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.MetricsRender;
import com.blindtigergames.werescrewed.util.Util;

public class DebugPlayTestScreen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	private Camera cam;
	private MetricsRender metRend;
	private SpriteBatch batch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private ProgressManager progressManager;
	private Player player1, player2;
	private PlatformBuilder platBuilder;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private boolean debug = true;
	private boolean debugTest = true;
	private TiledPlatform wall;
	private TiledPlatform obst;
	private TiledPlatform plat;
	private Skeleton skel1;
	private TiledPlatform stair;
	private EventTrigger et;

	private static final float TILE = 32;
	private TiledPlatform step;
	private Skeleton skel2;
	private TiledPlatform ground;
	private StrippedScrew strScrew;
	private Skeleton skel3;
	private Skeleton skel4;
	private Skeleton skel5, skel6;
	private Rope testRope;

	private BossScrew bossBolt;
	private float endgameCounter;
	private Music inceptionhorn;

	@SuppressWarnings( "unused" )
	private Anchor testAnchor;
	private static final String LEVEL_NAME = "Debug_Playtest";

	public DebugPlayTestScreen( ) {

		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );
		initCamera( );
		metRend = new MetricsRender( LEVEL_NAME );
		skeleton = new Skeleton( "skeleton", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );

		// Uncomment the tilset part to see the new tileset in game.
		platBuilder = new PlatformBuilder( world ).tileSet( "autumn" );

		testTexture = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( )
						+ "/common/tileset/TilesetTest.png", Texture.class );
		inceptionhorn = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/inceptionbutton.mp3" );
		endgameCounter = 0;

		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// Initialize players
		player1 = new PlayerBuilder( ).name( "player1" )
				.definition( "red_male" ).world( world )
				.position( 0 * TILE, 0 * TILE ).buildPlayer( );

		player2 = new PlayerBuilder( ).name( "player2" )
				.definition( "red_female" ).world( world )
				.position( 0 * TILE, 0 * TILE ).buildPlayer( );

		// END: 175f * TILE, 96f * TILE
		// START :: 1f * TILE, 1f * TILE
		// stripped screws: 170 * TILE, 17 * TILE
		// puzzle screws: 145f * TILE, 86f * TILE
		floor1( );
		floor2( );
		floor3( );
		floor4( );
		floor5( );
		floor6( );
		floor7( );
		initCheckPoints( );

		// event trigger that will remove box2d entites when they collide with
		// it
		EventTriggerBuilder etb = new EventTriggerBuilder( world );
		EventTrigger removeTrigger = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 10000 )
				.position( new Vector2( 100 * TILE, -100 * TILE ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		rootSkeleton.addEventTrigger( removeTrigger );

		// Create anchor with start position and buffer as parameters
		// testAnchor = new Anchor( new Vector2( 10000, 10 ), new Vector2(
		// 10000,
		// 10000 ) );
		// Add to the universally accessible anchor list
		// AnchorList.getInstance( ).addAnchor( testAnchor );
		// Set timer in steps
		// testAnchor.setTimer( 200 );
		// Activate it
		// testAnchor.activate( );

		rootSkeleton.addSkeleton( skeleton );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		// debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );

	}

	private void floor1( ) {

		skel1 = new Skeleton( "skel1", new Vector2( 0, 0 ), null, world );

		// PUZZLE 1 //
		EventTriggerBuilder etb = new EventTriggerBuilder( world );
		et = etb.name( "event1" ).circle( ).radius( 100 )
				.position( new Vector2( 10 * TILE, 1.5f * TILE ) )
				.beginAction( new MetricsStartTimeAction( skel1.name ) )
				.build( );

		skel1.addEventTrigger( et );

		ground = platBuilder.position( 50 * TILE, 0 ).name( "ground1" )
				.dimensions( 250, 2 ).texture( testTexture ).kinematic( )
				.buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( ground );

		wall = platBuilder.position( -77 * TILE, 100 * TILE ).name( "wall1" )
				.dimensions( 2, 250 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		wall.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( wall );

		obst = platBuilder.position( 15 * TILE, 1.5f * TILE ).name( "obst1" )
				.dimensions( 2, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 20 * TILE, 2f * TILE ).name( "obst2" )
				.dimensions( 2, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 25 * TILE, 2.5f * TILE ).name( "obst3" )
				.dimensions( 2, 3 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 30 * TILE, 3f * TILE ).name( "obst4" )
				.dimensions( 2, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 35 * TILE, 3.5f * TILE ).name( "obst5" )
				.dimensions( 2, 5 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 40 * TILE, 3f * TILE ).name( "obst6" )
				.dimensions( 2, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 45 * TILE, 3.5f * TILE ).name( "obst7" )
				.dimensions( 2, 5 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		obst.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( obst );

		// etb = new EventTriggerBuilder(world);
		// et = etb.name( "event2" ).circle( ).radius( 100 )
		// .position( new Vector2(50 * TILE, 1.5f * TILE ) )
		// .beginAction( new MetricsEndTimeAction() ).endAction( new
		// MetricsStartTimeAction() )
		// .build();
		// PUZZLE 2 //

		plat = platBuilder.position( 55 * TILE, 4.5f * TILE ).name( "plat1" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 67 * TILE, 4.5f * TILE ).name( "plat2" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 55 * TILE, 8.5f * TILE ).name( "plat3" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 67 * TILE, 8.5f * TILE ).name( "plat4" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 61 * TILE, 6.5f * TILE ).name( "plat5" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( plat );

		// etb = new EventTriggerBuilder(world);
		// et = etb.name( "event2" ).circle( ).radius( 100 )
		// .position( new Vector2(62 * TILE, 1.5f * TILE ) )
		// .beginAction( new MetricsEndTimeAction() ).endAction( new
		// MetricsStartTimeAction() )
		// .build();
		// PUZZLE 3 //

		stair = platBuilder.position( 77 * TILE, 2 * TILE ).name( "stair1" )
				.dimensions( 8, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 78 * TILE, 4 * TILE ).name( "stair2" )
				.dimensions( 6, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 79 * TILE, 6 * TILE ).name( "stair3" )
				.dimensions( 4, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 80 * TILE, 8 * TILE ).name( "stair4" )
				.dimensions( 2, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 82 * TILE, 2.5f * TILE ).name( "stair5" )
				.dimensions( 2, 3 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( stair );

		plat = platBuilder.position( 93 * TILE, 5 * TILE ).name( "plat6" )
				.dimensions( 2, 8 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( plat );

		stair = platBuilder.position( 95 * TILE, 2.5f * TILE ).name( "stair6" )
				.dimensions( 2, 3 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		stair.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( stair );
		// PUZZLE 4 //

		float dx = 4;
		float x = 106;
		float dy = 2;
		float y = 1.5f;
		float width = 7f;
		int i = 0;
		while ( width >= 2 ) {
			step = platBuilder.position( x * TILE, y * TILE )
					.name( "step" + ( i + 1 ) ).dimensions( width, 1 )
					.texture( testTexture ).kinematic( ).oneSided( true )
					.restitution( 0 ).buildTilePlatform( );
			skel1.addKinematicPlatform( step );
			x += dx + width + .5f;
			y += dy;
			i++;
			width--;
		}

		wall = platBuilder.position( 160 * TILE, 6.5f * TILE ).name( "wall2" )
				.dimensions( 2, 11 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		wall.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( wall );

		ground = platBuilder.position( 181 * TILE, 11 * TILE ).name( "ground2" )
				.dimensions( 40, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( ground );

		wall = platBuilder.position( 202 * TILE, 25 * TILE ).name( "wall3" )
				.dimensions( 2, 30 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		wall.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( wall );

		rootSkeleton.addSkeleton( skel1 );

		buildArmSkeleton( );
	}

	void buildArmSkeleton2( ) {
		SkeletonBuilder skeleBuilder = new SkeletonBuilder( world );
		// skeleton at shoulder joint
		Skeleton armSkeleton = skeleBuilder
				.position( 0, 400 )
				.texBackground(
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/robot/alphabot_texture_skin.png",
								Texture.class ) ).vert( -100, -100 )
				.vert( -100, 100 ).vert( 100, 100 ).vert( 100, -100 )
				.kinematic( ).build( );

		skel1.addSkeleton( armSkeleton );

		TiledPlatform upperArm = platBuilder.dynamic( ).position( 850, 800 )
				.dimensions( 9, 1 ).density( 1f ).oneSided( false )
				.buildTilePlatform( );
		TiledPlatform lowerArm = platBuilder.dynamic( ).position( 550, 800 )
				.dimensions( 8, 1 ).density( 1f ).oneSided( false )
				.buildTilePlatform( );
		upperArm.body.setFixedRotation( false );
		lowerArm.body.setFixedRotation( false );
	}

	private void buildArmSkeleton( ) {
		// puzzle screw that controls the gate
		PuzzleScrew pscrewtop = new PuzzleScrew( "armpuzzle", new Vector2(
				1670, 870 ), 50, skel1, world, 0, false, Vector2.Zero );
		// block platform
		plat = platBuilder.position( 1800, 800 ).name( "armstop" )
				.dimensions( 15, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );
		// gate platform
		plat = platBuilder.position( 1470, 700 ).name( "armstop" )
				.dimensions( 5, 32 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		pscrewtop.puzzleManager.addEntity( plat );
		pscrewtop.puzzleManager.addMover( new LerpMover( plat
				.getPositionPixel( ), plat.getPositionPixel( ).add( 0f, 256f ),
				LinearAxis.VERTICAL ) );
		skel1.addKinematicPlatform( plat );
		skel1.addScrewForDraw( pscrewtop );

		SkeletonBuilder skeleBuilder = new SkeletonBuilder( world );
		// skeleton at shoulder joint
		Skeleton upperArmSkeleton = skeleBuilder
				.position( 1000, 800 )
				.texBackground(
						WereScrewedGame.manager.get( WereScrewedGame.dirHandle
								+ "/common/robot/alphabot_texture_skin.png",
								Texture.class ) ).vert( -100, -100 )
				.vert( -100, 100 ).vert( 100, 100 ).vert( 100, -100 ).dynamic( )
				.build( );

		// dynSkeleton = new SkeletonBuilder( world ).position( 0, 200 )
		// .texBackground(
		// WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_skin.png",Texture.class
		// )).name( "dynamicSkeleton" )
		// .vert( -100, -100 ).vert( 100, -100 ).vert( 100, 100 ).vert( -100,100
		// )
		// .texForeground(
		// WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_tux.png",Texture.class
		// ))
		// .fg( ).vert( 200,0 ).vert( 300,100 ).vert( 200,200 )
		// .build( );
		// new Skeleton( "dynamicSkeleton",
		// new Vector2( 1000, 800 ), testTexture, world );

		// skeleton after elbow joint
		Skeleton lowerArmSkeleton = new Skeleton( "dynamic2Skeleton",
				new Vector2( 400, 800 ), testTexture, world );
		lowerArmSkeleton.setDensity( 10f );
		// platforms that represent the arms
		TiledPlatform upperArm = platBuilder.dynamic( ).position( 850, 800 )
				.dimensions( 9, 1 ).density( 1f ).oneSided( false )
				.buildTilePlatform( );
		TiledPlatform lowerArm = platBuilder.dynamic( ).position( 550, 800 )
				.dimensions( 8, 1 ).density( 1f ).oneSided( false )
				.buildTilePlatform( );
		@SuppressWarnings( "unused" )
		HazardBuilder spikesBuilder = new HazardBuilder( world );
		// and spikes under the arms
		// Spikes secondUpArm = spikesBuilder.position( 850, 970).dimensions( 4,
		// 1 )
		// .up( ).active( ).buildSpikes( );
		// Spikes secondLowArm = spikesBuilder.position( 550, 970).dimensions(
		// 4, 1 )
		// .up( ).active( ).buildSpikes( );
		// mover that the arm initially has and also that the bottom
		// puzzle screw can reset
		RotateTweenMover rtm1 = new RotateTweenMover( upperArmSkeleton, 10f,
				Util.PI, 2f, true );
		AnalogRotateMover anlgRot = new AnalogRotateMover( .6f, world );
		upperArmSkeleton.setMoverAtCurrentState( rtm1 );
		// puzzle screw that controls the initial arm move
		PuzzleScrew pscrewbottom = new PuzzleScrew( "armpuzzle", new Vector2(
				1200, 320 ), 5000, skel1, world, 0, false, Vector2.Zero );
		pscrewbottom.puzzleManager.addEntity( upperArmSkeleton );
		pscrewbottom.puzzleManager.addMover( rtm1 );
		skel1.addScrewForDraw( pscrewbottom );
		upperArmSkeleton.setActive( true );
		upperArmSkeleton.body.setType( BodyType.KinematicBody );
		lowerArmSkeleton.body.setType( BodyType.DynamicBody );
		// joints the first dynamic skeleton to the parent skeleton
		// puzzle screw that controls the analog arm puzzle
		PuzzleScrew shoulderJoint = new PuzzleScrew( "dynamic_skeleton_joint",
				new Vector2( 1000, 800 ), 5000, upperArmSkeleton, world, 5000,
				false, Vector2.Zero );
		// joints the arm to the background
		shoulderJoint.addStructureJoint( skel1 );
		// joints the upper arm platform to the skeleton
		shoulderJoint.addStructureJoint( upperArm );
		shoulderJoint.puzzleManager.addEntity( upperArmSkeleton );
		shoulderJoint.puzzleManager.addMover( anlgRot );
		// joints the first dynamic skeleton to the second dynamic skeleton
		StrippedScrew elbowJoint = new StrippedScrew( "dynamic_skeleton_joint",
				new Vector2( 700, 800 ), upperArmSkeleton, world, Vector2.Zero );
		elbowJoint.addStructureJoint( upperArm );
		elbowJoint.addStructureJoint( lowerArm );
		elbowJoint.addStructureJoint( lowerArmSkeleton );
		StrippedScrew platJoint1 = new StrippedScrew(
				"dynamic_skeleton_joint2", new Vector2( 400, 800 ), lowerArm,
				world, Vector2.Zero );
		// platJoint1.addStructureJoint( secondLowArm );
		skel1.addSkeleton( upperArmSkeleton );
		skel1.addSkeleton( lowerArmSkeleton );
		upperArmSkeleton.addKinematicPlatform( upperArm );
		// upperArmSkeleton.addKinematicPlatform( secondUpArm );
		lowerArmSkeleton.addDynamicPlatform( lowerArm );
		// lowerArmSkeleton.addKinematicPlatform( secondLowArm );
		lowerArmSkeleton.addScrewForDraw( platJoint1 );
		upperArmSkeleton.addScrewForDraw( shoulderJoint );
		upperArmSkeleton.addScrewForDraw( elbowJoint );

	}

	private void floor2( ) {
		skel2 = new Skeleton( "skel2", new Vector2( 0, 0 ), null, world );

		// PUZZLE 1 //

		strScrew = new StrippedScrew( "strScrew1", new Vector2( 170 * TILE,
				17 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew2", new Vector2( 185 * TILE,
				17 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew3", new Vector2( 190 * TILE,
				17 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		plat = platBuilder.position( 180 * TILE, 19 * TILE ).name( "plat6" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		plat = platBuilder.position( 195 * TILE, 19 * TILE ).name( "plat7" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		strScrew = new StrippedScrew( "strScrew3", new Vector2( 190 * TILE,
				24 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew3", new Vector2( 185 * TILE,
				24 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		plat = platBuilder.position( 195 * TILE, 25 * TILE ).name( "plat7" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		plat = platBuilder.position( 180 * TILE, 25 * TILE ).name( "plat7" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		plat = platBuilder.position( 187.5f * TILE, 30 * TILE ).name( "plat8" )
				.dimensions( 5, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		// PUZZLE 2 //

		strScrew = new StrippedScrew( "strScrew4", new Vector2( 178 * TILE,
				34 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew5", new Vector2( 171 * TILE,
				34 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		ground = platBuilder.position( 131 * TILE, 22 * TILE ).name( "ground3" )
				.dimensions( 68, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel2.addKinematicPlatform( ground );

		// PUZZLE 3 //

		plat = platBuilder.position( 127f * TILE, 28 * TILE ).name( "plat10" )
				.dimensions( 3, 11 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		strScrew = new StrippedScrew( "strScrew6", new Vector2( 139 * TILE,
				29 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew7", new Vector2( 134 * TILE,
				34 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew8", new Vector2( 119 * TILE,
				34 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew9", new Vector2( 115 * TILE,
				29 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		// PUZZLE 4 //

		strScrew = new StrippedScrew( "strScrew10", new Vector2( 105 * TILE,
				29 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew11", new Vector2( 101 * TILE,
				34 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew12", new Vector2( 109 * TILE,
				34 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew13", new Vector2( 105 * TILE,
				39 * TILE ), skel2, world, Vector2.Zero );
		skel2.addStrippedScrew( strScrew );

		wall = platBuilder.position( 96.5f * TILE, 71.5f * TILE )
				.name( "wall4" ).dimensions( 2, 100 ).texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		wall.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel2.addKinematicPlatform( wall );

		rootSkeleton.addSkeleton( skel2 );
	}

	private void floor3( ) {
		skel3 = new Skeleton( "skel3", new Vector2( 0, 0 ), null, world );
		// create the sub skeleton that rotates
		Skeleton rotatingRoom = new Skeleton( "rotateSkel", new Vector2(
				124 * TILE, 47 * TILE ), testTexture, world );
		RotateTweenMover rtm = new RotateTweenMover( rotatingRoom, 10f,
				-Util.PI, 2f, true );
		rotatingRoom.body.setType( BodyType.KinematicBody );
		rotatingRoom.setMoverAtCurrentState( rtm );
		rotatingRoom.setActive( true );
		skel3.addSkeleton( rotatingRoom );

		// the gate platform
		plat = platBuilder.position( 133.5f * TILE, 45 * TILE ).name( "plat10" )
				.dimensions( 1, 6 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( plat );

		// the gate platform
		plat = platBuilder.position( 133.5f * TILE, 45 * TILE ).name( "plat10" )
				.dimensions( 1, 6 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( plat );

		// puzzle that controls the elevator of the room and the gate
		PuzzleScrew elevatorscrew1 = new PuzzleScrew( "elevatorControlinside",
				rotatingRoom.getPositionPixel( ).add( 64, -64 ), 500,
				rotatingRoom, world, 0, false, Vector2.Zero );
		// add the entities and movers
		elevatorscrew1.puzzleManager.addEntity( rotatingRoom );
		elevatorscrew1.puzzleManager.addMover( new PuzzleRotateTweenMover( 2f,
				-Util.PI / 4.0f, false, PuzzleType.PUZZLE_SCREW_CONTROL ) );
		elevatorscrew1.puzzleManager.addEntity( plat );
		elevatorscrew1.puzzleManager.addMover( new LerpMover( plat
				.getPositionPixel( ), plat.getPositionPixel( ).add( 0, 128 ),
				LinearAxis.VERTICAL ) );
		rotatingRoom.addScrewForDraw( elevatorscrew1 );

		ground = platBuilder.position( 113 * TILE, 40.5f * TILE )
				.name( "ground4" ).dimensions( 8, 1 ).texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( ground );

		// the left wall of the room
		plat = platBuilder.position( 129f * TILE, 49 * TILE ).name( "plat9" )
				.dimensions( 3, 8 ).texture( testTexture ).dynamic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		plat.body.setFixedRotation( false );
		rotatingRoom.addDynamicPlatform( plat );

		StructureScrew s1 = new ScrewBuilder( )
				.position(
						plat.getPositionPixel( ).sub(
								plat.getPixelWidth( ) / 2.0f,
								plat.getPixelHeight( ) / 2 ) ).entity( plat )
				.skeleton( rotatingRoom ).world( world ).buildStructureScrew( );
		s1.addStructureJoint( rotatingRoom );
		rotatingRoom.addScrewForDraw( s1 );

		StructureScrew s2 = new ScrewBuilder( )
				.position(
						plat.getPositionPixel( ).add(
								-plat.getPixelWidth( ) / 2.0f,
								plat.getPixelHeight( ) / 2 ) ).entity( plat )
				.world( world ).buildStructureScrew( );
		s2.addStructureJoint( rotatingRoom );
		rotatingRoom.addScrewForDraw( s2 );
		//
		// // the right wall of the room
		// plat = platBuilder.position( 124.5f * TILE, 46f * TILE ).name(
		// "plat9" )
		// .dimensions( 1, 7 ).texture( testTexture ).dynamic( )
		// .oneSided( false ).restitution( 0 ).buildTilePlatform( );
		// plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_PLAYER
		// );
		// plat.body.setFixedRotation( false );
		// rotatingRoom.addDynamicPlatform( plat );
		//
		// Screw s3 = new ScrewBuilder( )
		// .position(
		// plat.getPositionPixel( ).sub( 0,
		// plat.getPixelHeight( ) / 2 ) ).entity( plat )
		// .world( world ).buildCosmeticScrew( );
		// s3.addStructureJoint( rotatingRoom );
		// rotatingRoom.addScrewForDraw( s3 );
		//
		// Screw s4 = new ScrewBuilder( )
		// .position(
		// plat.getPositionPixel( ).add( 0,
		// plat.getPixelHeight( ) / 2 ) ).entity( plat )
		// .world( world ).buildCosmeticScrew( );
		// s4.addStructureJoint( rotatingRoom );
		// rotatingRoom.addScrewForDraw( s4 );
		//
		// // the top wall of the room
		// plat = platBuilder.position( 118f * TILE, 52 * TILE ).name( "plat9" )
		// .dimensions( 2, 1 ).texture( testTexture ).dynamic( )
		// .oneSided( false ).restitution( 0 ).buildTilePlatform( );
		// plat.setCategoryMask( Util.CATEGORY_PLATFORMS,
		// Util.CATEGORY_EVERYTHING
		// );
		// plat.body.setFixedRotation( false );
		// rotatingRoom.addDynamicPlatform( plat );
		// s2.addStructureJoint( plat );
		//
		// Screw s6 = new ScrewBuilder( )
		// .position(
		// plat.getPositionPixel( ).add(
		// plat.getPixelWidth( ) / 2, 0 ) ).entity( plat )
		// .world( world ).buildCosmeticScrew( );
		// s6.addStructureJoint( rotatingRoom );
		// rotatingRoom.addScrewForDraw( s6 );

		// the bottom wall of the room
		plat = platBuilder.position( 123f * TILE, 43f * TILE ).name( "plat9" )
				.dimensions( 7, 1 ).texture( testTexture ).dynamic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		plat.body.setFixedRotation( false );
		rotatingRoom.addDynamicPlatform( plat );
		Screw s3 = new ScrewBuilder( )
				.position(
						plat.getPositionPixel( ).sub(
								plat.getPixelWidth( ) / 2, 0 ) ).entity( plat )
				.skeleton( rotatingRoom ).world( world ).buildCosmeticScrew( );
		s3.addStructureJoint( rotatingRoom );
		rotatingRoom.addScrewForDraw( s3 );

		Screw s4 = new ScrewBuilder( )
				.position(
						plat.getPositionPixel( ).add(
								plat.getPixelWidth( ) / 2, 0 ) ).entity( plat )
				.world( world ).buildCosmeticScrew( );
		s4.addStructureJoint( rotatingRoom );
		rotatingRoom.addScrewForDraw( s4 );

		ground = platBuilder.position( 183 * TILE, 40.5f * TILE )
				.name( "ground4" ).dimensions( 100, 1 ).texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( ground );

		ground = platBuilder.position( 160 * TILE, 46f * TILE )
				.name( "ground4" ).dimensions( 50, 1 ).texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( ground );

		plat = platBuilder.position( 135f * TILE, 58 * TILE ).name( "plat9" )
				.dimensions( 1, 25 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( plat );

		plat = platBuilder.position( 185f * TILE, 58 * TILE ).name( "plat9" )
				.dimensions( 1, 25 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( plat );

		plat = platBuilder.position( 195f * TILE, 67 * TILE ).name( "plat9" )
				.dimensions( 1, 50 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( plat );

		rootSkeleton.addSkeleton( skel3 );
	}

	private void floor4( ) {
		skel4 = new Skeleton( "skel4", new Vector2( 0, 0 ), null, world );

		strScrew = new StrippedScrew( "strScrew4", new Vector2( 192f * TILE,
				46 * TILE ), skel4, world, Vector2.Zero );
		skel4.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew4", new Vector2( 188f * TILE,
				46 * TILE ), skel4, world, Vector2.Zero );
		skel4.addStrippedScrew( strScrew );

		PathBuilder pb = new PathBuilder( );
		skel4.addMover( pb.begin( skel4 ).target( 0, 150, 3 ).target( 0, 0, 3 )
				.build( ), RobotState.IDLE );
		skel4.setActive( true );

		plat = platBuilder.position( 190f * TILE, 55 * TILE ).name( "plat9" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( plat );

		skel6 = new Skeleton( "skel6", new Vector2( 0, 0 ), null, world );

		strScrew = new StrippedScrew( "strScrew4", new Vector2( 192f * TILE,
				65 * TILE ), skel6, world, Vector2.Zero );
		skel6.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew4", new Vector2( 188f * TILE,
				65 * TILE ), skel6, world, Vector2.Zero );
		skel6.addStrippedScrew( strScrew );
		PathBuilder pb3 = new PathBuilder( );
		skel6.addMover( pb3.begin( skel6 ).target( 0, -150, 3 )
				.target( 0, 0, 3 ).build( ), RobotState.IDLE );
		skel6.setActive( true );

		skel5 = new Skeleton( "skel5", new Vector2( 0, 0 ), null, world );
		strScrew = new StrippedScrew( "strScrew4", new Vector2( 192f * TILE,
				69 * TILE ), skel5, world, Vector2.Zero );
		skel5.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew4", new Vector2( 188f * TILE,
				69 * TILE ), skel5, world, Vector2.Zero );
		skel5.addStrippedScrew( strScrew );

		PathBuilder pb2 = new PathBuilder( );
		skel5.addMover( pb2.begin( skel5 ).target( 0, 150, 3 ).target( 0, 0, 3 )
				.build( ), RobotState.IDLE );
		skel5.setActive( true );

		rootSkeleton.addSkeleton( skel6 );
		rootSkeleton.addSkeleton( skel4 );
		rootSkeleton.addSkeleton( skel5 );
	}

	private void floor5( ) {
		Skeleton skel7 = new Skeleton( "skel7", new Vector2( 0, 0 ), null,
				world );

		plat = platBuilder.position( 178f * TILE, 70 * TILE ).name( "plat9" )
				.dimensions( 7, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		PathBuilder pb = new PathBuilder( );
		plat.addMover( pb.begin( plat ).target( 0, -650, 6 ).target( 0, 0, 6 )
				.build( ), RobotState.IDLE );
		plat.setActive( true );

		plat = platBuilder.position( 172f * TILE, 50 * TILE ).name( "plat9" )
				.dimensions( 7, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		PathBuilder pb2 = new PathBuilder( );
		plat.addMover( pb2.begin( plat ).delay( 1f ).target( -750, 0, 4 )
				.target( 0, 0, 4 ).build( ), RobotState.IDLE );
		plat.setActive( true );

		plat = platBuilder.position( 142f * TILE, 50 * TILE ).name( "plat9" )
				.dimensions( 7, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		PathBuilder pb3 = new PathBuilder( );
		plat.addMover( pb3.begin( plat ).target( 0, 750, 5 ).target( 0, 0, 5 )
				.build( ), RobotState.IDLE );
		plat.setActive( true );

		plat = platBuilder.position( 153f * TILE, 55 * TILE ).name( "plat9" )
				.dimensions( 7, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		PathBuilder pb4 = new PathBuilder( );
		plat.addMover(
				pb4.begin( plat ).target( 0, 500, 3 ).target( 500, 500, 3 )
						.target( 500, 0, 3 ).target( 0, 0, 3 ).build( ),
				RobotState.IDLE );
		plat.setActive( true );

		plat = platBuilder.position( 116f * TILE, 71 * TILE ).name( "plat9" )
				.dimensions( 37, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		plat = platBuilder.position( 148f * TILE, 80 * TILE ).name( "plat9" )
				.dimensions( 75, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		plat = platBuilder.position( 185f * TILE, 92 * TILE ).name( "plat9" )
				.dimensions( 1, 25 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel7.addKinematicPlatform( plat );

		rootSkeleton.addSkeleton( skel7 );
	}

	private void floor6( ) {
		Skeleton skel8 = new Skeleton( "skel8", new Vector2( 0, 0 ), null,
				world );

		plat = platBuilder.position( 103f * TILE, 73 * TILE ).name( "plat9" )
				.dimensions( 5, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel8.addKinematicPlatform( plat );

		PuzzleScrew puzzleScrew = new PuzzleScrew( "001", new Vector2(
				109f * TILE, 73 * TILE ), 100, skel8, world, 0, false,
				Vector2.Zero );
		LerpMover lm2 = new LerpMover( new Vector2( plat.body.getPosition( ).x
				* Util.BOX_TO_PIXEL, plat.body.getPosition( ).y
				* Util.BOX_TO_PIXEL ), new Vector2( plat.body.getPosition( ).x,
				plat.body.getPosition( ).y + 1.5f ).mul( Util.BOX_TO_PIXEL ),
				LinearAxis.VERTICAL );

		plat.setActive( true );
		puzzleScrew.puzzleManager.addEntity( plat );
		puzzleScrew.puzzleManager.addMover( lm2 );
		skeleton.addScrewForDraw( puzzleScrew );

		PuzzleScrew puzzleScrew2 = new PuzzleScrew( "001", new Vector2(
				113f * TILE, 83 * TILE ), 100, skel8, world, 0, false,
				Vector2.Zero );
		// LerpMover lm3 = new LerpMover( new Vector2( plat.body.getPosition(
		// ).x,
		// plat.body.getPosition( ).y + 1.5f ).mul( Util.BOX_TO_PIXEL ),
		// plat.body.getPosition( ).mul( Util.BOX_TO_PIXEL ), 0.001f,
		// false, LinearAxis.VERTICAL, 0, 1, 0f );

		puzzleScrew.puzzleManager.addScrew( puzzleScrew2 );
		puzzleScrew2.puzzleManager.addScrew( puzzleScrew );
		plat.setActive( true );
		puzzleScrew2.puzzleManager.addEntity( plat );
		puzzleScrew2.puzzleManager.addMover( lm2 );
		skeleton.addScrewForDraw( puzzleScrew2 );

		rootSkeleton.addSkeleton( skel8 );
	}

	private void floor7( ) {
		Skeleton skel9 = new Skeleton( "skel9", new Vector2( 0, 0 ), null,
				world );

		plat = platBuilder.position( 120f * TILE, 85 * TILE ).name( "plat9" )
				.dimensions( 5, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );

		plat.setCrushing( true );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel9.addKinematicPlatform( plat );

		plat = platBuilder.position( 130f * TILE, 89 * TILE ).name( "plat10" )
				.dimensions( 1, 6 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel9.addKinematicPlatform( plat );

		PuzzleScrew puzzleScrew = new PuzzleScrew( "004", new Vector2(
				130f * TILE, 83 * TILE ), 100, skel9, world, 0, false,
				Vector2.Zero );
		@SuppressWarnings( "unused" )
		RotateByDegree rm = new RotateByDegree( 0.0f, -90.0f, 0, 0.5f );

		PuzzleRotateTweenMover rtm1 = new PuzzleRotateTweenMover( 1,
				Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );
		plat.setActive( true );
		puzzleScrew.puzzleManager.addEntity( plat );
		puzzleScrew.puzzleManager.addMover( rtm1 );
		skeleton.addScrewForDraw( puzzleScrew );

		plat = platBuilder.position( 143f * TILE, 89 * TILE ).name( "plat11" )
				.dimensions( 1, 6 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		plat.setCategoryMask( Util.CATEGORY_PLATFORMS, Util.CATEGORY_EVERYTHING );
		skel9.addKinematicPlatform( plat );

		PuzzleScrew puzzleScrew2 = new PuzzleScrew( "006", new Vector2(
				143f * TILE, 83 * TILE ), 100, skel9, world, 0, false,
				Vector2.Zero );
		plat.setActive( true );
		puzzleScrew2.puzzleManager.addEntity( plat );
		PuzzleRotateTweenMover rtm2 = new PuzzleRotateTweenMover( 1,
				Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );
		puzzleScrew2.puzzleManager.addMover( rtm2 );
		skeleton.addScrewForDraw( puzzleScrew2 );

		RopeBuilder ropeBuilder = new RopeBuilder( world );

		// texture only exists for 16x64 rope, which are default values for w/h
		testRope = ropeBuilder.position( 154f * TILE, 104 * TILE ).links( 5 )
				.createScrew( ).buildRope( );
		skel9.addRope( testRope, true );

		// StrippedScrew ropeScrew = new StrippedScrew( "ropeScrew", world,
		// new Vector2 ( 154f * TILE, 93 * TILE ), testRope.getLastLink( ) );
		// skel9.addScrewForDraw( ropeScrew );
		plat = platBuilder.position( 175f * TILE, 94 * TILE ).name( "plat11" )
				.dimensions( 6, 1 ).texture( testTexture ).kinematic( )
				.friction( 1.0f ).oneSided( true ).restitution( 0 )
				.buildTilePlatform( );
		skel9.addKinematicPlatform( plat );

		bossBolt = new BossScrew( "", new Vector2( plat.body.getPosition( ).x
				* Util.BOX_TO_PIXEL + ( plat.getMeterWidth( ) / 2 ),
				plat.body.getPosition( ).y * Util.BOX_TO_PIXEL ), 50, plat,
				world, Vector2.Zero );
		bossBolt.addStructureJoint( skel9 );
		skel9.addScrewForDraw( bossBolt );
		// specialPlat = platBuilder.position( 175f * TILE, 84 * TILE ).name(
		// "plat12" )
		// .dimensions( 6, 1 ).texture( testTexture ).kinematic( )
		// .friction( 1.0f ).oneSided( true ).restitution( 0 )
		// .buildTilePlatform( );
		// skel9.addKinematicPlatform( specialPlat );
		//
		// PathBuilder pb = new PathBuilder( );
		// specialPlat.addMover( pb.begin( specialPlat ).target( 0, 150, 3
		// ).target( 0, 0, 3 )
		// .build( ), RobotState.IDLE );
		// specialPlat.addMover( pb.begin( specialPlat ).target( 150, 0, 3
		// ).target( 0, 0, 3 )
		// .build( ), RobotState.DOCILE );
		// //specialPlat.setActive( true );
		//
		// EventTriggerBuilder etb = new EventTriggerBuilder(world);
		// et = etb.name( "event1" ).circle( ).radius( 100 )
		// .position( new Vector2(175f * TILE, 84 * TILE) )
		// .addEntity( specialPlat ).beginAction( new
		// EntityActivateMoverAction() )
		// .endAction( new EntityDeactivateMoverAction() ).repeatable(
		// ).twoPlayersToDeactivate( )
		// .twoPlayersToActivate( )
		// .build();

		rootSkeleton.addSkeleton( skel9 );
	}

	private void initCheckPoints( ) {
		progressManager = new ProgressManager( player1, player2, world );
		skeleton.addCheckPoint( new CheckPoint( "check_01", new Vector2( 0f,
				64f ), skeleton, world, progressManager, "levelStage_0_0" ) );
		skeleton.addCheckPoint( new CheckPoint( "check_02", new Vector2(
				512 * TILE, 64 * TILE ), skeleton, world, progressManager,
				"levelStage_0_1" ) );
		// progressManager
		// .addCheckPoint( new CheckPoint( "check_01", new Vector2( 512f,
		// 32f ), skeleton, world, progressManager,
		// "levelStage_0_2" ) );
		// progressManager.addCheckPoint( new CheckPoint( "check_01", new
		// Vector2(
		// 1024f, 32f ), skeleton, world, progressManager,
		// "levelStage_0_3" ) );
		// progressManager.addCheckPoint( new CheckPoint( "check_01", new
		// Vector2(
		// 1512f, 32f ), skeleton, world, progressManager,
		// "levelStage_0_4" ) );
	}

	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( new Vector2( Gdx.graphics.getWidth( ) * 5f,
				Gdx.graphics.getHeight( ) * 5f ), width, height, world );
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

		cam.update( deltaTime );

		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			Gdx.app.exit( );
		}

		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest ) {
				debug = !debug;
			}
			debugTest = false;
		} else
			debugTest = true;

		player1.update( deltaTime );
		player2.update( deltaTime );
		testRope.update( deltaTime );
		// et.update( deltaTime );

		rootSkeleton.update( deltaTime );
		progressManager.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		rootSkeleton.draw( batch, deltaTime, cam );
		progressManager.draw( batch, deltaTime, cam );
		testRope.draw( batch, deltaTime, cam );
		player1.draw( batch, deltaTime, cam );
		player2.draw( batch, deltaTime, cam );
		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		// if(endLevelFlag)
		if ( bossBolt.endLevelFlag( ) ) {

			if ( !inceptionhorn.isPlaying( ) ) {
				inceptionhorn.play( );
			}
			if ( endgameCounter == 0f ) {

				if ( Metrics.activated ) {
					Metrics.printMetrics( LEVEL_NAME );
				}
				rootSkeleton.addMover( new RockingMover( -0.1f, 0.5f ),
						RobotState.IDLE );
				rootSkeleton.setActive( true );
			}
			endgameCounter += deltaTime;
			cam.camera.zoom += 0.015f;
			if ( endgameCounter > 10f ) {
				Gdx.app.exit( );
			}
		}
		metRend.render( cam.camera );

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