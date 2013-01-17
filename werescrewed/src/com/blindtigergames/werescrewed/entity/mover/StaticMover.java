package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;

/*****************************************************
 * Doesn't calculate any new movement, but can apply
 * a movement passed in to it's move.
 * @author Stew
 *****************************************************/
public class StaticMover implements IMover {

	@Override
	public void move(Body body) {
		//Intentionally returns immediately
		return;
	}

	@Override
	public void move(Body body, SteeringOutput steering) {
		body.setLinearVelocity(steering.velocity);
		body.setAngularVelocity(steering.rotation);
	}
	
}
