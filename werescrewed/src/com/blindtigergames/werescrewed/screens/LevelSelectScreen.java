package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;

public class LevelSelectScreen extends Screen {

	public ScreenType screenType;
//	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Sprite menuBG = null;
	private Sprite transition = null;
	private Label screenLabel = null;
	private TextButton resurrectButton = null;
	private TextButton hazardButton = null;
	private TextButton physicsButton = null;
	private int lineHeight = 0;
	private TextButton level1Button;
	private TextButton backButton = null;
	private TextButton dragonButton = null;



	public LevelSelectScreen( ) {
		super();
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		//fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		// fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		Texture back = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/menu.png", Texture.class ); 
		Texture trans = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/menu/transition.png", Texture.class ); 
		menuBG = new Sprite( back );
		transition = new Sprite( trans );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 40 );
		screenLabel = new Label( "Level Select", fancyFont );
		loadButtons( );
	}

	@Override
	public void dispose( ) {
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
		super.render( delta );
		
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		
		batch.begin( );
		menuBG.draw( batch );
		//screenLabel.draw( batch );
		physicsButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		hazardButton.draw( batch, camera );
		level1Button.draw( batch, camera );
		dragonButton.draw(batch,camera);

		backButton.draw( batch, camera );
		
		if ( !finish )
			setAlpha( -0.02f );
		transition.draw( batch, alpha );
		
		batch.end( );

		
		
		
	}

	@Override
	public void resize( int width, int height ) {
		
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int leftX = ( int ) menuBG.getWidth( ) / 2;// / 5 - 20;
		int centerY = height / 5;
		float scaleX = width / 1280f;
		float scaleY = height / 720f;
		
		transition.setPosition( 0, 0 );
		//menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth( ) ); 
		menuBG.setPosition( 0, height / 2 - menuBG.getHeight( ) / 2 ); 
		//menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 - menuBG.getHeight( ) / 2 ); 
		screenLabel.setX( leftX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 3 * lineHeight);
		
		//quick fix
		physicsButton.setX( leftX - physicsButton.getWidth( ) / 2 );
		physicsButton.setY( centerY +  lineHeight * 6 );
		resurrectButton.setX( leftX - resurrectButton.getWidth( ) / 2 );
		resurrectButton.setY( centerY + lineHeight * 5 );
		hazardButton.setX( leftX - hazardButton.getWidth( ) / 2 );
		hazardButton.setY( centerY + lineHeight * 4 );
		level1Button.setX( leftX - level1Button.getWidth( ) / 2 );
		level1Button.setY( centerY + lineHeight * 3 );
		dragonButton.setX( leftX - dragonButton.getWidth() / 2 );
		dragonButton.setY( centerY + lineHeight * 2 );

		backButton.setX( leftX - backButton.getWidth( ) / 2 );
		backButton.setY( 100 + backButton.getHeight( ) );

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {
		

	}
	

	
	/**
	 * loads buttons appropriately
	 */
	private void loadButtons( ) {
		physicsButton = new TextButton( "Physics Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new TextButton( "Parallax Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		hazardButton = new TextButton( "Hazard Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.HAZARD ) );
		level1Button = new TextButton( "AlphaBot", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		dragonButton = new TextButton( "Dragon", fancyFont, new ScreenSwitchHandler( ScreenType.LOADING_2 ) );

		backButton = new TextButton( "Back", fancyFont, new ScreenSwitchHandler(
				ScreenType.MAIN_MENU ) );
		physicsButton.setColored( true );
		

		Buttons.add( physicsButton );
		Buttons.add( resurrectButton );
		Buttons.add( hazardButton );
		Buttons.add( level1Button );
		Buttons.add( dragonButton );
		Buttons.add( backButton );

	}

}
