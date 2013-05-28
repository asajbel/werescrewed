package com.blindtigergames.werescrewed.entity.particles;

import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.hazard.Enemy;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.entity.hazard.HazardType;
import com.blindtigergames.werescrewed.entity.mover.IMover;

/**
 * Particle data structure that keeps track of an entity's lifespan
 * as well as updates and draws the entity.
 * @author stew
 *
 */
public class EntityParticle {

	private float lifeSpanSeconds;
	private float initLifeSpan;
	private Entity particle;
	private IMover movement;
	private Entity baseEntity;

	/**
	 * @param particleEntity
	 * @param lifeSpanSeconds
	 * @param mover
	 */
	public EntityParticle( Entity particleEntity, float lifespanSeconds, IMover mover ) {
		this.particle = particleEntity;
		this.lifeSpanSeconds = lifespanSeconds;
		this.movement = mover;
		baseEntity = particle;
		initLifeSpan = lifeSpanSeconds;
	}

	/**
	 * updates life span
	 * 
	 * @param deltaTime
	 */
	public void update( float deltaTime ) {
		if ( lifeSpanSeconds > 0 ) {
			if(movement!=null)movement.move( deltaTime, particle.body );
			particle.update( deltaTime );
			particle.updateMover( deltaTime );
			lifeSpanSeconds-=deltaTime;
		}
	}

	/**
	 * if particle has run its course
	 * 
	 * @return boolean
	 */
	public boolean isDead( ) {
		return lifeSpanSeconds <= 0;
	}

	/**
	 * resets particle to initial state
	 */
	public void resetParticle( Vector2 posMeters ) {
		//part = baseEntity; //old code
		particle.reset( );
		particle.setPosition( posMeters.cpy() );
		resetLifespan( );
	}

	/**
	 * resets particle's life span to initial amount
	 */
	public void resetLifespan( ) {
		lifeSpanSeconds = initLifeSpan;
	}
	
	public Entity getEntity(){
		return this.particle;
	}
}
