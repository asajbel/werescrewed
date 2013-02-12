package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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

		// two examples of loading stuff:
		//WereScrewedGame.manager.load("assets/data/common/player_b_f.png", Texture.class);
		//WereScrewedGame.manager.load("assets/data/common/sounds/jump.ogg", Sound.class);
		
		
		FileHandle dirHandle;
		if (Gdx.app.getType() == ApplicationType.Android) {
		  dirHandle = Gdx.files.internal("data/common/");
		} else {
		  // ApplicationType.Desktop ..
		  dirHandle = Gdx.files.internal("assets/data/common/");
		}
		for (FileHandle entry: dirHandle.list()) {
		   
			if(!entry.isDirectory( ))
				WereScrewedGame.manager.load( dirHandle.path( )  + "/" + entry.name( ), Texture.class );
			
			//TODO: better way to go into directories in directories
			if(entry.name( ).equals("sounds"))
				WereScrewedGame.manager.load(dirHandle.path( ) + "/sounds/jump.ogg", Sound.class);
			
		}
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
			ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );
		}
		//Gdx.app.log( "LoadingScreen.render", "Loading... ");

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
