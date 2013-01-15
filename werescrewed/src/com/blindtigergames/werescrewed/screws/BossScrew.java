package com.blindtigergames.werescrewed.screws;

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
	}
}
