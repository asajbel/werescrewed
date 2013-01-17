package com.blindtigergames.werescrewed.platforms;

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


public class RoomPlatform extends Platform{
	protected final int tileConstant = 16;
	protected boolean rotate = false;
	protected int tileHeight, tileWidth;
	
	public RoomPlatform( String n, Vector2 pos, Texture tex, int width, int height, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructRoomBody( pos.x, pos.y, width, height );
	}
	
	private void constructRoomBody( float x, float y, int width, int height ){
		PolygonShape ps = new PolygonShape();
		FixtureDef fd = new FixtureDef();
		fd.density = 1f;
		fd.restitution = 0.0f;
		
		BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.KinematicBody; 
        groundBodyDef.position.set(x,y);
        body = world.createBody(groundBodyDef);  
        float hx = width * tileConstant * GameScreen.PIXEL_TO_BOX;
        float hy = height * tileConstant *  GameScreen.PIXEL_TO_BOX;

        Vector2 z = new Vector2();

        ps.setAsBox( hx, hy, z, 0 );
        fd.shape = ps;
        body.createFixture(fd);
        
        ps.setAsBox( hx, hy, new Vector2( 2*(hx*(height-1)), 0f ), 0 );
        fd.shape = ps;
        body.createFixture(fd);
        
        ps.setAsBox( hy, hx, new Vector2( hx*(height-1), -(hy - hy/height) ), 0 );
        fd.shape = ps;
        body.createFixture(fd);
	
        
        ps.setAsBox( hy, hx, new Vector2( hx*(height-1), (hy - hy/height) ), 0 );
        fd.shape = ps;
        body.createFixture(fd);
        
        
	}
	
	public void update(){
		super.update();
	}
}