package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.util.Util;

/**
 * THIS CLASS IS DEPRECATED
 * DON'T USE IT, INSSTEAD BUILD A COMPLEX PLATFORM WITH PLATFORMBUILDER
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

public class ComplexPlatform extends Platform {

	// String object would be like "bottle" then we will load that particular
	// body (precompiled)
<<<<<<< HEAD
	public ComplexPlatform( String name, Vector2 pos, Texture tex, int scale,
			World world, String definitionName, float anchRadius ) {
		super( name, EntityDef.getDefinition( definitionName ), world, pos, 0.0f,
				new Vector2( 1f, 1f ), anchRadius );
		// super(n, pos, tex, null);
		// this.world = world;
		// this.width = width;
		// this.height = height;
		// constructComplexBody( pos.x, pos.y, scale, bodyName );
		// this.scale = scale;
=======
	public ComplexPlatform( String n, Vector2 pos, Texture tex, float scale,
			World world, EntityDef def) {
		super( n, def, world, pos, 0.0f, new Vector2( 1f, 1f ) );
		platType = PlatformType.COMPLEX;
>>>>>>> origin/master
	}
	public ComplexPlatform( String n, Vector2 pos, Texture tex, float scale,
			World world, String definitionName ) {
		this( n, pos, tex, scale, world, EntityDef.getDefinition( definitionName ));
	}
}