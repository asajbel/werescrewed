package com.blindtigergames.werescrewed.entity.platforms;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Tiled platform that uses texture atlas
 * 
 * @author Ranveer / Stew / Anders
 * 
 */

public class TiledPlatform extends Platform {

	protected enum Shape {
		SINGLE, VERTICAL, HORIZONTAL, RECTANGLE
	}

	protected float tileHeight, tileWidth;
	protected Vector2 bodypos;

	protected Shape shape;// = Shape.SINGLE;

	protected Vector< Tile > tiles;
	protected Vector< Tile > bleedTiles = null;
	protected Vector< Tile > decal;
	
	protected Color tileColor;

	// private boolean doBleed;

	// private String tileSetName;

	public TiledPlatform( String n, Vector2 pos, TileSet tileSet, float width,
			float height, boolean isOneSided, boolean isMoveable, World world ) {
		super( n, pos, null, world );
		platType = PlatformType.TILED;
		// this.tileSet = tileset;
		this.tileHeight = height;
		this.tileWidth = width;
		
		tileColor = WereScrewedGame.manager.getTileColor( );

		if ( tileHeight > 1 && tileWidth > 1 ) {
			shape = Shape.RECTANGLE;
		} else if ( tileWidth > 1 ) {
			shape = Shape.HORIZONTAL;
		} else if ( tileHeight > 1 ) {
			shape = Shape.VERTICAL;
		} else {
			shape = Shape.SINGLE;
		}

		this.width = tileWidth * tileConstant;
		this.height = tileHeight * tileConstant;
		constructTileBody( pos.x, pos.y, tileWidth, tileHeight );
		body.setUserData( this );
		setOneSided( isOneSided );
		this.moveable = isMoveable;
		tileBody( tileSet );
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
		platformFixtureDef.filter.categoryBits = Util.CATEGORY_PLATFORMS;
		platformFixtureDef.filter.maskBits = Util.CATEGORY_PLATFORMS
				| Util.CATEGORY_PLAYER;
		body.createFixture( platformFixtureDef );

		polygon.dispose( );

	}

