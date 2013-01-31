package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.input.InputHandlerPlayer1;
import com.blindtigergames.werescrewed.level.Level;

public class Screen implements com.badlogic.gdx.Screen {

	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
	
	protected Level level;
	protected InputHandlerPlayer1 inputHandler;
	protected SpriteBatch batch;
	protected SBox2DDebugRenderer debugRenderer;
	MyContactListener MCL;

	
	public Screen( ){
		inputHandler = new InputHandlerPlayer1( );
		batch = new SpriteBatch( );
		debugRenderer = new SBox2DDebugRenderer( BOX_TO_PIXEL );
		MCL = new MyContactListener( );
		level = null;
	}
	
	@Override
	public void render( float delta ) {
		if(Gdx.gl20 != null){
			Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}
		level.update( delta );
		level.draw( batch, debugRenderer );
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

}
