package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.CannonLaunchMover;

public class CannonLaunchAction implements IAction {

	// IMover moverToRun;
	float impulseStrength;
	float delaySeconds;
	Skeleton cannon;

	/**
	 * Runs a mover once when this event is triggered
	 * 
	 * @param mover
	 */
	public CannonLaunchAction( Skeleton cannon, float impulseStrength,
			float delaySeconds ) {
		this.impulseStrength = impulseStrength;
		this.delaySeconds = delaySeconds;
		this.cannon = cannon;
	}

	@Override
	public void act( ) {
		// This shouldn't run
	}

	@Override
	public void act( Entity entity ) {
		// Gdx.app.log( "CannonLaunchAction",
		// "Launching in "+delaySeconds+" seconds" );
		entity.setMoverAtCurrentState( new CannonLaunchMover( cannon,
				impulseStrength, delaySeconds ) );
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.ACT_ON_PLAYER;
	}

}