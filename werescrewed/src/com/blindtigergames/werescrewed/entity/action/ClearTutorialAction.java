package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;

public class ClearTutorialAction implements IAction {

	public ClearTutorialAction( ) {
	}

	@Override
	public void act( ) {

	}

	@Override
	public void act( Entity entity ) {
		if ( entity.entityType != EntityType.PLAYER ) {
			Player player = ( Player ) entity;
			player.clearTutorial( );
			player.setDrawTutorial( false );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}