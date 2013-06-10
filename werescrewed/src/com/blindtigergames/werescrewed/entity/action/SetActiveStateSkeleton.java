package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;

public class SetActiveStateSkeleton implements IAction {

	int timesRun = 0;
	//private boolean isActive;

	public SetActiveStateSkeleton( boolean isActive ) {
		//this.isActive = isActive;
	}

	@Override
	public void act( ) {
		// intentionally left blank
	}

	@Override
	public void act( Entity entity ) {
		timesRun += 1;
		if ( entity.getEntityType( ) == EntityType.SKELETON ) {
			//Skeleton skeleton = ( Skeleton ) entity;
			//skeleton.getRoot( ).setSkeletonActiveState( skeleton, isActive );
			// Gdx.app.log( "SetActiveStateSkeleton:",
			// skeleton.name+" active state is: "+isActive+", "+timesRun );
		} else {
			// Gdx.app.log( "SetActiveStateSkeleton", ""+timesRun );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}
