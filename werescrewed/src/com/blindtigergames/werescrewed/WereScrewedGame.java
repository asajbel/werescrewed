package com.blindtigergames.werescrewed;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.blindtigergames.werescrewed.screens.ScreenManager;
import com.blindtigergames.werescrewed.screens.ScreenType;

public class WereScrewedGame extends Game {

	public static AssetManager manager = new AssetManager( );
	public static FileHandle dirHandle;

	public FPSLogger logger;

	@Override
	public void create( ) {
		ScreenManager.getInstance( ).initialize( this );

		// ScreenManager.getInstance().show(Screen.INTRO);

		// ScreenManager.getInstance().show(Screen.LEVELTEST);

		// uncomment next line to bypass intro

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
		update( 0 );
		super.render( );
		logger.log( );
	}

	public void update( float dT ) {
	}

}
