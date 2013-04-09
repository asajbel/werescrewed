package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.platforms.Platform;

public class DestoryPlatformJointAction implements IAction{


	@Override
	public void act( ) {
		
	}

	@Override
	public void act( Entity entity ) {
		Platform plat = (Platform) entity;
		plat.destorySkeletonJoint( );
		
	}
	
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}
	
}