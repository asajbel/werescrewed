package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;

public class EntityActivateMoverAction implements IAction {

	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {
		entity.setActive( true );

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}