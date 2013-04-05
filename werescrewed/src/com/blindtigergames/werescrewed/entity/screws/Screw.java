package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
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
	protected boolean playerNotSensor = false;
	protected ScrewType screwType;
	public ArrayList< Joint > extraJoints;

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
				WereScrewedGame.dirHandle.path( ) + "/common/screw/screw.png",
				Texture.class ), null, false );
		this.world = world;
		if(sprite!=null)sprite.rotate( ( float ) ( Math.random( )*360 ) );
		screwType = ScrewType.SCREW_COSMETIC;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< Joint >( );

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
				WereScrewedGame.dirHandle.path( ) + "/common/screw/screw.png",
				Texture.class ) : tex ), null, false );
		if(sprite!=null)sprite.rotate( ( float ) ( Math.random( )*360 ) );
		entityType = EntityType.SCREW;
	}

	/**
	 * destroys everything contained within the screw instance
	 */
	@Override
	public void remove( ) {
		if ( !removed ) {
			while ( body.getJointList( ).iterator( ).hasNext( ) ) {
				world.destroyJoint( body.getJointList( ).get( 0 ).joint );
			}
			world.destroyBody( body );
			body = null;
			removed = true;
		}
	}

	/**
	 * returns true if the box2d stuff has been completely removed
	 */
	public boolean isRemoved( ) {
		return removed;
	}

	/**
	 * returns true if this screw is on a rope
	 */
	public boolean playerNotSensor( ) {
		return playerNotSensor;
	}
	
	/**
	 * sets this screw to being on a rope;
	 */
	public void setPlayerNotSensor( ) {
		playerNotSensor = true;
	}
	
	/**
	 * returns the joint at this index
	 */
	public Joint getJoint( int index ) {
		if ( index < extraJoints.size( ) ) {
			return extraJoints.get( index );
		}
		return null;
	}

	/**
	 * used by controller controls to screw left
	 * @param region
	 * @param switchedDirections
	 */
	public void screwLeft( int region, boolean switchedDirections ) {
	}

	/**
	 * used by keyboard controls to screw left
	 */
	public void screwLeft( ) {
	}

	/**
	 * used by controller controls to screw right
	 * @param region
	 * @param switchedDirections
	 */
	public void screwRight( int region, boolean switchedDirections ) {
	}

	/**
	 * used by keyboard controls to screw right
	 */
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
	 * this screw is attached using a revolute joint
	 * 
	 * @param entity
	 */
	public void addStructureJoint( Entity entity ) {
		// connect other structure to structure screw
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		Joint screwJoint =  (Joint) world.createJoint( revoluteJointDef );
		extraJoints.add( screwJoint );
	}

	/**
	 * attaches another object using a weld joint
	 * @param entity
	 */
	public void addWeldJoint( Entity entity ) {
		WeldJointDef weldJointDef = new WeldJointDef();
		weldJointDef.initialize( body, entity.body, body.getPosition( ) );
		Joint screwJoint =  (Joint) world.createJoint( weldJointDef );
		extraJoints.add( screwJoint );
		
	}
	
	/**
	 * builds the screw body
	 * @param pos
	 */
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