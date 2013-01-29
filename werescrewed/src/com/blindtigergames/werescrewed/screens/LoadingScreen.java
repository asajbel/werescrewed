package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.LoadingBar;

/*
 * Doesn't work yet
 */

public class LoadingScreen implements com.badlogic.gdx.Screen {

	private Stage stage;

	private Image logo;
	private Image loadingFrame;
	private Image loadingBarHidden;
	private Image screenBg;
	private Image loadingBg;

	private float startX, endX;
	private float percent;

	private Actor loadingBar;

	public LoadingScreen( ) {

		// WereScrewedGame.manager.load("data/loading.pack",
		// TextureAtlas.class);
		// Tell the manager to load assets for the loading screen

		WereScrewedGame.manager.load( "data/loading.pack", TextureAtlas.class );
		// Wait until they are finished loading
		WereScrewedGame.manager.finishLoading( );
		// Initialize the stage where we will place everything
		stage = new Stage( );

		// Get our textureatlas from the manager
		TextureAtlas atlas = WereScrewedGame.manager.get( "data/loading.pack",
				TextureAtlas.class );

		// Grab the regions from the atlas and create some images
		logo = new Image( atlas.findRegion( "libgdx-logo" ) );
		loadingFrame = new Image( atlas.findRegion( "loading-frame" ) );
		loadingBarHidden = new Image( atlas.findRegion( "loading-bar-hidden" ) );
		screenBg = new Image( atlas.findRegion( "screen-bg" ) );
		loadingBg = new Image( atlas.findRegion( "loading-frame-bg" ) );

		// Add the loading bar animation
		Animation anim = new Animation( 0.05f,
				atlas.findRegions( "loading-bar-anim" ) );
		anim.setPlayMode( Animation.LOOP_REVERSED );
		loadingBar = new LoadingBar( anim );

		// Or if you only need a static bar, you can do
		// loadingBar = new Image(atlas.findRegion("loading-bar1"));

		// Add all the actors to the stage
		stage.addActor( screenBg );
		stage.addActor( loadingBar );
		stage.addActor( loadingBg );
		stage.addActor( loadingBarHidden );
		stage.addActor( loadingFrame );
		stage.addActor( logo );

		// Add everything to be loaded, for instance:
		// game.manager.load("data/assets1.pack", TextureAtlas.class);
		// game.manager.load("data/assets2.pack", TextureAtlas.class);
		// game.manager.load("data/assets3.pack", TextureAtlas.class);

	}

	@Override
	public void render( float delta ) {

		// Clear the screen
		Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );

		if ( WereScrewedGame.manager.update( ) ) { // Load some, will return
													// true if done loading
			if ( Gdx.input.isTouched( ) ) { // If the screen is touched after
											// the game is done loading, go to
											// the main menu screen
				ScreenManager.getInstance( ).show( Screen.GAME );
			}
		}

		// Interpolate the percentage to make it more smooth
		percent = Interpolation.linear.apply( percent,
				WereScrewedGame.manager.getProgress( ), 0.1f );

		// Update positions (and size) to match the percentage
		loadingBarHidden.setX( startX + endX * percent );
		loadingBg.setX( loadingBarHidden.getX( ) + 30 );
		loadingBg.setWidth( 450 - 450 * percent );
		loadingBg.invalidate( );

		// Show the loading screen
		stage.act( );
		stage.draw( );

	}

	@Override
	public void resize( int width, int height ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide( ) {
		// WereScrewedGame.manager.unload("data/loading.pack");
	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose( ) {
		WereScrewedGame.manager.unload( "data/loading.pack" );

	}

}