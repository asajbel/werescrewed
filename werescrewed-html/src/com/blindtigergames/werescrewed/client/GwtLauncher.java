package com.blindtigergames.werescrewed.client;

<<<<<<< HEAD:werescrewed-html/src/com/blindtiger/werescrewed/client/GwtLauncher.java
import com.blindtiger.werescrewed.WereScrewedGame;
=======
import com.blindtigergames.werescrewed.WereScrewed;
>>>>>>> parent of 9c60e0d... Shortened package name to blindtiger:werescrewed-html/src/com/blindtigergames/werescrewed/client/GwtLauncher.java
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(480, 320);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new WereScrewedGame();
	}
}