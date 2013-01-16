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

	protected boolean rotate = false;

	public ComplexPlatform( String n, Vector2 pos, Texture tex, int width, int height, World world ) {
		super(n, pos, tex, world);
		this.width = width;
		this.height = height;
		constructBody(pos.x, pos.y, width, height);
	}
	
	private void constructBody(float x, float y, int width, int height ){
		BodyEditorLoader loader = new BodyEditorLoader( Gdx.files.internal("data/bottle.json") );
		BodyDef bd = new BodyDef();
		bd.position.set(x,y);
		bd.type = BodyType.DynamicBody;
		
		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;
		fd.restitution = 0.0f;
		
		
		body = world.createBody(bd);
		
		loader.attachFixture( body, "bottle", fd, 1 );
	}
	
	public void update(){
		body.setActive(true);
		super.update();
		if( Gdx.input.isKeyPressed(Keys.T) ){
			rotate();
		}
		
		if( Gdx.input.isKeyPressed(Keys.Y) ){
			body.setAngularVelocity(0);
		}
		if( Gdx.input.isKeyPressed(Keys.O) ){
			changeType();
		}

		if( Gdx.input.isKeyPressed(Keys.N) ){
			//rotateBy90();
			rotate = !rotate;
			System.out.println(rotate);
			System.out.println(body.getAngle());
		}
		if( Gdx.input.isKeyPressed(Keys.L) ){
			setHorizontal();
		}
		
	}
}