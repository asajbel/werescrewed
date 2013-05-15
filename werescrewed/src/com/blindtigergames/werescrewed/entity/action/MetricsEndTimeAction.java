package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.util.Metrics;

public class MetricsEndTimeAction implements IAction {

	@Override
	public void act( ) {
		if ( Metrics.activated )
			Metrics.addPlayerEndTime( ( System.currentTimeMillis( ) / 1000 ) % 60 );

	}

	@Override
	public void act( Entity entity ) {

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}