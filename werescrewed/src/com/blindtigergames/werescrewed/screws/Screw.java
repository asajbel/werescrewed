package com.blindtigergames.werescrewed.screws;

import com.blindtigergames.werescrewed.entity.Entity;

public class Screw extends Entity {
	private int rotation;
	private int depth;
	
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
	
	
	
}
