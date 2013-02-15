package com.blindtigergames.werescrewed.entity.mover;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Rotate a kinematic platform INDEFINETLY. this is a set and ignore kind of mover
 * @author stew
 *
 */
public class PuzzleRotateTweenMover extends TweenMover implements IMover {

	
	private float duration;
	private float rotAmount;
	private boolean isYoyoRepeat;
	private PuzzleType puzzleType;
	private boolean isUp; //used for yoyo repeat, to go back and forth

	/**
	 * Rotate a platform. Use this if you want something to rotate indefinetly.
	 * @param duration - float, How long it takes to rotate
	 * @param rotAmountRadians - float, how much rotation applied in radian. use 2PI for a full circle
	 * @param isYoyoRepeat - boolean true for looping back and forth or false for looping and jumping to beginning. Use yoyo for not full rotations
	 */
	public PuzzleRotateTweenMover( float duration, float rotAmountRadians, boolean isYoyoRepeat ) {
		super(false);
		puzzleType = PuzzleType.ON_OFF_MOVER;
		this.duration = duration;
		this.rotAmount = rotAmountRadians;
		this.isYoyoRepeat = isYoyoRepeat;
		this.isUp = false;
	}
	
	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		p.mover = this;
		System.out.println( "yo!" );
		if( screwVal >= 0.5f ){
			float targetRotation = p.getLocalRot( );
			if ( isYoyoRepeat ){
				targetRotation += rotAmount * ((isUp)?-1f:1f);
				isUp = !isUp;
			}else{
				targetRotation += rotAmount;
			}
			addTween(Tween.to( p, PlatformAccessor.LOCAL_ROT, duration )
						  .target( targetRotation ).start());
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}

}
