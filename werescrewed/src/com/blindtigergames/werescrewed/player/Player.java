package com.blindtigergames.werescrewed.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.Screw.ScrewType;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * A representation of a player in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Player extends Entity {

	public final static float MAX_VELOCITY = 1.65f;
	public final static float MIN_VELOCITY = 0.01f;
	public final static float MOVEMENT_IMPULSE = 0.009f;
	public final static float JUMP_SCREW_IMPULSE = 0.1f;
	public final static int JUMP_COUNTER = 10;
	public final static float ANALOG_DEADZONE = 0.2f;
	public final static float ANALOG_MAX_RANGE = 1.0f;
	public final static float PLAYER_FRICTION = 0.6f;
	public final static int SCREW_JUMP_STEPS = 12;
	public final static float SCREW_ATTACH_SPEED = 0.07f;
	public final static int GRAB_COUNTER_STEPS = 5;
	public float JUMP_IMPULSE = 0.13f;

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
	private float leftAnalogX;
	private float leftAnalogY;
	// private float rightAnalogX;
	// private float rightAnalogY;

	private Screw currentScrew;
	private Player otherPlayer;
	private RevoluteJoint playerToScrew;
	private RevoluteJoint playerToPlayer;
	private Body platformBody;
	private boolean topPlayer = false;
	private boolean isDead = false, deadDebug;
	private boolean hitScrew;
	private int screwJumpTimeout = 0;
	private boolean grounded;
	private boolean jumpPressedKeyboard;
	private boolean jumpPressedController;
	private boolean screwButtonHeld;
	private int anchorID;

	public int grabCounter = 0;
	public int jumpCounter = 0;

	private Sound jumpSound;

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
	 * @param postion
	 *            of the player in the world
	 * @param name
	 */
	public Player( String name, World world, Vector2 pos ) {
		super( name, EntityDef.getDefinition( name ), world, pos, 0.0f,
				new Vector2( 1f, 1f ), null, true );
		body.setGravityScale( 0.25f );
		body.setFixedRotation( true );
		body.setSleepingAllowed( false );
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
		controllerDebug = true;

		jumpSound = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/jump.ogg" );
	}

	// PUBLIC METHODS

	/**
	 * Updates information about the player every step
	 */
	public void update( float deltaTime ) {
		super.update( deltaTime );
		// this.otherPlayer = otherPlayer;
		if ( this.name.equals( "player2" ) ) {
			// Gdx.app.log( "player2", "" + playerState );
			// Gdx.app.log( "player2:" , "" + isGrounded( ) );
		}
		if ( this.name.equals( "player1" ) ) {
			// Gdx.app.log( "player1", "" + playerState );
			// Gdx.app.log( "player1:" , "" + isGrounded( ) );
			// Gdx.app.log( "player1 y velocity", "" +body.getLinearVelocity(
			// ).y );
			// System.out.println( controllerListener.getLeftAnalogAngle( ) );
		}
		AnchorList.getInstance( ).setAnchorPosBox( anchorID, getPosition( ) );
		if ( isDead ) {
			body.setLinearVelocity( Vector2.Zero );
			body.setFixedRotation( false );
			body.setAngularVelocity( 0.1f );
		} else {
			body.setFixedRotation( true );
			body.setTransform( body.getPosition( ).x, body.getPosition( ).y, 0 );
			if ( controller != null ) {
				updateController( deltaTime );
			} else {
				updateKeyboard( deltaTime );
			}
		}

		// debug stuff
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

		if ( playerState == PlayerState.JumpingOffScrew ) {
			resetJumpOffScrew( );
		}
		if ( playerState == PlayerState.JumpingOffScrew ) {
			handleJumpOffScrew( );
		}
		if ( body.getLinearVelocity( ).y < -MIN_VELOCITY * 6f
				&& playerState != PlayerState.Screwing
				&& playerState != PlayerState.JumpingOffScrew
				&& playerState != PlayerState.HeadStand ) {
			playerState = PlayerState.Falling;
		}
		if ( playerState == PlayerState.Screwing ) {
			if ( mover != null ) {
				LerpMover lm = ( LerpMover ) mover;
				if ( !lm.atEnd( ) ) {
					lm.move( deltaTime, body );
				} else {
					body.setTransform(
							new Vector2( currentScrew.getPosition( ).x
									- ( sprite.getWidth( ) / 4.0f )
									* Util.PIXEL_TO_BOX, currentScrew
									.getPosition( ).y
									- ( sprite.getHeight( ) / 4.0f )
									* Util.PIXEL_TO_BOX ), 0.0f );
					RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
					revoluteJointDef.initialize( body, currentScrew.body,
							currentScrew.getPosition( ) );
					revoluteJointDef.enableMotor = false;
					playerToScrew = ( RevoluteJoint ) world
							.createJoint( revoluteJointDef );
					playerState = PlayerState.Screwing;
					mover = null;
				}
			}
		}
		terminalVelocityCheck( 15.0f );
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
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player left, or jumps them off of a screw to the left
	 */
	public void moveLeft( ) {
		if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
			body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player right, based off how far analog stick is pushed right
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogRight( ) {
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX - ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				+ MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x < temp ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player left, based off how far analog stick is pushed left
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogLeft( ) {
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX + ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				- MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x > temp ) {
			body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE, 0.0f ),
					body.getWorldCenter( ) );
		}
	}
	/**
	 * Moves the player right, based off how far analog stick is pushed right
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogRightInAir( ) {
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX - ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				+ MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x < temp ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE/2, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player left, based off how far analog stick is pushed left
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogLeftInAir( ) {
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX + ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				- MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x > temp ) {
			body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE/2, 0.0f ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * Causes the player to jump
	 */
	public void jump( ) {
		//Regardless of how the player jumps, we shouldn't consider them grounded anymore.
		setGrounded(false);
		// if the player isn't in head stand mode or if the player
		// is the top player then jump normally
		if ( playerState != PlayerState.HeadStand || topPlayer ) {
			body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x,
					0.0f ) );
			body.applyLinearImpulse( new Vector2( 0.0f, JUMP_IMPULSE ),
					body.getWorldCenter( ) );
		} else {
			// if in head stand mode and this is the bottom player then jump
			// with twice as much force
			// body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x,
			// 0.0f ) );
			body.applyLinearImpulse( new Vector2( 0.0f, JUMP_IMPULSE * 2f ),
					body.getWorldCenter( ) );
		}
		setGrounded( false );
	}

	/**
	 * directs jump impulse off of screw based on analog stick
	 */
	public void jumpScrew( ) {
		leftAnalogX = controllerListener.analogLeftAxisX( );
		leftAnalogY = controllerListener.analogLeftAxisY( ) * -1;
		float multiplierY = 1.5f;
		if ( leftAnalogY < 0.0 )
			multiplierY = 0.1f;
		// body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x,
		// 0.0f ) );
		body.applyLinearImpulse( new Vector2( JUMP_SCREW_IMPULSE * leftAnalogX
				* 0.7f, JUMP_SCREW_IMPULSE * leftAnalogY * multiplierY ),
				body.getWorldCenter( ) );
		setGrounded(false);
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
	 * return s the current state of the player
	 * 
	 * @return playerState
	 */
	public PlayerState getState( ) {
		return playerState;
	}

	/**
	 * returns true if this is the top player in the head stand
	 * 
	 * @return topPlayer
	 */
	public boolean isTopPlayer( ) {
		return topPlayer;
	}

	/**
	 * Sets whether or not the player is grounded
	 * 
	 * @param grounded
	 */
	public void setGrounded( boolean newVal ) {
		if ( !topPlayer ) {
			this.grounded = newVal;
		}
		// if ( newVal && playerState != PlayerState.Screwing ) {
		Filter filter = new Filter( );
		for ( Fixture f : body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			// move player back to original category
			filter.categoryBits = Util.CATEGORY_PLAYER;
			// player now collides with everything
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}
		// }
	}

	/**
	 * sets the players friction to the max limit
	 */
	public void maxFriction( ) {
		feet.setFriction( PLAYER_FRICTION );
	}

	/**
	 * sets the friction to zero, ie. no friction
	 */
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
	 * Transforms player position by offset
	 * 
	 * @param posOffset is the offset you want to apply to player
	 */
	public void setPlatformTransform( Vector2 posOffset ){
		body.setTransform( body.getPosition( ).add(posOffset), 0);
	}

	// PRIVATE METHODS

	/**
	 * Attaches a player to the current screw
	 * 
	 * @author dennis
	 */
	private void attachToScrew( ) {
		if ( currentScrew.body.getJointList( ).size( ) > 0
				&& playerState != PlayerState.HeadStand ) {
			boolean screwOccupied = false;
			for ( JointEdge j : currentScrew.body.getJointList( ) ) {
				// Altered if statement so both players can be on BossScrew.
				if ( j.joint.getBodyA( ).getUserData( ) instanceof Player
						&& currentScrew.getScrewType( ) != ScrewType.BOSS ) {
					screwOccupied = true;
				}
			}
			if ( !screwOccupied ) {
				Filter filter;
				for ( Fixture f : body.getFixtureList( ) ) {
					f.setSensor( true );
					filter = f.getFilterData( );
					// move player to another category so other objects stop
					// colliding
					filter.categoryBits = Util.CATEGORY_SUBPLAYER;
					// player still collides with sensor of screw
					filter.maskBits = Util.CATEGORY_SCREWS;
					f.setFilterData( filter );
				}
				mover = new LerpMover( body.getPosition( ).mul(
						Util.BOX_TO_PIXEL ), new Vector2(
						currentScrew.getPosition( ).x * Util.BOX_TO_PIXEL
								- ( sprite.getWidth( ) / 4.0f ),
						currentScrew.getPosition( ).y * Util.BOX_TO_PIXEL
								- ( sprite.getHeight( ) / 4.0f ) ),
						SCREW_ATTACH_SPEED, false,
						PuzzleType.OVERRIDE_ENTITY_MOVER, LinearAxis.DIAGONAL );
				playerState = PlayerState.Screwing;
				setGrounded(true);
			}
		}
	}

	/**
	 * set jumping off screw give the player a while to stop colliding with the
	 * current platform
	 * 
	 */
	private void jumpOffScrew( ) {
		if ( screwJumpTimeout == 0 ) {
			Filter filter = new Filter( );
			if ( platformBody != null ) {
				// set the bits of the platform back to everything
				for ( Fixture f : platformBody.getFixtureList( ) ) {
					filter = f.getFilterData( );
					// move platform back to original category
					if ( platformBody.getType( ) == BodyType.DynamicBody ) {
						filter.categoryBits = Util.DYNAMIC_OBJECTS;
					} else {
						filter.categoryBits = Util.KINEMATIC_OBJECTS;
					}
					// platform now collides with everything
					filter.maskBits = Util.CATEGORY_EVERYTHING;
					f.setFilterData( filter );
				}
			}
			// set the bits of the player back to everything
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setSensor( false );
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.CATEGORY_PLAYER;
				// player now collides with everything
				filter.maskBits = Util.CATEGORY_EVERYTHING;
				f.setFilterData( filter );
			}
			platformBody = null;
		} else if ( screwJumpTimeout == SCREW_JUMP_STEPS ) {
			// switch the player to not collide with the current platformBody
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.CATEGORY_PLAYER;
				// player now collides with everything except the platform in
				// the way
				filter.maskBits = ~Util.CATEGORY_SUBPLATFORM;
				f.setFilterData( filter );
			}
		}
	}

	/**
	 * jump logic for every time the jump button is pushed before applying an
	 * actual jump
	 */
	private void processJumpState( ) {
		if ( playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				world.destroyJoint( playerToScrew );
			}
			mover = null;
			playerState = PlayerState.JumpingOffScrew;
			screwJumpTimeout = SCREW_JUMP_STEPS;
			jump( );
		} else if ( isGrounded( )
				|| ( otherPlayer != null && playerState != PlayerState.HeadStand ) ) {
			if ( playerState != PlayerState.HeadStand ) {
				if ( playerState != PlayerState.GrabMode ) {
					playerState = PlayerState.Jumping;
				}
				jump( );
				jumpSound.play( );
			} else {
				// let the bottom player jump
				// with a large amount of force
				jump( );
			}
		} else if ( topPlayer ) {
			// jump first to make sure top player
			// only jumps with a small force
			jump( );
			// check if this player has the joint
			removePlayerToPlayer( );
			if ( otherPlayer != null ) {
				otherPlayer.hitPlayer( null );
			}
			hitPlayer( null );
			playerState = PlayerState.Jumping;
		}
	}

	/**
	 * jump logic for every time the jump button is pushed before applying an
	 * actual jump for the controller
	 */
	private void processJumpStateController( ) {
		if ( playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				world.destroyJoint( playerToScrew );
			}
			playerState = PlayerState.JumpingOffScrew;
			screwJumpTimeout = SCREW_JUMP_STEPS;
			jumpPressedController = true;
			mover = null;
			jumpScrew( );
		} else if ( !jumpPressedController ) {
			if ( !topPlayer ) {
				if ( playerState != PlayerState.JumpingOffScrew ) {
					playerState = PlayerState.Jumping;
				}
				jump( );
				jumpCounter++;
				if ( jumpCounter > JUMP_COUNTER ) {
					jumpCounter = 0;
					jumpPressedController = true;
				}
			} else if ( topPlayer ) {
				// jump first to make sure top player
				// only jumps with a small force
				jump( );
				jumpCounter++;
				if ( jumpCounter > JUMP_COUNTER ) {
					jumpCounter = 0;
					jumpPressedController = true;
				}
				// check if this player has the joint
				removePlayerToPlayer( );
				if ( otherPlayer != null ) {
					otherPlayer.hitPlayer( null );
				}
				hitPlayer( null );
				playerState = PlayerState.Jumping;
			}
		}
	}

	/**
	 * check to see if its ok to reset the state from the jumping off screw
	 * state
	 */
	private void resetJumpOffScrew( ) {
		// if state is jumping off screw
		// and the player is either not hitting a screw
		// or the player is grounded then reset the state
		if ( isGrounded( ) && screwJumpTimeout == 0 ) {
			playerState = PlayerState.Standing;
			screwJumpTimeout = 0;
			jumpOffScrew( );
		} else if ( currentScrew == null ) {
			playerState = PlayerState.Jumping;
			screwJumpTimeout = 0;
			jumpOffScrew( );
		}
	}

	/**
	 * handle the Jumping off screw state and update the player accordingly
	 */
	private void handleJumpOffScrew( ) {
		if ( screwJumpTimeout == 0 ) {
			jumpOffScrew( );
		} else if ( screwJumpTimeout == SCREW_JUMP_STEPS ) {
			boolean platformInWay = false;
			for ( JointEdge j : currentScrew.body.getJointList( ) ) {
				if ( j.joint.getBodyB( ).getUserData( ) instanceof Platform ) {
					platformInWay = true;
					platformBody = j.joint.getBodyB( );
					Filter filter = new Filter( );
					for ( Fixture f : platformBody.getFixtureList( ) ) {
						filter = f.getFilterData( );
						// move platform to its own single category
						// it should be the only thing in this category
						filter.categoryBits = Util.CATEGORY_SUBPLATFORM;
						// set to collide with everything
						filter.maskBits = ~Util.CATEGORY_PLAYER;
						f.setFilterData( filter );
					}
				}
			}
			if ( platformInWay ) {
				jumpOffScrew( );
			} else {
				screwJumpTimeout = 0;
				jumpOffScrew( );
			}
			if ( screwJumpTimeout > 0 ) {
				screwJumpTimeout--;
			}
		} else {
			screwJumpTimeout--;
		}
	}

	/**
	 * applies the screwing functionality after the player's input
	 */
	private void handleScrewing( boolean controller ) {
		// loosen and tighten screws and jump when the screw joint is gone
		if ( controller ) {
			if ( controllerListener.unscrewing( ) ) {
				currentScrew.screwLeft( );
			} else if ( controllerListener.screwing( ) ) {
				currentScrew.screwRight( );
			}
		} else {
			if ( inputHandler.unscrewing( ) ) {
				currentScrew.screwLeft( );
			} else if ( inputHandler.screwing( ) ) {
				currentScrew.screwRight( );
			}
		}
		if ( mover == null
				&& currentScrew.body.getJointList( ).size( ) <= 1
				|| ( currentScrew.getScrewType( ) == ScrewType.BOSS && currentScrew
						.getDepth( ) == 0 ) ) {
			if ( mover == null ) {
				world.destroyJoint( playerToScrew );
			}
			playerState = PlayerState.JumpingOffScrew;
			screwJumpTimeout = SCREW_JUMP_STEPS;
			jump( );
		}
	}

	/**
	 * applied before walking, running, jumping, left or right
	 */
	private void processMovingState( ) {
		// if moved left/right during head stands remove the joint
		// and reference to the other player and reset the playerstate
		if ( playerState == PlayerState.HeadStand ) {
			if ( otherPlayer != null ) {
				if ( !topPlayer ) {
					otherPlayer.removePlayerToPlayer( );
				} else {
					removePlayerToPlayer( );
				}
				otherPlayer.hitPlayer( null );
			} else {
				removePlayerToPlayer( );
			}
			hitPlayer( null );
			playerState = PlayerState.Standing;
		}
	}

	/**
	 * applied before applying a down movement
	 */
	private void processMovementDown( ) {
		if ( playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				world.destroyJoint( playerToScrew );
			}
			mover = null;
			playerState = PlayerState.JumpingOffScrew;
			screwJumpTimeout = SCREW_JUMP_STEPS;
		} else {
			processMovingState( );
		}
	}

	/**
	 * joints the top players feet to the bottom players head which is the
	 * position of the players are in before they attempt double jumping
	 * 
	 * @author dennis
	 */
	private void setHeadStand( ) {
		if ( otherPlayer != null ) {
			if ( topPlayer ) {
				playerState = PlayerState.HeadStand;
				this.setPosition( otherPlayer.body.getPosition( ).x,
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
			} else {
				playerState = PlayerState.HeadStand;
			}
		}
	}

	/**
	 * 
	 * check head stand status and resets states as necessary
	 */
	private void checkHeadStandState( ) {
		// if still in head stand state but have no joint attached
		// then reset the state
		if ( playerState == PlayerState.HeadStand
				&& body.getJointList( ).size( ) == 0 ) {
			if ( isGrounded( ) ) {
				if ( otherPlayer != null ) {
					otherPlayer.hitPlayer( null );
				}
				hitPlayer( null );
				playerState = PlayerState.Standing;
			} else {
				if ( otherPlayer != null ) {
					otherPlayer.hitPlayer( null );
				}
				hitPlayer( null );
				playerState = PlayerState.Jumping;
			}
		}
	}

	/**
	 * handles what happens when player releases the grab button
	 */
	private void processReleaseGrab( ) {
		if ( otherPlayer != null ) {
			otherPlayer.setGrounded( false );
			otherPlayer.body.setLinearVelocity( new Vector2( otherPlayer.body
					.getLinearVelocity( ).x, 0.0f ) );
			otherPlayer.body.applyLinearImpulse( new Vector2( 0.0f,
					otherPlayer.JUMP_IMPULSE * 1.5f ), otherPlayer.body
					.getWorldCenter( ) );
		}
		playerState = PlayerState.Standing;
	}

	/**
	 * handles what happens when player pressed the grab button
	 */
	private void processGrabPressed( ) {
		if ( otherPlayer != null
				&& otherPlayer.getState( ) == PlayerState.Standing
				&& !isGrounded( ) ) {
			// if this player is jumping or falling and the other player is
			// standing
			topPlayer = true;
			setHeadStand( );
			otherPlayer.setHeadStand( );
		} else if ( playerState == PlayerState.Standing ) {
			playerState = PlayerState.GrabMode;
		}
	}

	/**
	 * removes the player to player joint used for double jumping
	 */
	private void removePlayerToPlayer( ) {
		if ( topPlayer ) {
			if ( playerToPlayer != null ) {
				world.destroyJoint( playerToPlayer );
			}
			playerToPlayer = null;
			topPlayer = false;
		}
	}

	/**
	 * @author Bryan
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
	 * @author Bryan
	 */
	private void terminalVelocityCheck( float terminal ) {
		if ( playerState != PlayerState.HeadStand ) {
			if ( body.getLinearVelocity( ).y < -( terminal ) ) {
				body.setLinearVelocity( body.getLinearVelocity( ).x,
						-( terminal ) );
			} else if ( body.getLinearVelocity( ).y > terminal ) {
				body.setLinearVelocity( body.getLinearVelocity( ).x, terminal );
			}
		}
	}

	

	/**
	 * reseting jumpcounter and screw button being held and jump state and the
	 * grab button
	 */
	private void resetScrewJumpGrab( ) {
		if ( isGrounded( ) ) {
			jumpCounter = 0;
		}
		if ( !controllerListener.screwPressed( ) ) {
			screwButtonHeld = false;
		}
		if ( !controllerListener.isGrabPressed( ) ) {
			grabCounter++;
		}
		if ( !controllerListener.jumpPressed( ) ) {
			if ( isGrounded( ) || otherPlayer != null ) {
				jumpPressedController = false;
			} else if ( playerState == PlayerState.Screwing ) {
				jumpPressedController = false;
			} else {
				jumpPressedController = true;
			}
		}
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
				&& playerState != PlayerState.GrabMode
				&& playerState != PlayerState.HeadStand && isGrounded( ) ) {
			playerState = PlayerState.Standing;
		}
		checkHeadStandState( );
		if ( inputHandler.jumpPressed( ) ) {
			if ( !jumpPressedKeyboard ) {
				processJumpState( );
				jumpPressedKeyboard = true;
			}
		}
		if ( !inputHandler.jumpPressed( ) ) {
			jumpPressedKeyboard = false;
		}
		if ( inputHandler.leftPressed( ) ) {
			processMovingState( );
			moveLeft( );
			prevKey = Keys.A;
		}
		if ( inputHandler.rightPressed( ) ) {
			processMovingState( );
			moveRight( );
			prevKey = Keys.D;
		}
		if ( inputHandler.downPressed( ) ) {
			processMovementDown( );
		}
		if ( ( !inputHandler.leftPressed( ) && !inputHandler.rightPressed( ) )
				&& ( prevKey == Keys.D || prevKey == Keys.A ) ) {
			if ( !grounded ) {
				slow( );
			}
		}
		// grab another player, if your colliding, - for double jump
		// functionality
		if ( inputHandler.isGrabPressed( )
				&& playerState != PlayerState.Screwing
				&& playerState != PlayerState.HeadStand ) {
			processGrabPressed( );
		}
		if ( playerState == PlayerState.GrabMode
				&& !inputHandler.isGrabPressed( ) ) {
			processReleaseGrab( );
		}
		// attach to screws when attach button is pushed
		if ( inputHandler.screwPressed( )
				&& playerState != PlayerState.Screwing
				&& ( playerState != PlayerState.JumpingOffScrew ) ) {
			if ( hitScrew ) {
				attachToScrew( );
			}
		}
		// loosen tight screws and jump if screw joint is gone
		if ( playerState == PlayerState.Screwing ) {
			handleScrewing( controller != null );
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
				&& playerState != PlayerState.HeadStand && isGrounded( ) ) {
			// This code exists because you need to release the grab button
			// to toss the other player, while colliding with the other player
			if ( grabCounter > GRAB_COUNTER_STEPS ) {
				grabCounter = 0;
				playerState = PlayerState.Standing;
			}
		}
		checkHeadStandState( );
		if ( controllerListener.jumpPressed( ) ) {
			processJumpStateController( );
		}
		resetScrewJumpGrab( );
		if ( controllerListener.leftPressed( ) ) {
			processMovingState( );
			if ( controllerListener.analogUsed( ) ) {
				if(playerState == PlayerState.Falling || playerState == PlayerState.Jumping){
					moveAnalogLeftInAir();
				}
				else{
					moveAnalogLeft( );
				}
			} else {
				moveLeft( );
			}
			prevButton = PovDirection.west;
		}
		if ( controllerListener.rightPressed( ) ) {
			processMovingState( );
			if ( controllerListener.analogUsed( ) ) {
				if(playerState == PlayerState.Falling || playerState == PlayerState.Jumping){
					moveAnalogRightInAir();
				}
				else{
					moveAnalogRight( );
				}
			} else {
				moveRight( );
			}
			prevButton = PovDirection.east;
		}/*
		 * if(!controllerListener.rightPressed( ) &&
		 * !controllerListener.leftPressed( )){ if (grounded){
		 * body.applyLinearImpulse( new Vector2(0, -24), body.getWorldCenter( )
		 * ); } }
		 */
		if ( controllerListener.downPressed( ) ) {
			// processMovementDown( );
			stop( );
		}
		if ( ( !controllerListener.leftPressed( ) && !controllerListener
				.rightPressed( ) )
				&& ( prevButton == PovDirection.east || prevButton == PovDirection.west ) ) {
			if ( !grounded ) {
				slow( );
			}
		}
		// grab another player, if your colliding
		// with another player, for double jump
		if ( controllerListener.isGrabPressed( )
				&& playerState != PlayerState.Screwing
				&& playerState != PlayerState.HeadStand ) {
			processGrabPressed( );
		}
		if ( playerState == PlayerState.GrabMode
				&& !controllerListener.isGrabPressed( ) ) {
			processReleaseGrab( );
		}
		// If player hits the screw button and is in distance
		// then attach the player to the screw
		if ( ( controllerListener.screwPressed( ) )
				&& ( playerState != PlayerState.Screwing && playerState != PlayerState.JumpingOffScrew ) ) {
			if ( hitScrew && !screwButtonHeld ) {
				attachToScrew( );
				screwButtonHeld = true;
				jumpCounter = 0;
			}
		}
		// If the button is let go, then the player is dropped
		// Basically you have to hold attach button to stick to screw
		if ( !controllerListener.screwPressed( )
				&& playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				world.destroyJoint( playerToScrew );
			}
			mover = null;
			playerState = PlayerState.JumpingOffScrew;
			screwJumpTimeout = SCREW_JUMP_STEPS;
		}
		// loosen and tighten screws and jump when the screw joint is gone
		if ( playerState == PlayerState.Screwing ) {
			handleScrewing( true );
		}
	}

	/**
	 * Stops the player
	 */
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
			screwButtonHeld = false;
		}
	}
	
	/**
	 * This function creates a new controllerListener and sets the active
	 * controller depending on how many players is being created
	 * 
	 * @author Ranveer
	 */
	private void setUpController( ) {
		for ( Controller controller2 : Controllers.getControllers( ) ) {
			Gdx.app.log( "ok", controller2.getName( ) );
		}
		if ( Controllers.getControllers( ).size >= 1 ) {
			if ( this.name.equals( "player1" ) ) {
				controllerListener = new MyControllerListener( );
				controller = Controllers.getControllers( ).get( 0 );
				controller.addListener( controllerListener );
				JUMP_IMPULSE = 0.08f;
			}
		}
		if ( Controllers.getControllers( ).size == 2 ) {
			if ( this.name.equals( "player2" ) ) {
				controllerListener = new MyControllerListener( );
				controller = Controllers.getControllers( ).get( 1 );
				controller.addListener( controllerListener );
				JUMP_IMPULSE = 0.08f;
			}
		}

	}
}
