package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.util.Metrics;

public class MetricsStartTimeAction implements IAction{

	@Override
	public void act( ) {
		if( Metrics.activated )
			Metrics.addPlayerBeginTime( (System.currentTimeMillis( ) /1000) % 60  );
		
	}

	@Override
	public void act( Entity entity ) {
		
		
	}
	
}