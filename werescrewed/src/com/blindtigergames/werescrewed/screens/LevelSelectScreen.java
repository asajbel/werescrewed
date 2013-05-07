package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;

public class LevelSelectScreen extends Screen {

	public ScreenType screenType;
//	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Sprite logo = null;
	private Sprite menuBG = null;
	private Label screenLabel = null;
	private Button physicsButton = null;
	private Button resurrectButton = null;
	private Button hazardButton = null;
	private int lineHeight = 0;
	private Button level1Button;
	private Button backButton = null;
	private Button dragonButton = null;



	public LevelSelectScreen( ) {
		super();
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		//fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		// fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		Texture name = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/title_background_clear.png", Texture.class );
		Texture back = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/menu_placeholder.png", Texture.class ); 
		logo = new Sprite(name);
		menuBG = new Sprite(back);
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 20 );
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
		logo.draw( batch );
		// batch.draw(logo, -128, 0);
		screenLabel.draw( batch );
		physicsButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		hazardButton.draw( batch, camera );
		level1Button.draw( batch, camera );
		dragonButton.draw(batch,camera);

		backButton.draw( batch, camera );
		batch.end( );

		
		
		
	}

	@Override
	public void resize( int width, int height ) {
		
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int leftX = width / 5 - 20;
		int centerY = height / 2 - 10;
		float scaleX = width / 1280f;
		float scaleY = height / 720f;
		
		logo.setScale( scaleX, scaleY ); 
		menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth( ) ); 
		logo.setPosition( leftX - logo.getWidth( ) / 2, centerY + 4 * lineHeight + 20); 
		menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 - menuBG.getHeight( ) / 2 ); 
		screenLabel.setX( leftX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 4 * lineHeight);
		
		//quick fix
		physicsButton.setX( leftX - physicsButton.getWidth( )  /2 );
		physicsButton.setY( centerY +  lineHeight );
		resurrectButton.setX( leftX - resurrectButton.getWidth( ) / 2 );
		resurrectButton.setY( centerY );
		hazardButton.setX( leftX - hazardButton.getWidth( ) / 2 );
		hazardButton.setY( centerY - lineHeight );
		level1Button.setX( leftX - level1Button.getWidth( ) / 2 );
		level1Button.setY( centerY - lineHeight * 2 );
		dragonButton.setX( leftX-dragonButton.getWidth()/2 );
		dragonButton.setY(centerY-lineHeight*3);

		backButton.setX( leftX - backButton.getWidth( ) / 2 );
		backButton.setY( 20 + backButton.getHeight( ) );

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
		physicsButton = new Button( "Physics Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new Button( "Parallax Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		hazardButton = new Button( "Hazard Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.HAZARD ) );


		level1Button = new Button( "AlphaBot", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		dragonButton = new Button( "Dragon", fancyFont, new ScreenSwitchHandler( ScreenType.LOADING_2 ) );

		backButton = new Button( "Back", fancyFont, new ScreenSwitchHandler(
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
