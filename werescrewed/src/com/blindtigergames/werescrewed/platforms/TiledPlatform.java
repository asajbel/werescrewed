package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.screens.GameScreen;
import java.lang.Math;

/**
 * @param name blah blah
 * 
 * @author Ranveer
 *
 */

public class TiledPlatform extends Platform{
	protected final int tileConstant = 16;
	protected boolean rotate = false;
	protected int tileHeight, tileWidth;
	
	public TiledPlatform( String n, Vector2 pos, Texture tex, int width, int height, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructBody(pos.x, pos.y, width, height );
	}
	
	private void constructBody(float x, float y, int width, int height){

        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.KinematicBody;
        groundBodyDef.position.set(new Vector2(x * GameScreen.PIXEL_TO_BOX, 
        		                               y * GameScreen.PIXEL_TO_BOX));  
        body = world.createBody(groundBodyDef);  
        
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox((width * tileConstant) * GameScreen.PIXEL_TO_BOX, 
        		(height * tileConstant) * GameScreen.PIXEL_TO_BOX);
        
        FixtureDef platformFixtureDef = new FixtureDef();
		platformFixtureDef.shape = groundBox;
		platformFixtureDef.density = 1.9f;
		platformFixtureDef.friction = 0.5f;
		platformFixtureDef.restitution = 0.0f;
		body.setGravityScale(.1f);
		body.createFixture(platformFixtureDef);

	}
	
	public void update(){
		body.setActive(true);
		
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
		
		/*
		 * Doesn't work, I figure its more Imover stuff anyways
		if(rotate)
		{
			System.out.println("rotating");
			float nextAngle =  (body.getAngle() + body.getAngularVelocity() / 60.0f);
			float totalRotation = 180.0f - nextAngle;
			while (totalRotation < -180 * GameScreen.DEGTORAD) totalRotation += 360 * GameScreen.DEGTORAD;
			while (totalRotation > 180 * GameScreen.DEGTORAD) totalRotation -= 360 * GameScreen.DEGTORAD;
			float desiredAngularVelocity = totalRotation * 60f;
			float change = 10 * GameScreen.DEGTORAD;
			desiredAngularVelocity  = Math.min(change, Math.max(-change, desiredAngularVelocity));
			float impulse = body.getInertia() * desiredAngularVelocity;
			body.applyAngularImpulse(impulse);
		}
		*/
		mover.move(this.body);
		super.update();
	}
	
	
}