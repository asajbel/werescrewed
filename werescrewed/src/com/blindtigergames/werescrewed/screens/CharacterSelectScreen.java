package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;

public class CharacterSelectScreen extends Screen {
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Texture logo = null;
	private int lineHeight = 0;
	private Label screenLabel = null;
	private Button backButton = null;
	
	/*
	 * We need to be able to select gender and color.
	 */
	
	public CharacterSelectScreen(){
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		logo =  WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label("Character Select", fancyFont);
		backButton = new Button( "Back", fancyFont, new ScreenSwitchHandler(
				ScreenType.STORY ) );
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
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		// TODO Auto-generated method stub
		batch.begin( );
		batch.draw(logo, 0, 0);
		screenLabel.draw( batch );
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
		screenLabel.setX( centerX - screenLabel.getWidth( )/2);
		screenLabel.setY( centerY + 6 * lineHeight);
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
