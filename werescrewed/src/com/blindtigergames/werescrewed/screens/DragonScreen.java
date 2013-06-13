package com.blindtigergames.werescrewed.screens;

import java.util.Random;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.CannonLaunchAction;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.action.RotateTweenAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Enemy;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.hazard.MouthFire;
import com.blindtigergames.werescrewed.entity.mover.DirectionFlipMover;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.particles.EntityParticleEmitter;
import com.blindtigergames.werescrewed.entity.platforms.Pipe;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;
import com.blindtigergames.werescrewed.util.Util;

public class DragonScreen extends Screen {

	PuzzleScrew puzzleScrewBalloon1;
	Platform balloon1;
	Skeleton balloon1_super, bodyRoomRotateSkeleton, headSkeleton;
	PowerSwitch tail3Switch1, tail3Switch2, tail3Switch3, bodyPowerSwitch1,
			bodyPowerSwitch3, dragonBrainSwitch, dragonBrainSwitch2, powerSwitchBalloon1;
	Platform dragonBrain;
	RevoluteJoint bodyRoomJoint;
	EntityParticleEmitter fireballEmitter, brainEmitter1, brainEmitter2,
			brainEmitter3, brainEmitter4;
	StructureScrew tail1Left, tail1Right, tail2Left, tail2Right, tail3Left,
			tail3Right;
	StructureScrew jawStructureScrew;
	Skeleton jaw_skeleton;
	MouthFire mouthFire;
	boolean headEvent = false;
	int headEventTimer = 1;
	
	boolean balloon2_ss1Unscrewed = false;
	boolean balloon2_ss2Unscrewed = false;
	boolean tail_ssLUnscrewed = false;
	boolean tail_ssRUnscrewed = false;
	boolean tail2_ssLUnscrewed = false;
	boolean tail2_ssRUnscrewed = false;
	boolean tail3_ssLUnscrewed = false;
	boolean tail3_ssRUnscrewed = false;
	boolean tail2ToTail3Unscrewed = false;
	float mouthFireTimer=0;
	final float mouthFireDelay, mouthFireTotalTime;
	boolean mouthFireTriggered = false;
	boolean oneTimeTail2Fire1 = true, headAnchorEvent = false;;
	// the numbers here correspond to gleed numbers
	Fire tail3Fire2, tail3Fire3, tail3Fire4, tail3Fire5, tail3Fire6;

	public DragonScreen( ) {
		super( );
		String filename = "data/levels/dragonlevel.xml";
		level = new LevelFactory( ).load( filename );

		level.player1.setThatGuy( level.player2 );
		level.player2.setThatGuy( level.player1 );
		
		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
		EventTrigger removeTrigger = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 30500 )
				.position( new Vector2( 15500, -1500 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		level.root.addEventTrigger( removeTrigger );
		
		EventTrigger removeTrigger2 = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 250 )
				.position( new Vector2( 450, 1000 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger2.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_PLAYER );
		level.root.addEventTrigger( removeTrigger2 );
		
		EventTrigger removeTrigger3 = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 2000 )
				.position( new Vector2( 0, -2500 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger3.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_PLAYER );
		level.root.addEventTrigger( removeTrigger3 );

		buildBalloon( );

		groundDecals( );
		initPuzzleScrews( );
		tail3Pipes( );
		bodyDecals( );
		bodySkeletons( );
		buildAllCannons( );
		flamePlatformDecals( );
		buildBackground( );
		tail1Decals( );
		tail2Decals( );
		tail3Decals( );
		getTailStructureScrews( );
		initFireballEnemy( );

		introBalloonDecals();
		
		//********** Mouth fire stuff *******/
		mouthFire = new MouthFire( "mouth-fire", new Vector2(25000, 900), new Vector2(32000, 75),
				4f, 100f, 800f, level.world);
		mouthFire.dontPutToSleep=true;
		Skeleton head_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "head_sub_skeleton1" );
		head_skeleton.addHazard( mouthFire );
		jawStructureScrew = ( StructureScrew ) LevelFactory.entities
				.get( "jaw_structure_screw" );

		jaw_skeleton = ( Skeleton ) LevelFactory.entities.get( "jaw_skeleton" );
		Timeline t = Timeline.createSequence( );
		mouthFireDelay = 8f;
		mouthFireTotalTime=22f;
		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 6f )
				.ease( TweenEquations.easeNone ).target( -Util.PI / 20 )
				.start( ).delay( 2f ).setCallback( new ShootFireCallback( ) ) );


		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 6f )
				.ease( TweenEquations.easeNone ).target( 0 ).delay( 8f )
				.setCallback( new PlayJawCloseCallback( ) ).start( ) );

		t.repeat( Tween.INFINITY, 0f );
		jaw_skeleton.addMover( new TimelineTweenMover( t.start( ) ) );

		
		headDecals( );
		initEyebrow( );

	}

	@Override
	public void load( ) {
		super.load( );
		if (bgm == null){
			bgm = Gdx.audio.newMusic(Gdx.files.internal("data/levels/dragon/riding.mp3"));
		}
		if (sounds == null){
			sounds = new SoundManager();
			SoundRef calmRoar = sounds.getSound( "roar_calm",  WereScrewedGame.dirHandle + "/levels/dragon/sounds/dragon_roar_calm.ogg");
			calmRoar.setRange( 80000 );
			SoundRef angryRoar = sounds.getSound( "roar_angry", WereScrewedGame.dirHandle + "/levels/dragon/sounds/dragon_roar_angry.ogg");
			angryRoar.setRange( 8000 );
			angryRoar.setInternalVolume( 0.75f );
			sounds.getSound( "jaw_close", WereScrewedGame.dirHandle + "/levels/dragon/sounds/jawClose.ogg" ).setRange( 8000 );
			sounds.loadMultiSound( "fire_breath", 0, 
									1.0f, WereScrewedGame.dirHandle + "/levels/dragon/sounds/fire-breath-start.ogg", 
									0.9f, WereScrewedGame.dirHandle + "/levels/dragon/sounds/fire-breath-loop.ogg", 
									1.2f, WereScrewedGame.dirHandle + "/levels/dragon/sounds/fire-breath-end.ogg", 
									-0.1f );
			//sounds.getSound( "jaw_open",WereScrewedGame.dirHandle + "/levels/dragon/sounds/cannon.ogg" );
		}

		Texture transition = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle + "/transitions/trans-gear.png",
				Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
	}

	void buildBalloon( ) {
		balloon1 = ( Platform ) LevelFactory.entities.get( "balloon1" );

		// Platform balloon2 = ( Platform ) LevelFactory.entities.get(
		// "balloon2" );
		// Platform balloon3 = (Platform) LevelFactory.entities.get( "balloon3"
		// );
		// Platform balloon4 = (Platform) LevelFactory.entities.get( "balloon4"
		// );

		Platform tail1Balloon = ( Platform ) LevelFactory.entities
				.get( "tail1_balloon1" );
		Platform tail2Balloon = ( Platform ) LevelFactory.entities
				.get( "tail2_balloon1" );
		Platform tail3Balloon = ( Platform ) LevelFactory.entities
				.get( "tail3_balloon1" );

		Skeleton balloon1_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "balloon1_skeleton" );
		balloon1_super = ( Skeleton ) LevelFactory.entities
				.get( "balloon1_super" );

		powerSwitchBalloon1 = ( PowerSwitch ) LevelFactory.entities
				.get( "switch_balloon1" );

		tail1Balloon
				.addMover( balloonMover( tail1Balloon, 200, Util.PI / 32, 0 ) );
		tail2Balloon.addMover( balloonMover( tail2Balloon, 250, 0, 2 ) );
		tail3Balloon.addMover( balloonMover( tail3Balloon, 300, 0, 4 ) );

		// balloon4.addMover( balloonMover(balloon4, 600, 0, 0) );
		// balloon3_skeleton.addMover( balloonMover(balloon3_skeleton, 600,
		// Util.PI/8, 4) );
		// balloon4_skeleton.addMover( balloonMover(balloon4_skeleton, 700,
		// Util.PI/16, 2) );
		// balloon5_skeleton.addMover( balloonMover(balloon5_skeleton, 800,
		// Util.PI/32, 0) );
