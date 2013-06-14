package com.blindtigergames.werescrewed.entity.particles;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.action.ActionType;
import com.blindtigergames.werescrewed.entity.action.IAction;

public class EntityParticleActivator implements IAction {

	boolean activateState;
	
	public EntityParticleActivator(boolean activeState){
		this.activateState=activeState;
	}
	
	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {
		if ( entity==null || entity.getEntityType( ) != EntityType.PARTICLE_EMITTER ) {
			return;
		}
		EntityParticleEmitter pE = ( EntityParticleEmitter ) entity;
		pE.setEmittingActive( activateState );
		//Gdx.app.log("EntityParticleActivator",pE.name+"is now "+activateState);
	}

	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}
}