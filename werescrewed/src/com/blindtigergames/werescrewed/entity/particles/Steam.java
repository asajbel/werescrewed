package com.blindtigergames.werescrewed.entity.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.util.Util;

public class Steam extends Platform{

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
	public Steam( String name, Vector2 positionPixels,
			float pixelWidth, float pixelHeight, World world ) {

		super( name, positionPixels, null, null );
		entityType = EntityType.STEAM;
		width = pixelWidth;
		height = pixelHeight;
		this.world = world;
		particleEffect = WereScrewedGame.manager.getParticleEffect( "fastSteam" );//ParticleEffect.loadEffect("steam");
		particleEffect.setOffset(0f, -92f);
		particleEffect.setPosition( positionPixels.x, positionPixels.y);
		constructBody(positionPixels, pixelHeight, pixelWidth);
	}
	
	/**
	 * Draws the particles for steam from the base of its body
	 * 
	 * @param batch spriteBatch
	 * @param deltaTime float
	 */
	public void draw( SpriteBatch batch, float deltaTime ) {

		particleEffect.setPosition( this.getPositionPixel( ).x, this.getPositionPixel().y);
		particleEffect.setAngle( body.getAngle( ) );
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
