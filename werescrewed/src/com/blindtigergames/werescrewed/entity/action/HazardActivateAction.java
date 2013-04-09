package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;


public class HazardActivateAction implements IAction{

	@Override
	public void act( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Entity entity ) {
		//THIS CHECK EXISTS SO WE DONT CAST THINGS INTO WRONG THINGS
		if(entity.getEntityType( ) == EntityType.HAZARD){
			Hazard h = (Hazard) entity;
			h.setActive( true );
			//Code will change as hazards grow
		}
		
	}
	
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}