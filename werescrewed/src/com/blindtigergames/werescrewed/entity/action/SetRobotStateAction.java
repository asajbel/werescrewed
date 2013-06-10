package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;

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
		IMover current = entity.currentMover( );
		if(current instanceof TimelineTweenMover){
			TimelineTweenMover t = ((TimelineTweenMover)current);
			//float dur = t.timeline.getDuration( );
			//float curr = t.timeline.getCurrentTime( );
			//current.move( dur-curr+Util.MIN_VALUE, entity.body );
			//t.timeline.kill( );
			t.timeline.start( );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}