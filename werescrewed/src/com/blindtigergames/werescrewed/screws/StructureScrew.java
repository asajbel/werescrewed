package com.blindtigergames.werescrewed.screws;

/**
 * @param name blah blah
 * 
 * @author Dennis
 *
 */

public class StructureScrew extends Screw {
	private int maxDepth;
	public StructureScrew(int max){
		maxDepth = max;
		depth = max;		
	}

	public void update(){
		if ( depth > maxDepth ) {
			depth = maxDepth;
		}
	}
}
