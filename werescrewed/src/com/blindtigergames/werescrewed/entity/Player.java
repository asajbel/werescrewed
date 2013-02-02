package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.input.InputHandler;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * A representation of a player in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Player extends Entity {
	
	int check = 0;

	// FIELDS

	// Constants

	// Variables
	private int prevKey;
	private InputHandler inputHandler;
	private PlayerState playerState;

	private Screw currentScrew;
	private RevoluteJoint playerToScrew;
	private boolean hitScrew;
	private boolean grounded;
	private boolean jumpPressed;
	private int anchorID;
	int veloTest  = 5;
	

	// Static constants
	public final static float MAX_VELOCITY = 1.8f;
	public final static float MOVEMENT_IMPLUSE = 0.01f;
	public final static float JUMP_IMPLUSE = 0.15f;

	// Static variables
	public static Texture texture = new Texture(
			Gdx.files.internal( "data/player_r_m.png" ) );

	// Enums
	/**
	 * <p>
	 * <b>Values:</b>
	 * </p>
	 * <Ul>
	 * Standing <br />
	 * Jumping <br />
	 * Falling <br />
	 * Screwing <br />
	 * JumpingOffScrew
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
	 */
	public Player( String name, World world, Vector2 pos ) {
		super( name, EntityDef.getDefinition( "playerTest" ), world, pos, 0.0f,
				new Vector2( 1f, 1f ), null, true );
		body.setGravityScale( 0.25f );
		body.setFixedRotation( true );
		this.world = world;
		body.setUserData( this );
		body.setBullet( true );
		playerState = PlayerState.Standing;
		inputHandler = new InputHandler( this.name );
		anchorID = AnchorList.getInstance( ).addAnchor( true, pos );
	}

	// METHODS

	/**
	 * Moves the player right, or jumps them off of a screw to the right
	 */
	public void moveRight( ) {
		if ( body.getLinearVelocity( ).x < MAX_VELOCITY ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPLUSE, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player left, or jumps them off of a screw to the left
	 */
	public void moveLeft( ) {
		if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
			body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPLUSE, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Causes the player to jump
	 */
	public void jump( ) {
		body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x, 0.0f ) );
		body.applyLinearImpulse( new Vector2( 0.0f, JUMP_IMPLUSE ),
				body.getWorldCenter( ) );
	}

	/**
	 * Sets the current screw
	 * @author dennis
	 */
	public void hitScrew( Screw screw ) {
		if ( playerState != PlayerState.Screwing ) {
			hitScrew = true;
			currentScrew = screw;
		}
	}

	/**
	 * Sets the current screw to null
	 * @author dennis
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
	 * @author dennis
	 */
	private void attachToScrew( ) {
		if ( currentScrew.body.getJointList( ).size( ) > 0 ) {
			FixtureDef fix = new FixtureDef( );
			// move player to another category so other objects stop colliding 
			fix.filter.categoryBits = Util.CATEGORY_SUBPLAYER;      
			// player still collides with sensor of screw
			fix.filter.maskBits = 0x0008;          
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setFilterData( fix.filter );
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
				body.applyLinearImpulse( new Vector2( 0.005f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity > 0.1f )
				body.applyLinearImpulse( new Vector2( -0.005f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity >= -0.1 && velocity <= 0.1f && velocity != 0.0f )
				body.setLinearVelocity( 0.0f, 0.0f );
		}
	}
	
	/**
	 * @author Bryan Pacini
	 * @return void
	 * slows player
	 */
	private void slow( ) {
		float velocity = body.getLinearVelocity( ).x;
		if ( velocity != 0.0f ) {
			if ( velocity < -0.1f )
				body.applyLinearImpulse( new Vector2( 0.001f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity > 0.1f )
				body.applyLinearImpulse( new Vector2( -0.001f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity >= -0.1 && velocity <= 0.1f && velocity != 0.0f )
				body.setLinearVelocity( 0.0f, 0.0f );
		}
	}

	/**
	 * Updates information about the player every step
	 */
	public void update( float deltaTime ) {
		super.update( deltaTime );
		//inputHandler.update( );
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
			if(grounded) stop();
			else slow( );
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
				FixtureDef fix = new FixtureDef( );
				fix.filter.categoryBits = Util.CATEGORY_PLAYER; 
				fix.filter.maskBits = -1;
				for ( Fixture f : body.getFixtureList( ) ) {
					f.setFilterData( fix.filter );
				}
			}
		}

		if ( playerState == PlayerState.JumpingOffScrew ) {
			if ( body.getLinearVelocity( ).y < 0 ) {
				FixtureDef fix = new FixtureDef( );
				fix.filter.categoryBits = Util.CATEGORY_PLAYER; 
				fix.filter.maskBits = -1;
				for ( Fixture f : body.getFixtureList( ) ) {
					f.setFilterData( fix.filter );
				}
			}
		}
		terminalVelocityCheck(6.0f);
	}
	
	/**
	 * Checks player's vertical velocity and resets to be within bounds
	 * 
	 * @param terminal -float
	 * 		whatever you want terminal velocity to be
	 *	
	 * @author Bryan Pacini
	 */
	private void terminalVelocityCheck(float terminal){
		if( body.getLinearVelocity( ).y < -(terminal))
			body.setLinearVelocity( body.getLinearVelocity().x, - (terminal) );
		else if (body.getLinearVelocity().y > terminal)
			body.setLinearVelocity( body.getLinearVelocity().x, terminal );
	}

}
