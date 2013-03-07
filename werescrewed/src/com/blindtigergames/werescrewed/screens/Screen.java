package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.debug.FPSLoggerS;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.util.Util;

public class Screen implements com.badlogic.gdx.Screen {
	
	protected Level level;
	protected SpriteBatch batch;
	protected SBox2DDebugRenderer debugRenderer;
	
	BitmapFont debug_font;
	Camera uiCamera;
	
	public FPSLoggerS logger;
	
	public Screen( ){

		batch = new SpriteBatch( );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		level = null;
		
		debug_font = WereScrewedGame.manager.getFont( "debug_font" );
		logger = new FPSLoggerS( );
		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth( ), Gdx.graphics.getHeight( ));
		uiCamera.position.set(0,0 , 0); //-Gdx.graphics.getWidth( ), -Gdx.graphics.getHeight( )
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
		if (level != null){
			level.update( delta );
			level.draw( batch, debugRenderer );
			
			int FPS = logger.getFPS( );
			batch.setProjectionMatrix( uiCamera.combined );
			batch.begin( );
			debug_font.draw(batch, "FPS: "+FPS, -Gdx.graphics.getWidth( )/2, Gdx.graphics.getHeight( )/2);//-Gdx.graphics.getWidth( )/4, Gdx.graphics.getHeight( )/4
			batch.end( );
			
		}
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
