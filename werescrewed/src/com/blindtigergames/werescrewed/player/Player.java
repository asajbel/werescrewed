package com.blindtigergames.werescrewed.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;
import com.blindtigergames.werescrewed.platforms.Platform;
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

	public Fixture feet;
	public Fixture torso;
	int check = 0;

	private int prevKey;
	private PovDirection prevButton;
	private PlayerInputHandler inputHandler;
	private MyControllerListener controllerListener;
	private PlayerState playerState;
	private Controller controller;
	private boolean controllerIsActive, controllerDebug;
	private float axisX;

	private Screw currentScrew;
	private Player otherPlayer;
	private RevoluteJoint playerToScrew;
	private RevoluteJoint playerToPlayer;
	private boolean isDead = false, deadDebug;
	private boolean hitScrew;
	private int screwJumpTimeout = 0;
	private boolean grounded;
	private boolean jumpPressedKeyboard;
	private boolean jumpPressedController;
	private int anchorID;

	// Static constants
	public final static float MAX_VELOCITY = 1.8f;
	public final static float MIN_VELOCITY = 0.05f;
	public final static float MOVEMENT_IMPLUSE = 0.01f;
	public final static float JUMP_IMPLUSE = 0.15f;
	public final static float ANALOG_DEADZONE = 0.2f;
	public final static float ANALOG_MAX_RANGE = 1.0f;
	public final static float PLAYER_FRICTION = 0.6f;

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
		Standing, Jumping, Falling, Screwing, JumpingOffScrew, Dead, GrabMode, HeadStand
	}

	// CONSTRUCTORS

	/**
	 * 
	 * @param world
	 *            in which the player exists
	 * @param postion of the player in the world
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
		inputHandler = new PlayerInputHandler( this.name );
		anchorID = AnchorList.getInstance( ).addAnchor( true, pos );

		torso = body.getFixtureList( ).get( 0 );
		feet = body.getFixtureList( ).get( 1 );

		maxFriction( );

		setUpController( );
	}

	// METHODS

	/**
	 * Updates information about the player every step
	 */
	public void update( float deltaTime ) {
		super.update( deltaTime );

		AnchorList.getInstance( ).setAnchorPosBox( anchorID, getPosition( ) );
		if ( isDead ) {
			// TODO: do stuff here
			// playerState = playerState.Dead;
			body.setLinearVelocity( Vector2.Zero );
			body.setFixedRotation( false );
			body.setAngularVelocity( 0.1f );

		} else {
			body.setFixedRotation( true );
			body.setTransform( body.getPosition( ).x, body.getPosition( ).y, 0 );
			updateKeyboard( deltaTime );
			if ( controller != null ) {
				if ( controllerIsActive ) {
					updateController( deltaTime );

				}
			} else {
				// Look to see if controller was inserted
			}
		}

		// Hit backspace to kill the player or respawn him
		if ( Gdx.input.isKeyPressed( Keys.BACKSPACE ) ) {
			if ( deadDebug ) {
				isDead = !isDead;
			}
			deadDebug = false;
		} else
			deadDebug = true;

		// Hit Enter to active the controller
		if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			if ( controllerDebug )
				controllerIsActive = !controllerIsActive;
			controllerDebug = false;
		} else
			controllerDebug = true;
	}

	/**
	 * This function updates the keyboard state which the player checks to do
	 * stuff
	 * 
	 * @param deltaTime
	 */
	private void updateKeyboard( float deltaTime ) {
		inputHandler.update( );

		if ( playerState != PlayerState.Screwing
				&& playerState != PlayerState.JumpingOffScrew
				&& playerState != PlayerState.Standing && isGrounded( ) ) {
			playerState = PlayerState.Standing;
		}

		if ( inputHandler.jumpPressed( ) ) {
			if ( !jumpPressedKeyboard ) {
				if ( playerState == PlayerState.Screwing ) {
					world.destroyJoint( playerToScrew );
					playerState = PlayerState.JumpingOffScrew;
					screwJumpTimeout = 7;
					jump( );
				} else if ( isGrounded( ) ) {
					if( playerToPlayer != null ) {
						world.destroyJoint( playerToPlayer );
						playerToPlayer = null;
					}
					playerState = PlayerState.Jumping;
					jump( );
				} else if( playerToPlayer != null ) {
					world.destroyJoint( playerToPlayer );
					playerToPlayer = null;
				}
				jumpPressedKeyboard = true;
			}
		}
		if ( !inputHandler.jumpPressed( ) ) {
			jumpPressedKeyboard = false;
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
				screwJumpTimeout = 7;
			}
		}

		if ( ( !inputHandler.leftPressed( ) && !inputHandler.rightPressed( ) )
				&& ( prevKey == Keys.D || prevKey == Keys.A ) ) {
			if ( !grounded )
				slow( );
		}

		if ( inputHandler.screwPressed( )
				&& playerState != PlayerState.Screwing
				&& ( playerState != PlayerState.JumpingOffScrew || screwJumpTimeout < 2 ) ) {
			if ( hitScrew ) {
				attachToScrew( );
			} else if ( otherPlayer != null ) {
				setHeadStand( );
			}
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
				screwJumpTimeout = 7;
				jump( );
			}
		}

		if ( playerState == PlayerState.JumpingOffScrew ) {
			if ( screwJumpTimeout == 0 ) {
				Filter filter = new Filter( );
				for ( Fixture f : body.getFixtureList( ) ) {
					filter = f.getFilterData( );
					// move player back to original category
					filter.categoryBits = Util.CATEGORY_PLAYER;
					// player now collides with everything
					filter.maskBits = Util.CATEGORY_EVERYTHING;
					f.setFilterData( filter );
				}
				playerState = PlayerState.Jumping;
			} else if ( screwJumpTimeout == 7 ) {
				boolean platformInWay = false;
				for ( JointEdge j : currentScrew.body.getJointList( ) ) {
					if ( j.joint.getBodyB( ).getUserData( ) instanceof Platform ) {
						platformInWay = true;
					}
				}
				if ( platformInWay ) {
					if ( inputHandler.downPressed( ) ) {
						screwJumpTimeout = 2;
					} 
				} else {
					Filter filter = new Filter( );
					for ( Fixture f : body.getFixtureList( ) ) {
						filter = f.getFilterData( );
						// move player back to original category
						filter.categoryBits = Util.CATEGORY_PLAYER;
						// player now collides with everything
						filter.maskBits = Util.CATEGORY_EVERYTHING;
						f.setFilterData( filter );
					}
				}
				screwJumpTimeout--;
			} else if ( !hitScrew ) {
				screwJumpTimeout--;
			}
		}
		terminalVelocityCheck( 6.0f );
		// the jump doesn't work the first time on dynamic bodies so do it twice
		if ( playerState == PlayerState.Jumping && isGrounded( ) ) {
			jump( );
		}
	}

	/**
	 * This function sets player in dead state
	 */
	public void killPlayer( ) {
		isDead = true;
	}

	/**
	 * This function sets player in alive state
	 */
	public void respawnPlayer( ) {
		isDead = false;
	}

	/**
	 * This function returns whether the player is dead
	 * 
	 * @return boolean
	 */
	public boolean isPlayerDead( ) {
		return isDead;
	}

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
	 * Moves the player right, based off how far analog stick is pushed right
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogRight( ) {
		axisX = controllerListener.analogAxisX( );
		float temp = ( ( ( axisX - ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				+ MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x < temp ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPLUSE, 0.0f ),
					body.getWorldCenter( ) );
		}

	}

	/**
	 * Moves the player left, based off how far analog stick is pushed left
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogLeft( ) {
		axisX = controllerListener.analogAxisX( );
		float temp = ( ( ( axisX + ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				- MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x > temp ) {
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
		if ( playerState != PlayerState.JumpingOffScrew ) {
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.CATEGORY_PLAYER;
				// player now collides with everything
				filter.maskBits = ~Util.CATEGORY_PLAYER;
				f.setFilterData( filter );
			}
		}
	}

	/**
	 * Sets the current screw
	 * 
	 * @author dennis
	 */
	public void hitScrew( Screw screw ) {
		if ( playerState != PlayerState.Screwing ) {
			if ( screw != null ) {
				hitScrew = true;
			} else {
				hitScrew = false;
			}
			currentScrew = screw;
		}
	}

	/**
	 * Sets the other player if in grab mode
	 * 
	 * @param otherPlayer
	 *            the other player
	 */
	public void hitPlayer( Player otherPlayer ) {
		if ( playerState != PlayerState.Screwing ) {
			this.otherPlayer = otherPlayer;
		}
	}

	/**
	 * sets the players state to head stand mode for double jumping
	 */
	public void setHandStandState( ) {
		playerState = PlayerState.HeadStand;
	}

	/**
	 * joints one players feet to the others head which is the position of the
	 * players before they attempt double jumping
	 * 
	 * @author dennis
	 */
	public void setHeadStand( ) {
		if ( otherPlayer.body.getPosition( ).y > body.getPosition( ).y ) {
			otherPlayer.setPosition( body.getPosition( ).x,
					body.getPosition( ).y + ( sprite.getHeight( ) / 2.0f )
							* Util.PIXEL_TO_BOX );
			// connect the players together with a joint
			RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
			revoluteJointDef.initialize( otherPlayer.body, body,
					new Vector2( body.getPosition( ).x, body.getPosition( ).y
							- ( sprite.getHeight( ) ) * Util.PIXEL_TO_BOX ) );
			revoluteJointDef.enableMotor = false;
			playerToPlayer = ( RevoluteJoint ) world
					.createJoint( revoluteJointDef );
			playerState = PlayerState.HeadStand;
			otherPlayer.setHandStandState( );
			// set the mass of the two players less so they can double jump
		} else {
			this.setPosition(
					otherPlayer.body.getPosition( ).x,
					otherPlayer.body.getPosition( ).y
							+ ( otherPlayer.sprite.getHeight( ) / 2.0f )
							* Util.PIXEL_TO_BOX );
			// connect the players together with a joint
			RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
			revoluteJointDef.initialize(
					body,
					otherPlayer.body,
					new Vector2( otherPlayer.body.getPosition( ).x,
							otherPlayer.body.getPosition( ).y
									- ( sprite.getHeight( ) )
									* Util.PIXEL_TO_BOX ) );
			revoluteJointDef.enableMotor = false;
			playerToPlayer = ( RevoluteJoint ) world
					.createJoint( revoluteJointDef );
			playerState = PlayerState.HeadStand;
			otherPlayer.setHandStandState( );
		}
	}

	/**
	 * Sets whether or not the player is grounded
	 * 
	 * @param grounded
	 */
	public void setGrounded( boolean newVal ) {
		this.grounded = newVal;
		if ( newVal && playerState != PlayerState.Screwing ) {
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.CATEGORY_PLAYER;
				// player now collides with everything
				filter.maskBits = Util.CATEGORY_EVERYTHING;
				f.setFilterData( filter );
			}
		}
	}

	public void maxFriction( ) {
		feet.setFriction( PLAYER_FRICTION );
	}

	public void noFriction( ) {
		feet.setFriction( 0.0f );
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
	 * 
	 * @author dennis
	 */
	private void attachToScrew( ) {
		if ( currentScrew.body.getJointList( ).size( ) > 0 ) {
			boolean screwOccupied = false;
			for ( JointEdge j : currentScrew.body.getJointList( ) ) {
				if ( j.joint.getBodyA( ).getUserData( ) instanceof Player ) {
					screwOccupied = true;
				}
			}
			if ( !screwOccupied ) {
				Filter filter;
				for ( Fixture f : body.getFixtureList( ) ) {
					filter = f.getFilterData( );
					// move player to another category so other objects stop
					// colliding
					filter.categoryBits = Util.CATEGORY_SUBPLAYER;
					// player still collides with sensor of screw
					filter.maskBits = Util.CATEGORY_SCREWS;
					f.setFilterData( filter );
				}
				body.setTransform( new Vector2( currentScrew.getPosition( ).x
						- ( sprite.getWidth( ) / 4.0f ) * Util.PIXEL_TO_BOX,
						currentScrew.getPosition( ).y
								- ( sprite.getHeight( ) / 4.0f )
								* Util.PIXEL_TO_BOX ), 0.0f );
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
	}

	/**
	 * Stops the player
	 */

	/**
	 * @author Bryan Pacini
	 * @return void slows player
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
				body.setLinearVelocity( 0.0f, body.getLinearVelocity( ).y );
		}
	}

	/**
	 * Checks player's vertical velocity and resets to be within bounds
	 * 
	 * @param terminal
	 *            -float whatever you want terminal velocity to be
	 * 
	 * @author Bryan Pacini
	 */
	private void terminalVelocityCheck( float terminal ) {
		if ( body.getLinearVelocity( ).y < -( terminal ) )
			body.setLinearVelocity( body.getLinearVelocity( ).x, -( terminal ) );
		else if ( body.getLinearVelocity( ).y > terminal )
			body.setLinearVelocity( body.getLinearVelocity( ).x, terminal );
	}

	/**
	 * This function creates a new controllerListener and sets the active
	 * controller depending on how many players is being created
	 * 
	 * @author Ranveer
	 */
	private void setUpController( ) {
		controllerListener = new MyControllerListener( );
		for ( Controller controller2 : Controllers.getControllers( ) ) {
			Gdx.app.log( "ok", controller2.getName( ) );
		}
		if ( Controllers.getControllers( ).size >= 1 ) {
			if ( this.name.equals( "player1" ) ) {
				controller = Controllers.getControllers( ).get( 0 );
				controller.addListener( controllerListener );
			}
		}
		if ( Controllers.getControllers( ).size == 2 ) {
			if ( this.name.equals( "player2" ) ) {
				controller = Controllers.getControllers( ).get( 1 );
				controller.addListener( controllerListener );
			}
		}

	}

	/**
	 * This function updates the player based off the Controller's state
	 * 
	 * @param deltaTime
	 * @author Ranveer
	 */
	private void updateController( float deltaTime ) {
		if ( playerState != PlayerState.Screwing
				&& playerState != PlayerState.JumpingOffScrew
				&& playerState != PlayerState.Standing && isGrounded( ) ) {
			playerState = PlayerState.Standing;
		}
		if ( controllerListener.jumpPressed( ) ) {
			if ( !jumpPressedController ) {
				if ( playerState == PlayerState.Screwing ) {
					world.destroyJoint( playerToScrew );
					playerState = PlayerState.JumpingOffScrew;
					screwJumpTimeout = 7;
					jump( );
				} else if ( isGrounded( ) ) {
					if( playerToPlayer != null ) {
						world.destroyJoint( playerToPlayer );
						playerToPlayer = null;
					}
					playerState = PlayerState.Jumping;
					jump( );
				} else if( playerToPlayer != null ) {
					world.destroyJoint( playerToPlayer );
					playerToPlayer = null;
				}
				jumpPressedController = true;
			}
		}
		if ( !controllerListener.jumpPressed( ) ) {
			jumpPressedController = false;
		}
		if ( controllerListener.leftPressed( ) ) {
			if ( controllerListener.analogUsed( ) )
				moveAnalogLeft( );
			else
				moveLeft( );
			prevButton = PovDirection.west;
		}

		if ( controllerListener.rightPressed( ) ) {
			if ( controllerListener.analogUsed( ) )
				moveAnalogRight( );
			else
				moveRight( );
			prevButton = PovDirection.east;
		}
		if ( controllerListener.downPressed( ) ) {
			stop( );			
			if ( playerState == PlayerState.Screwing ) {
				world.destroyJoint( playerToScrew );
				playerState = PlayerState.JumpingOffScrew;
				screwJumpTimeout = 1;
			}
		}

		if ( ( !controllerListener.leftPressed( ) && !controllerListener
				.rightPressed( ) )
				&& ( prevButton == PovDirection.east || prevButton == PovDirection.west ) ) {
			if ( !grounded )
				slow( );
		}

		// If player hits the screw button and is in distance
		// then attach the player to the screw
		if ( controllerListener.screwPressed( )
				&& playerState != PlayerState.Screwing
				&& ( playerState != PlayerState.JumpingOffScrew || screwJumpTimeout < 2 ) ) {
			if ( hitScrew ) {
				attachToScrew( );
			} else if ( otherPlayer != null ) {
				setHeadStand( );
			}
		}
		// If the button is let go, then the player is dropped
		// Basically you have to hold attach button to stick to screw
		if ( !controllerListener.screwPressed( )
				&& playerState == PlayerState.Screwing ) {
			world.destroyJoint( playerToScrew );
			playerState = PlayerState.JumpingOffScrew;
			screwJumpTimeout = 7;
		}
		if ( playerState == PlayerState.Screwing ) {
			if ( controllerListener.unscrewing( ) ) {
				currentScrew.screwLeft( );
			} else if ( controllerListener.screwing( ) ) {
				currentScrew.screwRight( );
			}
			if ( currentScrew.body.getJointList( ).size( ) <= 1 ) {
				world.destroyJoint( playerToScrew );
				playerState = PlayerState.JumpingOffScrew;
				screwJumpTimeout = 7;
				jump( );
			}
		}

		if ( playerState == PlayerState.JumpingOffScrew ) {
			if ( screwJumpTimeout == 0 && !hitScrew ) {
				Filter filter = new Filter( );
				for ( Fixture f : body.getFixtureList( ) ) {
					filter = f.getFilterData( );
					// move player back to original category
					filter.categoryBits = Util.CATEGORY_PLAYER;
					// player now collides with everything
					filter.maskBits = Util.CATEGORY_EVERYTHING;
					f.setFilterData( filter );
				}
				playerState = PlayerState.Standing;
			} else if ( screwJumpTimeout == 7 ) {
				boolean platformInWay = false;
				for ( JointEdge j : currentScrew.body.getJointList( ) ) {
					if ( j.joint.getBodyB( ).getUserData( ) instanceof Platform ) {
						platformInWay = true;
					}
				}
				if ( platformInWay ) {
					if ( inputHandler.downPressed( ) ) {
						screwJumpTimeout = 2;
					} 
				} else {
					Filter filter = new Filter( );
					for ( Fixture f : body.getFixtureList( ) ) {
						filter = f.getFilterData( );
						// move player back to original category
						filter.categoryBits = Util.CATEGORY_PLAYER;
						// player now collides with everything
						filter.maskBits = Util.CATEGORY_EVERYTHING;
						f.setFilterData( filter );
					}
				}
				screwJumpTimeout--;
			} else if ( !hitScrew ) {
				screwJumpTimeout--;
			}
		}
		terminalVelocityCheck( 6.0f );
		// the jump doesn't work the first time on dynamic bodies so do it twice
		if ( playerState == PlayerState.Jumping && isGrounded( ) ) {
			jump( );
		}
	}
	private void stop( ) {
		if ( feet.getFriction( ) == 0 ) {
			float velocity = body.getLinearVelocity( ).x;
			if ( velocity != 0.0f ) {
				if ( velocity < -0.1f )
					body.applyLinearImpulse( new Vector2( 0.005f, 0.0f ),
							body.getWorldCenter( ) );
				else if ( velocity > 0.1f )
					body.applyLinearImpulse( new Vector2( -0.005f, 0.0f ),
							body.getWorldCenter( ) );
				else if ( velocity >= -0.1 && velocity <= 0.1f
						&& velocity != 0.0f )
					body.setLinearVelocity( 0.0f, body.getLinearVelocity( ).y );
			}
		}
	}
}
