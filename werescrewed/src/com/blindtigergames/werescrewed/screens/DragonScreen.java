package com.blindtigergames.werescrewed.screens;

import java.util.Random;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.CannonLaunchAction;
import com.blindtigergames.werescrewed.entity.action.RotateTweenAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Pipe;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.util.Util;

public class DragonScreen extends Screen {

	PuzzleScrew puzzleScrewBalloon1;
	Platform balloon1;
	Skeleton balloon1_super, bodyRoomRotateSkeleton;
	PowerSwitch tail3Switch1, tail3Switch2, tail3Switch3, bodyPowerSwitch3;
	RevoluteJoint bodyRoomJoint;

	// the numbers here correspond to gleed numbers
	Fire tail3Fire2, tail3Fire3, tail3Fire4, tail3Fire5, tail3Fire6;

	public DragonScreen( ) {
		super( );
		String filename = "data/levels/dragonlevel.xml";
		level = new LevelFactory( ).load( filename );

		buildBalloon( );

		initPuzzleScrews( );
		tail3Pipes( );
		bodySkeletons( );
		buildAllCannons();
		flamePlatformDecals();
		buildBackground( );
		tail1Decals();
		tail2Decals();
		tail3Decals();
		//neckDecals();
		bodyDecals();


		Skeleton jaw_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "fuck_jaw_skeleton" );
		Timeline t = Timeline.createSequence( );

		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 6f )
				.ease( TweenEquations.easeNone ).target( -Util.PI / 32 )
				.start( ).delay( 2f ) );

		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 4f )
				.ease( TweenEquations.easeNone ).target( 0 ).delay( 2f )
				.start( ) );

		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 6f )
				.ease( TweenEquations.easeNone ).target( -Util.PI / 32 )
				.start( ).delay( 2f ) );

		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 6f )
				.ease( TweenEquations.easeNone ).target( 0 ).delay( 2f )
				.start( ) );

		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 8f )
				.ease( TweenEquations.easeNone ).target( -Util.PI / 32 )
				.start( ).delay( 4f ) );

		t.push( Tween.to( jaw_skeleton, PlatformAccessor.LOCAL_ROT, 5f )
				.ease( TweenEquations.easeNone ).target( 0 ).delay( 2f )
				.start( ) );
		t.repeat( Tween.INFINITY, 0f );
		jaw_skeleton.addMover( new TimelineTweenMover( t.start( ) ) );
		
		headDecals();
		
	}

	void buildBalloon( ) {
		balloon1 = ( Platform ) LevelFactory.entities.get( "balloon1" );
		
		//Platform balloon2 = ( Platform ) LevelFactory.entities.get( "balloon2" );
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

		puzzleScrewBalloon1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "puzzle_screw_balloon1" );

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

	}

	float time;
	boolean restart = false;
	int bodyRoomAngle = 0;
	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		// time += deltaTime * 1000;
		bodyRoomAngle = (int) Math.abs( (bodyRoomRotateSkeleton.getAngle( ) * Util.RAD_TO_DEG) % 360);
		
		if ( time > 5000 ) {
			// balloon2.body.applyForce( new Vector2(0f, 100f),
			// balloon2.body.getWorldCenter( ));
			time = 0;
		}
		
		if(!bodyPowerSwitch3.isTurnedOn( )){
			bodyRoomJoint.setMotorSpeed( -0.1f );
			if((bodyRoomAngle == 359 || bodyRoomAngle == 180)  && bodyRoomJoint.isMotorEnabled( )){
				bodyRoomRotateSkeleton.body.setAngularVelocity( 0f );
				bodyRoomJoint.setMotorSpeed( 0.0f );
				bodyRoomJoint.setMaxMotorTorque( 0f );
				bodyRoomJoint.enableMotor( false );
			}
			
		}
		tail3FireEventsUpdate( );

		if ( puzzleScrewBalloon1.getDepth( ) == puzzleScrewBalloon1
				.getMaxDepth( ) ) {
			if ( balloon1_super.currentMover( ) == null ) {
				Timeline t = Timeline.createSequence( );

				t.beginParallel( );
				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_POS_XY, 8f )
						.delay( 0f ).target( 0, 2000 )
						.ease( TweenEquations.easeNone ).start( ) );

				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						.ease( TweenEquations.easeNone ).target( Util.PI / 32 )
						.delay( 0f ).start( ) );

				t.end( );

				t.beginParallel( );

				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_POS_XY, 8f )
						.delay( 0f ).target( 0, 4000f )
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
		Sprite can = WereScrewedGame.manager.getAtlas( "dragon_objects" ).createSprite( "cannon-small" ); 
		skel.addFGDecal( can, new Vector2(-can.getWidth( )/2, -64) );
		addFGEntityToBack(skel); 

		// base
		skel.addPlatform( pb.name( "cannon-base" ).dimensions( dim.x, 1 )
				.position( pos.cpy( ) ).buildTilePlatform( ) );
		// left
		skel.addPlatform( pb.name( "cannon-left" ).dimensions( 1, dim.y )
				.position( left.cpy( ) ).buildTilePlatform( ) );
		// right
		skel.addPlatform( pb.name( "cannon-right" ).dimensions( 1, dim.y )
				.position( right.cpy( ) ).buildTilePlatform( ) );

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
		PuzzleScrew tail2PuzzleScrew1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "tail2_puzzle_screw1" );
		PuzzleScrew tail2PuzzleScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "tail2_puzzle_screw2" );

		AnalogRotateMover anlgRot = new AnalogRotateMover( 0.6f, level.world );

		AnalogRotateMover anlgRot2 = new AnalogRotateMover( 0.6f, level.world );

		tail2PuzzleScrew1.puzzleManager.addMover( anlgRot );
		tail2PuzzleScrew2.puzzleManager.addMover( anlgRot );

		tail2PuzzleScrew1.puzzleManager.addMover( anlgRot2 );
		tail2PuzzleScrew2.puzzleManager.addMover( anlgRot2 );

		tail2PuzzleScrew1.puzzleManager.addScrew( tail2PuzzleScrew2 );
		tail2PuzzleScrew2.puzzleManager.addScrew( tail2PuzzleScrew1 );

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
		
		Pipe tail3TopPipe1 = ( Pipe ) LevelFactory.entities.get( "tail3_top_pipe_1" );
		tail3TopPipe1.setLocalRot( Util.PI / 60 );
		
		Pipe tail3TopPipe2 = ( Pipe ) LevelFactory.entities.get( "tail3_top_pipe_2" );
		tail3TopPipe2.setLocalRot( Util.PI / 60 );
		
		Pipe tail3TopPipe3 = ( Pipe ) LevelFactory.entities.get( "tail3_top_pipe_3" );
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
		
		tail3Switch1.actOnEntity = true;
		tail3Switch1.addEntityToTrigger( tail3MiddlePipe1 );
		tail3Switch1.addEntityToTrigger( tail3MiddlePipe2 );
		tail3Switch1.addBeginIAction( new RotateTweenAction( Util.PI / 2 ) );
		tail3Switch1.addEndIAction( new RotateTweenAction( 0 ) );

		tail3Switch2.actOnEntity = true;
		tail3Switch2.addEntityToTrigger( tail3MiddlePipe1 );
		tail3Switch2.addEntityToTrigger( tail3MiddlePipe2 );
		tail3Switch2.addBeginIAction( new RotateTweenAction( 0 ) );
		tail3Switch2.addEndIAction( new RotateTweenAction( Util.PI / 2 ) );

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

		Skeleton bodyInsideSkeleton1 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton1" );
		Skeleton bodyInsideSkeleton2 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton2" );
		Skeleton bodyInsideSkeleton3 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton3" );
		Skeleton bodyInsideSkeleton4 = ( Skeleton ) LevelFactory.entities
				.get( "body_inside_skeleton4" );

		bodyInsideSkeleton1
				.addMover( new RotateTweenMover( bodyInsideSkeleton1 ) );
		bodyInsideSkeleton2.addMover( new RotateTweenMover(
				bodyInsideSkeleton2, -1 ) );
		bodyInsideSkeleton3
			.addMover( new RotateTweenMover( bodyInsideSkeleton3 ) );
		
		bodyInsideSkeleton4.addMover( new RotateTweenMover( bodyInsideSkeleton4 ) );
		
		bodyRoomRotateSkeleton = ( Skeleton ) LevelFactory.entities
		.get( "body_room_rotate_skeleton" );
		Skeleton bodySkeleton = ( Skeleton ) LevelFactory.entities
				.get( "body_skeleton" );
		
		//bodyRoomRotateSkeleton.addMover( new RotateTweenMover(bodyRoomRotateSkeleton ));
		//bodyRoomRotateSkeleton.addMover( rotateCircleMover(bodyRoomRotateSkeleton) );
		
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( bodyRoomRotateSkeleton.body, bodySkeleton.body,
				bodyRoomRotateSkeleton.getPosition( ) );
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.maxMotorTorque = 100f;// high max motor force
															// yields a

	
		revoluteJointDef.motorSpeed = -0.3f;

		bodyRoomJoint = (RevoluteJoint)level.world.createJoint( revoluteJointDef );
		
		//body_bot_lower
		//These platforms are invisible
		Platform bodyTop = ( Platform ) LevelFactory.entities
				.get( "body_top" );

		Platform bodyBotLower = ( Platform ) LevelFactory.entities
				.get( "body_bot_lower" );
	}

	void buildAllCannons( ) {
		
		Skeleton balloon3CannonSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "balloon3_cannon_skeleton" );
		balloon3CannonSkeleton.setFgFade( false );
		buildCannon( balloon3CannonSkeleton,
				balloon3CannonSkeleton.getPositionPixel( ), 200, 200, 0.5f, 1f );
		balloon3CannonSkeleton.setLocalRot( -Util.PI / 4 );
		
		
		Skeleton cannonPuzzle = ( Skeleton ) LevelFactory.entities
				.get( "body_cannon_puzzle_skeleton" );
		cannonPuzzle.setLocalRot( -Util.PI / 2 );
		cannonPuzzle.setFgFade( false );
		
		
		buildCannon( cannonPuzzle, cannonPuzzle.getPositionPixel( ), 200, 200,
				0.5f, 0.5f );
		for ( int i = 1; i < 5; ++i ) {
			Skeleton skel = ( Skeleton ) LevelFactory.entities
					.get( "body_cannon_skeleton" + i );
			skel.setFgFade( false );

			if ( i % 2 == 1 ) {
				skel.setLocalRot( Util.PI / 6 );
			} else {
				skel.setLocalRot( -Util.PI / 6 );
			}
			buildCannon( skel, skel.getPositionPixel( ), 200, 200, 0.33f, 0.5f );

		}

	}
	
	
	private TimelineTweenMover rotateCircleMover(Skeleton skel){
		Timeline t = Timeline.createSequence( );


		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 10f )
				.ease( TweenEquations.easeNone ).target( Util.PI *2 )
				.start( ).delay( 0f ) );

	
		t.repeat( Tween.INFINITY, 0f );
		return  new TimelineTweenMover( t.start( ) );
	}
	
	void flamePlatformDecals(){
		TextureAtlas dragonObjects = WereScrewedGame.manager.getAtlas( "dragon_objects" );
		Sprite s;
		Platform p;
		String[] entities = {"balloon1_flame_plat","balloon2_flame_plat",
				"balloon3_flame_plat","tail1_flame_plat",
				"tail2_flame_plat","tail3_flame_plat"};
		for(int i =0; i < entities.length; ++i ){
			p = ( Platform ) LevelFactory.entities
					.get( entities[i] );
			s = dragonObjects.createSprite( "burner-med" );
			p.addFGDecal( s,new Vector2(-s.getWidth( )/2,-s.getHeight( )/2) );
			addFGEntity( p );
			p.addBehindParticleEffect( "fire_new", false, true ).setOffsetFromParent( 0, 75 ).start();
			p.setVisible( false, true );
		}
		
		
		// Joints the flame plats to their balloons, instead of making more skeleton
		for(int i = 1; i < 4; i++){
			p = ( Platform ) LevelFactory.entities
					.get( "tail" + i + "_flame_plat" );
			
			Platform balloon1 = (Platform) LevelFactory.entities.get("tail" +i+ "_balloon1");
			
			if(p != null && balloon1 != null){
				WeldJointDef weldJointDef = new WeldJointDef( );
				weldJointDef.initialize( p.body, balloon1.body, p.getPosition( ) );
				level.world.createJoint( weldJointDef );
			}
		}
		
		
	}

