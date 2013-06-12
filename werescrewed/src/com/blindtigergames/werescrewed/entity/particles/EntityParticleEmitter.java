package com.blindtigergames.werescrewed.entity.particles;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.util.Util;


public class EntityParticleEmitter extends Entity {

	private ArrayList< EntityParticle > particles;
	private boolean activeEmitting;
	private Vector2 emitionImpusle;
	private boolean recentlyActivated=false;

	/**
	 * Entity used to manage Particle Systems, in which the Particles are
	 * Entities
	 * 
	 * @param name
	 *            String name of this entity
	 * @param positionPixels
	 *            Vector2 position entities will be emitted from
	 * @param particleEmitImpulse
	 * @param active
	 *            boolean
	 */
	public EntityParticleEmitter( String name, Vector2 positionPixels, Vector2 particleEmitImpulse, World world,
			boolean active ) {
		super( name, positionPixels, null, null, false );
		
		particles = new ArrayList< EntityParticle >( );
		this.world = world;
		constructBody( positionPixels );
		activeEmitting = active;
		entityType = EntityType.PARTICLE_EMITTER;
		this.emitionImpusle = particleEmitImpulse.cpy( );
	}

	/**
	 * creates fixture-less body at the location
	 * 
	 * @param position
	 *            Vector2
	 */
	private void constructBody( Vector2 position ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX, position.y
				* Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );
		body.setUserData( this );
	}


	/**
	 * adds new particle to the engine with the following components
	 * 
	 * @param entity
	 *            Entity
	 * @param lifeSpan
	 *            float
	 * @param mover
	 *            mover
	 */
	public void addParticle( Entity entity, float lifeSpan ) {
		particles.add( new EntityParticle( entity, lifeSpan) );
	}
	
	/**
	 * Use this when you have mutiple particles that need to spawn at different times 
	 * (ie a line of entities that respawn when they reach a single destination)
	 * @param entity
	 * @param lifeSpan
	 * @param mover
	 * @param lifeSpanOffset
	 */
	public void addParticle( Entity entity, float lifeSpan, float delay, float initDelay ) {
		particles.add( new EntityParticle( entity, lifeSpan, delay, initDelay ) );
	}


	/**
	 * updates all particles in the system
	 * 
	 * @param deltaTime
	 *            float
	 */
	public void update( float deltaTime ) {
		if(recentlyActivated){
			for ( EntityParticle particle : particles ) {
				particle.hardReset( );
				particle.getEntity( ).body.setActive( true );
			}
			recentlyActivated=false;
		}
		if(activeEmitting){
			for ( EntityParticle p : particles ) {
				p.update( deltaTime );
				if( p.isDelayDone( ) ){//only returns true once per lifespan
					Body b = p.getEntity( ).body;
					b.setLinearVelocity( 0,0 );
					b.setGravityScale( 0.1f );
					b.applyLinearImpulse( emitionImpusle, b.getWorldCenter( ) );
				}
				if ( p.isDead( ) ) {
					p.resetParticle( this.getPosition( ) );
					//p.getEntity( ).body.applyLinearImpulse( emitionImpusle, p.getEntity( ).body.getWorldCenter( ) );
				}
			}
		}else{
			//HOLD THE particle in emitting place.
			//for ( EntityParticle p : particles ) {
			//	p.hardReset( );
			//}
		}
	}
	
	@Override
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		super.draw( batch, deltaTime, camera );
		if(active){
			for ( EntityParticle particle : particles ) {
				if(particle.isAlive()) particle.getEntity( ).draw( batch, deltaTime, camera );
			}
		}
	}
	
	@Override
	public void reset(){
		for ( EntityParticle particle : particles ) {
			particle.hardReset( );
		}
	}
	
	@Override
	public void setActive(boolean isActive){
		super.setActive( isActive );
		Entity e;
		for ( EntityParticle particle : particles ) {
			e=particle.getEntity( );
			if(e.entityType == EntityType.HAZARD){
				((Hazard)e).setActiveHazard(isActive);
			}
		}
	}
	
	public void setEmittingActive(boolean isEmitting){
		activeEmitting=isEmitting;
		recentlyActivated=true;
	}
}
