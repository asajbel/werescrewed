package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

public class AnalogRotateMover implements IMover {

	private PuzzleType puzzleType;
	private boolean clockWise = false;
	private float speedSteps = 0;
	private float speed;
	private float lastVal = 0;
	
	/**
	 * 
	 * @param startAngle
	 * @param endAngle
	 * @param offValue
	 * @param onValue
	 */
	public AnalogRotateMover( float speed ) {
		this.speed = speed;
		puzzleType = PuzzleType.PUZZLE_SCREW_CONTROL;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		if ( speedSteps > 0 ) {
			speedSteps--;		
			if ( clockWise ) {
				body.setAngularVelocity( speed );
			} else {
				body.setAngularVelocity( -speed );
			}
		} else {
			body.setAngularVelocity( 0 );
		}
	}

	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		if ( screwVal > lastVal ) {
			clockWise = true;
		} else {
			clockWise = false;
		}
		lastVal = screwVal;
		speedSteps = 10;
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
