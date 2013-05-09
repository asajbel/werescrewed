package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class CheckBox extends OptionControl {

	private static Texture onTex =  WereScrewedGame.manager.get(
			WereScrewedGame.dirHandle + "/common/powerswitches/on.png" );
	private static Texture offTex = WereScrewedGame.manager.get( 
			WereScrewedGame.dirHandle + "/common/powerswitches/off.png" );
	
	/************************************
	To whoever reads this: please delete powerswitches on/off for 
	the assets folder when you rplace these textures for finals ones. -stew
	************************************///
	
	public CheckBox( int min, int max, int current ) {
		super( min, max, current );
		
	}
	
	public void draw( SpriteBatch batch ) {
		
	}
}
