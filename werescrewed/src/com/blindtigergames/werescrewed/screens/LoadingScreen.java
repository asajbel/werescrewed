package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Label;


/*
 * Doesn't work yet
 */

public class LoadingScreen extends Screen {
	
	private BitmapFont font 	= null;
	private int scaleSize 		= 10;
	private Label loadingLabel 	= null;
	private SpriteBatch batch 	= null;
	
	
	/**
	 * Loading Screen Constructor
	 */
	public LoadingScreen(){		
		
		font = new BitmapFont();
		font.scale(scaleSize);
		
		loadingLabel = new Label("Loading... 0%", font);
		batch = new SpriteBatch();
		
		if (Gdx.app.getType() == ApplicationType.Android) {
			WereScrewedGame.dirHandle = Gdx.files.internal("data/");
		} else {
		  // ApplicationType.Desktop ..
			WereScrewedGame.dirHandle = Gdx.files.internal("assets/data/");
		}
		
		String screenTag = "level2";
		loadFilesInDirectory(WereScrewedGame.dirHandle, screenTag);
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
	 * 			Indicates which screen is going to be loaded, which influences which
	 * 			file's contents are loaded
	 * 
	 * @return void
	 */
	private void loadFilesInDirectory(FileHandle currentDirectory, String screenTag) {
		Gdx.app.log("GOING DOWN", "now inside " + currentDirectory.name() );
		
		for (FileHandle entry: currentDirectory.list()) {
			Gdx.app.log( currentDirectory.name(), "found file " + entry.name() );
		   
			//figure out what it means to be a file I don't care about
			String entryName = entry.name();
			boolean IDontCareAboutThisFile = entryName.equals("bodies") ||
					entryName.equals("entities") || entryName.equals(".DS_Store");
			
			if(IDontCareAboutThisFile) continue;
			
			//determine if we want to go into this directory
			//TODO: The string interpretation of screenTag will go here
			boolean IWannaLoadTheseFiles = 
					entryName.equals("common") || entryName.equals("sounds") ||
					entryName.equals("levels") || entryName.equals(screenTag);
			
			if(IWannaLoadTheseFiles) loadFilesInDirectory(entry, screenTag);
			
			//load the file that we're currently looking at
			String fileExtension = entry.extension( );
			String fullPathName = currentDirectory.parent() + "/" + currentDirectory.name() + "/" + entry.name();

			loadCurrentFile( fileExtension, fullPathName );
			
		}
		
		Gdx.app.log("GOING UP", "returning to " + currentDirectory.parent( ));
	}

	/**
	 * A small babby function to load the current file
	 * 
	 * @author Nick Patti
	 * 
	 * @param fileExtension
	 * 			The file extension, which is used to choose whether to load the file
	 * 			a texture, sound, or music object.
	 * 
	 * @param fullPathName
	 * 			The full path name of the file to be passed into the load function
	 */
	private void loadCurrentFile( String fileExtension, String fullPathName ) {
		if(fileExtension.equals("png")){
			WereScrewedGame.manager.load(fullPathName, Texture.class);
			Gdx.app.log( "Texture file loaded", fullPathName );
			
		//TODO: This will need to be adjusted when music files are loaded.
		//So far, I'm assuming if the file is an .mp3, it's a music file
		}else if(fileExtension.equals("ogg")){
			WereScrewedGame.manager.load(fullPathName, Sound.class);
			Gdx.app.log( "Sound file loaded", fullPathName );
			
		}else if(fileExtension.equals("mp3")){
			WereScrewedGame.manager.load(fullPathName, Music.class);
			Gdx.app.log( "Music file loaded", fullPathName );
		}
	}
	

	/**
	 * Runs every frame tick. Loads the assets that are queued in WereScrewedGame.manager,
	 * and moves to the next screen. Shows how much of the assets have been loaded.
	 * 
	 * @author Ranveer, Nick, and Jen
	 * 
	 * @return void
	 */
	@Override
	public void render(float delta) {
		super.render(delta);
		
		//tell me how much has loaded
		float percentLoaded = WereScrewedGame.manager.getProgress( ) * 100;
		loadingLabel.setCaption("Loading... " + (int)percentLoaded + "%");
		
		//draw the label on the screen
		batch.begin( );
		loadingLabel.draw( batch );
		batch.end();
		
		//begin loading the assets
		if ( WereScrewedGame.manager.update( ) ) { 
			
			//assets have been loaded!
			Gdx.app.log( "LoadingScreen.render", "Loading Complete!!" );
			
			//go to the physics screen
			ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );
		}

	}
	
	@Override
	public void resize(int width, int height){
		int centerX = width/2 - loadingLabel.getWidth()/2;
		int centerY = height/2 + loadingLabel.getHeight()/2;
		loadingLabel.setX(centerX);
		loadingLabel.setY(centerY);
	}
	
	@Override
	public void dispose( ) {
		WereScrewedGame.manager.dispose();
	}

}
