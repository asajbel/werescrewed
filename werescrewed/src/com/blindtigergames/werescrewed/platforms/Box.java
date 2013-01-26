package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.screens.GameScreen;

public class Box extends Platform{
	
	boolean colliding;

    public Box( String name, Vector2 pos, Texture tex, World world ){
		super( name, world, pos, tex);
		createBoxBody(pos.x, pos.y);
		colliding = false;
		body.setUserData(this);
	}
    
    private void createBoxBody(float x, float y){
    	BodyDef bodyDef = new BodyDef();
    	bodyDef.type = BodyType.StaticBody;
    	bodyDef.position.set(x * GameScreen.PIXEL_TO_BOX, y * GameScreen.PIXEL_TO_BOX);
    	body = world.createBody(bodyDef);
    	
    	PolygonShape groundBox = new PolygonShape();
    	groundBox.setAsBox(16.0f * GameScreen.PIXEL_TO_BOX, 16.0f * GameScreen.PIXEL_TO_BOX);
    	
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = groundBox;
    	fixtureDef.density = .3f;
    	fixtureDef.friction = 0.8f;
    	fixtureDef.restitution = .8f;
    	body.createFixture(fixtureDef);
    	
    	groundBox.dispose( );
    }
    
    public void startContact(){
    	colliding = true;
    }
    
    public void endContact(){
    	colliding = false;
    }
    
    public void exampleCollide(){
    	System.out.println("Oi, Im standing here - box");
    }
}