package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * @descrip: holds general methods for screws
 * 
 * @author Dennis
 * 
 */

public class Screw extends Entity {
	public Screw( String n, Vector2 pos, Texture tex, Body bod ) {
		super( n, pos, tex, bod, false );
	}
	
	public Screw( String name, Vector2 pos, Body body ) {
		super( name, pos,
				new Texture( Gdx.files.internal( "data/screw.png" ) ), body,
				false );
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
