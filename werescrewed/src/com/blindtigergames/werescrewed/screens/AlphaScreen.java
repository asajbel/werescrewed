package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.audio.Music;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.level.LevelFactory;

public class AlphaScreen extends Screen {

	public ScreenType screenType;
	Music music;
	Music intro, loop;
	boolean introPlayed = false;
	

	public AlphaScreen( ) {
		super( );
		String filename = "data/levels/FinalPresentationLevel2.xml";
		level = new LevelFactory( ).load( filename );
		music = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/TrainJob.mp3" );
		loop = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/introTrain.mp3" );
		intro = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/loopTrain.mp3" );
		



	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		updateMusic();


		
		
		

	}
	
	private void updateMusic(){
		// Doesn't work perfectly, but its okay
		if(!introPlayed){
			intro.play( );
			introPlayed = true;
		} else {
			if(!intro.isPlaying( )){
				if(!loop.isPlaying( )){
					loop.play( );
					loop.setLooping( true );
				}
			}
		}
	}

}
