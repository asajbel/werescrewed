package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class DeactivateSkeleton implements IAction {

	int timesRun = 0;
	
	@Override
	public void act( ) {
		// Intentionally left blank
	}

	@Override
	public void act( Entity entity ) {
		timesRun +=1;
		if ( entity.getEntityType( ) == EntityType.SKELETON ){
			Skeleton skeleton = (Skeleton)entity;
			if ( skeleton.body.isActive( ) ){
				skeleton.setSkeletonActive(false);
			}
			Gdx.app.log( "DeActivateSkeleton", skeleton.name+" is now deactivated." );
		}else{
			Gdx.app.log( "DeActivateSkeleton", ""+timesRun );
		}
	}

}
