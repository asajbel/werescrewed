package com.blindtigergames.werescrewed.entity.action;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;

public class RotateTweenAction implements IAction{

	float angle;
	
	public RotateTweenAction(float angle){
		this.angle = angle;
	}
	
	@Override
	public void act( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Entity entity ) {
		Timeline t = Timeline.createSequence( );
		t.push( Tween
				.to( entity, PlatformAccessor.LOCAL_ROT,
						1f ).ease( TweenEquations.easeInOutQuad )
				.target( ( angle ) ).delay( 0f ).start( ) );

		entity.addMover( new TimelineTweenMover( t.start( ) ) );
		
	}

	@Override
	public ActionType getActionType( ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}