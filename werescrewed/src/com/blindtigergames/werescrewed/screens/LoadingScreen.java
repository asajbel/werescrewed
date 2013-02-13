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
		
		
		FileHandle common;
		if (Gdx.app.getType() == ApplicationType.Android) {
			WereScrewedGame.dirHandle = Gdx.files.internal("assets/data/");
		} else {
		  // ApplicationType.Desktop ..
			WereScrewedGame.dirHandle = Gdx.files.internal("assets/data/");
		}
		for (FileHandle entry: WereScrewedGame.dirHandle.list()) {
		   
			//if this file is named ".DS_Store", which like to show up on Macs, then FUCK OFF.
			if(entry.name( ).equals(".DS_Store")) continue;
			
			if(!entry.isDirectory( )){
				WereScrewedGame.manager.load( WereScrewedGame.dirHandle.path( )  + "/" + entry.name( ), Texture.class );
				//System.out.println( entry.name());
			}
			
			//TODO: better way to go into directories in directories
			if(entry.name( ).equals( "common" )){
				common = Gdx.files.internal( WereScrewedGame.dirHandle.path( )  + "/common/" );
				
				for( FileHandle com: common.list( )){
					
					if(com.name( ).equals("sounds")){
						WereScrewedGame.manager.load( common.path( ) + "/sounds/jump.ogg", Sound.class);
						//System.out.println( "soundloaded" );
					}
					else{
						WereScrewedGame.manager.load( common.path( )  + "/" + com.name( ), Texture.class );
					}
						//System.out.println( com.name());
				}
			}
		}
		//System.out.println( WereScrewedGame.dirHandle.path( )  );
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
