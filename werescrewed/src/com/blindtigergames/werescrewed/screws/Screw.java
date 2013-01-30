package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * Base class for the various types of screws. Defines basic behavior.
 * 
 * @author Dennis
 * 
 */

public class Screw extends Entity {

	protected int rotation;
	protected int depth;
	protected int maxDepth;
	protected int screwStep;
	protected final short CATEGORY_SCREWS = 0x0008;

	public Screw( String name, Vector2 pos, Texture tex, Body body ) {
		super( name, pos, ( tex == null ? new Texture(
				Gdx.files.internal( "data/screw.png" ) ) : tex ), body, false );
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
	}

	public void remove( ) {
		world.destroyBody( body );
	}

	public void screwLeft( ) {
	}

	public void screwRight( ) {
	}

	public int getRotation( ) {
		return rotation;
	}

	public int getDepth( ) {
		return depth;
	}

	public void exampleCollide( String str ) {
		System.out.println( str );
	}

}
