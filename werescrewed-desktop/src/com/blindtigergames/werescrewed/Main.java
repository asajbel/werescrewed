package com.blindtigergames.werescrewed;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "WereScrewed";
		//cfg.useGL20 = false; //Compatibility option for OpenGL 1.0
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.vSyncEnabled = true;

		
		//cfg.useCPUSynch = false;
		//cfg.vSyncEnabled = true;
		//cfg.fullscreen = true;
		
		new LwjglApplication(new WereScrewedGame(), cfg);
	}
}
