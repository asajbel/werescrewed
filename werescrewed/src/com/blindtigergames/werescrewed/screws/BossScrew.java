package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * @param name blah blah
 * 
 * @author Dennis
 *
 */

public class BossScrew extends Screw {
	private int maxDepth;
	public BossScrew( int max ){
		maxDepth = max;
		depth = max;		
	}

	public void update(){
		if ( depth > maxDepth ) {
			depth = maxDepth;
		}
		if ( depth <= 0 ) {
			body.setType(BodyType.DynamicBody);
		}
	}
}
