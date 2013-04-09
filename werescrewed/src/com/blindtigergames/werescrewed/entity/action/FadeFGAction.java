package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class FadeFGAction implements IAction {

	private boolean hasFGFade;
	
	/**
	 * @param hasFGFade if true, this action will fade the skeleton fg out to transparent.
	 */
	public FadeFGAction(boolean hasFGFade){
		this.hasFGFade = hasFGFade;
	}
	
	@Override
	public void act( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Entity entity ) {
		// TODO Auto-generated method stub
		
		if ( entity.entityType == EntityType.SKELETON ){
			Skeleton s = (Skeleton)entity;
			s.setFGFade( hasFGFade );
		}else{
			//THIS SHOULD NEVER RUN,
			//TODO Fix this bug!
			//Gdx.app.log( "FadeFGAction:", "Wrongly calling act(entity) on " + entity.name );
		}
	}
	
	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}
