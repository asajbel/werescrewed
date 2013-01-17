package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;

/*******************************************************************************
 * IMover Interface -
 * Move a Box2D body
 *
 ******************************************************************************/
public interface IMover {

	/**
	 * 
	 * Calculates movement then applies
	 *  a movement to a Box2D body.
	 */
	void move(Body body);
	
	void move(Body body, SteeringOutput steering);
}
