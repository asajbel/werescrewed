package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.input.InputHandlerPlayer1;
import com.blindtigergames.werescrewed.screws.Screw;

/**
 * 
 * A representation of a player in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Player extends Entity {

	// FIELDS

	// Constants

	// Variables
	private int prevKey;
	private InputHandlerPlayer1 inputHandler;
	private PlayerState playerState;

	private Screw currentScrew;
	private RevoluteJoint playerToScrew;
	private boolean hitScrew;
	private boolean grounded;
	private boolean jumpPressed;
	private int anchorID;

	// Static constants
	public final static float MAX_VELOCITY = 1.8f;

	// Static variables
	public static Texture texture = new Texture(
			Gdx.files.internal( "data/player_r_m.png" ) );

	// Enums
	/**
	 * <p>
	 * <b>Values:</b>
	 * </p>
	 * <Ul>
	 * <Li>Standing</Li>
	 * <Li>Jumping</Li>
	 * <Li>Falling</Li>
	 * <Li>Screwing</Li>
	 * <Li>JumpingOffScrew</Li>
	 * </Ul>
	 */
	public enum PlayerState {
		Standing, Jumping, Falling, Screwing, JumpingOffScrew
	}

	// CONSTRUCTORS

	/**
	 * 
	 * @param world
	 *            in which the player exists
	 * @param pos
	 *            ition of the player in the world
	 * @param name
	 * @param tex
	 *            ture of the player sprite
	 */
	public Player( World world, Vector2 pos, String name, Texture tex ) {
		super( name, EntityDef.getDefinition( "playerTest" ), world, pos, 0.0f,
				new Vector2( 1f, 1f ), null, true);
		body.setGravityScale( 0.25f );
		body.setFixedRotation( true );
		this.world = world;
		body.setUserData( this );
		body.setBullet( true );
		playerState = PlayerState.Standing;
		inputHandler = new InputHandlerPlayer1( );
		anchorID = AnchorList.getInstance( ).addAnchor( true, pos );
	}

	/**
	 * 
	 * @param world
	 *            in which the player exists
	 * @param pos
	 *            ition of the player in the world
	 * @param name
	 */
	public Player( World world, Vector2 pos, String name ) {
		this( world, pos, name, texture );
	}

	// METHODS

	/**
	 * Moves the player right, or jumps them off of a screw to the right
	 */
	public void moveRight( ) {
		/*
		if ( playerState == PlayerState.Screwing ) {
			world.destroyJoint( playerToScrew );
			playerState = PlayerState.JumpingOffScrew;
			body.applyLinearImpulse( new Vector2( 0.05f, 0.0f ),
					body.getWorldCenter( ) );
			jump( );
		} else 
		*/
		if ( body.getLinearVelocity( ).x < MAX_VELOCITY ) {
			body.applyLinearImpulse( new Vector2( 0.01f, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player left, or jumps them off of a screw to the left
	 */
	public void moveLeft( ) {
		/*
		if ( playerState == PlayerState.Screwing ) {
			world.destroyJoint( playerToScrew );
			playerState = PlayerState.JumpingOffScrew;
			body.applyLinearImpulse( new Vector2( -0.05f, 0.0f ),
					body.getWorldCenter( ) );
			jump( );
		} else 
		*/
		if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
			body.applyLinearImpulse( new Vector2( -0.01f, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Causes the player to jump
	 */
	public void jump( ) {
		body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x, 0.0f ) );
		body.applyLinearImpulse( new Vector2( 0.0f, 0.15f ),
				body.getWorldCenter( ) );
	}

	/**
	 * Sets the current screw
	 */
	public void hitScrew( Screw screw ) {
		if ( playerState != PlayerState.Screwing ) {
			hitScrew = true;
			currentScrew = screw;
		}
	}

	/**
	 * Sets the current screw to null
	 */
	public void endHitScrew( ) {
		hitScrew = false;
	}

	/**
	 * Sets whether or not the player is grounded
	 * 
	 * @param grounded
	 */
	public void setGrounded( boolean grounded ) {
		this.grounded = grounded;
	}

	/**
	 * Checks if the player is grounded
	 * 
	 * @return a boolean representing whether or not the player is "grounded"
	 */
	public boolean isGrounded( ) {
		return grounded;
	}

	/**
	 * Attaches a player to the current screw
	 */
	private void attachToScrew( ) {
		if ( currentScrew.body.getJointList( ).size( ) > 0 ) {
			for ( Fixture f : body.getFixtureList( ) ) {
				f.getFilterData( ).maskBits = 0x0008;
			}
			body.setTransform( currentScrew.getPosition( ), 0.0f );
			// connect the screw to the skeleton;
			RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
			revoluteJointDef.initialize( body, currentScrew.body,
					currentScrew.getPosition( ) );
			revoluteJointDef.enableMotor = false;
			playerToScrew = ( RevoluteJoint ) world
					.createJoint( revoluteJointDef );
			playerState = PlayerState.Screwing;
		}
	}

	/**
	 * Stops the player
	 */
	private void stop( ) {
		float velocity = body.getLinearVelocity( ).x;
		if ( velocity != 0.0f ) {
			if ( velocity < -0.1f )
				body.applyLinearImpulse( new Vector2( 0.010f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity > 0.1f )
				body.applyLinearImpulse( new Vector2( -0.010f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity > -0.1 && velocity < 0.1f )
				body.setLinearVelocity( 0.0f, 0.0f );
		}
	}

	/**
	 * Updates information about the player every step
	 */
	public void update( float deltaTime ) {
		super.update( deltaTime );
		inputHandler.update( );
		AnchorList.getInstance( ).setAnchorPosBox( anchorID, getPosition( ) );
		if ( playerState != PlayerState.Screwing
				&& playerState != PlayerState.Standing && isGrounded( ) ) {
			playerState = PlayerState.Standing;
		}

		if ( inputHandler.jumpPressed( ) ) {
			if ( !jumpPressed ) {
				if ( playerState == PlayerState.Screwing ) {
					world.destroyJoint( playerToScrew );
					playerState = PlayerState.JumpingOffScrew;
					jump( );
				} else if ( isGrounded( ) ) {
					jump( );
				}
				jumpPressed = true;
			}
		}
		if ( !inputHandler.jumpPressed( ) ) {
			jumpPressed = false;
		}
		if ( inputHandler.leftPressed( ) ) {
			moveLeft( );
			prevKey = Keys.A;
		}

		if ( inputHandler.rightPressed( ) ) {
			moveRight( );
			prevKey = Keys.D;
		}
		if ( inputHandler.downPressed( ) ) {
			if ( playerState == PlayerState.Screwing ) {
				world.destroyJoint( playerToScrew );
				playerState = PlayerState.JumpingOffScrew;
			}
			stop( );
		}

		if ( ( !inputHandler.leftPressed( ) && !inputHandler.rightPressed( ) )
				&& ( prevKey == Keys.D || prevKey == Keys.A ) ) {
			stop( );
		}

		if ( inputHandler.screwPressed( ) && hitScrew
				&& playerState != PlayerState.Screwing ) {
			attachToScrew( );
		}

		if ( playerState == PlayerState.Screwing ) {
			if ( inputHandler.unscrewing( ) ) {
				currentScrew.screwLeft( );
			} else if ( inputHandler.screwing( ) ) {
				currentScrew.screwRight( );
			}
			if ( currentScrew.body.getJointList( ).size( ) <= 1 ) {
				world.destroyJoint( playerToScrew );
				playerState = PlayerState.JumpingOffScrew;
				jump( );
				for ( Fixture fix : body.getFixtureList( ) ) {
					fix.getFilterData( ).maskBits = 0x0008;
				}
			}
		}

		if ( playerState == PlayerState.JumpingOffScrew ) {
			if ( body.getLinearVelocity( ).y < 0 ) {
				for ( Fixture fix : body.getFixtureList( ) ) {
					fix.getFilterData( ).maskBits = 0x0001;
				}
			}
		}

	}

}
