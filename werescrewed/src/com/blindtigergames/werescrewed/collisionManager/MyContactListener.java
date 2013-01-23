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
		System.out.println( "HALP" );
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
			if ( playerFix != null
					&& !( playerFix.getShape( ) instanceof CircleShape ) ) {
				playerInvolved = false;
			}
			if ( playerInvolved ) {
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof Box ) {
					Box example = ( Box ) objectFix.getBody( ).getUserData( );
					example.exampleCollide( );
					player.jump( );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StructureScrew ) {
					StructureScrew example = ( StructureScrew ) objectFix
							.getBody( ).getUserData( );
					example.exampleCollide( "begin collision with screw " );
					player.hitScrew( example );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StrippedScrew ) {
					StrippedScrew example = ( StrippedScrew ) objectFix
							.getBody( ).getUserData( );
					example.exampleCollide( "begin collision with screw " );
					player.hitScrew( example );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof PuzzleScrew ) {
					PuzzleScrew example = ( PuzzleScrew ) objectFix.getBody( )
							.getUserData( );
					example.exampleCollide( "begin collision with screw " );
					player.hitScrew( example );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					TiledPlatform collider = ( TiledPlatform ) objectFix
							.getBody( ).getUserData( );
					Vector2 platformPos = collider.getPosition( );
					Vector2 playerPos = player.getPosition( );
					if ( platformPos.y < playerPos.y ) {
						player.setGrounded( true );
						System.out.println( "hey there good looking" );
					}
				} else {
					System.out.println( "Called" );
				}
			} else {
				System.out.println( "Player not involved, I guess." );
			}
		} else {
			System.out.println( "NULL?!" );
		}
	}

	/**
	 * When two objects stop touching
	 */
	@Override
	public void endContact( Contact contact ) {
		System.out.println( "endContact" );
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
			if ( playerFix != null
					&& !( playerFix.getShape( ) instanceof CircleShape ) ) {
				playerInvolved = false;
			}
			if ( playerInvolved ) {
<<<<<<< HEAD
				Player player = ( Player ) playerFix.getBody( ).getUserData( );
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
					TiledPlatform collider = ( TiledPlatform ) objectFix
							.getBody( ).getUserData( );
					Vector2 platformPos = collider.getPosition( );
					Vector2 playerPos = player.getPosition( );
					if ( platformPos.y < playerPos.y ) {
						player.setGrounded( false );
						System.out.println( "not interested" );
						contact.setEnabled( true );
					}
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StructureScrew ) {
					StructureScrew example = ( StructureScrew ) objectFix
							.getBody( ).getUserData( );
					example.exampleCollide( "end collision with screw " );
					player.endHitScrew( );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof StrippedScrew ) {
					StrippedScrew example = ( StrippedScrew ) objectFix
							.getBody( ).getUserData( );
					example.exampleCollide( "end collision with screw " );
					player.endHitScrew( );
				} else if ( objectFix.getBody( ).getUserData( ) instanceof PuzzleScrew ) {
					PuzzleScrew example = ( PuzzleScrew ) objectFix.getBody( )
							.getUserData( );
					example.exampleCollide( "end collision with screw " );
					player.endHitScrew( );
				}
=======
				// Everything in this if statement are collisions with player
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ){
					Player player = ( Player ) playerFix.getBody( ).getUserData( );
					player.setGrounded(false);
				} else if ( objectFix.getBody( ).getUserData( ) instanceof RoomPlatform ){
					Player player = ( Player ) playerFix.getBody( ).getUserData( );
					player.setGrounded(false);
					RoomPlatform rp = (RoomPlatform) objectFix.getBody().getUserData();
					rp.setOneSided( false );
						
				} else if ( objectFix.getBody().getUserData() instanceof StructureScrew ) {
                    StructureScrew example = (StructureScrew) objectFix.getBody().getUserData();
                    example.exampleCollide( "end collision with screw ");
                    Player asshole = (Player) playerFix.getBody().getUserData();
                    asshole.endHitScrew( );
                    
                } else if ( objectFix.getBody().getUserData() instanceof StrippedScrew ) {
                	StrippedScrew example = (StrippedScrew) objectFix.getBody().getUserData();
                    example.exampleCollide( "end collision with screw ");
                    Player asshole = (Player) playerFix.getBody().getUserData();
                    asshole.endHitScrew( );
                    
                } else if ( objectFix.getBody().getUserData() instanceof PuzzleScrew ) {
                	PuzzleScrew example = (PuzzleScrew) objectFix.getBody().getUserData();
                    example.exampleCollide( "end collision with screw ");
                    Player asshole = (Player) playerFix.getBody().getUserData();
                    asshole.endHitScrew( );
                }
>>>>>>> 75bf473e76066c0c0d934326f6a892575f5dc599
			}
		}

	}

	/**
	 * What to do with each contact before physics is calculated each step
	 */
	@Override
	public void preSolve( Contact contact, Manifold oldManifold ) {
<<<<<<< HEAD
		// final Fixture x1 = contact.getFixtureA( );
		// final Fixture x2 = contact.getFixtureB( );
		//
		// Fixture playerFix = null;
		// Fixture objectFix = null;
		//
		// boolean playerInvolved = false;
		//
		// if ( x1.getBody( ).getUserData( ) != null
		// && x2.getBody( ).getUserData( ) != null ) {
		// if ( x1.getBody( ).getUserData( ) instanceof Player ) {
		// playerFix = x1;
		// objectFix = x2;
		// playerInvolved = true;
		// } else if ( x2.getBody( ).getUserData( ) instanceof Player ) {
		// playerFix = x2;
		// objectFix = x1;
		// playerInvolved = true;
		// }
		// if ( playerInvolved ) {
		// if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ) {
		// Player player = ( Player ) playerFix.getBody( )
		// .getUserData( );
		// TiledPlatform tilePlat = ( TiledPlatform ) objectFix
		// .getBody( ).getUserData( );
		// Vector2 platformPos = tilePlat.getPosition( );
		// Vector2 playerPos = player.getPosition( );
		// if ( tilePlat.getOneSided( ) ) {
		// if ( platformPos.y > playerPos.y ) {
		// // System.out.println("setting");
		// contact.setEnabled( false );
		// }
		// }
		// }
		// }
		// }
=======
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
            if (playerInvolved) {
            	Player player = (Player) playerFix.getBody().getUserData();
                if(objectFix.getBody().getUserData() instanceof TiledPlatform){
                    TiledPlatform tilePlat = (TiledPlatform) objectFix.getBody().getUserData();
                    Vector2 platformPos = tilePlat.getPosition();
                    Vector2 playerPos = player.getPosition();
                    if (tilePlat.getOneSided()){
	                    if(platformPos.y > playerPos.y){
	                        contact.setEnabled( false );
	                    }
                	}
                }
                if(objectFix.getBody().getUserData() instanceof RoomPlatform){
                    RoomPlatform roomPlat = (RoomPlatform) objectFix.getBody().getUserData();
                    Vector2 platformPos = roomPlat.getPosition();
                    Vector2 playerPos = player.getPosition();
                    if (roomPlat.getOneSided()){
	                    if(platformPos.y > playerPos.y){
	                        contact.setEnabled( false );
	                    }
                	}
                }
            }
        }
>>>>>>> 75bf473e76066c0c0d934326f6a892575f5dc599
	}

	/**
	 * What to do with each contact after physics is calculated each step
	 */
	@Override
	public void postSolve( Contact contact, ContactImpulse impulse ) {
		// TODO Auto-generated method stub

	}
}