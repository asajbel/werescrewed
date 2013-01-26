package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * Holds general methods for screws
 * 
 * @author Dennis
 * 
 */

public class Screw extends Entity {
<<<<<<< HEAD
	public Screw( String n, Vector2 pos, Texture tex, Body bod ) {
		super( n, pos, tex, bod, false );
=======
	public Screw( String name, Vector2 pos, Texture tex, Body body ) {
		super( name, null, ( body == null ? null : body.getWorld( ) ), false,
				pos, 0.0f, new Vector2( 1.0f, 1.0f ), tex );
>>>>>>> 7ea9392a83464bbd1e547df5b3ec94a7a78e51f9
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

	protected int rotation;
	protected int depth;
	protected int maxDepth;
	protected int screwStep;
	protected final short CATEGORY_SCREWS = 0x0008;

}
