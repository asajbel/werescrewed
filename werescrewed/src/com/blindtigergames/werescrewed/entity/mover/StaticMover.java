package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;

/*****************************************************
 * StaticMover.java
 * This class doesn't apply any movement to a body,
 * rather it exists for code clarity.
 * @author Stew
 *****************************************************/

public class StaticMover implements IMover {

	@Override
	public void move(Body body) {
		return;
	}

	@Override
	public void move(Body body, SteeringOutput steering) {
		// TODO Auto-generated method stub
		
	}
	
}
