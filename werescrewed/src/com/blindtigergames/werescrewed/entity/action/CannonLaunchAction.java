package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.CannonLaunchMover;
import com.blindtigergames.werescrewed.player.Player;

public class CannonLaunchAction implements IAction {

	// IMover moverToRun;
	float impulseStrength;
	float delaySeconds;
	boolean regain_control;
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
		regain_control = false;

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
		if(entity.entityType == EntityType.PLAYER ){
			Player player = (Player) entity;
			player.loseControl( delaySeconds * 2 );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.ACT_ON_PLAYER;
	}
	
	/** sets regain control value for final cannon in sequence
	 * 
	 * @param value boolean
	 */
	public void set_control( boolean value ){
		regain_control = value;
	}

}