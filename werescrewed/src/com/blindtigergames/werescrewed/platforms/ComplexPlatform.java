package com.blindtigergames.werescrewed.platforms;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.util.Util;

/**
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

public class ComplexPlatform extends Platform {

	// String object would be like "bottle" then we will load that particular
	// body (precompiled)
	public ComplexPlatform( String n, Vector2 pos, Texture tex, int scale,
			World world, String definitionName ) {
		super( n, EntityDef.getDefinition( definitionName ), world, pos, 0.0f,
				new Vector2( 1f, 1f ) );
		// super(n, pos, tex, null);
		// this.world = world;
		// this.width = width;
		// this.height = height;
		// constructComplexBody( pos.x, pos.y, scale, bodyName );
		// this.scale = scale;
	}

	@SuppressWarnings( "unused" )
	private void constructComplexBody( float x, float y, int scale,
			String bodyName ) {
		String filename = "data/bodies/" + bodyName + ".json";
		BodyEditorLoader loader = new BodyEditorLoader(
				Gdx.files.internal( filename ) );
		BodyDef bd = new BodyDef( );
		bd.position.set( x * Util.PIXEL_TO_BOX, y * Util.PIXEL_TO_BOX );
		bd.type = BodyType.DynamicBody;

		// public void update( float deltaTime ) {
		// super.update( deltaTime );
		// }
	}
}