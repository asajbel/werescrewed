package com.blindtigergames.werescrewed;


// Hope this doesn't fuck up

import com.badlogic.gdx.Game;
import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenManager;

public class WereScrewedGame extends Game {
	
	@Override
	public void create() {
		ScreenManager.getInstance().initialize(this);

       // ScreenManager.getInstance().show(Screen.INTRO);
        
        //uncomment next line to bypass intro
		ScreenManager.getInstance().show(Screen.GAME);
	}

	@Override
	public void dispose() {
		super.dispose();
        ScreenManager.getInstance().dispose();
	}
	
}
