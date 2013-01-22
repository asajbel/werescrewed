package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: holds general methods for screws
 * 
 * @author Dennis
 * 
 */

public class Screw extends Entity {
	public Screw( String n, Vector2 pos, Texture tex, Body bod ) {
		super( n, pos, tex, bod );
	}

	public void update( ) {
		super.update( );
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
