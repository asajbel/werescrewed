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

//Need to fix widht/height storage
public class TiledPlatform extends Platform {
	protected boolean rotate = false;
	protected boolean oneSided;
	protected float tileHeight, tileWidth;

	public TiledPlatform( String n, Vector2 pos, Texture tex, float width,
			float height, boolean isOneSided, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructTileBody( pos.x, pos.y, width, height );
		body.setUserData(this);
		setOneSided( isOneSided );
	}

	private void constructTileBody( float x, float y, float width, float height ) {

		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( new Vector2( x , y ) );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( ( width * tileConstant ) * GameScreen.PIXEL_TO_BOX,
				( height * tileConstant ) * GameScreen.PIXEL_TO_BOX );

		FixtureDef platformFixtureDef = new FixtureDef( );
		platformFixtureDef.shape = polygon;
		body.createFixture( platformFixtureDef );

		polygon.dispose( );
	}
	
	public void setOneSided( boolean value ){
		oneSided = value;
	}
	
	public boolean getOneSided(){
		return oneSided;
	}
	
	public float getActualHeight(){
	    return height * 32;
	}
	
	public float getActualWidth(){
	    return width * 32;
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			setOneSided(!getOneSided());
			System.out.println(getOneSided());
		}
	}
	

}