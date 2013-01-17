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
		world.destroyBody(body);
	}
	
	public void screwLeft(){
		body.applyAngularImpulse(-1);
		depth--;		
	}
	
	public void screwRight(){
		body.applyAngularImpulse(1);
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
