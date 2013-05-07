package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;

class CreditsScreen  extends Screen {
//implements com.badlogic.gdx.Screen
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Texture logo = null;
	private Label screenLabel = null;
	private Label authorLabel = null;
	private Label licenseLabel = null;
	private Label versionLabel = null;
	private TextButton backButton = null;
	private int lineHeight = 0;

	public CreditsScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		//fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		logo =  WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label("CREDITS",fancyFont);
		authorLabel = new Label( "", font );
		licenseLabel = new Label( "", font );
		versionLabel = new Label( "" + Version.VERSION, font );
		backButton = new TextButton( "Back", fancyFont, new ScreenSwitchHandler(
				ScreenType.MAIN_MENU ) );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		batch.draw(logo, 0, 0);
		screenLabel.draw( batch );
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
		screenLabel.setX( centerX - screenLabel.getWidth()/2);
		screenLabel.setY( centerY + 6 * lineHeight );
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
