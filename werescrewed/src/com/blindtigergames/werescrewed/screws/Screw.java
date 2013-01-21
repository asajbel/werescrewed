package com.blindtigergames.werescrewed.screws;

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
		super( n, pos, tex, bod );
	}

	public void update( ) {
		super.update( );
	}

	public void remove( ) {
		world.destroyBody( body );
	}

	public void screwLeft( ) {
		body.setAngularVelocity( 15 );
		depth--;
		rotation += 10;
		screwStep = depth + 5;
	}

	public void screwRight( ) {
		if ( depth < maxDepth ) {
			body.setAngularVelocity( -15 );
			depth++;
			rotation -= 10;
			screwStep = depth + 5;
		}
	}

	public int getRotation( ) {
		return rotation;
	}

	public int getDepth( ) {
		return depth;
	}

	public void exampleCollide( ) {
		System.out.println( "Hello from screw" );
	}

	public boolean collisionCheck( Vector2 pos ) {
		float dx = pos.x - body.getPosition( ).x;
		float dy = pos.y - body.getPosition( ).y;
		float magnitude = dx*dx + dy*dy;
		if( magnitude < radius*radius ) {
			return true;
		}
		return false;
	}
	
	protected float radius;
	protected int rotation;
	protected int depth;
	protected int maxDepth;
	protected int screwStep;
	protected final short CATEGORY_SCREWS = 0x0008;

}
