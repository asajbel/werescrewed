package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.util.Metrics;

public class MetricsStartTimeAction implements IAction {

	/**
	 * Event Trigger that adds the name of the section and start time to the
	 * list of section names and times in Metrics.
	 * 
	 * @param name
	 *            The name of the section.
	 */
	public MetricsStartTimeAction( String name ) {
		if ( Metrics.activated ) {
			Metrics.addPlayerBeginTime( ( System.currentTimeMillis( ) / 1000 ) % 60 );
			Metrics.addSectionName( name );
		}

	}

	@Override
	public void act( ) {
		if ( Metrics.activated )
			Metrics.addPlayerBeginTime( ( System.currentTimeMillis( ) / 1000 ) % 60 );

	}

	@Override
	public void act( Entity entity ) {

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}