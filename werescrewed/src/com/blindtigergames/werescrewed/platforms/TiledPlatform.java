package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.screens.GameScreen;


public class TiledPlatform extends Platform
{
	private World world;
	private final int tileConstant = 32;

	public TiledPlatform(String n, Vector2 pos, Texture tex, int width, int height, World world) {
		super(n, pos, tex);
		this.width = width;
		this.height = height;
		this.world = world;
		constructBody(width, height);
	}
	
	private void constructBody(int width, int height)
	{

        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.KinematicBody;
        groundBodyDef.position.set(new Vector2(this.position.x * GameScreen.PIXEL_TO_BOX, 
        		this.position.y * GameScreen.PIXEL_TO_BOX));  
        body = world.createBody(groundBodyDef);  
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox((width * tileConstant) * GameScreen.PIXEL_TO_BOX, 
        		(height * tileConstant) * GameScreen.PIXEL_TO_BOX);  
        body.createFixture(groundBox, 0.0f);
        body.getFixtureList().get(0).setFriction(0.5f);
	}
	
	public void update()
	{
		Vector2 pos = body.getPosition();
		this.position = pos;
	}
	
}