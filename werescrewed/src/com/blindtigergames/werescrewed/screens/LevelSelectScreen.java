package com.blindtigergames.werescrewed.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;

public class LevelSelectScreen implements com.badlogic.gdx.Screen {
	
	
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label screenLabel = null;
	private Button playButton = null;
	private Button gleedButton = null;
	private Button testButton;
	private Button resurrectButton = null;
	private Button hazardButton = null;
	private int lineHeight = 0;
	private Button level1Button;
	private Button backButton = null;
	
	/*
	 * Things needed...
	 * Being able to select levels
	 * 
	 */
	public LevelSelectScreen(){
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label( "Level Select", font);
		playButton = new Button( "Physics Test Screen", font,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new Button( "Resurrect Test Screen", font,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		hazardButton = new Button ( "Hazard Test Screen", font,
				new ScreenSwitchHandler( ScreenType.HAZARD ) );
		testButton = new Button( "Playtest Screen", font,
				new ScreenSwitchHandler( ScreenType.PLAYTEST ) );
		gleedButton = new Button( "Gleed Screen", font,
				new ScreenSwitchHandler( ScreenType.GLEED ) );
		level1Button = new Button( "Level 1", font, 
				new ScreenSwitchHandler(ScreenType.LOADING_1 ) );
		
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
		screenLabel.draw( batch );
		playButton.draw( batch, camera );
		gleedButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		hazardButton.draw(  batch, camera );
		testButton.draw( batch, camera );
		level1Button.draw( batch, camera );
		
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
		screenLabel.setY( centerY + 7 * lineHeight);
		testButton.setX( centerX - testButton.getWidth( ) / 2 );
		testButton.setY( centerY + 3 * lineHeight );
		playButton.setX( centerX - playButton.getWidth( ) / 2 );
		playButton.setY( centerY + 2 * lineHeight );
		gleedButton.setX( centerX - gleedButton.getWidth( ) / 2 );
		gleedButton.setY( centerY + lineHeight);
		resurrectButton.setX( centerX - resurrectButton.getWidth( ) /2 );
		resurrectButton.setY( centerY);
		hazardButton.setX( centerX - hazardButton.getWidth( ) /2 );
		hazardButton.setY( centerY - lineHeight);
		level1Button.setX( centerX - level1Button.getWidth( ) / 2 );
		level1Button.setY( centerY - lineHeight * 2 );
		
		
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