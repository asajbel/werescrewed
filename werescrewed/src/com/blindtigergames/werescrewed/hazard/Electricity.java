package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Electricity extends Hazard {

	public Electricity( String name, Vector2 pos, World world,
			boolean isActive ) {
		super( name, pos, null, world, 0, 0, isActive );
		
		
	}
}
