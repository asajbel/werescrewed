package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;

public class LoadingScreen extends Screen {

	public ScreenType screenType;
	private BitmapFont font = null;
	private Label loadingLabel = null;
	private Label loadingCompleteLabel = null;
	private Label pressStart = null;
	private SpriteBatch batch = null;
	private String screenTag = null;
	private Entity loadingBar;
	private ScreenType sT_trophy;

	private int timer = 0;
	private int currIndex = 0;
	private ArrayList< Texture > storyBoardArray = new ArrayList< Texture >( );
	private int currLevel = 0;
	
	private OrthographicCamera camera = null;
	
	private boolean initialLoadFinished = false;
	

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
	//WereScrewedGame.manager.clear( );
		if ( st != null && !st.isEmpty( ) ) {
			screenTag = st;
			if ( screenTag.equals( "level1" ) ) {
				currLevel = 1;
			} else if ( screenTag.equals( "level2" ) ) {
				currLevel = 2;
			} else{
				String[] parts = screenTag.split( " " );
				if(parts[0].equals( "trophy" )){
					screenTag = "menu";
					if ( parts[1].equals( "level2" ) ) {
						sT_trophy = ScreenType.TROPHY_1;
						currLevel = 99;
					}
					else if ( parts[1].equals( "level3" ) ) {
						sT_trophy = ScreenType.TROPHY_END;
					}
				}
			}
		} else {
			screenTag = "commonLevel";
		}

		// loadFilesInDirectory( WereScrewedGame.dirHandle, screenTag );
		setClearColor( 40, 40, 40, 255 );

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
		
