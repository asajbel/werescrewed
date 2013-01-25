package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;

/**
 * 
 * Used for collision handling
 * 
 */
public class MyContactListener implements ContactListener {

	/**
	 * When two new objects start to touch
	 */
	@Override
	public void beginContact( Contact contact ) {
		// System.out.println( "beginContact" );
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
				if ( objectFix.getBody( ).getUserData( ) instanceof Box ) {
					// System.out.print( "Box collision start. " );
					// Box box = ( Box ) objectFix.getBody( ).getUserData( );
					// box.exampleCollide( );
					// Vector2 boxPos = box.getPosition( );
					// Vector2 playerPos = player.getPosition( );
					// if ( boxPos.y < playerPos.y ) {
					// System.out.println( "Grounded." );
					player.setGrounded( true );
					// } else {
					// System.out.println( "Not grounded." );
					// }
					// player.jump( );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StructureScrew ) {
					StructureScrew example = ( StructureScrew ) objectFix
							.getBody( ).getUserData( );
					// example.exampleCollide( "StructureScrew collision start."
					// );
					player.hitScrew( example );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StrippedScrew ) {
					StrippedScrew example = ( StrippedScrew ) objectFix
							.getBody( ).getUserData( );
					// example.exampleCollide( "StrippedScrew collision start."
					// );
					player.hitScrew( example );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof PuzzleScrew ) {
					PuzzleScrew example = ( PuzzleScrew ) objectFix.getBody( )
							.getUserData( );
					// example.exampleCollide( "PuzzleScrew collision start." );
					player.hitScrew( example );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					// System.out.print( "TiledPlatform collision start. " );
					// TiledPlatform collider = ( TiledPlatform ) objectFix
					// .getBody( ).getUserData( );
					// Vector2 platformPos = collider.getPosition( );
					// Vector2 playerPos = player.getPosition( );
					// if ( platformPos.y < playerPos.y ) {
					// System.out.println( "Grounded." );
					player.setGrounded( true );
					// System.out.println( "hey there good looking" );
					// } else {
					// System.out.println( "Not grounded." );
					// }
				} else {
					// System.out.println( "Unknown collision start." );
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
		// System.out.println( "endContact" );
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
				// System.out.println( "Body collision end." );
				playerInvolved = false;
			}
			if ( playerInvolved ) {
				// System.out.print( "Player - " );
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					// System.out.print( "TiledPlatform collision end. " );
					TiledPlatform collider = ( TiledPlatform ) objectFix
							.getBody( ).getUserData( );
					Vector2 platformPos = collider.getPosition( );
					Vector2 playerPos = player.getPosition( );
					// if ( platformPos.y < playerPos.y ) {
					player.setGrounded( false );
					// System.out.println( "Not grounded." );
					contact.setEnabled( true );
					// }
				} else if ( objectFix.getBody( ).getUserData( ) instanceof RoomPlatform ) {
					// System.out
					// .println( "RoomPlatform collision end. Not grounded." );
					player.setGrounded( false );
					RoomPlatform rp = ( RoomPlatform ) objectFix.getBody( )
							.getUserData( );
					rp.setOneSided( false );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StructureScrew ) {
					StructureScrew example = ( StructureScrew ) objectFix
							.getBody( ).getUserData( );
					// example.exampleCollide( "StructureScrew collision end."
					// );
					player.endHitScrew( );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StrippedScrew ) {
					StrippedScrew example = ( StrippedScrew ) objectFix
							.getBody( ).getUserData( );
					// example.exampleCollide( "StrippedScrew collision end." );
					player.endHitScrew( );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof PuzzleScrew ) {
					PuzzleScrew example = ( PuzzleScrew ) objectFix.getBody( )
							.getUserData( );
					// example.exampleCollide( "PuzzleScrew collision end." );
					player.endHitScrew( );
				} else {
					// System.out.println( "Unknown collision end." );
				}
			} else {
				// System.out.println( "Non-player collision end." );
			}
		} else {
			// System.out.println( "Null end." );
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
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					TiledPlatform tilePlat = ( TiledPlatform ) objectFix
							.getBody( ).getUserData( );
					Vector2 platformPos = tilePlat.getPosition( );
					Vector2 playerPos = player.getPosition( );
					if ( tilePlat.getOneSided( ) ) {
						if ( platformPos.y > playerPos.y ) {
							contact.setEnabled( false );
						}
					}
				}
				if ( objectFix.getBody( ).getUserData( ) instanceof RoomPlatform ) {
					RoomPlatform roomPlat = ( RoomPlatform ) objectFix
							.getBody( ).getUserData( );
					Vector2 platformPos = roomPlat.getPosition( );
					Vector2 playerPos = player.getPosition( );
					if ( roomPlat.getOneSided( ) ) {
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