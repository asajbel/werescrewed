package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.blindtigergames.werescrewed.WereScrewedGame;

class IntroScreen implements com.badlogic.gdx.Screen {

	private SpriteBatch batch = null;
	private BitmapFont font = null;
	static Texture player = WereScrewedGame.manager.get(
			"assets/common/data/TilesetTest.png", Texture.class);

	Stage stage;

	public IntroScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 1f, 1f, 1f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		/*
		 * batch.begin(); font.setColor(0f, 0f, 0f, 1f); font.draw(batch, LIB,
		 * captionX1, captionY); font.setColor(1f, 0f, 0f, 1f); font.draw(batch,
		 * GDX, captionX2, captionY); batch.end();
		 */
		stage.act( delta );
		stage.draw( );
	}

	@Override
	public void resize( int width, int height ) {
		// batch.getProjectionMatrix().setToOrtho2D(-width/2, -height/2, width,
		// height);
		stage.setViewport( width, height, true );

	}

	@Override
	public void show( ) {
		/* schedule to show main menu screen after 2 seconds */
		// Timer.schedule(new ScreenSwitchTask(Screen.MAIN_MENU), 2f);
		stage = new Stage( );
		Gdx.input.setInputProcessor( stage );

		Image splashImage = new Image( player );
		// Image splashImage = new Image(Assets.logoTexture);
		// splashImage.addAction(Actions.fadeIn( 2f ));
		splashImage
				.addAction( Actions.sequence( Actions.fadeOut( 0.001f ),
						Actions.fadeIn( 2f ),
						Actions.run( onSplashFinishedRunnable ) ) );

		stage.addActor( splashImage );
	}

	Runnable onSplashFinishedRunnable = new Runnable( ) {

		@Override
		public void run( ) {
			// TODO Auto-generated method stub
			// game.setScreen(new MainMenuScreen(game));
			ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );

		}
	};

	@Override
	public void hide( ) {
		/* dispose intro screen because it won't be needed anymore */
		ScreenManager.getInstance( ).dispose( ScreenType.INTRO );
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