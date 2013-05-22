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

class CreditsScreen extends Screen {
	// implements com.badlogic.gdx.Screen
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	//private Texture logo = null;
	private int people = 12;
	private Label screenLabel = null;
	private Label[] authorsLabel = new Label[ people ];
	private Label licenseLabel = null;
	private Label versionLabel = null;
	private TextButton backButton = null;
	private int lineHeight = 0;

	public CreditsScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		// fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		//logo = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		//		+ "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label( "WE'RE SCREWED!! CREDITS", fancyFont );
		licenseLabel = new Label( "Blind Tiger Games", font );
		versionLabel = new Label( "Ver. " + Version.VERSION, font );
		backButton = new TextButton( "Back", fancyFont,
				new ScreenSwitchHandler( ScreenType.MAIN_MENU ) );
		backButton.setColored( true );
		 initPeople( );
	}
	
	private void initPeople ( ) {
		authorsLabel[ 0 ] = new Label( "Name", font );
		authorsLabel[ 1 ] = new Label( "Name", font );
		authorsLabel[ 2 ] = new Label( "Name", font );
		authorsLabel[ 3 ] = new Label( "Name", font );
		authorsLabel[ 4 ] = new Label( "Name", font );
		authorsLabel[ 5 ] = new Label( "Name", font );
		authorsLabel[ 6 ] = new Label( "Name", font );
		authorsLabel[ 7 ] = new Label( "Name", font );
		authorsLabel[ 8 ] = new Label( "Name", font );
		authorsLabel[ 9 ] = new Label( "Name", font );
		authorsLabel[ 10 ] = new Label( "Name", font );
		authorsLabel[ 11 ] = new Label( "Name", font );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		//batch.draw( logo, 0, 0 );
		screenLabel.draw( batch );
		licenseLabel.draw( batch );
		for ( int i = 0; i < people; i++ ) {
			authorsLabel[ i ].draw( batch );
		}
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
		int k = 6;
		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 10 * lineHeight );
		licenseLabel.setX( centerX - licenseLabel.getWidth( ) / 2 );
		licenseLabel.setY( centerY + ( k + 1 ) * lineHeight);
		for ( int j = 0; j < people; j++ ) {
			authorsLabel[ j ].setX( centerX - authorsLabel[ j ].getWidth( ) / 2 );
			authorsLabel[ j ].setY( centerY + k * lineHeight );
			k--;
		}
		versionLabel.setX( centerX - versionLabel.getWidth( ) / 2 );
		versionLabel.setY( centerY + k * lineHeight );
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 100 + backButton.getHeight( ) );
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
}
