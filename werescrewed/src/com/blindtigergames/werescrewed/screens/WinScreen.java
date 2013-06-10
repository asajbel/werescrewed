package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;

class WinScreen extends MenuScreen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label firstLine = null;
	private Label secLine = null;
	private TextButton nextButton = null;
	private int lineHeight = 0;

	public WinScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		firstLine = new Label( "Mission Complete!", font );
		secLine = new Label( "Winners Never Lose!!", font );

		TextureRegion buttonTex = WereScrewedGame.manager.
				getAtlas( "menu-textures" ).findRegion( "button" );
		nextButton = new TextButton( "Continue", font, buttonTex, 
				new ScreenSwitchHandler( ScreenType.TROPHY ) );
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.1f, 0.1f, 0.1f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

		ScreenManager.getInstance( ).dispose( ScreenType.TROPHY );

		if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) ) {
			ScreenManager.getInstance( ).show( ScreenType.TROPHY );
			// Later this will probably call the next level screen instead.
		}
		batch.begin( );
		firstLine.draw( batch );
		secLine.draw( batch );
		nextButton.draw( batch, camera );
		batch.end( );

	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		firstLine.setX( centerX - firstLine.getWidth( ) / 2 );
		firstLine.setY( centerY + lineHeight );
		secLine.setX( centerX - secLine.getWidth( ) / 2 );
		secLine.setY( centerY );
		nextButton.setX( centerX - nextButton.getWidth( ) / 2 );
		nextButton.setY( 20 + nextButton.getHeight( ) );
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
	}

}
