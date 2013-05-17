package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class FadeSkeletonAction implements IAction {

	private boolean hasFadeOut;

	/**
	 * @param hasFadeOut
	 *            if true, this action will fade the skeleton fg out to
	 *            transparent.
	 */
	public FadeSkeletonAction( boolean hasFadeOut ) {
		this.hasFadeOut = hasFadeOut;
	}

	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {
		// TODO Auto-generated method stub

		if ( entity.entityType == EntityType.SKELETON ) {
			Skeleton s = ( Skeleton ) entity;
			s.setFade( hasFadeOut );
			// boolean isSkeleActive = s.isActive( );
			// if ( (hasFadeOut && !isSkeleActive) || (isSkeleActive &&
			// !hasFadeOut) ){
			// s.setActive( hasFadeOut );
			// }
			//
		} else {
			// THIS SHOULD NEVER RUN,
			// TODO Fix this bug!
			// Gdx.app.log( "FadeFGAction:", "Wrongly calling act(entity) on " +
			// entity.name );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}
