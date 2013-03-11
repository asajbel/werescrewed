package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Button.ButtonHandler;
import com.blindtigergames.werescrewed.gui.Label;

class MainMenuScreen implements com.badlogic.gdx.Screen {
	// extends Screen

	private SpriteBatch batch = null;
	private Texture logo = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	BitmapFont fancyFont;
	private Label headingLabel = null;
	private Button exitButton = null;
	private int lineHeight = 0;

	private Button storyButton = null;
	private Button levelSelectButton = null;
	private Button optionsButton = null;

	public MainMenuScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		
		//font = WereScrewedGame.manager.getFont( "ornatique" );
		logo =  WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		headingLabel = new Label( "We're Screwed!!", fancyFont );
		
		storyButton = new Button("Start", fancyFont,
				new ScreenSwitchHandler(ScreenType.STORY));
		levelSelectButton = new Button( "Level Select", fancyFont,
				new ScreenSwitchHandler(ScreenType.LEVEL_SELECT));
		optionsButton = new Button("Options", fancyFont,
				new ScreenSwitchHandler( ScreenType.OPTIONS));
		exitButton = new Button( "Exit", fancyFont, new ButtonHandler( ) {
			@Override
			public void onClick( ) {
				Gdx.app.exit( );
			}
		} );
	}

	@Override
	public void render( float delta ) {
		//super.render(delta);
		Gdx.gl.glClearColor( 0.5f, 0.5f, 0.5f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		batch.draw(logo, 0, 0);
		//headingLabel.draw( batch );
		storyButton.draw( batch, camera );
		levelSelectButton.draw( batch, camera );
		optionsButton.draw( batch, camera );
		// imoverButton.draw( batch, camera );
		exitButton.draw( batch, camera );
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 1 );
		}
		if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PLAYTEST );
		}
		
		if ( Gdx.input.isKeyPressed( Keys.EQUALS ) ) {
			ScreenManager.getInstance( ).show( ScreenType.GLEED );
		}
		
	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		headingLabel.setX( centerX - headingLabel.getWidth( ) / 2 );
		headingLabel.setY( centerY + 7 * lineHeight );
		storyButton.setX( centerX  - storyButton.getWidth( )/2);
		storyButton.setY( centerY + 5 * lineHeight);
		levelSelectButton.setX( centerX - levelSelectButton.getWidth( )/2 );
		levelSelectButton.setY( centerY + 4 * lineHeight);
		optionsButton.setX( centerX - optionsButton.getWidth( )/2);
		optionsButton.setY( centerY + 3 * lineHeight );
		// imoverButton.setX( centerX - imoverButton.getWidth( )/2 );
		// imoverButton.setY( centerY - lineHeight );
		exitButton.setX( centerX - exitButton.getWidth( ) / 2 );
		exitButton.setY( centerY + 2 * lineHeight );
	}

	@Override
	public void show( ) {

	}

	@Override
	public void hide( ) {

	}

	@Override
	public void pause( ) {

	}

	@Override
	public void resume( ) {

	}

	@Override
	public void dispose( ) {
		font.dispose( );
		batch.dispose( );
	}

}
