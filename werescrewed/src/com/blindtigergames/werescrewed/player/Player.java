package com.blindtigergames.werescrewed.player;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.PlayerSpinemator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * A representation of a player in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Player extends Entity {

	public final static float MAX_VELOCITY = 1.60f;
	public final static float MIN_VELOCITY = 0.01f;
	public final static float MOVEMENT_IMPULSE = 0.010f;
	public final static float JUMP_IMPULSE = 0.12f;
	public final static float JUMP_SCREW_IMPULSE = JUMP_IMPULSE * 5 / 4;
	public final static float JUMP_SLOW_SPEED = 0.002f;
	public final static int JUMP_COUNTER = 10;
	public final static float ANALOG_DEADZONE = 0.4f;
	public final static float ANALOG_MAX_RANGE = 1.0f;
	public final static float PLAYER_FRICTION = 0.7f;
	public final static int SCREW_JUMP_STEPS = 15;
	public final static int HEAD_JUMP_STEPS = 30;
	public final static int DEAD_STEPS = 0;
	public final static int RUN_STEPS = 7;
	public final static float SCREW_ATTACH_SPEED = 0.1f;
	public final static int GRAB_COUNTER_STEPS = 5;
	public final static Vector2 ANCHOR_BUFFER_SIZE = new Vector2( 300f, 200f );
	public final static float STEAM_FORCE = .5f;
	public final static float STEAM_IMPULSE = 0.2f;
	public final static float FRICTION_INCREMENT = 0.3f;
	public final static float FEET_OFFSET_X = 59f * Util.PIXEL_TO_BOX;
	public final static float FEET_OFFSET_Y = 23.5f * Util.PIXEL_TO_BOX;
	public final static float JUMP_DIRECTION_MULTIPLIER = 2f;
	public final static float JUMP_DEFAULT_DIVISION = 2.0f;
	public float directionJumpDivsion = JUMP_DEFAULT_DIVISION;
	public boolean flipX = false;
	public final static float HEIGHT = 128;
	public final static float WIDTH = 64;
	//public final static float 

	public Fixture feet;
	public Fixture torso;
	public Fixture rightSensor;
	public Fixture leftSensor;
	public Fixture topSensor;

	boolean rightCrush;
	boolean leftCrush;
	boolean topCrush;
	boolean botCrush;

	int check = 0;

	private PovDirection prevButton;
	public PlayerInputHandler inputHandler;
	private MyControllerListener controllerListener;
	private PlayerState playerState;
	private ConcurrentState extraState;
	private PlayerDirection playerDirection;
	private boolean reachedMaxSpeed;
	private PlayerDirection prevPlayerDir;
	private Controller controller;
	@SuppressWarnings( "unused" )
	private boolean controllerIsActive, controllerDebug;
	private float leftAnalogX;
	@SuppressWarnings( "unused" )
	private float leftAnalogY;
	// private float rightAnalogX;
	// private float rightAnalogY;
	private boolean switchedScrewingDirection;
	private boolean isScrewing;
	private boolean isUnscrewing;
	private boolean resetScrewing;

	private Screw currentScrew;
	private Player otherPlayer;
	private RevoluteJoint playerJoint;
	private Body platformBody;
	private boolean topPlayer = false;
	private boolean isDead = false;
	private boolean hitSolidObject;
	private boolean knockedOff = false;
	private int screwJumpTimeout = 0;
	private int headStandTimeout = 0;
	private int runTimeout = 0;
	private int respawnTimeout = 0;
	private boolean grounded;
	private boolean jumpPressedKeyboard;
	private boolean jumpPressedController;
	private boolean canJumpOffScrew;
	private boolean screwButtonHeld;
	private boolean kinematicTransform = false;
	private boolean changeDirectionsOnce = false;
	private boolean steamCollide = false;
	private boolean steamDone = false;

	private IMover mover;

	public int grabCounter = 0;
	public int jumpCounter = 0;

	// private ParticleEffect land_cloud;

	@SuppressWarnings( "unused" )
	private Sound jumpSound;

	// TODO: fill in the frames counts and frame rates for various animations
	// like below
	@SuppressWarnings( "unused" )
	private int jumpFrames = 3;
	@SuppressWarnings( "unused" )
	private float jumpSpeed = 0.3f;

	public float frictionCounter = 0;

	// Enums
	/**
	 * <p>
	 * <b>Values:</b>
	 * </p>
	 * <Ul>
	 * Standing <br />
	 * Running <br />
	 * Jumping <br />
	 * Falling <br />
	 * Screwing <br />
	 * JumpingOffScrew
	 * </Ul>
	 */
	public enum PlayerState {
		Standing, Running, Jumping, Falling, Screwing, JumpingOffScrew, Dead, GrabMode, HeadStand, Landing, RespawnMode
	}

	public enum ConcurrentState {
		Ignore, ExtraJumping, ExtraFalling
	}

	// enum to handle different states of movement
	public enum PlayerDirection {
		Idle, Left, Right
	}
	
	private String[] injuredParticles = {"injured/baf","injured/extra_crispy","injured/ooph"};
	Random r;

	// CONSTRUCTORS

	/**
	 * 
	 * @param world
	 *            in which the player exists
	 * @param postion
	 *            of the player in the world
	 * @param name
	 */
	public Player( String name, String def, World world, Vector2 pos ) {
		super( name, EntityDef.getDefinition( def ), world, pos, 0.0f,
				new Vector2( 1f, 1f ), null, true );
		entityType = EntityType.PLAYER;
		body.setGravityScale( 0.3f );
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
				ANCHOR_BUFFER_SIZE.y ) );
		anchor.special = true;
		AnchorList.getInstance( ).addAnchor( anchor );

		// build spine animator
		if ( this.type.isAnimatorType( "spine" ) ) {
			spinemator = new PlayerSpinemator( this );
			spinemator.setPosition( body.getWorldCenter( ) );
		}

		setFixtures( );
		maxFriction( );

		BodyDef bodydef = new BodyDef( );
		bodydef.position.set( pos );

		setUpController( );
		controllerDebug = true;

		jumpSound = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/WilhelmScream.ogg" );

		r = new Random();
		addFrontParticleEffect( "land_cloud_new", false, false );
		addFrontParticleEffect( "skid_left", false, false );
		addFrontParticleEffect( "skid_right", false, false );
		//addFrontParticleEffect( "blood", false, false );
		for(String s : injuredParticles ){
			addBehindParticleEffect( s, false, false );
		}
		addBehindParticleEffect( "revive", false, false );
		// land_cloud = ParticleEffect.loadEffect( "land_cloud" );
	}

	// PUBLIC METHODS

	/**
	 * Updates information about the player every step
	 */
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( Gdx.input.isKeyPressed( Keys.G ) )
			Gdx.app.log( "steamCollide: " + steamCollide, "steamDone: "
					+ steamDone );
