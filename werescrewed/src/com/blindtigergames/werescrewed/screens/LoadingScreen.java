package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.WereScrewedGame;


/*
 * Doesn't work yet
 */

public class LoadingScreen extends Screen {

	/**
	 * 
	 */
	public LoadingScreen(){

		// two examples of loading stuff:
		//WereScrewedGame.manager.load("assets/data/common/player_b_f.png", Texture.class);
		//WereScrewedGame.manager.load("assets/data/common/sounds/jump.ogg", Sound.class);
		
		
		FileHandle common;
		if (Gdx.app.getType() == ApplicationType.Android) {
			WereScrewedGame.dirHandle = Gdx.files.internal("data/");
		} else {
		  // ApplicationType.Desktop ..
			WereScrewedGame.dirHandle = Gdx.files.internal("assets/data/");
		}
		
		String screenTag = "junk";
		loadFilesInDirectory(WereScrewedGame.dirHandle, screenTag);
		//System.out.println( WereScrewedGame.dirHandle.path( )  );
	}

	/**
	 * A recursive function to load all files in a given directory, and load the files
	 * directories in directories
	 * 
	 * @author Nick Patti
	 * 
	 * @param currentDirectory
	 * 			The current directory that the function is loading files from
	 * 
	 * @param screenTag
	 * 			indicates which screen is going to be loaded, which influences which
	 * 			file's contents are loaded
	 * 
	 * @return void
	 */
	private void loadFilesInDirectory(FileHandle currentDirectory, String screenTag) {
		Gdx.app.log("GOING DOWN", "now inside " + currentDirectory.name() );
		
		for (FileHandle entry: currentDirectory.list()) {
			Gdx.app.log( currentDirectory.name(), "found file " + entry.name() );
		   
			//if this file is named ".DS_Store", which like to show up on Macs, then FUCK OFF.
			if(entry.name( ).equals(".DS_Store")) continue;
			
			//if the entry is a directory, then go into this folder
			if(entry.isDirectory( )){
				
				//TODO: determine if we need to go into this folder
				
				//hop inside this directory
				loadFilesInDirectory(entry, screenTag);
			}
			
			//load the file that we're currently looking at
			String fileExtension = entry.extension( );
			
			//TODO: I don't think this is right...
			String fileName = currentDirectory.name() + entry.name();
			Gdx.app.log( "filename to be loaded", fileName );
			if(fileExtension.equals("png")){
				WereScrewedGame.manager.load(fileName, Texture.class);
			}else if(fileExtension.equals("ogg")){
				WereScrewedGame.manager.load(fileName, Sound.class);
			}
			
		}
		
		Gdx.app.log("GOING UP", "returning to " + currentDirectory.parent( ));
	}
	
	/*if(!entry.isDirectory( )){
	WereScrewedGame.manager.load( WereScrewedGame.dirHandle.path( )  + "/" + entry.name( ), Texture.class );
	//System.out.println( entry.name());
}


//TODO: better way to go into directories in directories
if(entry.name( ).equals( "common" )){
	common = Gdx.files.internal( WereScrewedGame.dirHandle.path( )  + "/common/" );
	
	for( FileHandle com: common.list( )){
		
		//TODO: load either texture or sound based on file extension
		String commonPathName = common.path() + "/" + com.name( );
		
		if(com.extension().equals("ogg")){
			WereScrewedGame.manager.load( commonPathName, Sound.class);
			Gdx.app.log("Sound Loaded", commonPathName);
		}
		else{
			WereScrewedGame.manager.load( commonPathName, Texture.class );
			Gdx.app.log("Texture Loaded", commonPathName);
		}
	}
}*/

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
		super.render(delta);

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
	public void dispose( ) {
		WereScrewedGame.manager.dispose();
	}

}
