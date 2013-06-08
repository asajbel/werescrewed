package com.blindtigergames.werescrewed.gui;

import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class CheckBox extends OptionControl {

	Sprite on, off;
	
	public CheckBox( int min, int max, int current ) {
		super( min, max, current );
		on = WereScrewedGame.manager.getAtlas( "common-textures" ).createSprite( "switch_on" );
		off = WereScrewedGame.manager.getAtlas( "common-textures" ).createSprite( "switch_off" ); ;
		on.setRotation( -90 ); 
		on.setOrigin( on.getWidth( ), 0 );
		on.setScale( 0.7f ); 
		off.setRotation( -90 ); 
		off.setScale( 0.7f );
		off.setOrigin( off.getWidth( ), 0 );
	}

	public void draw( SpriteBatch batch ) {
		if (curValue == maxValue) {
			on.setPosition( x, y );
			on.draw( batch ); 
		} else {
			off.setPosition( x, y );
			off.draw( batch ); 
		}
	}
}
