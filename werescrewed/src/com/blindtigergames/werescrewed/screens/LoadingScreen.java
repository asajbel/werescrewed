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
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;

public class LoadingScreen extends Screen {

	public ScreenType screenType;
	private BitmapFont font = null;
	@SuppressWarnings( "unused" )
	private int scaleSize = 7;
	private Label loadingLabel = null;
	private Label loadingCompleteLabel = null;
	private Label pressStart = null, pressToConfirm = null;
	private Label player1 = null, player2 = null;
	private SpriteBatch batch = null;
	private String screenTag = null;
	private Entity loadingBar;

	private int timer = 0;
	private int currIndex = 0;
	private ArrayList< Texture > storyBoardArray = new ArrayList< Texture >( );
	private int currLevel = 0;
	private boolean characterSelect = false, playersHaveBeenSelected = false;
	private boolean player1Volunteer = false, player2Volunteer = false;
	private boolean player1Confirm = false, player2Confirm = false;
	private boolean p1SelectFemale = false;
	private int p1LabelPositionX, p2LabelPositionX;
	private OrthographicCamera camera = null;

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
		WereScrewedGame.manager.clear( );
		if ( st != null && !st.isEmpty( ) ) {
			screenTag = st;
			if ( screenTag.equals( "level1" ) ) {
				currLevel = 1;
			}
		} else {
			screenTag = "commonLevel";
		}
		// Gdx.app.log( "Loading assets for", screenTag );

