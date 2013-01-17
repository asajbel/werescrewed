package com.blindtigergames.werescrewed.entity.mover;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
	/**
	 * Simple 2d kinematic movement storage class.
	 * 
	 * @param velocity - Vector2 storage for velocity / linear impulse
	 * @param rotation - float storage for angular velocity / angular impulse
	 * 
	 * @author stew
	 *
	 */
public class SteeringOutput {

	public Vector2 velocity; //linear velocity
	public float rotation;  //angular velocity
	
	public SteeringOutput(Vector2 _velocity, float _rotation){
		this.velocity = _velocity;
		this.rotation = _rotation;
	}
	
	public SteeringOutput add(SteeringOutput that){
		velocity.add(that.velocity);
		rotation += that.rotation;
		return this;
	}
	
	public SteeringOutput add(ArrayList<SteeringOutput> them){
		for ( SteeringOutput s : them ){
			velocity.add(s.velocity);
			rotation += s.rotation;
		}
		return this;
	}
}
