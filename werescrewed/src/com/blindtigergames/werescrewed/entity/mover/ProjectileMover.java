package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

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
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {

	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
