package com.blindtigergames.werescrewed.screws;

import com.blindtigergames.werescrewed.entity.Entity;

/**
 * @param name blah blah
 * 
 * @author Dennis
 *
 */

public class Screw extends Entity {
	public void update(){
		
	}
	
	public void remove(){
		
	}
	
	public void screwLeft(){
		depth--;		
	}
	
	public void screwRight(){
		depth++;
	}
	
	public int getRotation(){
		return rotation;
	}
	
	public int getDepth(){
		return depth;
	}
	
	protected int rotation;
	protected int depth;
	
}
