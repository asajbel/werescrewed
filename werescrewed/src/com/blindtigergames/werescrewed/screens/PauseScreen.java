package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;
import com.blindtigergames.werescrewed.sound.SoundManager;

class PauseScreen extends MenuScreen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
//	private Texture logo = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Label screenLabel = null;
	private TextButton mainMenuButton = null;
	private TextButton optionsButton = null;
	private TextButton returnButton = null;
	private int lineHeight = 0;

	public PauseScreen( ) {
		super( );
		batch = new SpriteBatch( );
		font = new BitmapFont( );
	}

	@Override
	public void load( ){
		super.load( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
//		logo = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
//		+ "/common/title_background.png", Texture.class );
		
		fancyFont.setScale( 1.0f );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label( "Pause Screen", fancyFont );

		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		
		WereScrewedGame.manager.loadAtlas( "data/menu/menu-textures.pack" );
		
		loadButtons( );
		setClearColor( 40, 40, 40, 255 );
	}
	
	public void disposeAll( ) {
		// ScreenManager.getInstance( ).dispose(ScreenType.PHYSICS );
		// ScreenManager.getInstance( ).dispose(ScreenType.PLAYTEST );
		// ScreenManager.getInstance( ).dispose(ScreenType.HAZARD );
		// ScreenManager.getInstance( ).dispose(ScreenType.OPTIONS );
		// ScreenManager.getInstance( ).dispose(ScreenType.LEVEL_SELECT );
		// ScreenManager.getInstance( ).dispose(ScreenType.RESURRECT );
		// ScreenManager.getInstance( ).dispose(ScreenType.GLEED );
		// ScreenManager.getInstance( ).dispose(ScreenType.LEVEL_SELECT );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

		batch.begin( );
//		batch.draw( logo, 0, 0 );
		fancyFont.setScale( 1.0f );
		screenLabel.draw( batch );
		returnButton.draw( batch, camera );
		optionsButton.draw( batch, camera );
		mainMenuButton.draw( batch, camera );

		if ( !transInEnd ) {
			transInEnd = true;
		}
		
		if ( !transOutEnd ) {
			transOutEnd = true;
			Buttons.get( buttonIndex ).setSelected( true );
			buttonIndex = 0;
		}
		
		batch.end( );

		//Unpause
		if ( WereScrewedGame.p1Controller != null ) {
			if ( WereScrewedGame.p1ControllerListener.pausePressed( ) ) {
				if ( !ScreenManager.p1PauseHeld ) {
					ScreenManager.getInstance( ).show(
							ScreenManager.getPrevScreen( ) );
				}
			} else {
				ScreenManager.p1PauseHeld = false;
			}
		}
		if ( WereScrewedGame.p2Controller != null ) {
			if ( WereScrewedGame.p2ControllerListener.pausePressed( ) ) {
				if ( !ScreenManager.p2PauseHeld ) {
					ScreenManager.getInstance( ).show(
							ScreenManager.getPrevScreen( ) );
				}
			} else {
				ScreenManager.p2PauseHeld = false;
			}

		}
		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			if ( !ScreenManager.escapeHeld ) {
				ScreenManager.getInstance( ).show(
						ScreenManager.getPrevScreen( ) );
			}
		} else
			ScreenManager.escapeHeld = false;
	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, WereScrewedGame.getWidth(), WereScrewedGame.getHeight() );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		
		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 6 * lineHeight );
		returnButton.setX( centerX - returnButton.getWidth( ) / 2 );
		returnButton.setY( 240 + returnButton.getHeight( ) );
		optionsButton.setX( centerX - optionsButton.getWidth( ) / 2 );
		optionsButton.setY( 170 + optionsButton.getHeight( ) );
		mainMenuButton.setX( centerX - mainMenuButton.getWidth( ) / 2 );
		mainMenuButton.setY( 100 + mainMenuButton.getHeight( ) );
	}
	
	private void loadButtons( ) {
		buttonTex = WereScrewedGame.manager.getAtlas( "menu-textures" ).findRegion( "button" );
		
		returnButton = new TextButton( "Return to Game", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenManager.getPrevScreen( ) ) );
		optionsButton = new TextButton( "Options", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.OPTIONS_PAUSE ) );
		mainMenuButton = new TextButton( "Main Menu", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.LOADING_MENU ) );
		Buttons = new ArrayList< Button >( );
		Buttons.add( returnButton );
		Buttons.add( optionsButton );
		Buttons.add( mainMenuButton );
		returnButton.setColored( true );
	}
	
	@Override
	public void show( ) {
		if (!assetsLoaded){
			load();
		}
		SoundManager.setEnableLoops( false );
	}

	@Override
	public void hide( ) {
		SoundManager.setEnableLoops( true );
	}
}
