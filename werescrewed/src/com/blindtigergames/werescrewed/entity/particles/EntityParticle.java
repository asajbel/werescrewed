package com.blindtigergames.werescrewed.entity.particles;

import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.Entity;

/**
 * Particle data structure that keeps track of an entity's lifespan
 * as well as updates and draws the entity.
 * @author stew
 *
 */
public class EntityParticle {

	private float lifeDelta;
	private float lifeSpanSeconds;
	private Entity particle;
	private Vector2 initPositionMeter;
	private float initDelay, delayDelta, delay;
	private boolean isDelayDone;

	/**
	 * @param particleEntity
	 * @param lifeSpanSeconds
	 * @param mover
	 */
	public EntityParticle( Entity particleEntity, float lifespanSeconds ) {
		this( particleEntity, lifespanSeconds, 0, 0 );
	}
	
	/**
	 * Delay happens once on creation or if you call hard reset
	 */
	public EntityParticle( Entity particleEntity, float lifespanSeconds, float delayBetweenSpawn ) {
		this( particleEntity, lifespanSeconds, delayBetweenSpawn, 0 );
	}
	
	public EntityParticle( Entity particleEntity, float lifespanSeconds, float delayBetweenSpawn, float initDelay ) {
		this.particle = particleEntity;
		this.lifeSpanSeconds = lifespanSeconds;
		this.lifeDelta = lifeSpanSeconds;
		this.initPositionMeter = particleEntity.getPosition( );
		this.initDelay = initDelay;
		this.delayDelta = delayBetweenSpawn+initDelay;
		this.delay=delayBetweenSpawn;
		isDelayDone = (this.delayDelta <= 0);
	}

	/**
	 * updates life span
	 * 
	 * @param deltaTime
	 */
	public void update( float deltaTime ) {
		if(delayDelta>0){
			delayDelta -= deltaTime;
			if(delayDelta<=0)isDelayDone=true;
			particle.setPosition( initPositionMeter );
			particle.body.setGravityScale( 0.0f );
		} else{
			if(isDelayDone) isDelayDone = false;
		}
		if(delayDelta <= 0 && !isDelayDone )	
		if ( lifeDelta > 0 ) {
			particle.update( deltaTime );
			particle.updateMover( deltaTime );
			lifeDelta-=deltaTime;
		}
		
	}
	
	//This is true for one tic when this particle has recently finished it's delay
	public boolean isDelayDone(){
		return isDelayDone;
	}

	/**
	 * if particle has run its course
	 * 
	 * @return boolean
	 */
	public boolean isDead( ) {
		return lifeDelta <= 0;
	}
	
	public boolean isDelayed(){
		return delayDelta > 0;
	}

	/**
	 * resets particle to initial state
	 */
	public void resetParticle( Vector2 posMeters ) {
		particle.reset( );
		particle.setPosition( posMeters.cpy() );
		resetLifespan( );
		delayDelta=delay;
	}

	/**
	 * resets particle's life span to initial amount
	 */
	public void resetLifespan( ) {
		lifeDelta = lifeSpanSeconds;
	}
	
	/**
	 * Reset particle to initial constructor lifetime, position, and delay
	 */
	public void hardReset(){
		particle.setPosition( initPositionMeter.cpy() );
		lifeDelta = lifeSpanSeconds;
		delayDelta = initDelay+delay;
	}
	
	public Entity getEntity(){
		return this.particle;
	}
	
	public boolean isAlive(){
		return (!isDead() && !isDelayed());
	}
}
