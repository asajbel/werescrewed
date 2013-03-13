package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class ActivateSkeleton implements IAction {

	int timesRun = 0;
	@Override
	public void act( ) {
		//intentionally left blank
	}

	@Override
	public void act( Entity entity ) {
		timesRun +=1;
		if ( entity.getEntityType( ) == EntityType.SKELETON ){
			Skeleton skeleton = (Skeleton)entity;
			if ( !skeleton.body.isActive( ) ){
				skeleton.setSkeletonActive(true);
			}
			Gdx.app.log( "ActivateSkeleton", skeleton.name+" is now activated. "+timesRun );
		}else{
			Gdx.app.log( "ActivateSkeleton", ""+timesRun );
		}
	}

}
