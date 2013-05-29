package com.blindtigergames.werescrewed.entity.particles;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.action.ActionType;
import com.blindtigergames.werescrewed.entity.action.IAction;

public class EntityParticleActivator implements IAction {

	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings( "deprecation" )
	@Override
	public void act( Entity entity ) {
		if ( entity.getEntityType( ) == EntityType.PARTICLE_EMITTER ) {
			// Gdx.app.log("ParticleActivator called on non-ParticleSystem: ",
			// entity.name);
			return;
		}
		EntityParticleEmitter pE = ( EntityParticleEmitter ) entity;
		if ( pE.isActive( ) )
			pE.activate( );
		else
			pE.deactivate( );
	}

	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}
}