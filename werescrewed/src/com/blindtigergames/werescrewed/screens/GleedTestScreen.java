package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.particles.Steam;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.util.Util;

public class GleedTestScreen extends Screen {

	Music music;
	Steam testSteam;

	public GleedTestScreen( String name ) {
		super( );
		String filename = "data/levels/" + name + ".xml";
		level = new LevelFactory( ).load( filename );

		music = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/TrainJob.mp3" );

		Skeleton skel = ( Skeleton ) LevelFactory.entities.get( "skeleton2" );
		skel.body.setTransform( skel.body.getPosition( ).x, skel.body.getPosition( ).sub( 0f, 720 ).y, 0 );
		 skel.body.setType( BodyType.DynamicBody );
//		 skel.addMover( new RotateTweenMover(skel, 15f, Util.PI, 2f, true),
//		 RobotState.IDLE );

		Skeleton skel2 = ( Skeleton ) LevelFactory.entities.get( "skeleton4" );
		level.root.getSubSkeletonByName( "skeleton4" ).body
				.setType( BodyType.DynamicBody );

		level.player1.setPixelPosition(
				level.player1.getPositionPixel( ).x + 500,
				level.player1.getPositionPixel( ).y + 2000 );
		level.player2.setPixelPosition(
				level.player2.getPositionPixel( ).x + 500,
				level.player2.getPositionPixel( ).y + 2000 );

		// PathBuilder pb = new PathBuilder( );
		// skel.addMover( pb.begin( skel ).target( 0, 150, 3 ).target( 0, 0, 3 )
		// .build( ), RobotState.IDLE );

		PuzzleScrew puzzleScrew = new PuzzleScrew( "006", new Vector2(
				skel2.getPositionPixel( ).x, skel2.getPositionPixel( ).y ),
				100, skel2, level.world, 0, false );

		PuzzleRotateTweenMover rtm1 = new PuzzleRotateTweenMover( 1,
				Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );

		puzzleScrew.puzzleManager.addEntity( skel2 );
		puzzleScrew.puzzleManager.addMover( rtm1 );
		skel2.addScrewForDraw( puzzleScrew );

		// 2913, 2770

		testSteam = new Steam( "testSteam", new Vector2( 2913, 2770f ), null,
				null, false, 25, 50, level.world );

		// // Create anchor with start position and buffer as parameters
		// Anchor testAnchor = new Anchor( new Vector2(skel.getPositionPixel(
		// ).x, skel.getPositionPixel( ).y ),
		// new Vector2( 1000,
		// 1000 ) );
		// // Add to the universally accessible anchor list
		// AnchorList.getInstance( ).addAnchor( testAnchor );
		// // Set timer in steps
		// //testAnchor.setTimer( 200 );
		// // Activate it
		// testAnchor.activate( );

		Platform p = ( Platform ) LevelFactory.entities.get( "plat4" );
		p.oneSided = true;

		// skel.addMover( new RotateTweenMover( skel, 5f,
		// Util.PI / 2, 1f, true ),
		// RobotState.IDLE );

		// 2000, -80
		// System.out.println( skel.name );
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		testSteam.update( deltaTime );

		batch.setProjectionMatrix( level.camera.combined( ) );
		batch.begin( );
		testSteam.draw( batch, deltaTime );
		batch.end( );

		if ( !music.isPlaying( ) ) {
			music.play( );
			music.setLooping( true );
			music.setVolume( 0.3f );
		}
		// music.setLooping( true);

	}

}