		if(!initialLoadFinished){
			// check for level1
			if ( currLevel > 0 ) {
				if( currLevel == 1 ) {
					WereScrewedGame.manager.load(
							"data/common/slides/slide1_intro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide2_audience.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide3_alphabot.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide4_players.png", Texture.class );
		
					WereScrewedGame.manager.finishLoading( );
					
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide0_intro_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide1_intro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide1_intro_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide2_audience.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide2_audience_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide3_alphabot.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide3_alphabot_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide4_players.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide4_players_text.png", Texture.class ) );
					//duplicate to stay on last slide longer
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide4_players_text.png", Texture.class ) );
				} if (currLevel == 2 ){
					WereScrewedGame.manager.load(
							"data/common/slides/slide1_dragon.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide2_dragon.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide3_dragon.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide4_dragon.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide5_dragon.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide6_dragon.png", Texture.class );
		
					WereScrewedGame.manager.finishLoading( );

					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide0_dragon_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide1_dragon.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide1_dragon_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide2_dragon.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide2_dragon_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide3_dragon.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide3_dragon_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide4_dragon.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide4_dragon_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide5_dragon.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide5_dragon_text.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide6_dragon.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide6_dragon_text.png", Texture.class ) );
					//duplicate to stay on last slide longer
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide6_dragon_text.png", Texture.class ) );
				} if (currLevel == 99 ){
					WereScrewedGame.manager.load(
							"data/common/slides/slide1_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide2_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide3_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide4_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide5_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide6_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide7_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide8_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide9_outro.png", Texture.class );
					WereScrewedGame.manager.load(
							"data/common/slides/slide10_outro.png", Texture.class );
		
					WereScrewedGame.manager.finishLoading( );
		
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide1_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide2_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide3_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide4_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide5_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide6_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide7_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide8_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide9_outro.png", Texture.class ) );
					storyBoardArray.add( WereScrewedGame.manager.get(
							"data/common/slides/slide10_outro.png", Texture.class ) );
				}
				
				for (Texture t : storyBoardArray) {
					t.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear ); 
				}
							
				pressStart = new Label( "Press Start To Volunteer!",
							WereScrewedGame.manager.getFont( "longdon" ) );
				pressStart.setX( width / 2 - pressStart.getWidth( ) / 2 );
				pressStart.setY( height / 4 );

				// debug turning character select off until someone else can finish
				// it
			}
			initialLoadFinished=true;
		}

		

		if ( WereScrewedGame.manager.isAtlasLoaded( "common-textures" )
				&& loadingBar.sprite == null ) {
			TextureRegion screwTex = WereScrewedGame.manager.getAtlas(
					"common-textures" ).findRegion( "flat_head_circular" );
			loadingBar.sprite = loadingBar.constructSprite( screwTex );
			loadingBar.sprite.setPosition(
					width / 2 - loadingBar.sprite.getWidth( ) / 2,
					height / 3 - loadingBar.sprite.getHeight( ) / 2 );
		} else if ( loadingBar.sprite != null ) {
			loadingBar.sprite.setPosition(
					width / 2 - loadingBar.sprite.getWidth( ) / 2,
					height / 3 - loadingBar.sprite.getHeight( ) / 2 );
			loadingBar.sprite.setRotation( -1080
					* WereScrewedGame.manager.getProgress( ) );
		}

		batch.begin( );

		// begin loading the assets
		if ( WereScrewedGame.manager.update( ) ) {

			// HIT ANY KEY TO SKIP
//			if ( Gdx.app.getInput( ).isTouched( ) ) {
//				ScreenManager.getInstance( ).show( ScreenType.LEVEL_1 );
//			}
			if ( currLevel < 0 ) {
				// assets have been loaded!
				loadingLabel.setCaption( "Loading Complete!!" );
				loadingCompleteLabel.draw( batch );
			}

			if ( storyBoardArray.size( ) == 0
					|| currIndex == storyBoardArray.size( ) - 1 ) {
				if ( screenTag != null && screenTag.equals( "level1" ) ) {
					ScreenManager.getInstance( ).show( ScreenType.LEVEL_1 );
				} else if ( screenTag != null && screenTag.equals( "level2" ) ) {
					ScreenManager.getInstance( ).show( ScreenType.DRAGON );
				} else if ( screenTag != null && screenTag.equals("menu") && sT_trophy != null){
					ScreenManager.getInstance( ).show( sT_trophy );
				} else {
					ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );
				}
			}
		}

		timer++;

		if ( timer > 300 ) {
			timer = 0;
			if ( !( currIndex == storyBoardArray.size( ) - 1 ) ) {
				currIndex++;
			}

		}
		if ( currLevel > 0 ) {
			int posX = width / 2
					- storyBoardArray.get( currIndex ).getWidth( ) / 2;
			int posY = height / 2
					- storyBoardArray.get( currIndex ).getHeight( ) / 2;
			batch.draw( storyBoardArray.get( currIndex ), posX, posY );
		} else {
			// draw the label on the screen
			loadingLabel.draw( batch );
			loadingBar.sprite.draw( batch );
		}
		batch.end( );
	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, WereScrewedGame.getWidth(), WereScrewedGame.getHeight() );
		batch.setProjectionMatrix( camera.combined );
		if ( currLevel > 0 ) {
//			pressStart.setX( width / 2 - pressStart.getWidth( ) / 2 );
//			pressStart.setY( height / 4 );
		}
		// set position of the loading label
		// TODO: Figure out a way to keep it in the center of the screen without
		// resizing
		int loadingLabelX = width / 2 - loadingLabel.getWidth( ) / 2;
		int loadingLabelY = height / 2 + loadingLabel.getHeight( );
		loadingLabel.setX( loadingLabelX );
		loadingLabel.setY( loadingLabelY );

		// set position of loading complete label
		int loadingCompleteLabelX = width / 2 - loadingCompleteLabel.getWidth( )
				/ 2;
		int loadingCompleteLabelY = height / 2
				- loadingCompleteLabel.getHeight( ) / 3;
		loadingCompleteLabel.setX( loadingCompleteLabelX );
		loadingCompleteLabel.setY( loadingCompleteLabelY );
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

		loadingBar = new Entity( "loadingScrew", null, null, null, false );

		//unloads textures and texture atlases
		Array<String> assets = WereScrewedGame.manager.getAssetNames( );
		for( int i=0; i<assets.size; i++ ) {
			if ( WereScrewedGame.manager.getAssetType( assets.get( i ) ) == Texture.class ) {
				WereScrewedGame.manager.unload( assets.get( i ) );
			} 
			//else if ( WereScrewedGame.manager.getAssetType( assets.get( i ) ) == Sound.class ) {
				//WereScrewedGame.manager.unload(  assets.get( i ) );
			//}
			
		}
		Object[] atlases = WereScrewedGame.manager.getAtlases( );
		for( int i=0; i<WereScrewedGame.manager.getAtlases( ).length; i++ ) {
			WereScrewedGame.manager.unloadAtlas( (String)atlases[i] );
		}
		
		readLoadFiles( "commonLevel" );
		if ( !screenTag.equals( "commonLevel" ) ) {
			readLoadFiles( screenTag );
		}

	}

	private void readLoadFiles( String filename ) {
		// reads through the text file that is named
		// the same thing as the screenTag
		// and reads each line which is a path and loads that file
		FileHandle handle = Gdx.files.internal( "data/" + filename + ".txt" );
		String split[] = handle.readString( ).split( "\\r?\\n" );
		EntityDef.clearDefs( );
		for ( String s : split ) {
			s.replaceAll( "\\s", "" );
			if ( s.length( ) > 0 ) {
				if ( s.charAt( 0 ) == '#' ) {
					// A comment
					continue;
				} else if ( s.charAt( 0 ) == '@' ) {
					// special case for level parameters
					loadLevelParameter( s );
				} else {
					// A regular file
					loadFromPath( s );
				}
			}
		}
	}
	private void loadLevelParameter( String s ) {
		/*
		 * format for level options:
		 * 
		 * @ fg /path/to/fg/tex
		 * 
		 * @ bg /path/to/bg/tex
		 * 
		 * @ outline /path/to/outline/tex
		 * 
		 * @ tilecolor r g b 
		 * r, g, b = [0-255]
		 */
		String[ ] options = s.split( "\\s" );
		if(options[1].equals( "tilecolor" )){
			int r=255,g=255,b=255;
			try{
				r = Integer.parseInt( options[2] );
				g = Integer.parseInt( options[3] );
				b = Integer.parseInt( options[4] );
			}catch(ArrayIndexOutOfBoundsException e){
				
			}
			WereScrewedGame.manager.setTileColor(r,g,b);
		}else{
			loadFromPath( options[ 2 ] );
			WereScrewedGame.manager.finishLoading( );
			Texture tex = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
					+ options[ 2 ], Texture.class );
			if ( options[ 1 ].equals( "fg" ) ) {
				WereScrewedGame.manager.setLevelRobotFGTex( tex );
			} else if ( options[ 1 ].equals( "bg" ) ) {
				WereScrewedGame.manager.setLevelRobotBGTex( tex );
			} else if ( options[ 1 ].equals( "outline" ) ) {
				WereScrewedGame.manager.setLevelRobotOutlineTex( tex );
			}
		}
	}

	private void loadFromPath( String s ) {
		String ext;
		String fileAndExtension[] = s.split( "\\." );
		if ( fileAndExtension.length > 1 ) {
			// gets the extension
			ext = fileAndExtension[ 1 ];
			// loads the file
			loadCurrentFile( ext, WereScrewedGame.dirHandle + s );
		} else {
			// Gdx.app.log( "Loading screen: ", s + " doesn't have an extension"
			// );
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
	 *            The path to call when loading the texture from the
	 *            AssetManager. Usually will be the same as fullPathName, but
	 *            may differ for alt textures.
	 */
	private void loadCurrentFile( String fileExtension, String fullPathName,
			String listedPathName ) {
		if ( fileExtension.equals( "png" ) ) {
			if ( !WereScrewedGame.manager
					.isLoaded( fullPathName, Texture.class ) ) {
				WereScrewedGame.manager.load( fullPathName, Texture.class );
				// Gdx.app.log( "Texture file loaded", fullPathName );
			}

		} else if ( fileExtension.equals( "ogg" ) ) {
			if ( !WereScrewedGame.manager.isLoaded( fullPathName, Sound.class ) ) {
				WereScrewedGame.manager.load( fullPathName, Sound.class );
				// Gdx.app.log( "Sound file loaded", fullPathName );
			}

		} else if ( fileExtension.equals( "mp3" ) ) {
			if ( !WereScrewedGame.manager.isLoaded( fullPathName, Music.class ) ) {
				WereScrewedGame.manager.load( fullPathName, Music.class );
				// Gdx.app.log( "Music file loaded", fullPathName );
			}

		} else if ( fileExtension.equals( "pack" ) ) {
			FileHandle fileHandle = Gdx.files.internal( fullPathName );

			if ( !WereScrewedGame.manager.isAtlasLoaded( fileHandle
					.nameWithoutExtension( ) ) ) {
				WereScrewedGame.manager.loadAtlas( fullPathName );
				// Gdx.app.log( "Atlas pack file loaded", fullPathName );
			}
		} else if ( fileExtension.equals( "fnt" ) ) {
			if ( !WereScrewedGame.manager.isLoaded( fullPathName ) ) {
				WereScrewedGame.manager.loadFont( fullPathName );
			}
			// Gdx.app.log( "Bitmap pack file loaded", fullPathName );
		} else if ( fileExtension.equals( "palette" ) ) {
			String[ ] path = fullPathName.split( "\\." );
			String colorName = path[ 0 ].substring( WereScrewedGame.dirHandle
					.name( ).length( ) );
			WereScrewedGame.manager.addToPalette( colorName );
			// Gdx.app.log( "Color Loaded", colorName );
		} else if ( fileExtension.equals( "p" ) ) { // load a particle effect
			String[ ] path = fullPathName.split( "\\." );
			String effectName = path[ 0 ].substring( WereScrewedGame.dirHandle
					.name( ).length( ) );
			WereScrewedGame.manager.loadParticleEffect( effectName );
			// Gdx.app.log( "Color Loaded", colorName );
		}
		// else if ( fileExtension.equals( "skel" )){
		// String[] path = fullPathName.split( "\\." );
		// String skelName = path[0].substring( WereScrewedGame.dirHandle.name(
		// ).length( ) );
		// WereScrewedGame.manager.loadSpineSkeleton( skeletonName, atlas )
		// }
	}

	// Simple overloading for when you only have an extension and pathname.
	private void loadCurrentFile( String fE, String fPN ) {
		loadCurrentFile( fE, fPN, fPN );
	}
}
