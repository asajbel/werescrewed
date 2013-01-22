package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.StructureScrew;

/*
 *
 *
 *
 *
 *
 */

public class MyContactListener implements ContactListener {

	@Override
    public void beginContact( Contact contact ) {
        final Fixture x1 = contact.getFixtureA();
        final Fixture x2 = contact.getFixtureB();

        Fixture playerFix = null;
        Fixture objectFix = null;

        boolean playerInvolved = false;

        if ( x1.getBody().getUserData() != null
                && x2.getBody().getUserData() != null ) {
            if ( x1.getBody().getUserData() instanceof Player ) {
                playerFix = x1;
                objectFix = x2;
                playerInvolved = true;
            } else if ( x2.getBody().getUserData() instanceof Player ) {
                playerFix = x2;
                objectFix = x1;
                playerInvolved = true;
            }
            if ( playerInvolved ) {
                if ( objectFix.getBody().getUserData() instanceof Box ) {
                    Box example = (Box) objectFix.getBody().getUserData();
                    example.exampleCollide();
                    Player asshole = (Player) playerFix.getBody().getUserData();
                    asshole.jump();
                }
                if ( objectFix.getBody().getUserData() instanceof StructureScrew ) {
                    StructureScrew example = (StructureScrew) objectFix.getBody().getUserData();
                    example.exampleCollide( "begin collision with screw ");
                    Player asshole = (Player) playerFix.getBody().getUserData();
                    asshole.hitScrew( example );
                }
                if ( objectFix.getBody().getUserData() instanceof TiledPlatform ) {
                    TiledPlatform collider = (TiledPlatform) objectFix.getBody().getUserData();
                    Player player = (Player) playerFix.getBody().getUserData();
                    Vector2 platformPos = collider.getPosition();
                    Vector2 playerPos = player.getPosition();
                    if ( platformPos.y < playerPos.y ) {
                        player.setGrounded( true );
                        System.out.println("hey there good looking");
                    }
                }
            }

        }
	}

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
				if ( objectFix.getBody( ).getUserData( ) instanceof TiledPlatform ){
					Player player = ( Player ) playerFix.getBody( ).getUserData( );
					player.setGrounded(false);
					//System.out.println( "not interested" );
					contact.setEnabled( true );
				}
                if ( objectFix.getBody().getUserData() instanceof StructureScrew ) {
                    StructureScrew example = (StructureScrew) objectFix.getBody().getUserData();
                    example.exampleCollide( "end collision with screw ");
                    Player asshole = (Player) playerFix.getBody().getUserData();
                    asshole.endHitScrew( );
                }
			}
		}

	}

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
            if (playerInvolved) {
                if(objectFix.getBody().getUserData() instanceof TiledPlatform){
                    Player player = (Player) playerFix.getBody().getUserData();
                    TiledPlatform oneSidedPlat = (TiledPlatform) objectFix.getBody().getUserData();
                    Vector2 platformPos = oneSidedPlat.getPosition();
                    Vector2 playerPos = player.getPosition();
                    if(platformPos.y > playerPos.y){
                        //System.out.println("setting");
                        contact.setEnabled( false );
                    } 
                }
            }
        }
	}

	@Override
	public void postSolve( Contact contact, ContactImpulse impulse ) {
		// TODO Auto-generated method stub

	}
}