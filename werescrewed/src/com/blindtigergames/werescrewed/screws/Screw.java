package com.blindtigergames.werescrewed.screws;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Base class for the various types of screws. Defines basic behavior.
 * 
 * @param name
 *            - id of screw
 * @param pos
 *            - position in the world of the screw
 * @param tex
 *            - texture applied to the screw
 * 
 * @author Dennis
 * 
 */
public class Screw extends Entity {

	protected int rotation;
	protected int depth;
	protected int maxDepth;
	protected int screwStep;
	protected int spriteRegion;
	protected int startRegion;
	protected int newDiff;
	protected int prevDiff;
	protected int startDepth;
	protected int diff;
	protected boolean playerAttached = false;
	protected boolean removed = false;
	protected ScrewType screwType;
	protected ArrayList< RevoluteJoint > extraJoints;

	/**
	 * constructor to use if you want a cosmetic screw
	 * 
	 * @param name
	 * @param pos
	 * @param max
	 * @param entity
	 * @param skeleton
	 * @param world
	 */
	public Screw( String name, Vector2 pos, Entity entity, World world ) {
		super( name, pos, WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/screw1.png",
				Texture.class ), null, false );
		this.world = world;
		screwType = ScrewType.SCREW_COSMETIC;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< RevoluteJoint >( );

		constructBody( pos );
		addStructureJoint( entity );
	}

	/**
	 * constructor called by subclass screws don't use this to build a screw
	 * 
	 * @param name
	 * @param pos
	 * @param tex
	 */
	public Screw( String name, Vector2 pos, Texture tex ) {
		super( name, pos, ( tex == null ? WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/screw.png",
				Texture.class ) : tex ), null, false );
		entityType = EntityType.SCREW;
	}

	/**
	 * destroys everything contained within the screw instance
	 */
	@Override
	public void remove( ) {
		while ( body.getJointList( ).iterator( ).hasNext( ) ) {
			world.destroyJoint( body.getJointList( ).get( 0 ).joint );
		}
		world.destroyBody( body );
		removed = true;
	}

	/**
	 * returns true if the box2d stuff has been completely removed
	 */
	public boolean isRemoved( ) {
		return removed;
	}

	/**
	 * returns the joint at this index
	 */
	public RevoluteJoint getJoint( int index ) {
		if ( index < extraJoints.size( ) ) {
			return extraJoints.get( index );
		}
		return null;
	}

	/**
	 * Turns structural and puzzle screws to the left which decreases depth
	 * structural screws will eventually fall out
	 * 
	 * @param
	 */
	public void screwLeft( int region ) {
	}

	public void screwLeft( int region, boolean switchedDirections ) {
	}
	public void screwLeft( ) {
	}

	/**
	 * Turns structural and puzzle screws to the right which increases depth and
	 * tightens structural screws
	 * 
	 * @param
	 */
	public void screwRight( int region ) {
	}

	public void screwRight( int region, boolean switchedDirections ) {
	}
	
	public void screwRight( ) {
	}

	/**
	 * returns true if the screws body is jointed to a player
	 * 
	 * @return playerAttached
	 */
	public boolean isPlayerAttached( ) {
		return playerAttached;
	}

	/**
	 * sets if the player is attached to this screw
	 * 
	 * @param isPlayerAttached
	 */
	public void setPlayerAttached( boolean isPlayerAttached ) {
		playerAttached = isPlayerAttached;
	}

	/**
	 * Turns structural and puzzle screws to the left structural screws will
	 * eventually fall out
	 * 
	 * @param
	 */
	public int getDepth( ) {
		return depth;
	}

	/**
	 * public access to get max depth of a screw
	 * 
	 * @return value of maxDepth
	 */
	public int getMaxDepth( ) {
		return maxDepth;
	}

	public boolean endLevelFlag( ) {
		return false;
	}

	/**
	 * returns the ScrewType of the screw
	 * 
	 * @return ScrewType type of screw
	 */
	public ScrewType getScrewType( ) {
		return screwType;
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
		RevoluteJoint screwJoint = ( RevoluteJoint ) world
				.createJoint( revoluteJointDef );
		extraJoints.add( screwJoint );
	}

	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.gravityScale = 0.07f;
		screwBodyDef.fixedRotation = false;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape.setRadius( 0f );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		body.setUserData( this );

		// You dont dispose the fixturedef, you dispose the shape
		screwShape.dispose( );
	}
}
