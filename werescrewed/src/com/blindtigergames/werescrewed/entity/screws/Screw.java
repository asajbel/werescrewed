package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.blindtigergames.werescrewed.sound.SoundManager;
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
	protected Entity entity;
	protected Vector2 detachDirection;
	protected Vector2 interfaceOffset = new Vector2( 87f, -10f );
	protected boolean upDownDetach;
	protected float entityAngle;
	protected boolean playerAttached = false;
	protected boolean removed = false;
	protected boolean playerNotSensor = false;
	protected ScrewType screwType;
	public ArrayList< Joint > extraJoints;
	
	public static float SCREW_SOUND_DELAY = 0.5f;
	public static float UNSCREW_SOUND_DELAY = 0.5f;
	
	private static TextureRegion screwTexRegion = WereScrewedGame.manager.getAtlas( "common-textures" ).findRegion( "flat_head_circular" );

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
		super( name, pos, null, null, false );
		loadSounds();
		this.world = world;
		this.sprite = constructSprite(screwTexRegion);
		this.entity = entity;
		this.entityAngle = entity.getAngle( );
		screwType = ScrewType.SCREW_COSMETIC;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< Joint >( );
		constructBody( pos );
		if(sprite!=null)sprite.rotate( ( float ) ( Math.random( )*360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )*Util.DEG_TO_RAD );
		addStructureJoint( entity );
	}

	protected void loadSounds( ) {
		sounds = new SoundManager();
		sounds.getSound( "screwing" , WereScrewedGame.dirHandle
				+ "/common/sounds/screwing.ogg");
		sounds.getSound( "unscrewing" , WereScrewedGame.dirHandle
				+ "/common/sounds/unscrewing.ogg");
	}

	/**
	 * constructor called by subclass screws don't use this to build a screw
	 * 
	 * @param name
	 * @param pos
	 * @param tex
	 */
	public Screw( String name, Vector2 pos, Texture tex ) {
		super( name, pos, null, null, false );
		this.sprite = constructSprite(screwTexRegion);
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

	@Override
	public void dispose( ) {
		remove( );
	}
	
	/**
	 * returns true if the box2d stuff has been completely removed
	 */
	public boolean isRemoved( ) {
		return removed;
	}

	/**
	 * sets the detach direction
	 */
	public void setDetachDirection( float x, float y ) {
		if ( detachDirection != null ) {
			detachDirection.x = x;
			detachDirection.y = y;
		} else {
			detachDirection = new Vector2( x, y );
		}
	}
	
	/**
	 * gets the detach direction
	 */
	public Vector2 getDetachDirection( ) {
		if ( detachDirection == null ) {
			return Vector2.Zero;
		} 
		return detachDirection;
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
		sounds.playSound( "unscrewing" , UNSCREW_SOUND_DELAY );
	}

	/**
	 * used by keyboard controls to screw left
	 */
	public void screwLeft( ) {
		sounds.playSound( "unscrewing" , UNSCREW_SOUND_DELAY );
	}

	/**
	 * used by controller controls to screw right
	 * @param region
	 * @param switchedDirections
	 */
	public void screwRight( int region, boolean switchedDirections ) {
		sounds.playSound( "screwing" , SCREW_SOUND_DELAY );
	}

	/**
	 * used by keyboard controls to screw right
	 */
	public void screwRight( ) {
		sounds.playSound( "screwing" , SCREW_SOUND_DELAY );
	}

	
	/**
	 * used when the player is not screwing either left or right.
	 * 
	 */
	public void stopScrewing(){
		if (sounds != null){
			sounds.stopSound("screwing");
			sounds.stopSound("unscrewing");
		}
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
		this.entity = entity;
		if ( entity != null ) {
			this.entityAngle = entity.getAngle( );
		}
	}

	public void addStructureJoint( Entity entity, float degreeLimit ) {
		// connect other structure to structure screw
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( entity.body, body, body.getPosition( ) );
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.enableLimit = true;
		revoluteJointDef.upperAngle = degreeLimit * Util.DEG_TO_RAD;
		revoluteJointDef.lowerAngle = -degreeLimit * Util.DEG_TO_RAD;

		revoluteJointDef.maxMotorTorque = 30f;
		revoluteJointDef.motorSpeed = 0.1f;
		body.setFixedRotation( true );
		
		Joint screwJoint =  (Joint) world.createJoint( revoluteJointDef );
		
		extraJoints.add( screwJoint );
		this.entity = entity;
		if ( entity != null ) {
			this.entityAngle = entity.getAngle( );
		}
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
