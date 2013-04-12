package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.util.Util;

public class PipeTile extends Entity {

	public PipeTile( String name, World world, Vector2 pos, Texture texture ) {
		super( name, pos, texture, null, true );
		this.world = world;
		
		constructBody( pos );
	}
	
	private void constructBody( Vector2 pos ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.position.set( pos );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.gravityScale = 0.1f;
		PolygonShape polygonShape = new PolygonShape( );
		polygonShape.setAsBox( 16.0f * Util.PIXEL_TO_BOX,
				16.0f * Util.PIXEL_TO_BOX );
		FixtureDef fixtureDef = new FixtureDef( );
		fixtureDef.filter.categoryBits = Util.CATEGORY_EVERYTHING;
		fixtureDef.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.friction = 0.1f;
		body = world.createBody( bodyDef );
		body.createFixture( fixtureDef );

		body.setUserData( this );
	}
	
	
}
