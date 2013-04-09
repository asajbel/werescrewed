package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.PlatformType;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;

/**
 * 
 * Used for collision handling
 * 
 */
public class MyContactListener implements ContactListener {

	private static int NUM_PLAYER1_CONTACTS = 0;
	private static int NUM_PLAYER2_CONTACTS = 0;
	private Player p1;

	/**
	 * When two new objects start to touch
	 */
	@Override
	public void beginContact( Contact contact ) {
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
							if ( object.isSolid( )
									&& playerFix.getShape( ) instanceof CircleShape ) {
								if ( player.name.equals( "player1" ) ) {
									NUM_PLAYER1_CONTACTS++;
								} else if ( player.name.equals( "player2" ) ) {
									NUM_PLAYER2_CONTACTS++;
								}

								player.hitSolidObject( objectFix.getBody( ) );
								if ( player.getState( ) != PlayerState.JumpingOffScrew
										|| player.getState( ) != PlayerState.Screwing ) {
									player.setGrounded( true );
								}
							}
							break;
						case SCREW:
							// if ( player.isPlayerDead( ) ) {
							Screw screw = ( Screw ) object;
							if ( p1 == null || p1 == player ) {
								p1 = player;
								player.hitScrew( screw );
								if ( screw.getScrewType( ) == ScrewType.SCREW_RESURRECT ) {
									ResurrectScrew rScrew = ( ResurrectScrew ) screw;
									rScrew.hitPlayer( player );
								}
							} else if ( p1 != player ) {
								player.hitScrew( screw );
								if ( screw.getScrewType( ) == ScrewType.SCREW_RESURRECT ) {
									ResurrectScrew rScrew = ( ResurrectScrew ) screw;
									rScrew.hitPlayer( player );
								}
							}

							// }
							break;
						case PLAYER:
//							Player player2 = ( Player ) object;
//							player.hitPlayer( player2 );
//							player2.hitPlayer( player );
//							if ( player.getState( ) != PlayerState.HeadStand
//									&& player2.getState( ) != PlayerState.HeadStand
//									&& !player.isHeadStandPossible( )
//									&& !player2.isHeadStandPossible( ) ) {
//								player.hitPlayer( null );
//								player2.hitPlayer( null );
//								contact.setEnabled( false );
//							} else if ( player.getState( ) != PlayerState.HeadStand
//									&& player2.getState( ) != PlayerState.HeadStand ) {
//								player.setGrounded( true );
//								player2.setGrounded( true );
//							}
							break;
						case HAZARD:
						// if ( player.getCurrentScrew( ) == null
						// || player.getCurrentScrew( ).getScrewType( ) !=
						// ScrewType.SCREW_RESURRECT )
						{
							Hazard hazard = ( Hazard ) object;
							hazard.performContact( player, objectFix );
						}
							// Gdx.app.log( "Player " + player.name,
							// " Collided with Hazard" );
							break;
						case CHECKPOINT:
							CheckPoint checkP = ( CheckPoint ) objectFix
									.getBody( ).getUserData( );
							checkP.hitPlayer( );
							break;
						case STEAM:
							player.setSteamCollide( true );
							break;
						case EVENTTRIGGER:
							EventTrigger et = ( EventTrigger ) object;
							et.setActivated( true, player.name );
							if ( playerFix.getShape( ) instanceof CircleShape ) {
								et.triggerBeginEvent( );
							}
							break;
						default:
							break;
						}
					} else {
						Gdx.app.log(
								"please declare your entity with a type to the Entity Type enum",
								"" );
					}
				} else if ( objectFix.getBody( ).getUserData( ) instanceof Anchor ) {
					Anchor anchor = ( Anchor ) objectFix.getBody( )
							.getUserData( );
					if ( !anchor.special )
						anchor.activate( );
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
					if ( player.getEntityType( ) != null
							&& player.getEntityType( ) == EntityType.EVENTTRIGGER ) {
						EventTrigger et = ( EventTrigger ) player;
						// needs to get the action in order to act on just this
						// object
						et.getBeginAction( ).act( object );
					} else if ( object.getEntityType( ) != null
							&& object.getEntityType( ) == EntityType.EVENTTRIGGER ) {
						EventTrigger et = ( EventTrigger ) object;
						// needs to get the action in order to act on just this
						// object
						et.getBeginAction( ).act( player );
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
							if ( object.isSolid( )
									&& playerFix.getShape( ) instanceof CircleShape ) {
								if ( player.name.equals( "player1" ) ) {
									p1 = player;
									NUM_PLAYER1_CONTACTS--;
									if ( NUM_PLAYER1_CONTACTS <= 0 ) {
										if ( player.getState( ) != PlayerState.HeadStand ) {
											player.setGrounded( false );
										}
									}
								} else if ( player.name.equals( "player2" ) ) {
									NUM_PLAYER2_CONTACTS--;
									if ( NUM_PLAYER2_CONTACTS <= 0 ) {
										if ( player.getState( ) != PlayerState.HeadStand ) {
											player.setGrounded( false );
										}
									}
								}
								player.hitSolidObject( null );
								contact.setEnabled( true );

							}
							break;
						case SCREW:
							if ( player.name.equals( "player1" ) ) {
								p1 = player;
								if ( player.getState( ) != PlayerState.Screwing ) {
									player.hitScrew( null );
								}
							} else if ( player.name.equals( "player2" ) ) {
								if ( player.getState( ) != PlayerState.Screwing ) {
									player.hitScrew( null );

								}

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
							player.setSteamCollide( false );
							break;
						case EVENTTRIGGER:
							EventTrigger et = ( EventTrigger ) objectFix
									.getBody( ).getUserData( );
							if ( playerFix.getShape( ) instanceof CircleShape ) {
								et.triggerEndEvent( );
							}
							et.setActivated( false, player.name );
							break;
						default:
							break;
						}
					} else {
						Gdx.app.log(
								"please declare your entity with a type to the Entity Type enum",
								"" );
					}
				} else if ( objectFix.getBody( ).getUserData( ) instanceof Anchor ) {
					Anchor anchor = ( Anchor ) objectFix.getBody( )
							.getUserData( );
					if ( !anchor.special )
						anchor.deactivate( );
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
							if ( player.getState( ) == PlayerState.GrabMode
									|| player2.getState( ) == PlayerState.GrabMode ) {
								contact.setEnabled( false );
							} else {
								player.hitPlayer( player2 );
								player2.hitPlayer( player );
								if ( player.getState( ) != PlayerState.HeadStand
										&& player2.getState( ) != PlayerState.HeadStand
										&& !player.isHeadStandPossible( )
										&& !player2.isHeadStandPossible( ) ) {
									player.hitPlayer( null );
									player2.hitPlayer( null );
									contact.setEnabled( false );
								} else if ( player.getState( ) != PlayerState.HeadStand
										&& player2.getState( ) != PlayerState.HeadStand ) {
									player.setGrounded( true );
									player2.setGrounded( true );
								}
							}
							break;
						default:
							break;
						}
					} else {
						Gdx.app.log(
								"please declare your entity with a type to the Entity Type enum",
								"" );
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
						Gdx.app.log(
								"please declare your entity with a type to the Entity Type enum",
								"" );
					}
				}
			}
		}
	}
}