package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;

public class CharacterSelectScreen implements com.badlogic.gdx.Screen {
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private int lineHeight = 0;
	private Label placeHolder = null;
	private Button backButton = null;
	
	/*
	 * We need to be able to select gender and color.
	 */
	
	public CharacterSelectScreen(){
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		placeHolder = new Label("Under Contruction", font);
		backButton = new Button( "Back", font, new ScreenSwitchHandler(
				ScreenType.MAIN_MENU ) );
	}
	
	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub
		font.dispose( );
		batch.dispose( );
	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render( float delta ) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor( 0.1f, 0.1f, 0.1f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		// TODO Auto-generated method stub
		batch.begin( );
		placeHolder.draw( batch );
		backButton.draw( batch, camera );
		batch.end( );
		
	}

	@Override
	public void resize( int width, int height ) {
		// TODO Auto-generated method stub
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		placeHolder.setX( centerX -placeHolder.getWidth( )/2);
		placeHolder.setY( centerY + 3 * lineHeight);
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 20 + backButton.getHeight( ) );
		
	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub
		
	}

}
