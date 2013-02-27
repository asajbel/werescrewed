package com.blindtigergames.werescrewed.checkpoints;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
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
	private String levelLoadStage;
	private boolean active = false;
	private ProgressManager progressManager;
	private boolean removed = false;

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
		super( name, pos, WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/cletter.png",
				Texture.class ), null, false );
		this.world = world;
		this.progressManager = pm;
		this.levelLoadStage = levelToReload;
		this.entityType = EntityType.CHECKPOINT;
		sprite.setColor( Color.PINK );
		constructBody( pos );
		connectScrewToEntity( entity );
	}

	/**
	 * destroys the joints
	 */
	public void remove( ) {
		while ( body.getJointList( ).iterator( ).hasNext( ) ) {
			world.destroyJoint( body.getJointList( ).get( 0 ).joint );
		}
		world.destroyBody( body );
		removed = true;
	}
	
	/**
	 * returns whether or not this entity has been removed
	 */
	public boolean isRemoved( ) {
		return removed;
	}
	
	/**
	 * activates this checkpoint if not already active
	 */
	public void hitPlayer( ) {
		if ( !active ) {
			body.setAngularVelocity( 3f );
		}
		Filter filter = new Filter( );
		for ( Fixture f : body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			filter.categoryBits = Util.CATEGORY_IGNORE;
			filter.maskBits = Util.CATEGORY_NOTHING;
			f.setFilterData( filter );
		}
		active = true;
		progressManager.hitNewCheckPoint( this );
	}

	/**
	 * returns whether the checkpoint is the most recent active checkpoint
	 * 
	 * @return active
	 */
	public boolean isActive( ) {
		return active;
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( active ) {
			if ( body.getAngle( ) >= 90f * Util.DEG_TO_RAD ) {
				body.setAngularVelocity( 0.0f );
				active = false;
			}
		}
	}

	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef checkPBodyDef = new BodyDef( );
		checkPBodyDef.type = BodyType.DynamicBody;
		checkPBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		checkPBodyDef.fixedRotation = false;
		body = world.createBody( checkPBodyDef );
		CircleShape checkPShape = new CircleShape( );
		checkPShape.setRadius( ( sprite.getWidth( ) / 2.0f )
				* Util.PIXEL_TO_BOX );
		FixtureDef checkPFixture = new FixtureDef( );
		checkPFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
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