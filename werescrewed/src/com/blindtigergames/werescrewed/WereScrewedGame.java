package com.blindtigergames.werescrewed;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.blindtigergames.werescrewed.asset.AssetManager;
import com.blindtigergames.werescrewed.screens.ScreenManager;
import com.blindtigergames.werescrewed.screens.ScreenType;
import com.blindtigergames.werescrewed.util.Metrics;

public class WereScrewedGame extends Game {

	public static final int targetFrameRate = 60;
	public static final float oneOverTargetFrameRate = 1f/targetFrameRate;
	
	public static AssetManager manager = new AssetManager( );
	public static FileHandle dirHandle;

	public FPSLogger logger;
	
	private boolean restartFlag = false;
	private boolean metricsFlag = false;
	
	public static ShaderProgram defaultShader;
	
	@SuppressWarnings( "unused" )
	private float fpsTime = 0;

	@Override
	public void create( ) {
		ScreenManager.getInstance( ).initialize( this );
		
		if (Gdx.graphics.isGL20Available( ))
			defaultShader = SpriteBatch.createDefaultShader( );
		else
			defaultShader = null;

		ScreenManager.getInstance( ).show( ScreenType.LOADING );

		logger = new FPSLogger( );


	}

	@Override
	public void dispose( ) {
		super.dispose( );
		ScreenManager.getInstance( ).dispose( );
	}

	@Override
	public void render( ) {
		//update( 0 );
		if ( Gdx.input.isKeyPressed( Keys.SHIFT_LEFT ) && Gdx.input.isKeyPressed( Keys.ESCAPE ) ){
			if ( !restartFlag ){
				restartFlag = true;
				restart( );
			}
		}else{
			restartFlag = false;
		}
		
		if(Gdx.input.isKeyPressed( Keys.SEMICOLON )){
			if( !metricsFlag ){
				metricsFlag = true;
				Metrics.activated = !Metrics.activated;
				Gdx.app.log( "Metrics activated", "" + Metrics.activated );
			}
		}else{
			metricsFlag = false;
		}
		
		
		
		//super.render( );
		if (Gdx.app.getType() == ApplicationType.Android) {
			logger.log( ); 
		}
//		logger.log( );
		
		super.render( );
		
//		float deltaTime = Gdx.graphics.getDeltaTime( );
//		fpsTime += deltaTime;
//		if ( fpsTime >= oneOverTargetFrameRate ){
//			//Gdx.app.log( "FPS", ""+fpsTime+", "+ oneOverTargetFrameRate);
//			fpsTime = fpsTime - oneOverTargetFrameRate;
//			//Gdx.app.log( "Rendering", "Rendering");
//			super.render( );
//		}else{
//			return;
//			//fpsTime += deltaTime;
//			//Gdx.app.log( "Not rendering", "");
//		}
	}
	
	public void restart(){
		this.dispose();
		manager = new AssetManager( );
		ScreenManager.getInstance( ).initialize( this );
		ScreenManager.getInstance( ).show( ScreenType.LOADING );
	}

	public void update( float dT ) {
	}

}
