package com.blindtigergames.werescrewed.entity.mover;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.Platform;

/**
 * Rotate a kinematic platform or add arbitrary rotation amounts
 * @author stew
 *
 */
public class RotateTweenMover extends TweenMover implements IMover {
	
	PuzzleType type;
	float duration;
	float rotAmount;
	
	public RotateTweenMover( PuzzleType type, float duration ) {
		super();
	}
	
	public RotateTweenMover( ){
		
	}
	
	@Override
	public void move( float deltaTime, Body body ) {
		//It shouldn't

	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		p.mover = this;
		addTween(Tween.to( p, PlatformAccessor.LOCAL_ROT, duration ));
		//NOT DONE
		
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

}