void buildBackground(){
		TextureAtlas clouds_sun_bg = WereScrewedGame.manager.getAtlas( "clouds_sun_bg" );
		TextureAtlas mountains_back_clouds = WereScrewedGame.manager.getAtlas( "mountains-back-clouds" );
		float frontTopCloudsY = 2800, midOrangeCloudsY = -400, bottomFrontCloudsY = -1000;
		float frontCloudVariation = 600;
		float numFrontClouds = 50;
		float xMax = 32000, xMin = -3000;
		float minCloudScale = 0.5f;
		float cloudScale = 1f/.75f;
		
		setClearColor( 105f/255f, 208f/255f, 255f/255f, 1f ); //SKY BLUE
		
		Random r = new Random();
		//loop variables
		BodyDef bdef; Body b; Entity e; ParallaxMover m; float yPos, scale; String cloudId;
		
		//TOP yellow big clouds
		for(int i = 0; i < numFrontClouds; ++i){
			bdef = new BodyDef();
			bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "top-front"+(r.nextInt( 5 )+1);
			e = new Entity("top-front-"+i,
								  new Vector2(),
								  clouds_sun_bg.findRegion( cloudId ),
								  b,
								  false,
								  0);
			scale = (r.nextFloat( )/2+minCloudScale)*cloudScale;
			e.sprite.setScale( scale * (r.nextBoolean( )?1f:-1f), r.nextFloat( )/2+0.5f );
			
			yPos = frontTopCloudsY + Util.binom( )*frontCloudVariation;
			m = new ParallaxMover( new Vector2(xMin,yPos),
												 new Vector2(xMax,yPos),
												 -0.0001f*r.nextFloat()-.00001f,
												 r.nextFloat(),
												 null,
												 true,
												 LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}
		
		
		//MID orange big cloud layer
		for(int i = 0; i < numFrontClouds; ++i){
			bdef = new BodyDef();
			bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "front1-"+(r.nextInt( 4 )+1);
			e = new Entity("front1-"+i,
								  new Vector2(),
								  clouds_sun_bg.findRegion( cloudId ),
								  b,
								  false,
								  0);
			scale = (r.nextFloat( )/2+minCloudScale)*cloudScale;
			e.sprite.setScale( scale * (r.nextBoolean( )?1f:-1f), r.nextFloat( )/2+0.5f );
			e.addFGDecal( e.sprite );
			e.sprite=null;
			addFGEntity( e );
			yPos = midOrangeCloudsY + Util.binom( )*frontCloudVariation;
			m = new ParallaxMover( new Vector2(xMin,yPos),
												 new Vector2(xMax,yPos),
												 -0.0001f*r.nextFloat()-.00001f,
												 r.nextFloat(),
												 null,
												 true,
												 LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}
		
		//bottom darker cloud layer
		for(int i = 0; i < numFrontClouds; ++i){
			bdef = new BodyDef();
			bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
			b = level.world.createBody( bdef );
			cloudId = "front2-"+(r.nextInt( 2 )+2); //FIX THIS, front2-1 is not in pack file
			e = new Entity("front1-"+i,
								  new Vector2(),
								  clouds_sun_bg.findRegion( cloudId ),
								  b,
								  false,
								  0);
			scale = (r.nextFloat( )/2+minCloudScale)*cloudScale;
			e.sprite.setScale( scale * (r.nextBoolean( )?1f:-1f), r.nextFloat( )/2+0.5f );
			yPos = bottomFrontCloudsY + Util.binom( )*frontCloudVariation;
			m = new ParallaxMover( new Vector2(xMin,yPos),
												 new Vector2(xMax,yPos),
												 //new Vector2( r.nextFloat( )*(xMax-xMin)+xMin, yPos ),
												 -0.0001f*r.nextFloat()-.00001f, r.nextFloat(), null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
			level.root.addLooseEntity( e );
		}
		

		// !Important
		level.initBackgroundRoot( );
		
		//bgGradient
		float xOffset = -450, yOffset = -900;
		bdef=new BodyDef();
		bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
		b=level.world.createBody( bdef );
		e=new Entity("bg-gradient",new Vector2(0,-500),null,b,false,0);
		
		e.changeSprite( Sprite.scale( clouds_sun_bg.createSprite( "bg-gradient" ), 140f,1 ) );//67.5f
		level.backgroundRootSkeleton.addLooseEntity( e );
		m = new ParallaxMover( new Vector2(xOffset,0+yOffset),
				 new Vector2(xOffset,-2048+yOffset),
				 0.0002f,0.00001f, level.camera, false, LinearAxis.VERTICAL );
		m.setLoopRepeat( false );
		e.setMoverAtCurrentState( m );
		
		
		//clouds behind mountains
		level.bgCamZoomScale = .1f;
		level.bgCamZoomMax = 1.5f;
		level.bgCamZoomMin = .5f;
		int numMountains = 3;
		float mountainW = (1859-100)*level.bgCamZoomMax, mountainY = 100;

		float alpha, aOffset;
		for(int i = 0; i < numMountains; ++i ){
			bdef=new BodyDef();
			bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
			b=level.world.createBody( bdef );
			e=new Entity("back-clouds"+i,new Vector2(),null,b,false,0);
			e.changeSprite( mountains_back_clouds.createSprite( "back-clouds" ) );
			level.backgroundRootSkeleton.addLooseEntity( e );
			aOffset=i*.0001f;
			alpha = (i)*(1f/(numMountains));//+aOffset;
			m = new ParallaxMover( new Vector2(mountainW,mountainY),
					new Vector2(-mountainW,mountainY),
					 0.00001f,alpha, null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
		}
		
		//mountains
		mountainW = 1275*level.bgCamZoomMax; mountainY = -200f;
		for(int i = 0; i < numMountains; ++i ){
			bdef=new BodyDef();
			bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
			b=level.world.createBody( bdef );
			e=new Entity("mountains"+i,new Vector2(),null,b,false,0);
			e.changeSprite( mountains_back_clouds.createSprite( "mountains" ) );
			level.backgroundRootSkeleton.addLooseEntity( e );
			aOffset=i*.0001f;
			alpha = (i)*(1f/(numMountains));//+aOffset;
			m = new ParallaxMover( new Vector2(mountainW,mountainY),
					new Vector2(-mountainW,mountainY),
					 0.00002f,alpha, null, true, LinearAxis.HORIZONTAL );
			e.setMoverAtCurrentState( m );
		}
		
		
		//Sun 50% = 2
		float sunScale = 1f;
		bdef=new BodyDef();
		bdef.fixedRotation=true;
			bdef.type=BodyType.StaticBody;
		b=level.world.createBody( bdef );
		e=new Entity("sun",new Vector2(),null,b,false,0);//0,2048-2*
		e.changeSprite( Sprite.scale( clouds_sun_bg.createSprite( "sun" ), sunScale ) );
		//e.setPosition( new Vector2().mul( Util.PIXEL_TO_BOX ) );
		float sunYPos = 2048-sunScale*e.sprite.getHeight( )+yOffset;
		m = new ParallaxMover( new Vector2(400,sunYPos),
				 new Vector2(400,-2048+sunYPos),
				 0.00009f,0.00001f, level.camera, false, LinearAxis.VERTICAL );
		e.setMoverAtCurrentState( m );
		m.setLoopRepeat( false );
		level.backgroundRootSkeleton.addLooseEntity( e );
	}
	
	void headDecals(){
		//head_skeleton
		Skeleton jaw_skeleton = ( Skeleton ) LevelFactory.entities
				.get( "fuck_jaw_skeleton" ),
				head_skeletonSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "head_skeleton" );
		TextureAtlas headAtlas = WereScrewedGame.manager.getAtlas( "head" );
		float scale = 3;//1f/.66f;
		//UPPER HEAD
		Sprite s;
		
		Vector2 headPos = new Vector2(-3500,-1460 );
		s = headAtlas.createSprite( "dragontop_left" );
		head_skeletonSkeleton.addFGDecal( Sprite.scale( s, scale ), new Vector2().add( headPos ) );
		head_skeletonSkeleton.addFGDecal( 
				Sprite.scale( headAtlas.createSprite( "dragontop_right" ), scale ), 
				new Vector2(s.getWidth( )*scale-10,1085).add( headPos ) );
		addFGSkeleton( head_skeletonSkeleton );
		
		//LOW HEAD/ JAW
		Vector2 pos = new Vector2(-1375,-615);
		s = headAtlas.createSprite( "dragonbottom_left" );
		jaw_skeleton.addFGDecal( Sprite.scale( s, scale ), new Vector2().add( pos ) );//959,615
		jaw_skeleton.addFGDecal( Sprite.scale( headAtlas.createSprite( "dragonbottom_right" ), scale ), 
				new Vector2(s.getWidth( )*scale-10,25).add( pos ) );
		addFGSkeleton( jaw_skeleton );
		
		
	}
	
	void tail1Decals(){
		Skeleton tail = (Skeleton)LevelFactory.entities.get( "tail_skeleton" );
		
		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "tail-fg" );
		
		tail.addFGDecal( Sprite.scale( tailAtlas.createSprite( "tail" ), 2), new Vector2(-1800,-400) );
		tail.fgSprite=null;
	//tail.setFgFade( true );
		addFGSkeleton( tail );
		
	}
	
	void tail2Decals(){
		////1189,431
		Skeleton tail2_skeleton = (Skeleton)LevelFactory.entities.get( "tail2_skeleton" );
		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "tail-fg" );
		tail2_skeleton.addFGDecal( Sprite.scale( tailAtlas.createSprite( "tail2" ), 2), new Vector2(-1206,-638) );//227,17
		tail2_skeleton.fgSprite=null;
		//tail2_skeleton.setFgFade( true );
		
		addFGSkeleton( tail2_skeleton );
	}
	
	void tail3Decals(){
		//tail3_skeleton
		Skeleton tail3_skeleton = (Skeleton)LevelFactory.entities.get( "tail3_skeleton" );
		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "tail-fg" );
		tail3_skeleton.addFGDecal( Sprite.scale( tailAtlas.createSprite( "tail3" ), 2), new Vector2(-1166,-765) );//227,17
		tail3_skeleton.fgSprite=null;
		//tail3_skeleton.setFgFade( true );
		
		addFGSkeleton( tail3_skeleton );
	}
	
	void bodyDecals(){
		Skeleton neck_skeleton = (Skeleton)LevelFactory.entities.get( "neck_skeleton" );
		TextureAtlas tailAtlas = WereScrewedGame.manager.getAtlas( "body-neck" );
		neck_skeleton.addFGDecal( Sprite.scale( tailAtlas.createSprite( "neck" ), 2f), new Vector2(-1167,-914) );//4,414
		neck_skeleton.fgSprite=null;
		//neck_skeleton.setFgFade( false );//3497.1770
		
		addFGSkeleton( neck_skeleton );
	}
	
	void neckDecal(){
		
	}
}
