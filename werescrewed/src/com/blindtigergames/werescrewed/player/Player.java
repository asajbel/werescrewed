package com.blindtigergames.werescrewed.player;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.animator.PlayerSpinemator;
import com.blindtigergames.werescrewed.entity.mover.FollowEntityMover;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.Metrics.TrophyMetric;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * A representation of a player in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Player extends Entity {

	public final static float MAX_VELOCITY = 1.50f;
	public final static float MIN_VELOCITY = 0.01f;
	public final static float MOVEMENT_IMPULSE = 0.010f;
	public final static float JUMP_IMPULSE = 0.12f;
	public final static float JUMP_SCREW_IMPULSE = JUMP_IMPULSE * 5 / 4;
	public final static float JUMP_SLOW_SPEED = 0.002f;
	public final static int JUMP_COUNTER = 10;
	public final static float ANALOG_DEADZONE = 0.4f;
	public final static float ANALOG_MAX_RANGE = 1.0f;
	public final static float PLAYER_FRICTION = 1.0f;
	public final static float FRICTION_INCREMENT = 0.2f;
	public final static int SCREW_ATTACH_STEPS = 15;
	public final static int HEAD_JUMP_STEPS = 30;
	public final static int DEAD_STEPS = 0;
	public final static int RUN_STEPS = 7;
	public final static float SCREW_ATTACH_SPEED = 0.1f;
	public final static int GRAB_COUNTER_STEPS = 5;
	public final static Vector2 ANCHOR_BUFFER_SIZE = new Vector2( 300f, 200f );
	public final static float STEAM_FORCE = .5f;
	public final static float STEAM_IMPULSE = 0.2f;
	public final static float FEET_OFFSET_X = 59f * Util.PIXEL_TO_BOX;
	public final static float FEET_OFFSET_Y = 23.5f * Util.PIXEL_TO_BOX;
	public final static float JUMP_DIRECTION_MULTIPLIER = 2f;
	public final static float JUMP_DEFAULT_DIVISION = 2.0f;
	public float directionJumpDivsion = JUMP_DEFAULT_DIVISION;
	public boolean flipX = false;
	public final static float HEIGHT = 128;
	public final static float WIDTH = 64;
	public final static float FOOTSTEP_DELAY = 1.0f;
	public final static float FOOTSTEP_PITCH_DROP = 0.75f;
	public final static float FOOTSTEP_VOLUME_DROP = 0.0f;
	
	// public final static float

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

	private PovDirection prevButton = null;
	public PlayerInputHandler inputHandler;
	private MyControllerListener controllerListener;
	private PlayerState playerState;
	private ConcurrentState extraState;
	private PlayerDirection playerDirection = PlayerDirection.Idle;
	@SuppressWarnings( "unused" )
	private boolean reachedMaxSpeed;
	private PlayerDirection prevPlayerDir = PlayerDirection.Idle;
	private Controller controller;
	@SuppressWarnings( "unused" )
	private boolean controllerIsActive, controllerDebug;
	private boolean flyDebug = false;
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
	private Platform currentPlatform;
	private Platform lastPlatformHit;
	private boolean topPlayer = false;
	private boolean isDead = false;
	private boolean hitSolidObject;
	private boolean knockedOff = false;
	private int screwAttachTimeout = 0;
	private int headStandTimeout = 0;
	private int runTimeout = 0;
	private int respawnTimeout = 0;
	private boolean grounded;
	private boolean jumpPressedKeyboard;
	private boolean jumpPressedController;
	private boolean canJumpOffScrew;
	private boolean screwButtonHeld;
	private boolean kinematicTransform = false;
	private boolean changeDirectionsOnceInAir = false;
	private boolean changeDirections = false;
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

	public float frictionCounter = PLAYER_FRICTION;

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
		Standing, Running, Jumping, Falling, Screwing, Dead, GrabMode, HeadStand, Landing, RespawnMode
	}

	public enum ConcurrentState {
		Ignore, ExtraJumping, ExtraFalling, ScrewReady, ScrewStow
	}

	// enum to handle different states of movement
	public enum PlayerDirection {
		Idle, Left, Right
	}

	private String landCloudName = "land_cloud";
	private String[ ] injuredParticles = { "injured/oof", "injured/arg",
			"injured/doof" };
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
		Anchor anchor = new Anchor( new Vector2( body.getWorldCenter( ).x
				* Util.BOX_TO_PIXEL, body.getWorldCenter( ).y
				* Util.BOX_TO_PIXEL ), new Vector2( 0, 0 ), new Vector2(
				ANCHOR_BUFFER_SIZE.x, ANCHOR_BUFFER_SIZE.y ) );
		anchor.activate( );
		addAnchor( anchor );

		// build spine animator
		if ( this.type.isAnimatorType( "spine" ) ) {
			spinemator = new PlayerSpinemator( this );
			spinemator.setPosition( body.getWorldCenter( ) );
		}
		Filter filter = new Filter( );
		for ( Fixture f : body.getFixtureList( ) ) {
			filter.categoryBits = Util.CATEGORY_PLAYER;
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}

		setFixtures( );
		maxFriction( );

		BodyDef bodydef = new BodyDef( );
		bodydef.position.set( pos );

		setUpController( );
		controllerDebug = true;

		if ( sounds == null ) {
			sounds = new SoundManager( );
		}
		loadSounds();

		r = new Random( );
		addBehindParticleEffect( landCloudName, false, false );
		addFrontParticleEffect( "skid_left", false, false );
		addFrontParticleEffect( "skid_right", false, false );
		for ( String s : injuredParticles ) {
			addBehindParticleEffect( s, false, false );
			getEffect( s ).allowCompletion( );
		}
		addBehindParticleEffect( "revive", false, false );
		// land_cloud = ParticleEffect.loadEffect( "land_cloud" );

		createCircle( PLAYER_FRICTION );
		frictionCounter = PLAYER_FRICTION;
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
		if ( Gdx.input.isKeyPressed( Keys.NUM_7 )) 
			flyDebug = !flyDebug;
		if ( flyDebug ) 
			grounded = true;
		if ( kinematicTransform ) {
			// setPlatformTransform( platformOffset );
			kinematicTransform = false;
		}
		if ( playerState != PlayerState.Screwing && screwAttachTimeout > 0 ) {
			screwAttachTimeout--;
		}
		// if dead do dead stuff
		if ( isDead ) {
			// Trophy check for time spent dead
			if ( this.name == Metrics.player1( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P1DEADTIME, 0.01f );
			} else if ( this.name == Metrics.player2( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P2DEADTIME, 0.01f );
			}

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
			// if re-spawning decrement time out
			// player will not die in this time
			if ( respawnTimeout > 0 ) {
				respawnTimeout--;
			}
			// build extra fixture to have new friction
			// updateFootFriction( );
			// test if player is still moving after timeout
			if ( playerDirection != PlayerDirection.Idle
					&& playerState != PlayerState.Screwing ) {
				if ( runTimeout == 0 && playerState != PlayerState.Jumping
						&& playerState != PlayerState.Falling
						&& extraState != ConcurrentState.ExtraFalling
						&& extraState != ConcurrentState.ExtraJumping ) {
					playerDirection = PlayerDirection.Idle;
				} else if ( playerDirection == PlayerDirection.Left
						&& prevPlayerDir != PlayerDirection.Left ) {
					// && type.getScale( ).x > 0 ) {
					prevPlayerDir = PlayerDirection.Left;
					flipX = true;
					type.setScale( type.getScale( ).x * -1, type.getScale( ).y );
				} else if ( playerDirection == PlayerDirection.Right
						&& prevPlayerDir != PlayerDirection.Right ) {
					prevPlayerDir = PlayerDirection.Right;
					// && type.getScale( ).x < 0 ) {
					flipX = false;
					type.setScale( type.getScale( ).x * -1, type.getScale( ).y );
				} else if ( playerState != PlayerState.Jumping
						&& playerState != PlayerState.Falling
						&& extraState != ConcurrentState.ExtraFalling
						&& extraState != ConcurrentState.ExtraJumping ) {
					runTimeout--;
				}
			}
		}
		updateFootFrictionNew( );
		// switch between states
		switch ( playerState ) {
		case Dead:
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
				removePlayerToScrew( );
				knockedOff = false;
			} else if ( mover != null ) {
				FollowEntityMover lm = ( FollowEntityMover ) mover;
				if ( !lm.atEnd( ) ) {
					lm.move( deltaTime, body );
				} else {
					body.setTransform(
							new Vector2( currentScrew.getPositionPixel( ).x
									- ( WIDTH / 2.0f ), currentScrew
									.getPositionPixel( ).y - ( HEIGHT / 2.0f ) )
									.mul( Util.PIXEL_TO_BOX ), 0.0f );
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
				// if resurrect screw and its not active remove the player
				// joint
				if ( currentScrew.getScrewType( ) == ScrewType.SCREW_RESURRECT ) {
					ResurrectScrew rezScrew = ( ResurrectScrew ) currentScrew;
					if ( rezScrew.deleteQueue( ) ) {
						// jump( );
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
				&& currentPlatform == null && !isDead ) {
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
				&& ( playerState != PlayerState.Screwing ) ) {
			// increments crush death metrics
			if ( this.name == Metrics.player1( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P1CRUSHDEATHS, 1 );
			} else if ( this.name == Metrics.player2( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P2CRUSHDEATHS, 1 );
			}

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
		// the jump doesn't work the first time on dynamic bodies so do it
		// twice
		if ( playerState == PlayerState.Jumping && isGrounded( ) ) {
			jump( );
		}

		// Trophy checks for certain player states
		// if true increments time counter for that state
		if ( grounded ) {
			if ( this.name == Metrics.player1( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P1GROUNDTIME, 0.01f );
				if ( playerDirection == PlayerDirection.Idle ) {
					Metrics.incTrophyMetric( TrophyMetric.P1IDLETIME, 0.01f );
				}
			} else if ( this.name == Metrics.player2( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P2GROUNDTIME, 0.01f );
				if ( playerDirection == PlayerDirection.Idle ) {
					Metrics.incTrophyMetric( TrophyMetric.P2IDLETIME, 0.01f );
				}
			}
		} else if ( playerState == PlayerState.Falling
				|| playerState == PlayerState.Jumping ) {
			if ( this.name == Metrics.player1( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P1AIRTIME, 0.01f );
			} else if ( this.name == Metrics.player2( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P2AIRTIME, 0.01f );
			}
		} else if ( playerState == PlayerState.Screwing
				&& currentScrew.getScrewType( ) == ScrewType.SCREW_PUZZLE ) {
			if ( this.name == Metrics.player1( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P1PUZZLETIME, 0.01f );
			}
			else if ( this.name == Metrics.player2( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P2PUZZLETIME, 0.01f );
			}
		}
		//debug no collision while holding 7
		if ( Gdx.input.isKeyPressed( Keys.NUM_8 ) ) {
			for ( Fixture f: body.getFixtureList( ) ) {
				f.setSensor( true );
			}
		} else {
			for ( Fixture f: body.getFixtureList( ) ) {
				if ( f != rightSensor && f != leftSensor && f != topSensor ) {
					f.setSensor( false );
				}
			}
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
				currentPlatform = null;
				mover = null;
				Filter filter = new Filter( );
				for ( Fixture f : body.getFixtureList( ) ) {
					if ( f != rightSensor && f != leftSensor && f != topSensor ) {
						f.setSensor( true );
					}
					filter.categoryBits = Util.CATEGORY_SUBPLAYER;
					filter.maskBits = Util.CATEGORY_SCREWS;
					f.setFilterData( filter );
				}
				playerState = PlayerState.Dead;
				// Trophy Check, Figures out which player died and increments
				// death count by 1
				// If player died after the other player, increment team death
				// count by 1
				if ( this.name == Metrics.player1( ) ) {
					Metrics.incTrophyMetric( TrophyMetric.P1DEATHS, 1.0f );
					
					if ( otherPlayer != null
							&& otherPlayer.getState( ) == PlayerState.Dead ) {
						Metrics.incTrophyMetric( TrophyMetric.P1TEAMDEATHS,
								1.0f );
					}
				} else if ( this.name == Metrics.player2( ) ) {
					Metrics.incTrophyMetric( TrophyMetric.P2DEATHS, 1.0f );
					if ( otherPlayer != null
							&& otherPlayer.getState( ) == PlayerState.Dead ) {
						Metrics.incTrophyMetric( TrophyMetric.P2TEAMDEATHS,
								1.0f );
					}
				}
				if ( Metrics.activated ) {
					Metrics.addPlayerDeathPosition( this.getPositionPixel( ) );
				}
			} else {
				playerState = PlayerState.Standing;
				currentPlatform = null;
			}

			if ( !isDead ) {
				ParticleEffect blood = getEffect( injuredParticles[ r
						.nextInt( injuredParticles.length ) ] );
				blood.restartAt( getPositionPixel( ) );
			}
			isDead = true;
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
			filter.categoryBits = Util.CATEGORY_PLAYER;
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}
		playerState = PlayerState.Standing;
		currentPlatform = null;
		isDead = false;
		respawnTimeout = DEAD_STEPS;

		getEffect( "revive" ).restartAt( getPositionPixel( ).add( 0, 500 ) );
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
			if ( changeDirectionsOnceInAir && prevButton == PovDirection.west ) {
				directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
				changeDirectionsOnceInAir = false;
			}
			if ( body.getLinearVelocity( ).x < MAX_VELOCITY ) {
				body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE
						/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
			}
		} else {
			// Trophy check for player movement, checks which player and
			// increments time running
			if ( this.name == Metrics.player1( )
					&& playerState != PlayerState.Screwing ) {
				Metrics.incTrophyMetric( TrophyMetric.P1RUNDIST, 0.01f );
			} else if ( this.name == Metrics.player2( )
					&& playerState != PlayerState.Screwing ) {
				Metrics.incTrophyMetric( TrophyMetric.P2RUNDIST, 0.01f );
			}

			if ( body.getLinearVelocity( ).x < MAX_VELOCITY ) {
				body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE, 0.0f ),
						body.getWorldCenter( ) );
				if ( body.getLinearVelocity( ).x >= MAX_VELOCITY * 0.99f )
					reachedMaxSpeed = true;
				else
					reachedMaxSpeed = false;
			}
		}
		if ( playerState != PlayerState.Screwing ) {
			playerDirection = PlayerDirection.Right;
		}
		if ( grounded && prevPlayerDir == PlayerDirection.Left ) {
			getEffect( "skid_left" )
					.restartAt( getPositionPixel( ).add( 30, 0 ) );
			reachedMaxSpeed = false;
		}
		runTimeout = RUN_STEPS;
		footstepSound(1.0f);
	}

	/**
	 * Moves the player left, and in the air it halves the amount the player can
	 * jump
	 * 
	 */
	public void moveLeft( ) {
		if ( playerState == PlayerState.Falling
				|| playerState == PlayerState.Jumping ) {
			if ( changeDirectionsOnceInAir && prevButton == PovDirection.east ) {
				directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
				changeDirectionsOnceInAir = false;
			}
			if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
				body.applyLinearImpulse( new Vector2( -MOVEMENT_IMPULSE
						/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
			}
		} else {
			// Trophy check for player movement, checks which player and
			// increments time running
			if ( this.name == Metrics.player1( )
					&& playerState != PlayerState.Screwing ) {
				Metrics.incTrophyMetric( TrophyMetric.P1RUNDIST, 0.01f );
			} else if ( this.name == Metrics.player2( )
					&& playerState != PlayerState.Screwing ) {
				Metrics.incTrophyMetric( TrophyMetric.P2RUNDIST, 0.01f );
			}

			if ( body.getLinearVelocity( ).x > -MAX_VELOCITY ) {
				body.applyLinearImpulse(
						new Vector2( -MOVEMENT_IMPULSE, 0.0f ),
						body.getWorldCenter( ) );
				if ( body.getLinearVelocity( ).x <= -MAX_VELOCITY * 0.99f )
					reachedMaxSpeed = true;
				else
					reachedMaxSpeed = false;
			}
		}
		if ( playerState != PlayerState.Screwing ) {
			playerDirection = PlayerDirection.Left;
		}
		if ( grounded && prevPlayerDir == PlayerDirection.Right ) {
			getEffect( "skid_right" ).restartAt(
					getPositionPixel( ).add( 100, 0 ) );
			reachedMaxSpeed = false;
		}
		runTimeout = RUN_STEPS;
		footstepSound(1.0f);
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
		if ( grounded && prevPlayerDir == PlayerDirection.Left ) {
			getEffect( "skid_left" )
					.restartAt( getPositionPixel( ).add( 30, 0 ) );
			reachedMaxSpeed = false;
		}
		runTimeout = RUN_STEPS;

		// Trophy check for player movement, checks which player and increments
		// time running
		if ( this.name == Metrics.player1( )
				&& playerState != PlayerState.Screwing ) {
			Metrics.incTrophyMetric( TrophyMetric.P1RUNDIST, 0.01f );
		} else if ( this.name == Metrics.player2( )
				&& playerState != PlayerState.Screwing ) {
			Metrics.incTrophyMetric( TrophyMetric.P2RUNDIST, 0.01f );
		}
		footstepSound(leftAnalogX);
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
		if ( grounded && prevPlayerDir == PlayerDirection.Right ) {
			getEffect( "skid_right" ).restartAt(
					getPositionPixel( ).add( 100, 0 ) );
			reachedMaxSpeed = false;
		}
		runTimeout = RUN_STEPS;

		// Trophy check for player movement, checks which player and increments
		// time running
		if ( this.name == Metrics.player1( )
				&& playerState != PlayerState.Screwing ) {
			Metrics.incTrophyMetric( TrophyMetric.P1RUNDIST, 0.01f );
		} else if ( this.name == Metrics.player2( )
				&& playerState != PlayerState.Screwing ) {
			Metrics.incTrophyMetric( TrophyMetric.P2RUNDIST, 0.01f );
		}
		footstepSound(leftAnalogX);
	}

	/**
	 * Moves the player right, in the air, movement impulse is lessened
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogRightInAir( ) {

		if ( changeDirectionsOnceInAir && prevButton == PovDirection.west ) {
			directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
			changeDirectionsOnceInAir = false;
		}
		leftAnalogX = controllerListener.analogLeftAxisX( );
		float temp = ( ( ( leftAnalogX - ANALOG_DEADZONE ) / ( ANALOG_MAX_RANGE - ANALOG_DEADZONE ) ) * ( MAX_VELOCITY - MIN_VELOCITY ) )
				+ MIN_VELOCITY;
		if ( body.getLinearVelocity( ).x < temp ) {
			body.applyLinearImpulse( new Vector2( MOVEMENT_IMPULSE
					/ directionJumpDivsion, 0.0f ), body.getWorldCenter( ) );
		}
		playerDirection = PlayerDirection.Right;
		footstepSound(leftAnalogX);
	}

	/**
	 * Moves the player left, in the air, movement impulse is lessened
	 * 
	 * @author Ranveer
	 */
	public void moveAnalogLeftInAir( ) {
		if ( changeDirectionsOnceInAir && prevButton == PovDirection.east ) {
			directionJumpDivsion *= JUMP_DIRECTION_MULTIPLIER;
			changeDirectionsOnceInAir = false;
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
		if ( grounded
				|| ( playerState == PlayerState.HeadStand && this.isTopPlayer( ) ) ) {
			sounds.playSound( "jump" );
			// Trophy check for player jumps
			if ( this.name == Metrics.player1( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P1JUMPS, 1.0f );
				if ( playerState == PlayerState.HeadStand ) {
					Metrics.incTrophyMetric( TrophyMetric.P1HEADSTANDS, 1.0f );
				}
			} else if ( this.name == Metrics.player2( ) ) {
				Metrics.incTrophyMetric( TrophyMetric.P2JUMPS, 1.0f );
				if ( playerState == PlayerState.HeadStand ) {
					Metrics.incTrophyMetric( TrophyMetric.P2HEADSTANDS, 1.0f );
				}
			}
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
	 */
	public void hitScrew( Screw screw ) {
		if ( playerState != PlayerState.Screwing ) {
			currentScrew = screw;

			// Trophy check for if player attaches to a stripped screw
			if ( screw != null ) {
				if ( currentScrew.getScrewType( ) == ScrewType.SCREW_STRIPPED ) {
					if ( this.name == Metrics.player1( ) ) {
						Metrics.incTrophyMetric( TrophyMetric.P1STRIPATTACH,
								1.0f );
					} else if ( this.name == Metrics.player2( ) ) {
						Metrics.incTrophyMetric( TrophyMetric.P2STRIPATTACH,
								1.0f );
					}
				}
			}
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
				getEffect( landCloudName ).restartAt(
						getPositionPixel( ).add( 50, 0 ) );
			}
			this.grounded = newVal;
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

	private void updateFootFrictionNew( ) {

		if ( prevButton != null ) {
			if ( body.getLinearVelocity( ).x > MAX_VELOCITY ) {
				body.setLinearVelocity( MAX_VELOCITY,
						body.getLinearVelocity( ).y );
			} else if ( body.getLinearVelocity( ).x < -MAX_VELOCITY ) {
				body.setLinearVelocity( -MAX_VELOCITY,
						body.getLinearVelocity( ).y );
			}
		}

		if ( prevButton == null ) {

			if ( feet.getFriction( ) < PLAYER_FRICTION ) {

				frictionCounter += FRICTION_INCREMENT;

				if ( frictionCounter > PLAYER_FRICTION ) {
					frictionCounter = PLAYER_FRICTION;

				}

				createCircle( frictionCounter );

			}
		} else {

			if ( grounded && ( prevPlayerDir != playerDirection ) ) {
				createCircle( PLAYER_FRICTION );
				frictionCounter = PLAYER_FRICTION;
			}

			if ( feet.getFriction( ) > 0 ) {

				frictionCounter -= FRICTION_INCREMENT;
				if ( frictionCounter < 0 ) {
					frictionCounter = 0;
				}
				createCircle( frictionCounter );

			}
		}

	}

	/**
	 * sets the body of some body that the player is hitting
	 */
	public void hitSolidObject( Platform platform ) {
		if ( currentPlatform == null && platform != null
				&& playerState == PlayerState.Screwing ) {
			// if on a screw that isn't already over a platform and a platform
			// hits you
			// you get knocked off
			// knockedOff = true;
		} else {
			currentPlatform = platform;
			if ( playerState == PlayerState.Falling ) {
				playerState = PlayerState.Standing;
			}
		}
		if ( platform == null ) {
			knockedOff = false;
			hitSolidObject = false;
		} else {
			if ( platform.isKinematic( ) ) {
				lastPlatformHit = platform;
			}
			hitSolidObject = true;
		}
	}

	/**
	 * gets the last kinematic platform hit
	 */
	public Platform getLastPlatform( ) {
		return lastPlatformHit;
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
		if ( currentScrew != null && screwAttachTimeout == 0
				&& currentScrew.body.getJointList( ).size( ) > 0
				&& playerState != PlayerState.HeadStand
				&& !currentScrew.isPlayerAttached( ) ) {
			if ( !currentScrew.playerNotSensor( ) ) {
				for ( Fixture f : body.getFixtureList( ) ) {
					// may be removed later leaving in for now
					f.setSensor( true );
				}
			}
			mover = new FollowEntityMover( body.getPosition( ).mul(
					Util.BOX_TO_PIXEL ), currentScrew, new Vector2(
					-WIDTH / 2.0f, -HEIGHT / 2.0f ), SCREW_ATTACH_SPEED );
			playerState = PlayerState.Screwing;
			currentScrew.setPlayerAttached( true );
			screwAttachTimeout = SCREW_ATTACH_STEPS;
			setGrounded( false );
			if ( Metrics.activated ) {
				Metrics.addPlayerAttachToScrewPosition( this.getPositionPixel( ) );
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
					if ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRUCTURAL
							|| currentScrew.getDepth( ) >= 0 ) {
						jump( );
						removePlayerToScrew( );
					}
					removePlayerToScrew( );
					if ( Metrics.activated ) {
						Metrics.addPlayerJumpPosition( this.getPositionPixel( ) );
						Metrics.addToUnscrewListOnce = false;
					}
				}
			}
		} else if ( !jumpPressedKeyboard ) {
			if ( !topPlayer ) {
				if ( playerState != PlayerState.HeadStand ) {
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
					if ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRUCTURAL
							|| currentScrew.getDepth( ) >= 0 ) {
						jump( );
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
				if ( playerState != PlayerState.HeadStand ) {
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
							// Trophy check for unscrewed screws
							if ( this.name == Metrics.player1( ) ) {
								Metrics.incTrophyMetric(
										TrophyMetric.P1UNSCREWED, 1.0f );
							} else if ( this.name == Metrics.player2( ) ) {
								Metrics.incTrophyMetric(
										TrophyMetric.P2UNSCREWED, 1.0f );
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
			} else {
				currentScrew.stopScrewing();
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
							// Trophy check for unscrewed screws
							if ( this.name == Metrics.player1( ) ) {
								Metrics.incTrophyMetric(
										TrophyMetric.P1UNSCREWED, 1.0f );
							} else if ( this.name == Metrics.player2( ) ) {
								Metrics.incTrophyMetric(
										TrophyMetric.P2UNSCREWED, 1.0f );
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
			} else {
				currentScrew.stopScrewing();
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
				if ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRUCTURAL
						|| currentScrew.getDepth( ) >= 0 ) {
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
					&& currentPlatform == null ) {
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
		// old grab functionality
		// if ( otherPlayer != null
		// && otherPlayer.getState( ) == PlayerState.Standing
		// && !isGrounded( ) ) {
		// if this player is jumping or falling and the other player is
		// standing
		// topPlayer = true;
		// setHeadStand( );
		// otherPlayer.setHeadStand( );
		// } else if ( playerState == PlayerState.Standing ) {
		// playerState = PlayerState.GrabMode;
		// }
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
		for ( Fixture f : body.getFixtureList( ) ) {
			if ( f != rightSensor && f != leftSensor && f != topSensor ) {
				f.setSensor( false );
			}
		}
		mover = null;
		if ( currentScrew != null ) {
			currentScrew.setPlayerAttached( false );
		}
		currentScrew = null;
		playerState = PlayerState.Jumping;
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

		if ( controllerListener.checkLeftStickForScrewing( ) ) {
			if ( ( controllerListener.analogLeftAxisX( ) < 0.7 && controllerListener
					.analogLeftAxisY( ) < 0.7 )
					&& ( controllerListener.analogLeftAxisX( ) > -0.7 && controllerListener
							.analogLeftAxisY( ) > -0.7 ) ) {
				switchedScrewingDirection = true;
				resetScrewing = true;
			}
		} else if ( controllerListener.checkRightStickForScrewing( ) ) {
			if ( ( controllerListener.analogRightAxisX( ) < 0.7 && controllerListener
					.analogRightAxisY( ) < 0.7 )
					&& ( controllerListener.analogRightAxisX( ) > -0.7 && controllerListener
							.analogRightAxisY( ) > -0.7 ) ) {
				switchedScrewingDirection = true;
				resetScrewing = true;
			}
		} else if ( controllerListener.checkRightStickForScrewing( ) ) {
			if ( ( controllerListener.analogRightAxisX( ) < 0.7 && controllerListener
					.analogRightAxisY( ) < 0.7 )
					&& ( controllerListener.analogRightAxisX( ) > -0.7 && controllerListener
							.analogRightAxisY( ) > -0.7 ) ) {
				switchedScrewingDirection = true;
				resetScrewing = true;
			}
		}

		if ( isGrounded( ) ) {
			jumpCounter = 0;
			directionJumpDivsion = JUMP_DEFAULT_DIVISION;
			changeDirectionsOnceInAir = true;
			prevButton = null;
			// switchedScrewingDirection = false;
		}
		if ( !controllerListener.screwPressed( ) ) {
			extraState = ConcurrentState.Ignore;
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
			changeDirectionsOnceInAir = true;
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
			extraState = ConcurrentState.Ignore;
		}
		// attach to screws when attach button is pushed
		if ( inputHandler.screwPressed( ) ) {
			if ( playerState != PlayerState.Screwing && screwAttachTimeout == 0 ) {
				if ( currentScrew != null ) {
					attachToScrew( );
					jumpCounter = 0;
					if ( inputHandler.jumpPressed( ) ) {
						canJumpOffScrew = false;
					}
					if ( inputHandler.screwPressed( ) ) {
						screwButtonHeld = true;
					}
				} else {
					extraState = ConcurrentState.ScrewReady;
				}
			} else {
				if ( !screwButtonHeld ) {
					if ( mover == null ) {
						if ( currentScrew != null
								&& ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRUCTURAL || currentScrew
										.getDepth( ) >= 0 ) ) {
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
		if ( ( controllerListener.screwPressed( ) && screwAttachTimeout == 0 )
				&& ( playerState != PlayerState.Screwing ) ) {
			if ( currentScrew != null ) {
				attachToScrew( );
				if ( controllerListener.jumpPressed( ) ) {
					canJumpOffScrew = false;
				}

				jumpCounter = 0;
			} else
				extraState = ConcurrentState.ScrewReady;
		}
		// If the button is let go, then the player is dropped
		// Basically you have to hold attach button to stick to screw
		if ( !controllerListener.screwPressed( )
				&& playerState == PlayerState.Screwing ) {
			if ( mover == null ) {
				if ( currentScrew.getScrewType( ) != ScrewType.SCREW_STRUCTURAL
						|| currentScrew.getDepth( ) >= 0 ) {
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

		// increments steam jump trophy metric
		if ( this.name == Metrics.player1( ) ) {
			Metrics.incTrophyMetric( TrophyMetric.P1STEAMJUMPS, 1 );
		} else if ( this.name == Metrics.player2( ) ) {
			Metrics.incTrophyMetric( TrophyMetric.P2STEAMJUMPS, 1 );
		}
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
		// torso.setFriction( 0.5f );
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

	private void createCircle( float friction ) {
		CircleShape ps = new CircleShape( );
		ps.setRadius( feet.getShape( ).getRadius( ) );

		ps.setPosition( ps.getPosition( ).add( FEET_OFFSET_X, FEET_OFFSET_Y ) );
		FixtureDef fd = new FixtureDef( );

		fd.shape = ps;
		fd.density = 1f;
		fd.restitution = 0.001f;
		fd.friction = friction;

		if ( playerState == PlayerState.Screwing ) {
			fd.isSensor = true;
		}

		fd.filter.categoryBits = Util.CATEGORY_PLAYER;
		fd.filter.maskBits = Util.CATEGORY_EVERYTHING;

		body.destroyFixture( feet );

		feet = body.createFixture( fd );

	}

	public float getAbsAnalogXRatio( ) {
		if ( controllerListener != null ) {
			float x = Math.abs( controllerListener.analogLeftAxisX( ) );
			if ( x > MyControllerListener.DEADZONE ) {
				x = ( x - MyControllerListener.DEADZONE )
						/ ( 1 - MyControllerListener.DEADZONE );
				return x / 1;
			} else if ( controllerListener.leftPressed( )
					|| controllerListener.rightPressed( ) ) {
				return 1f;
			}
			return 0;
		}
		return 1f;
	}

	public void collide( Platform that, Contact contact ) {
		// Ensure the object is solid and involves the
		// player's
		// feet
		// also make sure its not the player
		Fixture playerFix;
		if ( contact.getFixtureB( ).getUserData( ).equals( this ) ) {
			playerFix = contact.getFixtureB( );
		} else {
			playerFix = contact.getFixtureA( );
		}
		if ( that.isSolid( ) ) {
			if ( playerFix.getShape( ) instanceof CircleShape ) {
				// this.contacts++;
				hitSolidObject( that );
				if ( getState( ) != PlayerState.Screwing ) {
					setGrounded( true );
				}
			} else {
				collide( ( Entity ) that, contact );
			}
		}
	}
	
	public void footstepSound( float a ){
		float amount = (float)Math.pow( Math.abs( a ), 2.0);
		if (isGrounded() && this.playerState != PlayerState.Screwing){
			float rate = FOOTSTEP_DELAY;
			float pitch = FOOTSTEP_PITCH_DROP + amount * (1.0f - FOOTSTEP_PITCH_DROP);
			float vol = FOOTSTEP_VOLUME_DROP + amount * (1.0f - FOOTSTEP_VOLUME_DROP);
			if (sounds.isDelayed( "footstep1" )){
				sounds.setSoundVolume( "footstep2", vol );
				sounds.setSoundPitch( "footstep2", pitch );
				sounds.playSound( "footstep2", rate );
			} else {
				sounds.setSoundVolume( "footstep1", vol );
				sounds.setSoundPitch( "footstep1", pitch );
				sounds.playSound( "footstep1", rate );
				sounds.setDelay( "footstep2", 0.5f * rate );
			}
		}
	}
	
	public void loadSounds(){
		sounds.getSound( "jump", WereScrewedGame.dirHandle
				+ "/common/sounds/jump.ogg" );
		sounds.getSound( "footstep1" , WereScrewedGame.dirHandle
				+ "/common/sounds/footstep1.ogg");
		sounds.getSound( "footstep2" , WereScrewedGame.dirHandle
				+ "/common/sounds/footstep2.ogg");
		sounds.getSound( "land" , WereScrewedGame.dirHandle
				+ "/common/sounds/land.ogg");
	}
}
