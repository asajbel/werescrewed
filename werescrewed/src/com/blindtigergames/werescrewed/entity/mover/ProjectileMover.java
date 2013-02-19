package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;

public class ProjectileMover implements IMover {
	public ProjectileMover(){
		
	}
	
	@Override
	public void move( float deltaTime, Body body ) {
	}
	
	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		move( deltaTime, body );
	}


	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {

	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
