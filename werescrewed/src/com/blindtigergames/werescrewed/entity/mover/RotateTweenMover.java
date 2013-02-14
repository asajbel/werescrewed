package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * Rotate a kinematic platform or add arbitrary rotation amounts
 * @author stew
 *
 */
public class RotateTweenMover extends TweenMover implements IMover {

	Entity entityToBeMoved;
	
	public RotateTweenMover( Entity entityToBeMoved ) {
		super();
	}
	
	public RotateTweenMover(Entity entityToBeMoved ){
		
	}
	
	@Override
	public void move( float deltaTime, Body body ) {
		//It shouldn't

	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		// TODO Auto-generated method stub

	}

}
