package com.blindtiger.werescrewed.screens;

import com.blindtiger.werescrewed.gui.Button.ButtonHandler;
import com.blindtiger.werescrewed.screens.Screen;
import com.blindtiger.werescrewed.screens.ScreenManager;


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
