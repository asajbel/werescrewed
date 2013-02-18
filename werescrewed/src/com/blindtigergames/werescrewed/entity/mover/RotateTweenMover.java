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
public class RotateTweenMover extends TweenMover implements IMover {

	private float duration;
	private float rotAmount;
	private float delay;
	private boolean isYoyoRepeat;

	/**
	 * Rotate a platform. Use this if you want something to rotate indefinetly.
	 * @param duration - float, How long it takes to rotate
	 * @param rotAmountRadians - float, how much rotation applied in radian. use 2PI for a full circle
	 * @param isYoyoRepeat - boolean true for looping back and forth or false for looping and jumping to beginning. Use yoyo for not full rotations
	 */
	public RotateTweenMover( Platform platform, float duration, float rotAmountRadians, float delay, boolean isYoyoRepeat ) {
		super(true);
		this.duration = duration;
		this.rotAmount = rotAmountRadians;
		this.delay = delay;
		this.isYoyoRepeat = isYoyoRepeat;
		if ( isYoyoRepeat ){
			addTween( Tween.to( platform, PlatformAccessor.LOCAL_ROT, duration )
						   .ease(TweenEquations.easeNone) //no ease for smooth lerp
						   .target( rotAmountRadians )
						   .repeatYoyo( Tween.INFINITY, delay ).start()
						   );
		}else{
			addTween( Tween.to( platform, PlatformAccessor.LOCAL_ROT, duration )
						.ease(TweenEquations.easeNone) //no ease for smooth lerp
					   .target( rotAmountRadians )
					   .repeat( Tween.INFINITY, delay ).start()
					   );
		}
	}
	
	/**
	 * Will rotate the platform indefinetly in a full circle for a default time
	 */
	public RotateTweenMover( Platform platform ){
		this(platform,10f,Util.TWO_PI,0f,false);
	}

	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		//p.mover = this;
		//addTween(Tween.to( p, PlatformAccessor.LOCAL_ROT, duration ));
		//NOT DONE
		
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

}