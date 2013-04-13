package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
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
	private Player deadPlayer;
	private Vector2 playerOffset;
	private boolean removeNextStep = false;
	private LerpMover playerMover;
	private Entity screwInterface;

	/**
	 * 
	 * @param name
	 * @param pos
	 * @param entity
	 * @param world
	 * @param deadPlayer
	 */
	public ResurrectScrew( Vector2 pos, Entity entity, World world,
			Player deadPlayer, LerpMover lm, Vector2 offset ) {
		super( "rezScrew", pos, null );
		this.world = world;
		this.depth = 0;
		this.maxDepth = 50;
		this.deadPlayer = deadPlayer;
		this.playerOffset = new Vector2 ( offset.x, offset.y );
		playerMover = lm;
		extraJoints = new ArrayList< Joint >( );
		active = true;
		screwType = ScrewType.SCREW_RESURRECT;
		entityType = EntityType.SCREW;
		screwInterface = new Entity( name + "_screwInterface", pos, null, null,
				false );
		SimpleFrameAnimator interfaceAnimator = new SimpleFrameAnimator( )
				.speed( 1f ).loop( LoopBehavior.STOP ).startFrame( 1 )
				.maxFrames( 2 ).time( 0.0f );
		screwInterface.sprite = new Sprite(
				WereScrewedGame.manager.getTextureAtlas( "screwInterface" ),
				interfaceAnimator );
		sprite.setColor( 0f, 0f, 1f, 1f );

		constructBody( pos );
		if ( sprite != null )
			sprite.rotate( ( float ) ( Math.random( ) * 360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )
				* Util.DEG_TO_RAD );
		connectScrewToEntity( entity );
	}

	/**
	 * if the pulley weight goes to the left use left to draw dead player closer
	 */
	@Override
	public void screwLeft( ) {
		if ( depth > 0 ) {
			depth -= 2;
			body.setAngularVelocity( 15 );
			rotation += 10;
			screwStep = depth + 5;
			if ( deadPlayer.isPlayerDead( ) ) {
				playerMover.moveAnalog( this, ( float ) depth
						/ ( ( float ) maxDepth ), deadPlayer.body );
			}
			// int value = (int ) ( ( (float) depth / (float)maxDepth ) * 9f );
			// screwInterface.sprite.getAnimator( ).setFrame( value );
		}
	}

	@Override
	public void screwLeft( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( depth > 0 ) {
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
			if ( deadPlayer.isPlayerDead( ) ) {
				playerMover.moveAnalog( this, ( float ) depth
						/ ( ( float ) maxDepth ), deadPlayer.body );
			}
			// int value = (int ) ( ( (float) depth / (float)maxDepth ) * 9f );
			// screwInterface.sprite.getAnimator( ).setFrame( value );
		}

	}

	/**
	 * if the pulley weight goes to the right use right to draw dead player
	 * closer
	 */
	@Override
	public void screwRight( ) {
		if ( depth < maxDepth ) {
			depth++;
			body.setAngularVelocity( -15 );
			rotation -= 10;
			screwStep = depth + 5;
			if ( deadPlayer.isPlayerDead( ) ) {
				playerMover.moveAnalog( this, ( float ) depth
						/ ( ( float ) maxDepth ), deadPlayer.body );
			}
			// int value = (int ) ( ( (float) depth / (float)maxDepth ) * 9f );
			// screwInterface.sprite.getAnimator( ).setFrame( value );
		}
	}

	@Override
	public void screwRight( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( depth < maxDepth ) {
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
			if ( deadPlayer.isPlayerDead( ) ) {
				playerMover.moveAnalog( this, ( float ) depth
						/ ( ( float ) maxDepth ), deadPlayer.body );
			}
			// int value = (int ) ( ( (float) depth / (float)maxDepth ) * 9f );
			// screwInterface.sprite.getAnimator( ).setFrame( value );
		}

	}

	/**
	 * look at collisions with the screw and determine if it is the dead player
	 * if so bring the player back to life
	 * 
	 * @param player
	 */
	public void hitPlayer( Player player ) {
//		if ( player == deadPlayer ) {
//			destroyJoint = true;
//		}
	}

	/**
	 * returns the dead player attached
	 */
	public Player getDeadPlayer( ) {
		return deadPlayer;
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
			if ( !playerAttached ) {
				while ( body.getJointList( ).iterator( ).hasNext( ) ) {
					world.destroyJoint( body.getJointList( ).get( 0 ).joint );
				}
				world.destroyBody( body );
				removed = true;
			}
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( !removed ) {
			// if ( playerAttached ) {
//			screwInterface.sprite.setPosition( this.getPositionPixel( ) );
//			screwInterface.sprite.update( deltaTime );
			// }
			if ( playerMover.atEnd( ) || depth == maxDepth ) {
				deadPlayer.body.setTransform(
						this.getPositionPixel( )
								.sub( Player.WIDTH / 3.0f, Player.HEIGHT )
								.mul( Util.PIXEL_TO_BOX ), 0.0f );
				deadPlayer.body.setType( BodyType.DynamicBody );
				deadPlayer.body.setLinearVelocity( Vector2.Zero );
				deadPlayer.respawnPlayer( );
				remove( );
				active = false;
			}
			if ( active ) {
				sprite.setPosition( this.getPositionPixel( ) );
				sprite.setRotation( rotation );
				if ( depth != screwStep ) {
					screwStep--;
				}
				if ( depth == screwStep ) {
					body.setAngularVelocity( 0 );
				}
				if ( deadPlayer.isPlayerDead( ) ) {
					Vector2 temp = this.getPositionPixel( ).cpy( );
					playerMover.changeEndPos( temp );
					if ( playerOffset.x > 0 ) {
						playerMover.changeBeginPos( temp.sub(
								playerOffset ) );
					} else {
						playerMover.changeBeginPos( temp.sub(
								playerOffset ) );
					}
					playerMover.moveAnalog( this, ( float ) depth
							/ ( ( float ) maxDepth ), deadPlayer.body );
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
}
