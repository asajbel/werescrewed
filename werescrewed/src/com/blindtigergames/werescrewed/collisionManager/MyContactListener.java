package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
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

			// Ensure the collision involves the player's feet
			if ( playerInvolved
					&& !( playerFix.getShape( ) instanceof CircleShape ) ) {
				playerInvolved = false;
			}

			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( object.isSolid( ) ) {
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
						player.hitScrew( screw );
					}
				} else if ( objectFix.getBody( ).getUserData( ) instanceof Anchor ) {
					Anchor anchor = ( Anchor ) objectFix.getBody( )
							.getUserData( );
					if ( !anchor.special )
						anchor.activate( );
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
			// Ensure the collision involves the player's feet
			if ( playerInvolved
					&& !( playerFix.getShape( ) instanceof CircleShape ) ) {
				playerInvolved = false;
			}
			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( object.isSolid( ) ) {
						if ( p1 == null || p1 == player ) {
							p1 = player;
							NUM_PLAYER1_CONTACTS--;
							if ( NUM_PLAYER1_CONTACTS <= 0 ) {
								player.setGrounded( false );
							}
						} else if ( p1 != player ) {
							NUM_PLAYER2_CONTACTS--;
							if ( NUM_PLAYER2_CONTACTS <= 0 ) {
								player.setGrounded( false );
							}
						}
						contact.setEnabled( true );
					} else if ( objectFix.getBody( ).getUserData( ) instanceof Screw ) {
						player.endHitScrew( );
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