package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Fire extends Hazard {

	public Fire( String name, Vector2 pos, World world, boolean solid,
			float pixelWidth, float pixelHeight, boolean isActive ) {
		super( name, pos, null, world, isActive );
		entityType = entityType.HAZARD;
		
	}
}
