package com.blindtigergames.werescrewed.entity.mover;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

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
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		// TODO Auto-generated method stub
		p.setMoverAtCurrentState( this );
		if (screwVal > 0.0f){
			p.setActive( true );
		} else {
			p.setActive( false );
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

}
