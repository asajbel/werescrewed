package com.blindtigergames.werescrewed.particles;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;

public class Particle {

	private float lifeSpan;
	private Entity part;
	private IMover movement;
	private Entity baseEntity;

	public Particle( Entity particle, float lifeSpan, IMover mover ) {
		baseEntity = part;
		this.part = particle;
		this.lifeSpan = lifeSpan;
		this.movement = mover;
	}

	public void update( float deltaTime ) {
		if ( lifeSpan > 0 ) {
			movement.move( deltaTime, part.body );
			lifeSpan--;
		}
	}

	public boolean isDead( ) {
		return lifeSpan == 0;
	}

	public void resetParticle( ) {
		part = baseEntity;
	}
}
