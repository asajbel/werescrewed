package com.blindtigergames.werescrewed.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class WereScrewedPreferences {
	private static final String PREF_NAME = "WereScrewedPreferences";
	private static final String FULL = "fullscreen";

	public WereScrewedPreferences( ) {
	}

	protected Preferences getPrefs( ) {
		return Gdx.app.getPreferences( PREF_NAME );
	}

	public boolean isFullScreen( ) {
		return getPrefs( ).getBoolean( FULL, false );
	}

	public void setFullScreen( boolean fullscreen ) {
		getPrefs( ).putBoolean( FULL, fullscreen );
		getPrefs( ).flush( );
	}
	
	public float getSoundValue( String key ) {
		return getPrefs( ).getFloat( key, 1.0f );
	}

	public void setSoundValue( String key, float val ) {
		getPrefs( ).putFloat( key, val );
		getPrefs( ).flush( );
	}
}
