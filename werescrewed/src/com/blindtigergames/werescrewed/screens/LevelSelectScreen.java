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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.Button.ButtonHandler;
import com.blindtigergames.werescrewed.input.MyControllerListener;

public class LevelSelectScreen implements com.badlogic.gdx.Screen {

	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Texture logo = null;
	private Label screenLabel = null;
	private Button playButton = null;
	private Button gleedButton = null;
	private Button testButton;
	private Button resurrectButton = null;
	private Button hazardButton = null;
	private int lineHeight = 0;
	private Button level1Button;
	private Button backButton = null;

	private int buttonIndex = 0;
	private ArrayList< Button > Buttons;
	private Controller controller1;
	private Controller controller2;
	private MyControllerListener controllerListener;
	private int controllerTimer;
	private int controllerMax = 15;

	/*
	 * Things needed... Being able to select levels
	 */
	public LevelSelectScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		// fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		logo = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label( "Level Select", fancyFont );
		ControllerSetUp( );
		loadButtons( );
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
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		// TODO Auto-generated method stub
		batch.begin( );
		batch.draw( logo, 0, 0 );
		// batch.draw(logo, -128, 0);
		screenLabel.draw( batch );
		playButton.draw( batch, camera );
		gleedButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		hazardButton.draw( batch, camera );
		testButton.draw( batch, camera );
		level1Button.draw( batch, camera );

		backButton.draw( batch, camera );
		batch.end( );

		if ( controllerTimer > 0 ) {
			controllerTimer--;
		} else {
			if(controller1 != null || controller2 != null){
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
		int centerX = width / 2;
		int centerY = height / 2;
		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 6 * lineHeight );
		testButton.setX( centerX - testButton.getWidth( ) / 2 );
		testButton.setY( centerY + 3 * lineHeight );
		playButton.setX( centerX - playButton.getWidth( ) / 2 );
		playButton.setY( centerY + 2 * lineHeight );
		gleedButton.setX( centerX - gleedButton.getWidth( ) / 2 );
		gleedButton.setY( centerY + lineHeight );
		resurrectButton.setX( centerX - resurrectButton.getWidth( ) / 2 );
		resurrectButton.setY( centerY );
		hazardButton.setX( centerX - hazardButton.getWidth( ) / 2 );
		hazardButton.setY( centerY - lineHeight );
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
	
	/**
	 * sets up controller functionality
	 */
	private void ControllerSetUp( ) {
		if ( Controllers.getControllers( ).size > 0 ) {
			controllerListener = new MyControllerListener( );
			controller1 = Controllers.getControllers( ).get( 0 );
			controller1.addListener( controllerListener );
		}
		if ( Controllers.getControllers( ).size > 1 ) {
			controllerListener = new MyControllerListener( );
			controller2 = Controllers.getControllers( ).get( 1 );
			controller2.addListener( controllerListener );
		}
	}
	
	/**
	 * loads buttons appropriately
	 */
	private void loadButtons( ) {
		playButton = new Button( "Physics Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new Button( "Parallax Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		hazardButton = new Button( "Hazard Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.HAZARD ) );
		testButton = new Button( "Playtest Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PLAYTEST ) );
		gleedButton = new Button( "Gleed Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.GLEED ) );
		level1Button = new Button( "Level 1", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );

		backButton = new Button( "Back", fancyFont, new ScreenSwitchHandler(
				ScreenType.MAIN_MENU ) );
		playButton.setColored( true );
		Buttons = new ArrayList< Button >( );
		Buttons.add( testButton );
		Buttons.add( playButton );
		Buttons.add( gleedButton );
		Buttons.add( resurrectButton );
		Buttons.add( hazardButton );
		Buttons.add( level1Button );
		Buttons.add( backButton );

	}

}
