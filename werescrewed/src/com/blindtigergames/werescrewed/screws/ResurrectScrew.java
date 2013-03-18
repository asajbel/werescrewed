package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

/**
 * resurrect screws allow one alive player to screw in a dead player once the
 * dead player reaches the screw he is brought back to life
 * 
 * @author dennis
 * 
 */
public class ResurrectScrew extends Screw {
	private PulleyJoint pulleyJoint;
	private Body pulleyWeight;
	private Player deadPlayer;
	private boolean pullLeft;
	private boolean destroyJoint = false;
	private boolean removeNextStep = false;

	/**
	 * 
	 * @param name
	 * @param pos
	 * @param entity
	 * @param world
	 * @param deadPlayer
	 */
	public ResurrectScrew( Vector2 pos, Entity entity, World world,
			Player deadPlayer ) {
		super( "onlyInstance_RezScrew", pos, null );
		this.world = world;
		this.depth = 1;
		this.deadPlayer = deadPlayer;
		active = true;
		screwType = ScrewType.SCREW_RESURRECT;
		entityType = EntityType.SCREW;

		sprite.setColor( 200f / 255f, 200f / 255f, 200f / 255f, 0.33f );

		constructBody( pos );
		connectScrewToEntity( entity );
		constructPulley( );
	}

	/**
	 * if the pulley weight goes to the left use left to draw dead player closer
	 */
	@Override
	public void screwLeft( ) {
		if ( pullLeft ) {
			body.setAngularVelocity( 15 );
			rotation += 10;
			screwStep = depth + 5;
			pulleyWeight.setLinearVelocity( new Vector2( -1f, 0f ) );
		}
	}

	@Override
	public void screwLeft( int region ) {
		if ( pullLeft ) {
			body.setAngularVelocity( 15 );
			rotation += 10;
			screwStep = depth + 5;
			pulleyWeight.setLinearVelocity( new Vector2( -1f, 0f ) );
		}
	}

	@Override
	public void screwLeft( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( pullLeft ) {
			diff = startRegion - region;
			newDiff = diff - prevDiff;
			if ( newDiff > 10 ) {
				newDiff = 0;
			}
			prevDiff = diff;

			body.setAngularVelocity( 1 );
			depth += newDiff;
			spriteRegion += region;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 5;
			pulleyWeight.setLinearVelocity( new Vector2( -1f, 0f ) );
		}

	}

	/**
	 * if the pulley weight goes to the right use right to draw dead player
	 * closer
	 */
	@Override
	public void screwRight( ) {
		if ( !pullLeft ) {
			body.setAngularVelocity( -15 );
			rotation -= 10;
			screwStep = depth + 5;
			pulleyWeight.setLinearVelocity( new Vector2( 1f, 0f ) );
		}
	}

	@Override
	public void screwRight( int region ) {
		if ( !pullLeft ) {
			body.setAngularVelocity( -15 );
			rotation -= 10;
			screwStep = depth + 5;
			pulleyWeight.setLinearVelocity( new Vector2( 1f, 0f ) );
		}
	}

	@Override
	public void screwRight( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( !pullLeft ) {
			diff = startRegion - region;
			newDiff = diff - prevDiff;
			if ( newDiff < -10 ) {
				newDiff = 0;
			}
			prevDiff = diff;

			body.setAngularVelocity( -1 );
			depth += newDiff;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 5;
			pulleyWeight.setLinearVelocity( new Vector2( 1f, 0f ) );
		}

	}

	/**
	 * look at collisions with the screw and determine if it is the dead player
	 * if so bring the player back to life
	 * 
	 * @param player
	 */
	public void hitPlayer( Player player ) {
		if ( player == deadPlayer ) {
			destroyJoint = true;
		}
	}

	/**
	 * check if this screw should be removed
	 */
	public boolean deleteQueue( ) {
		return removeNextStep;
	}

	/**
	 * set if this screw should be removed next step
	 */
	public void setRemove( boolean setRemoved ) {
		removeNextStep = setRemoved;
	}

	/**
	 * destroys the joints and body of the object
	 */
	@Override
	public void remove( ) {
		if ( !removed ) {
			if ( pulleyJoint != null ) {
				world.destroyJoint( pulleyJoint );
				pulleyJoint = null;
			}
			if ( !playerAttached ) {
				while ( body.getJointList( ).iterator( ).hasNext( ) ) {
					world.destroyJoint( body.getJointList( ).get( 0 ).joint );
				}
				world.destroyBody( body );
				removed = true;
				world.destroyBody( pulleyWeight );
				pulleyWeight = null;
			}
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( !removed ) {
			if ( destroyJoint ) {
				deadPlayer.body.setTransform(
						this.getPositionPixel( )
								.sub( deadPlayer.sprite.getWidth( ) / 3.0f,
										deadPlayer.sprite.getHeight( ) ).mul( Util.PIXEL_TO_BOX ), 0.0f );
				deadPlayer.respawnPlayer( );
				remove( );
				active = false;
			}
			sprite.setRotation( rotation );
			if ( depth != screwStep ) {
				screwStep--;
			}
			if ( depth == screwStep ) {
				body.setAngularVelocity( 0 );
				if ( pulleyWeight != null ) {
					pulleyWeight.setLinearVelocity( new Vector2( 0f, 0f ) );
				}
			}
		}
	}

	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.angle = pos.hashCode( );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape
				.setRadius( ( sprite.getWidth( ) / 2.0f ) * Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_PLAYER
				| Util.CATEGORY_SUBPLAYER;
		body.createFixture( screwFixture );
		body.setUserData( this );

		// add radar sensor to screw
		// CircleShape radarShape = new CircleShape( );
		// radarShape.setRadius( sprite.getWidth( ) * 1.25f * Util.PIXEL_TO_BOX
		// );
		// FixtureDef radarFixture = new FixtureDef( );
		// radarFixture.shape = radarShape;
		// radarFixture.isSensor = true;
		// radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
		// | Util.CATEGORY_SUBPLAYER;
		// body.createFixture( radarFixture );
		// radarShape.dispose( );

		screwShape.dispose( );
	}

	private void connectScrewToEntity( Entity entity ) {
		// connect the screw to the entity;
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}

	/**
	 * constructs a pulley one side with a kinematic body that is controlled by
	 * the screws rotation
	 */
	private void constructPulley( ) {
		if ( deadPlayer.body.getPosition( ).x > body.getPosition( ).x ) {
			pullLeft = true;
		} else {
			pullLeft = false;
		}
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.KinematicBody;
		screwBodyDef.position.set( body.getPosition( ) );
		pulleyWeight = world.createBody( screwBodyDef );

		PulleyJointDef pulleyJointDef = new PulleyJointDef( );
		pulleyJointDef.initialize( pulleyWeight, deadPlayer.body,
				body.getPosition( ), body.getPosition( ),
				pulleyWeight.getWorldCenter( ),
				deadPlayer.body.getWorldCenter( ), 1.0f );

		pulleyJoint = ( PulleyJoint ) world.createJoint( pulleyJointDef );
	}
}
