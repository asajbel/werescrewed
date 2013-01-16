package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class ShapePlatform extends Platform{
	
	public ShapePlatform(String n, Vector2 pos, Texture tex, World world ){
		super( n, pos, tex , null);
		this.world = world;
	}
}