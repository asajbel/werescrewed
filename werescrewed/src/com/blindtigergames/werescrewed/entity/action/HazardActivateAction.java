package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;

public class HazardActivateAction implements IAction {

	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {

		if ( entity.getEntityType( ) == EntityType.HAZARD ) {
			Hazard h = ( Hazard ) entity;
			h.setActiveHazard( true );

		}

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}