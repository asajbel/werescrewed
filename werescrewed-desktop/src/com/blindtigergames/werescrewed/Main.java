package com.blindtigergames.werescrewed;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	private static boolean debug = false;

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gadzooks, Robots! We're Screwed!!";
		// cfg.useGL20 = false; //Compatibility option for OpenGL 1.0
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.vSyncEnabled = true;

		cfg.vSyncEnabled = true;

		for (String cmd : args) {
			if (cmd.equals("-debug")) debug = true; 
		}
		WereScrewedGame.debug = debug;
		new LwjglApplication(new WereScrewedGame(), cfg);
	}
}
