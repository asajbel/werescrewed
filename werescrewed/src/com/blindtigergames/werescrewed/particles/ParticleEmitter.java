package com.blindtigergames.werescrewed.particles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;

public class ParticleEmitter extends Entity{
	
	ParticleEmitter( String name, Vector2 positionPixels, Texture texture, Body body,
			boolean solid ){
		super( name, positionPixels, texture, null, true );
	}
	
	

	
}