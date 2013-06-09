package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.CheckBox;
import com.blindtigergames.werescrewed.gui.TextButton.ButtonHandler;

public class FullHandler implements ButtonHandler {
	private CheckBox c;
	
	public FullHandler(CheckBox check) {
		c = check; 
		
	}

	@Override
	public void onClick( ) {
		OptionsScreen.restart = true; 
		float value = c.getCurrentValue( );
		if (value >= 1) {
			c.setCurrentValue( 0 );
			WereScrewedGame.getPrefs( ).setFullSceen( false );
		} else {
			c.setCurrentValue( 1 ); 
			WereScrewedGame.getPrefs( ).setFullSceen( true );
		}
	}

}
