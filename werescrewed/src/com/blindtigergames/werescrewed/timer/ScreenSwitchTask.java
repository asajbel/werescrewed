package com.blindtigergames.werescrewed.timer;

import com.badlogic.gdx.utils.Timer.Task;
import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenManager;

public class ScreenSwitchTask extends Task {
	
	private Screen screen = null;
	
	public ScreenSwitchTask(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void run() {
		/* easily implemented screen switching thanks to singleton pattern */
		ScreenManager.getInstance().show(screen);
	}

}
