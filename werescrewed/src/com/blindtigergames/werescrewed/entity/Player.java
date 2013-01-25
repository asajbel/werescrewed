package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.input.InputHandlerPlayer1;
import com.blindtigergames.werescrewed.screws.BossScrew;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;

/**
 * 
 * A representation of a player in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Player extends Entity {

	// FIELDS

	// Variables
	private Fixture playerPhysicsFixture;
	private Fixture playerSensorFixture;
	private float stillTime = 0;
	private long lastGroundTime = 0;
	private int prevKey;
	private InputHandlerPlayer1 inputHandler;
	private PlayerState playerState;
	public ArrayList< Contact > contacts;

	private Screw currentScrew;
	private RevoluteJoint playerToScrew;
	private boolean hitScrew;
	private boolean grounded;
	private boolean jumpPressed;

	// Constants

	// Static constants
	public final static float MAX_VELOCITY = 300f;

	// Static variables
	public static Texture texture = new Texture(
			Gdx.files.internal( "data/player_r_m.png" ) );

	// private Camera cam;

	/**
	 * 
	 * One of Standing, Jumping, and Falling.
	 * 
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
				new Vector2( 1f, 1f ) );
		// Encompasses:
		// world = w;
		// createPlayerBody(posX, posY);
		// createPlayerBodyOLD(pos.x, pos.y);
		body.setGravityScale( 0.25f );
		body.setFixedRotation( true );
		// sprite.setScale( 100f * GameScreen.PIXEL_TO_BOX );
		// offset.x = -15f;
		// offset.y = -7.5f;
		body.setUserData( this );
		playerState = PlayerState.Standing;
		inputHandler = new InputHandlerPlayer1( );
		contacts = new ArrayList< Contact >( );
	}

	/**
	 * 
	 * @param world
	 *            in which the player exists
	 * @param posX
	 *            of the player
	 * @param posY
	 *            of the player
	 * @param name
	 * @param tex
	 *            ture of the player sprite
	 */
	public Player( World world, float posX, float posY, String name, Texture tex ) {
		this( world, new Vector2( posX, posY ), name, tex );
		// createPlayerBody(posX, posY);
		// createPlayerBodyOLD(posX, posY);
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
		this.world = world;
		// createPlayerBody(posX, posY);
		// createPlayerBodyOLD(pos.x, pos.y);
	}

	// METHODS

	/**
	 * Moves the player right, or jumps them off of a screw to the right
	 */
	public void moveRight( ) {
		if ( playerState == PlayerState.Screwing ) {
			world.destroyJoint( playerToScrew );
			playerState = PlayerState.JumpingOffScrew;
			body.applyLinearImpulse( new Vector2( 0.05f, 0.0f ),
					body.getWorldCenter( ) );
			jump( );
		} else if ( body.getLinearVelocity( ).x < 2.0f ) {
			body.applyLinearImpulse( new Vector2( 0.01f, 0.0f ),
					body.getWorldCenter( ) );
		}
		// body.applyLinearImpulse(new Vector2(0.001f, 0.0f),
		// body.getWorldCenter());

		// Following three lines update the texture
		// doesn't belong here, I learned
		// Vector2 pos = playerBody.getPosition();
		// this.positionX = pos.x;
		// this.positionY = pos.y;
	}

	/**
	 * Moves the player left, or jumps them off of a screw to the left
	 */
	public void moveLeft( ) {
		if ( playerState == PlayerState.Screwing ) {
			world.destroyJoint( playerToScrew );
			playerState = PlayerState.JumpingOffScrew;
			body.applyLinearImpulse( new Vector2( -0.05f, 0.0f ),
					body.getWorldCenter( ) );
			jump( );
		} else if ( body.getLinearVelocity( ).x > -2.0f ) {
			body.applyLinearImpulse( new Vector2( -0.01f, 0.0f ),
					body.getWorldCenter( ) );
		}
		// body.applyLinearImpulse(new Vector2(-0.001f, 0.0f),
		// body.getWorldCenter());
		// Gdx.app.debug("Physics:",
		// "Applying Left Impulse to player at "+playerBody.getWorldCenter());

	}

	/**
	 * Causes the player to jump
	 */
	public void jump( ) {
		body.setLinearVelocity( new Vector2( body.getLinearVelocity( ).x, 0.0f ) );
		body.applyLinearImpulse( new Vector2( 0.0f, 0.15f ),
				body.getWorldCenter( ) );
		/* Math.abs( body.getLinearVelocity( ).y ) < 1e-5 */
	}

	/**
	 * Sets the current screw
	 */
	public void hitScrew( Screw screw ) {
		if( playerState != PlayerState.Screwing ) {
			hitScrew = true;
			currentScrew =  screw;
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
		// sprite.setY( sprite.getY( ) + sprite.getHeight( ) / 8);
		// sprite.setPosition( body.getPosition( ).x, body.getPosition( ).y);
		// sprite.setOrigin( offset.x, offset.y );
		// Vector2 pos = body.getPosition();
		// Vector2 vel = body.getLinearVelocity();
		if ( playerState != PlayerState.Screwing && playerState != PlayerState.Standing && isGrounded( ) ) {
			playerState = PlayerState.Standing;
			System.out.println( "Standing" );
		}

		if ( inputHandler.jumpPressed( ) ) {
			if ( !jumpPressed ) {
				System.out.println( grounded );
				System.out.println( body.getLinearVelocity( ) );
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
			sprite.setPosition( currentScrew.sprite.getX( ),
			 currentScrew.sprite.getY( ) );
			if ( inputHandler.unscrewPressed( ) ) {
				currentScrew.screwLeft( );
			} else if ( inputHandler.screwPressed( ) ) {
				currentScrew.screwRight( );
			}
			if ( currentScrew.body.getJointList( ).size( ) <= 1 ) {
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

// /**
// *
// * @return A boolean that indicates if the player is on ground.
// *
// */
// private boolean isGrounded( ) {
//
// // A list of all instances of contact in the world
// List< Contact > contactList = world.getContactList( );
//
// // Loop through all contacts
// for ( Contact contact : contactList ) {
// // If the current contact is touching and involves a player
// if ( contact.isTouching( )
// && ( contact.getFixtureA( ) == playerSensorFixture || contact
// .getFixtureB( ) == playerSensorFixture ) ) {
//
// Vector2 playerPos = body.getPosition( );
// WorldManifold manifold = contact.getWorldManifold( );
//
// // Tracks whether every point is "below" the character
// boolean below = true;
//
// // Loop through all contact points in current contact
// for ( Vector2 contactPoint : manifold.getPoints( ) ) {
// below &= ( contactPoint.y < playerPos.y - GROUND_THRESHOLD );
// }
//
// if ( below ) {
// return true;
// }
//
// return false;
// }
// }
// return false;
// }
//

// I tried some weird stuff in this constructor
// private void createPlayerBody( float x, float y ) {
//
// BodyDef playerBodyDef = new BodyDef( );
// playerBodyDef.type = BodyType.DynamicBody;
// playerBodyDef.position.set( x, y );
// body = world.createBody( playerBodyDef );
//
// PolygonShape poly = new PolygonShape( );
// poly.setAsBox( 25f, 25f );
// playerPhysicsFixture = body.createFixture( poly, 1 );
// poly.dispose( );
//
// CircleShape circle = new CircleShape( );
// circle.setRadius( 25f );
// circle.setPosition( new Vector2( 0, -25f ) );
// playerSensorFixture = body.createFixture( circle, 0 );
//
// circle.dispose( );
//
// body.setBullet( true );
// /*
// * CircleShape playerfeetShape = new CircleShape();
// * playerfeetShape.setRadius(7f);
// *
// * FixtureDef playerFixtureDef = new FixtureDef();
// * //playerBody.createFixture(playerPolygonShape, 1.0f);
// * playerFixtureDef.shape = playerfeetShape; playerFixtureDef.density =
// * 0.9f; playerFixtureDef.friction = 0f; playerFixtureDef.restitution =
// * 0.0f; playerBody.createFixture(playerFixtureDef);
// * playerBody.setGravityScale(1f); playerBody.setFixedRotation(true);
// * //playerBody. playerfeetShape.dispose();
// */
// }

// // functionality has been moved to EntityDef
// private void createPlayerBodyOLD( float x, float y ) {
//
// BodyDef playerBodyDef = new BodyDef( );
// playerBodyDef.type = BodyType.DynamicBody;
// playerBodyDef.position.set( x, y );
// body = world.createBody( playerBodyDef );
// CircleShape playerfeetShape = new CircleShape( );
// playerfeetShape.setRadius( 10f * GameScreen.PIXEL_TO_BOX );
// FixtureDef playerFixtureDef = new FixtureDef( );
// // playerBody.createFixture(playerPolygonShape, 1.0f);
// playerFixtureDef.shape = playerfeetShape;
// playerFixtureDef.density = 9.9f;
// playerFixtureDef.friction = 0.05f;
// playerFixtureDef.restitution = 0.5f;
// body.createFixture( playerFixtureDef );
// body.setGravityScale( .1f );
// body.setFixedRotation( true );
// playerfeetShape.dispose( );
//
// }

// This example is found at a blog, i couldn't get it to work right away
// boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
// if(grounded) { lastGroundTime = System.nanoTime(); } else {
// if(System.nanoTime() - lastGroundTime < 100000000) { grounded = true;
// } }
//
// // cap max velocity on x if(Math.abs(vel.x) > MAX_VELOCITY) { vel.x =
// Math.signum(vel.x) * MAX_VELOCITY;
// playerBody.setLinearVelocity(vel.x, vel.y); }
//
// // calculate stilltime & damp if(!Gdx.input.isKeyPressed(Keys.A) &&
// !Gdx.input.isKeyPressed(Keys.D)) { stillTime +=
// Gdx.graphics.getDeltaTime(); playerBody.setLinearVelocity(vel.x *
// 0.9f, vel.y); } else { stillTime = 0; }
//
// // disable friction while jumping if(!grounded) {
// playerPhysicsFixture.setFriction(0f);
// playerSensorFixture.setFriction(0f); } else {
// if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)
// && stillTime > 0.2) { playerPhysicsFixture.setFriction(100f);
// playerSensorFixture.setFriction(100f); } else {
// playerPhysicsFixture.setFriction(0.2f);
// playerSensorFixture.setFriction(0.2f); }
//
// //if(groundedPlatform != null && groundedPlatform.dist == 0) { //
// playerBody.applyLinearImpulse(0, -24, pos.x, pos.y); //} }
//
// // apply left impulse, but only if max velocity is not reached yet
//
// if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
// //playerBody.applyLinearImpulse(-2f, 0, pos.x, pos.y); moveLeft(); }
//
// // apply right impulse, but only if max velocity is not reached yet
// if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
// //playerBody.applyLinearImpulse(2f, 0, pos.x, pos.y); moveRight(); }
//
// // jump, but only when grounded if(Gdx.input.isKeyPressed(Keys.W)) {
// //jump = false; if(grounded) { playerBody.setLinearVelocity(vel.x,
// 0); //System.out.println("jump before: " +
// player.getLinearVelocity()); playerBody.setTransform(pos.x, pos.y +
// 0.01f, 0); playerBody.applyLinearImpulse(0, 30, pos.x, pos.y);
// //System.out.println("jump, " + player.getLinearVelocity()); } }
//
