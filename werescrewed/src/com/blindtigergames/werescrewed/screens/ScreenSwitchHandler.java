package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.gui.TextButton.ButtonHandler;
import com.blindtigergames.werescrewed.screens.ScreenType;
import com.blindtigergames.werescrewed.screens.ScreenManager;

public class ScreenSwitchHandler implements ButtonHandler {

	private ScreenType screen = null;

	public ScreenSwitchHandler( ScreenType screen ) {
		this.screen = screen;
	}

	@Override
	public void onClick( ) {
		ScreenManager.getInstance( ).show( screen );
	}

}
