package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

/*****************************************************
 * Doesn't calculate any new movement, but can apply a movement passed in to
 * it's move.
 * 
 * @author Stew
 *****************************************************/
public class RockingMover implements IMover {

	protected float time;
	public float max;
	public float speed;

	public RockingMover( float m, float s ) {
		time = 0.0f;
		max = m;
		speed = s;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		time += deltaTime;
		body.setAngularVelocity( ( float ) ( Math.sin( time * speed ) ) * max );
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {

	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
