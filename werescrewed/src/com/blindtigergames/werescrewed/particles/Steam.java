package com.blindtigergames.werescrewed.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.util.Util;

public class Steam extends Entity{

	public ParticleEffect particleEffect;
	float width;
	float height;
	boolean test;
	
	/**
	 * Creates a steam vent which moves players up when they collide
	 * 
	 * @param name String
	 * @param positionPixels Vector2
	 * @param texture Texture
	 * @param body Body
	 * @param solid boolean
	 * @param pixelWidth float
	 * @param pixelHeight float
	 */
	public Steam( String name, Vector2 positionPixels, Texture texture, Body body,
			boolean solid, float pixelWidth, float pixelHeight, World world ) {

		super( name, positionPixels, texture, body, false );
		entityType = EntityType.STEAM;
		width = pixelWidth;
		height = pixelHeight;
		this.world = world;
		particleEffect = new ParticleEffect( );
		particleEffect.load(
				Gdx.files.internal( "data/particles/steam" ),
				Gdx.files.internal( "data/particles" ) );
		particleEffect.setPosition( positionPixels.x, positionPixels.y - height);

		constructBody(positionPixels, pixelHeight, pixelWidth);
	}
	
	/**
	 * Draws the particles for steam from the base of its body
	 * 
	 * @param batch spriteBatch
	 * @param deltaTime float
	 */
	public void draw( SpriteBatch batch, float deltaTime ) {
		//if ( Gdx.input.isKeyPressed( Keys.B ) ) {
		//	if (test){
				//particleEffect.start( );
		//	}
		//	test = false;
		//}
		//else test = true;
		particleEffect.draw( batch, deltaTime );
	}
	
	/**
	 * builds body of steam collision rectangle
	 * 
	 * @param position Vector2
	 * @param height float
	 * @param width float
	 */
	private void constructBody(Vector2 position, float height, float width){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( width * Util.PIXEL_TO_BOX, height * Util.PIXEL_TO_BOX );
		FixtureDef steamFixtureDef = new FixtureDef( );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
	}

}
