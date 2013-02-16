package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;

/*****************************************************
 * Doesn't calculate any new movement, but can apply
 * a movement passed in to it's move.
 * @author Stew
 *****************************************************/
public class RockingMover implements IMover {

	protected float time;
	public float max;
	public float speed;

	public RockingMover(float m, float s){
		time = 0.0f;
		max = m;
		speed = s;
	}
	
	@Override
	public void move(float deltaTime, Body body) {
		time += deltaTime;
		body.setAngularVelocity( (float)(Math.sin( time * speed))*max );
	}

	@Override
	public void move(float deltaTime, Body body, SteeringOutput steering) {
		body.setLinearVelocity(steering.velocity);
		body.setAngularVelocity(steering.rotation);
	}
	
	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {

	}
	
	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