//		bgm = WereScrewedGame.manager.get( WereScrewedGame.dirHandle.path( )
//				+ "/levels/dragon/riding.mp3", Music.class );
	}

	float time;
	boolean restart = false;
	int bodyRoomAngle = 0;

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		// time += deltaTime * 1000;
		bodyRoomAngle = ( int ) Math
				.abs( ( bodyRoomRotateSkeleton.getAngle( ) * Util.RAD_TO_DEG ) % 360 );

		if ( time > 5000 ) {
			// balloon2.body.applyForce( new Vector2(0f, 100f),
			// balloon2.body.getWorldCenter( ));
			time = 0;
		}

		if ( !bodyPowerSwitch3.isTurnedOn( ) ) {
			bodyPowerSwitch3.setActive( false );
			bodyRoomJoint.setMotorSpeed( -0.1f );
			if ( ( bodyRoomAngle == 359 || bodyRoomAngle == 90
					|| bodyRoomAngle == 180 || bodyRoomAngle == 270 )
					&& bodyRoomJoint.isMotorEnabled( ) ) {
				bodyRoomRotateSkeleton.body.setAngularVelocity( 0f );
				bodyRoomJoint.setMotorSpeed( 0.0f );
				bodyRoomJoint.setMaxMotorTorque( 0f );
				bodyRoomJoint.enableMotor( false );

				Fire bf7 = ( Fire ) LevelFactory.entities.get( "body_fire7" );
				Fire bf8 = ( Fire ) LevelFactory.entities.get( "body_fire8" );
				Fire bf9 = ( Fire ) LevelFactory.entities.get( "body_fire9" );
				Fire bf10 = ( Fire ) LevelFactory.entities.get( "body_fire10" );
				Fire bf11 = ( Fire ) LevelFactory.entities.get( "body_fire11" );
				Fire bf12 = ( Fire ) LevelFactory.entities.get( "body_fire12" );

				bf7.activeHazard = false;
				bf8.activeHazard = false;
				bf9.activeHazard = false;
				bf10.activeHazard = false;
				bf11.activeHazard = false;
				bf12.activeHazard = false;

			}

		}
		tail3FireEventsUpdate( );

		if ( tail2Left.body == null && tail2Right.body == null ) {

			if ( oneTimeTail2Fire1 ) {
				oneTimeTail2Fire1 = false;
				Fire tail2Fire1 = ( Fire ) LevelFactory.entities
						.get( "tail2_fire1" );
				tail2Fire1.activeHazard = false;
			}
		}
		if ( !bodyPowerSwitch1.isTurnedOn( ) ) {
			fireballEmitter.setActive( false );

		} else {

			fireballEmitter.setActive( true );
		}
		if ( jawStructureScrew != null ) {
			
			if ( jawStructureScrew.getDepth( ) == 0) {
				jaw_skeleton.body.setType( BodyType.DynamicBody );
				headEvent = true;
				sounds.playSound( "roar_angry" );
				CheckPoint checkpointHead = ( CheckPoint ) LevelFactory.entities
						.get( "checkpoint_head" );
				checkpointHead.setRemoveNextStep( );
			}
		}
		
		

		// Zoom out and fade the head skeleton back in so you can see the jaw
		// fall off
		if ( headEvent ) {
			// System.out.println( headEventTimer );

			headEventTimer--;
			if ( headEventTimer == 0 ) {
				headSkeleton.setFade( false );
				headSkeleton.anchors.get( 0 ).setTimer( 200 );
				headSkeleton.anchors.get( 0 ).activate( );
				headEvent = false;
				
				headAnchorEvent = true;

			} else {

				headSkeleton.setFade( true );
			}
		}else if (!headSkeleton.anchors.get( 0 ).activated && headAnchorEvent){
			headSkeleton.setFade( true );
			headAnchorEvent = false;
		}
		

		if ( dragonBrainSwitch.isTurnedOn( ) && dragonBrainSwitch2.isTurnedOn( ) ) {

			if ( dragonBrain.currentMover( ) == null ) {
				sounds.playSound( "roar_angry" );
				Timeline t = Timeline.createSequence( );

				t.push( Tween.to( dragonBrain, PlatformAccessor.LOCAL_ROT, 1f )
						.ease( TweenEquations.easeNone ).target( Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.push( Tween.to( dragonBrain, PlatformAccessor.LOCAL_ROT, 1f )
						.ease( TweenEquations.easeNone ).target( -Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.push( Tween.to( dragonBrain, PlatformAccessor.LOCAL_ROT, 1f )
						.ease( TweenEquations.easeNone ).target( Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.push( Tween.to( dragonBrain, PlatformAccessor.LOCAL_ROT, 1f )
						.ease( TweenEquations.easeNone ).target( -Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.push( Tween.to( dragonBrain, PlatformAccessor.LOCAL_ROT, 1f )
						.ease( TweenEquations.easeNone ).target( 0 ).delay( 0f )
						.start( ) );

				dragonBrain.addMover( new TimelineTweenMover( t.start( ) ) );
			} else {
				if ( dragonBrain.isTimeLineMoverFinished( ) ) {

					transOutEnd = false;

					// You win and goto next screen!!!
					// ScreenManager.getInstance( ).show(
					// ScreenType.LOADING_TROPHY_3 );

				}
			}
		}
		if ( powerSwitchBalloon1.isTurnedOn( ) ) {
			if ( balloon1_super.currentMover( ) == null ) {

				Platform balloon1LeftHatch = ( Platform ) LevelFactory.entities
						.get( "balloon1_left_hatch" );

				Platform balloon1Ledge3 = ( Platform ) LevelFactory.entities
						.get( "balloon1_ledge3" );

				PathBuilder pb = new PathBuilder( );
				pb.begin( balloon1LeftHatch ).target( 0f, -200f, 0.5f ).loops( 0 );
				balloon1LeftHatch.addMover( pb.build( ) );

				PathBuilder pb2 = new PathBuilder( );
				pb2.begin( balloon1Ledge3 ).target( 0f, -175f, 1f ).loops( 0 );
				balloon1Ledge3.addMover( pb2.build( ) );

				Timeline t = Timeline.createSequence( );

				t.beginParallel( );
				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_POS_XY, 8f )
						.delay( 0f ).target( 0, 2100 )
						.ease( TweenEquations.easeNone ).start( ) );

				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						.ease( TweenEquations.easeNone ).target( Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.end( );

				t.beginParallel( );

				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_POS_XY, 8f )
						.delay( 0f ).target( 0, 4200f )
						.ease( TweenEquations.easeNone ).start( ) );

				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						.ease( TweenEquations.easeNone ).target( -Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.end( );

				t.beginSequence( );
				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						.ease( TweenEquations.easeNone ).target( 0 ).delay( 0f )
						.start( ) );
				t.end( );
				balloon1_super.addMover( new TimelineTweenMover( t.start( ) ) );
			}

		}

		level.backgroundBatch.begin( );
		if ( !transInEnd ) {
			drawTransIn( level.backgroundBatch );
		}

		if ( !transOutEnd ) {
			drawTransOut( level.backgroundBatch, ScreenType.LOADING_TROPHY_3 );
		}
		level.backgroundBatch.end( );
	}

	IMover balloonMover( Platform skel, float yPos, float angle, float initPause ) {
		Timeline t = Timeline.createSequence( );

		t.delay( initPause );

		// t.beginParallel( );
		t.push( Tween.to( skel, PlatformAccessor.LOCAL_POS_XY, 8f ).delay( 0f )
				.target( 0, yPos ).ease( TweenEquations.easeInOutQuad ).start( ) );

		// t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 2f )
		// .ease(TweenEquations.easeInOutQuad)
		// .target( angle ).delay( 0f )
		// .start()
		// );
		//
		// t.end( );

		// t.beginSequence( );

		// t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 3f )
		// .ease(TweenEquations.easeNone)
		// .target( 0 ).delay( 0f )
		// .start()
		// );
		// t.end();

		// t.beginParallel( );

		t.push( Tween.to( skel, PlatformAccessor.LOCAL_POS_XY, 8f ).delay( 0f )
				.target( 0, 0f ).ease( TweenEquations.easeInOutQuad ).start( ) );

		// t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 2f )
		// .ease(TweenEquations.easeInOutQuad)
		// .target( -angle ).delay( 0f )
		// .start()
		// );
		//
		// t.end( );

		// t.beginSequence( );
		//
		//
		// t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 3f )
		// .ease(TweenEquations.easeInOutQuad)
		// .target( 0 ).delay( 0f )
		// .start()
		// );
		//
		// t.end( );

		// t.delay( initPause );
		t = t.repeat( Tween.INFINITY, 0f );
		return new TimelineTweenMover( t.start( ) );
	}

	void buildCannon( Skeleton skel, Vector2 pos, int widthPix, int heightPix,
			float power, float delay ) {
		if ( widthPix <= 64 )
			throw new RuntimeException(
					"Cannon width needs to be greater than 64 (2tiles) to work properly" );
		PlatformBuilder pb = new PlatformBuilder( level.world )
				.tileSet( "TilesetTest" );

		Vector2 dim = new Vector2( ( ( int ) ( widthPix / 32 ) ) - 2,
				( ( int ) ( heightPix / 32 ) ) );
		Vector2 left = new Vector2( pos.x - dim.x / 2 * 32 - 16, pos.y - 16
				+ dim.y * 16 );
		Vector2 right = new Vector2( pos.x + dim.x / 2 * 32 + 16, pos.y - 16
				+ dim.y * 16 );
		skel.setFgFade( false );
		Sprite can = WereScrewedGame.manager.getAtlas( "dragon_objects" )
				.createSprite( "cannon-small" );
		skel.addFGDecal( can, new Vector2( -can.getWidth( ) / 2, -64 ) );
		addFGEntityToBack( skel );

		// base
		TiledPlatform p1 = pb.name( "cannon-base" ).dimensions( dim.x, 1 )
				.position( pos.cpy( ) ).buildTilePlatform( );
		p1.setVisible( false );
		skel.addPlatform( p1 );
		// left
		TiledPlatform p2 = pb.name( "cannon-left" ).dimensions( 1, dim.y )
				.position( left.cpy( ) ).buildTilePlatform( );
		p2.setVisible( false );
		skel.addPlatform( p2 );
		// right
		TiledPlatform p3 = pb.name( "cannon-right" ).dimensions( 1, dim.y )
				.position( right.cpy( ) ).buildTilePlatform( );
		p3.setVisible( false );
		skel.addPlatform( p3 );

		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );

		int quarter = ( int ) ( dim.y * 32 / 4 );
		Vector2 eventPos = new Vector2( pos.x, pos.y + 16 + quarter );

		Array< Vector2 > triggerVerts = new Array< Vector2 >( 4 );
		// triggerVerts.add( new Vector2 )

		triggerVerts.add( new Vector2( quarter, -quarter ) );
		triggerVerts.add( new Vector2( quarter, quarter ) );
		triggerVerts.add( new Vector2( -quarter, quarter ) );
		triggerVerts.add( new Vector2( -quarter, -quarter ) );
		triggerVerts.add( new Vector2( -quarter, -quarter ) );

		EventTrigger et = etb.name( "cannon-trigger" ).setVerts( triggerVerts )
				.extraBorder( 0 ).position( eventPos )
				// .addEntity( s )
				.beginAction( new CannonLaunchAction( skel, power, delay ) )
				.repeatable( ).build( );
		skel.addEventTrigger( et );
	}

	private void initPuzzleScrews( ) {
		// PuzzleScrew tail2PuzzleScrew1 = ( PuzzleScrew ) LevelFactory.entities
		// .get( "tail2_puzzle_screw1" );
		// PuzzleScrew tail2PuzzleScrew2 = ( PuzzleScrew ) LevelFactory.entities
		// .get( "tail2_puzzle_screw2" );
		//
		// AnalogRotateMover anlgRot = new AnalogRotateMover( 0.6f, level.world
		// );
		//
		// AnalogRotateMover anlgRot2 = new AnalogRotateMover( 0.6f, level.world
		// );
		//
		// tail2PuzzleScrew1.puzzleManager.addMover( anlgRot );
		// tail2PuzzleScrew2.puzzleManager.addMover( anlgRot );
		//
		// tail2PuzzleScrew1.puzzleManager.addMover( anlgRot2 );
		// tail2PuzzleScrew2.puzzleManager.addMover( anlgRot2 );
		//
		// tail2PuzzleScrew1.puzzleManager.addScrew( tail2PuzzleScrew2 );
		// tail2PuzzleScrew2.puzzleManager.addScrew( tail2PuzzleScrew1 );

		PuzzleScrew tail3PuzzleScrew1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "tail3_puzzle_screw1" );
		PuzzleScrew tail3PuzzleScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "tail3_puzzle_screw2" );

		Platform tail3MoverPlat1 = ( Platform ) LevelFactory.entities
				.get( "tail3_mover_plat1" );
		Platform tail3MoverPlat2 = ( Platform ) LevelFactory.entities
				.get( "tail3_mover_plat2" );

		float distance = 800f;

		LerpMover lm1 = new LerpMover( tail3MoverPlat1.getPositionPixel( ),
				new Vector2( tail3MoverPlat1.getPositionPixel( ).x,
						tail3MoverPlat1.getPositionPixel( ).y + distance ),
				LinearAxis.VERTICAL );

		LerpMover lm2 = new LerpMover( tail3MoverPlat2.getPositionPixel( ),
				new Vector2( tail3MoverPlat2.getPositionPixel( ).x,
						tail3MoverPlat2.getPositionPixel( ).y + distance ),
				LinearAxis.VERTICAL );

		tail3PuzzleScrew1.puzzleManager.addEntity( tail3MoverPlat1 );
		tail3PuzzleScrew1.puzzleManager.addEntity( tail3MoverPlat2 );
		tail3PuzzleScrew2.puzzleManager.addEntity( tail3MoverPlat1 );
		tail3PuzzleScrew2.puzzleManager.addEntity( tail3MoverPlat2 );

		tail3PuzzleScrew1.puzzleManager.addMover( lm1 );
		tail3PuzzleScrew2.puzzleManager.addMover( lm1 );
		tail3PuzzleScrew1.puzzleManager.addMover( lm2 );
		tail3PuzzleScrew2.puzzleManager.addMover( lm2 );

		tail3PuzzleScrew1.puzzleManager.addScrew( tail3PuzzleScrew2 );
		tail3PuzzleScrew2.puzzleManager.addScrew( tail3PuzzleScrew1 );

	}

	private void tail3Pipes( ) {

		Pipe tail3TopPipe1 = ( Pipe ) LevelFactory.entities
				.get( "tail3_top_pipe_1" );
		tail3TopPipe1.setLocalRot( Util.PI / 60 );

		Pipe tail3TopPipe2 = ( Pipe ) LevelFactory.entities
				.get( "tail3_top_pipe_2" );
		tail3TopPipe2.setLocalRot( Util.PI / 60 );

		Pipe tail3TopPipe3 = ( Pipe ) LevelFactory.entities
				.get( "tail3_top_pipe_3" );
		tail3TopPipe3.setLocalRot( -Util.PI / 60 );

		Pipe tail3MiddlePipe1 = ( Pipe ) LevelFactory.entities
				.get( "tail3_middle_pipe1" );
		Pipe tail3MiddlePipe2 = ( Pipe ) LevelFactory.entities
				.get( "tail3_middle_pipe2" );

		tail3Fire2 = ( Fire ) LevelFactory.entities.get( "tail3_fire2" );
		tail3Fire3 = ( Fire ) LevelFactory.entities.get( "tail3_fire3" );
		tail3Fire4 = ( Fire ) LevelFactory.entities.get( "tail3_fire4" );
		tail3Fire4.setActiveHazard( false );
		tail3Fire5 = ( Fire ) LevelFactory.entities.get( "tail3_fire5" );
		tail3Fire5.setActiveHazard( false );
		tail3Fire6 = ( Fire ) LevelFactory.entities.get( "tail3_fire6" );

		tail3Switch1 = ( PowerSwitch ) LevelFactory.entities
				.get( "tail3_switch1" );
		tail3Switch2 = ( PowerSwitch ) LevelFactory.entities
				.get( "tail3_switch2" );
		tail3Switch3 = ( PowerSwitch ) LevelFactory.entities
				.get( "tail3_switch3" );
		bodyPowerSwitch3 = ( PowerSwitch ) LevelFactory.entities
				.get( "body_power_switch3" );

		bodyPowerSwitch1 = ( PowerSwitch ) LevelFactory.entities
				.get( "body_power_switch1" );

		dragonBrainSwitch = ( PowerSwitch ) LevelFactory.entities
				.get( "dragon_brain_switch" );

		dragonBrainSwitch2 = ( PowerSwitch ) LevelFactory.entities
				.get( "dragon_brain_switch2" );
		dragonBrain = ( Platform ) LevelFactory.entities.get( "dragon_brain" );

		tail3Switch1.actOnEntity = true;
		tail3Switch1.addEntityToTrigger( tail3MiddlePipe1 );
		tail3Switch1.addEntityToTrigger( tail3MiddlePipe2 );
		tail3Switch1.setBeginIAction( new RotateTweenAction( Util.PI / 2 ) );
		tail3Switch1.setEndIAction( new RotateTweenAction( 0 ) );

		tail3Switch2.actOnEntity = true;
		tail3Switch2.addEntityToTrigger( tail3MiddlePipe1 );
		tail3Switch2.addEntityToTrigger( tail3MiddlePipe2 );
		tail3Switch2.setBeginIAction( new RotateTweenAction( 0 ) );
		tail3Switch2.setEndIAction( new RotateTweenAction( Util.PI / 2 ) );

	}

	void tail3FireEventsUpdate( ) {
		if ( tail3Switch1.isTurnedOn( ) && tail3Switch2.isTurnedOn( ) ) {
			tail3Fire2.setActiveHazard( true );
			tail3Fire3.setActiveHazard( true );
			tail3Fire4.setActiveHazard( false );
			tail3Fire5.setActiveHazard( false );
		} else if ( tail3Switch1.isTurnedOn( ) ) {
			tail3Fire2.setActiveHazard( false );
			tail3Fire3.setActiveHazard( false );
			tail3Fire4.setActiveHazard( true );
			tail3Fire5.setActiveHazard( true );
		} else if ( tail3Switch2.isTurnedOn( ) ) {
			tail3Fire2.setActiveHazard( true );
			tail3Fire3.setActiveHazard( true );
			tail3Fire4.setActiveHazard( false );
			tail3Fire5.setActiveHazard( false );
		} else {
			tail3Fire2.setActiveHazard( true );
			tail3Fire3.setActiveHazard( true );
			tail3Fire4.setActiveHazard( false );
			tail3Fire5.setActiveHazard( false );
		}

		if ( tail3Switch3.isTurnedOn( ) ) {
			tail3Fire6.setActiveHazard( false );
		} else {
			tail3Fire6.setActiveHazard( true );
		}
	}

	void bodySkeletons( ) {

		Skeleton bodySection2Skeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_section2_skeleton" );

		Skeleton bodyInsideSkeleton1 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton1" );
		Skeleton bodyInsideSkeleton2 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton2" );
		Skeleton bodyInsideSkeleton3 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton3" );
		// Skeleton bodyInsideSkeleton4 = ( Skeleton ) LevelFactory.entities
		// .get( "body_inside_skeleton4" );

		float motorSpeed = 0.7f;
		createMotor( bodyInsideSkeleton1, bodySection2Skeleton, motorSpeed );
		createMotor( bodyInsideSkeleton2, bodySection2Skeleton, -motorSpeed );
		createMotor( bodyInsideSkeleton3, bodySection2Skeleton, motorSpeed );
		// createMotor( bodyInsideSkeleton4, bodySection2Skeleton, -motorSpeed
		// );

		bodyRoomRotateSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_room_rotate_skeleton" );
		Skeleton bodySkeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_skeleton" );

		// bodyRoomRotateSkeleton.addMover( new
		// RotateTweenMover(bodyRoomRotateSkeleton ));
		// bodyRoomRotateSkeleton.addMover(
		// rotateCircleMover(bodyRoomRotateSkeleton) );

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( bodyRoomRotateSkeleton.body,
				bodySkeleton.body, bodyRoomRotateSkeleton.getPosition( ) );
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.maxMotorTorque = 100f;// high max motor force
												// yields a

		revoluteJointDef.motorSpeed = -0.3f;

		bodyRoomJoint = ( RevoluteJoint ) level.world
				.createJoint( revoluteJointDef );

		// body_bot_lower
		// These platforms are invisible
		Platform bodyTop = ( Platform ) LevelFactory.entities.get( "body_top" );

		Platform bodyBotLower = ( Platform ) LevelFactory.entities
				.get( "body_bot_lower" );

		Fire bodyFire6 = ( Fire ) LevelFactory.entities.get( "body_fire6" );
		bodyFire6.particleEffect.setAngle( Util.PI / 2 );
		bodyFire6.particleEffect.setRotation( -Util.PI / 4 );

		Fire bodyFire5 = ( Fire ) LevelFactory.entities.get( "body_fire5" );

		Fire bodySection2Fire0 = ( Fire ) LevelFactory.entities
				.get( "body_section2_fire0" );
		Fire bodySection2Fire1 = ( Fire ) LevelFactory.entities
				.get( "body_section2_fire1" );
		Fire bodySection2Fire2 = ( Fire ) LevelFactory.entities
				.get( "body_section2_fire2" );
		Fire bodySection2Fire3 = ( Fire ) LevelFactory.entities
				.get( "body_section2_fire3" );
		Fire bodySection2Fire4 = ( Fire ) LevelFactory.entities
				.get( "body_section2_fire4" );
		Fire bodySection2Fire5 = ( Fire ) LevelFactory.entities
				.get( "body_section2_fire5" );

		PowerSwitch bodyPowerSwitch2 = ( PowerSwitch ) LevelFactory.entities
				.get( "body_power_switch2" );

		bodyPowerSwitch2.addEntityToTrigger( bodySection2Fire0 );
		bodyPowerSwitch2.addEntityToTrigger( bodySection2Fire1 );
		bodyPowerSwitch2.addEntityToTrigger( bodySection2Fire2 );
		bodyPowerSwitch2.addEntityToTrigger( bodySection2Fire3 );
		bodyPowerSwitch2.addEntityToTrigger( bodySection2Fire4 );
		bodyPowerSwitch2.addEntityToTrigger( bodySection2Fire5 );

		bodyPowerSwitch2.actOnEntity = true;
		bodyPowerSwitch2.addEntityToTrigger( bodyFire5 );
		bodyPowerSwitch2.addEntityToTrigger( bodyFire6 );

		Fire bodyFireI;
		for ( int iter = 13; iter < 19; iter++ ) {
			bodyFireI = ( Fire ) LevelFactory.entities.get( "body_fire" + iter );
			if ( iter < 15 ) {
				bodyFireI.particleEffect.setAngle( Util.PI / 2 );
				bodyFireI.particleEffect.setRotation( -Util.PI / 4 );
			}
			bodyPowerSwitch2.addEntityToTrigger( bodyFireI );
		}

		// DECALS:
		TextureAtlas dragon_objects = WereScrewedGame.manager
				.getAtlas( "dragon_objects" );
		Skeleton[ ] wheelSkeles = { bodyInsideSkeleton1, bodyInsideSkeleton2,
				bodyInsideSkeleton3 };
		Sprite s;
		for ( int i = 0; i < wheelSkeles.length; ++i ) {
			s = dragon_objects.createSprite( "wheel" );
			wheelSkeles[ i ].addBGDecal( s,
					new Vector2( -s.getWidth( ) / 2, -s.getHeight( ) / 2 ) );
			addBGEntity( wheelSkeles[ i ] ); // add to entity list so it's
												// amongst the entity layer not
												// skele layer
		}

		// bodyRoomRotateSkeleton
		// top left
		bodyRoomRotateSkeleton.addFGDecal( Sprite.scale(
				dragon_objects.createSprite( "turbine-outside" ), 2 ),
				new Vector2( -1012, 100 ) );
		bodyRoomRotateSkeleton.sprite = null;
		// bottom left
		bodyRoomRotateSkeleton.addFGDecal( Sprite.scale(
				dragon_objects.createSprite( "turbine-outside" ), 2, -2 ),
				new Vector2( -1012, -100 ) );
		// top right
		bodyRoomRotateSkeleton.addFGDecal( Sprite.scale(
				dragon_objects.createSprite( "turbine-outside" ), -2, 2 ),
				new Vector2( 1012, 100 ) );
		// bottom right
		bodyRoomRotateSkeleton.addFGDecal( Sprite.scale(
				dragon_objects.createSprite( "turbine-outside" ), -2, -2 ),
				new Vector2( 1012, -100 ) );
		addFGEntity( bodyRoomRotateSkeleton );

		s = dragon_objects.createSprite( "turbine" );
		float scale = 2f;
		bodySkeleton.addBGDecal( Sprite.scale( s, scale ), new Vector2( 1473,
				-1198 ) );
		addBGSkeleton( bodySkeleton );
		
		
		
	}

	void buildAllCannons( ) {

		Skeleton balloon3CannonSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "balloon3_cannon_skeleton" );
		balloon3CannonSkeleton.bgSprite = null;
		balloon3CannonSkeleton.setFgFade( false );
		buildCannon( balloon3CannonSkeleton,
				balloon3CannonSkeleton.getPositionPixel( ), 195, 350, 0.5f, 1f );
		balloon3CannonSkeleton.setLocalRot( -Util.PI / 4 );

		// Skeleton cannonPuzzle = ( Skeleton ) LevelFactory.entities
		// .get( "body_cannon_puzzle_skeleton" );
		// cannonPuzzle.setLocalRot( -Util.PI / 2 );
		// cannonPuzzle.setFgFade( false );
		//
		//
		// buildCannon( cannonPuzzle, cannonPuzzle.getPositionPixel( ), 200,
		// 200,
		// 0.5f, 0.5f );
		// for ( int i = 1; i < 5; ++i ) {
		// Skeleton skel = ( Skeleton ) LevelFactory.entities
		// .get( "body_cannon_skeleton" + i );
		// skel.setFgFade( false );
		//
		// if ( i % 2 == 1 ) {
		// skel.setLocalRot( Util.PI / 6 );
		// } else {
		// skel.setLocalRot( -Util.PI / 6 );
		// }
		// buildCannon( skel, skel.getPositionPixel( ), 200, 200, 0.33f, 0.5f );
		//
		// }

	}

	private TimelineTweenMover rotateCircleMover( Skeleton skel ) {
		Timeline t = Timeline.createSequence( );

		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 10f )
				.ease( TweenEquations.easeNone ).target( Util.PI * 2 ).start( )
				.delay( 0f ) );

		t.repeat( Tween.INFINITY, 0f );
		return new TimelineTweenMover( t.start( ) );
	}

	void flamePlatformDecals( ) {
		TextureAtlas balloons = WereScrewedGame.manager.getAtlas( "balloons" );
		TextureAtlas dragonObjects = WereScrewedGame.manager
				.getAtlas( "dragon_objects" );
		Sprite s;
		float scale = 1f / 0.5f;
		Platform p;
		String[ ] entities = { "balloon1_flame_plat", "balloon2_flame_plat",
				"balloon3_flame_plat", "tail1_flame_plat", "tail2_flame_plat",
				"tail3_flame_plat" };
		for ( int i = 0; i < entities.length; ++i ) {
			p = ( Platform ) LevelFactory.entities.get( entities[ i ] );
			s = dragonObjects.createSprite( "burner-med" );
			p.addFGDecal( s, new Vector2( -s.getWidth( ) / 2,
					-s.getHeight( ) / 2 ) );
			addFGEntity( p );
			p.addBehindParticleEffect( "fire_new", false, true )
					.setOffsetFromParent( 0, 75 ).start( );//
			p.getEffect( "fire_new" ).updateAngleWithParent = false;
			p.setVisible( false, true ); // don't draw the platform, but do draw
											// particles
		}

		// Joints the flame plats to their balloons, instead of making more
		// skeleton
		for ( int i = 1; i < 4; i++ ) {
			p = ( Platform ) LevelFactory.entities.get( "tail" + i
					+ "_flame_plat" );

			Platform balloon1 = ( Platform ) LevelFactory.entities.get( "tail"
					+ i + "_balloon1" );

			if ( p != null && balloon1 != null ) {
				WeldJointDef weldJointDef = new WeldJointDef( );
				weldJointDef
						.initialize( p.body, balloon1.body, p.getPosition( ) );
				level.world.createJoint( weldJointDef );
			}
			if ( i == 1 )
				scale = 1f / .7f;
			else
				scale = 1f / .5f;
			balloon1.sprite = null;
			s = balloons.createSprite( "balloon_big" + i );
			balloon1.addFGDecal( Sprite.scale( s, scale ),
					new Vector2( -s.getWidth( ) / 2 * scale, -s.getHeight( )
							/ 2.8f * scale ) );
			addFGEntity( balloon1 );
			// pizza2

		}

		// intro balloons
		// balloon1,2,3
		scale = 1f / .7f;
		for ( int i = 1; i <= 3; i++ ) {
			p = ( Platform ) LevelFactory.entities.get( "balloon" + i );
			p.sprite = null;
			s = balloons.createSprite( "balloon_big" + i );
			p.addFGDecal( Sprite.scale( s, scale ), new Vector2( -s.getWidth( )
					/ 2 * scale, -s.getHeight( ) / 2.8f * scale ) );
			addFGEntity( p );
		}

	}

	void buildBackground( ) {
		float ratio = ( float ) screenHeight / ( float ) height;

		TextureAtlas clouds_sun_bg = WereScrewedGame.manager
				.getAtlas( "clouds_sun_bg" );
		TextureAtlas mountains_back_clouds = WereScrewedGame.manager
				.getAtlas( "mountains-back-clouds" );
		float frontTopCloudsY = 2650, midOrangeCloudsY = -900, bottomFrontCloudsY = -1000;
		float frontCloudVariation = 600;
		float numFrontClouds = 50;
		float xMax = 32000, xMin = -7000;
		float minCloudScale = 0.5f;
		float cloudScale = 1f / .75f;
		float moveLeftSpeed = -0.00013f;
		float speedOffset = -.00001f;

		setClearColor( 105f / 255f, 208f / 255f, 255f / 255f, 1f ); // SKY BLUE

		Random r = new Random( );
		// loop variables
		BodyDef bdef;
		Body b;
		Entity e;
		ParallaxMover m;
		float yPos, scale;
		String cloudId;

		// TOP yellow big clouds
		for ( int i = 0; i < numFrontClouds; ++i ) {
			bdef = new BodyDef( );
			bdef.fixedRotation = true;
			bdef.type = BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "top-front" + ( r.nextInt( 5 ) + 1 );
			e = new Entity( "top-front-" + i, new Vector2( ),
					clouds_sun_bg.findRegion( cloudId ), b, false, 0 );
			scale = ( r.nextFloat( ) / 2 + minCloudScale ) * cloudScale;
			e.sprite.setScale( scale * ( r.nextBoolean( ) ? 1f : -1f ),
					r.nextFloat( ) / 2 + 0.5f );
			e.addFGDecal( e.sprite );
			e.sprite = null;
			addFGEntity( e );
			yPos = frontTopCloudsY + Util.binom( ) * frontCloudVariation;
			m = new ParallaxMover( new Vector2( xMin, yPos ), new Vector2(
					xMax, yPos ), moveLeftSpeed * r.nextFloat( ) + speedOffset,
					r.nextFloat( ), null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}

		// MID orange big cloud layer
		for ( int i = 0; i < numFrontClouds; ++i ) {
			bdef = new BodyDef( );
			bdef.fixedRotation = true;
			bdef.type = BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "front1-" + ( r.nextInt( 4 ) + 1 );
			e = new Entity( "front1-" + i, new Vector2( ),
					clouds_sun_bg.findRegion( cloudId ), b, false, 0 );
			scale = ( r.nextFloat( ) / 2 + minCloudScale ) * cloudScale;
			e.sprite.setScale( scale * ( r.nextBoolean( ) ? 1f : -1f ),
					r.nextFloat( ) / 2 + 0.5f );
			e.addFGDecal( e.sprite );
			e.sprite = null;
			addFGEntity( e );
			yPos = midOrangeCloudsY + Util.binom( ) * frontCloudVariation;
			m = new ParallaxMover( new Vector2( xMin, yPos ), new Vector2(
					xMax, yPos ), moveLeftSpeed * r.nextFloat( ) + speedOffset,
					r.nextFloat( ), null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}

		// bottom darker cloud layer
		for ( int i = 0; i < numFrontClouds; ++i ) {
			bdef = new BodyDef( );
			bdef.fixedRotation = true;
			bdef.type = BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "front2-" + ( r.nextInt( 2 ) + 2 ); // FIX THIS, front2-1
															// is not in pack
															// file
			e = new Entity( "front1-" + i, new Vector2( ),
					clouds_sun_bg.findRegion( cloudId ), b, false, 0 );
			scale = ( r.nextFloat( ) / 2 + minCloudScale ) * cloudScale;
			e.sprite.setScale( scale * ( r.nextBoolean( ) ? 1f : -1f ),
					r.nextFloat( ) / 2 + 0.5f );
			e.addFGDecal( e.sprite );
			e.sprite = null;
			addFGEntity( e );
			yPos = bottomFrontCloudsY + Util.binom( ) * frontCloudVariation;
			m = new ParallaxMover( new Vector2( xMin, yPos ), new Vector2(
					xMax, yPos ),
					// new Vector2( r.nextFloat( )*(xMax-xMin)+xMin, yPos ),
					moveLeftSpeed * r.nextFloat( ) + speedOffset,
					r.nextFloat( ), null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}

		// more in the beginning orange big cloud layer
		for ( int i = 0; i < 10; ++i ) {
			bdef = new BodyDef( );
			bdef.fixedRotation = true;
			bdef.type = BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "front1-" + ( r.nextInt( 4 ) + 1 );
			e = new Entity( "front1-" + i, new Vector2( ),
					clouds_sun_bg.findRegion( cloudId ), b, false, 0 );
			scale = ( r.nextFloat( ) / 2 + minCloudScale ) * cloudScale;
			e.sprite.setScale( scale * ( r.nextBoolean( ) ? 1f : -1f ),
					r.nextFloat( ) / 2 + 0.5f );
			e.addFGDecal( e.sprite );
			e.sprite = null;
			addFGEntity( e );
			yPos = midOrangeCloudsY + 900 + Util.binom( ) * frontCloudVariation;
			m = new ParallaxMover( new Vector2( xMin, yPos ), new Vector2(
					2500, yPos ), -0.001f * r.nextFloat( ) - .0001f,
					r.nextFloat( ), null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}

		// !Important
		level.initBackgroundRoot( );

		// bgGradient
		float xOffset = -450, yOffset = -900;
		bdef = new BodyDef( );
		bdef.fixedRotation = true;
		bdef.type = BodyType.StaticBody;
		b = level.world.createBody( bdef );
		e = new Entity( "bg-gradient", new Vector2( 0, -500 ), null, b, false,
				0 );

		e.changeSprite( Sprite.scale(
				clouds_sun_bg.createSprite( "bg-gradient" ),
				.194444444f * WereScrewedGame.getWidth( ), 1 ) );// 67.5f
		level.backgroundRootSkeleton.addLooseEntity( e );
		m = new ParallaxMover( new Vector2( xOffset, 0 + yOffset ),
				new Vector2( xOffset, -2048 + yOffset ), 0.0002f, 0.00001f,
				level.camera, false, LinearAxis.VERTICAL );
		m.setLoopRepeat( false );
		e.setMoverAtCurrentState( m );

		// clouds behind mountains
		level.bgCamZoomScale = .1f;
		level.bgCamZoomMax = 1.5f;
		level.bgCamZoomMin = .5f;
		int numMountains = 3;
		float mountainW = ( 1859 - 100 ) * level.bgCamZoomMax, mountainY = 100;

		float alpha, aOffset;

		// mountains
		mountainW = 1275 * level.bgCamZoomMax;
		mountainY = -200f * ratio;
		for ( int i = 0; i < numMountains; ++i ) {
			bdef = new BodyDef( );
			bdef.fixedRotation = true;
			bdef.type = BodyType.StaticBody;
			b = level.world.createBody( bdef );
			e = new Entity( "mountains" + i, new Vector2( ), null, b, false, 0 );
			e.changeSprite( mountains_back_clouds.createSprite( "mountains" ) );
			level.backgroundRootSkeleton.addLooseEntity( e );
			aOffset = i * .0001f;
			alpha = ( i ) * ( 1f / ( numMountains ) );// +aOffset;
			m = new ParallaxMover( new Vector2( mountainW, mountainY ),
					new Vector2( -mountainW, mountainY ), 0.00002f, alpha,
					null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
		}

		// Sun 50% = 2
		float sunScale = 1f;
		bdef = new BodyDef( );
		bdef.fixedRotation = true;
		bdef.type = BodyType.StaticBody;
		b = level.world.createBody( bdef );
		e = new Entity( "sun", new Vector2( ), null, b, false, 0 );// 0,2048-2*
		e.changeSprite( Sprite.scale( clouds_sun_bg.createSprite( "sun" ),
				sunScale ) );
		// e.setPosition( new Vector2().mul( Util.PIXEL_TO_BOX ) );
		float sunYPos = ( height ) + e.sprite.getHeight( ) + 150 - sunScale
				* e.sprite.getHeight( );
		sunYPos = sunYPos * ratio;
		float endHeight = -2048 / ratio + sunYPos;
		m = new ParallaxMover( new Vector2( 400 * ratio, sunYPos ),
				new Vector2( 400 * ratio, endHeight ), 0.00009f, 0.00001f,
				level.camera, false, LinearAxis.VERTICAL );
		e.setMoverAtCurrentState( m );
		m.setLoopRepeat( false );
		level.backgroundRootSkeleton.addLooseEntity( e );
	}

	void headDecals( ) {
		// head_skeleton
		Skeleton jaw_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "jaw_skeleton" );

		headSkeleton = ( Skeleton ) LevelFactory.entities.get( "head_skeleton" );

		Skeleton headSubSkeleton1 = ( Skeleton ) LevelFactory.entities
				.get( "head_sub_skeleton1" );

		TextureAtlas head_left = WereScrewedGame.manager.getAtlas( "head_left" );
		TextureAtlas head_right = WereScrewedGame.manager
				.getAtlas( "head_top_right" );
		TextureAtlas head_jaw = WereScrewedGame.manager.getAtlas( "head_jaw" );
		TextureAtlas head_interior = WereScrewedGame.manager
				.getAtlas( "body_right-head_interior" );
		TextureAtlas dragon_objects = WereScrewedGame.manager
				.getAtlas( "dragon_objects" );
		float scale = 2;// 1f/.66f;
		// UPPER HEAD
		Sprite s;

		Vector2 headPos = new Vector2( -3570, -1455 ).add( 125, 41 );// 1219,31
		s = head_left.createSprite( "head_left" );// 1219,31
		headSkeleton.addFGDecal( Sprite.scale( s, scale ),
				new Vector2( 65, 0 ).add( headPos ) );
		headSubSkeleton1.addFGDecal(
				Sprite.scale( head_right.createSprite( "head_right" ), scale ),
				new Vector2( 5146, 740 ).add( headPos ).add( -1219, 31 ) );// 200,276
		headSkeleton
				.addFGDecal( Sprite.scale(
						head_right.createSprite( "head_middle" ), scale ),
						new Vector2( 3050, 30 ).add( headPos ) );
		addFGSkeleton( headSkeleton );
		addFGSkeleton( headSubSkeleton1 );
		// LOW HEAD/ JAW
		Vector2 pos = new Vector2( -1420, -615 );
		s = head_jaw.createSprite( "dragonbottom_left" );
		jaw_skeleton.addFGDecal( Sprite.scale( s, scale ),
				new Vector2( ).add( pos ).add( 2, 37 ) );// 959,615
		jaw_skeleton.addFGDecal( Sprite.scale(
				head_jaw.createSprite( "dragonbottom_right" ), scale ),
				new Vector2( s.getWidth( ) * scale - 8, 16 ).add( pos ) );
		addFGSkeleton( jaw_skeleton );

		// inside of head.
		headSkeleton.addBGDecal( Sprite.scale(
				head_interior.createSprite( "head-interior" ), 1f / .4f, 1f/ .38f ),
				new Vector2( -1440, -740 ) );
		addBGSkeleton( headSkeleton );

		scale = 1f / .50f;
		Platform brain = dragonBrain;
		s = dragon_objects.createSprite( "dragon_brain" );
		brain.addFGDecal( Sprite.scale( s, scale ), new Vector2( -s.getWidth( )
				/ 2 * scale, -s.getHeight( ) / 2 * scale ) );
		addFGEntity( brain );
	}

	void initEyebrow( ) {
		Skeleton skeleton = ( Skeleton ) LevelFactory.entities
				.get( "head_skeleton" );
		Vector2 posPix = skeleton.getPositionPixel( ).add( -575, 800 );// -975,306
		// TiledPlatform brow = new PlatformBuilder(level.world).name( "eyebrow"
		// ).dimensions( 2,2 ).position( posPix.cpy() ).buildTilePlatform( );
		Skeleton brow = new Skeleton( "eyebrow", posPix.cpy( ), null,
				level.world );
		skeleton.addSkeleton( brow );
		// brow.noCollide( );
		brow.setVisible( true );
		brow.setFgFade( false );

		TextureAtlas browAtlas = new TextureAtlas(
				Gdx.files.internal( "data/levels/dragon/head_top_right.pack" ) );
		// At rest the eyebrow is unrotated at 0,0 local position.
		brow.addFGDecal( Sprite.scale( browAtlas.createSprite( "eyebrow" ),
				1.6f ) );// , new Vector2(-393,-161) );
		addFGSkeleton( brow );
		
		//pizza.
		
		//angry mover
		Timeline browSequence = Timeline.createSequence( );
		//begin the mover by moving it to the starting position quickly
		browSequence.beginParallel( );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, .5f )
				.target( 0,0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, .5f )
				.target( 0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.end( );
		//.5s
		
		browSequence.beginParallel( );//idling
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 1.875f )
			.target( 20, -20f ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 2, 0 ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 1.875f )
				.target( -Util.FOURTH_PI/6 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 2, 0 ).start( ) );
		browSequence.end( );
		//8s of brow movement by now
		
		browSequence.beginParallel( );//angry
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, .5f )
				.target( 100, -100f ).ease( TweenEquations.easeInOutQuad ).start( ) );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, .5f )
					.target( -Util.FOURTH_PI/2 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.end( );
		
		browSequence.beginParallel( );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 1.875f )
					.target( 110, -130 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 2, 0 ).start( ) );
			browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 1.875f )
				.target( -Util.FOURTH_PI/2-Util.FOURTH_PI/6 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 2, 0 ).start( ) );
		browSequence.end( );
		//16s by now
		
		browSequence.beginParallel( );///move back to beginning
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 2f )
				.target( 0,0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 2f )
				.target( 0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
		browSequence.end( );
		//18 s

		browSequence.beginParallel( );//idling
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 2f )
			.target( 20, -20f ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 1, 0 ).start( ) );
		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 2f )
				.target( -Util.FOURTH_PI/6 ).ease( TweenEquations.easeInOutQuad ).repeatYoyo( 1, 0 ).start( ) );
		browSequence.end( );
		browSequence = browSequence.repeat( Tween.INFINITY, 0f );
		//22s now
		
