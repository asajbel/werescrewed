package com.blindtigergames.werescrewed.screws;

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
	}
	
	public void update() {
		if ( depth < 0 ) {
			depth = 0;
		} else if ( depth > maxDepth ) {
			depth = maxDepth;
		}
	}

}
