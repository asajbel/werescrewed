package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;

public class EntityDeactivateMoverAction implements IAction {

	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {
		entity.setActive( false );

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}