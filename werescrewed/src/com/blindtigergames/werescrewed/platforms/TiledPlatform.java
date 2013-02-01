package com.blindtigergames.werescrewed.platforms;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * @author Ranveer
 * 
 */

public class TiledPlatform extends Platform {
	protected float tileHeight, tileWidth;
	protected Vector2 bodypos;

	protected class Tile {
		public float xOffset, yOffset;
		public Sprite tileSprite;

		public Tile( ) {
			xOffset = 0;
			yOffset = 0;
			sprite = null;
		}

		public Tile( float offset_x, float offset_y, Sprite the_sprite ) {
			xOffset = offset_x;
			yOffset = offset_y;
			tileSprite = the_sprite;
		}
	}

	protected Vector< Tile > tiles;

	public TiledPlatform( String n, Vector2 pos, Texture tex, float width,
			float height, boolean isOneSided, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructTileBody( pos.x, pos.y, width, height );
		body.setUserData( this );
		setOneSided( isOneSided );
		tileBody( );
	}

	private void constructTileBody( float x, float y, float width, float height ) {

		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set( new Vector2( x * Util.PIXEL_TO_BOX, y
				* Util.PIXEL_TO_BOX ) );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( ( width * tileConstant ) * Util.PIXEL_TO_BOX,
				( height * tileConstant ) * Util.PIXEL_TO_BOX );
		//
		// sprite.setPosition( body.getPosition( ).x - sprite.getWidth( ) / 2,
		// body.getPosition( ).y - sprite.getHeight( ) / 2);
		// sprite.setOrigin( sprite.getWidth( ) / 2 , sprite.getHeight( ) / 2);

		FixtureDef platformFixtureDef = new FixtureDef( );
		platformFixtureDef.shape = polygon;
		body.createFixture( platformFixtureDef );

		polygon.dispose( );
	}

	private void tileBody( ) {
		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		tiles = new Vector< Tile >( ( int ) ( tileHeight * tileWidth ) );
		Sprite temp;
		Tile insub;
		float offset_x, offset_y;
		for ( int i = 0; i < tileWidth; i++ ) {
			offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
			for ( int j = 0; j < tileHeight; j++ ) {
				temp = new Sprite( sprite );
				offset_y = ( j - tileHeight / 2 + 1 ) * tileConstant * 2;
				temp.setOrigin( offset_x, offset_y );
				temp.setPosition( bodypos.x - offset_x, bodypos.y - offset_y );
				temp.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
				insub = new Tile( offset_x, offset_y, temp );
				tiles.add( insub );
			}
		}
	}

	public float getActualHeight( ) {
		return height * 32;
	}

	public float getActualWidth( ) {
		return width * 32;
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			setOneSided( !getOneSided( ) );
			System.out.println( getOneSided( ) );
		}
		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
	}

	@Override
	public void draw( SpriteBatch batch ) {
		Tile d;
		Iterator< Tile > v = tiles.listIterator( );
		while ( v.hasNext( ) ) {
			d = v.next( );
			d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
					- d.yOffset );
			d.tileSprite.setRotation( MathUtils.radiansToDegrees
					* body.getAngle( ) );
			d.tileSprite.draw( batch );
		}
		for ( Screw s : screws )
			s.draw( batch );
	}

}