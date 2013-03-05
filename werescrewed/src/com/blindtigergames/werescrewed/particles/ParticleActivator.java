package com.blindtigergames.werescrewed.particles;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.action.IAction;

public class ParticleActivator implements IAction{

	@Override
	public void act( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Entity entity ) {
		if (entity.getEntityType( ) == EntityType.PARTICLE_EMITTER){
			Gdx.app.log("ParticleActivator called on non-ParticleSystem: ", entity.name);
			return;
		}
		ParticleEmitter pE = (ParticleEmitter) entity;
		if (pE.isActive( ))
			pE.activate( );
		else
			pE.deactivate( );
	}
}