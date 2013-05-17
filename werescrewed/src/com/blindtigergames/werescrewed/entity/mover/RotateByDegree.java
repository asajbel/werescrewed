package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class RotateByDegree implements IMover {

	private PuzzleType puzzleType;
	private float startAngle;
	private float endAngle;
	private float onVal;
	private int offVal;

	/**
	 * 
	 * @param startAngle
	 * @param endAngle
	 * @param offValue
	 * @param onValue
	 */
	public RotateByDegree( float startAngle, float endAngle, int offValue,
			float onValue ) {
		puzzleType = PuzzleType.ON_OFF_MOVER;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		if ( offValue >= 1 ) {
			this.offVal = 1;
		} else {
			this.offVal = 0;
		}
		if ( onValue <= 1 && onValue >= 0 && onValue != offVal ) {
			onVal = onValue;
		} else {
			throw new IllegalArgumentException(
					"onValue has to be in the range 0-1 and cannot be equal to the off value" );
		}
	}

	@Override
	public void move( float deltaTime, Body body ) {

	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		if ( offVal == 0 ) {
			if ( screwVal >= onVal ) {
				p.setLocalRot( endAngle * Util.DEG_TO_RAD );
			} else {
				p.setLocalRot( startAngle * Util.DEG_TO_RAD );
			}
		} else {
			if ( screwVal <= onVal ) {
				p.setLocalRot( endAngle * Util.DEG_TO_RAD );
			} else {
				p.setLocalRot( startAngle * Util.DEG_TO_RAD );
			}
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
