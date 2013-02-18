package com.blindtigergames.werescrewed.particles;

import java.util.ArrayList;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;

public class ParticleSystem {

	private ArrayList< Particle > particles;

	public ParticleSystem( Entity baseEntity, IMover mover, float lifeSpan ) {
		particles = new ArrayList< Particle >( );
		Particle p = new Particle( baseEntity, lifeSpan, mover );
		particles.add( p );
	}

	public void update( float deltaTime ) {
		for ( Particle p : particles ) {
			p.update( deltaTime );
			if ( p.isDead( ) ) {
				p.resetParticle( );
			}
		}
	}

	public void addParticle( Entity e, float lifeSpan, IMover mover ) {
		particles.add( new Particle( e, lifeSpan, mover ) );
	}

	public void duplicateParticle( Entity e, float lifeSpan, IMover mover,
			int duplications ) {
		for ( int i = 0; i < duplications; i++ ) {
			particles.add( new Particle( e, lifeSpan, mover ) );
		}
	}
}
