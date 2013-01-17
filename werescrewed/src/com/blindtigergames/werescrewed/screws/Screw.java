package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * @descrip: holds general methods for screws
 * 
 * @author Dennis
 *
 */

public class Screw extends Entity {
	public Screw(String n, Vector2 pos, Texture tex){
		super( n, pos, tex , null);
	}
	
	public void update(){
		super.update();
	}
	
	public void remove(){
		world.destroyBody(body);
	}
	
	public void screwLeft(){
		//body.applyAngularImpulse(-100);
		body.setAngularVelocity(15);
		depth--;		
		rotation += 10;
		screwStep = depth + 5;
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
	protected final short CATEGORY_SCREWS = 0x0000;
	protected int screwStep;
	
}