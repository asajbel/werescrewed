package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.RopeBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.rope.Rope;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.util.Util;

public class GleedTestScreen extends Screen {
	
	Music music;
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new LevelFactory().load( filename );

		music = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/TrainJob.mp3" );

//		EventTrigger et = (EventTrigger) LevelFactory.entities.get( "et1" );
//		
//		System.out.println( et.name );
	}
	
	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		
		if(!music.isPlaying( )){
			music.play( );
			music.setLooping( true);
			music.setVolume( 0.3f );
		}
		//music.setLooping( true);
		
	}
	
}
