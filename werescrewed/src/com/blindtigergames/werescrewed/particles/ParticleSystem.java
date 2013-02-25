package com.blindtigergames.werescrewed.particles;

import java.util.ArrayList;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;

public class ParticleSystem {

	private ArrayList< Particle > particles;
	
	/**
	 * Builds new particle engine with initial particle
	 * 
	 * @param baseEntity Entity
	 * @param mover IMover
	 * @param lifeSpan float
	 */
	public ParticleSystem( Entity baseEntity, IMover mover, float lifeSpan ) {
		particles = new ArrayList< Particle >( );
		Particle p = new Particle( baseEntity, lifeSpan, mover );
		particles.add( p );
	}

	/**
	 * updates all particles in the system
	 * 
	 * @param deltaTime float
	 */
	public void update( float deltaTime ) {
		for ( Particle p : particles ) {
			p.update( deltaTime );
			if ( p.isDead( ) ) {
				p.resetParticle( );
			}
		}
	}
	
	/**
<<<<<<< HEAD
	 * adds new particle to the engine with the following components
	 * @param entity Entity
	 * @param lifeSpan float
	 * @param mover mover
	 */
	public void addParticle( Entity entity, float lifeSpan, IMover mover ) {
		particles.add( new Particle( entity, lifeSpan, mover ) );
	}

	/*public void duplicateParticle( Entity e, float lifeSpan, IMover mover,
=======
	 * adds a new particle to the system
	 * 
	 * @param e Entity
	 * @param lifeSpan float
	 * @param mover IMover
	 *
	public void addParticle( Entity e, float lifeSpan, IMover mover ) {
		particles.add( new Particle( e, lifeSpan, mover ) );
	}*/
	
	/**
	 * don't use this right now
	 * 
	 * @param e Entity
	 * @param lifeSpan float
	 * @param mover IMover
	 * @param duplications int
	 */
	public void duplicateParticle( Entity e, float lifeSpan, IMover mover,
			int duplications ) {
		for ( int i = 0; i < duplications; i++ ) {
			particles.add( new Particle( e, lifeSpan, mover ) );
		}
	}
	
}
