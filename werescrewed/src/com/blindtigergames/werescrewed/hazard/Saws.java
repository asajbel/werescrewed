package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Saws extends Hazard {

	public Saws( String name, Vector2 pos, Texture texture, World world,
			boolean isActive ) {
		super( name, pos, texture, world, isActive );
		
		
	}

}