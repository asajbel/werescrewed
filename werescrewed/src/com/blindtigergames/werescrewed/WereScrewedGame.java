package com.blindtigergames.werescrewed;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.asset.AssetManager;
import com.blindtigergames.werescrewed.screens.ScreenManager;
import com.blindtigergames.werescrewed.screens.ScreenType;

public class WereScrewedGame extends Game {

	public static AssetManager manager = new AssetManager( );
	public static FileHandle dirHandle;

	public FPSLogger logger;
	
	private boolean restartFlag = false;

	@Override
	public void create( ) {
				
		WereScrewedGame.dirHandle = Gdx.files.internal( "data/" );
		
		startLoading(null);
		
		ScreenManager.getInstance( ).initialize( this );

		// ScreenManager.getInstance().show(Screen.INTRO);

		// ScreenManager.getInstance().show(Screen.LEVELTEST);

		// uncomment next line to bypass intro

		ScreenManager.getInstance( ).show( ScreenType.LOADING );

		logger = new FPSLogger( );

	}

	public void startLoading(String st){
		String screenTag;
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
					String file[] = s.split( "\\." );
					if ( file.length > 1 ) {
						// gets the extension
						String extension = file[1];
						// loads the file
						loadCurrentFile( extension, WereScrewedGame.dirHandle
								+ s );
					} else {
						Gdx.app.log( "Loading screen: ", s + "doesn't have an extension" );
					}
				}
			}
		}
	}
	
	/**
	 * A recursive function to load all files in a given directory, and load the
	 * files directories in directories
	 * 
	 * @author Nick Patti
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
	@SuppressWarnings("unused")
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
	 *            
	 * Note from Kevin: This should probably be moved into the AssetManager at some point.
	 */
	private void loadCurrentFile( String fileExtension, String fullPathName ) {
		Gdx.app.log("AssetManager", "Loading asset file ["+fullPathName+"]" );
		if ( fileExtension.equals( "png" ) ) {
			WereScrewedGame.manager.load( fullPathName, Texture.class );
			// Gdx.app.log( "Texture file loaded", fullPathName );

			// TODO: This will need to be adjusted when music files are loaded.
			// So far, I'm assuming if the file is an .mp3, it's a music file
		} else if ( fileExtension.equals( "ogg" ) ) {
			WereScrewedGame.manager.load( fullPathName, Sound.class );
			// Gdx.app.log( "Sound file loaded", fullPathName );

		} else if ( fileExtension.equals( "mp3" ) ) {
			WereScrewedGame.manager.load( fullPathName, Music.class );
			// Gdx.app.log( "Music file loaded", fullPathName );

		} else if ( fileExtension.equals( "pack" ) ) {
			WereScrewedGame.manager.loadAtlas( fullPathName );
			// Gdx.app.log( "Atlas pack file loaded", fullPathName );
			
		} else if (fileExtension.equals( "fnt" )){
			WereScrewedGame.manager.load( fullPathName, BitmapFont.class );
			// Gdx.app.log( "Font file loaded", fullPathName );
		}
	}
	
	@Override
	public void dispose( ) {
		super.dispose( );
		ScreenManager.getInstance( ).dispose( );
	}

	@Override
	public void render( ) {
		update( 0 );
		if ( Gdx.input.isKeyPressed( Keys.SHIFT_LEFT ) && Gdx.input.isKeyPressed( Keys.ESCAPE ) ){
			if ( !restartFlag ){
				restartFlag = true;
				restart( );
			}
		}else{
			restartFlag = false;
		}
		super.render( );
		if (Gdx.app.getType() == ApplicationType.Android) {
			logger.log( ); 
		}
//		logger.log( );
	}
	
	public void restart(){
		this.dispose();
		manager = new AssetManager( );
		ScreenManager.getInstance( ).initialize( this );
		ScreenManager.getInstance( ).show( ScreenType.LOADING );
	}

	public void update( float dT ) {
	}

}
