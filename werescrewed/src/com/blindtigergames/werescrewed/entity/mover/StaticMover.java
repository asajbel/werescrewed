package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

/*****************************************************
 * Doesn't calculate any new movement, but can apply
 * a movement passed in to it's move.
 * @author Stew
 *****************************************************/
public class StaticMover implements IMover {

	@Override
	public void move(float deltaTime, Body body) {
		//Intentionally returns immediately
		return;
	}

	@Override
	public void move(float deltaTime, Body body, SteeringOutput steering) {
		body.setLinearVelocity(steering.velocity);
		body.setAngularVelocity(steering.rotation);
	}
	
	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {

	}
	
	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
