package com.blindtigergames.werescrewed.checkpoints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.animator.SimpleSpinemator;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

/**
 * checkpoint class handles collisions and activations and de-activation of
 * checkpoints also could be used to reference which stage of the level to go
 * back to.
 * 
 * @author dennis
 * 
 */
public class CheckPoint extends Entity {
	private boolean active = false;
	private ProgressManager progressManager;
	private boolean removed = false;
	// private SimpleFrameAnimator checkpointFrameAnimator;
	private Entity entity;
	private static final float radius = 64;

	/**
	 * builds a checkpoint jointed to a skeleton
	 * 
	 * @param name
	 * @param pos
	 * @param max
	 * @param skeleton
	 * @param world
	 */
	public CheckPoint( String name, Vector2 pos, Entity entity, World world,
			ProgressManager pm, String levelToReload ) {
		super( name, pos, null, null, false );
		this.world = world;
		this.progressManager = pm;
		this.entityType = EntityType.CHECKPOINT;
		this.entity = entity;
		if ( progressManager.currentCheckPoint == null ) {
			progressManager.currentCheckPoint = this;
		}
		setSpinemator( new SimpleSpinemator( "rez_chamber_atlas", "chamber",
				"off-idle", false ) );
		getSpinemator().setPosition( pos.x, pos.y );

		constructBody( pos );
		connectScrewToEntity( entity );
	}

	/**
	 * destroys the joints
	 */
	@Override
	public void remove( ) {
		if ( body != null ) {
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
	 * returns whether or not this entity has been removed
	 */
	public boolean isRemoved( ) {
		return removed;
	}

	/**
	 * sets the next checkpoint to one after this one
	 */
	public void setNextCheckPointInPM( ) {
		progressManager.setNextChkpt( this );
	}
	
	/**
	 * activates this checkpoint if not already active
	 */
	public void hitPlayer( Player player ) {
		if ( player.body.getType( ) != BodyType.KinematicBody ) {
			if ( !active ) {
				// checkpointFrameAnimator.speed( 1.0f );
				getSpinemator().changeAnimation( "on-idle", true );
				// body.setAngularVelocity( 3f );
			}
			active = true;
		}
		progressManager.hitNewCheckPoint( this, player );
	}
	
	/**
	 * returns the entity this checkpoint is attached to
	 */
	public Entity getEntity( ) {
		return entity;
	}
	
	/**
	 * returns whether the checkpoint is the most recent active checkpoint
	 * 
	 * @return active
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * deactivates a checkpoint
	 */
	public void deactivate( ) {
		active = false;
		// checkpointFrameAnimator.speed( -1.0f );
		getSpinemator().changeAnimation( "off-idle", false );
		// body.setAngularVelocity( -3f );
	}

	@Override
	public void update( float deltaTime ) {
		Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		getSpinemator().setPosition( bodyPos );
		getSpinemator().setRotation( body.getAngle( ) );
		super.update( deltaTime );
	}

	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef checkPBodyDef = new BodyDef( );
		checkPBodyDef.type = BodyType.DynamicBody;
		checkPBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		checkPBodyDef.fixedRotation = false;
		body = world.createBody( checkPBodyDef );
		CircleShape checkPShape = new CircleShape( );
		checkPShape.setRadius( ( radius ) * Util.PIXEL_TO_BOX );
		FixtureDef checkPFixture = new FixtureDef( );
		checkPFixture.filter.categoryBits = Util.CATEGORY_CHECKPOINTS;
		checkPFixture.filter.maskBits = Util.CATEGORY_PLAYER
				| Util.CATEGORY_SUBPLAYER;
		checkPFixture.shape = checkPShape;
		checkPFixture.isSensor = true;
		body.createFixture( checkPFixture );
		body.setUserData( this );

		// You dont dispose the fixturedef, you dispose the shape
		checkPShape.dispose( );
	}

	/**
	 * joints this checkpoint to some entity or skeleton
	 * 
	 * @param entity
	 */
	private void connectScrewToEntity( Entity entity ) {
		// connect the screw to the entity
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}
}
