package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

class CreditsScreen implements com.badlogic.gdx.Screen {

	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label authorLabel = null;
	private Label licenseLabel = null;
	private Label versionLabel = null;
	private Button backButton = null;
	private int lineHeight = 0;

	public CreditsScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		authorLabel = new Label( "Author: biobob (Robert Komorovsky)", font );
		licenseLabel = new Label( "License: Apache License 2.0", font );
		versionLabel = new Label( "LibGDX version: " + Version.VERSION, font );
		backButton = new Button( "Back", font, new ScreenSwitchHandler(
				Screen.MAIN_MENU ) );
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.5f, 0.5f, 0.5f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		authorLabel.draw( batch );
		licenseLabel.draw( batch );
		versionLabel.draw( batch );
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
		authorLabel.setX( centerX - authorLabel.getWidth( ) / 2 );
		authorLabel.setY( centerY + lineHeight );
		licenseLabel.setX( centerX - licenseLabel.getWidth( ) / 2 );
		licenseLabel.setY( centerY );
		versionLabel.setX( centerX - versionLabel.getWidth( ) / 2 );
		versionLabel.setY( centerY - lineHeight );
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
