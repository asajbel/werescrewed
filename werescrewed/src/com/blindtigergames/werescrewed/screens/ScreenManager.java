package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;

public final class ScreenManager {

	private static ScreenManager instance;

	private Game game;

	private IntMap< com.badlogic.gdx.Screen > screens;

	private ScreenManager( ) {
		screens = new IntMap< com.badlogic.gdx.Screen >( );
	}

	public static ScreenManager getInstance( ) {
		if ( null == instance ) {
			instance = new ScreenManager( );
		}
		return instance;
	}

	public void initialize( Game game ) {
		this.game = game;
	}

	public void show( ScreenType screen ) {
		if ( null == game )
			return;
		if ( !screens.containsKey( screen.ordinal( ) ) ) {
			screens.put( screen.ordinal( ), screen.getScreenInstance( ) );
		}
		game.setScreen( screens.get( screen.ordinal( ) ) );
		Gdx.app.log( "ScreenManager", screens.get( screen.ordinal( ) ).getClass( ).getSimpleName( )+" starting");
	}

	public void dispose( ScreenType screen ) {
		if ( !screens.containsKey( screen.ordinal( ) ) )
			return;
		screens.remove( screen.ordinal( ) ).dispose( );
	}

	public void dispose( ) {
		for ( com.badlogic.gdx.Screen screen : screens.values( ) ) {
			screen.dispose( );
		}
		screens.clear( );
		instance = null;
	}
}
