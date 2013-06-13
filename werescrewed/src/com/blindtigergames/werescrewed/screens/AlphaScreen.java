package com.blindtigergames.werescrewed.screens;

import java.util.Random;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.Panel;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.AnchorActivateAction;
import com.blindtigergames.werescrewed.entity.action.AnchorDeactivateAction;
import com.blindtigergames.werescrewed.entity.action.DestroyPlatformJointAction;
import com.blindtigergames.werescrewed.entity.action.EntityActivateMoverAction;
import com.blindtigergames.werescrewed.entity.action.EntityDeactivateMoverAction;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.action.RotateTweenAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Enemy;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.DirectionFlipMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.particles.EntityParticleEmitter;
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;
import com.blindtigergames.werescrewed.util.Util;

public class AlphaScreen extends Screen {

	public ScreenType screenType;
	
	private float dT = 0;
	private Random generator = new Random();

	private PowerSwitch powerSwitch1, powerSwitch2, powerSwitch3, powerSwitch4,
			powerSwitch5, powerSwitch6, powerSwitch7, powerSwitch8,
			powerSwitch9, powerSwitch10, chestSteamPowerSwitch,
			powerSwitchBrain1, powerSwitchBrain2;

	private PowerSwitch powerSwitchPuzzle1, powerSwitchPuzzle2;
	private EntityParticleEmitter fireballEmitter;
	private Skeleton footSkeleton, kneeSkeleton, thighSkeleton, hipSkeleton,
			chestSkeleton, leftShoulderSkeleton, headSkeleton, thighSkeleton2;

	private TiledPlatform kneeMovingPlat, chestRotatePlat1, chestRotatePlat3,
			headEntrancePlatform4, headEyebrow1, headEyebrow2;

	Platform leftShoulderSideHatch, ankleHatch;
	@SuppressWarnings( "unused" )
	private PuzzleScrew leftArmScrew, chestPuzzleScrew2;

	private Steam engineSteam;

	private boolean chestSteamTriggered = false, headPlatformCreated = false,
			headAnchorActivatedOnce = false, testOnce = true;
	private boolean rLegTriggered = false, thighSteamTriggered = false;

	private Skeleton rightShoulderSkeleton;

	private Platform rightArmDoor;

	@SuppressWarnings( "unused" )
	private StrippedScrew rightArmDoorHinge;
	@SuppressWarnings( "unused" )
	private StructureScrew structureScrew1;

	@SuppressWarnings( "unused" )
	private int rightShoulderSkeletonAnchorCounter = 0;

	Array< Panel > panels;

	public AlphaScreen( ) {
		super( );

		setClearColor( 79.0f / 255.0f, 82.0f / 255.0f, 104.0f / 255.0f, 1.0f ); // purple-ish

		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );

		// level.camera.position = new Vector3( 0, 0, 0 );

		// ***************************************
		// Death Barriers
		// ***************************************

