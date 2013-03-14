package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

class PauseScreen implements com.badlogic.gdx.Screen {

	private SpriteBatch batch = null;
	private Texture logo = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
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

		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );

		logo =  WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label("Pause Screen", fancyFont);
		mainMenuButton = new Button("Main Menu",fancyFont, 
				new ScreenSwitchHandler(ScreenType.MAIN_MENU));
		backButton = new Button( "Physics Screen", fancyFont, 
				new ScreenSwitchHandler(ScreenType.PHYSICS ) );
	}

	public void disposeAll(){
		ScreenManager.getInstance( ).dispose(ScreenType.PHYSICS );
		ScreenManager.getInstance( ).dispose(ScreenType.PLAYTEST );
		ScreenManager.getInstance( ).dispose(ScreenType.HAZARD );
		ScreenManager.getInstance( ).dispose(ScreenType.OPTIONS );
		ScreenManager.getInstance( ).dispose(ScreenType.LEVEL_SELECT );
		ScreenManager.getInstance( ).dispose(ScreenType.CHARACTER_SELECT );
		ScreenManager.getInstance( ).dispose(ScreenType.STORY );
		ScreenManager.getInstance( ).dispose(ScreenType.RESURRECT );
		ScreenManager.getInstance( ).dispose(ScreenType.GLEED );
		ScreenManager.getInstance( ).dispose(ScreenType.LEVEL_SELECT );
	}
	
	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		//taking this out temporarily  
		//if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
		//	ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		//}
		disposeAll();
		batch.begin( );
		batch.draw(logo, 0, 0);
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
		screenLabel.setY( centerY + 6 * lineHeight );
		mainMenuButton.setX( centerX - mainMenuButton.getWidth()/2);
		mainMenuButton.setY( 60 + mainMenuButton.getHeight( ) );
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
