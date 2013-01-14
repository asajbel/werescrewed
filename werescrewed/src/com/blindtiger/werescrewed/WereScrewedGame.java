package com.blindtiger.werescrewed;




import com.badlogic.gdx.Game;
import com.blindtiger.werescrewed.screens.Screen;
import com.blindtiger.werescrewed.screens.ScreenManager;

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
