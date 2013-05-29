package com.blindtigergames.werescrewed.entity.particles;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.util.Util;


public class EntityParticleEmitter extends Entity {

	private ArrayList< EntityParticle > particles;
	private boolean activated;

	/**
	 * Entity used to manage Particle Systems, in which the Particles are
	 * Entities
	 * 
	 * @param name
	 *            String
	 * @param positionPixels
	 *            Vector2
	 * @param texture
	 *            Texture
	 * @param body
	 *            Body
	 * @param baseEntity
	 *            Entity
	 * @param lifeSpan
	 *            float
	 * @param mover
	 *            IMover
	 * @param active
	 *            boolean
	 */
	public EntityParticleEmitter( String name, Vector2 positionPixels, 
			Entity baseEntity, float lifeSpan, IMover mover, World world,
			boolean active ) {
		super( name, positionPixels, null, null, false );
		particles = new ArrayList< EntityParticle >( );
		EntityParticle p = new EntityParticle( baseEntity, lifeSpan, mover );
		particles.add( p );
		this.world = world;
		constructBody( positionPixels );
		activated = active;
		entityType = EntityType.PARTICLE_EMITTER;
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
	 * updates all particles in the system
	 * 
	 * @param deltaTime
	 *            float
	 */
	public void update( float deltaTime ) {
		for ( EntityParticle p : particles ) {
			p.update( deltaTime );
			if ( p.isDead( ) ) {
				p.resetParticle( this.getPosition( ) );
			}
		}
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
	public void addParticle( Entity entity, float lifeSpan, IMover mover ) {
		particles.add( new EntityParticle( entity, lifeSpan, mover ) );
	}
	
	/**
	 * Use this when you have mutiple particles that need to spawn at different times 
	 * (ie a line of entities that respawn when they reach a single destination)
	 * @param entity
	 * @param lifeSpan
	 * @param mover
	 * @param lifeSpanOffset
	 */
	public void addParticle( Entity entity, float lifeSpan, IMover mover, float lifeSpanOffset ) {
		particles.add( new EntityParticle( entity, lifeSpan, mover, lifeSpanOffset ) );
	}

	/**
	 * don't use this right now
	 * 
	 * @param e
	 *            Entity
	 * @param lifeSpan
	 *            float
	 * @param mover
	 *            IMover
	 * @param duplications
	 *            int
	 */
	public void duplicateParticle( Entity e, float lifeSpan, IMover mover,
			int duplications ) {
		for ( int i = 0; i < duplications; i++ ) {
			particles.add( new EntityParticle( e, lifeSpan, mover ) );
		}
	}

	/**
	 * turns on emmitter
	 */
	public void activate( ) {
		activated = true;
	}

	/**
	 * turns off emitter
	 */
	public void deactivate( ) {
		activated = false;
	}

	/**
	 * returns value of activated;
	 * 
	 * @return boolean
	 */
	public boolean isActive( ) {
		return activated;
	}
	
	@Override
	public void draw( SpriteBatch batch, float deltaTime ) {
		super.draw( batch, deltaTime );
		for ( EntityParticle particle : particles ) {
			if(!particle.isDead( ))particle.getEntity( ).draw( batch, deltaTime );
		}
	}
}
