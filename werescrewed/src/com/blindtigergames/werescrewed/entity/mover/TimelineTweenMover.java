package com.blindtigergames.werescrewed.entity.mover;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;

public class TimelineTweenMover implements IMover {

	Timeline timeline;
	
	public TimelineTweenMover(Timeline timeline){
		this.timeline = timeline;
	}
	
	@Override
	public void move( float deltaTime, Body body ) {
		timeline.update( deltaTime );
	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

}
