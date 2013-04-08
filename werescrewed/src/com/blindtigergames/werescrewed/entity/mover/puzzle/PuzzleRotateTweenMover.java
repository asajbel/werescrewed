package com.blindtigergames.werescrewed.entity.mover.puzzle;

import aurelienribon.tweenengine.Tween;

import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.TweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;

/**
 * Rotate a kinematic platform INDEFINETLY. this is a set and ignore kind of
 * mover
 * 
 * @author stew
 * 
 */
public class PuzzleRotateTweenMover extends TweenMover implements IMover {

	private float duration;
	private float rotAmount;
	private boolean isYoyoRepeat;
	private PuzzleType puzzleType;
	private boolean isUp; // used for yoyo repeat, to go back and forth
	private boolean isAtMaxScrewValue;

	/**
	 * Rotate a platform. Use this if you want something to rotate indefinetly.
	 * 
	 * @param duration
	 *            - float, How long it takes to rotate
	 * @param rotAmountRadians
	 *            - float, how much rotation applied in radian. use 2PI for a
	 *            full circle
	 * @param isYoyoRepeat
	 *            - boolean true for looping back and forth or false for looping
	 *            and jumping to beginning. Use yoyo for not full rotations
	 */
	public PuzzleRotateTweenMover( float duration, float rotAmountRadians,
			boolean isYoyoRepeat, PuzzleType ptype ) {
		super( false );
		puzzleType = ptype;
		this.duration = duration;
		this.rotAmount = rotAmountRadians;
		this.isYoyoRepeat = isYoyoRepeat;
		this.isUp = false;
		this.isAtMaxScrewValue = false;
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		if ( puzzleType == PuzzleType.ON_OFF_MOVER ) {
			if ( p.currentMover( ) == null && hasNoTweens( ) ) {
				if ( ( screwVal >= 0.9 && !isAtMaxScrewValue )
						|| ( screwVal <= 0.1 && isAtMaxScrewValue ) ) {
					isAtMaxScrewValue = !isAtMaxScrewValue;
					p.setMoverAtCurrentState( this );
					float targetRotation = p.getLocalRot( );
					if ( isYoyoRepeat ) {
						targetRotation += rotAmount * ( ( isUp ) ? -1f : 1f );
						isUp = !isUp;
					} else {
						targetRotation += rotAmount;
					}
					addTween( Tween
							.to( p, PlatformAccessor.LOCAL_ROT, duration )
							.target( targetRotation ).start( ) );
				}
			} else if ( hasNoTweens( ) ) {
				p.setMoverNullAtCurrentState( );
			}
		} else if ( puzzleType == PuzzleType.PUZZLE_SCREW_CONTROL ) {
			if ( p.currentMover( ) == null && hasNoTweens( ) ) {
				isAtMaxScrewValue = !isAtMaxScrewValue;
				p.setMoverAtCurrentState( this );
				float targetRotation = p.getLocalRot( );
				if ( isYoyoRepeat ) {
					targetRotation += rotAmount * ( ( isUp ) ? -1f : 1f );
					isUp = !isUp;
				} else {
					targetRotation += rotAmount;
				}
				addTween( Tween.to( p, PlatformAccessor.LOCAL_ROT, duration )
						.target( targetRotation ).start( ) );
			} else if ( hasNoTweens( ) ) {
				p.setMoverNullAtCurrentState( );
			}
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}

}
