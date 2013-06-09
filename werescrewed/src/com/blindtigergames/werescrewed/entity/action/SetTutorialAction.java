package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;

public class SetTutorialAction implements IAction {

	int first;
	int last;
	boolean value;

	public SetTutorialAction( int startSequence, int endSequence, boolean newValue ) {
		first = startSequence;
		last = endSequence;
		value = newValue;
	}

	@Override
	public void act( ) {
		//Gdx.app.log("not_working: ", "" + value);
	}

	@Override
	public void act( Entity entity ) {
		//Gdx.app.log("working: ", "" + value);
		if ( entity.entityType == EntityType.PLAYER ) {
			Player player = ( Player ) entity;
			if ( value ) {
				player.setTutorial( first, last );
				player.setDrawTutorial( true );
			} else {
				player.setDrawTutorial( false );
			}
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.ACT_ON_PLAYER;
	}

}