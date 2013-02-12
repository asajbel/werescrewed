package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * Base class for the various types of screws. Defines basic behavior.
 * 
 * @param name - id of screw
 * @param pos - position in the world of the screw
 * @param tex - texture applied to the screw
 * 
 * @author Dennis
 * 
 */

public class Screw extends Entity {

	protected int rotation;
	protected int depth;
	protected int maxDepth;
	protected int screwStep;

	public Screw( String name, Vector2 pos, Texture tex ) {
		super( name, pos, ( tex == null ? new Texture(
				Gdx.files.internal( "data/screw.png" ) ) : tex ), null, false );
	}

	/**
	 * destroys everything contained within the screw instance
	 */
	public void remove( ) {
		world.destroyBody( body );
	}

	/**
	 * Turns structural and puzzle screws to the left
	 * which decreases depth
	 * structural screws will eventually fall out
	 * @param
	 */
	public void screwLeft( ) {
	}
	
	/**
	 * Turns structural and puzzle screws to the right
	 * which increases depth and tightens structural screws
	 * @param
	 */
	public void screwRight( ) {
	}
	/**
	 * Turns structural and puzzle screws to the left
	 * structural screws will eventually fall out
	 * @param
	 */
	public int getDepth( ) {
		return depth;
	}
	
	/**
	 * attaches any other object between this screw and the main entity that
	 * this screw is attached
	 * 
	 * @param entity
	 */
	public void addStructureJoint( Entity entity ) {
		// connect other structure to structure screw
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}

}
