package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StructureScrew;

/**
 * 
 * Used for collision handling
 * 
 */
public class MyContactListener implements ContactListener {

	private static int NUM_PLAYER1_CONTACTS = 0;
	private static int NUM_PLAYER2_CONTACTS = 0;
	private static int NUM_PLAYER1_SCREWCONTACTS = 0;
	private static int NUM_PLAYER2_SCREWCONTACTS = 0;
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
			}

			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					// Ensure the object is solid and involves the player's feet
					// also make sure its not the player
					if ( object.isSolid( )
							&& playerFix.getShape( ) instanceof CircleShape
							&& !( objectFix.getBody( ).getUserData( ) instanceof Player ) ) {
						if ( p1 == null || p1 == player ) {
							p1 = player;
							NUM_PLAYER1_CONTACTS++;
						} else if ( p1 != player ) {
							NUM_PLAYER2_CONTACTS++;
						}
						player.setGrounded( true );
					} else if ( objectFix.getBody( ).getUserData( ) instanceof Screw ) {
						Screw screw = ( Screw ) objectFix.getBody( )
								.getUserData( );
						if ( p1 == null || p1 == player ) {
							p1 = player;
							NUM_PLAYER1_SCREWCONTACTS++;
							player.hitScrew( screw );
						} else if ( p1 != player ) {
							NUM_PLAYER2_SCREWCONTACTS++;
							player.hitScrew( screw );
						}
					} else if ( objectFix.getBody( ).getUserData( ) instanceof Player ) {
						Player player2 = ( Player ) objectFix.getBody( )
								.getUserData( );
						player.hitPlayer( player2 );
						player2.hitPlayer( player );
					} else if ( objectFix.getBody( ).getUserData( ) instanceof Anchor ) {
						Anchor anchor = ( Anchor ) objectFix.getBody( )
								.getUserData( );
						if ( !anchor.special )
							anchor.activate( );
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
					// Ensure the object is solid and involves the player's feet
					// also make sure its not the player
					if ( object.isSolid( )
							&& playerFix.getShape( ) instanceof CircleShape
							&& !( objectFix.getBody( ).getUserData( ) instanceof Player ) ) {
						if ( p1 == null || p1 == player ) {
							p1 = player;
							NUM_PLAYER1_CONTACTS--;
							if ( NUM_PLAYER1_CONTACTS <= 0 ) {
								if ( player.getState( ) == PlayerState.Falling ) {
									player.setGrounded( false );
								}
							}
						} else if ( p1 != player ) {
							NUM_PLAYER2_CONTACTS--;
							if ( NUM_PLAYER2_CONTACTS <= 0 ) {
								if ( player.getState( ) == PlayerState.Falling ) {
									player.setGrounded( false );
								}
							}
						}
						contact.setEnabled( true );
					} else if ( objectFix.getBody( ).getUserData( ) instanceof Screw ) {
						if ( p1 == null || p1 == player ) {
							p1 = player;
							NUM_PLAYER1_SCREWCONTACTS--;
							if ( NUM_PLAYER1_SCREWCONTACTS <= 0 ) {
								if ( player.getState( ) != PlayerState.Screwing ) {
									player.hitScrew( null );
								}
							}
						} else if ( p1 != player ) {
							NUM_PLAYER2_SCREWCONTACTS--;
							if ( NUM_PLAYER2_SCREWCONTACTS <= 0 ) {
								if ( player.getState( ) != PlayerState.Screwing ) {
									player.hitScrew( null );
								}
							}
						}
					} else if ( objectFix.getBody( ).getUserData( ) instanceof Player ) {
						Player player2 = ( Player ) objectFix.getBody( )
								.getUserData( );
						if ( player.getState( ) != PlayerState.HeadStand ) {
							player.hitPlayer( null );
							player2.hitPlayer( null );
						}
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
				if ( objectFix.getBody( ).getUserData( ) instanceof Platform ) {
					if ( objectFix.getBody( ).getType( ) == BodyType.KinematicBody) {
						Platform plat = (Platform) objectFix.getBody( ).getUserData( );
						if(plat.mover != null){
							player.setMovingPlatformFlag( true );
							player.setOffset( plat.getChangePosition( ) );
							//Gdx.app.log( "x: " + plat.getChangePosition( ).x, "y: " + plat.getChangePosition( ).y );
						}
					}
				}
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					TiledPlatform tilePlat = ( TiledPlatform ) objectFix
							.getBody( ).getUserData( );
					boolean isScrew = false;
					for ( JointEdge j : tilePlat.body.getJointList( ) ) {
						if ( j.joint.getBodyB( ).getUserData( ) instanceof StructureScrew ) {
							isScrew = true;
						}
					}
					if ( !isScrew ) {
						player.maxFriction( );
						tilePlat.body.getFixtureList( ).get( 0 )
								.setFriction( 1f );
					} else {
						player.noFriction( );
					}
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
				} else if ( objectFix.getBody( ).getUserData( ) instanceof Player ) {
					Player player2 = ( Player ) objectFix.getBody( )
							.getUserData( );
					if ( player.getState( ) == PlayerState.GrabMode
							|| player2.getState( ) == PlayerState.GrabMode ) {
						contact.setEnabled( false );
					} else if ( ( !player.isGrounded( ) || !player2
							.isGrounded( ) )
							&& player.getState( ) != PlayerState.Falling
							&& player2.getState( ) != PlayerState.Falling ) {
						contact.setEnabled( false );
					}
				}
			}
		}
	}

	/**
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
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					TiledPlatform tilePlat = ( TiledPlatform ) objectFix
							.getBody( ).getUserData( );
					boolean isScrew = false;
					for ( JointEdge j : tilePlat.body.getJointList( ) ) {
						if ( j.joint.getBodyB( ).getUserData( ) instanceof StructureScrew ) {
							isScrew = true;
						}
					}
					if ( !isScrew ) {
						player.maxFriction( );
						tilePlat.body.getFixtureList( ).get( 0 )
								.setFriction( 1f );
					} else {
						player.noFriction( );
					}
					Vector2 platformPos = tilePlat.getPosition( );
					Vector2 playerPos = player.getPosition( );
					if ( tilePlat.getOneSided( ) ) {
						if ( platformPos.y > playerPos.y ) {
							contact.setEnabled( false );
						}
					}
				}
			}
		}
	}
}