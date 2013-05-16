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
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.Metrics.TrophyMetric;
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
	private SimpleFrameAnimator screwUIAnimator;
	private final int startFrame = 15;
	private final int lastMotionFrame = 14;
	private final int animeSteps = 12;

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
		loadSounds( );
		this.world = world;
		this.depth = 0;
		this.maxDepth = 50;
		this.deadPlayer = deadPlayer;
		this.playerOffset = new Vector2( offset.x, offset.y );
		playerMover = lm;
		extraJoints = new ArrayList< Joint >( );
		active = true;
		screwType = ScrewType.SCREW_RESURRECT;
		entityType = EntityType.SCREW;
		screwInterface = new Entity( name + "_screwInterface", pos, null, null,
				false );
		TextureAtlas atlas = WereScrewedGame.manager
				.getTextureAtlas( "screwInterface" );
		screwUIAnimator = new SimpleFrameAnimator( ).speed( 0f )
				.loop( LoopBehavior.STOP ).time( 0.0f ).startFrame( 0 )
				.maxFrames( 35 );
		Sprite spr = new Sprite( atlas, screwUIAnimator );
		spr.setOrigin( spr.getWidth( ) / 2.0f, spr.getHeight( ) / 2.0f );
		screwInterface.changeSprite( spr );
		sprite.setColor( 98f / 255f, 146f / 255f, 169f / 255f, 0.6f );
		constructBody( pos );
		if ( sprite != null )
			sprite.rotate( ( float ) ( Math.random( ) * 360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )
				* Util.DEG_TO_RAD );
		rotation = ( int ) ( body.getAngle( ) * Util.RAD_TO_DEG );
		connectScrewToEntity( entity );
	}

	/**
	 * if the pulley weight goes to the left use left to draw dead player closer
	 */
	@Override
	public void screwLeft( ) {
		if ( depth > 0 ) {
			depth -= 1;
			body.setAngularVelocity( 15 );
			rotation += 10;
			screwStep = depth + 5;
			if ( deadPlayer.isPlayerDead( ) ) {
				playerMover.moveAnalog( this, ( float ) depth
						/ ( ( float ) maxDepth ), deadPlayer.body );
			}
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
			if ( newDiff != 0 )
				newDiff /= newDiff;
			newDiff *= -1;
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
			if ( newDiff != 0 )
				newDiff /= newDiff;
			depth += newDiff;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 5;
			if ( deadPlayer.isPlayerDead( ) ) {
				playerMover.moveAnalog( this, ( float ) depth
						/ ( ( float ) maxDepth ), deadPlayer.body );
			}
		}

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
				if ( body != null ) {
					while ( body.getJointList( ).iterator( ).hasNext( ) ) {
						world.destroyJoint( body.getJointList( ).get( 0 ).joint );
					}
					world.destroyBody( body );
					removed = true;
					body = null;
				}
			} else {
				removeNextStep = true;
			}
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( !removed ) {
			if ( playerMover.atEnd( ) || depth == maxDepth ) {
				deadPlayer.body
						.setTransform(
								this.getPositionPixel( )
										.sub( Player.WIDTH / 3.0f,
												Player.HEIGHT / 2.0f )
										.mul( Util.PIXEL_TO_BOX ), 0.0f );
				deadPlayer.body.setType( BodyType.DynamicBody );
				deadPlayer.body.setLinearVelocity( Vector2.Zero );
				deadPlayer.respawnPlayer( );
				remove( );
				active = false;

				// Trophy check for revived player
				if ( deadPlayer.name == Metrics.player1( ) ) {
					// If player 1 is revived, give player 2 the point
					Metrics.incTrophyMetric( TrophyMetric.P2REVIVES, 0.5f );
					// System.out.println("player1 revive " +
					// Metrics.getTrophyMetric( TrophyMetric.P2REVIVES ) );
				} else if ( deadPlayer.name == Metrics.player2( ) ) {
					// If player 2 is revived, give player 1 the point
					Metrics.incTrophyMetric( TrophyMetric.P1REVIVES, 0.5f );
				}
			}
			if ( active ) {
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
						playerMover.changeBeginPos( temp.sub( playerOffset ) );
					} else {
						playerMover.changeBeginPos( temp.sub( playerOffset ) );
					}
					playerMover.moveAnalog( this, ( float ) depth
							/ ( ( float ) maxDepth ), deadPlayer.body );
				}
				if ( playerAttached ) {
					if ( screwInterface.sprite.getAnimator( ).getFrame( ) == 0 ) {
						screwUIAnimator.speed( 1 );
					} else if ( screwInterface.sprite.getAnimator( ).getFrame( ) > lastMotionFrame ) {
						screwUIAnimator.speed( 0 );
						int value = ( int ) ( ( ( float ) depth / ( float ) maxDepth ) * animeSteps )
								+ startFrame;
						screwUIAnimator.setFrame( value );
					}
				} else {
					if ( screwInterface.sprite.getAnimator( ).getFrame( ) > lastMotionFrame ) {
						screwUIAnimator.setFrame( lastMotionFrame );
					}
					screwUIAnimator.speed( -1 );
				}
				screwInterface.sprite.setPosition( this.getPositionPixel( )
						.sub( interfaceOffset ) );
				screwInterface.sprite.update( deltaTime );
				screwUIAnimator.update( deltaTime );
			}
		}
	}

	@Override
	public void draw( SpriteBatch batch, float deltaTime ) {
		if ( playerAttached ) {
			screwInterface.sprite.draw( batch );
		}
		// drawParticles( behindParticles, batch );
		if ( sprite != null && visible && !removeNextStep ) {
			sprite.draw( batch );
		}
		// drawOrigin(batch);
		// drawParticles( frontParticles, batch );
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
