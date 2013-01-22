package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.screens.GameScreen;
import java.lang.Math;
import java.util.Iterator;
import java.util.Vector;

/**
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

public class TiledPlatform extends Platform {
	protected boolean rotate = false;
	protected int tileHeight, tileWidth;
	protected float tileSize;
	protected Vector<Sprite> tiles;
	protected float posx, posy; 

	public TiledPlatform( String n, Vector2 pos, Texture tex, int width,
			int height, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.tileSize = tileConstant * 2 * GameScreen.BOX_TO_PIXEL; 
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructTileBody( pos.x, pos.y, width, height );
		tiles = new Vector<Sprite>();
		for (int i = 0; i < width; i++ ){
			
			tiles.add( this.sprite );
		}
	}

	private void constructTileBody( float x, float y, int width, int height ) {

		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( x , y ) );
		body = world.createBody( groundBodyDef );

		PolygonShape groundBox = new PolygonShape( );
		if ( width == 0 )
			width = 1;
		if ( height == 0 )
			height = 1;
		groundBox.setAsBox( ( width * tileConstant ) * GameScreen.PIXEL_TO_BOX,
				( height * tileConstant ) * GameScreen.PIXEL_TO_BOX );

		sprite.setPosition( body.getPosition( ).x, body.getPosition( ).y - sprite.getHeight( ) / 2);
		sprite.setOrigin( 0 , sprite.getHeight( ) / 2);

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
//		sprite.setOrigin( body.getWorldCenter( ).x, body.getWorldCenter( ).y);
//		Iterator<Sprite> v = tiles.listIterator( ); 
//		while (v.hasNext( )) {
//			posx = body.getPosition( ).x - 
//			v.next( ).setPosition( posx, posy );
//		}
	}
	
//	@Override 
//	public void draw(SpriteBatch batch ) {
//		Iterator<Sprite> v = tiles.listIterator( ); 
//		while (v.hasNext( )) {
//			v.next( ).draw( batch );
//		}
//		
//	}
	

}