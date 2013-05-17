package com.blindtigergames.werescrewed.entity.particles;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;

@Deprecated
public class Particle {

	private float lifeSpan;
	private float initLifeSpan;
	private Entity part;
	private IMover movement;
	private Entity baseEntity;

	/**
	 * @param particle
	 * @param lifeSpan
	 * @param mover
	 */
	public Particle( Entity particle, float lifespan, IMover mover ) {
		this.part = particle;
		this.lifeSpan = lifespan;
		this.movement = mover;
		baseEntity = part;
		initLifeSpan = lifeSpan;
	}

	/**
	 * updates life span
	 * 
	 * @param deltaTime
	 */
	public void update( float deltaTime ) {
		if ( lifeSpan > 0 ) {
			movement.move( deltaTime, part.body );
			part.update( deltaTime );
			lifeSpan--;
		}
	}

	/**
	 * if particle has run its course
	 * 
	 * @return boolean
	 */
	public boolean isDead( ) {
		return lifeSpan == 0;
	}

	/**
	 * resets particle to initial state
	 */
	public void resetParticle( ) {
		part = baseEntity;
	}

	/**
	 * resets particle's life span to initial amount
	 */
	public void resetLifespan( ) {
		lifeSpan = initLifeSpan;
	}
}
