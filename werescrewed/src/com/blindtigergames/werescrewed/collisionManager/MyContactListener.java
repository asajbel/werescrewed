package com.blindtigergames.werescrewed.collisionManager;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.screws.StructureScrew;

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
	    
	    Fixture playerFix = null;
	    Fixture objectFix = null;
	    
        if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
        	if (x1.getBody().getUserData() instanceof Player){
        		playerFix = x1;
        		objectFix = x2;
        	}
            else if (x2.getBody().getUserData() instanceof Player){                                   
            	playerFix = x2;
            	objectFix = x1;
            }
        	if (objectFix.getBody().getUserData() instanceof Box){
        		Box example = (Box) objectFix.getBody().getUserData();
        		example.exampleCollide();
        		Player asshole = (Player) playerFix.getBody().getUserData();
        		asshole.jump();
        	}
        	if (objectFix.getBody().getUserData() instanceof StructureScrew){
        		StructureScrew example = (StructureScrew) objectFix.getBody().getUserData();
        		example.exampleCollide();
        		Player asshole = (Player) playerFix.getBody().getUserData();
        		asshole.hitScrew (example);
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