//		browSequence.beginParallel( );
//		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_POS_XY, 2f )
//				.target( 0,0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
//		browSequence.push( Tween.to( brow, PlatformAccessor.LOCAL_ROT, 2f )
//				.target( 0 ).ease( TweenEquations.easeInOutQuad ).start( ) );
//		browSequence.end( );
		
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
		brow.setCurrentMover( RobotState.HOSTILE);
		
		//((TimelineTweenMover)brow.currentMover( )).timeline.start( );
		
		
		brow.dontPutToSleep=true;
		
	}

	void tail1Decals( ) {
		Skeleton tail = ( Skeleton ) LevelFactory.entities
				.get( "tail_skeleton" );

		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "tail-fg" );
		Sprite s = Sprite.scale( tailAtlas.createSprite( "tail" ),2);
		s.setOrigin( 0, 0 );
		tail.addFGDecal( s ,
				new Vector2( -1800+47, -400+36 ) );
		tail.fgSprite = null;
		// tail.setFgFade( true );
		addFGSkeleton( tail );

	}

	void tail2Decals( ) {
		// //1189,431
		Skeleton tail2_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "tail2_skeleton" );
		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "tail-fg" );
		TextureAtlas tailInterior = WereScrewedGame.manager
				.getAtlas( "interior_tail2_bodyright" );
		Sprite s;

		// fg
		s = Sprite.scale( tailAtlas.createSprite( "tail2" ), 2 );
		s.setOrigin( 0, 0 ); // this one has weird origin issues in the pack or
								// something
		tail2_skeleton.addFGDecal( s, new Vector2( -1206, -638 ) );
		tail2_skeleton.fgSprite = null;
		addFGSkeleton( tail2_skeleton );

		// bg interior
		tail2_skeleton
				.addBGDecal( Sprite.scale(
						tailInterior.createSprite( "tail2_interior" ), 2 ),
						new Vector2( -1137, -525 ) );// -1137,-525
		tail2_skeleton.bgSprite = null;
		addBGSkeleton( tail2_skeleton );

		PowerSwitch tail2Switch1 = ( PowerSwitch ) LevelFactory.entities
				.get( "tail2_switch1" );
		PowerSwitch tail2Switch2 = ( PowerSwitch ) LevelFactory.entities
				.get( "tail2_switch2" );
		PowerSwitch tail2Switch3 = ( PowerSwitch ) LevelFactory.entities
				.get( "tail2_switch3" );

		Pipe tail2Pipe1 = ( Pipe ) LevelFactory.entities.get( "tail2_pipe1" );

		Pipe tail2Pipe2 = ( Pipe ) LevelFactory.entities.get( "tail2_pipe2" );

		tail2Switch1.actOnEntity = true;
		tail2Switch1.addEntityToTrigger( tail2Pipe1 );
		tail2Switch1.addEntityToTrigger( tail2Pipe2 );
		tail2Switch1.setBeginIAction( new RotateTweenAction( -Util.PI / 2 ) );
		tail2Switch1.setEndIAction( new RotateTweenAction( 0 ) );

		tail2Switch2.actOnEntity = true;
		tail2Switch2.addEntityToTrigger( tail2Pipe1 );
		tail2Switch2.addEntityToTrigger( tail2Pipe2 );
		tail2Switch2.setBeginIAction( new RotateTweenAction( -Util.PI / 2 ) );
		tail2Switch2.setEndIAction( new RotateTweenAction( 0 ) );

		tail2Switch3.actOnEntity = true;
		tail2Switch3.addEntityToTrigger( tail2Pipe1 );
		tail2Switch3.addEntityToTrigger( tail2Pipe2 );
		tail2Switch3.setBeginIAction( new RotateTweenAction( -Util.PI / 2 ) );
		tail2Switch3.setEndIAction( new RotateTweenAction( 0 ) );

	}

	void tail3Decals( ) {
		// tail3_skeleton
		Skeleton tail3_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "tail3_skeleton" );
		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "tail-fg" );
		TextureAtlas interiorAtlas = WereScrewedGame.manager
				.getAtlas( "interior_tail3_bodyleft" );

		// fg
		Sprite s = tailAtlas.createSprite( "tail3" );
		s.setOrigin( 0, 0 );
		tail3_skeleton.addFGDecal( Sprite.scale( s, 2.6f ), new Vector2(
				-820 - 330, -580 - 134 - 28 ) );
		tail3_skeleton.fgSprite = null;
		addFGSkeleton( tail3_skeleton );

		// bg
		tail3_skeleton.addBGDecal( Sprite.scale(
				interiorAtlas.createSprite( "tail3_interior" ), 2.06f, 2.12f ),
				new Vector2( -1100, -685 ) );// 227,17
		tail3_skeleton.bgSprite = null;
		addBGSkeleton( tail3_skeleton );

	}

	void bodyDecals( ) {
		Skeleton neck_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "neck_skeleton" );
		TextureAtlas body_neck = WereScrewedGame.manager.getAtlas( "body-neck" );
		TextureAtlas bodyRight = WereScrewedGame.manager
				.getAtlas( "body_right-head_interior" );
		TextureAtlas interiorLeftAtlas = WereScrewedGame.manager
				.getAtlas( "interior_tail3_bodyleft" );
		TextureAtlas interiorRightAtlas = WereScrewedGame.manager
				.getAtlas( "interior_tail2_bodyright" );
		TextureAtlas dragon_objects = WereScrewedGame.manager
				.getAtlas( "dragon_objects" );

		// neck
		neck_skeleton.addFGDecal(
				Sprite.scale( body_neck.createSprite( "neck" ), 2f ),
				new Vector2( -1167, -914 ) );// 4,414
		neck_skeleton.fgSprite = null;
		// neck_skeleton.setFgFade( false );//3497.1770
		addFGSkeleton( neck_skeleton );

		float bodyScale = 1f / .385f;
		Skeleton bodySkeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_skeleton" );
		// body exterior //32,11

		Vector2 bodyPos = new Vector2( -3468, -1433 );
		Sprite s = Sprite.scale( body_neck.createSprite( "body_left" ),
				bodyScale );

		bodySkeleton.addFGDecal( s, bodyPos );
		bodySkeleton.addFGDecal( Sprite.scale(
				bodyRight.createSprite( "body_right" ), bodyScale ), bodyPos
				.cpy( ).add( s.getWidth( ) * bodyScale, 0 ) );
		addFGSkeleton( bodySkeleton );

		// interior body decals
		Vector2 interiorPos = new Vector2( -3360, -1350 );
		bodySkeleton.addBGDecal( Sprite.scale(
				interiorLeftAtlas.createSprite( "body_interior_left" ), 2 ),
				interiorPos.cpy( ) );
		bodySkeleton.addBGDecal( Sprite.scale(
				interiorRightAtlas.createSprite( "body_interior_right" ), 2 ),
				interiorPos.cpy( ).add( 4074, 0 ) );
		bodySkeleton.bgSprite = null;
		addBGSkeleton( bodySkeleton );

		// rotation puzzle machine decals
		Entity screw = LevelFactory.entities.get( "body_rotate_puzzle_screw1" );
		s = dragon_objects.createSprite( "rotation_machine_wheel" );
		screw.addBGDecal( s, new Vector2( -s.getWidth( ) / 2,
				-s.getHeight( ) / 2 ) );
		addBGEntity( screw );

		Skeleton rotateScrewSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_rotatepuzzle_skeleton" );
		s = dragon_objects.createSprite( "rotation_machine_rotate_plate" );// rotation_machine_wheel
		s.setScale( 1, -1.37f );
		rotateScrewSkeleton
				.addBGDecal( s, new Vector2( -s.getWidth( ) / 2, 30 ) );// -s.getHeight(
																		// )/2
		addBGEntity( rotateScrewSkeleton );

		Vector2 bodyP = bodySkeleton.getPositionPixel( );
		screw = LevelFactory.entities.get( "body_rotate_puzzle2" );
		Vector2 screwP = new Vector2( -338, -383 );