		// check for level1
		if ( currLevel == 1 ) {

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
					"data/common/slides/slide1_intro.png", Texture.class ) );
			storyBoardArray.add( WereScrewedGame.manager.get(
					"data/common/slides/slide2_audience.png", Texture.class ) );
			storyBoardArray.add( WereScrewedGame.manager.get(
					"data/common/slides/slide3_alphabot.png", Texture.class ) );
			storyBoardArray.add( WereScrewedGame.manager.get(
					"data/common/slides/slide4_players.png", Texture.class ) );
			
			if ( WereScrewedGame.p1Controller != null
					&& WereScrewedGame.p2Controller != null ) {
				characterSelect = true;

				pressStart = new Label( "Press Start To Volunteer!",
						WereScrewedGame.manager.getFont( "longdon" ) );
				pressToConfirm = new Label(
						"Left/Right to switch, Start to confirm",
						WereScrewedGame.manager.getFont( "longdon" ) );

				player1 = new Label( "Player1",
						WereScrewedGame.manager.getFont( "longdon" ) );
				player2 = new Label( "Player2",
						WereScrewedGame.manager.getFont( "longdon" ) );

				pressToConfirm.setX( width / 2
						- pressToConfirm.getWidth( ) / 2 );
				pressToConfirm.setY( height / 4 );

				pressStart.setX( width / 2 - pressStart.getWidth( ) / 2 );
				pressStart.setY( height / 4 );

				p1LabelPositionX = ( width / 2 );// - player1.getWidth( )
														// / 2;
				player1.setX( p1LabelPositionX );
				player1.setY( height / 4 - 100 );

				p2LabelPositionX = ( width / 2 );// + player2.getWidth( )
														// / 2 + 100;
				player2.setX( p2LabelPositionX );
				player2.setY( height / 4 - 50 );
			}

			// debug turning character select off until someone else can finish
			// it

			playersHaveBeenSelected = true;
		}

		// stage = new Stage( );
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
			if ( Gdx.app.getInput( ).isTouched( ) ) {
				ScreenManager.getInstance( ).show( ScreenType.LEVEL_1 );
			}
			if ( currLevel == 0 ) {
				// assets have been loaded!
				loadingLabel.setCaption( "Loading Complete!!" );
				loadingCompleteLabel.draw( batch );
			}

			if ( storyBoardArray.size( ) == 0
					|| currIndex == storyBoardArray.size( ) - 1 ) {
				if ( characterSelect ) {
					if ( playersHaveBeenSelected ) {
						WereScrewedGame.player1Female = p1SelectFemale;
						if ( screenTag != null && screenTag.equals( "level1" ) ) {
							ScreenManager.getInstance( ).show(
									ScreenType.LEVEL_1 );
						}
					}
				} else {

					if ( screenTag != null && screenTag.equals( "level1" ) ) {
						ScreenManager.getInstance( ).show( ScreenType.LEVEL_1 );
					} else if ( screenTag != null
							&& screenTag.equals( "level2" ) ) {
						ScreenManager.getInstance( ).show( ScreenType.DRAGON );
					} else {
						ScreenManager.getInstance( )
								.show( ScreenType.MAIN_MENU );
					}

				}
			}

		}

		timer++;

		if ( timer > 100 ) {
			timer = 0;
			if ( !( currIndex == storyBoardArray.size( ) - 1 ) ) {
				currIndex++;
			}

		}

		if ( characterSelect ) {
			if ( ( currIndex == storyBoardArray.size( ) - 1 ) ) {

				if ( WereScrewedGame.p1ControllerListener.jumpPressed( )
						|| WereScrewedGame.p1ControllerListener.pausePressed( ) ) {
					player1Volunteer = true;

				}
				if ( WereScrewedGame.p2ControllerListener.jumpPressed( )
						|| WereScrewedGame.p2ControllerListener.pausePressed( ) ) {
					player2Volunteer = true;

				}
				if ( !( player1Volunteer || player2Volunteer ) )
					pressStart.draw( batch );
				else {
					pressToConfirm.draw( batch );
				}
				if ( player1Volunteer )
					player1.draw( batch );
				if ( player2Volunteer )
					player2.draw( batch );

				if ( player1Volunteer && player2Volunteer ) {

					if ( WereScrewedGame.p1ControllerListener.rightPressed( )
							|| WereScrewedGame.p1ControllerListener
									.leftPressed( )
							|| WereScrewedGame.p2ControllerListener
									.rightPressed( )
							|| WereScrewedGame.p2ControllerListener
									.leftPressed( ) ) {

						// Swap positions;
						int temp = p1LabelPositionX;
						p1LabelPositionX = p2LabelPositionX;
						p2LabelPositionX = temp;

						player1.setX( p1LabelPositionX );

						player2.setX( p2LabelPositionX );

						player1.unselect( );
						player2.unselect( );
						p1SelectFemale = !p1SelectFemale;

					}

					if ( WereScrewedGame.p1ControllerListener.jumpPressed( )
							|| WereScrewedGame.p1ControllerListener
									.pausePressed( ) ) {
						player1Confirm = true;
						player1.select( );

					}
					if ( WereScrewedGame.p2ControllerListener.jumpPressed( )
							|| WereScrewedGame.p2ControllerListener
									.pausePressed( ) ) {
						player2Confirm = true;
						player2.select( );

					}
					if ( player1Confirm && player2Confirm )
						playersHaveBeenSelected = true;

				}
			}

		}
		if ( currLevel == 1 ) {
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
		if ( currLevel == 1 && characterSelect ) {

			pressStart.setX( width / 2 - pressStart.getWidth( ) / 2 );
			pressStart.setY( height / 4 );

			player1.setX( p1LabelPositionX );
			player1.setY( height / 4 - 100 );

			player2.setX( p2LabelPositionX );
			player2.setY( height / 4 - 100 );

			pressToConfirm.setX( width / 2 - pressToConfirm.getWidth( )
					/ 2 );
			pressToConfirm.setY( height / 4 );
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
		// WereScrewedGame.manager.dispose( );
	}

	@Override
	public void show( ) {
		font = new BitmapFont( );
		font.scale( 1.0f );

		loadingLabel = new Label( "Loading... 0%", font );
		loadingCompleteLabel = new Label( "Press 'A'!!", font );
		batch = new SpriteBatch( );

		@SuppressWarnings( "unused" )
		int width = Gdx.graphics.getWidth( );
		@SuppressWarnings( "unused" )
		int height = Gdx.graphics.getHeight( );

		loadingBar = new Entity( "loadingScrew", null, null, null, false );

		WereScrewedGame.dirHandle = Gdx.files.internal( "data/" );
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
		 */
		String[ ] options = s.split( "\\s" );
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
