package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.util.Util;

public class Fire extends Hazard {

	public ParticleEffect particleEffect;
	float width;
	float height;
	
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

		constructBody(pos);
		
	}
	
	private void constructBody( Vector2 position ){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		FixtureDef steamFixtureDef = new FixtureDef( );
		EdgeShape polygon = new EdgeShape( );
		polygon.set( ( 0 ) * Util.PIXEL_TO_BOX, ( 0 ) * Util.PIXEL_TO_BOX, 
				( width ) * Util.PIXEL_TO_BOX, ( height ) * Util.PIXEL_TO_BOX );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );
		polygon.set( ( 0 ) * Util.PIXEL_TO_BOX, ( 0 ) * Util.PIXEL_TO_BOX, 
				( width * -1 ) * Util.PIXEL_TO_BOX, ( height ) * Util.PIXEL_TO_BOX );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );
		polygon.set( ( width ) * Util.PIXEL_TO_BOX, ( height ) * Util.PIXEL_TO_BOX, 
				( width * -1 ) * Util.PIXEL_TO_BOX, ( height ) * Util.PIXEL_TO_BOX );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
	}
	
	public void draw( SpriteBatch batch, float deltaTime ) {
		particleEffect.draw( batch, deltaTime );
		particleEffect.start( );
	}
}
