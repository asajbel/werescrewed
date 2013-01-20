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
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

public class TiledPlatform extends Platform {
	protected final int tileConstant = 16;
	protected boolean rotate = false;
	protected int tileHeight, tileWidth;

	public TiledPlatform( String n, Vector2 pos, Texture tex, int width,
			int height, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructTileBody( pos.x, pos.y, width, height );
	}

	private void constructTileBody( float x, float y, int width, int height ) {

		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( x * GameScreen.PIXEL_TO_BOX, y
				* GameScreen.PIXEL_TO_BOX ) );
		body = world.createBody( groundBodyDef );

		PolygonShape groundBox = new PolygonShape( );
		if ( width == 0 )
			width = 1;
		if ( height == 0 )
			height = 1;
		groundBox.setAsBox( ( width * tileConstant ) * GameScreen.PIXEL_TO_BOX,
				( height * tileConstant ) * GameScreen.PIXEL_TO_BOX );

		FixtureDef platformFixtureDef = new FixtureDef( );
		platformFixtureDef.shape = groundBox;
		platformFixtureDef.density = 1.9f;
		platformFixtureDef.friction = 0.5f;
		platformFixtureDef.restitution = 0.0f;
		body.setGravityScale( .1f );
		body.createFixture( platformFixtureDef );

	}

	public void update( ) {
		super.update( );
	}

}