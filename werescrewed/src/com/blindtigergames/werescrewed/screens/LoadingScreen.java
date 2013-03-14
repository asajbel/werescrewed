package com.blindtigergames.werescrewed.screens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Label;

public class LoadingScreen extends Screen {

	private BitmapFont font = null;
	private int scaleSize = 7;
	private Label loadingLabel = null;
	private Label loadingCompleteLabel = null;
	private SpriteBatch batch = null;
	private String screenTag = null;

	/**
	 * Displays the loading screen and loads the appropriate contents for the
	 * next screen based on the screenTag that is submitted
	 * 
	 * @author Nick Patti
	 * 
	 * @param st
	 *            A string to designate which files to load for the next screen
	 */
	public LoadingScreen( String st ) {

		font = new BitmapFont( );
		font.scale( scaleSize );

		loadingLabel = new Label( "Loading... 0%", font );
		loadingCompleteLabel = new Label( "Press 'A'!!", font );
		batch = new SpriteBatch( );

		if ( st != null && !st.isEmpty( ) ) {
			screenTag = st;
		} else {
			screenTag = "commonLevel";
		}
		Gdx.app.log( "Loading assets for", screenTag );

		// THIS IS WHAT THE DIRECTORY SHOULD ALWAYS BE
		// THERE SHOULDN"T BE TWO FOLDERS
		WereScrewedGame.dirHandle = Gdx.files.internal( "data/" );

		// reads through the text file that is named
		// the same thing as the screenTag
		// and reads each line which is a path and loads that file
		FileHandle handle = Gdx.files.internal( "data/" + screenTag + ".txt" );		
		String split[] = handle.readString( ).split( "\\r?\\n" );
		for ( String s : split ) {
			s.replaceAll( "\\s", "" );
			if ( s.length( ) > 0 ) {
				if ( s.charAt( 0 ) != '#' ) {
					String ext;
					String fileAndExtension[] = s.split( "\\." );
					if ( fileAndExtension.length > 1 ) {
						// gets the extension
						ext = fileAndExtension[1];
						// loads the file
						loadCurrentFile( ext, WereScrewedGame.dirHandle
								+ s );
					} else {
						Gdx.app.log( "Loading screen: ", s + " doesn't have an extension" );
					}
					
					/*
					*/
				}
			}
		}

		// loadFilesInDirectory( WereScrewedGame.dirHandle, screenTag );

	}

	/**
	 * Only loads the contents of "common," created so that some of the other
	 * code didn't have to change.
	 * 
	 * @param none
	 */
	public LoadingScreen( ) {
		this( null );
	}

	/**
	 * A recursive function to load all files in a given directory, and load the
	 * files directories in directories
	 * 
	 * @author Nick Patti
	 * 
	 * @deprecated
	 * 
	 * @param currentDirectory
	 *            The current directory that the function is loading files from
	 * 
	 * @param screenTag
	 *            Indicates which screen is going to be loaded, which influences
	 *            which file's contents are loaded
	 * 
	 * @return void
	 */
	private void loadFilesInDirectory( FileHandle currentDirectory,
			String screenTag ) {
		// Gdx.app.log( "GOING DOWN", "now inside " + currentDirectory.name( )
		// );

		for ( FileHandle entry : currentDirectory.list( ) ) {
			// Gdx.app.log( currentDirectory.name( ), "found file " +
			// entry.name( ) );

			// figure out what it means to be a file I don't care about
			String entryName = entry.name( );
			boolean IDontCareAboutThisFile = entryName.equals( "bodies" )
					|| entryName.equals( "entities" )
					|| entryName.equals( ".DS_Store" );

			if ( IDontCareAboutThisFile )
				continue;

			// determine if we want to go into this directory
			// TODO: The string interpretation of screenTag will go here
			boolean IWannaLoadTheseFiles = entryName.equals( "common" )
					|| entryName.equals( "sounds" )
					|| entryName.equals( "levels" )
					|| entryName.equals( screenTag );

			if ( IWannaLoadTheseFiles )
				loadFilesInDirectory( entry, screenTag );

			// load the file that we're currently looking at
			String fileExtension = entry.extension( );
			String fullPathName = currentDirectory.parent( ) + "/"
					+ currentDirectory.name( ) + "/" + entry.name( );

			loadCurrentFile( fileExtension, fullPathName );

		}

		// Gdx.app.log( "GOING UP", "returning to " + currentDirectory.parent( )
		// );
	}

