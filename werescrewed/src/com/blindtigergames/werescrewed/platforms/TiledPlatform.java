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
 * @author Ranveer / Stew / Anders
 * 
 */

public class TiledPlatform extends Platform {
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

	protected enum Shape {
		SINGLE, VERTICAL, HORIZONTAL, RECTANGLE
	}

	protected float tileHeight, tileWidth;
	protected Vector2 bodypos;
	protected TileSet tileSet;

	protected Shape shape;// = Shape.SINGLE;

	protected Vector< Tile > tiles;

	public TiledPlatform( String n, Vector2 pos, Texture tex, float width,
			float height, boolean isOneSided, boolean isMoveable, World world ) {
		super( n, pos, tex, world );
		platType = PlatformType.TILED;
		this.tileSet = new TileSet( tex );
		this.tileHeight = height;
		this.tileWidth = width;

		if ( height > 1 && width > 1 ) {
			shape = Shape.RECTANGLE;
		} else if ( width > 1 ) {
			shape = Shape.HORIZONTAL;
		} else if ( height > 1 ) {
			shape = Shape.VERTICAL;
		} else {
			shape = Shape.SINGLE;
		}

		if ( tex.getHeight( ) != 128 && tex.getWidth( ) != 128 ) {
			shape = Shape.SINGLE;
		}

		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructTileBody( pos.x, pos.y, width, height );
		body.setUserData( this );
		setOneSided( isOneSided );
		this.moveable = isMoveable;
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
		platformFixtureDef.filter.categoryBits = Util.DYNAMIC_OBJECTS;
		platformFixtureDef.filter.maskBits = Util.DYNAMIC_OBJECTS
				| Util.CATEGORY_PLAYER;
		body.createFixture( platformFixtureDef );

		polygon.dispose( );
	}

	private void tileBody( ) {
		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		tiles = new Vector< Tile >( ( int ) ( tileHeight * tileWidth ) );
		Sprite temp;
		Tile insub;
		float offset_x, offset_y;
		switch ( shape ) {
		case SINGLE:
			temp = tileSet.getSingleTile( );
			offset_x = temp.getWidth( ) / 2;
			offset_y = temp.getHeight( ) / 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			break;
		case VERTICAL:
			temp = tileSet.getVerticalTopTile( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( 0 - tileHeight / 2 + 1 ) * tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			for ( int j = 1; j < tileHeight - 1; j++ ) {
				temp = tileSet.getVerticalMiddleTile( );
				offset_y = ( j - tileHeight / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
			}
			temp = tileSet.getVerticalBottomTile( );
			offset_y = ( ( tileHeight - 1 ) - tileHeight / 2 + 1 )
					* tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			break;
		case HORIZONTAL:
			temp = tileSet.getHorizontalRightTile( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( 0 - tileHeight / 2 + 1 ) * tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			for ( int i = 1; i < tileWidth - 1; i++ ) {
				temp = tileSet.getHorizontalMiddleTile( );
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
			}
			temp = tileSet.getHorizontalLeftTile( );
			offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 ) * tileConstant
					* 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			break;
		case RECTANGLE:
			temp = tileSet.getRectangleUpperRight( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( 0 - tileHeight / 2 + 1 ) * tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			for ( int i = 1; i < tileWidth - 1; i++ ) {
				temp = tileSet.getRectangleUpperMiddle( );
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
			}
			temp = tileSet.getRectangleUpperLeft( );
			offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 ) * tileConstant
					* 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );

			for ( int j = 1; j < tileHeight - 1; j++ ) {
				temp = tileSet.getRectangleMiddleRight( );
				offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
				offset_y = ( j - tileHeight / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				for ( int i = 1; i < tileWidth - 1; i++ ) {
					temp = tileSet.getRectangleMiddleCenter( );
					offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
					temp.setOrigin( offset_x, offset_y );
					temp.setPosition( bodypos.x - offset_x, bodypos.y
							- offset_y );
					temp.setRotation( MathUtils.radiansToDegrees
							* body.getAngle( ) );
					insub = new Tile( offset_x, offset_y, temp );
					tiles.add( insub );
				}
				temp = tileSet.getRectangleMiddleLeft( );
				offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 )
						* tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
			}

			temp = tileSet.getRectangleBottomRight( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( ( tileHeight - 1 ) - tileHeight / 2 + 1 )
					* tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			for ( int i = 1; i < tileWidth - 1; i++ ) {
				temp = tileSet.getRectangleBottomMiddle( );
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
			}
			temp = tileSet.getRectangleBottomLeft( );
			offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 ) * tileConstant
					* 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			break;
		default:
			for ( int i = 0; i < tileWidth; i++ ) {
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				for ( int j = 0; j < tileHeight; j++ ) {
					temp = tileSet.getSingleTile( );
					offset_y = ( j - tileHeight / 2 + 1 ) * tileConstant * 2;
					temp.setOrigin( offset_x, offset_y );
					temp.setPosition( bodypos.x - offset_x, bodypos.y
							- offset_y );
					temp.setRotation( MathUtils.radiansToDegrees
							* body.getAngle( ) );
					insub = new Tile( offset_x, offset_y, temp );
					tiles.add( insub );
				}
			}
			break;
		}
	}

	private Tile setTile( Sprite temp, float offset_x, float offset_y ) {
		temp.setOrigin( offset_x, offset_y );
		temp.setPosition( bodypos.x - offset_x, bodypos.y - offset_y );
		temp.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
		return ( new Tile( offset_x, offset_y, temp ) );
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