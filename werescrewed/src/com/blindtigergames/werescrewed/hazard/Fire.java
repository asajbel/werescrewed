package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.util.Util;

public class Fire extends Hazard {

	public ParticleEffect particleEffect;
	public Array< ParticleEmitter > particleEmitter;
	float width;
	float height;
	boolean upsideDown = true;
	
	/**
	 * Constructor for fire
	 * 
	 * @param name String
	 * @param pos Vector2
	 * @param world World
	 * @param isActive boolean
	 * @param pixelWidth float
	 * @param pixelHeight float
	 */
	public Fire( String name, Vector2 pos, World world, boolean isActive, 
			float pixelWidth, float pixelHeight ) {
		super( name, pos, null, world, isActive );
		entityType = EntityType.HAZARD;
		
		width = pixelWidth;
		height = pixelHeight;
		this.world = world;
		particleEffect = new ParticleEffect( );
		particleEffect.load(
				Gdx.files.internal( "data/particles/steam" ),
				Gdx.files.internal( "data/particles" ) );
		particleEffect.setPosition( pos.x, pos.y);
		particleEmitter = particleEffect.getEmitters( );
		constructBody(pos);
		for( ParticleEmitter PE: particleEmitter)
			PE.setContinuous( false );
	}
	
	/**
	 * sets up physics bodies
	 * 
	 * @param position Vector2
	 */
	private void constructBody( Vector2 position ){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		FixtureDef steamFixtureDef = new FixtureDef( );
		EdgeShape polygon = new EdgeShape( );
		polygon.set( ( 0 ) * Util.PIXEL_TO_BOX, ( 0 ) * Util.PIXEL_TO_BOX, 
				( width * 2 ) * Util.PIXEL_TO_BOX, ( height * 2 ) * Util.PIXEL_TO_BOX );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );
		polygon.set( ( 0 ) * Util.PIXEL_TO_BOX, ( 0 ) * Util.PIXEL_TO_BOX, 
				( width * -2 ) * Util.PIXEL_TO_BOX, ( height * 2 ) * Util.PIXEL_TO_BOX );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );
		polygon.set( ( width * 2 ) * Util.PIXEL_TO_BOX, ( height * 2 ) * Util.PIXEL_TO_BOX, 
				( width * -2 ) * Util.PIXEL_TO_BOX, ( height * 2 ) * Util.PIXEL_TO_BOX );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
	}
	
	/**
	 * flips vertical direction of fire
	 */
	public void flip( ){
		particleEffect.flipY( );
		if(upsideDown){
			body.setTransform( body.getPosition( ), (float) Math.PI  );
		} else {
			body.setTransform( body.getPosition( ), 0.0f  );
		}
		upsideDown = !upsideDown;
	}
	
	
	/**
	 * draws fire particles
	 * 
	 * @param batch SpriteBatch
	 * @param deltaTime float
	 */
	public void draw( SpriteBatch batch, float deltaTime ) {
		if (Gdx.input.isKeyPressed( Input.Keys.TAB ))
			flip( );
		if (Gdx.input.isKeyPressed( Input.Keys.BACKSLASH ))
			setActiveHazard(!activeHazard);
		particleEffect.draw( batch, deltaTime );
		if(activeHazard){
			particleEffect.start( );
		}
	}
}
