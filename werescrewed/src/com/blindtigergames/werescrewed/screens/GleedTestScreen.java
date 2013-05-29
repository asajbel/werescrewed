package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.util.Util;

public class GleedTestScreen extends Screen {

	public ScreenType screenType;
	Music music;
	Music intro, loop;
	boolean introPlayed = false;

	Steam testSteam, steam2;

	Platform fallingGear1;
	RevoluteJoint fg1;
	Fire f1, f2, f3;
	int fireCounter = 0;
	EventTrigger et;
	boolean etTriggered = false;

	public GleedTestScreen( String name ) {
		super( );
		String filename = "data/levels/" + name + ".xml";
		level = new LevelFactory( ).load( filename );

		music = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/TrainJob.mp3" );

		loop = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/introTrain.mp3" );

		intro = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/loopTrain.mp3" );

		Platform plat = ( Platform ) LevelFactory.entities
				.get( "structureplat" );
		plat.quickfixCollisions( );

		// Platform fallinggear1 = (Platform) LevelFactory.entities.get(
		// "fallinggear1" );
		// System.out.println( fallinggear1.name + " , " +
		// fallinggear1.getPositionPixel( ));

		Skeleton skel1 = ( Skeleton ) LevelFactory.entities.get( "skeleton1" );

		fallingGear1 = new PlatformBuilder( level.world ).name( "fallingGear1" )
				.type( EntityDef.getDefinition( "gearSmall" ) )
				.position( 496.0f, 400.0f )
				.texture( EntityDef.getDefinition( "gearSmall" ).getTexture( ) )
				.solid( true ).dynamic( true ).buildComplexPlatform( );

		fallingGear1.setCrushing( true );

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( fallingGear1.body, skel1.body,
				fallingGear1.body.getWorldCenter( ) );
		revoluteJointDef.enableMotor = false;
		fg1 = ( RevoluteJoint ) level.world.createJoint( revoluteJointDef );

		f1 = new Fire( "Fire1", new Vector2( 600f, 1850.0f ), 15, 60,
				level.world, true );

		f2 = new Fire( "Fire1", new Vector2( 900f, 1850.0f ), 15, 60,
				level.world, true );
		f2.flip( );

		f3 = new Fire( "Fire1", new Vector2( 1200f, 1850.0f ), 15, 60,
				level.world, true );

		et = ( EventTrigger ) LevelFactory.entities.get( "et1" );
		// StructureScrew ss = ( StructureScrew ) LevelFactory.entities.get(
		// "structurescrew1" );
		// et.addEntityToTrigger( ss );
		// et.addBeginIAction( new DestoryJointAction( fg1 ) );

		// fallinggear1.getParentSkeleton( ).
		Skeleton skel = ( Skeleton ) LevelFactory.entities.get( "skeleton2" );
		// skel.body.setType( BodyType.DynamicBody );
		// PrismaticJointDef pj = new PrismaticJointDef( );
		// pj.bodyA = level.root.body;
		// pj.bodyB = skel.body;
		// pj.collideConnected = false;
		// pj.localAxisA.set( 0, 1 );
		// pj.enableLimit = false;
		// pj.upperTranslation = 1024;
		// pj.lowerTranslation = 0;
		// level.world.createJoint( pj );
		skel.addMover( new RotateTweenMover( skel, 15f, Util.PI, 2f, true ),
				RobotState.IDLE );

		Skeleton skel2 = ( Skeleton ) LevelFactory.entities.get( "skeleton4" );
		// skel2.body.setType( BodyType.DynamicBody );

		// PathBuilder pb = new PathBuilder( );
		// skel.addMover( pb.begin( skel ).target( 0, 150, 3 ).target( 0, 0, 3 )
		// .build( ), RobotState.IDLE );

		PuzzleScrew puzzleScrew = new PuzzleScrew( "006", new Vector2(
				skel2.getPositionPixel( ).x, skel2.getPositionPixel( ).y ),
				100, skel2, level.world, 0, false, Vector2.Zero );

		PuzzleRotateTweenMover rtm1 = new PuzzleRotateTweenMover( 1, -1
				* Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );

		puzzleScrew.puzzleManager.addEntity( skel2 );
		puzzleScrew.puzzleManager.addMover( rtm1 );
		skel2.addScrewForDraw( puzzleScrew );

		// 1680, 750

		testSteam = new Steam( "testSteam", new Vector2( 2913, 2770f ), 25, 50,
				level.world );
		steam2 = new Steam( "steam2", new Vector2( 1680, 780 ), 25, 50,
				level.world );
		// // Create anchor with start position and buffer as parameters
		// Anchor testAnchor = new Anchor( new Vector2(
		// skel.getPositionPixel( ).x, skel.getPositionPixel( ).y ),
		// new Vector2( 1000, 1000 ) );
		// // Add to the universally accessible anchor list
		// AnchorList.getInstance( ).addAnchor( testAnchor );
		// // Set timer in steps
		// // testAnchor.setTimer( 200 );
		// // Activate it
		// testAnchor.activate( );

		// skel.addMover( new RotateTweenMover( skel, 5f,
		// Util.PI / 2, 1f, true ),
		// RobotState.IDLE );

	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		testSteam.update( deltaTime );
		fallingGear1.update( deltaTime );
		fireCounter++;
		if ( fireCounter > 150 ) {
			f1.flip( );
			f2.flip( );
			f3.flip( );

			fireCounter = 0;
		}

		if ( !etTriggered ) {
			if ( et.isActivated( ) ) {
				if ( fg1 != null ) {
					level.world.destroyJoint( fg1 );
					fg1 = null;
					etTriggered = true;
				}
			}
		}

		batch.setProjectionMatrix( level.camera.combined( ) );
		batch.begin( );
		testSteam.draw( batch, deltaTime, level.camera );
		steam2.draw( batch, deltaTime, level.camera );
		fallingGear1.draw( batch, deltaTime, level.camera );
		f1.draw( batch, deltaTime, level.camera );
		f2.draw( batch, deltaTime, level.camera );
		f3.draw( batch, deltaTime, level.camera );
		batch.end( );

		// Doesn't work perfectly, but its okay
		if ( !introPlayed ) {
			intro.play( );
			introPlayed = true;
		} else {
			if ( !intro.isPlaying( ) ) {
				if ( !loop.isPlaying( ) ) {
					loop.play( );
					loop.setLooping( true );
				}
			}
		}

	}

}
