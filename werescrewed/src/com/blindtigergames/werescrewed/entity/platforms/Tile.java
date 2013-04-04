package com.blindtigergames.werescrewed.entity.platforms;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Tile {
	public float xOffset, yOffset;
	public Sprite tileSprite;

	public Tile( ) {
		xOffset = 0;
		yOffset = 0;
		tileSprite = null;
	}

	public Tile( float offset_x, float offset_y, Sprite the_sprite ) {
		xOffset = offset_x;
		yOffset = offset_y;
		tileSprite = the_sprite;
	}
}