//		if ( name.equals( "player1" ) ) {
//
//		}
		if ( kinematicTransform ) {
			// setPlatformTransform( platformOffset );
			kinematicTransform = false;
		}
		// if dead do dead stuff
		if ( isDead ) {
			// if player is dead but state is not dead
			// repeat kill player
			// removes all the joints and stuff
			if ( playerState != PlayerState.Dead
					&& playerState != PlayerState.RespawnMode ) {
				killPlayer( );
			}
			// check input only for grab mode
			// to allow player to re-spawn
			if ( controller != null ) {
				if ( controllerListener.isGrabPressed( ) ) {
					playerState = PlayerState.RespawnMode;
				} else {
					playerState = PlayerState.Dead;
				}
			} else {
				inputHandler.update( );
				if ( inputHandler.isGrabPressed( ) ) {
					playerState = PlayerState.RespawnMode;
				} else {
					playerState = PlayerState.Dead;
				}
			}
		} else {
			// else player is not dead update regular input
			if ( controller != null ) {
				updateController( deltaTime );
			} else if ( inputHandler != null ) {
				updateKeyboard( deltaTime );
			}
		}
		// if re-spawning decrement time out
		// player will not die in this time
		if ( respawnTimeout > 0 ) {
			respawnTimeout--;
		}
		// build extra fixture to have new friction
		updateFootFriction( );
		// test if player is still moving after timeout
		if ( playerDirection != PlayerDirection.Idle ) {
			if ( runTimeout == 0 && playerState != PlayerState.Jumping
					&& playerState != PlayerState.Falling
					&& extraState != ConcurrentState.ExtraFalling
					&& extraState != ConcurrentState.ExtraJumping ) {
				playerDirection = PlayerDirection.Idle;
			} else if ( playerDirection == PlayerDirection.Left
					&& type.getScale( ).x > 0 ) {
				flipX = true;
				type.setScale( type.getScale( ).x * -1, type.getScale( ).y );
			} else if ( playerDirection == PlayerDirection.Right
					&& type.getScale( ).x < 0 ) {
				flipX = false;
				type.setScale( type.getScale( ).x * -1, type.getScale( ).y );
			} else if ( playerState != PlayerState.Jumping
					&& playerState != PlayerState.Falling
					&& extraState != ConcurrentState.ExtraFalling
					&& extraState != ConcurrentState.ExtraJumping ) {
				runTimeout--;
			}
		}
		// switch between states
		switch ( playerState ) {
		case Dead:
			break;
		case JumpingOffScrew:
			resetJumpOffScrew( );
			if ( playerState == PlayerState.JumpingOffScrew ) {
				handleJumpOffScrew( );
			}
			break;
		case HeadStand:
			if ( hitSolidObject ) {
				if ( topPlayer ) {
					removePlayerToPlayer( );
				}
			}
			break;
		case Screwing:
			if ( knockedOff ) {
				detachScrewImpulse( );
				removePlayerToScrew( );
				knockedOff = false;
			} else if ( mover != null ) {
				LerpMover lm = ( LerpMover ) mover;
				if ( !lm.atEnd( ) ) {
					lm.move( deltaTime, body );
				} else {
					body.setTransform(
							new Vector2( currentScrew.getPosition( ).x
									- ( WIDTH / 2.0f ) * Util.PIXEL_TO_BOX,
									currentScrew.getPosition( ).y
											- ( HEIGHT / 2.0f )
											* Util.PIXEL_TO_BOX ), 0.0f );
					RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
					revoluteJointDef.initialize( body, currentScrew.body,
							currentScrew.getPosition( ) );
					revoluteJointDef.enableMotor = false;
					playerJoint = ( RevoluteJoint ) world
							.createJoint( revoluteJointDef );
					playerState = PlayerState.Screwing;
					mover = null;
				}
			} else {
				// if resurrect screw and its not active remove the player joint
				if ( currentScrew.getScrewType( ) == ScrewType.SCREW_RESURRECT ) {
					ResurrectScrew rezScrew = ( ResurrectScrew ) currentScrew;
					if ( rezScrew.deleteQueue( ) ) {
						if ( !detachScrewImpulse( ) ) {
							jump( );
						}
						removePlayerToScrew( );
					}
				}
			}
			break;
		default:
			break;
		}
		// if the player is falling
		if ( body.getLinearVelocity( ).y < -MIN_VELOCITY * 4f
				&& playerState != PlayerState.Screwing
				&& playerState != PlayerState.JumpingOffScrew
				&& platformBody == null && !isDead ) {
			switch ( playerState ) {
			case HeadStand:
				// don't set the player state use the extra state
				if ( !topPlayer ) {
					extraState = ConcurrentState.ExtraFalling;
				}
				runTimeout = 0;
				break;
			default:
				playerState = PlayerState.Falling;
				runTimeout = 0;
				break;
			}
			setGrounded( false );
		} else if ( playerState == PlayerState.Falling
				&& !isHeadStandPossible( ) ) {
			// if the player is falling but y velocity is too slow
			// the the player hit something
			playerState = PlayerState.Standing;
			setGrounded( true );
		} else if ( extraState == ConcurrentState.ExtraFalling ) {
			// if the player is falling but y velocity is too slow
			// the the player hit something
			extraState = ConcurrentState.Ignore;
			setGrounded( true );
		}
		// check if the head stand requirements are met
		if ( otherPlayer != null && playerState != PlayerState.HeadStand ) {
			if ( isHeadStandPossible( ) ) {
				setHeadStand( );
				otherPlayer.setHeadStand( );
			} else if ( !otherPlayer.isHeadStandPossible( ) ) {
				otherPlayer.hitPlayer( null );
				hitPlayer( null );
			}
		} else {
			if ( headStandTimeout > 0 ) {
				headStandTimeout--;
			}
		}
		// check for crushing stuff
		if ( ( ( topCrush && botCrush ) || ( leftCrush && rightCrush ) )
				&& ( playerState != PlayerState.JumpingOffScrew && playerState != PlayerState.Screwing ) ) {
			Gdx.app.log( "test state:", " " + playerState );
			this.killPlayer( );
			// Gdx.app.log( "\nright: ", "" + rightCrush );
			// Gdx.app.log( "left: ", "" + leftCrush );
			// Gdx.app.log( "top: ", "" + topCrush );
			// Gdx.app.log( "bottom: ", "" + botCrush );
		} else if ( steamCollide ) {
			if ( !steamDone ) {
				steamResolution( );
				steamDone = true;
			}
		} else
			steamDone = false;
		terminalVelocityCheck( 15.0f );
		// the jump doesn't work the first time on dynamic bodies so do it twice
		if ( playerState == PlayerState.Jumping && isGrounded( ) ) {
			jump( );
		}

		prevPlayerDir = playerDirection;
	}

	/**
	 * This function sets player in dead state
	 */
	public void killPlayer( ) {
		if ( respawnTimeout == 0 ) {
			if ( !world.isLocked( ) ) {
				if ( otherPlayer != null
						&& otherPlayer.getState( ) == PlayerState.HeadStand ) {
					otherPlayer.checkHeadStandState( );
				}
				removePlayerToScrew( );
				removePlayerToPlayer( );
				currentScrew = null;
				platformBody = null;
				mover = null;
				Filter filter = new Filter( );
				for ( Fixture f : body.getFixtureList( ) ) {
					if ( f != rightSensor && f != leftSensor && f != topSensor ) {
						f.setSensor( false );
					}
					filter = f.getFilterData( );
					// move player back to original category
					filter.categoryBits = Util.CATEGORY_PLAYER;
					// player now collides with everything
					filter.maskBits = Util.CATEGORY_SCREWS;
					f.setFilterData( filter );
				}
				playerState = PlayerState.Dead;
				if ( Metrics.activated ) {
					Metrics.addPlayerDeathPosition( this.getPositionPixel( ) );
				}
			} else {
				playerState = PlayerState.Standing;
				platformBody = null;
			}
			isDead = true;
			ParticleEffect blood = getEffect(injuredParticles[r.nextInt( injuredParticles.length )]);
			blood.restartAt( getPositionPixel() );
		}
	}

	/**
	 * This function sets player in alive state
	 */
	public void respawnPlayer( ) {
		topCrush = false;
		botCrush = false;
		leftCrush = false;
		rightCrush = false;
		body.setTransform( body.getPosition( ), 0f );
		Filter filter = new Filter( );
		for ( Fixture f : body.getFixtureList( ) ) {
			if ( f != rightSensor && f != leftSensor && f != topSensor ) {
				f.setSensor( false );
			}
			filter = f.getFilterData( );
			// move player back to original category
			filter.categoryBits = Util.CATEGORY_PLAYER;
			// player now collides with everything
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}
		playerState = PlayerState.Standing;
		platformBody = null;
		isDead = false;
		respawnTimeout = DEAD_STEPS;
		
		getEffect("revive").restartAt( getPositionPixel() );
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
				directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
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
				if ( body.getLinearVelocity( ).x >= MAX_VELOCITY*0.99f )
					reachedMaxSpeed = true;
				else
					reachedMaxSpeed = false;
			}
		}
		playerDirection = PlayerDirection.Right;
		if ( grounded && prevPlayerDir== PlayerDirection.Left ){//&& reachedMaxSpeed ){
			getEffect( "skid_left" ).restartAt( getPositionPixel( ).add( 30,0 ) );
			reachedMaxSpeed = false;
		}
		runTimeout = RUN_STEPS;
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
				directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
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
				if ( body.getLinearVelocity( ).x <= -MAX_VELOCITY*0.99f )
					reachedMaxSpeed = true;
				else
					reachedMaxSpeed = false;
			}
		}
		playerDirection = PlayerDirection.Left;
		if ( grounded && prevPlayerDir == PlayerDirection.Right ){// && reachedMaxSpeed ){
			getEffect( "skid_right" ).restartAt( getPositionPixel( ).add( 100,0 ) );
			reachedMaxSpeed = false;
		}
		runTimeout = RUN_STEPS;
	}

	/**
	 * get players direction
	 */
	public PlayerDirection getMoveState( ) {
		return playerDirection;
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
		playerDirection = PlayerDirection.Right;
		runTimeout = RUN_STEPS;
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
		playerDirection = PlayerDirection.Left;
		runTimeout = RUN_STEPS;
	}

	/**
	 * Moves the player right, in the air, movement impulse is lessened
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogRightInAir( ) {

		if ( changeDirectionsOnce && prevButton == PovDirection.west ) {
			directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
			changeDirectionsOnce = false;
		}
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX - ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				+ MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x < temp ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE
					/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
		}
		playerDirection = PlayerDirection.Right;
	}

	/**
	 * Moves the player left, in the air, movement impulse is lessened
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogLeftInAir( ) {
		if ( changeDirectionsOnce && prevButton == PovDirection.east ) {
			directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
			changeDirectionsOnce = false;
		}
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX + ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				- MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x > temp ) {
			body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE
					/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
		}
		playerDirection = PlayerDirection.Left;
	}

	/**
	 * Causes the player to jump
	 */
	public void jump( ) {

		if ( Metrics.activated
				&& ( grounded || playerState == PlayerState.Screwing ) ) {
			Metrics.addPlayerJumpPosition( this.getPositionPixel( ) );
		}
		// Regardless of how the player jumps, we shouldn't consider them
		// grounded anymore.
		setGrounded( false );
		body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x, 0.0f ) );
		body.applyLinearImpulse( new Vector2( 0.0f, JUMP_IMPULSE ),
				body.getWorldCenter( ) );
	}

	/**
	 * Sets the current screw
	 * 
	 * @author dennis
	 */
	public void hitScrew( Screw screw ) {
		if ( playerState != PlayerState.Screwing ) {
			currentScrew = screw;
		}
	}

	/**
	 * Sets the other player if in grab mode
	 * 
	 * @param otherPlayer
	 *            the other player
	 */
	public void hitPlayer( Player player ) {
		if ( playerState != PlayerState.Screwing || player == null ) {
			this.otherPlayer = player;
		}
	}

	/**
	 * get concurrent player state
	 */
	public ConcurrentState getExtraState( ) {
		return extraState;
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
	 * get the instance of the currentscrew
	 */
	public Screw getCurrentScrew( ) {
		return currentScrew;
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
			if ( newVal != false && !grounded && otherPlayer == null ) {
				/*
				 * hitCloud.setPixelPosition( this.getPositionPixel( ) .sub( 0,
				 * 12f ) ); hitCloud.sprite.setColor( 1, 1, 1,
				 * body.getLinearVelocity( ).y / ( float ) MAX_VELOCITY );
				 * hitCloud.sprite.reset( );
				 */
				// if ( !world.isLocked( ) ) {
				getEffect( "land_cloud_new" ).restartAt(
						getPositionPixel( ).add( 50, 0 ) );
				// }
			}
			this.grounded = newVal;
		}
		if ( screwJumpTimeout == 0 && !world.isLocked( ) ) {
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
	 * slowly increases friction to avoid that silly stopping bug. Call this
	 * every player.update()
	 */
	private void updateFootFriction( ) {

		if ( isGrounded( ) ) {
			if ( feet.getFriction( ) < PLAYER_FRICTION ) {
				// if ( playerState != PlayerState.Screwing && otherPlayer ==
				// null ) {
				// playerState = PlayerState.Landing;
				// }
				frictionCounter += FRICTION_INCREMENT;

				CircleShape ps = new CircleShape( );
				ps.setRadius( feet.getShape( ).getRadius( ) );

				ps.setPosition( ps.getPosition( ).add( FEET_OFFSET_X,
						FEET_OFFSET_Y ) );
				FixtureDef fd = new FixtureDef( );

				fd.shape = ps;
				fd.density = 1f;
				fd.restitution = 0.001f;
				fd.friction = frictionCounter;

				if ( playerState == PlayerState.Screwing ) {
					fd.isSensor = true;
				}

				fd.filter.categoryBits = Util.CATEGORY_PLAYER;
				fd.filter.maskBits = Util.CATEGORY_EVERYTHING;

				body.destroyFixture( feet );

				feet = body.createFixture( fd );

				if ( feet.getFriction( ) > PLAYER_FRICTION ) {
					feet.setFriction( PLAYER_FRICTION );

				}
				// currentScrew = null;
			}
		} else {
			frictionCounter = 0f;
			feet.setFriction( frictionCounter );
		}

	}

	/**
	 * sets the body of some body that the player is hitting
	 */
	public void hitSolidObject( Body b ) {
		if ( platformBody == null && b != null
				&& playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				knockedOff = true;
			} else {
				platformBody = null;
			}
		} else if ( screwJumpTimeout == 0 ) {
			if ( b != null || playerState != PlayerState.Screwing ) {
				platformBody = b;
			}
			if ( playerState == PlayerState.Falling ) {
				playerState = PlayerState.Standing;
			}
		}
		if ( b == null ) {
			knockedOff = false;
			hitSolidObject = false;
		} else {
			hitSolidObject = true;
		}
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
			if ( !currentScrew.playerNotSensor( ) ) {
				for ( Fixture f : body.getFixtureList( ) ) {
					f.setSensor( true );
				}
			}
			if ( currentScrew.body.getLinearVelocity( ).len( ) < SCREW_ATTACH_SPEED ) {
				mover = new LerpMover( body.getPosition( ).mul(
						Util.BOX_TO_PIXEL ), new Vector2(
						currentScrew.getPosition( ).x * Util.BOX_TO_PIXEL
								- ( WIDTH / 2.0f ),
						currentScrew.getPosition( ).y * Util.BOX_TO_PIXEL
								- ( HEIGHT / 2.0f ) ), SCREW_ATTACH_SPEED,
						false, LinearAxis.DIAGONAL, 0 );
			} else {
				body.setTransform(
						new Vector2( currentScrew.getPosition( ).x
								- ( WIDTH / 2.0f ) * Util.PIXEL_TO_BOX,
								currentScrew.getPosition( ).y
										- ( HEIGHT / 2.0f ) * Util.PIXEL_TO_BOX ),
						0.0f );
				RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
				revoluteJointDef.initialize( body, currentScrew.body,
						currentScrew.getPosition( ) );
				revoluteJointDef.enableMotor = false;
				playerJoint = ( RevoluteJoint ) world
						.createJoint( revoluteJointDef );
				mover = null;
			}
			playerState = PlayerState.Screwing;
			currentScrew.setPlayerAttached( true );
			platformBody = null;
			if ( currentScrew.getScrewType( ) == ScrewType.SCREW_STRUCTURAL ) {
				for ( JointEdge je : currentScrew.body.getJointList( ) ) {
					// if this body is a platform but not a skeleton save the
					// instance
					if ( je.joint.getBodyA( ).getUserData( ) instanceof Entity ) {
						Entity p = ( Entity ) je.joint.getBodyA( )
								.getUserData( );
						if ( p.getEntityType( ) == EntityType.PLATFORM ) {
							platformBody = je.joint.getBodyA( );
						}
					}
					// if this body is a platform but not a skeleton save the
					// instance
					if ( je.joint.getBodyB( ).getUserData( ) instanceof Entity ) {
						Entity p = ( Entity ) je.joint.getBodyB( )
								.getUserData( );
						if ( p.getEntityType( ) == EntityType.PLATFORM ) {
							platformBody = je.joint.getBodyB( );
						}
					}
				}
			}
			setGrounded( false );
			if ( Metrics.activated ) {
				Metrics.addPlayerAttachToScrewPosition( this.getPositionPixel( ) );
			}
		}
	}

	/**
	 * applies the current screws directional impulse
	 */
	private boolean detachScrewImpulse( ) {
		if ( platformBody != null && currentScrew.getDepth( ) >= 0
				&& currentScrew.getDetachDirection( ) != null
				&& currentScrew.getDetachDirection( ).len( ) != 0f ) {
			body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x,
					0.0f ) );
			Vector2 temp = currentScrew.getDetachDirection( ).cpy( );
			body.applyLinearImpulse( temp.mul( JUMP_IMPULSE ),
					body.getWorldCenter( ) );
			return true;
		}
		return false;
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
				if ( f != rightSensor && f != leftSensor && f != topSensor ) {
					f.setSensor( false );
				}
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
					filter.categoryBits = Util.CATEGORY_PLATFORMS;
					// platform now collides with everything
					filter.maskBits = Util.CATEGORY_EVERYTHING;
					f.setFilterData( filter );
				}
			}
		}
	}

	/**
	 * jump logic for every time the jump button is pushed before applying an
	 * actual jump
	 */
	private void processJumpState( ) {
		if ( playerState == PlayerState.Screwing ) {
			if ( canJumpOffScrew ) {
				if ( mover == null ) {
					// jumpPressedKeyboard = true;
					if ( !detachScrewImpulse( ) ) {
						jump( );
					}
					if ( currentScrew.getDepth( ) >= 0 ) {
						removePlayerToScrew( );
					}
					if ( Metrics.activated ) {
						Metrics.addPlayerJumpPosition( this.getPositionPixel( ) );
						Metrics.addToUnscrewListOnce = false;
					}
				}
			}
		} else if ( !jumpPressedKeyboard ) {
			if ( !topPlayer ) {
				if ( playerState != PlayerState.JumpingOffScrew
						&& playerState != PlayerState.HeadStand ) {
					playerState = PlayerState.Jumping;
				} else if ( playerState == PlayerState.HeadStand ) {
					extraState = ConcurrentState.ExtraJumping;
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
				playerState = PlayerState.Jumping;
				// check if this player has the joint
				removePlayerToPlayer( );
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
					// jumpPressedController = true;
					if ( !detachScrewImpulse( ) ) {
						jump( );
					}
					if ( currentScrew.getDepth( ) >= 0 ) {
						removePlayerToScrew( );
					}
					if ( Metrics.activated ) {
						Metrics.addPlayerJumpPosition( this.getPositionPixel( ) );
						Metrics.addToUnscrewListOnce = false;
					}
				}
			}
		} else if ( !jumpPressedController ) {
			if ( !topPlayer ) {
				if ( playerState != PlayerState.JumpingOffScrew
						&& playerState != PlayerState.HeadStand ) {
					playerState = PlayerState.Jumping;
				} else if ( playerState == PlayerState.HeadStand ) {
					// don't change the actual player state use the extra state
					extraState = ConcurrentState.ExtraJumping;
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
		// and the player is grounded and done with
		// the screw jump
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
			if ( controllerListener.unscrewing( ) && currentMover( ) == null ) {
				if ( resetScrewing ) {
					resetScrewing = false;
				} else if ( isScrewing ) {
					switchedScrewingDirection = true;
					isUnscrewing = true;
					isScrewing = false;
				} else {
					isUnscrewing = true;
					switchedScrewingDirection = false;
				}
				currentScrew.screwLeft( controllerListener.getCurrRegion( ),
						switchedScrewingDirection );
				if ( Metrics.activated ) {
					if ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRIPPED ) {
						if ( currentScrew.getDepth( ) == 0 ) {
							if ( !Metrics.addToUnscrewListOnce ) {
								Metrics.addPlayerUnscrewedScrewPosition( this
										.getPositionPixel( ) );
								Metrics.addToUnscrewListOnce = true;
							}
						} else {
							Metrics.addToUnscrewListOnce = false;
						}
					}
				}
			} else if ( controllerListener.screwing( )
					&& currentMover( ) == null ) {
				if ( resetScrewing ) {
					resetScrewing = false;
				} else if ( isUnscrewing ) {
					switchedScrewingDirection = true;
					isScrewing = true;
					isUnscrewing = false;
				} else {
					isScrewing = true;
					switchedScrewingDirection = false;
				}
				currentScrew.screwRight( controllerListener.getCurrRegion( ),
						switchedScrewingDirection );
				if ( Metrics.activated ) {
					if ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRIPPED ) {
						if ( currentScrew.getDepth( ) == currentScrew
								.getMaxDepth( ) ) {
							if ( !Metrics.addToUnscrewListOnce ) {
								Metrics.addPlayerScrewedScrewPosition( this
										.getPositionPixel( ) );
								Metrics.addToUnscrewListOnce = true;
							}
						} else {
							Metrics.addToUnscrewListOnce = false;
						}
					}
				}
			}
		} else {
			if ( inputHandler.unscrewing( ) && currentMover( ) == null ) {
				currentScrew.screwLeft( );
				if ( Metrics.activated ) {
					if ( currentScrew.getScrewType( ).toString( ) != ScrewType.SCREW_STRIPPED
							.toString( ) ) {
						if ( currentScrew.getDepth( ) == 0 ) {
							if ( !Metrics.addToUnscrewListOnce ) {
								Metrics.addPlayerUnscrewedScrewPosition( this
										.getPositionPixel( ) );
								Metrics.addToUnscrewListOnce = true;
							}
						} else {
							Metrics.addToUnscrewListOnce = false;
						}

					}
				}

			} else if ( inputHandler.screwing( ) && currentMover( ) == null ) {
				currentScrew.screwRight( );

				if ( Metrics.activated ) {
					if ( currentScrew.getScrewType( ).toString( ) != ScrewType.SCREW_STRIPPED
							.toString( ) ) {
						if ( currentScrew.getDepth( ) == currentScrew
								.getMaxDepth( ) ) {
							if ( !Metrics.addToUnscrewListOnce ) {
								Metrics.addPlayerScrewedScrewPosition( this
										.getPositionPixel( ) );
								Metrics.addToUnscrewListOnce = true;
							}
						} else {
							Metrics.addToUnscrewListOnce = false;
						}
					}
				}
			}
		}

		if ( mover == null
				&& currentScrew.body.getJointList( ).size( ) <= 1
				|| ( currentScrew.getScrewType( ) == ScrewType.SCREW_BOSS && currentScrew
						.getDepth( ) == 0 ) ) {
			// JUMP COMMENTED OUT BECAUSE IT ADDS JUMP TO METRICS WHEN
			// PLAYER DOESN'T ACTUALLY HIT THE JUMP BUTTON
			// jump( );

			body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x,
					0.0f ) );
			body.applyLinearImpulse( new Vector2( 0.0f, JUMP_IMPULSE ),
					body.getWorldCenter( ) );

			removePlayerToScrew( );
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
					Gdx.app.log( "process moving state",
							"other player is not null" );
				} else {
					removePlayerToPlayer( );
				}
			} else {
				Gdx.app.log( "process moving state",
						"other player is null for some reason" );
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
				detachScrewImpulse( );
				if ( currentScrew.getDepth( ) >= 0 ) {
					removePlayerToScrew( );
				}
			}
		} else {
			processMovingState( );
		}
	}

	/**
	 * checks if a head stand is possible
	 */
	public boolean isHeadStandPossible( ) {
		if ( otherPlayer != null
				&& otherPlayer.getPositionPixel( )
						.sub( this.getPositionPixel( ) ).len( ) < 150f ) {
			if ( playerState == PlayerState.Falling
					&& otherPlayer.getState( ) == PlayerState.Standing
					&& !otherPlayer.isPlayerDead( ) && headStandTimeout == 0
					&& otherPlayer.isHeadStandTimedOut( )
					&& platformBody == null ) {
				// check if the top player is in-line with the other players
				// head
				// and check if the top player is actually above the other
				// player
				if ( ( this.getPositionPixel( ).y > otherPlayer
						.getPositionPixel( ).add( 0, HEIGHT / 2f ).y )
						&& ( otherPlayer.getPositionPixel( ).sub( WIDTH / 3.0f,
								0.0f ).x <= this.getPositionPixel( ).x )
						&& ( otherPlayer.getPositionPixel( ).add( WIDTH / 4.0f,
								0.0f ).x > this.getPositionPixel( ).x ) ) {
					boolean isMoving = false;
					// check if the player is using input
					// to move either left or right
					if ( controller != null ) {
						if ( controllerListener.leftPressed( )
								|| controllerListener.rightPressed( ) ) {
							isMoving = true;
						}
					} else {
						if ( inputHandler.leftPressed( )
								|| inputHandler.rightPressed( ) ) {
							isMoving = true;
						}
					}
					if ( !isMoving ) {
						topPlayer = true;
						return true;
					}
				}
			}
		}
		return false;

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
						otherPlayer.body.getPosition( ).y + ( HEIGHT - 8f )
								* Util.PIXEL_TO_BOX );
				// connect the players together with a joint
				RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
				revoluteJointDef.initialize( body, otherPlayer.body,
						new Vector2( otherPlayer.body.getPosition( ).x,
								otherPlayer.body.getPosition( ).y - ( HEIGHT )
										* Util.PIXEL_TO_BOX ) );
				revoluteJointDef.enableMotor = false;
				playerJoint = ( RevoluteJoint ) world
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
				extraState = ConcurrentState.Ignore;
			} else {
				if ( body.getLinearVelocity( ).y > 0 ) {
					playerState = PlayerState.Jumping;
					extraState = ConcurrentState.Ignore;
				} else {
					playerState = PlayerState.Falling;
					extraState = ConcurrentState.Ignore;
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
					JUMP_IMPULSE * 1.5f ), otherPlayer.body.getWorldCenter( ) );
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
			if ( playerJoint != null ) {
				world.destroyJoint( playerJoint );
			}
			playerJoint = null;
			topPlayer = false;
		} else if ( otherPlayer != null ) {
			otherPlayer.removePlayerToPlayer( );
		}
		otherPlayer = null;
	}

	/**
	 * removes the player to screw joint
	 */
	public void removePlayerToScrew( ) {
		if ( playerJoint != null ) {
			world.destroyJoint( playerJoint );
			playerJoint = null;
		}
		for ( Fixture f : body.getFixtureList( ) ) {
			if ( f != rightSensor && f != leftSensor && f != topSensor ) {
				f.setSensor( false );
			}
		}
		Filter filter = new Filter( );
		if ( platformBody != null ) {
			// set the bits of the platform back to everything
			for ( Fixture f : platformBody.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move platform to sub category
				filter.categoryBits = Util.CATEGORY_SUBPLATFORM;
				// platform only doesn't collide with player
				filter.maskBits = ~Util.CATEGORY_SUBPLAYER;
				f.setFilterData( filter );
			}
			for ( Fixture f : body.getFixtureList( ) ) {
				if ( f != rightSensor && f != leftSensor && f != topSensor ) {
					f.setSensor( false );
				}
				filter = f.getFilterData( );
				// move player to sub category
				filter.categoryBits = Util.CATEGORY_SUBPLAYER;
				// player now collides with everything except the platform in
				// the way
				filter.maskBits = ~Util.CATEGORY_SUBPLATFORM;
				f.setFilterData( filter );
			}
		} else {
			for ( Fixture f : body.getFixtureList( ) ) {
				if ( f != rightSensor && f != leftSensor && f != topSensor ) {
					f.setSensor( false );
				}
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.CATEGORY_PLAYER;
				// player now collides with everything
				filter.maskBits = Util.CATEGORY_EVERYTHING;
				f.setFilterData( filter );
			}
		}
		mover = null;
		if ( currentScrew != null ) {
			currentScrew.setPlayerAttached( false );
		}
		currentScrew = null;
		playerState = PlayerState.JumpingOffScrew;
		screwJumpTimeout = SCREW_JUMP_STEPS;
	}

	/**
	 * @author Bryan
	 * @return void slows player
	 */
	private void slow( ) {
		float velocity = body.getLinearVelocity( ).x;
		if ( velocity != 0.0f ) {
			if ( velocity < -0.1f )
				body.applyLinearImpulse( new Vector2( JUMP_SLOW_SPEED, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity > 0.1f )
				body.applyLinearImpulse( new Vector2( -JUMP_SLOW_SPEED, 0.0f ),
						body.getWorldCenter( ) );
			else if ( velocity >= -0.1 && velocity <= 0.1f && velocity != 0.0f )
				body.setLinearVelocity( 0.0f, body.getLinearVelocity( ).y );
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
	 * reseting jump counter and screw button being held and jump state and the
	 * grab button
	 */
	private void resetScrewJumpGrab( ) {
		if ( ( controllerListener.analogRightAxisX( ) < 0.7 && controllerListener
				.analogRightAxisY( ) < 0.7 )
				&& ( controllerListener.analogRightAxisX( ) > -0.7 && controllerListener
						.analogRightAxisY( ) > -0.7 ) ) {
			switchedScrewingDirection = true;
			resetScrewing = true;
		}
		if ( isGrounded( ) ) {
			jumpCounter = 0;
			directionJumpDivsion = JUMP_DEFAULT_DIVISION;
			changeDirectionsOnce = true;
			prevButton = null;
			// switchedScrewingDirection = false;
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
			directionJumpDivsion = JUMP_DEFAULT_DIVISION;
			changeDirectionsOnce = true;
			prevButton = null;
		}
		if ( !inputHandler.screwPressed( ) ) {
			screwButtonHeld = false;
		}
		if ( !inputHandler.isGrabPressed( ) ) {
			grabCounter++;
		}
		if ( !inputHandler.jumpPressed( ) ) {
			canJumpOffScrew = true;
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
					if ( currentScrew != null ) {
						attachToScrew( );
						jumpCounter = 0;
						if ( inputHandler.jumpPressed( ) ) {
							canJumpOffScrew = false;
						}
						if ( inputHandler.screwPressed( ) ) {
							screwButtonHeld = true;
						}
					}
				}
			} else {
				if ( !screwButtonHeld ) {
					if ( mover == null ) {
						detachScrewImpulse( );
						if ( currentScrew.getDepth( ) >= 0 ) {
							removePlayerToScrew( );
						}
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
		}
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
			if ( currentScrew != null ) {
				attachToScrew( );
				if ( controllerListener.jumpPressed( ) ) {
					canJumpOffScrew = false;
				}

				jumpCounter = 0;
			}
		}
		// If the button is let go, then the player is dropped
		// Basically you have to hold attach button to stick to screw
		if ( !controllerListener.screwPressed( )
				&& playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				detachScrewImpulse( );
				if ( currentScrew.getDepth( ) >= 0 ) {
					removePlayerToScrew( );
				}
			}
		}
		// loosen and tighten screws and jump when the screw joint is gone
		if ( playerState == PlayerState.Screwing ) {
			handleScrewing( true );
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
	 * sets crushing sensor
	 * 
	 * @param fixture
	 *            Fixture
	 * @param value
	 *            boolean
	 */
	public void setCrush( Fixture fixture, boolean value ) {
		if ( fixture == feet )
			botCrush = value;
		else if ( fixture == topSensor )
			topCrush = value;
		else if ( fixture == rightSensor )
			rightCrush = value;
		else if ( fixture == leftSensor )
			leftCrush = value;
	}

	/**
	 * applies force to player
	 */
	private void steamResolution( ) {
		if ( prevButton == null )
			body.setLinearVelocity( new Vector2( 0f,
					body.getLinearVelocity( ).y ) );
		// body.applyForceToCenter( 0f, STEAM_FORCE );
		// body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x, 0f
		// ) );
		body.applyLinearImpulse( new Vector2( 0, STEAM_IMPULSE ),
				body.getWorldCenter( ) );
		grounded = false;
	}

	/**
	 * sets fixture specific data for use in constructor;
	 */
	private void setFixtures( ) {
		torso = body.getFixtureList( ).get( 0 );
		leftSensor = body.getFixtureList( ).get( 1 );
		rightSensor = body.getFixtureList( ).get( 3 );
		topSensor = body.getFixtureList( ).get( 2 );
		feet = body.getFixtureList( ).get( 4 );
		feet.setRestitution( 0.001f );
		torso.getShape( ).setRadius( 0 );
		rightSensor.setSensor( true );
		leftSensor.setSensor( true );
		topSensor.setSensor( true );
		rightSensor.setDensity( 0.0f );
		leftSensor.setDensity( 0.0f );
		topSensor.setDensity( 0.0f );
	}

	public Controller getController( ) {
		return controller;
	}

	public void setControllerIndex( int i ) {
		controllerListener = new MyControllerListener( );
		controller = Controllers.getControllers( ).get( i );
		controller.addListener( controllerListener );
	}

	public void setController( Controller c ) {
		controller = c;
	}

	public void setInputNull( ) {
		inputHandler = null;
		controller = null;

	}
}
