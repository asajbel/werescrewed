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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;
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
	public final static float JUMP_SCREW_IMPULSE = 0.12f;
	public final static float JUMP_CONTROL_MUTIPLIER = 0.5f;
	public final static int JUMP_COUNTER = 10;
	public final static float ANALOG_DEADZONE = 0.2f;
	public final static float ANALOG_MAX_RANGE = 1.0f;
	public final static float PLAYER_FRICTION = 0.6f;
	public final static int SCREW_JUMP_STEPS = 20;
	public final static int HEAD_JUMP_STEPS = 30;
	public final static float SCREW_ATTACH_SPEED = 0.1f;
	public final static int GRAB_COUNTER_STEPS = 5;
	public final static Vector2 ANCHOR_BUFFER_SIZE = new Vector2( 400f, 256f );
	public final static float STEAM_FORCE = .5f;
	public float JUMP_IMPULSE = 0.09f;
	public float directionJumpDivsion = 2.0f;

	public Fixture feet;
	public Fixture torso;
	int check = 0;

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
	private int headStandTimeout = 0;
	private boolean grounded;
	private boolean jumpPressedKeyboard;
	private boolean jumpPressedController;
	private boolean canJumpOffScrew;
	private boolean screwButtonHeld;
	private boolean kinematicTransform = false;
	private boolean changeDirectionsOnce = false;
	private boolean steamCollide = false;
	private float footFriction = PLAYER_FRICTION;

	private IMover mover;

	public int grabCounter = 0;
	public int jumpCounter = 0;

	@SuppressWarnings( "unused" )
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
				new Vector2( 1f, 1f ), null, true, 0.0f );
		entityType = EntityType.PLAYER;
		body.setGravityScale( 0.25f );
		body.setFixedRotation( true );
		body.setSleepingAllowed( false );
		this.world = world;
		body.setUserData( this );
		body.setBullet( true );
		playerState = PlayerState.Standing;
		inputHandler = new PlayerInputHandler( this.name );
		anchor = new Anchor( true, new Vector2( body.getWorldCenter( ).x
				* Util.BOX_TO_PIXEL, body.getWorldCenter( ).y
				* Util.BOX_TO_PIXEL ), new Vector2( ANCHOR_BUFFER_SIZE.x,
				ANCHOR_BUFFER_SIZE.y ), world, 0f );
		anchor.special = true;
		AnchorList.getInstance( ).addAnchor( anchor );

		torso = body.getFixtureList( ).get( 0 );
		feet = body.getFixtureList( ).get( 1 );
		torso.getShape( ).setRadius( 0 );
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
		if ( name.equals( "player1" ) ) {
			// Gdx.app.log( "playerState", "" + playerState + " " + grounded );
			// System.out.println( jumpPressedKeyboard );
			Gdx.app.log( name + " playerState", "" + playerState + " "
					+ grounded + "isDead? = " + isDead );
		}
		if ( name.equals( "player2" ) ) {
			Gdx.app.log( name + " playerState", "" + playerState + " "
					+ grounded + "isDead? = " + isDead );
		}
		if ( kinematicTransform ) {
			// setPlatformTransform( platformOffset );
			kinematicTransform = false;
		}
		if ( isDead ) {
			// TODO: death stuff
			if ( controller != null ) {
				updateController( deltaTime );
				if ( controllerListener.isGrabPressed( ) ) {
					playerState = PlayerState.GrabMode;
				} else {
					playerState = PlayerState.Standing;
				}
			} else {
				updateKeyboard( deltaTime );
				if ( inputHandler.isGrabPressed( ) ) {
					playerState = PlayerState.GrabMode;
				} else {
					playerState = PlayerState.Standing;
				}
			}
		} else {
			if ( controller != null ) {
				updateController( deltaTime );
			} else {
				updateKeyboard( deltaTime );
			}
		}

		// updateFootFriction();

		// debug stuff
		// Hit backspace to kill the player or respawn him
		if ( Gdx.input.isKeyPressed( Keys.BACKSPACE ) ) {
			if ( deadDebug ) {
				isDead = !isDead;
			}
			deadDebug = false;
		} else {
			deadDebug = true;
		}
		// Hit Enter to active the controller
		if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			if ( controllerDebug ) {
				controllerIsActive = !controllerIsActive;
			}
			controllerDebug = false;
		} else {
			controllerDebug = true;
		}
		if ( playerState == PlayerState.JumpingOffScrew ) {
			resetJumpOffScrew( );
		}
		if ( playerState == PlayerState.JumpingOffScrew ) {
			handleJumpOffScrew( );
		}
		if ( body.getLinearVelocity( ).y < -MIN_VELOCITY * 6f
				&& playerState != PlayerState.Screwing
				&& playerState != PlayerState.JumpingOffScrew
				&& playerState != PlayerState.HeadStand && !isDead ) {
			playerState = PlayerState.Falling;
		}
		// after the players collide check if one is falling
		// and one is standing and if a head stand didn't occur step before
		// then put them into head stand state
		if ( otherPlayer != null && playerState == PlayerState.Falling
				&& otherPlayer.getState( ) == PlayerState.Standing
				&& headStandTimeout == 0 && otherPlayer.isHeadStandTimedOut( ) ) {
			topPlayer = true;
			setHeadStand( );
			otherPlayer.setHeadStand( );
		} else {
			if ( headStandTimeout > 0 ) {
				headStandTimeout--;
			}
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
		} else if ( steamCollide ) {
			steamResolution( );
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
		playerState = PlayerState.Standing;
		while ( body.getJointList( ).iterator( ).hasNext( ) ) {
			world.destroyJoint( body.getJointList( ).get( 0 ).joint );
		}
		playerToPlayer = null;
		playerToScrew = null;
		if ( currentScrew != null ) {
			currentScrew.setPlayerAttached( false );
			currentScrew = null;
		}
		Filter filter = new Filter( );
		for ( Fixture f : body.getFixtureList( ) ) {
			f.setSensor( false );
			filter = f.getFilterData( );
			// move player back to original category
			filter.categoryBits = Util.CATEGORY_PLAYER;
			// player now collides with everything
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}
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
	 * Moves the player right, and in the air it halves the amount the player
	 * can jump
	 */
	public void moveRight( ) {
		if ( playerState == PlayerState.Falling
				|| playerState == PlayerState.Jumping ) {
			if ( changeDirectionsOnce && prevButton == PovDirection.west ) {
				directionJumpDivsion *= 2;
				changeDirectionsOnce = false;
			}
			if ( body.getLinearVelocity( ).x < MAX_VELOCITY ) {
				body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE
						/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
			}
		} else {
			if ( body.getLinearVelocity( ).x < MAX_VELOCITY ) {
				body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE, 0.0f ),
						body.getWorldCenter( ) );
			}
		}
	}

	/**
	 * Moves the player left, and in the air it halves the amount the player can
	 * jump
	 * 
	 */
	public void moveLeft( ) {
		if ( playerState == PlayerState.Falling
				|| playerState == PlayerState.Jumping ) {
			if ( changeDirectionsOnce && prevButton == PovDirection.east ) {
				directionJumpDivsion *= 2;
				changeDirectionsOnce = false;
			}
			if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
				body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE
						/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
			}
		} else {
			if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
				body.applyLinearImpulse(
						new Vector2( -MOVEMENT_IMPULSE, 0.0f ),
						body.getWorldCenter( ) );
			}
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
	 * Moves the player right, in the air, movement impulse is lessened
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogRightInAir( ) {

		if ( changeDirectionsOnce && prevButton == PovDirection.west ) {
			directionJumpDivsion *= 2;
			changeDirectionsOnce = false;
		}
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX - ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				+ MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x < temp ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE
					/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
		}
	}

	/**
	 * Moves the player left, in the air, movement impulse is lessened
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogLeftInAir( ) {
		if ( changeDirectionsOnce && prevButton == PovDirection.east ) {
			directionJumpDivsion *= 2;
			changeDirectionsOnce = false;
		}
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX + ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				- MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x > temp ) {
			body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE
					/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
		}
	}

	/**
	 * Causes the player to jump
	 */
	public void jump( ) {
		// Regardless of how the player jumps, we shouldn't consider them
		// grounded anymore.
		setGrounded( false );
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
			body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x,
					0.0f ) );
			body.applyLinearImpulse( new Vector2( 0.0f, JUMP_IMPULSE ),
					body.getWorldCenter( ) );
		}
	}

	/**
	 * directs jump impulse off of screw based on analog stick
	 */
	public void jumpScrew( ) {
		leftAnalogX = controllerListener.analogLeftAxisX( );
		leftAnalogY = controllerListener.analogLeftAxisY( );
		float yImpulse = JUMP_SCREW_IMPULSE;
		if ( leftAnalogY > -0.7f ) {
			if ( leftAnalogY > 0.01f || leftAnalogY < -0.01f ) {
				if ( leftAnalogX > 0.01f || leftAnalogX < -0.01f ) {
					float temp = ( leftAnalogY + 0.7f ) / 1.7f;
					yImpulse -= temp * JUMP_SCREW_IMPULSE;
				}
			}
		}
		float xImpulse = leftAnalogX * JUMP_SCREW_IMPULSE * 0.1f;
		body.applyLinearImpulse( new Vector2( xImpulse, yImpulse ),
				body.getWorldCenter( ) );
		setGrounded( false );
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
	 * sets the body of some body that the player is hitting
	 */
	public void hitSolidObject( Body b ) {
		if ( screwJumpTimeout == 0 ) {
			platformBody = b;
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
	 * returns if head stand is still in timeout mode
	 */
	public boolean isHeadStandTimedOut( ) {
		return headStandTimeout == 0;
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

	/**
	 * sets the players friction to the max limit
	 */
	public void maxFriction( ) {
		feet.setFriction( PLAYER_FRICTION );
		footFriction = PLAYER_FRICTION;
	}

	/**
	 * sets the friction to zero, ie. no friction
	 */
	public void noFriction( ) {
		feet.setFriction( 0.0f );
		footFriction = 0f;
	}

	/**
	 * slowly increases friction to avoid that silly stopping bug. Call this
	 * every player.update()
	 */
	@SuppressWarnings( "unused" )
	private void updateFootFriction( ) {
		if ( isGrounded( ) ) {
			// increase friction while on ground
			if ( footFriction < PLAYER_FRICTION ) {
				footFriction += 0.1f;
				if ( footFriction > PLAYER_FRICTION ) {
					footFriction = PLAYER_FRICTION;
				}
			}
		} else {
			footFriction = 0f;
		}
		Gdx.app.log( name, feet.getFriction( ) + "" );
		// Gdx.app.log( name, feet.getFriction()+"" );
		feet.setFriction( footFriction );
	}

	/**
	 * Checks if the player is grounded
	 * 
	 * @return a boolean representing whether or not the player is "grounded"
	 */
	public boolean isGrounded( ) {
		return grounded;
	}

	// PRIVATE METHODS

	/**
	 * Attaches a player to the current screw
	 * 
	 * @author dennis
	 */
	private void attachToScrew( ) {
		if ( currentScrew != null
				&& currentScrew.body.getJointList( ).size( ) > 0
				&& playerState != PlayerState.HeadStand
				&& !currentScrew.isPlayerAttached( ) ) {
			// Filter filter;
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setSensor( true );
			}
			mover = new LerpMover(
					body.getPosition( ).mul( Util.BOX_TO_PIXEL ), new Vector2(
							currentScrew.getPosition( ).x * Util.BOX_TO_PIXEL
									- ( sprite.getWidth( ) / 4.0f ),
							currentScrew.getPosition( ).y * Util.BOX_TO_PIXEL
									- ( sprite.getHeight( ) / 4.0f ) ),
					SCREW_ATTACH_SPEED, false, LinearAxis.DIAGONAL, 0 );
			playerState = PlayerState.Screwing;
			currentScrew.setPlayerAttached( true );
			setGrounded( false );
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
		} else if ( screwJumpTimeout == SCREW_JUMP_STEPS ) {
			// switch the player to not collide with the current platformBody
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.CATEGORY_SUBPLAYER;
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
			if ( !screwButtonHeld ) {
				if ( mover == null ) {
					world.destroyJoint( playerToScrew );
					for ( Fixture f : body.getFixtureList( ) ) {
						f.setSensor( false );
					}
					mover = null;
					currentScrew.setPlayerAttached( false );
					playerState = PlayerState.JumpingOffScrew;
					screwJumpTimeout = SCREW_JUMP_STEPS;
					jump( );
				}
			}
		} else if ( !jumpPressedKeyboard ) {
			if ( !topPlayer ) {
				if ( playerState != PlayerState.GrabMode
						&& playerState != PlayerState.HeadStand ) {
					playerState = PlayerState.Jumping;
				}
				jump( );
				jumpCounter++;
				if ( jumpCounter > JUMP_COUNTER ) {
					jumpCounter = 0;
					jumpPressedKeyboard = true;
				}
			} else if ( topPlayer ) {
				// jump first to make sure top player
				// only jumps with a small force
				jump( );
				jumpCounter++;
				if ( jumpCounter > JUMP_COUNTER ) {
					jumpCounter = 0;
					jumpPressedKeyboard = true;
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
	 * jump logic for every time the jump button is pushed before applying an
	 * actual jump for the controller
	 */
	private void processJumpStateController( ) {
		if ( playerState == PlayerState.Screwing ) {
			if ( canJumpOffScrew ) {
				if ( mover == null ) {
					world.destroyJoint( playerToScrew );
					for ( Fixture f : body.getFixtureList( ) ) {
						f.setSensor( false );
					}
					playerState = PlayerState.JumpingOffScrew;
					screwJumpTimeout = SCREW_JUMP_STEPS;
					jumpPressedController = true;
					mover = null;
					currentScrew.setPlayerAttached( false );
					jumpScrew( );
				}
			}
		} else if ( !jumpPressedController ) {
			if ( !topPlayer ) {
				if ( playerState != PlayerState.JumpingOffScrew
						&& playerState != PlayerState.HeadStand ) {
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
			jumpOffScrew( );
		} else if ( screwJumpTimeout == 0 ) {
			if ( isGrounded( ) ) {
				playerState = PlayerState.Standing;
			} else if ( body.getLinearVelocity( ).y > 0 ) {
				playerState = PlayerState.Jumping;
			} else {
				playerState = PlayerState.Falling;
			}
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
			if ( platformBody != null ) {
				Filter filter = new Filter( );
				for ( Fixture f : platformBody.getFixtureList( ) ) {
					filter = f.getFilterData( );
					// move platform to its own single category
					// it should be the only thing in this category
					filter.categoryBits = Util.CATEGORY_SUBPLATFORM;
					// set to collide with everything
					filter.maskBits = ~Util.CATEGORY_SUBPLAYER;
					f.setFilterData( filter );
				}
				jumpOffScrew( );
				screwJumpTimeout--;
			} else {
				jumpOffScrew( );
				screwJumpTimeout--;
			}
		} else {
			if ( screwJumpTimeout > 0 ) {
				screwJumpTimeout--;
				jumpOffScrew( );
			} else {
				jumpOffScrew( );
			}
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
			headStandTimeout = HEAD_JUMP_STEPS;
			if ( otherPlayer != null ) {
				if ( !topPlayer ) {
					otherPlayer.removePlayerToPlayer( );
				} else {
					removePlayerToPlayer( );
				}
			} else {
				removePlayerToPlayer( );
			}
		}
	}

	/**
	 * applied before applying a down movement
	 */
	private void processMovementDown( ) {
		if ( playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				world.destroyJoint( playerToScrew );
				for ( Fixture f : body.getFixtureList( ) ) {
					f.setSensor( false );
				}
				mover = null;
				currentScrew.setPlayerAttached( false );
				playerState = PlayerState.JumpingOffScrew;
				screwJumpTimeout = SCREW_JUMP_STEPS;
			}
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
			jumpCounter = 0;
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
				playerState = PlayerState.Standing;
			} else {
				if ( body.getLinearVelocity( ).y > 0 ) {
					playerState = PlayerState.Jumping;
				} else {
					playerState = PlayerState.Falling;
				}
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
			// topPlayer = true;
			// setHeadStand( );
			// otherPlayer.setHeadStand( );
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
			directionJumpDivsion = 2;
			changeDirectionsOnce = true;
			prevButton = null;
		}
		if ( !controllerListener.screwPressed( ) ) {
			screwButtonHeld = false;
		}
		if ( !controllerListener.isGrabPressed( ) ) {
			grabCounter++;
		}
		if ( !controllerListener.jumpPressed( ) ) {
			canJumpOffScrew = true;
			if ( isGrounded( ) || topPlayer ) {
				jumpPressedController = false;
			} else if ( playerState == PlayerState.Screwing ) {
				jumpPressedController = false;
			} else {
				jumpPressedController = true;
			}
		}
	}

	private void resetScrewJumpGrabKeyboard( ) {
		if ( isGrounded( ) ) {
			jumpCounter = 0;
			directionJumpDivsion = 2;
			changeDirectionsOnce = true;
			prevButton = null;
		}
		if ( !inputHandler.jumpPressed( ) ) {
			if ( isGrounded( ) || topPlayer ) {
				jumpPressedKeyboard = false;
			} else if ( playerState == PlayerState.Screwing ) {
				jumpPressedKeyboard = false;
			} else {
				jumpPressedKeyboard = true;
			}
		}
	}

	/**
	 * Transforms player position by offset
	 * 
	 * @param posOffset
	 *            is the offset you want to apply to player
	 */
	@SuppressWarnings( "unused" )
	private void setPlatformTransform( Vector2 posOffset ) {
		Gdx.app.log( name + "old:", " " + body.getPosition( ) );
		body.setTransform( body.getPosition( ).cpy( ).add( posOffset ), 0 );
		Gdx.app.log( name + "new:", " " + body.getPosition( ) );
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
			processJumpState( );
		}
		resetScrewJumpGrabKeyboard( );

		if ( inputHandler.leftPressed( ) ) {
			processMovingState( );
			moveLeft( );
			prevButton = PovDirection.west;
		}
		if ( inputHandler.rightPressed( ) ) {
			processMovingState( );
			moveRight( );
			prevButton = PovDirection.east;
		}
		if ( inputHandler.downPressed( ) ) {
			processMovementDown( );
		}
		if ( ( !inputHandler.leftPressed( ) && !inputHandler.rightPressed( ) )
				&& ( prevButton == PovDirection.east || prevButton == PovDirection.west ) ) {
			if ( !grounded ) {
				slow( );
				// prevButton = null;
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

		if ( !inputHandler.screwPressed( ) ) {
			screwButtonHeld = false;
		}
		// attach to screws when attach button is pushed
		if ( inputHandler.screwPressed( ) ) {
			if ( playerState != PlayerState.Screwing ) {
				if ( playerState != PlayerState.JumpingOffScrew ) {
					if ( hitScrew ) {
						attachToScrew( );
						jumpCounter = 0;
						if ( inputHandler.screwPressed( ) ) {
							screwButtonHeld = true;
						}
					}
				}
			} else {
				if ( !screwButtonHeld ) {
					if ( mover == null ) {
						world.destroyJoint( playerToScrew );
						for ( Fixture f : body.getFixtureList( ) ) {
							f.setSensor( false );
						}
						mover = null;
						currentScrew.setPlayerAttached( false );
						playerState = PlayerState.JumpingOffScrew;
						screwJumpTimeout = SCREW_JUMP_STEPS;
					}
				}
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
				if ( playerState == PlayerState.Falling
						|| playerState == PlayerState.Jumping ) {
					moveAnalogLeftInAir( );
				} else {
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
				if ( playerState == PlayerState.Falling
						|| playerState == PlayerState.Jumping ) {
					moveAnalogRightInAir( );
				} else {
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
			// stop( );
		}
		if ( ( !controllerListener.leftPressed( ) && !controllerListener
				.rightPressed( ) )
				&& ( prevButton == PovDirection.east || prevButton == PovDirection.west ) ) {
			if ( !grounded ) {
				slow( );
				prevButton = null;
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
				if ( controllerListener.jumpPressed( ) ) {
					canJumpOffScrew = false;
				}
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
				for ( Fixture f : body.getFixtureList( ) ) {
					f.setSensor( false );
				}
				mover = null;
				currentScrew.setPlayerAttached( false );
				playerState = PlayerState.JumpingOffScrew;
				screwJumpTimeout = SCREW_JUMP_STEPS;
			}
		}
		// loosen and tighten screws and jump when the screw joint is gone
		if ( playerState == PlayerState.Screwing ) {
			handleScrewing( true );
		}
	}

	/**
	 * Stops the player
	 */
	@SuppressWarnings( "unused" )
	private void stop( ) {
		// if ( feet.getFriction( ) == 0 ) {
		float velocity = body.getLinearVelocity( ).x;
		if ( velocity != 0.0f ) {
			if ( velocity < -0.1f )
				body.applyLinearImpulse( new Vector2( 0.005f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity > 0.1f )
				body.applyLinearImpulse( new Vector2( -0.005f, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity >= -0.1 && velocity <= 0.1f && velocity != 0.0f )
				body.setLinearVelocity( 0.0f, body.getLinearVelocity( ).y );
		}
		// screwButtonHeld = false;
		// }
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
			}
		}
		if ( Controllers.getControllers( ).size == 2 ) {
			if ( this.name.equals( "player2" ) ) {
				controllerListener = new MyControllerListener( );
				controller = Controllers.getControllers( ).get( 1 );
				controller.addListener( controllerListener );
			}
		}

	}

	/**
	 * sets the value of steam collide flag
	 * 
	 * @param value
	 *            boolean
	 */

	public void setSteamCollide( boolean value ) {
		steamCollide = value;
	}

	/**
	 * says whether the player is in steam
	 * 
	 * @return boolean
	 */
	public boolean isSteamCollide( ) {
		return steamCollide;
	}

	/**
	 * applys force to player
	 */
	private void steamResolution( ) {
		body.applyForceToCenter( 0f, STEAM_FORCE );
	}
}
