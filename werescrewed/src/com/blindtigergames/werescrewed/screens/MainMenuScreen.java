package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.TextButton.ButtonHandler;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;

class MainMenuScreen extends Screen {
	

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private Sprite menuBG = null; 
	private Sprite transition = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont;
	private Label headingLabel = null;
	private TextButton exitButton = null;
	private int lineHeight = 0;

	private TextButton storyButton = null;
	private TextButton levelSelectButton = null;
	private TextButton optionsButton = null;
	
	TweenManager manager = new TweenManager( );


	public MainMenuScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		
		loadButtons( );
	}

	@Override
	public void render( float delta ) {
		super.render(delta);		
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		manager.update( delta );
		batch.begin( );
		menuBG.draw( batch );

		storyButton.draw( batch, camera );
		levelSelectButton.draw( batch, camera );
		optionsButton.draw( batch, camera );

		exitButton.draw( batch, camera );
		
		if ( !finish )
			setAlpha( -0.02f );
		transition.draw( batch, alpha );
		
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 1 );
		}
		
		if ( Gdx.input.isKeyPressed( Keys.Z ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		}
		
		if ( Gdx.input.isKeyPressed( Keys.D ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_2 );
		}
		
		if ( Gdx.input.isKeyPressed( Keys.A ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_1 );
		}
		if ( Gdx.input.isKeyPressed( Keys.H ) ) {
			ScreenManager.getInstance( ).show( ScreenType.HAZARD );
		}
		
		
		
	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int leftX = ( int ) menuBG.getWidth( ) / 2;
		int centerY = height / 5;
		float scaleX = width / 1280f;
		float scaleY = height / 720f;
		
		transition.setPosition( 0, 0 );
		//menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth( ) ); 
		menuBG.setPosition( 0, height / 2 - menuBG.getHeight( ) / 2 ); 
		//menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 - menuBG.getHeight( ) / 2 ); 
		headingLabel.setX( leftX - headingLabel.getWidth( ) / 2 );
		headingLabel.setY( centerY + 7 * lineHeight );
		storyButton.setX( leftX  - storyButton.getWidth( ) / 2 );
		storyButton.setY( centerY + 5 * lineHeight);
		levelSelectButton.setX( leftX - levelSelectButton.getWidth( ) / 2 );
		levelSelectButton.setY( centerY + 4 * lineHeight);
		optionsButton.setX( leftX - optionsButton.getWidth( ) / 2 );
		optionsButton.setY( centerY + 3 * lineHeight );
		// imoverButton.setX( centerX - imoverButton.getWidth( )/2 );
		// imoverButton.setY( centerY - lineHeight );
		exitButton.setX( leftX - exitButton.getWidth( ) / 2 );
		exitButton.setY( centerY + 2 * lineHeight );
		
		//Tween.to( storyButton, ButtonTweenAccessor.POSITION_X, 1 ).ease( TweenEquations.easeInBounce ).target( leftX  - storyButton.getWidth( )/2 ).start( manager );
	}
	

	
	/**
	 * loads all button related content appropriately
	 */
	private void loadButtons( ){
		//font = WereScrewedGame.manager.getFont( "ornatique" );
		Texture back = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/menu/menu.png", Texture.class );
		Texture trans = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/menu/transition.png", Texture.class ); 
		menuBG = new Sprite( back );
		transition = new Sprite( trans );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 50 );
		headingLabel = new Label( "We're Screwed!!", fancyFont );
		
		storyButton = new TextButton("Start", fancyFont,
				new ScreenSwitchHandler(ScreenType.LOADING_1));
		levelSelectButton = new TextButton( "Level Select", fancyFont,
				new ScreenSwitchHandler(ScreenType.LEVEL_SELECT));
		optionsButton = new TextButton("Options", fancyFont,
				new ScreenSwitchHandler( ScreenType.OPTIONS));
		exitButton = new TextButton( "Exit", fancyFont, new ButtonHandler( ) {
			@Override
			public void onClick( ) {
				Gdx.app.exit( );
			}
		} );
		storyButton.setColored( true );
		Buttons.add( storyButton );
		Buttons.add( levelSelectButton );
		Buttons.add( optionsButton );
		Buttons.add( exitButton );
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
