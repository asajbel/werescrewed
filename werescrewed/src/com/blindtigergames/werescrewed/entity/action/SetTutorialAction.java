package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;

public class SetTutorialAction implements IAction {

	int[ ] sequence;
	boolean value;

	public SetTutorialAction( int[ ] tutorialSequence, boolean newValue ) {
		sequence = tutorialSequence;
		value = newValue;
	}

	@Override
	public void act( ) {

	}

	@Override
	public void act( Entity entity ) {
		if ( entity.entityType == EntityType.PLAYER ) {
			Player player = ( Player ) entity;
			if ( value ) {
				player.setTutorial( sequence );
				player.setDrawTutorial( true );
			} else {
				player.setDrawTutorial( false );
			}
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}