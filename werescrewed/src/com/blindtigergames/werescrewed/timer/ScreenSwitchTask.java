package com.blindtigergames.werescrewed.timer;

import com.badlogic.gdx.utils.Timer.Task;
import com.blindtigergames.werescrewed.screens.ScreenType;
import com.blindtigergames.werescrewed.screens.ScreenManager;

public class ScreenSwitchTask extends Task {
	
	private ScreenType screen = null;
	
	public ScreenSwitchTask(ScreenType screen) {
		this.screen = screen;
	}

	@Override
	public void run() {
		/* easily implemented screen switching thanks to singleton pattern */
		ScreenManager.getInstance().show(screen);
	}

}
