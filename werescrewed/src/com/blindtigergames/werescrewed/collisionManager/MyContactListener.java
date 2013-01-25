package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;

/**
 * 
 * Used for collision handling
 * 
 */
public class MyContactListener implements ContactListener {

	private static int NUM_PLAYER_CONTACTS = 0;

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
				// System.out.println( "Body collision start." );
				playerInvolved = false;
			}

			if ( playerInvolved ) {
				// System.out.print( "Player - " );
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Entity ) {
					Entity object = ( Entity ) objectFix.getBody( )
							.getUserData( );
					if ( object.isSolid( ) ) {
						NUM_PLAYER_CONTACTS++;
						System.out.println( NUM_PLAYER_CONTACTS );
						player.setGrounded( true );
					} else if ( object instanceof Screw ) {
						Screw screw = ( Screw ) object;
						player.hitScrew( screw );
					}
				}
			} else {
				// System.out.println( "Non-player collision start." );
			}
		} else {
			// System.out.println( "Null" );
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
						NUM_PLAYER_CONTACTS--;
						if ( NUM_PLAYER_CONTACTS <= 0 ) {
							player.setGrounded( false );
						}
					} else if ( object instanceof Screw ) {
						player.endHitScrew( );
					}
				}
			}
		}
	}

	/**
	 * What to do with each contact before physics is calculated each step
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
					Platform tilePlat = ( Platform ) objectFix.getBody( )
							.getUserData( );
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
	 * What to do with each contact after physics is calculated each step
	 */
	@Override
	public void postSolve( Contact contact, ContactImpulse impulse ) {
		// TODO Auto-generated method stub

	}
}