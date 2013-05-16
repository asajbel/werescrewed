package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;

public class RemoveEntityAction implements IAction {

	/**
	 * removes an entity when called by an event trigger
	 */
	public RemoveEntityAction( ) {

	}

	@Override
	public void act( ) {

	}

	/**
	 * will remove this entity box2d data if this entity is the player it will
	 * kill the player
	 */
	@Override
	public void act( Entity entity ) {
		if ( entity.getEntityType( ) != EntityType.PLAYER ) {
			entity.setRemoveNextStep( );
			// Gdx.app.log( "removing Entity", entity.name );
		} else {
			Player player = ( Player ) entity;
			player.killPlayer( );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORANYENTITY;
	}

}
