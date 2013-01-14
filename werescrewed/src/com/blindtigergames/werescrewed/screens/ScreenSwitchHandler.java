package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.gui.Button.ButtonHandler;
import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenManager;


public class ScreenSwitchHandler implements ButtonHandler {
	
	private Screen screen = null;
	
	public ScreenSwitchHandler(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void onClick() {
		/* easily implemented screen switching thanks to singleton pattern */
		ScreenManager.getInstance().show(screen);
	}

}
