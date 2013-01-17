package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;

/*******************************************
 * Move a Box2D body
 * @author stew
 *******************************************/
public interface IMover {

	/**
	 * Calculates movement then applies to body
	 * @param Body - The body to apply the movement to
	 */
	void move(Body body);
	
	
	/**
	 * Calculates movement then applies to body
	 * @param Body - The body to apply the movement to
	 * @param SteeringOutput - Optional additional movement to apply to body
	 */
	
	
	void move(Body body, SteeringOutput steering);
}
