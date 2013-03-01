package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.Button.ButtonHandler;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

class MainMenuScreen implements com.badlogic.gdx.Screen {

	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label headingLabel = null;
	private Button playButton = null;
	private Button gleedButton = null;
	private Button exitButton = null;
	private Button testButton;
	private Button resurrectButton = null;
	private int lineHeight = 0;
	private Button level1Button;
	private Button optionsButton = null;

	public MainMenuScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		headingLabel = new Label( "We're Screwed!!", font );
		playButton = new Button( "Physics Test Screen", font,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new Button( "Resurrect Test Screen", font,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		testButton = new Button( "Playtest Screen", font,
				new ScreenSwitchHandler( ScreenType.PLAYTEST ) );
		gleedButton = new Button( "Gleed Screen", font,
				new ScreenSwitchHandler( ScreenType.GLEED ) );
		level1Button = new Button( "Level 1", font, 
				new ScreenSwitchHandler(ScreenType.LOADING_1 ) );
		optionsButton = new Button("Options", font,
				new ScreenSwitchHandler( ScreenType.CHARACTER_SELECT));
		exitButton = new Button( "Exit", font, new ButtonHandler( ) {
			@Override
			public void onClick( ) {
				Gdx.app.exit( );
			}
		} );
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.5f, 0.5f, 0.5f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		headingLabel.draw( batch );
		playButton.draw( batch, camera );
		gleedButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		testButton.draw( batch, camera );
		level1Button.draw( batch, camera );
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
	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		headingLabel.setX( centerX - headingLabel.getWidth( ) / 2 );
		headingLabel.setY( centerY + 3 * lineHeight );
		testButton.setX( centerX - testButton.getWidth( ) / 2 );
		testButton.setY( centerY + 2 * lineHeight );
		playButton.setX( centerX - playButton.getWidth( ) / 2 );
		playButton.setY( centerY + lineHeight );
		gleedButton.setX( centerX - gleedButton.getWidth( ) / 2 );
		gleedButton.setY( centerY );
		resurrectButton.setX( centerX - testButton.getWidth( ) /2 );
		resurrectButton.setY( centerY - lineHeight );
		level1Button.setX( centerX - level1Button.getWidth( ) / 2 );
		level1Button.setY( centerY - lineHeight * 2 );
		optionsButton.setX( centerX - optionsButton.getWidth( )/2);
		optionsButton.setY( centerY - 3 * lineHeight );
		// imoverButton.setX( centerX - imoverButton.getWidth( )/2 );
		// imoverButton.setY( centerY - lineHeight );
		exitButton.setX( centerX - exitButton.getWidth( ) / 2 );
		exitButton.setY( centerY - 4 * lineHeight );
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
