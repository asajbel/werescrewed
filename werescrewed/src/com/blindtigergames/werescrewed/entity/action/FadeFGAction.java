package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
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
		Skeleton s = (Skeleton)entity;
		s.setFGFade( hasFGFade );
	}

}
