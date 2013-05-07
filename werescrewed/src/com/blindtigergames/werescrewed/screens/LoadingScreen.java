package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;

public class LoadingScreen extends Screen {

	public ScreenType screenType;
	private BitmapFont font = null;
	private int scaleSize = 7;
	private Label loadingLabel = null;
	private Label loadingCompleteLabel = null;
	private SpriteBatch batch = null;
	private String screenTag = null;
	private Entity loadingBar;
	private int screenWidth;
	private int screenHeight;
	
	private int timer = 0;
	private int currIndex = 0;
	private ArrayList<Texture> storyBoardArray = new ArrayList<Texture>();
	private int currLevel = 0;

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

		if ( st != null && !st.isEmpty( ) ) {
			screenTag = st;
			if(screenTag.equals( "level1" )){
				currLevel = 1;
			}
		} else {
			screenTag = "commonLevel";
		}
		Gdx.app.log( "Loading assets for", screenTag );
		
		if(currLevel == 1){
			// check for level1
			WereScrewedGame.manager.load( "data/common/slides/slide1_intro.png", Texture.class );
			WereScrewedGame.manager.load( "data/common/slides/slide2_audience.png", Texture.class );
			WereScrewedGame.manager.load( "data/common/slides/slide3_alphabot.png", Texture.class );
			WereScrewedGame.manager.load( "data/common/slides/slide4_players.png", Texture.class );
			
			WereScrewedGame.manager.finishLoading( );
			
			storyBoardArray.add( WereScrewedGame.manager.get( "data/common/slides/slide1_intro.png", Texture.class ) );
			storyBoardArray.add( WereScrewedGame.manager.get( "data/common/slides/slide2_audience.png", Texture.class ) );
			storyBoardArray.add( WereScrewedGame.manager.get( "data/common/slides/slide3_alphabot.png", Texture.class ) );
			storyBoardArray.add( WereScrewedGame.manager.get( "data/common/slides/slide4_players.png", Texture.class ) );
		}
		
		
		//stage = new Stage( );
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

		if ( WereScrewedGame.manager.isAtlasLoaded( "common-textures" ) && loadingBar.sprite == null ) {
			TextureRegion screwTex = WereScrewedGame.manager.getAtlas( "common-textures" ).findRegion( "flat_head_circular" );
			loadingBar.sprite = loadingBar.constructSprite( screwTex );
			loadingBar.sprite.setPosition( screenWidth / 2 - loadingBar.sprite.getWidth( ) / 2, 
											screenHeight / 3 - loadingBar.sprite.getHeight( ) / 2 );
		} else if ( loadingBar.sprite != null ) {
			loadingBar.sprite.setPosition( screenWidth / 2 - loadingBar.sprite.getWidth( ) / 2, 
											screenHeight / 3 - loadingBar.sprite.getHeight( ) / 2 );
			loadingBar.sprite.setRotation( -1080 * WereScrewedGame.manager.getProgress( ) );
		}
		
		batch.begin( );
		
