package com.blindtigergames.werescrewed.particles;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.util.Util;

public class ParticleEmitter extends Entity{
	

	private ArrayList< Particle > particles;
	private boolean activated;
	
	ParticleEmitter( String name, Vector2 positionPixels, Texture texture, Body body,
			boolean solid, Entity baseEntity, float lifeSpan, IMover mover, boolean active){
		super( name, positionPixels, texture, null, true );
		particles = new ArrayList< Particle >( );
		Particle p = new Particle( baseEntity, lifeSpan, mover );
		particles.add( p );
		constructBody(positionPixels);
		activated = active;
	}
	
	/**
	 * creates fixture-less body at the location
	 * 
	 * @param position Vector2
	 */
	private void constructBody(Vector2 position){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );
		body.setUserData( this );
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
	 * adds new particle to the engine with the following components
	 * 
	 * @param entity Entity
	 * @param lifeSpan float
	 * @param mover mover
	 */
	public void addParticle( Entity entity, float lifeSpan, IMover mover ) {
		particles.add( new Particle( entity, lifeSpan, mover ) );
	}


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
	
	/**
	 * turns on emmitter
	 */
	public void activate( ){
		activated = true;
	}
	
	/**
	 * turns off emitter
	 */
	public void deactivate( ){
		activated = false;
	}
	
	/**
	 * returns value of activated;
	 * 
	 * @return boolean
	 */
	public boolean isActive( ){
		return activated;
	}
}
