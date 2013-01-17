package com.blindtigergames.werescrewed.entity.mover;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class TimelineMover implements IMover {

	//kinematic can't collide with kinematic or static bodies
	//kinematic bodies have infinite mass
	
	SteeringOutput movement;
	
	public TimelineMover(){
		movement = new SteeringOutput(new Vector2(0.01f,0f), 0);
	}
	
	@Override
	public void move(Body body) {
		body.setAngularVelocity(movement.rotation);
		body.setLinearVelocity(movement.velocity);
	}

	@Override
	public void move(Body body, SteeringOutput steering) {
		// TODO Auto-generated method stub
		
	}

}
