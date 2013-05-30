package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;

public class SetRobotStateAction implements IAction {

	RobotState state;
	
	public SetRobotStateAction(RobotState state){
		this.state = state;
	}
	
	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {
		entity.setCurrentMover( state );

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}