package com.blindtigergames.werescrewed.entity.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityDef;

/**
 * THIS CLASS IS DEPRECATED DON'T USE IT, INSSTEAD BUILD A COMPLEX PLATFORM WITH
 * PLATFORMBUILDER
 * 
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * @deprecated
 * 
 */

public class ComplexPlatform extends Platform {

	// String object would be like "bottle" then we will load that particular
	// body (precompiled)
	public ComplexPlatform( String n, Vector2 pos, Texture tex, float scale,
			World world, EntityDef def ) {
		super( n, def, world, pos, 0.0f, new Vector2( 1f, 1f ) );
		platType = PlatformType.COMPLEX;
	}

	public ComplexPlatform( String n, Vector2 pos, Texture tex, float scale,
			World world, String definitionName, float anchRadius ) {
		this( n, pos, tex, scale, world, EntityDef
				.getDefinition( definitionName ) );
	}
}