//		bodySkeleton.addBGDecal(
//				dragon_objects.createSprite( "rotation_machine_decal_left" ),
//				new Vector2( 0, 8 ).add( screwP ) );

		screw = LevelFactory.entities.get( "body_rotate_puzzle4" );
//		bodySkeleton.addBGDecal(
//				dragon_objects.createSprite( "rotation_machine_decal_right" ),
//				new Vector2( 995, 9 ).add( screwP ) );
//		addBGSkeleton( bodySkeleton );

		// balloons
		TextureAtlas balloons = WereScrewedGame.manager.getAtlas( "balloons" );

		String[ ] bodyBalloons = { "body_balloon_left", "body_balloon_center",
				"body_balloon_right" };
		float balloonScale = 1f;
		// PIZZA1
		for ( int i = 0; i < bodyBalloons.length; ++i ) {
			if ( i == 1 )
				balloonScale = 1f / .4f;
			else
				balloonScale = 1f / .6f;
			s = Sprite.scale(
					balloons.createSprite( "balloon_big" + ( i % 3 + 1 ) ),
					balloonScale );
			Entity entity = LevelFactory.entities.get( bodyBalloons[ i ] );
			entity.addFGDecal( s, new Vector2( -s.getWidth( ) / 2
					* balloonScale, -s.getHeight( ) / 2 * balloonScale ) );
			entity.sprite = null;
			addFGEntity( entity );
		}

		Skeleton mesh_skeleton1 = ( Skeleton ) LevelFactory.entities
				.get( "mesh_skeleton1" );
		for ( int i = 0; i < 9; ++i ) {
			// dragon_objects
			s = dragon_objects.createSprite( "mesh" );
			mesh_skeleton1.addBGDecal( s, new Vector2( -220, -s.getHeight( )
					* i + i * 7 + 612 ) );
		}
		addBGSkeleton( mesh_skeleton1 );

		Skeleton body_lower_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_lower_skeleton" );
		Vector2 legPos = new Vector2( -3 * 1280 + 120 + 34 - 21,
				-700 + 183 - 63 + 21 );
		body_lower_skeleton.addFGDecal( body_neck.createSprite( "legs_left" ),
				legPos.cpy( ).add( -103,2 ) );
		for ( int i = 0; i < 9; ++i ) {
			// dragon_objects
			s = body_neck.createSprite( "legs_middle" );
			body_lower_skeleton.addFGDecal( s, new Vector2( s.getWidth( )
					* ( i + 0 ) + 377 + 3 - i * 7, 3 ).add( legPos ) );
		}
		body_lower_skeleton.addFGDecal( body_neck.createSprite( "legs_right" ),
				legPos.cpy( ).add( 9 * s.getWidth( ) - 7 + 326 + 5, -7 + 9 ) );
		addFGSkeleton( body_lower_skeleton );
	}

	void createMotor( Skeleton rotating, Skeleton parent, float motorSpeed ) {

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( rotating.body, parent.body,
				rotating.getPosition( ) );
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.maxMotorTorque = 100f;// high max motor force
												// yields a

		revoluteJointDef.motorSpeed = motorSpeed;

		level.world.createJoint( revoluteJointDef );

	}

	void groundDecals( ) {
		// ground1
		TiledPlatform ground = ( TiledPlatform ) LevelFactory.entities
				.get( "ground1" );
		TextureAtlas objects = WereScrewedGame.manager
				.getAtlas( "dragon_objects" );
		ground.addFGDecal( Sprite.scale( objects.createSprite( "bridge" ), 3 ),
				new Vector2( -1550, -277 * 3 - 26 ) );
		ground.setVisible( false );
		addFGEntity( ground );
	}

	private void initFireballEnemy( ) {

		int w = 15, n = 10, h = 140;

		fireballEmitter = new EntityParticleEmitter( "bolt emitter",
				new Vector2( new Vector2(13730, 1650) ),
				new Vector2(),
				 level.world, true );
		for(int i =0; i < 4; ++i ){
			fireballEmitter.addParticle( createBoltEnemy( new Vector2(13730, 1650), i ), 10, 3, i*5 );
		}
		level.root.addLooseEntity( fireballEmitter );
	//	fireballEmitter.setEmittingActive( false );
		float brain_impulse = 0.1f;
		Vector2 pos = new Vector2( 24032, 136 );
		brainEmitter1 = new EntityParticleEmitter( "brainEmitter1",
				new Vector2( pos.cpy().add(0,n*h) ),
				new Vector2(-brain_impulse - 0.03f, 0),
				 level.world, true );
		
		int boltsPerEmitter = 2, boltLife = 5;
		for(int i =0; i < boltsPerEmitter; ++i ){
			brainEmitter1.addParticle( createBoltEnemy( pos.cpy().add(0,n*h), i ), boltLife, 1, i*boltLife/boltsPerEmitter );
		}
		level.root.addLooseEntity( brainEmitter1 );

		Vector2 pos2 = new Vector2( 25218, 136 );
		brainEmitter2 = new EntityParticleEmitter( "brainEmitter2",
				new Vector2( pos2.cpy().add(0,n*h) ),
				new Vector2(brain_impulse + 0.03f, 0),
				 level.world, true );
		
		for(int i =0; i < boltsPerEmitter; ++i ){
			brainEmitter2.addParticle( createBoltEnemy( pos2.cpy().add(0,n*h), i ), boltLife, 1, i*boltLife/boltsPerEmitter );
		}
		level.root.addLooseEntity( brainEmitter2 );

		Vector2 pos3 = new Vector2( 24032, -198 );
		brainEmitter3 = new EntityParticleEmitter( "brainEmitter3",
				new Vector2( pos3.cpy().add(0,n*h) ),
				new Vector2(-brain_impulse - 0.05f, 0),
				 level.world, true );
		
		for(int i =0; i < boltsPerEmitter; ++i ){
			brainEmitter3.addParticle( createBoltEnemy( pos3.cpy().add(0,n*h), i ), boltLife, 1, i*boltLife/boltsPerEmitter );
		}
		level.root.addLooseEntity( brainEmitter3 );

		Vector2 pos4 = new Vector2( 25218, -198 );
		brainEmitter4 = new EntityParticleEmitter( "brainEmitter4",
				new Vector2( pos4.cpy().add(0,n*h) ),
				new Vector2(brain_impulse, 0),
				 level.world, true );
		
		for(int i =0; i < boltsPerEmitter; ++i ){
			brainEmitter4.addParticle( createBoltEnemy( pos4.cpy().add(0,n*h), i ), boltLife, 1, i*boltLife/boltsPerEmitter );
		}
		level.root.addLooseEntity( brainEmitter4 );
		
		
		
		//change body skeleton to fireball emitter
		//Skeleton bodySkeleton = (Skeleton)LevelFactory.entities.get( "body_skeleton" );
		//bodySkeleton.getEvent(bodySkeleton.name+"-fg-fader").
		//bodySkeleton.getEvent(bodySkeleton.name+"-fg-fader").setEndIAction( new EntityParticleActivator(false) );
	
		//EventTrigger headFireballEvent = (EventTrigger)LevelFactory.entities.get( "fireball_event" );
		//headFireballEvent.setBeginIAction( new EntityParticleActivator(true) ).addEntitiesToTrigger( brainEmitter1,brainEmitter2,brainEmitter3,brainEmitter4 );
	
		//headFireballEvent.setBeginIAction(  new EntityParticleActivator(true) );
		//headFireballEvent.setEndIAction( new EntityParticleActivator(false) );
		//headFireballEvent.addEntityToTrigger( fireballEmitter );
		//headFireballEvent.actOnEntity = true;
		
	
	}
	
	Enemy createBoltEnemy(Vector2 pos, int index){
		Enemy hotbolt = new Enemy( "hot-bolt"+index, pos,25, level.world, true );
		hotbolt.addMover( new DirectionFlipMover( false, 0.002f, hotbolt, 1.5f, .04f ) );
		addBGEntity( hotbolt );
		return hotbolt;
	}

	void getTailStructureScrews( ) {
		tail1Left = ( StructureScrew ) LevelFactory.entities
				.get( "tail_ss_left" );
		tail1Right = ( StructureScrew ) LevelFactory.entities
				.get( "tail_ss_right" );

		tail2Left = ( StructureScrew ) LevelFactory.entities
				.get( "tail2_ss_left" );
		tail2Right = ( StructureScrew ) LevelFactory.entities
				.get( "tail2_ss_right" );

	}
	
	void introBalloonDecals(){
		Skeleton skele;
		Sprite s;
		TextureAtlas mesh = WereScrewedGame.manager.getAtlas( "intro-balloon" );
		Vector2 pos;
		float scale = 1f/.75f;
		for(int i = 1; i <= 3; i++){
			skele = ( Skeleton ) LevelFactory.entities
					.get( "balloon"+i+"_skeleton" );
			skele.bgSprite=null;
			s = mesh.createSprite( "intro-balloon"+i );
			pos = new Vector2(-s.getWidth( )/2f*scale,-s.getHeight( )/2f*scale);
			if(i==1)pos.sub(20,105);
			if(i==2)pos.add(23,-117);
			if(i==3)pos.sub( -8,50 );
			skele.addBGDecal( Sprite.scale( s,(i==1)?scale*1.33f:scale), pos);
			addBGSkeleton( skele );
		}
	}
	
	public class ShootFireCallback implements TweenCallback{

		@Override
		public void onEvent( int type, BaseTween< ? > source ) {
			if(jawStructureScrew.getDepth( )>0){
				Vector2 roarPos = new Vector2(25000, 900);
				Vector2 camPos = new Vector2(Camera.getCurrentCameraCoords( ).x, Camera.getCurrentCameraCoords( ).y);
				SoundRef roarRef;
				if (roarPos.dst( camPos ) < 3000.0f){
					roarRef = sounds.getSound( "roar_angry" );
				} else {
					roarRef = sounds.getSound( "roar_calm" );
				}
				float volume = roarRef.calculatePositionalVolume( roarPos, Camera.CAMERA_RECT );
				roarRef.setVolume( volume );
				roarRef.play( false );
				mouthFire.setActiveHazard( true );
				
				
				boolean fireSound;
				if (roarPos.dst( camPos ) < 4000.0f){
					roarRef = sounds.getSound( "roar_angry" );
					fireSound = (jaw_skeleton.body != null);
				} else {
					roarRef = sounds.getSound( "roar_calm" );
					fireSound = false;
				}
				if (fireSound){
					SoundRef fireRef = sounds.getSound("fire_breath");
					fireRef.setVolume( volume );
					fireRef.play( false );
				}
			}
			
		}
	}
	
	public class PlayJawCloseCallback implements TweenCallback{

		@Override
		public void onEvent( int type, BaseTween< ? > source ) {
			float volume = sounds.calculatePositionalVolume( "jaw_close",
					new Vector2( 25000, 900 ), Camera.CAMERA_RECT );
			SoundRef jawRef = sounds.getSound( "jaw_close" );
			jawRef.setVolume( volume );
			jawRef.play( false);
		}
		
	}
}