	private void tileBody( TileSet tileSet ) {
		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		tiles = new Vector< Tile >( ( int ) ( tileHeight * tileWidth ) );
		decal = new Vector< Tile >( ( int ) ( tileHeight * tileWidth ) );
		if ( tileSet.canBleed( ) && !oneSided ) {
			bleedTiles = new Vector< Tile >( ( int ) ( tileHeight * tileWidth ) );
		}
		// Gdx.app.log( "TP:"+name+":", doBleed+"" );

		Sprite temp;
		Tile insub;
		float offset_x, offset_y;
		switch ( shape ) {
		case SINGLE:
			temp = tileSet.getSingleTile( );
			offset_x = tileConstant / 2 + temp.getOriginX( ) + 1; // not sure
																	// why +1
																	// works
			offset_y = tileConstant / 2 + temp.getOriginY( );
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			break;
		case VERTICAL:
			temp = tileSet.getVerticalTopTile( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( 0 - tileHeight / 2 + 1 ) * tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getVerticalTopTileBleed( ), offset_x,
						offset_y );
			for ( int j = 1; j < tileHeight - 1; j++ ) {
				temp = tileSet.getVerticalMiddleTile( );
				offset_y = ( j - tileHeight / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				if ( bleedTiles != null )
					setBleedTile( tileSet.getVerticalMiddleTileBleed( ),
							offset_x, offset_y );
			}
			temp = tileSet.getVerticalBottomTile( );
			offset_y = ( ( tileHeight - 1 ) - tileHeight / 2 + 1 )
					* tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getVerticalBottomTileBleed( ), offset_x,
						offset_y );
			break;
		case HORIZONTAL:
			temp = tileSet.getHorizontalRightTile( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( 0 - tileHeight / 2 + 1 ) * tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getHorizontalRightTileBleed( ), offset_x,
						offset_y );
			for ( int i = 1; i < tileWidth - 1; i++ ) {
				temp = tileSet.getHorizontalMiddleTile( );
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				if ( bleedTiles != null )
					setBleedTile( tileSet.getHorizontalMiddleTileBleed( ),
							offset_x, offset_y );
			}
			temp = tileSet.getHorizontalLeftTile( );
			offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 ) * tileConstant
					* 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getHorizontalLeftTileBleed( ), offset_x,
						offset_y );
			break;
		case RECTANGLE:
			temp = tileSet.getRectangleUpperRight( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( 0 - tileHeight / 2 + 1 ) * tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getRectangleUpperRightBleed( ), offset_x,
						offset_y );
			for ( int i = 1; i < tileWidth - 1; i++ ) {
				temp = tileSet.getRectangleUpperMiddle( );
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				if ( bleedTiles != null )
					setBleedTile( tileSet.getRectangleUpperMiddleBleed( ),
							offset_x, offset_y );
			}
			temp = tileSet.getRectangleUpperLeft( );
			offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 ) * tileConstant
					* 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getRectangleUpperLeftBleed( ), offset_x,
						offset_y );

			for ( int j = 1; j < tileHeight - 1; j++ ) {
				temp = tileSet.getRectangleMiddleRight( );
				offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
				offset_y = ( j - tileHeight / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				if ( bleedTiles != null )
					setBleedTile( tileSet.getRectangleMiddleRightBleed( ),
							offset_x, offset_y );
				for ( int i = 1; i < tileWidth - 1; i++ ) {
					temp = tileSet.getRectangleMiddleCenter( );
					offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
					insub = setTile( temp, offset_x, offset_y );
					tiles.add( insub );
					if ( bleedTiles != null && Math.random( ) > 0.33 )
						setBleedTile( tileSet.getRectangleMiddleCenterBleed( ),
								offset_x, offset_y );
				}
				temp = tileSet.getRectangleMiddleLeft( );
				offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 )
						* tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				if ( bleedTiles != null )
					setBleedTile( tileSet.getRectangleMiddleLeftBleed( ),
							offset_x, offset_y );
			}

			temp = tileSet.getRectangleBottomRight( );
			offset_x = ( 0 - tileWidth / 2 + 1 ) * tileConstant * 2;
			offset_y = ( ( tileHeight - 1 ) - tileHeight / 2 + 1 )
					* tileConstant * 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getRectangleBottomRightBleed( ),
						offset_x, offset_y );
			for ( int i = 1; i < tileWidth - 1; i++ ) {
				temp = tileSet.getRectangleBottomMiddle( );
				offset_x = ( i - tileWidth / 2 + 1 ) * tileConstant * 2;
				insub = setTile( temp, offset_x, offset_y );
				tiles.add( insub );
				if ( bleedTiles != null )
					setBleedTile( tileSet.getRectangleBottomMiddleBleed( ),
							offset_x, offset_y );
			}
			temp = tileSet.getRectangleBottomLeft( );
			offset_x = ( ( tileWidth - 1 ) - tileWidth / 2 + 1 ) * tileConstant
					* 2;
			insub = setTile( temp, offset_x, offset_y );
			tiles.add( insub );
			if ( bleedTiles != null )
				setBleedTile( tileSet.getRectangleBottomLeftBleed( ), offset_x,
						offset_y );
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

	private void setBleedTile( Sprite temp, float offset_x, float offset_y ) {
		offset_x += temp.getOriginX( );
		offset_y += temp.getOriginY( );
		temp.setOrigin( offset_x, offset_y );
		temp.setPosition( bodypos.x - offset_x, bodypos.y - offset_y );
		temp.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
		bleedTiles.add( new Tile( offset_x, offset_y, temp ) );

	}

	private Tile setTile( Sprite temp, float offset_x, float offset_y ) {
		if ( oneSided ) {
			offset_x += 16;
			offset_y += 16;
		} else {
			offset_x += temp.getOriginX( );
			offset_y += temp.getOriginY( );
		}
		temp.setOrigin( offset_x, offset_y );
		temp.setPosition( bodypos.x - offset_x, bodypos.y - offset_y );
		temp.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
		if(!oneSided) temp.setColor( tileColor );
		return ( new Tile( offset_x, offset_y, temp ) );
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
	}

	@Override
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		super.draw(batch,deltaTime, camera);
		// drawBGDecals( batch );
		if ( visible ) {
			Tile d;
			Iterator< Tile > v = tiles.listIterator( );
			while ( v.hasNext( ) ) {
				d = v.next( );
				d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
						- d.yOffset );
				d.tileSprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
				if ( d.tileSprite.getBoundingRectangle( ).overlaps( camera.getBounds( ) ) ) {
					d.tileSprite.draw( batch );
				}
			}
			// for ( Screw s : screws )
			// s.draw( batch, deltaTime );
			//
			// if ( bleedTiles != null ){
			// v = bleedTiles.listIterator( );
			// while ( v.hasNext( ) ) {
			// d = v.next( );
			// d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
			// - d.yOffset );
			// d.tileSprite.setRotation( MathUtils.radiansToDegrees
			// * body.getAngle( ) );
			// d.tileSprite.draw( batch );
			// }
			// }
		}
		// drawOrigin( batch );
		// drawFGDecals( batch );
	}

	/**
	 * Get the actual sprite width of this tiled platform
	 * 
	 * @return Pixel float width of tiled platform
	 */
	@Override
	public float getPixelWidth( ) {
		return width * 2;
	}

	/**
	 * Get the actual sprite height of this tiled platform
	 * 
	 * @return Pixel float height of tiled platform
	 */
	@Override
	public float getPixelHeight( ) {
		return height * 2;
	}

	/**
	 * Get the actual sprite meter width of this tiled platform
	 * 
	 * @return METER float width of tiled platform
	 */
	@Override
	public float getMeterWidth( ) {
		return width * 2 * Util.PIXEL_TO_BOX;
	}

	/**
	 * Get the actual sprite METER height of this tiled platform
	 * 
	 * @return METER float height of tiled platform
	 */
	@Override
	public float getMeterHeight( ) {
		return height * 2 * Util.PIXEL_TO_BOX;
	}
	
	public void setTileColor(int r, int g, int b){
		for(Tile t:tiles){
			t.tileSprite.setColor( r/255f, g/255f, b/255f, 1.0f );
		}
	}
	
	public void setTileColor(Color c){
		for(Tile t:tiles){
			t.tileSprite.setColor( c );
		}
	}
	
	public void setTilesBlack(){
		this.setTileColor(0,0,0);
	}
	
	public void setTilesGold(){
		//this.setTileColor(30, 102, 226);
	}

}