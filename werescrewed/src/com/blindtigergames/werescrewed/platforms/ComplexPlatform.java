package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityDef;

/**
 * A platform that is in an atypical shape.
 * 
 * @author Ranveer
 * 
 */

public class ComplexPlatform extends Platform {

	// String object would be like "bottle" then we will load that particular
	// body (precompiled)
	public ComplexPlatform( String name, Vector2 pos, Texture tex, int scale,
			World world, String definitionName ) {
		super( name, EntityDef.getDefinition( definitionName ), world, pos,
				0.0f, new Vector2( 1f, 1f ), tex );
	}
}
// private void constructComplexBody( float x, float y, int scale,
// String bodyName ) {
// String filename = "data/bodies/" + bodyName + ".json";
// BodyEditorLoader loader = new BodyEditorLoader(
// Gdx.files.internal( filename ) );
// BodyDef bd = new BodyDef( );
// bd.position.set( x * GameScreen.PIXEL_TO_BOX, y * GameScreen.PIXEL_TO_BOX );
// bd.type = BodyType.DynamicBody;

// public void update( float deltaTime ) {
// super.update( deltaTime );
// }
// }
