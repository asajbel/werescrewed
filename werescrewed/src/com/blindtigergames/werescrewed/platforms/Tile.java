package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Tile {
	public float xOffset, yOffset;
	public Image tileSprite;

	public Tile( ) {
		xOffset = 0;
		yOffset = 0;
		tileSprite = null;
	}

	public Tile( float offset_x, float offset_y, Image the_sprite ) {
		xOffset = offset_x;
		yOffset = offset_y;
		tileSprite = the_sprite;
	}
}