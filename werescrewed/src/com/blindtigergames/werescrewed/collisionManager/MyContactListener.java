package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.Box;

/*
 *
 *
 *
 *
 *
 */

public class MyContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
	    final Fixture x1 = contact.getFixtureA();
	    final Fixture x2 = contact.getFixtureB();
        if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){                   
        	if (x1.getBody().getUserData() instanceof Box){
        		System.out.print("test0");
        	}
            else if(x2.getBody().getUserData() instanceof Box){                                               
        		System.out.print("test1");
            }
        	if (x1.getBody().getUserData() instanceof Player){
        		System.out.print("test1");
        	}
            else if(x2.getBody().getUserData() instanceof Player){                                               
        		System.out.print("test0");
        		x2.getBody().applyLinearImpulse(new Vector2(0.0f, .05f),x2.getBody().getWorldCenter());
        		System.out.print("test0");
            }
        }
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}