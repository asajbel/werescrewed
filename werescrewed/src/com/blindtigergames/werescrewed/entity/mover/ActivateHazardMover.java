package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.hazard.Hazard;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

public class ActivateHazardMover implements IMover{

	@Override
	public void move( float deltaTime, Body body ) {
		Hazard h = (Hazard) body.getUserData( );
		if(!h.isActive( ))
			h.setActive( true );
		
	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}
	
}