		// bottom
		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
		EventTrigger removeTrigger = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 50000 ).position( new Vector2( 0, -3200 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		level.root.addEventTrigger( removeTrigger );

		// left arm
		etb = new EventTriggerBuilder( level.world );
		removeTrigger = etb.name( "removeEntity" ).rectangle( ).width( 10 )
				.height( 5000 ).position( new Vector2( -3700, 2800 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		level.root.addEventTrigger( removeTrigger );

		// right arm
		etb = new EventTriggerBuilder( level.world );
		removeTrigger = etb.name( "removeEntity" ).rectangle( ).width( 10 )
				.height( 5000 ).position( new Vector2( 5000, 2800 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		level.root.addEventTrigger( removeTrigger );

		createFootObjects( );
		createKneeObjects( );
		thighDecals( thighSkeleton );// createFootObjects initializes
										// thighSkeleton

		// start: 512, 256
		// power screws: -700f, 1800f
		// chest entrance : -200f, 3800f
		// upper chest: 1300f, 6000f
		// rope on left side of the robot <- -950f, 5100f
		// top left: -1582f, 6150f <<<< side
		// head: 480f, 6688f
		// right arm: 2600f, 6000f >>>> side
		// left side hand <- -2224, 3008

		Vector2 spawnPos = new Vector2( 350, 200 );

		if ( level.player1 == null ) {
			level.player1 = new PlayerBuilder( ).world( level.world )
					.position( spawnPos.cpy( ) ).name( "player1" )
					.definition( "red_male" ).buildPlayer( );
			level.progressManager.addPlayerOne( level.player1 );
		}
		if ( level.player2 == null ) {
			level.player2 = new PlayerBuilder( ).world( level.world )
					.position( spawnPos.cpy( ) ).name( "player2" )
					.definition( "red_female" ).buildPlayer( );
			level.progressManager.addPlayerTwo( level.player2 );
		}
		level.player1.setThatGuy( level.player2 );
		level.player2.setThatGuy( level.player1 );

		// background stuff
		level.backgroundBatch = new SpriteBatch( );
		level.backgroundRootSkeleton = new RootSkeleton( "backgroundroot",
				Vector2.Zero, null, level.world );
		float _width = WereScrewedGame.getWidth( ) / 1f;
		float _height = WereScrewedGame.getHeight( ) / 1f;
		level.backgroundCam = new OrthographicCamera( 1, _width / _height );
		level.backgroundCam.viewportWidth = _width;
		level.backgroundCam.viewportHeight = _height;
		level.backgroundCam.position.set( _width * .5f, _height * .5f, 0f );
		level.backgroundCam.update( );

		chestObjects( );
		leftArm( );
		rightArm( );
		knee2Objects( );
		buildBackground( );
		initPowerScrews( );

		buildEngineHeart( new Vector2( 0, 5450 ) );

		// powerSwitch();
		initPanels( );

		rightArmDecal( );
		headDecals( );

		rightLegDecals( );
		leftArmDecal( );
		chestDecals( );
		Skeleton root = ( Skeleton ) LevelFactory.entities.get( "RootSkeleton" );
		root.setFgFade( false );

		sounds = new SoundManager( );
		sounds.getSound( "arm_start", WereScrewedGame.dirHandle.path( )
				+ "/levels/alphabot/sounds/arm_move_begin.ogg" );
		sounds.getSound( "arm_loop", WereScrewedGame.dirHandle.path( )
				+ "/levels/alphabot/sounds/arm_move_loop.ogg" );
		sounds.getSound( "arm_end", WereScrewedGame.dirHandle.path( )
				+ "/levels/alphabot/sounds/arm_move_end.ogg" );

		Skeleton skel = ( Skeleton ) LevelFactory.entities.get( "hipSkeleton" );
		skel.setMacroSkel( true );
		skel = ( Skeleton ) LevelFactory.entities.get( "thighSkeleton" );
		skel.setMacroSkel( true );
		skel = ( Skeleton ) LevelFactory.entities.get( "kneeSkeleton" );
		skel.setMacroSkel( true );
		skel = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		skel.setMacroSkel( true );
		
		Platform footWall2 = ( Platform ) LevelFactory.entities.get( "footWall2" );
		footWall2.dontPutToSleep = true;
		Platform footBottom = ( Platform ) LevelFactory.entities.get( "footBottom" );
		footBottom.dontPutToSleep = true;
	}

	@Override
	public void load(){
		if (bgm == null){
			bgm = WereScrewedGame.manager.get( WereScrewedGame.dirHandle.path( ) + "/common/music/waltz.mp3", Music.class );
		}
		if (sounds == null){
			sounds = new SoundManager( );
		}
		if (!assetsLoaded){
			sounds.loadMultiSound( "left_arm_movement", 
									0, 
									0.0f, 
									WereScrewedGame.dirHandle.path( )
									+ "/levels/alphabot/sounds/arm_move_begin.ogg", 
									1.0f, 
									WereScrewedGame.dirHandle.path( )
									+ "/levels/alphabot/sounds/arm_move_loop.ogg", 
									0.0f, 
									WereScrewedGame.dirHandle.path( )
									+ "/levels/alphabot/sounds/arm_move_end.ogg", 
									0.0f );
			sounds.loadMultiSound( "right_arm_movement_1", 
					0, 
					0.0f, 
					WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/arm_move_begin.ogg", 
					1.0f, 
					WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/arm_move_loop.ogg", 
					0.0f, 
					WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/arm_move_end.ogg", 
					0.0f );
			sounds.loadMultiSound( "right_arm_movement_2", 
					0, 
					5.0f, 
					WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/arm_move_begin.ogg", 
					1.0f, 
					WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/arm_move_loop.ogg", 
					0.0f, 
					WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/arm_move_end.ogg", 
					0.0f );
			sounds.getSound( "fireworks", WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/fireworks1.ogg" );
			sounds.getSound( "fireworks", WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/fireworks2.ogg" );
			sounds.getSound( "applause_action", WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/applause_action.ogg" );
			sounds.getSound( "applause_final", WereScrewedGame.dirHandle.path( )
					+ "/levels/alphabot/sounds/applause_final.ogg" );
			assetsLoaded = true;
		}
		
		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
	}
	
	@Override
	public void render( float deltaTime ) {
		
		dT += deltaTime;
		
		super.render( deltaTime );
		powerScrewUpdate( deltaTime );
		
		// If everything is on
		if ( powerSwitch1.isTurnedOn( ) && powerSwitch2.isTurnedOn( )
				&& powerSwitch3.isTurnedOn( ) && powerSwitch4.isTurnedOn( )
				&& powerSwitch5.isTurnedOn( ) && powerSwitch6.isTurnedOn( )
				&& powerSwitch7.isTurnedOn( ) && powerSwitch8.isTurnedOn( )
				&& powerSwitch9.isTurnedOn( ) && powerSwitch10.isTurnedOn( ) ) {

			// check if players are outside of arms and above half the chest
			if ( ( level.player1.getPositionPixel( ).x > -1747f
					&& level.player1.getPositionPixel( ).x < 2848f && level.player1
					.getPositionPixel( ).y > 4688 )
					&& ( level.player2.getPositionPixel( ).x > -1747f
							&& level.player2.getPositionPixel( ).x < 2848f && level.player2
							.getPositionPixel( ).y > 4688 ) ) {

				if ( !headSkeleton.anchors.get( 0 ).activated
						&& !headAnchorActivatedOnce ) {
					headAnchorActivatedOnce = true;
					headSkeleton.anchors.get( 0 ).setTimer( 3600 );
					headSkeleton.anchors.get( 0 ).activate( );
				}

				if ( !headPlatformCreated ) {
					headPlatformCreated = true;
					EventTriggerBuilder etb = new EventTriggerBuilder(
							level.world );

					etb.name( "head_platform_event" ).rectangle( )
							.height( 500f ).width( 300f )
							.position( new Vector2( 900, 6150 ) );

					etb.addEntity( headEntrancePlatform4 );
					etb.beginAction( new EntityActivateMoverAction( ) );

					EventTrigger et = etb.repeatable( ).build( );
					chestSkeleton.addEventTrigger( et );
				}
			}

			if ( powerSwitchBrain1.isTurnedOn( )
					&& powerSwitchBrain2.isTurnedOn( ) ) {
				if (!sounds.isDelayed( "applause_final" )){
					sounds.playSound( "applause_final", 6.0f );
				}
				if(dT > .5){
					dT = 0;
					shootFireworks();
				}

				if ( headEyebrow1.currentMover( ) == null ) {
					Timeline t = Timeline.createSequence( );

					t.push( Tween
							.to( headEyebrow1, PlatformAccessor.LOCAL_POS_XY,
									0.5f ).delay( 0f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );

					t.push( Tween
							.to( headEyebrow2, PlatformAccessor.LOCAL_POS_XY,
									0f ).delay( 5f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );

					headEyebrow1
							.addMover( new TimelineTweenMover( t.start( ) ) );
				}

				if ( headEyebrow2.currentMover( ) == null ) {
					Timeline t = Timeline.createSequence( );

					t.push( Tween
							.to( headEyebrow2, PlatformAccessor.LOCAL_POS_XY,
									0.5f ).delay( 0f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );

					t.push( Tween
							.to( headEyebrow2, PlatformAccessor.LOCAL_POS_XY,
									0f ).delay( 5f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );

					headEyebrow2
							.addMover( new TimelineTweenMover( t.start( ) ) );
				}

				if ( headEyebrow1.isTimeLineMoverFinished( )
						&& headEyebrow2.isTimeLineMoverFinished( ) ) {
					
					// You win and goto next screen!!!
					//ScreenManager.getInstance( ).show( ScreenType.LOADING_TROPHY_2 );
					transOutEnd = false;
				}
			}
		}

		if ( WereScrewedGame.debug && Gdx.input.isKeyPressed( Keys.NUM_9 ) ) {
			powerSwitch1.setState( true );
			powerSwitch2.setState( true );
			powerSwitch3.setState( true );
			powerSwitch4.setState( true );
			powerSwitch5.setState( true );
			powerSwitch6.setState( true );
			powerSwitch7.setState( true );
			powerSwitch8.setState( true );
			powerSwitch9.setState( true );
			powerSwitch10.setState( true );
		}
		
		level.backgroundBatch.begin( );
		if ( !transInEnd ) {
			drawTransIn( level.backgroundBatch );
		}
		
		if ( !transOutEnd ) {
			drawTransOut( level.backgroundBatch, ScreenType.LOADING_TROPHY_2  );
		}
		level.backgroundBatch.end( );

	}

	@SuppressWarnings( "unused" )
	private void buildBackground( ) {
		Skeleton bgSkele;
		bgSkele = ( Skeleton ) LevelFactory.entities.get( "stageSkeleton" );

		// Gdx.app.log( "bgSKele", bgSkele.getPositionPixel( ) + "" );
		Skeleton light_skel = ( Skeleton ) LevelFactory.entities
				.get( "lightSkeleton" );
		addBGSkeletonBack( light_skel );

		// level.skelBGList.put( key, value )
		TextureAtlas theater_floor_seats_stage = WereScrewedGame.manager
				.getAtlas( "theater_floor_seats_stage" );
		TextureAtlas light_curtain = WereScrewedGame.manager
				.getAtlas( "light_curtain" );

		Sprite s;

		int max = 2030;
		int offsetX = 200;
		int offsetY = 0;
		int floorY = -199 + offsetY;
		int seatsY = -583 + offsetY;
		int seatsX = -1180 + offsetX;// -1180
		int floorX = -max + offsetX;
		int stage_pillarY = -202 + offsetY;
		int stage_pillarX = floorX - 530;
		int lightX = offsetX - 1974;
		int lightY = offsetY + 24;

		int domeSliceX = 1234 * 2;
		int domeSliceY = 1638;

		int supportY = 6500 + offsetY;
		int supportX = -max + seatsX;

		int curtainX = seatsX - max + 1195;
		int curtainY = seatsY + 585;

		float scale = 1f / 0.75f;
		float floorScale = 1f / 0.724848916f; // Don't ask me why

		// support beam
		/*
		 * light_skel.addBGDecalBack( support_left.createSprite( "support_left"
		 * ), new Vector2( supportX, supportY ) ); light_skel.addBGDecalBack(
		 * support_middle_right.createSprite( "support_middle" ), new Vector2(
		 * supportX + max, supportY + 216 ) ); light_skel.addBGDecalBack(
		 * support_middle_right.createSprite( "support_right" ), new Vector2(
		 * supportX + 2 * max, supportY ) );
		 */

		// lights
		s = light_curtain.createSprite( "light_left" );
		s.setScale( scale );
		light_skel.addBGDecal( s, new Vector2( lightX, lightY ) );
		s = light_curtain.createSprite( "light_right" );
		s.setScale( scale );
		light_skel.addBGDecal( s, new Vector2( lightX + 2033, lightY ) );

		// floor
		s = theater_floor_seats_stage.createSprite( "floor" );
		s.setScale( floorScale );
		light_skel.addBGDecal( s, new Vector2( floorX, floorY ) );

		// curtains
		s = light_curtain.createSprite( "curtain_bottom" );
		// s.setScale( scale );
		bgSkele.addFGDecal( s, new Vector2( curtainX, curtainY ) );
		s = light_curtain.createSprite( "curtain_top" );
		bgSkele.addFGDecal( s, new Vector2( curtainX, curtainY + 1176 ) );
		float curtainTopWidth = s.getWidth( );
		s = light_curtain.createSprite( "curtain_bottom" );
		s.setScale( -1, 1 );
		bgSkele.addFGDecal( s, new Vector2( curtainX + curtainTopWidth * 2 - 1,
				curtainY ) );

		s = light_curtain.createSprite( "curtain_top" );
		s.setScale( -1, 1 );
		bgSkele.addFGDecal( s, new Vector2( curtainX + s.getWidth( ) * 2 - 1,
				curtainY + 1176 ) );

		// stage is in between floor & seats
		s = theater_floor_seats_stage.createSprite( "stage_bottom" );
		s.setScale( scale );
		bgSkele.addFGDecal( s, new Vector2( stage_pillarX, stage_pillarY ) );
		s = theater_floor_seats_stage.createSprite( "stage_top" );
		s.setScale( scale );
		bgSkele.addFGDecal( s, new Vector2( stage_pillarX + 2,
				1684 + stage_pillarY ) );
		s = theater_floor_seats_stage.createSprite( "stage_top" );
		s.setScale( -scale, scale );
		bgSkele.addFGDecal( s, new Vector2( stage_pillarX - 2 + s.getWidth( )
				* scale * 2, 1684 + stage_pillarY ) );
		float widthTop = s.getWidth( ) * scale;
		s = theater_floor_seats_stage.createSprite( "stage_bottom" );
		s.setScale( -scale, scale );
		bgSkele.addFGDecal( s, new Vector2( stage_pillarX + widthTop * 2,
				stage_pillarY ) );

		/*
		 * bgSkele.addFGDecal( stage_pillar.createSprite( "stage_right" ), new
		 * Vector2( stage_pillarX + 3204, stage_pillarY ) );
		 * 
		 * bgSkele.addFGDecal( stage_upperright.createSprite( "stage_upperright"
		 * ), new Vector2( stage_pillarX + 2004, stage_pillarY + 1616 ) );//
		 * 1617
		 */

		// works
		// seats
		// todo:
		s = theater_floor_seats_stage.createSprite( "seats" );
		s.setScale( scale );
		light_skel.addFGDecal( s, new Vector2( -max + seatsX, seatsY ) );
		s = theater_floor_seats_stage.createSprite( "seats" );
		s.setScale( -scale, scale );
		light_skel.addFGDecal( s, new Vector2( ( -max + seatsX + 2614 * 2 ),
				seatsY ) );
		light_skel.setFade( false );

		// addBackGroundEntity( bgSkele );
		// addForeGroundEntity( bgSkele );

		// initBackground( dome, numDomes, domeSliceX, domeSliceY,
		// 100,100);//-max + seatsX, seatsY );
		initParallaxBackground( );
		// level.entityFGList.add(bgSkele);
		// level.entityBGList.add(bgSkele);

		// level.entityBGList.remove( level.root );
		// level.entityFGList.remove( level.root );

		addBGSkeletonBack( bgSkele );
		addFGSkeleton( bgSkele );
		level.skelFGList.remove( light_skel );
		addFGSkeleton( light_skel );

		light_skel.setFgFade( false );

	}

	private void initParallaxBackground( ) {
		TextureAtlas dome = WereScrewedGame.manager.getAtlas( "dome_small" );
		BodyDef screwBodyDef;
		Body body;
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity e1;

		screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.KinematicBody;
		Vector2 bodyPos = new Vector2( 0, -512 );
		screwBodyDef.position.set( bodyPos.cpy( ).mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.fixedRotation = true;
		body = level.world.createBody( screwBodyDef );
		screwShape = new CircleShape( );
		screwShape.setRadius( 64 * Util.PIXEL_TO_BOX );
		screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = Util.CATEGORY_IGNORE;
		screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		body.setUserData( this );
		screwShape.dispose( );

		e1 = new Entity( "bg_1", bodyPos, null, body, false );
		e1.sprite = dome.createSprite( "dome_small" );
		e1.setMoverAtCurrentState( new ParallaxMover( new Vector2( e1
				.getPositionPixel( ) ), new Vector2( e1.getPositionPixel( )
				.add( 0f, 312f ) ), 0.00005f, .5f, level.camera, false,
				LinearAxis.VERTICAL ) );
		level.backgroundRootSkeleton.addLooseEntity( e1 );
	}

	// Called by BuildBackground()
	@SuppressWarnings( "unused" )
	private void initBackground( TextureAtlas[ ] dome, int numDomes,
			int domeSliceX, int domeSliceY, int startX, int startY ) {

		BodyDef screwBodyDef;
		Body[ ] body = new Body[ 2 ];
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity e1, e2;
		Vector2 pos;
		Vector2 offset = new Vector2( -4000, -400 );
		for ( int i = numDomes; i > 0; --i ) {
			int yStep = ( int ) ( ( 10 - i ) / 2 );
			pos = new Vector2( startX, startY + domeSliceY * yStep );
			int flipX = 4;
			if ( i % 2 == 0 ) {// even
				pos.x += domeSliceX;
				flipX = 2;
			}
			Sprite a = dome[ i - 1 ].createSprite( "dome" + i );
			a.setScale( 2, 1 );
			Sprite b = dome[ i - 1 ].createSprite( "dome" + i );
			b.setScale( -2, 1 );

			for ( int j = 0; j < 2; ++j ) {
				screwBodyDef = new BodyDef( );
				screwBodyDef.type = BodyType.KinematicBody;
				Vector2 bodyPos = pos.cpy( );
				if ( j == 1 )
					bodyPos.add( flipX * domeSliceX, 0 );
				screwBodyDef.position.set( bodyPos );
				screwBodyDef.fixedRotation = true;
				body[ j ] = level.world.createBody( screwBodyDef );
				screwShape = new CircleShape( );
				screwShape.setRadius( 64 * Util.PIXEL_TO_BOX );
				screwFixture = new FixtureDef( );
				screwFixture.filter.categoryBits = Util.CATEGORY_IGNORE;
				screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
				screwFixture.shape = screwShape;
				screwFixture.isSensor = true;
				body[ j ].createFixture( screwFixture );
				body[ j ].setUserData( this );
				screwShape.dispose( );
			}
			// the position of each entity and sprite is set at this point.
			e1 = new Entity( "bg_1_" + i, pos, null, body[ 0 ], false );
			e1.sprite = a;
			e1.setMoverAtCurrentState( new ParallaxMover( new Vector2( e1
					.getPositionPixel( ) ), new Vector2( e1.getPositionPixel( )
					.add( 0f, 512f ) ), 0.0002f, .5f, level.camera, false,
					LinearAxis.VERTICAL ) );
			level.backgroundRootSkeleton.addLooseEntity( e1 );

			e2 = new Entity( "bg_2_" + i, pos.cpy( )
					.add( flipX * domeSliceX, 0 ), null, body[ 1 ], false );
			e2.sprite = b;
			e2.setMoverAtCurrentState( new ParallaxMover( new Vector2( e2
					.getPositionPixel( ) ), new Vector2( e2.getPositionPixel( )
					.add( 0f, 512f ) ), 0.0002f, .5f, level.camera, false,
					LinearAxis.VERTICAL ) );
			level.backgroundRootSkeleton.addLooseEntity( e2 );

			// pos.add(offset);
			// level.backgroundRootSkeleton.addBGDecal( a, pos );
			// level.backgroundRootSkeleton.addBGDecal(b, pos.cpy().add( flipX *
			// domeSliceX, 0 ));
		}

	}

	private void thighDecals( Skeleton thighSkeleton ) {
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "foot_shin_thigh" );

		// level.entityBGList.add(thighSkeleton);
		Sprite sprite = decals.createSprite( "left-thigh" );
		sprite.setScale( 1f / 0.75f );
		thighSkeleton.addBGDecalBack( sprite, new Vector2( -365, -994 ) );
		// 380,1117

		Vector2 thighPos = new Vector2( -425, -1010 );
		thighSkeleton.addFGDecal(
				Sprite.scale( decals.createSprite( "thigh_exterior" ), 1.75f ),
				thighPos );

		thighSkeleton.bgSprite = null;
	}

	@SuppressWarnings( "unused" )
	private void createFootObjects( ) {
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "foot_shin_thigh" );

		footSkeleton = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		footSkeleton.setFgFade( true );

		kneeSkeleton = ( Skeleton ) LevelFactory.entities.get( "kneeSkeleton" );

		hipSkeleton = ( Skeleton ) LevelFactory.entities.get( "hipSkeleton" );

		thighSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "thighSkeleton" );
		thighSkeleton2 = ( Skeleton ) LevelFactory.entities
				.get( "thighSkeleton2" );

		footSkeleton.body.setType( BodyType.KinematicBody );
		kneeSkeleton.body.setType( BodyType.KinematicBody );
		thighSkeleton.body.setType( BodyType.KinematicBody );

		structureScrew1 = ( StructureScrew ) LevelFactory.entities
				.get( "structureScrew1" );
		// 414, 48
		ankleHatch = ( Platform ) LevelFactory.entities.get( "ankle_hatch" );
		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( ankleHatch.body, footSkeleton.body,
		// new Vector2( -414, 48 ).mul( Util.PIXEL_TO_BOX ) );
				new Vector2( -415, 14 ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );

		TiledPlatform structurePlat3 = ( TiledPlatform ) LevelFactory.entities
				.get( "structurePlat3" );
		TiledPlatform pivotPlat1 = ( TiledPlatform ) LevelFactory.entities
				.get( "pivotPlat1" );
		TiledPlatform footPlat6 = ( TiledPlatform ) LevelFactory.entities
				.get( "footPlat6" );

		// RevoluteJointDef rjd = new RevoluteJointDef( );
		// rjd.initialize( structurePlat3.body, pivotPlat1.body, pivotPlat1
		// .getPosition( ).add( pivotPlat1.getMeterWidth( ) / 2, 0 ) );
		// rjd.collideConnected = false;
		// level.world.createJoint( rjd );

		// structurePlat3.setGroupIndex( ( short ) -5 );
		footPlat6.setGroupIndex( ( short ) -5 );

		// DECALS for Foot / Shin
		Sprite s;
		float scale = 1f / .75f;
		int decalX = -703;// -482;//587
		int decalY = -614;// -558;//536
		Sprite footBG = decals.createSprite( "left-foot" );
		Sprite legBG = decals.createSprite( "left-shin" );
		Skeleton foot = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		foot.addBGDecal( Sprite.scale( footBG, scale ), new Vector2( decalX,
				decalY ) );
		footBG.setOrigin( 0f, 0f );
		foot.addBGDecal( Sprite.scale( legBG, scale ), new Vector2(
				450 + decalX, 440 + decalY ) );

		addBGSkeleton( footSkeleton );
		addFGSkeleton( footSkeleton );

		addBGSkeleton( thighSkeleton );
		addFGSkeleton( thighSkeleton );

		Vector2 footFGPos = new Vector2( decalX - 0, decalY - 10 );
		foot.addFGDecal(
				Sprite.scale( decals.createSprite( "foot_exterior" ), scale ),
				footFGPos );
		foot.addFGDecal(
				Sprite.scale( decals.createSprite( "shin_exterior" ), scale ),
				footFGPos.cpy( ).add( 400, 386 ) );

		foot.bgSprite = null;

	}

	private void rightLegDecals( ) {
		Skeleton rightFoot = ( Skeleton ) LevelFactory.entities
				.get( "footSkeleton2" );
		Skeleton rightKnee = ( Skeleton ) LevelFactory.entities
				.get( "kneeSkeleton2" );
		Skeleton rightThigh = ( Skeleton ) LevelFactory.entities
				.get( "thighSkeleton2" );

		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "foot_shin_thigh" );

		// DECALS for Foot / Shin
		Sprite sprite;
		float scale = 1f / .75f;

		addFGSkeleton( rightFoot );

		Vector2 footFGPos = new Vector2( 750, -690 );
		sprite = decals.createSprite( "foot_exterior" );
		sprite.setScale( -scale, scale );
		rightFoot.addFGDecal( sprite, footFGPos );

		sprite = decals.createSprite( "shin_exterior" );
		sprite.setScale( -scale, scale );
		rightFoot.addFGDecal( sprite, footFGPos.cpy( ).add( -400, 386 ) );

		// EXTERNAL:
		// KNEE
		decals = WereScrewedGame.manager.getAtlas( "foot_shin_thigh" );

		Vector2 kneeDecalPos = new Vector2( 630, -530 );
		sprite = decals.createSprite( "knee_exterior" );
		sprite.setScale( -scale, scale );
		rightKnee.addFGDecal( sprite, kneeDecalPos.cpy( ) );
		addFGSkeleton( rightKnee );

		// THIGH
		TextureAtlas thigh_exterior = WereScrewedGame.manager
				.getAtlas( "foot_shin_thigh" );
		addFGSkeleton( rightThigh );
		Vector2 thighDecalPos = new Vector2( -425, -420 );
		sprite = Sprite.scale( thigh_exterior.createSprite( "thigh_exterior" ),
				1.8f );
		rightThigh.addFGDecal( sprite, thighDecalPos );

		// INTERNAL:
		rightKnee.bgSprite = null;
		rightThigh.bgSprite = null;

		TextureAtlas right_leg_internal = WereScrewedGame.manager
				.getAtlas( "right-leg" );
		sprite = Sprite.scale(
				right_leg_internal.createSprite( "right-leg-top" ), scale );
		rightThigh.addBGDecal( sprite, thighDecalPos.cpy( ).add( 0, 25 ) );
		sprite = Sprite.scale(
				right_leg_internal.createSprite( "right-leg-knee" ), scale );
		rightKnee.addBGDecal( sprite, new Vector2( -441, -601 ) );

	}

	private void createKneeObjects( ) {
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "foot_shin_thigh" );
		TextureAtlas knee_exterior = WereScrewedGame.manager
				.getAtlas( "knee_chest_in" );
		float scale = 1f / .75f;
		kneeMovingPlat = ( TiledPlatform ) LevelFactory.entities
				.get( "kneeMovingPlat" );
		kneeMovingPlat.setActive( false );

		kneeSkeleton = ( Skeleton ) LevelFactory.entities.get( "kneeSkeleton" );

		Vector2 kneeDecalPos = kneeSkeleton.getPositionPixel( )
				.add( 260, -2279 ); // this is horrible I know know why knee is
									// here even 50,60
		kneeSkeleton.addFGDecalBack(
				Sprite.scale( decals.createSprite( "knee_exterior" ), scale ),
				kneeDecalPos.cpy( ).add( -36, -65 ) );
		addFGSkeleton( kneeSkeleton );
		addBGSkeleton( kneeSkeleton );

		kneeSkeleton
				.addBGDecalBack( Sprite.scale(
						knee_exterior.createSprite( "left-knee" ), scale ),
						kneeDecalPos.cpy( ).add( 22, -6 ) );
		// removePlayerToScrew( )

		kneeSkeleton.bgSprite = null;
	}

	private void initPowerScrews( ) {

		powerSwitchBrain1 = ( PowerSwitch ) LevelFactory.entities
				.get( "PowerSwitchBrain1" );
		powerSwitchBrain2 = ( PowerSwitch ) LevelFactory.entities
				.get( "PowerSwitchBrain2" );

		powerSwitch1 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch1" );
		powerSwitch2 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch2" );

		powerSwitch3 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch3" );
		powerSwitch4 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch4" );

		powerSwitch5 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch5" );
		powerSwitch6 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch6" );

		powerSwitch7 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch7" );
		powerSwitch8 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch8" );

		powerSwitch9 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch9" );
		powerSwitch10 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitch10" );

		chestSteamPowerSwitch = ( PowerSwitch ) LevelFactory.entities
				.get( "chestSteamPowerSwitch" );
		chestSteamPowerSwitch.setState( true );
		chestSteamPowerSwitch.addEntityToTrigger( engineSteam );
		chestSteamPowerSwitch.actOnEntity = true;
		chestSteamPowerSwitch
				.setBeginIAction( new EntityActivateMoverAction( ) );
		chestSteamPowerSwitch
				.setEndIAction( new EntityDeactivateMoverAction( ) );

		powerSwitchPuzzle1 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitchPuzzle1" );
		powerSwitchPuzzle2 = ( PowerSwitch ) LevelFactory.entities
				.get( "powerSwitchPuzzle2" );

		powerSwitchPuzzle1.actOnEntity = true;
		powerSwitchPuzzle1.addEntityToTrigger( chestRotatePlat3 );
		powerSwitchPuzzle1.addEntityToTrigger( chestRotatePlat1 );
		powerSwitchPuzzle1
				.setBeginIAction( new RotateTweenAction( Util.PI / 2 ) );
		powerSwitchPuzzle1.setEndIAction( new RotateTweenAction( 0 ) );

		powerSwitchPuzzle2.actOnEntity = true;
		powerSwitchPuzzle2.addEntityToTrigger( chestRotatePlat3 );
		powerSwitchPuzzle2.addEntityToTrigger( chestRotatePlat1 );
		powerSwitchPuzzle2
				.setBeginIAction( new RotateTweenAction( Util.PI / 2 ) );
		powerSwitchPuzzle2.setEndIAction( new RotateTweenAction( 0 ) );

	}

	private void powerScrewUpdate( float deltaTime ) {
		SoundRef leftArmSound = sounds.getSound( "left_arm_movement" );
		SoundRef rightArmSound = sounds.getSound( "left_arm_movement" );
		SoundRef rightArmSound2 = sounds.getSound( "left_arm_movement" );
		leftArmSound.setVolume( 1.0f );
		rightArmSound.setVolume( 1.0f );
		rightArmSound2.setVolume( 1.0f );
		
		if ( powerSwitch1.isTurnedOn( ) && powerSwitch2.isTurnedOn( ) ) {
			kneeMovingPlat.setActive( true );

			if ( hipSkeleton.currentMover( ) == null ) {
				updatePanels( "left_leg" );
				hipSkeleton.addMover( new RotateTweenMover( hipSkeleton, 3f,
						-Util.PI / 45, 1f, true ), RobotState.IDLE );

				// PathBuilder pb = new PathBuilder( );
				// hipSkeleton.addMover( pb.begin( hipSkeleton )
				// .target( 0, 100, 3 ).delay( 1 ).target( 0, -25, 3 )
				// .target( 0, 0, 3 ).build( ), RobotState.IDLE );

			}

		}

		if ( powerSwitch5.isTurnedOn( ) && powerSwitch6.isTurnedOn( )
				&& !thighSteamTriggered ) {
			thighSteamTriggered = true;
			Steam steam = new Steam( "steamThigh1", new Vector2( 2015, 2880 ),
					25, 120, level.world );
			thighSkeleton2.addSteam( steam );

			// Trap door goes back up so players can jump out
			Skeleton rightKneeTrapDoorSkeleton1 = ( Skeleton ) LevelFactory.entities
					.get( "rightKneeTrapDoorSkeleton1" );

			Timeline t = Timeline.createSequence( );
			t.push( Tween
					.to( rightKneeTrapDoorSkeleton1,
							PlatformAccessor.LOCAL_ROT, 2f )
					.ease( TweenEquations.easeInOutQuad ).target( ( 0 ) )
					.delay( 0f ).start( ) );

			rightKneeTrapDoorSkeleton1.addMover( new TimelineTweenMover( t
					.start( ) ) );
		}
		if ( powerSwitch7.isTurnedOn( ) && powerSwitch8.isTurnedOn( ) ) {
			if ( leftShoulderSkeleton.currentMover( ) == null ) {
				//sounds.playSound( "arm_start", 2.0f );
				leftArmSound.loop( false );
				updatePanels( "left_arm" );
				Timeline t = Timeline.createSequence( );

				t.push( Tween
						.to( leftShoulderSkeleton, PlatformAccessor.LOCAL_ROT,
								20f ).ease( TweenEquations.easeInOutQuad )
						.target( ( -Util.PI / 2 ) ).delay( 1f ).start( ) );

				leftShoulderSkeleton.addMover( new TimelineTweenMover( t
						.start( ) ) );

				// activate anchor
				leftShoulderSkeleton.anchors.get( 0 ).activate( );
			} else if (leftShoulderSkeleton.anchors.get( 0 ).activated){
				if (leftShoulderSkeleton.isTimeLineMoverFinished( )){
					// deactivate anchor
					leftArmSound.stop( false );
					sounds.playSound( "applause_action", 1.0f );
					leftShoulderSkeleton.anchors.get( 0 ).deactivate( );
				}
			}
		}
		if ( powerSwitch9.isTurnedOn( ) && powerSwitch10.isTurnedOn( ) ) {
			Skeleton rightElbowSkeleton = ( Skeleton ) LevelFactory.entities
					.get( "rightElbowSkeleton" );
			Skeleton rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
					.get( "rightShoulderSkeleton" );

			if ( rightElbowSkeleton.currentMover( ) == null ) {
				//sounds.playSound( "arm_start", 2.0f );
				rightArmSound.loop( false );
				updatePanels( "right_arm" );
				Timeline t = Timeline.createSequence( );

				t.push( Tween
						.to( rightElbowSkeleton, PlatformAccessor.LOCAL_ROT,
								10f ).ease( TweenEquations.easeInOutQuad )
						.target( ( Util.PI / 2 ) ).delay( 0f ).start( ) );

				rightElbowSkeleton
						.addMover( new TimelineTweenMover( t.start( ) ) );

				rightElbowSkeleton.anchors.get( 0 ).activate( );

			} else if (rightElbowSkeleton.anchors.get( 0 ).activated){
				if (rightElbowSkeleton.isTimeLineMoverFinished( )){
					// deactivate anchor
					rightArmSound.stop( false );
					rightArmSound2.loop( false );
					//sounds.playSound( "arm_end", 5.0f );
					rightElbowSkeleton.anchors.get( 0 ).deactivate( );
				}
			}

			if ( rightShoulderSkeleton.currentMover( ) == null
					&& rightElbowSkeleton.isTimeLineMoverFinished( ) ) {
				Timeline t2 = Timeline.createSequence( );
				t2.delay( 5f );
				t2.push( Tween
						.to( rightShoulderSkeleton, PlatformAccessor.LOCAL_ROT,
								10f ).ease( TweenEquations.easeInOutQuad )
						.target( ( Util.PI / 2 ) ).delay( 0f ).start( ) );

				rightShoulderSkeleton.addMover( new TimelineTweenMover( t2
						.start( ) ) );

				rightShoulderSkeleton.anchors.get( 0 ).activate( );

			} else if ( rightShoulderSkeleton.anchors.get( 0 ).activated ) {
				if (rightShoulderSkeleton.isTimeLineMoverFinished( )){
					// deactivate anchor
					rightArmSound2.stop( false );
					sounds.playSound( "applause_action", 1.0f );
					rightShoulderSkeleton.anchors.get( 0 ).deactivate( );
				}
			}
		}

		if ( powerSwitch3.isTurnedOn( ) && powerSwitch4.isTurnedOn( )
				&& !chestSteamTriggered ) {
			updatePanels( "chest" );

			chestSteamTriggered = true;
			EventTriggerBuilder etb = new EventTriggerBuilder( level.world );

			etb.name( "chestPuzzle_event_anchor" ).rectangle( ).height( 400f )
					.width( 600f ).position( new Vector2( 570, 4000 ) );

			Anchor anchor = chestPuzzleScrew2.anchors.get( 0 );
			etb.beginAction( new AnchorActivateAction( anchor ) );
			etb.endAction( new AnchorDeactivateAction( anchor ) );

			EventTrigger et = etb.repeatable( ).twoPlayersToActivate( )
					.twoPlayersToDeactivate( ).build( );
			chestSkeleton.addEventTrigger( et );

			Steam steam = new Steam( "steamChest1", new Vector2( 576, 4000 ),
					25, 225, level.world );
			chestSkeleton.addSteam( steam );

			// GET RID OF TEMP STEAM WHEN PARTICLES MATCH THE BODY SIZE
			Steam temp = new Steam( "steamtemp1", new Vector2( 576, 4000 ), 25,
					120, level.world );
			temp.setTempCollision( false );
			chestSkeleton.addSteam( temp );

			Steam temp2 = new Steam( "steamtemp2", new Vector2( 576, 4100 ),
					25, 120, level.world );
			temp2.setTempCollision( false );
			chestSkeleton.addSteam( temp );

			// Steam steam2 = new Steam( "steamChest2", new Vector2( 350, 4450
			// ), 25, 120, level.world );
			// // steam2.setLocalRot( 270 * Util.DEG_TO_RAD );
			// chestSkeleton.addSteam(steam2);
		}

		if ( !rLegTriggered && powerSwitch5.isTurnedOn( )
				&& powerSwitch6.isTurnedOn( ) ) {
			// do right leg activation stuff stuff
			rLegTriggered = true;
			updatePanels( "right_leg" );
		}
	}

	private void knee2Objects( ) {

		PuzzleRotateTweenMover m1 = new PuzzleRotateTweenMover( 2, Util.PI / 2,
				true, PuzzleType.ON_OFF_MOVER );
		PuzzleRotateTweenMover m2 = new PuzzleRotateTweenMover( 2,
				-Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );

		PuzzleScrew knee2rotateScrew1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "knee2rotateScrew1" );

		knee2rotateScrew1.puzzleManager.addMover( m1 );

		PuzzleScrew knee2rotateScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "knee2rotateScrew2" );

		knee2rotateScrew2.puzzleManager.addMover( m2 );

	}

	@SuppressWarnings( "unused" )
	private void chestObjects( ) {
		headSkeleton = ( Skeleton ) LevelFactory.entities.get( "headSkeleton" );

		chestSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "chestSkeleton" );
		PuzzleScrew chestScrew1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew5" );
		PuzzleScrew chestScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew6" );
		PuzzleScrew chestScrew3 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew9" );
		PuzzleScrew chestScrew4 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew10" );
		PuzzleScrew chestScrew5 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew7" );

		headEntrancePlatform4 = ( TiledPlatform ) LevelFactory.entities
				.get( "head_entrance_platform_4" );
		headEntrancePlatform4.setActive( false );

		TiledPlatform chestRotatingPlat2 = ( TiledPlatform ) LevelFactory.entities
				.get( "chestRotatePlat2" );

		AnalogRotateMover anlgRot = new AnalogRotateMover( .6f, level.world );

		RotateTweenMover rtm1 = new RotateTweenMover( chestRotatingPlat2, 8f,
				Util.PI, 2f, true );

		chestScrew3.puzzleManager.addMover( anlgRot );
		chestScrew4.puzzleManager.addMover( anlgRot );

		chestScrew3.puzzleManager.addScrew( chestScrew4 );
		chestScrew4.puzzleManager.addScrew( chestScrew3 );

		chestScrew1.puzzleManager.addScrew( chestScrew2 );
		chestScrew1.puzzleManager.addScrew( chestScrew5 );
		chestScrew2.puzzleManager.addScrew( chestScrew1 );
		chestScrew2.puzzleManager.addScrew( chestScrew5 );
		chestScrew5.puzzleManager.addScrew( chestScrew1 );
		chestScrew5.puzzleManager.addScrew( chestScrew2 );

		StructureScrew stuctureScrew1 = ( StructureScrew ) LevelFactory.entities
				.get( "structureScrew1" );

		EventTrigger etGearFall = ( EventTrigger ) LevelFactory.entities
				.get( "et1" );

		// it has the anchor I need when power switches 3-4 are on
		chestPuzzleScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew2" );

		engineSteam = new Steam( "steamChest3", new Vector2( -420, 5050 ), 25,
				120, level.world );
		chestSkeleton.addSteam( engineSteam );

		// Pipe chestPipe3 = ( Pipe ) LevelFactory.entities
		// .get( "chestPipe3" );

		// chestPipe3.setCategoryMask( Util.CATEGORY_PLATFORMS,
		// Util.CATEGORY_PLAYER );

		// Pipe chestPipe3 = ( Pipe ) LevelFactory.entities
		// .get( "chestPipe3" );

		// chestPipe3.setCategoryMask( Util.CATEGORY_PLATFORMS,
		// Util.CATEGORY_PLAYER );

		chestRotatePlat1 = ( TiledPlatform ) LevelFactory.entities
				.get( "chestRotatePlat1" );
		chestRotatePlat1.setActive( true );

		chestRotatePlat3 = ( TiledPlatform ) LevelFactory.entities
				.get( "chestRotatePlat3" );
		chestRotatePlat3.setActive( true );

		headEyebrow1 = ( TiledPlatform ) LevelFactory.entities
				.get( "headEyebrow1" );

		headEyebrow2 = ( TiledPlatform ) LevelFactory.entities
				.get( "headEyebrow2" );

		Skeleton rightKneeTrapDoorSkeleton1 = ( Skeleton ) LevelFactory.entities
				.get( "rightKneeTrapDoorSkeleton1" );
		Skeleton rightKneeTrapDoorSkeleton2 = ( Skeleton ) LevelFactory.entities
				.get( "rightKneeTrapDoorSkeleton2" );

		// rightKneeTrapDoorSkeleton2.setActive( false );
		// rightKneeTrapDoorSkeleton1.setActive( false );

		EventTrigger rightKneeTrapDoorEvent1 = ( EventTrigger ) LevelFactory.entities
				.get( "rightKneeTrapDoorEvent1" );

		rightKneeTrapDoorEvent1.addEntityToTrigger( rightKneeTrapDoorSkeleton1 );
		rightKneeTrapDoorEvent1.actOnEntity = true;
		rightKneeTrapDoorEvent1.setBeginIAction( new RotateTweenAction(
				-Util.PI / 2, 0.8f ) );

		EventTrigger rightKneeTrapDoorEvent2 = ( EventTrigger ) LevelFactory.entities
				.get( "rightKneeTrapDoorEvent2" );

		rightKneeTrapDoorEvent2.addEntityToTrigger( rightKneeTrapDoorSkeleton2 );
		rightKneeTrapDoorEvent2.actOnEntity = true;
		rightKneeTrapDoorEvent2.setBeginIAction( new RotateTweenAction(
				Util.PI / 2, 0.8f ) );

	}

	private void chestDecals( ) {
		// TextureAtlas chest_powerscrew = WereScrewedGame.manager.getAtlas(
		// "chest_pipes_thigh_pipes" );
		@SuppressWarnings( "unused" )
		TextureAtlas chestEnginePipes = WereScrewedGame.manager
				.getAtlas( "knee_chest_in" );
		float scale = 1f / 0.75f;
		Skeleton chestSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "chestSkeleton" );
//		chestSkeleton.addBGDecal( Sprite.scale( chestEnginePipes
//				.createSprite( "chest_powerswitches_pipestoengine" ), scale ),
//				new Vector2( -732, -1300 ) );// -52,-346

		TextureAtlas chest_lower, chest_middle, chest_upper1, chest_upper2;
		chest_lower = WereScrewedGame.manager.getAtlas( "chest_exterior_lower" );
		chest_middle = WereScrewedGame.manager
				.getAtlas( "chest_exterior_middle" );
		chest_upper1 = WereScrewedGame.manager
				.getAtlas( "chest_exterior_upper1" );
		chest_upper2 = WereScrewedGame.manager
				.getAtlas( "chest_exterior_upper2" );

		Vector2 chestPos = new Vector2( -2320, -1875 );
		// 2625
		Sprite s;

		// LOWER SECTION
		s = chest_lower.createSprite( "torso1" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 364, 89 ) );

		s = chest_lower.createSprite( "torso2" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 1520, 0 ) );

		s = chest_lower.createSprite( "torso3" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 3143, 89 ) );

		// MIDDLE SECTION
		s = chest_middle.createSprite( "torso4" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 88, 1168 ) );

		s = chest_middle.createSprite( "torso5" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 1520, 1168 ) );

		s = chest_upper1.createSprite( "torso6" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 3143, 1168 ) );

		// UPPER SECTION
		s = chest_upper1.createSprite( "torso7" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 0, 2336 ) );

		s = chest_upper2.createSprite( "torso8" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 1520, 2336 ) );

		s = chest_upper2.createSprite( "torso9" );
		s.setScale( scale );
		chestSkeleton.addFGDecal( s, chestPos.cpy( ).add( 3143, 2336 ) );

		addFGSkeleton( chestSkeleton );
		level.skelFGList.remove( chestSkeleton );
		level.skelFGList.add( chestSkeleton );

		// END FG DECALS

		// INTERIOR DECALS

		// chest_bottomleft_mechanisms75
		/*TextureAtlas chest_gear = WereScrewedGame.manager
				.getAtlas( "chest_pipes_thigh_pipes" );
		chestSkeleton.addBGDecal(
				chest_gear.createSprite( "chest_bottomleft_mechanisms75" ),
				new Vector2( -1800, -1157 ) );

		TextureAtlas chest_interiorAtlas = WereScrewedGame.manager
				.getAtlas( "knee_chest_in" );
		Vector2 chestPipesRightPos = new Vector2( -200, -75 );
		chestSkeleton
				.addBGDecalBack(
						Sprite.scale( chest_interiorAtlas
								.createSprite( "lower-right-chest" ), scale ),
						new Vector2( 450, -1200 ).add( chestPipesRightPos ) );

		chestSkeleton
				.addBGDecalBack(
						Sprite.scale( chest_interiorAtlas
								.createSprite( "upper-right-chest" ), scale ),
						new Vector2( 370, -117 ).add( chestPipesRightPos ) );*/
		
		Vector2 chestInerdsPos = new Vector2(-2300,-90);
		scale = 1f/.66f;
		TextureAtlas chest1 = WereScrewedGame.manager.getAtlas( "knee_chest_in" );
		chestSkeleton.addBGDecal( Sprite.scale(chest1.createSprite( "chest1" ),scale), chestInerdsPos.cpy() );
		TextureAtlas chest23 = WereScrewedGame.manager.getAtlas( "chest2" );
		chestSkeleton.addBGDecal( Sprite.scale(chest23.createSprite( "chest2" ),scale), chestInerdsPos.cpy().add( 2278,0 ) );
		chestSkeleton.addBGDecal( Sprite.scale(chest23.createSprite( "chest3" ),scale),chestInerdsPos.cpy().add( -365,-1614 ) );
		
		TextureAtlas chest4 = WereScrewedGame.manager.getAtlas( "head_right" );
		chestSkeleton.addBGDecal( Sprite.scale(chest4.createSprite( "chest4" ),scale), chestInerdsPos.cpy().add(2278-365,-1614) );
		chestSkeleton.bgSprite = null;
		
		// TextureAtlas chest2 = WereScrewedGame.manager.getAtlas("chest2");
		// s = chest2.createSprite(
		// "chest_upperleftandtop_mechanismS_ROUGHwithnotes" );
		// s.setScale( 1f/0.75f );
		// chestSkeleton.addBGDecal( s, new Vector2(-2050,525) );

	}

	private void leftArm( ) {
		float scale = 1f / .75f;
		leftArmScrew = ( PuzzleScrew ) LevelFactory.entities
				.get( "leftShoulderPuzzleScrew1" );

		leftShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "leftShoulderSkeleton" );

		leftShoulderSideHatch = ( Platform ) LevelFactory.entities
				.get( "leftShoulderTopHatch1" );

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( leftShoulderSideHatch.body, leftShoulderSkeleton.body,
				new Vector2( -1748, 5980 ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );

		TextureAtlas left_arm = WereScrewedGame.manager
				.getAtlas( "left-arm-internal" );
		Vector2 pos = new Vector2( -526, -3314.66f );
		leftShoulderSkeleton.addBGDecal( Sprite.scale(
				left_arm.createSprite( "left-arm-bottom" ), scale ), pos );
		leftShoulderSkeleton.addBGDecal( Sprite.scale(
				left_arm.createSprite( "left-arm-top" ), scale, scale ), pos
				.cpy( ).add( 0, 1916 ) );
		// 714,1437

		leftShoulderSkeleton.bgSprite = null;
		
		Skeleton handSkeleton = new Skeleton( "left-hand-skele", leftShoulderSkeleton.getPositionPixel( ) , null, level.world );
		
		//All in one line cus I'm high level like that:
		Sprite s = Sprite.scale( 
				WereScrewedGame.manager.getAtlas( "head_right" )
				.createSprite( "hand" ),scale);
		handSkeleton.addBGDecal(s, 
				pos.cpy( ).add( -65,-1300 ) );
		handSkeleton.setFgFade( false );
		addBGSkeletonBack( handSkeleton );
		leftShoulderSkeleton.addSkeleton( handSkeleton );

		addFGSkeleton( leftShoulderSkeleton );
		addBGSkeleton( leftShoulderSkeleton );
	}

	private void rightArm( ) {
		rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "rightShoulderSkeleton" );

		rightArmDoor = ( Platform ) LevelFactory.entities
				.get( "rightShoulderTopHatch1" );

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( rightArmDoor.body, rightShoulderSkeleton.body,
				new Vector2( 2851f, 5904f ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );
	}

	private void buildEngineHeart( Vector2 posPix ) {
		Skeleton engineSkeleton = new Skeleton( "engineSkeleton", posPix, null,
				level.world );
		level.root.addSkeleton( engineSkeleton );
		engineSkeleton.setFgFade( false );
		int pistonDistanceApart = 280;
		float engineSpeed = 2.5f;

		PlatformBuilder platBuilder = new PlatformBuilder( level.world );
		Platform chestEngine = platBuilder.name( "chestEngine" )
				.position( -200, 5100 ).texture( null ).type( "chestEngine" )
				.buildComplexPlatform( );
		chestEngine.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_PLAYER );

		engineSkeleton.addPlatform( chestEngine );

		TextureAtlas engineAtlas = WereScrewedGame.manager.getAtlas( "engine" );
		Vector2 decalPos = engineSkeleton.getPositionPixel( ).sub( posPix )
				.add( -230, -360 );
		engineSkeleton.addBGDecal( engineAtlas.createSprite( "chest-engine" ),
				new Vector2( decalPos ) );
		addFGEntity( engineSkeleton );

		for ( int i = 0; i < 3; ++i ) {
			buildPiston( engineSkeleton, engineAtlas,
					posPix.cpy( ).add( pistonDistanceApart * i, 0 ), i,
					engineSpeed );
		}
	}

	private void buildPiston( Skeleton engineSkeleton,
			TextureAtlas engineAtlas, Vector2 posPix, int index,
			float engineSpeed ) {
		Vector2 posMeter = posPix.cpy( ).mul( Util.PIXEL_TO_BOX );
		// Build wheel
		Sprite wheelSprite = engineAtlas.createSprite( "wheel" );
		float radiusPix = wheelSprite.getWidth( ) / 2;
		float radiusMeter = radiusPix * Util.PIXEL_TO_BOX;
		Platform wheel1 = buildWheel( posPix.cpy( ), radiusMeter );
		// Attach wheel decal

		engineSkeleton.addPlatform( wheel1 );
		// Make wheel rotate
		new RevoluteJointBuilder( level.world ).entityA( engineSkeleton )
				.entityB( wheel1 ).motor( true ).motorSpeed( engineSpeed )
				.maxTorque( 5000 ).build( );

		// setup for building girder
		Sprite girderSprite = engineAtlas.createSprite( "girder0" );
		float girderInset = 0.97f;
		float pistonDistApartMetre = girderSprite.getHeight( ) * girderInset
				* Util.PIXEL_TO_BOX;
		Sprite wheelBolt = engineAtlas.createSprite( "bolt0" );
		// Build GIRDER!
		float targetRadiusOnWheelMeter = ( radiusPix - wheelBolt.getHeight( ) / 3 )
				* Util.PIXEL_TO_BOX;
		boolean isDown = ( index % 2 == 0 );

		Vector2 wheelJointPosMeter = new Vector2( posMeter );
		if ( isDown ) {
			wheelJointPosMeter.sub( 0, targetRadiusOnWheelMeter );
		} else {
			wheelJointPosMeter.add( 0, targetRadiusOnWheelMeter );
		}
		// Build girder!!
		Platform girder1 = buildGirder( girderSprite, wheelJointPosMeter,
				pistonDistApartMetre );
		engineSkeleton.addPlatform( girder1 );

		// Build piston!!
		Vector2 pistonJointPosMeter = wheelJointPosMeter.cpy( ).sub( 0,
				pistonDistApartMetre );

		Vector2 finalPos = pistonJointPosMeter.cpy( ).mul( Util.BOX_TO_PIXEL );
		finalPos = finalPos.sub( 58f, 90f );
		PlatformBuilder pBuilder = new PlatformBuilder( level.world )
				.name( "piston" ).position( finalPos ).texture( null )
				.dynamic( );

		if ( index == 0 ) {
			pBuilder.type( EntityDef.getDefinition( "pistonLeft" ) );
		} else if ( index == 1 ) {
			pBuilder.type( EntityDef.getDefinition( "pistonMiddle" ) );
		} else {
			pBuilder.type( EntityDef.getDefinition( "pistonRight" ) );
		}

		Platform piston = pBuilder.buildComplexPlatform( );
		piston.setCrushing( true );
		piston.setVisible( false );// only draw decals, not tiled body!
		engineSkeleton.addDynamicPlatform( piston );

		// Setup prismatic joint for piston!
		new PrismaticJointBuilder( level.world ).bodyA( engineSkeleton )
				.bodyB( piston ).axis( new Vector2( 0, 1 ) ).build( );

		// setup bolt on wheel image
		Vector2 boltPosPix = new Vector2( -wheelBolt.getWidth( ) / 2, 0 );
		if ( isDown ) {
			boltPosPix.sub( 0, targetRadiusOnWheelMeter * Util.BOX_TO_PIXEL
					+ wheelBolt.getHeight( ) / 2 );
		} else {
			boltPosPix.add( 0, targetRadiusOnWheelMeter * Util.BOX_TO_PIXEL
					- wheelBolt.getHeight( ) / 2 );
		}

		// Bolt everything together
		RevoluteJointBuilder rBuilder = new RevoluteJointBuilder( level.world )
				.collideConnected( false );
		rBuilder.entityA( girder1 ).entityB( wheel1 )
				.anchor( wheelJointPosMeter ).build( );
		rBuilder.entityB( piston ).anchor( pistonJointPosMeter ).build( );// entity
																			// a
																			// is
																			// still
																			// girder

		engineSkeleton.addPlatforms( girder1 );

		Sprite boltSprite;
		Sprite pistonSprite;
		switch ( index ) {
		case 0:
			pistonSprite = engineAtlas.createSprite( "piston_left" );
			boltSprite = engineAtlas.createSprite( "bolt" + ( index + 1 ) );
			break;
		case 1:
			pistonSprite = engineAtlas.createSprite( "piston_middle" );
			boltSprite = engineAtlas.createSprite( "bolt" + ( index + 1 ) );
			break;
		case 2:
			pistonSprite = engineAtlas.createSprite( "piston_right" );
			boltSprite = engineAtlas.createSprite( "bolt" + ( index + 1 ) );
			break;
		default:
			pistonSprite = engineAtlas.createSprite( "piston_middle" );
			boltSprite = engineAtlas.createSprite( "bolt1" );
			break;
		}

		// Draw order:
		wheel1.addFGDecalBack(
				wheelSprite,
				new Vector2( -wheelSprite.getWidth( ) / 2, -wheelSprite
						.getHeight( ) / 2 ) );
		girder1.addFGDecalBack(
				girderSprite,
				new Vector2( -girderSprite.getWidth( ) / 2, -girderSprite
						.getHeight( ) / 2 ) );

		piston.addFGDecal( pistonSprite, Vector2.Zero );
		piston.addFGDecal( boltSprite, new Vector2( 32f, 48f ) );
		wheel1.addFGDecal( wheelBolt, boltPosPix );

		addFGEntity( wheel1 );
		addFGEntity( piston );
		addFGEntity( girder1 );
	}

	@SuppressWarnings( "unused" )
	private Platform buildGirder( Sprite girder, Vector2 topMeter,
			float pistonDistApartMeter ) {

		Vector2 pos = topMeter.cpy( ).sub( 0, pistonDistApartMeter / 2 );

		BodyDef girderBodyDef = new BodyDef( );
		girderBodyDef.type = BodyType.DynamicBody;
		girderBodyDef.position.set( pos );
		girderBodyDef.fixedRotation = false;
		girderBodyDef.gravityScale = 1f; // doesn't need gravity
		Body girderBody = level.world.createBody( girderBodyDef );

		PolygonShape girderShape = new PolygonShape( );
		Vector2 shape = new Vector2( 0.01f, pistonDistApartMeter / 2 );
		float distPix = pistonDistApartMeter * Util.BOX_TO_PIXEL;
		girderShape.setAsBox( shape.x, shape.y );
		FixtureDef wheelFixture = new FixtureDef( );
		// wheelFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// wheelFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		wheelFixture.shape = girderShape;
		wheelFixture.density = 0.1f;
		girderBody.createFixture( wheelFixture );

		Platform out = new Platform( "girder", pos, null, level.world );
		out.body = girderBody;
		return out;

	}

	private Platform buildWheel( Vector2 pos, float radiusMeter ) {
		BodyDef wheelBodyDef = new BodyDef( );
		wheelBodyDef.type = BodyType.DynamicBody;
		wheelBodyDef.position.set( pos.cpy( ).mul( Util.PIXEL_TO_BOX ) );
		wheelBodyDef.fixedRotation = false;
		wheelBodyDef.gravityScale = 0.07f;
		Body wheelBody = level.world.createBody( wheelBodyDef );

		CircleShape wheelShape = new CircleShape( );
		wheelShape.setRadius( radiusMeter );
		FixtureDef wheelFixture = new FixtureDef( );
		// wheelFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// wheelFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		wheelFixture.shape = wheelShape;
		wheelFixture.density = 0.1f;
		wheelBody.createFixture( wheelFixture );

		wheelShape.dispose( );

		Platform out = new Platform( "wheel", pos, null, level.world );
		out.body = wheelBody;
		// RotateTweenMover m = new RotateTweenMover(out, 4, Util.PI*2, 0, false
		// );
		// out.setMoverAtCurrentState( new RotateTweenMover( out ) );

		return out;
	}

	@SuppressWarnings( "unused" )
	private void powerSwitch( ) {
		Platform fallingGear1 = new PlatformBuilder( level.world )
				.name( "fallingGear1" )
				.type( EntityDef.getDefinition( "gearSmall" ) )
				.position( 0.0f, 400.0f )
				.texture( EntityDef.getDefinition( "gearSmall" ).getTexture( ) )
				.solid( true ).dynamic( ).buildComplexPlatform( );
		chestSkeleton.addDynamicPlatform( fallingGear1 );

		fallingGear1.addJointToSkeleton( chestSkeleton );

		PowerSwitch ps = new PowerSwitch( "power1", new Vector2( 0, 100 ),
				level.world );
		ps.addEntityToTrigger( fallingGear1 );
		ps.actOnEntity = true;
		ps.setBeginIAction( new DestroyPlatformJointAction( ) );
		ps.setEndIAction( new DestroyPlatformJointAction( ) );
		// AnchorDeactivateAction

		chestSkeleton.addEventTrigger( ps );
	}

	private void initPanels( ) {
		int numPanels = 7;
		@SuppressWarnings( "unused" )
		String panelAtlas = "alphabot-panel";
		panels = new Array< Panel >( numPanels );
		Panel p;
		// get panels 1-5 (possibly 6, but not yet)
		for ( int i = 1; i <= numPanels; ++i ) {
			p = ( Panel ) LevelFactory.entities.get( "panel" + i );
			p.setPanelSprite( "alphabot-panel_off" );
			panels.add( p );
		}

		// Panel p = new Panel( kneeSkeleton.getPositionPixel( ), level.world,
		// panelAtlas, "alphabot-panel_off" );
		// kneeSkeleton.addPlatform( p );
		// panels.add(p);

	}

	private void updatePanels( String activatedPanel ) {
		String panelName = "alphabot-panel";

		if ( activatedPanel.equals( "left_leg" ) ) {
			panelName = "alphabot-panel_left_leg";
		} else if ( activatedPanel.equals( "chest" ) ) {
			panelName = "alphabot-panel_chest";
		} else {
			// rleg
			if ( powerSwitch5.isTurnedOn( ) && powerSwitch6.isTurnedOn( ) ) {
				panelName = panelName + "_rleg";
				if(testOnce){
					initFireballEnemy(new Vector2(1625, 600));
					testOnce = false;
				}
			}

			// right arm
			if ( powerSwitch9.isTurnedOn( ) && powerSwitch10.isTurnedOn( ) ) {
				panelName = panelName + "_rarm";
			}

			// left arm
			if ( powerSwitch7.isTurnedOn( ) && powerSwitch8.isTurnedOn( ) ) {
				panelName = panelName + "_larm";
			}
		}
		for ( Panel p : panels ) {
			p.setPanelSprite( panelName );
		}
	}

	private void headDecals( ) {
		headSkeleton = ( Skeleton ) LevelFactory.entities.get( "headSkeleton" );

		TextureAtlas head_left = WereScrewedGame.manager.getAtlas( "head_left" );
		TextureAtlas head_right = WereScrewedGame.manager
				.getAtlas( "head_right" );

		Vector2 pos = new Vector2( -1475, -590 );
		Sprite s;
		float scale = 1f / .75f;
		s = head_left.createSprite( "head_left" );
		s.setScale( scale );
		headSkeleton.addFGDecal( s, pos.cpy( ) );
		s = head_right.createSprite( "head_right" );
		s.setScale( scale );
		headSkeleton.addFGDecal( s, pos.cpy( ).add( s.getWidth()*scale-5, 0 ) );
		addBGEntity( headSkeleton );
		addFGEntity( headSkeleton );

	}

	private void leftArmDecal( ) {
		Skeleton leftShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "leftShoulderSkeleton" );
		float scale = 1f / 0.75f;
		Sprite s;
		int x = 0, y = -1;
		// upper arm decals
		TextureAtlas arm_decals = WereScrewedGame.manager.getAtlas( "arm_ex" );
		Vector2 armPos = new Vector2( -520, -128 );
		x = 0;
		y = -1;
		for ( int i = 0; i < 6; ++i ) {
			// 515,710
			x = ( i % 2 );
			if ( x == 0 )
				++y;
			s = arm_decals.createSprite( "upperarm_exterior" + ( i + 1 ) );
			s.setScale( scale );
			leftShoulderSkeleton.addFGDecal( s,
					armPos.cpy( ).add( 479 * x, -683 * y+i ) );
		}

		// forearm decals
		TextureAtlas elbow_decals = WereScrewedGame.manager.getAtlas( "arm_ex" );
		Vector2 elbowPos = new Vector2( -555, 232 );

		for ( int i = 0; i < 6; ++i ) {
			// 515,710
			x = ( i % 2 );
			if ( x == 0 )
				++y;
			s = elbow_decals.createSprite( "forearmandelbow_exterior"
					+ ( i + 1 ) );
			s.setScale( scale );
			// s.setScale( -1,1 );
			leftShoulderSkeleton.addFGDecal( s,
					elbowPos.cpy( ).add( 513 * x, -710 * y+i ) );
		}

	}

	private void rightArmDecal( ) {
		Skeleton rightElbowSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "rightElbowSkeleton" );
		Skeleton rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "rightShoulderSkeleton" );
		Sprite s;
		// forearm decals
		TextureAtlas elbow_decals = WereScrewedGame.manager.getAtlas( "arm_ex" );
		Vector2 elbowPos = new Vector2( -520, -278 );
		float scale = 1f / .75f;
		int x = 0, y = -1;
		for ( int i = 0; i < 6; ++i ) {
			// 515,710
			x = i % 2;
			if ( x == 0 )
				++y;
			s = elbow_decals.createSprite( "forearmandelbow_exterior"
					+ ( i + 1 ) );
			s.setScale( scale );
			rightElbowSkeleton.addFGDecal( s,
					elbowPos.cpy( ).add( 513 * x, -710 * y+i ) );
		}

		// upper arm decals
		TextureAtlas arm_decals = WereScrewedGame.manager.getAtlas( "arm_ex" );
		Vector2 armPos = new Vector2( -500, -128 );
		x = 0;
		y = -1;
		for ( int i = 0; i < 6; ++i ) {
			// 515,710
			x = i % 2;
			if ( x == 0 )
				++y;
			rightShoulderSkeleton.addFGDecal(
					Sprite.scale(
							arm_decals.createSprite( "upperarm_exterior"
									+ ( i + 1 ) ), scale ),
					armPos.cpy( ).add( 479 * x, -683 * y+i ) );
		}

		addFGSkeleton( rightElbowSkeleton );
		addFGSkeleton( rightShoulderSkeleton );

		addBGSkeleton( rightElbowSkeleton );
		addBGSkeleton( rightShoulderSkeleton );
		
		
		
		Skeleton handSkeleton = new Skeleton( "right-hand-skele", rightElbowSkeleton.getPositionPixel( ).add(0,700) , null, level.world );
		
		//All in one line cus I'm high level like that:
		s = Sprite.scale( 
				WereScrewedGame.manager.getAtlas( "head_right" )
				.createSprite( "hand" ),scale);
		s.setScale( -scale,scale );
		handSkeleton.addBGDecal(s, 
				new Vector2( s.getWidth( )-320,-3700 ) );
		handSkeleton.setFgFade( false );
		addBGSkeletonBack( handSkeleton );
		rightElbowSkeleton.addSkeleton( handSkeleton );

		addFGSkeleton( leftShoulderSkeleton );
		addBGSkeleton( leftShoulderSkeleton );

	}
	
	/**
	 *  shoots off the fireworks on top of alphabot
	 */
	private void shootFireworks(){
		Skeleton fw;
		for (int i = 1; i < 16; i++){
			if(generator.nextInt(2) == 1){
				sounds.playSound( "fireworks" , 0.2f + 0.8f * generator.nextFloat());
				fw = ( Skeleton ) LevelFactory.entities.get( "firework_skeleton" + i );
				fw.addFrontParticleEffect( "fireworks/firework" + ((i % 5) + 1) , true , true ).start();
			}
		}
	}
	
	/**
	 * taken from dragonScreen
	 * @param pos vector2
	 */
	private void initFireballEnemy(Vector2 pos){
		
		int n= 10, h = 140;
		
		//build a little cage for the fireball
	
		
		fireballEmitter = new EntityParticleEmitter( "bolt emitter",
				new Vector2( pos.cpy( ).add(0,n*h) ),
				new Vector2(),
				 level.world, true );
		for(int i = 0; i < 1; ++i ){
			fireballEmitter.addParticle( createBoltEnemy( pos.cpy( ), i ), 5, 0, i*5 );
		}
		level.root.addLooseEntity( fireballEmitter );
	}
	Enemy createBoltEnemy(Vector2 pos, int index){
		Enemy hotbolt = new Enemy( "hot-bolt"+index, pos,25, level.world, true );
		hotbolt.addMover( new DirectionFlipMover( false, 0.002f, hotbolt, 1f, .04f ) );
		addBGEntity( hotbolt );
		return hotbolt;
	}
}
