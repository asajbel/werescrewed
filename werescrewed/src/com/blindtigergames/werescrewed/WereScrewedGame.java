package com.blindtigergames.werescrewed;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenManager;

public class WereScrewedGame extends Game {

	@Override
	public void create() {
		ScreenManager.getInstance().initialize(this);

       // ScreenManager.getInstance().show(Screen.INTRO);
        
        //uncomment next line to bypass intro
<<<<<<< HEAD
		ScreenManager.getInstance().show(Screen.IMOVER);
=======
		ScreenManager.getInstance().show(Screen.STRESSTEST);
>>>>>>> stress-test2
	}

	@Override
	public void dispose() {
		super.dispose();
        ScreenManager.getInstance().dispose();
	}
<<<<<<< HEAD
	
	@Override
	public void render () {
		update(0);
		super.render();
	}
	
	public void update(float dT) {
	}
=======
>>>>>>> stress-test2
}
