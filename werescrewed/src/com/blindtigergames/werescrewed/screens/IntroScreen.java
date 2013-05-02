package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.blindtigergames.werescrewed.WereScrewedGame;

class IntroScreen implements com.badlogic.gdx.Screen {
	
	public ScreenType screenType;
	private SpriteBatch batch = null;
	private BitmapFont font = null;
	static TextureRegion player = WereScrewedGame.manager.getAtlas( "common-textures" ).findRegion( "flat_head_circular" );
	private Texture intro, audience, alphabot, players;
	Stage stage;

	public IntroScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		WereScrewedGame.manager.load( "data/common/slides/slide1_intro.png", Texture.class );
		WereScrewedGame.manager.load( "data/common/slides/slide2_audience.png", Texture.class );
		WereScrewedGame.manager.load( "data/common/slides/slide3_alphabot.png", Texture.class );
		WereScrewedGame.manager.load( "data/common/slides/slide4_players.png", Texture.class );
		
		intro = WereScrewedGame.manager.get( "data/common/slides/slide1_intro.png", Texture.class );
		audience =  WereScrewedGame.manager.get( "data/common/slides/slide2_audience.png", Texture.class );
		alphabot=  WereScrewedGame.manager.get( "data/common/slides/slide3_alphabot.png", Texture.class );
		players= WereScrewedGame.manager.get( "data/common/slides/slide4_players.png", Texture.class );
		
		stage = new Stage( );
		Gdx.input.setInputProcessor( stage );

		Image introImage = new Image( intro );
		Image audienceImage = new Image( audience );
		Image alphabotImage = new Image( alphabot );
		Image playersImage = new Image( players );
		// Image splashImage = new Image(Assets.logoTexture);
		// splashImage.addAction(Actions.fadeIn( 2f ));
		introImage
				.addAction( Actions.sequence( Actions.delay( 0f ),
						Actions.fadeIn( 2f ),
						Actions.fadeOut( 2f ),
						Actions.hide( )));
		
		audienceImage
		.addAction( Actions.sequence( Actions.delay( 4f ),
				Actions.fadeIn( 2f ),
				Actions.fadeOut( 2f ),
				Actions.hide( )));
		
		alphabotImage
		.addAction( Actions.sequence( Actions.delay( 8f ),
				Actions.fadeIn( 2f ),
				Actions.fadeOut( 2f ),Actions.hide( ) ));
		
		playersImage
		.addAction( Actions.sequence( Actions.delay( 16f ),
				Actions.fadeIn( 2f ),
				Actions.fadeOut( 2f ),
				Actions.run( onSplashFinishedRunnable )
				));

		
		stage.addActor( playersImage );
		
		stage.addActor( alphabotImage );
		
		stage.addActor( audienceImage );
		stage.addActor( introImage );
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 1f, 1f, 1f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

		stage.act( delta );
		stage.draw( );
	}

	@Override
	public void resize( int width, int height ) {
		stage.setViewport( width, height, true );

	}

	@Override
	public void show( ) {
		/* schedule to show main menu screen after 2 seconds */
		// Timer.schedule(new ScreenSwitchTask(Screen.MAIN_MENU), 2f);
		
	}

	Runnable onSplashFinishedRunnable = new Runnable( ) {

		@Override
		public void run( ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_1 );

		}
	};

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

}