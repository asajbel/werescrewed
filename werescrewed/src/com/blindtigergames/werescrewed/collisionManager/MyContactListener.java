package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.action.ActionType;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.PlatformType;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;

/**
 * 
 * Used for collision handling
 * 
 */
public class MyContactListener implements ContactListener {

	private static int NUM_PLAYER1_CONTACTS = 0;
	private static int NUM_PLAYER2_CONTACTS = 0;
	private Platform player1Plat;
	private Platform player2Plat;
	private static final float LAND_DELAY = 0;
	private static final float LAND_VOLUME = 0.15f;
	private static final float COLLISION_VOLUME = 5.0f;
	private static final float LAND_FALLOFF = 3.0f;
	private static final float COLLISION_SOUND_DELAY = 0.2f;
	private static final float COLLISION_FORCE_FALLOFF = 3.0f;
	//private static final float COLLISION_SCREEN_FALLOFF = 1.0f;
	private static final float MINIMUM_HIT_FORCE = 1.0f;
	private static final float MAXIMUM_HIT_FORCE = 5.0f;
	private static final float HIT_X_Y_RATIO = 5.0f;
	private static final float HIT_SOUND_DELAY = 0.1f;

	/**
	 * When two new objects start to touch
	 */
	@Override
	public void beginContact( Contact contact ) {
		final Fixture x1 = contact.getFixtureA( );
		final Fixture x2 = contact.getFixtureB( );
		Object objectA = x1.getBody( ).getUserData( );
		Object objectB = x2.getBody( ).getUserData( );

		Fixture playerFix = null;
		Fixture objectFix = null;

		boolean playerInvolved = false;

		Vector2 force = calculateForce( contact );
		Vector2 painForce = force;
		painForce.x /= HIT_X_Y_RATIO;

		if ( objectA != null && objectB != null ) {
			if ( objectA instanceof Entity && objectB instanceof Entity ) {
				handleCollisionSounds( ( Entity ) objectA, ( Entity ) objectB,
						contact, force.len( ) );
			}
			if ( x1.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x1;
				objectFix = x2;
				playerInvolved = true;
			} else if ( x2.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x2;
				objectFix = x1;
				playerInvolved = true;
			} else {
				playerFix = x2;
				objectFix = x1;
			}
			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( object.getCrushing( ) ) {
						player.setCrush( playerFix, true );
					}
					if ( object.getEntityType( ) != null ) {
						switch ( object.getEntityType( ) ) { // switch between
																// different
																// types
																// of entities
						case PLATFORM:
							// Ensure the object is solid and involves the
							// player's
							// feet
							// also make sure its not the player
							Platform plat = ( Platform ) object;

							if ( player.getState( ) == PlayerState.Screwing ) {
								player.knockedOff = true;
							}else {
								if ( x1 == player.feet || x2 == player.feet ) {
									Vector2 contactPos = contact.getWorldManifold( ).getPoints( )[ 0 ];
									if ( contactPos.y <= ( player.getPosition( ).y + 0.1 ) ) {
										if ( player.name.equals( "player1" ) && player1Plat == null ) {
											player1Plat = plat;
										} else if ( player.name.equals( "player2" ) && player1Plat == null ) {
											player2Plat = plat;
										}
										player.hitSolidObject( plat, contact );
										//player.setGrounded( true );
									} 
								} 
							}
							if ( object.isSolid( ) ) {
								if ( playerFix.getShape( ) instanceof CircleShape ) {
									if ( player.name.equals( "player1" ) ) {
										NUM_PLAYER1_CONTACTS++;
									} else if ( player.name.equals( "player2" ) ) {
										NUM_PLAYER2_CONTACTS++;
									}
									if ( player.getState( ) != PlayerState.Screwing ) {
										if ( !player.isGrounded( ) ) {
											float fallVolume = ( float ) Math.pow(force.len( ) * LAND_VOLUME, LAND_FALLOFF );
											SoundRef fallSound = player.sounds.getSound( "land" );
											fallSound.setVolume( fallVolume );
											fallSound.setEndDelay( LAND_DELAY );
											fallSound.play( false );
										}
										player.setGrounded( true );
									}
								} else {
									if ( playerFix.equals( player.topSensor )
											&& !plat.oneSided
											&& painForce.len( ) > MINIMUM_HIT_FORCE ) {
										SoundRef hitSound = player.sounds.getSound( "hit" , 
																					player.sounds.randomSoundId( "hit" ));
										hitSound.setVolume( painForce.len( ) / MAXIMUM_HIT_FORCE );
										hitSound.play( false );
										player.sounds.setDelay( "hit", HIT_SOUND_DELAY );
									}
								}
							}
							break;
						case SCREW:
							Screw screw = ( Screw ) object;
							player.hitScrew( screw );
							break;
						case HAZARD:
							if ( !player.isPlayerDead( ) && player.body.getType( ) != BodyType.KinematicBody ) {
								Hazard hazard = ( Hazard ) object;
								hazard.performContact( player, objectFix );
							}
							break;
						case CHECKPOINT:
							CheckPoint checkP = ( CheckPoint ) objectFix
									.getBody( ).getUserData( );
							checkP.hitPlayer( player );
							break;
						case STEAM:
							// TODO: GET RID OF TEMPCOLLISION WHEN STEAM
							// PARTICLES MATCH THE BODY
							Steam steam = ( Steam ) object;
							if ( playerFix == player.torso
									&& steam.getTempCollision( ) ) {

								player.setSteamCollide( steam, true );
							}
							break;
						case EVENTTRIGGER:
							EventTrigger et = ( EventTrigger ) object;
							et.setActivated( true, player.name );
							if ( et.getBeginAction( ) instanceof RemoveEntityAction ) {
								et.getBeginAction( ).act( player );
							}
							if ( playerFix == player.torso ) {
								if ( et.getBeginAction( ).getActionType( ) == ActionType.ACT_ON_PLAYER ) {
									et.triggerBeginEvent( player );
								} else
									et.triggerBeginEvent( );
							}
							break;
						case POWERSWITCH:
							if ( playerFix == player.torso ) {
								PowerSwitch ps = ( PowerSwitch ) object;
								player.setPowerSwitch( ps );
							}
							break;
						default:
							break;
						}
					} else {
						// Gdx.app.log(
						// "please declare your entity with a type to the Entity Type enum",
						// "" );
					}
				}
			} else {
				// Player are not involved in this section //
				// checks if the object fix or player fix is an event trigger
				// then applies the event to the object that is colliding with
				// it
				if ( playerFix.getBody( ).getUserData( ) instanceof Entity
						&& objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity player = ( Entity ) playerFix.getBody( )
							.getUserData( );
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( player.getEntityType( ) == EntityType.EVENTTRIGGER
							|| object.getEntityType( ) == EntityType.EVENTTRIGGER ) {
						// EventTrigger stuff:
						if ( player.getEntityType( ) == EntityType.EVENTTRIGGER ) {
							EventTrigger et = ( EventTrigger ) player;
							if ( et.getBeginAction( ).getActionType( ) == ActionType.FORANYENTITY ) {
								// needs to get the action in order to act on
								// just
								// this
								// object
								et.getBeginAction( ).act( object );
							}
						} else if ( object.getEntityType( ) == EntityType.EVENTTRIGGER ) {
							EventTrigger et = ( EventTrigger ) object;
							if ( et.getBeginAction( ).getActionType( ) == ActionType.FORANYENTITY ) {
								// needs to get the action in order to act on
								// just
								// this
								// object
								et.getBeginAction( ).act( player );
							}
						}
					}
				}
			}
		}
	}

	/**
	 * When two objects stop touching
	 */
	@Override
	public void endContact( Contact contact ) {
		final Fixture x1 = contact.getFixtureA( );
		final Fixture x2 = contact.getFixtureB( );

		Fixture playerFix = null;
		Fixture objectFix = null;

		boolean playerInvolved = false;

		if ( x1.getBody( ).getUserData( ) != null
				&& x2.getBody( ).getUserData( ) != null ) {
			if ( x1.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x1;
				objectFix = x2;
				playerInvolved = true;
			} else if ( x2.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x2;
				objectFix = x1;
				playerInvolved = true;
			}
			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( object.getCrushing( ) ) {
						player.setCrush( playerFix, false );
					}
					if ( object.getEntityType( ) != null ) {
						switch ( object.getEntityType( ) ) { // switch between
																// different
																// types
																// of entities
						case PLATFORM:
							// Ensure the object is solid and involves the
							// player's
							// feet
							// also make sure its not the player
							Platform plat = ( Platform ) object;
							if ( object.isSolid( )
									&& playerFix.getShape( ) instanceof CircleShape ) {
								if ( player.name.equals( "player1" ) ) {
									NUM_PLAYER1_CONTACTS--;
									if ( NUM_PLAYER1_CONTACTS <= 0 ) {
										if ( player.getState( ) != PlayerState.HeadStand && plat == player1Plat ) {
											player.setGrounded( false );
										}
									}
								} else if ( player.name.equals( "player2" ) ) {
									NUM_PLAYER2_CONTACTS--;
									if ( NUM_PLAYER2_CONTACTS <= 0 ) {
										if ( player.getState( ) != PlayerState.HeadStand && plat == player2Plat) {
											player.setGrounded( false );
										}
									}
								}
								contact.setEnabled( true );
							}
							if ( player.name.equals( "player1" ) ) {
								if ( plat == player1Plat ) 
									plat = null;
									player.hitSolidObject( null, contact );
							} else if ( player.name.equals( "player2" ) ) {
								if ( plat == player2Plat ) 
									plat = null;
									player.hitSolidObject( null, contact );
							}
							break;
						case SCREW:
							if ( player.getState( ) != PlayerState.Screwing ) {
								player.hitScrew( null );
							}
							break;
						case PLAYER:
							Player player2 = ( Player ) objectFix.getBody( )
									.getUserData( );
							if ( player.getState( ) != PlayerState.HeadStand ) {
								player.hitPlayer( null );
								player2.hitPlayer( null );
							}
							break;
						case STEAM:
							// TODO: GET RID OF TEMPCOLLISION WHEN STEAM
							// PARTICLES MATCH THE BODY
							Steam steam = ( Steam ) object;
							if ( playerFix == player.torso
									&& steam.getTempCollision( ) ) {
								player.setSteamCollide( null, false );
							}
							break;
						case EVENTTRIGGER:
							if ( !player.isPlayerDead( ) ) {
								EventTrigger et = ( EventTrigger ) objectFix
										.getBody( ).getUserData( );
								if ( et.getEndAction( )  != null){
									if ( playerFix == player.torso ) {	
										if ( et.getEndAction( ).getActionType( ) == ActionType.ACT_ON_PLAYER ) {
											et.triggerEndEvent( player );
										} else
											et.triggerEndEvent( );
									}
								}
								et.setActivated( false, player.name );
							}
							break;
						case POWERSWITCH:
							if ( playerFix == player.torso ) {
								player.setPowerSwitch( null );
							}
							break;
						default:
							break;
						}
					} else {
						// Gdx.app.log(
						// "please declare your entity with a type to the Entity Type enum",
						// "" );
					}
				}
			}
		}
	}

	/**
	 * Before physics is calculated each step
	 */
	@Override
	public void preSolve( Contact contact, Manifold oldManifold ) {
		final Fixture x1 = contact.getFixtureA( );
		final Fixture x2 = contact.getFixtureB( );

		Fixture playerFix = null;
		Fixture objectFix = null;

		boolean playerInvolved = false;

		if ( x1.getBody( ).getUserData( ) != null
				&& x2.getBody( ).getUserData( ) != null ) {
			if ( x1.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x1;
				objectFix = x2;
				playerInvolved = true;
			} else if ( x2.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x2;
				objectFix = x1;
				playerInvolved = true;
			}
			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( object.getEntityType( ) != null ) {
						switch ( object.getEntityType( ) ) { // switch between
																// different
																// types
																// of entities
						case PLATFORM:
							Platform plat = ( Platform ) object;
							if ( plat.getPlatformType( ) == PlatformType.TILED ) {
								TiledPlatform tilePlat = ( TiledPlatform ) objectFix
										.getBody( ).getUserData( );
								Vector2 platformPos = tilePlat.getPosition( );
								// Gdx.app.log("center: " +
								// tilePlat.getPosition( ).y,"\nheight: " +
								// tilePlat.getMeterHeight( ));
								Vector2 playerPos = player.getPosition( );
								// platformPos.y += tilePlat.getMeterHeight( );
								if ( tilePlat.getOneSided( ) ) {
									if ( platformPos.y > playerPos.y ) {
										contact.setEnabled( false );
									}
								}
							}
							break;
						case PLAYER:
							Player player2 = ( Player ) object;
							// if ( player.getState( ) == PlayerState.GrabMode
							// || player2.getState( ) == PlayerState.GrabMode )
							// {
							// contact.setEnabled( false );
							// } else {
							if ( player.getState( ) != PlayerState.HeadStand
									&& player2.getState( ) != PlayerState.HeadStand ) {
								player.hitPlayer( player2 );
								player2.hitPlayer( player );
								if ( !player.isHeadStandPossible( )
										&& !player2.isHeadStandPossible( ) ) {
									player.hitPlayer( null );
									player2.hitPlayer( null );
									contact.setEnabled( false );
								} else {
									player.setGrounded( true );
									player2.setGrounded( true );
								}
							}
							// }
							break;
						default:
							break;
						}
					} else {
						// Gdx.app.log(
						// "please declare your entity with a type to the Entity Type enum",
						// "" );
					}
				}
			}
		}
	}

	/*
	 * After physics is calculated each step
	 */
	@Override
	public void postSolve( Contact contact, ContactImpulse impulse ) {
		final Fixture x1 = contact.getFixtureA( );
		final Fixture x2 = contact.getFixtureB( );

		Object objectA = x1.getBody( ).getUserData( );
		Object objectB = x2.getBody( ).getUserData( );

		Fixture playerFix = null;
		Fixture objectFix = null;

		boolean playerInvolved = false;

		if ( objectA != null && objectB != null ) {
			if ( objectA instanceof Player ) {
				playerFix = x1;
				objectFix = x2;
				playerInvolved = true;
			} else if ( x2.getBody( ).getUserData( ) instanceof Player ) {
				playerFix = x2;
				objectFix = x1;
				playerInvolved = true;
			}
			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity notPlayer = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( notPlayer.getEntityType( ) != null ) {
						switch ( notPlayer.getEntityType( ) ) { // switch
																// between
																// different
																// types
																// of entities
						case PLATFORM:
							Platform plat = ( Platform ) notPlayer;
							if ( plat.getPlatformType( ) == PlatformType.TILED ) {
								TiledPlatform tilePlat = ( TiledPlatform ) objectFix
										.getBody( ).getUserData( );
								Vector2 platformPos = tilePlat.getPosition( );
								Vector2 playerPos = player.getPosition( );
								if ( tilePlat.getOneSided( ) ) {
									if ( platformPos.y > playerPos.y ) {
										contact.setEnabled( false );
									}
								}
								if ( player.isTopPlayer( ) ) {
									contact.setEnabled( false );
								}
							}
							break;
						default:
							break;
						}
					} else {
						// Gdx.app.log(
						// "please declare your entity with a type to the Entity Type enum",
						// "" );
					}
				}
			}
		}
	}

	public Vector2 calculateForce( Contact contact ) {
		final Body x1 = contact.getFixtureA( ).getBody( );
		final Body x2 = contact.getFixtureB( ).getBody( );

		Vector2 contactPos;
		int contactPoints = contact.getWorldManifold( ).getPoints( ).length;
		Vector2 forceVector = new Vector2( 0, 0 );
		for ( int c = 0; c < contactPoints; c++ ) {
			contactPos = contact.getWorldManifold( ).getPoints( )[ c ];
			forceVector.add( x1.getLinearVelocityFromWorldPoint( contactPos ) );

			forceVector.sub( x2.getLinearVelocityFromWorldPoint( contactPos ) );
		}
		return forceVector.div( contactPoints );
	}

	public void handleCollisionSounds( Entity objectA, Entity objectB,
			Contact contact, float force ) {
		/*
		 * Generally, we want to avoid playing the same sound twice with any one
		 * collision. So we'll use two soundref variables to store the sounds
		 * coming from both entities, then check to see if they share a sound.
		 * If so, then we can skip playing the second one.
		 */
		// Determine the sound played by object A.
		boolean playSoundA = objectA.hasSoundManager( );
		// Determine the sound played by object B.
		boolean playSoundB = objectB.hasSoundManager( );
		// Only continue if we have at least one sound manager
		if ( playSoundA || playSoundB ) {
			String soundNameA = "collision";
			int indexA = 0;

			String soundNameB = "collision";
			int indexB = 0;

			if ( playSoundB && objectA instanceof Platform
					&& objectB.sounds.hasSound( "platformcollision" ) ) {
				soundNameB = "platformcollision";
				indexB = 0;
			}
			if ( playSoundA && objectB instanceof Platform
					&& objectA.sounds.hasSound( "platformcollision" ) ) {
				soundNameA = "platformcollision";
				indexA = 0;
			}

			// Skip playing a sound that the sound manager doesn't have.
			if ( playSoundA && !objectA.sounds.hasSound( soundNameA, indexA ) )
				playSoundA = false;
			if ( playSoundB && !objectB.sounds.hasSound( soundNameA, indexA ) )
				playSoundB = false;

			// Resolve duplicate sounds
			if ( playSoundA && playSoundB ) {
				if ( objectA.sounds.getGDXSound( soundNameA, indexA ).equals(
						objectB.sounds.getGDXSound( soundNameB, indexB ) ) ) {
					playSoundB = false;
				}
			}

			float v = Math
					.min( Math
							.max( ( float ) Math.pow( force * COLLISION_VOLUME,
									COLLISION_FORCE_FALLOFF ), 0.0f ),
							2.0f );
			// Play soundA
			if ( playSoundA ) {
				SoundRef soundA = objectA.sounds.getSound( soundNameA , indexA );
				float vA = v * SoundManager.calculatePositionalVolume( objectA.getPositionPixel(), 
						Camera.CAMERA_RECT, 
						soundA.getRange(), 
						soundA.getDepth(), 
						soundA.getFalloff() 
						);
				objectA.sounds.playSound( soundNameA, indexA,
						COLLISION_SOUND_DELAY, vA, 1.0f );
			}

			// Play soundB
			if ( playSoundB ) {
				SoundRef soundB = objectB.sounds.getSound( soundNameB , indexB );
				float vB = v * SoundManager.calculatePositionalVolume( objectB.getPositionPixel(), 
						Camera.CAMERA_RECT, 
						soundB.getRange(), 
						soundB.getDepth(), 
						soundB.getFalloff() 
						);
				objectB.sounds.playSound( soundNameB, indexB,
						COLLISION_SOUND_DELAY, vB, 1.0f );
			}
		}
	}
}