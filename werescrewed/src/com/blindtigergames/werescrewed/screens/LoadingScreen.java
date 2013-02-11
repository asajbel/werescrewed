package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.WereScrewedGame;


/*
 * Doesn't work yet
 */

public class LoadingScreen implements com.badlogic.gdx.Screen {

	/**
	 * 
	 */
	public LoadingScreen(){
		WereScrewedGame.manager.load("assets/data/common/player_b_f.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/player_r_m.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/rletter.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/screw.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/TilesetTest.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/test01.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/jumping_man.png", Texture.class);
		WereScrewedGame.manager.load("assets/data/common/sounds/jump.ogg", Sound.class);
		Gdx.app.log( "LoadingScreen", "Assets queued for loading..." );
	}

	/**
	 * Runs every frame tick. Loads the assets that are queued in WereScrewedGame.manager,
	 * and moves to the next screen
	 * 
	 * @author Ranveer and Nick
	 * 
	 * @return void
	 */
	@Override
	public void render(float delta) {

		// Clear the screen
		Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );

		//begin loading the assets
		if ( WereScrewedGame.manager.update( ) ) { 
			
			//assets have been loaded!
			Gdx.app.log( "LoadingScreen.render", "Loading Complete!!" );
			
			//go to the physics screen
			ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		}
		Gdx.app.log( "LoadingScreen.render", "Loading... ");

	}

	@Override
	public void resize( int width, int height ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide( ) {
		// WereScrewedGame.manager.unload("data/loading.pack");
	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose( ) {
		WereScrewedGame.manager.dispose();
	}

}
