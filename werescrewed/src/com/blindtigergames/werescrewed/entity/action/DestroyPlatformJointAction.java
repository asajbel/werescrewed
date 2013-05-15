package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.platforms.Platform;

public class DestroyPlatformJointAction implements IAction {

	@Override
	public void act( ) {

	}

	@Override
	public void act( Entity entity ) {
		if ( entity.entityType == EntityType.PLATFORM ) {
			Platform plat = ( Platform ) entity;
			plat.destorySkeletonJoint( );
		}

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}