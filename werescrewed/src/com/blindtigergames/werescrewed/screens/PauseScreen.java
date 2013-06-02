package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;
import com.blindtigergames.werescrewed.sound.SoundManager;

class PauseScreen extends Screen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private Texture logo = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Label screenLabel = null;
	private TextButton mainMenuButton = null;
	private int lineHeight = 0;

	public PauseScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );

		fancyFont = WereScrewedGame.manager.getFont( "longdon" );

		logo = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label( "Pause Screen", fancyFont );
		mainMenuButton = new TextButton( "Main Menu", fancyFont,
				new ScreenSwitchHandler( ScreenType.MAIN_MENU ) );
		mainMenuButton.setColored( true );
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
		batch.draw( logo, 0, 0 );
		screenLabel.draw( batch );
		mainMenuButton.draw( batch, camera );
		batch.end( );

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
	public void resize( int width, int height ) {
		super.resize( width, height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 6 * lineHeight );
		mainMenuButton.setX( centerX - mainMenuButton.getWidth( ) / 2 );
		mainMenuButton.setY( 100 + mainMenuButton.getHeight( ) );
	}

	@Override
	public void show( ) {
		SoundManager.setEnableLoops( false );
	}

	@Override
	public void hide( ) {
		SoundManager.setEnableLoops( true );
	}

	@Override
	public void pause( ) {
	}

	@Override
	public void resume( ) {
	}

	@Override
	public void dispose( ) {
	}

}
