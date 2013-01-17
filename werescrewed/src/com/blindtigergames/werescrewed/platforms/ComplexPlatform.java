package com.blindtigergames.werescrewed.platforms;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.screens.GameScreen;


/**
 * @param name blah blah
 * 
 * @author Ranveer
 *
 */

public class ComplexPlatform extends Platform{

	private int scale;

	//String object would be like "bottle" then we will load that particular body (precompiled)
	public ComplexPlatform( String n, Vector2 pos, Texture tex, int scale, 
			World world, String bodyName ) {
		super(n, pos, tex, world);
		//this.width = width;
		//this.height = height;
		this.scale = scale;
		constructComplexBody( pos.x, pos.y, scale, bodyName );
	}
	
	private void constructComplexBody( float x, float y, int scale, String bodyName ){
		String filename = "data/bodies/" + bodyName + ".json";
		BodyEditorLoader loader = new BodyEditorLoader( Gdx.files.internal(filename) );
		BodyDef bd = new BodyDef();
		bd.position.set(x,y);
		bd.type = BodyType.DynamicBody;
		
		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;
		fd.restitution = 0.0f;
		
		
		body = world.createBody(bd);
		body.setGravityScale(.1f);
		
		loader.attachFixture( body, bodyName, fd, scale );
		
		
	}
	
	public void update(){
		super.update();
	}
}