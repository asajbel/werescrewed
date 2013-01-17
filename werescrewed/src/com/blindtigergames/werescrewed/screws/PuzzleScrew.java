package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * @param name blah blah
 * 
 * @author Dennis
 *
 */

public class PuzzleScrew extends Screw {
	private int maxDepth;
	public PuzzleScrew( int max ){
		maxDepth = max;
		depth = max;
		
		//add radar sensor to screw
		CircleShape radarShape = new CircleShape();
		radarShape.setRadius(sprite.getWidth()*2);
	    FixtureDef radarFixture = new FixtureDef();
	    radarFixture.shape = radarShape;
	    radarFixture.isSensor = true;
	    radarFixture.filter.categoryBits = 0x0003; // category of Screw Radar...
	    radarFixture.filter.maskBits = 0x0001;//radar only collides with player (player category bits 0x0001)
		body.createFixture(radarFixture);
	}
	
	public void update() {
		if ( depth < 0 ) {
			depth = 0;
		} else if ( depth > maxDepth ) {
			depth = maxDepth;
		}
	}

}