	/**
	 * A small babby function to load the current file
	 * 
	 * @author Nick Patti
	 * 
	 * @param fileExtension
	 *            The file extension, which is used to choose whether to load
	 *            the file a texture, sound, or music object.
	 * 
	 * @param fullPathName
	 *            The full path name of the file to be passed into the load
	 *            function
 	 * @param listedPathName
 	 * 			  The path to call when loading the texture from the AssetManager.
 	 * 			  Usually will be the same as fullPathName, but may differ for alt textures.            
	 */
	private void loadCurrentFile( String fileExtension, String fullPathName , String listedPathName) {
		if ( fileExtension.equals( "png" ) ) {
			WereScrewedGame.manager.load( fullPathName, Texture.class );
			Gdx.app.log( "Texture file loaded", fullPathName );

		} else if ( fileExtension.equals( "ogg" ) ) {
			WereScrewedGame.manager.load( fullPathName, Sound.class );
			Gdx.app.log( "Sound file loaded", fullPathName );

		} else if ( fileExtension.equals( "mp3" ) ) {
			WereScrewedGame.manager.load( fullPathName, Music.class );
			//Gdx.app.log( "Music file loaded", fullPathName );

		} else if ( fileExtension.equals( "pack" ) ) {
			WereScrewedGame.manager.loadAtlas( fullPathName );
			Gdx.app.log( "Atlas pack file loaded", fullPathName );
		}
		else if ( fileExtension.equals( "fnt" )){
			WereScrewedGame.manager.loadFont( fullPathName );
			//Gdx.app.log( "Bitmap pack file loaded", fullPathName );
		}
	}

	//Simple overloading for when you only have an extension and pathname.
	private void loadCurrentFile( String fE, String fPN){
		loadCurrentFile( fE, fPN, fPN);
	}
	
	/**
	 * Runs every frame tick. Loads the assets that are queued in
	 * WereScrewedGame.manager, and moves to the next screen. Shows how much of
	 * the assets have been loaded.
	 * 
	 * @author Ranveer, Nick, and Jen
	 * 
	 * @return void
	 */
	@Override
	public void render( float delta ) {
		super.render( delta );

		// tell me how much has loaded
		float percentLoaded = WereScrewedGame.manager.getProgress( ) * 100;
		loadingLabel.setCaption( "Loading... " + ( int ) percentLoaded + "%" );

		batch.begin( );
		
		//begin loading the assets
		if ( WereScrewedGame.manager.update( ) ) { 
			
			//assets have been loaded!
			loadingLabel.setCaption("Loading Complete!!");
			//loadingCompleteLabel.draw( batch );
		
			// TODO: Use the screenTag to pick which screen to go to next
			if ( screenTag != null && screenTag.equals( "level1" ) ) {
				ScreenManager.getInstance( ).show( ScreenType.LEVEL_1 );
			} else {
				ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );
			}
			
		}

		// draw the label on the screen
		loadingLabel.draw( batch );
		batch.end( );
	}

	@Override
	public void resize(int width, int height){
		
		//set position of the loading label
		//TODO: Figure out a way to keep it in the center of the screen without resizing
		int loadingLabelX = width/2 - loadingLabel.getWidth()/2;
		int loadingLabelY = height/2 + loadingLabel.getHeight();
		loadingLabel.setX(loadingLabelX);
		loadingLabel.setY(loadingLabelY);
		
		//set position of loading complete label
		int loadingCompleteLabelX = width/2 - loadingCompleteLabel.getWidth()/2;
		int loadingCompleteLabelY = height/2 - loadingCompleteLabel.getHeight()/3;
		loadingCompleteLabel.setX(loadingCompleteLabelX);
		loadingCompleteLabel.setY(loadingCompleteLabelY);
	}
	

	@Override
	public void dispose( ) {
		WereScrewedGame.manager.dispose( );
	}

}
