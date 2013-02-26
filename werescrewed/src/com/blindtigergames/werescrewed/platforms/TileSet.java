/**
 * 
 */
package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.blindtigergames.werescrewed.graphics.TextureAtlasS;

/**
 * Original by Ander
 * Reworked TileSet by Stew.  Used to build a tiledplatform.
 * @author Anders / Stew
 * 
 */
public class TileSet {
	private TextureAtlasS atlas;
	private int tileHeight, tileWidth;

	/**
	 * Creates a Tile Set for tiled Entities. Assumes all tiles are the w/h of the first tile
	 *  and that the tileset is 4x4
	 * 
	 * @param texture
	 *            The set of tiles that is needed for a tile set.
	 * 
	 */
	public TileSet( TextureAtlasS atlas ) {
		this.atlas = atlas;
		Texture first = atlas.getTextures( ).iterator( ).next( );
		this.tileHeight = first.getHeight( ) / 4;
		this.tileWidth = first.getWidth( ) / 4;
	}
	
	private enum TiIdx{
		Single(1),HorizontalLeft(2),HorizontalMiddle(3),HorizontalRight(4),
		VerticalTop(5),VerticalMiddle(9),VerticalBottom(13),RectangleUpperLeft(6),
		RectangleUpperMiddle(7),RectangleUpperRight(8),RectangleMiddleLeft(10),
		RectangleMiddleCenter(11),RectangleMiddleRight(12),RectangleBottomLeft(14),
		RectangleBottomMiddle(15),RectangleBottomRight(16);
		
		public int value;

    	private TiIdx(int value) {
            this.value = value;
    	}
	};
	
	
	public Sprite getSingleTile( ) {
		return ( atlas.createSprite( TiIdx.Single.value+"" ) );
	}

	public Sprite getHorizontalLeftTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalLeft.value+"" ) );
	}

	public Sprite getHorizontalMiddleTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalMiddle.value+"" ) );
	}
	public Sprite getHorizontalRightTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalRight.value+"" ) );
	}
	public Sprite getVerticalTopTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalTop.value+"" ) );
	}
	public Sprite getVerticalMiddleTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalMiddle.value+"" ) );
	}
	public Sprite getVerticalBottomTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalBottom.value+"" ) );
	}
	public Sprite getRectangleUpperLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperLeft.value+"" ) );
	}
	public Sprite getRectangleUpperMiddle( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperMiddle.value+"" ) );
	}
	public Sprite getRectangleUpperRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperRight.value+"" ) );
	}
	public Sprite getRectangleMiddleLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleLeft.value+"" ) );
	}
	public Sprite getRectangleMiddleCenter( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleCenter.value+"" ) );
	}

	public Sprite getRectangleMiddleRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleRight.value+"" ) );
	}

	public Sprite getRectangleBottomLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomLeft.value+"" ) );
	}

	public Sprite getRectangleBottomMiddle( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomMiddle.value+"" ) );
	}
	public Sprite getRectangleBottomRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomRight.value+"" ) );
	}
}
