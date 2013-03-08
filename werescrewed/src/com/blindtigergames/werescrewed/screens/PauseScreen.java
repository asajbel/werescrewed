package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

class PauseScreen implements com.badlogic.gdx.Screen {

	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label screenLabel = null;
	private Label authorLabel = null;
	private Label licenseLabel = null;
	private Label versionLabel = null;
	private Button mainMenuButton = null;
	private Button backButton = null;
	private int lineHeight = 0;

	public PauseScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label("Pause Screen", font);
		mainMenuButton = new Button("Main Menu",font, 
				new ScreenSwitchHandler(ScreenType.MAIN_MENU));
		backButton = new Button( "Physics Screen", font, 
				new ScreenSwitchHandler(ScreenType.PHYSICS ) );
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.5f, 0.5f, 0.5f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		//taking this out temporarily  
		//if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
		//	ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		//}
		batch.begin( );
		screenLabel.draw( batch );
		mainMenuButton.draw( batch, camera );
		backButton.draw( batch, camera );
		batch.end( );

	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		screenLabel.setX( centerX - screenLabel.getWidth()/2);
		screenLabel.setY( centerY + 7 * lineHeight );
		mainMenuButton.setX( centerX - mainMenuButton.getWidth()/2);
		mainMenuButton.setY( 40 + mainMenuButton.getHeight( ) );
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 20 + backButton.getHeight( ) );
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
