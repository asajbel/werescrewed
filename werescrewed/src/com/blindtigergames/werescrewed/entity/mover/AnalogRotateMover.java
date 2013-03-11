package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.util.Util;

public class AnalogRotateMover implements IMover {

	private PuzzleType puzzleType;
	private float speed;
	private float direction;
	
	/**
	 * 
	 * @param startAngle
	 * @param endAngle
	 * @param offValue
	 * @param onValue
	 */
	public AnalogRotateMover( float speed) {
		this.speed = speed;
		puzzleType = PuzzleType.PUZZLE_SCREW_CONTROL;
	}

	@Override
	public void move( float deltaTime, Body body ) {

	}

	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		if ( screwVal < direction ) {
			p.setMoverNullAtCurrentState( );
			p.body.setTransform( p.body.getPosition( ), (p.body.getAngle( )*Util.DEG_TO_RAD - speed)*Util.DEG_TO_RAD );
		} else {
			p.setMoverNullAtCurrentState( );
			p.body.setTransform( p.body.getPosition( ), (p.body.getAngle( )*Util.DEG_TO_RAD + speed)*Util.DEG_TO_RAD );
		}
		direction = screwVal;
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