		//begin loading the assets
		if ( WereScrewedGame.manager.update( ) ) { 
			
			if(currLevel == 0){
			//assets have been loaded!
			 loadingLabel.setCaption("Loading Complete!!");
			 loadingCompleteLabel.draw( batch );
			}
			// TODO: Use the screenTag to pick which screen to go to next
			if(storyBoardArray.size() == 0 || currIndex == storyBoardArray.size() - 1){
				if ( screenTag != null && screenTag.equals( "level1" ) ) {
					ScreenManager.getInstance( ).show( ScreenType.LEVEL_1 );
				} else if ( screenTag != null && screenTag.equals( "level2" ) ) {
					ScreenManager.getInstance( ).show( ScreenType.DRAGON );
				}else {
					ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU ); 
				}
			}
			
		}
		
		
		
		timer++;
		if(timer > 100){
			timer = 0;
			if( !(currIndex == storyBoardArray.size() - 1) ){
				currIndex++;
			}
			
		}
		
		if(currLevel == 1){
			batch.draw( storyBoardArray.get(currIndex),  screenWidth / 4,  screenHeight / 4 );
		}else{
		// draw the label on the screen
			loadingLabel.draw( batch );
		 	loadingBar.sprite.draw( batch );
		}
		batch.end( );
	}

	@Override
	public void resize(int width, int height){
		screenWidth = width;
		screenHeight = height; 
		
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
		//WereScrewedGame.manager.dispose( );
	}

	@Override
	public void show( ) {
		font = new BitmapFont( );
		font.scale( 1.0f );

		loadingLabel = new Label( "Loading... 0%", font );
		loadingCompleteLabel = new Label( "Press 'A'!!", font );
		batch = new SpriteBatch( );

		int width = Gdx.graphics.getWidth( );
		int height = Gdx.graphics.getHeight( );
		
		loadingBar = new Entity( "loadingScrew", null , null, null, false );

		WereScrewedGame.dirHandle = Gdx.files.internal( "data/" );

		// reads through the text file that is named
		// the same thing as the screenTag
		// and reads each line which is a path and loads that file
		FileHandle handle = Gdx.files.internal( "data/" + screenTag + ".txt" );		
		String split[] = handle.readString( ).split( "\\r?\\n" );
		for ( String s : split ) {
			s.replaceAll( "\\s", "" );
			if ( s.length( ) > 0 ) {
				if ( s.charAt( 0 ) == '#' ) {
					//A comment
					continue;
				}else if (s.charAt(0)=='@'){
					//special case for level parameters
					loadLevelParameter(s);
				}else{
					//A regular file
					loadFromPath(s);
				}
			}
		}
	}
	
	private void loadLevelParameter(String s){
		/*format for level options: 
			@ fg /path/to/fg/tex
			@ bg /path/to/bg/tex
			@ outline /path/to/outline/tex	
		*/
		String[] options = s.split( "\\s" );
		loadFromPath( options[2] );
		WereScrewedGame.manager.finishLoading();
		Texture tex = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+options[2], Texture.class );
		if(options[1].equals("fg")){
			WereScrewedGame.manager.setLevelRobotFGTex( tex );
		}else if(options[1].equals("bg")){
			WereScrewedGame.manager.setLevelRobotBGTex( tex );
		}else if(options[1].equals("outline")){
			WereScrewedGame.manager.setLevelRobotOutlineTex( tex );
		}
	}
	
	private void loadFromPath(String s){
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
			if(!WereScrewedGame.manager.isLoaded( fullPathName, Texture.class )){
				WereScrewedGame.manager.load( fullPathName, Texture.class );
				//Gdx.app.log( "Texture file loaded", fullPathName );
			}

		} else if ( fileExtension.equals( "ogg" ) ) {
			if(!WereScrewedGame.manager.isLoaded( fullPathName, Sound.class )){
				WereScrewedGame.manager.load( fullPathName, Sound.class );
				//Gdx.app.log( "Sound file loaded", fullPathName );
			}

		} else if ( fileExtension.equals( "mp3" ) ) {
			if(!WereScrewedGame.manager.isLoaded( fullPathName, Music.class )){
				WereScrewedGame.manager.load( fullPathName, Music.class );
				//Gdx.app.log( "Music file loaded", fullPathName );
			}

		} else if ( fileExtension.equals( "pack" ) ) {
			FileHandle fileHandle = Gdx.files.internal( fullPathName );
			
			if(!WereScrewedGame.manager.isAtlasLoaded( fileHandle.nameWithoutExtension( ) )){
				WereScrewedGame.manager.loadAtlas( fullPathName );
				//Gdx.app.log( "Atlas pack file loaded", fullPathName );
			}
		}
		else if ( fileExtension.equals( "fnt" )){
			if(!WereScrewedGame.manager.isLoaded( fullPathName )){
				WereScrewedGame.manager.loadFont( fullPathName );
			}
			//Gdx.app.log( "Bitmap pack file loaded", fullPathName );
		}
		else if ( fileExtension.equals( "palette" )){
			String[] path = fullPathName.split( "\\." );
			String colorName = path[0].substring( WereScrewedGame.dirHandle.name( ).length( ) );
			WereScrewedGame.manager.addToPalette( colorName );
			//Gdx.app.log( "Color Loaded", colorName );
		}
		else if ( fileExtension.equals( "p" )){ //load a particle effect
			String[] path = fullPathName.split( "\\." );
			String effectName = path[0].substring( WereScrewedGame.dirHandle.name( ).length( ) );
			WereScrewedGame.manager.loadParticleEffect( effectName );
			//Gdx.app.log( "Color Loaded", colorName );
		}
//		else if ( fileExtension.equals( "skel" )){
//			String[] path = fullPathName.split( "\\." );
//			String skelName = path[0].substring( WereScrewedGame.dirHandle.name( ).length( ) );
//			WereScrewedGame.manager.loadSpineSkeleton( skeletonName, atlas )
//		}
	}

	//Simple overloading for when you only have an extension and pathname.
	private void loadCurrentFile( String fE, String fPN){
		loadCurrentFile( fE, fPN, fPN);
	}
}
