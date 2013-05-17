package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;

public interface IAction {

	public void act( );

	public void act( Entity entity );

	public ActionType getActionType( );

}