package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.input.MyControllerListener;

public class LevelSelectScreen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Sprite logo = null;
	private Sprite menuBG = null;
	private Label screenLabel = null;
	private TextButton playButton = null;
	private TextButton gleedButton = null;
	private TextButton testButton;
	private TextButton resurrectButton = null;
	private TextButton hazardButton = null;
	private int lineHeight = 0;
	private TextButton level1Button;
	private TextButton backButton = null;
	private TextButton dragonButton = null;

	private int buttonIndex = 0;
	private ArrayList< Button > Buttons;
	private Controller controller1;
	private Controller controller2;
	private MyControllerListener controllerListener;
	private int controllerTimer = 10;
	private int controllerMax = 10;


	public LevelSelectScreen( ) {
		
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
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		// TODO Auto-generated method stub
		batch.begin( );
		menuBG.draw( batch );
		logo.draw( batch );
		// batch.draw(logo, -128, 0);
		screenLabel.draw( batch );
		playButton.draw( batch, camera );
		gleedButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		hazardButton.draw( batch, camera );
		testButton.draw( batch, camera );
		level1Button.draw( batch, camera );
		dragonButton.draw(batch,camera);

		backButton.draw( batch, camera );
		batch.end( );

		if ( controllerTimer > 0 ) {
			controllerTimer--;
		} else {
			if(controller1 != null || controller2 != null 
					|| (controller1 == null && controller2 == null)){
				if ( controllerListener.jumpPressed( )
						|| Gdx.input.isKeyPressed( Keys.ENTER ) ) {
					Buttons.get( buttonIndex ).setSelected( true );
					controllerTimer = controllerMax;
				} else if ( controllerListener.downPressed( )
						|| Gdx.input.isKeyPressed( Keys.DOWN ) ) {
					Buttons.get( buttonIndex ).setColored( false );
					buttonIndex++;
					buttonIndex = buttonIndex % Buttons.size( );
					Buttons.get( buttonIndex ).setColored( true );
					controllerTimer = controllerMax;
				} else if ( controllerListener.upPressed( )
						|| Gdx.input.isKeyPressed( Keys.UP ) ) {
					Buttons.get( buttonIndex ).setColored( false );
					if ( buttonIndex == 0 ) {
						buttonIndex = Buttons.size( ) - 1;
					} else {
						buttonIndex--;
					}
					Buttons.get( buttonIndex ).setColored( true );
					controllerTimer = controllerMax;
				}
			}
		}
	}

	@Override
	public void resize( int width, int height ) {
		// TODO Auto-generated method stub
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
		testButton.setX( leftX - testButton.getWidth( ) / 2 );
		testButton.setY( centerY + 3 * lineHeight );
		
		//quick fix
		playButton.setX( leftX - playButton.getWidth( )  + 350);
		playButton.setY( centerY + 2 * lineHeight );
		gleedButton.setX( leftX - gleedButton.getWidth( ) / 2 );
		gleedButton.setY( centerY + lineHeight );
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
		batch = new SpriteBatch( );
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
		ControllerSetUp( );
		loadButtons( );

	}
	
	/**
	 * sets up controller functionality
	 */
	private void ControllerSetUp( ) {
		controllerListener = new MyControllerListener( );
		if ( Controllers.getControllers( ).size > 0 ) {
			controller1 = Controllers.getControllers( ).get( 0 );
			controller1.addListener( controllerListener );
		}
		if ( Controllers.getControllers( ).size > 1 ) {
			controller2 = Controllers.getControllers( ).get( 1 );
			controller2.addListener( controllerListener );
		}
	}
	
	/**
	 * loads buttons appropriately
	 */
	private void loadButtons( ) {
		playButton = new TextButton( "Physics Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new TextButton( "Parallax Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		hazardButton = new TextButton( "Hazard Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.HAZARD ) );
		testButton = new TextButton( "Playtest Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PLAYTEST ) );
		gleedButton = new TextButton( "Gleed Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.GLEED ) );
		level1Button = new TextButton( "AlphaBot", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		dragonButton = new TextButton( "Dragon", fancyFont, new ScreenSwitchHandler( ScreenType.LOADING_2 ) );

		backButton = new TextButton( "Back", fancyFont, new ScreenSwitchHandler(
				ScreenType.MAIN_MENU ) );
		playButton.setColored( true );
		Buttons = new ArrayList< Button >( );
		Buttons.add( testButton );
		Buttons.add( playButton );
		Buttons.add( gleedButton );
		Buttons.add( resurrectButton );
		Buttons.add( hazardButton );
		Buttons.add( level1Button );
		Buttons.add( dragonButton );
		Buttons.add( backButton );

	}

}
