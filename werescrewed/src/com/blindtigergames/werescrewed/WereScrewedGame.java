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

public class WereScrewedGame extends Game {

	public static AssetManager manager = new AssetManager( );
	public static FileHandle dirHandle;

	public FPSLogger logger;
	
	private boolean restartFlag = false;
	
	public static ShaderProgram defaultShader;

	@Override
	public void create( ) {
		ScreenManager.getInstance( ).initialize( this );

		// ScreenManager.getInstance().show(Screen.INTRO);

		// ScreenManager.getInstance().show(Screen.LEVELTEST);

		// uncomment next line to bypass intro

		ScreenManager.getInstance( ).show( ScreenType.LOADING );

		logger = new FPSLogger( );
		if (Gdx.graphics.isGL20Available( ))
			defaultShader = SpriteBatch.createDefaultShader( );
		else
			defaultShader = null;

	}

	@Override
	public void dispose( ) {
		super.dispose( );
		ScreenManager.getInstance( ).dispose( );
	}

	@Override
	public void render( ) {
		update( 0 );
		if ( Gdx.input.isKeyPressed( Keys.SHIFT_LEFT ) && Gdx.input.isKeyPressed( Keys.ESCAPE ) ){
			if ( !restartFlag ){
				restartFlag = true;
				restart( );
			}
		}else{
			restartFlag = false;
		}
		super.render( );
		if (Gdx.app.getType() == ApplicationType.Android) {
			logger.log( ); 
		}
//		logger.log( );
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
