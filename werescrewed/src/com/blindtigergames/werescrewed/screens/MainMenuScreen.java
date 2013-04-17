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
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Button.ButtonHandler;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.input.MyControllerListener;

class MainMenuScreen implements com.badlogic.gdx.Screen {
	// extends Screen

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private Texture logo = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	BitmapFont fancyFont;
	private Label headingLabel = null;
	private Button exitButton = null;
	private int lineHeight = 0;
	private int buttonIndex = 0;
	private ArrayList<Button> Buttons;

	private Button storyButton = null;
	private Button levelSelectButton = null;
	private Button optionsButton = null;
	private Controller controller1;
	private Controller controller2;
	private MyControllerListener controllerListener;
	private int controllerTimer;
	private int controllerMax = 10;


	public MainMenuScreen( ) {
		
	}

	@Override
	public void render( float delta ) {
		//super.render(delta);		
		Gdx.gl.glClearColor( 0.5f, 0.5f, 0.5f, 1f );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		batch.draw(logo, 0, 0);
		//batch.draw(logo, -128, 0);
		//headingLabel.draw( batch );
		storyButton.draw( batch, camera );
		levelSelectButton.draw( batch, camera );
		optionsButton.draw( batch, camera );
		// imoverButton.draw( batch, camera );
		exitButton.draw( batch, camera );
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 1 );
		}
		
		if ( Gdx.input.isKeyPressed( Keys.EQUALS ) ) {
			ScreenManager.getInstance( ).show( ScreenType.GLEED );
		}
		
		if ( Gdx.input.isKeyPressed( Keys.Z ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PHYSICS);
		}
		
		if ( Gdx.input.isKeyPressed( Keys.D ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_2);
		}
		
		if ( Gdx.input.isKeyPressed( Keys.A ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_1);
		}
		
		
		if( controllerTimer > 0){
			controllerTimer -- ;
		}else{
			if(controller1 != null || controller2 != null){
				if ( controllerListener.jumpPressed( ) || Gdx.input.isKeyPressed( Keys.ENTER )){
					Buttons.get( buttonIndex ).setSelected( true );
					controllerTimer = controllerMax;
				}
				else if( controllerListener.downPressed( ) || Gdx.input.isKeyPressed( Keys.DOWN )){
					Buttons.get( buttonIndex ).setColored( false );
					buttonIndex++;
					buttonIndex = buttonIndex % Buttons.size( );
					Buttons.get( buttonIndex ).setColored( true );
					controllerTimer = controllerMax;
				}
				else if( controllerListener.upPressed( ) || Gdx.input.isKeyPressed( Keys.UP )){
					Buttons.get( buttonIndex ).setColored( false );
					if( buttonIndex == 0 ) {
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
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		
		headingLabel.setX( centerX - headingLabel.getWidth( ) / 2 );
		headingLabel.setY( centerY + 7 * lineHeight );
		storyButton.setX( centerX  - storyButton.getWidth( )/2);
		storyButton.setY( centerY + 5 * lineHeight);
		levelSelectButton.setX( centerX - levelSelectButton.getWidth( )/2 );
		levelSelectButton.setY( centerY + 4 * lineHeight);
		optionsButton.setX( centerX - optionsButton.getWidth( )/2);
		optionsButton.setY( centerY + 3 * lineHeight );
		// imoverButton.setX( centerX - imoverButton.getWidth( )/2 );
		// imoverButton.setY( centerY - lineHeight );
		exitButton.setX( centerX - exitButton.getWidth( ) / 2 );
		exitButton.setY( centerY + 2 * lineHeight );
	}
	
	/**
	 * sets up controller functionality
	 */
	private void ControllerSetUp( ){
		controllerListener = new MyControllerListener( );
		if( Controllers.getControllers( ).size > 0){
			controller1 = Controllers.getControllers( ).get( 0 );
			controller1.addListener( controllerListener );
		}
		if( Controllers.getControllers( ).size > 1){
			controller2 = Controllers.getControllers( ).get( 1 );
			controller2.addListener( controllerListener );
		}
	}
	
	/**
	 * laods all button related content appropriately
	 */
	private void loadButtons( ){
		//font = WereScrewedGame.manager.getFont( "ornatique" );
		logo =  WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		headingLabel = new Label( "We're Screwed!!", fancyFont );
		
		storyButton = new Button("Start", fancyFont,
				new ScreenSwitchHandler(ScreenType.INTRO));
		levelSelectButton = new Button( "Level Select", fancyFont,
				new ScreenSwitchHandler(ScreenType.LEVEL_SELECT));
		optionsButton = new Button("Options", fancyFont,
				new ScreenSwitchHandler( ScreenType.OPTIONS));
		exitButton = new Button( "Exit", fancyFont, new ButtonHandler( ) {
			@Override
			public void onClick( ) {
				Gdx.app.exit( );
			}
		} );
		storyButton.setColored( true );
		Buttons = new ArrayList<Button>();
		Buttons.add( storyButton );
		Buttons.add( levelSelectButton );
		Buttons.add( optionsButton );
		Buttons.add( exitButton );
	}

	@Override
	public void show( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		//fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		
		ControllerSetUp( );
		loadButtons( );
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
	
	/*levelSelectButton = new Button( "Level Select", fancyFont,
			new ScreenSwitchHandler(ScreenType.LEVEL_SELECT));
	optionsButton = new Button("Options", fancyFont,
			new ScreenSwitchHandler( ScreenType.OPTIONS));
	exitButton = new Button( "Exit", fancyFont, new ButtonHandler( ) {
		@Override
		public void onClick( ) {
			Gdx.app.exit( );
		}
	} );
	*/

}
