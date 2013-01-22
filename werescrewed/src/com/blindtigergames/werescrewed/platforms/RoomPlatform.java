package com.blindtigergames.werescrewed.platforms;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

public class RoomPlatform extends Platform {
	protected boolean rotate = false;
	protected float tileHeight, tileWidth;

	public RoomPlatform( String n, Vector2 pos, Texture tex, float width,
			float height, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructRoomBody( pos.x, pos.y, width, height );
	}

	public void constructRoomBody( float x, float y, float width, float height ) {
		PolygonShape ps = new PolygonShape( );
		FixtureDef fd = new FixtureDef( );

		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( x, y );
		body = world.createBody( groundBodyDef );
		float hx = width * tileConstant * GameScreen.PIXEL_TO_BOX;
		float hy = height * tileConstant * GameScreen.PIXEL_TO_BOX;

		Vector2 p1 = new Vector2( 0, -hy + hx );
		Vector2 p2 = new Vector2( -hy + hx,  0 );
		Vector2 p3 = new Vector2( 0, hy - hx );
		Vector2 p4 = new Vector2( hy - hx, 0 );

		ps.setAsBox( hy, hx, p1, 0 );
		fd.shape = ps;
		body.createFixture( fd );
		
		ps.setAsBox( hx, hy, p2, 0 );
		fd.shape = ps;
		body.createFixture( fd );
		
		ps.setAsBox( hy, hx, p3, 0 );
		fd.shape = ps;
		body.createFixture( fd );
		
		ps.setAsBox( hx, hy, p4, 0 );
		fd.shape = ps;
		body.createFixture( fd );
		
	}

	public void update( ) {
		super.update( );
	}
}