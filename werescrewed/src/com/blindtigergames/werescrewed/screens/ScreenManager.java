package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntMap;
import com.blindtigergames.werescrewed.WereScrewedGame;

public final class ScreenManager {

	private static ScreenManager instance;

	private Game game;

	private IntMap< com.badlogic.gdx.Screen > screens;

	private static ScreenType prevScreen = null;
	private boolean pauseScreenShown = false;
	private boolean optionsPauseShown = false;
	public static boolean escapeHeld, p1PauseHeld, p2PauseHeld;

	private ScreenManager( ) {
		screens = new IntMap< com.badlogic.gdx.Screen >( );
	}

	public static ScreenManager getInstance( ) {
		if ( null == instance ) {
			instance = new ScreenManager( );
		}
		return instance;
	}

	public static ScreenType getPrevScreen( ) {
		return prevScreen;
	}

	public void initialize( Game game ) {
		this.game = game;
	}

	public void show( ScreenType screen ) {
		if ( Gdx.input.isKeyPressed( Keys.ESCAPE ) ) {
			ScreenManager.escapeHeld = true;
		} else
			ScreenManager.escapeHeld = false;

		if ( WereScrewedGame.p1Controller != null ) {
			if ( WereScrewedGame.p1ControllerListener.pausePressed( ) ) {
				ScreenManager.p1PauseHeld = true;
			} else
				ScreenManager.p1PauseHeld = false;
		}

		if ( WereScrewedGame.p2Controller != null ) {
			if ( WereScrewedGame.p2ControllerListener.pausePressed( ) ) {
				ScreenManager.p2PauseHeld = true;
			} else
				ScreenManager.p2PauseHeld = false;
		}

		if ( null == game )
			return;

		if ( !screens.containsKey( screen.ordinal( ) ) ) {
			screens.put( screen.ordinal( ), screen.getScreenInstance( ) );
		}

		if ( screen != ScreenType.PAUSE && screen != ScreenType.OPTIONS_PAUSE ) {
			if ( prevScreen != null ) {
				if ( screen != prevScreen ) {
					// Gdx.app.log( "disposing", screens
					// .get( prevScreen.ordinal( ) ).getClass( )
					// .getSimpleName( ) );
					screens.get( prevScreen.ordinal( ) ).dispose( );
					dispose( prevScreen );
					//pauseScreenShown = false;
				}
			}
		}

		if ( screen == ScreenType.PAUSE ) {
			pauseScreenShown = true;
			game.setScreen( screens.get( screen.ordinal( ) ) );
		} else if ( screen == ScreenType.OPTIONS_PAUSE ) {
			optionsPauseShown = true;
			game.setScreen( screens.get( screen.ordinal( ) ) );
		} else {
			if ( screen == ScreenType.MAIN_MENU ) {
				if ( pauseScreenShown ) {
					// Gdx.app.log( "disposing", screens
					// .get( prevScreen.ordinal( ) ).getClass( )
					// .getSimpleName( ) );
					//screens.get( prevScreen.ordinal( ) ).dispose( );
					//dispose( prevScreen );
					screens.get( ScreenType.PAUSE.ordinal( ) ).dispose( );
					dispose( ScreenType.PAUSE );
					pauseScreenShown = false;
				}
			}
			if ( optionsPauseShown ) {
				screens.get( ScreenType.OPTIONS_PAUSE.ordinal( ) ).dispose( );
				dispose( ScreenType.OPTIONS_PAUSE );
				optionsPauseShown = false;
			}
			game.setScreen( screens.get( screen.ordinal( ) ) );
			prevScreen = screen;
		}

		// Gdx.app.log( "ScreenManager", screens.get( screen.ordinal( ) )
		// .getClass( ).getSimpleName( )
		// + " starting" );

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
