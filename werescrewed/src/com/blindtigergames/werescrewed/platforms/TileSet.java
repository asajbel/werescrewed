/**
 * 
 */
package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Anders
 * 
 */
public class TileSet {
	private Texture tex;
	private int tileHeight, tileWidth;

	/**
	 * Creates a Tile Set for tiled Entities. Assumes a 4 by 4 set.
	 * 
	 * @param texture
	 *            The set of tiles that is needed for a tile set.
	 * 
	 */
	public TileSet( Texture texture ) {
		this.tex = texture;
		this.tileHeight = texture.getHeight( ) / 4;
		this.tileWidth = texture.getWidth( ) / 4;
	}

	// /**
	// * Creates a Tile Set for tiled Entities.
	// *
	// * @param texture
	// * The set of tiles that is needed for a tile set.
	// *
	// * @param width
	// * The number of tiles horizontally.
	// *
	// * @param height
	// * The number of tiles vertically.
	// *
	// */
	// public TileSet( Texture texture, int width, int height ) {
	// this.tex = texture;
	// this.tileHieght = texture.getHeight( ) / width;
	// this.tileWidth = texture.getWidth( ) / height;
	// }

	/**
	 * Returns the sprite from the set that is the single 1 by 1 tile.
	 * 
	 * @return A sprite that is the single tile.
	 */
	public Sprite getSingleTile( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the left tile of a horizontal x by 1
	 * tile.
	 * 
	 * @return A sprite that is the left tile of a horizontal tile set.
	 */
	public Sprite getHorizontalLeftTile( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth, 0, tileWidth,
				tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the middle tiles of a horizontal x by 1
	 * tile.
	 * 
	 * @return A sprite that is the middle tile of a horizontal tile set.
	 */
	public Sprite getHorizontalMiddleTile( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 2, 0,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the right tile of a horizontal x by 1
	 * tile.
	 * 
	 * @return A sprite that is the right tile of a horizontal tile set.
	 */
	public Sprite getHorizontalRightTile( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 3, 0,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the top tile of a vertical 1 by y
	 * tile.
	 * 
	 * @return A sprite that is the top tile of a vertical tile set.
	 */
	public Sprite getVerticalTopTile( ) {
		TextureRegion reg = new TextureRegion( tex, 0, tileHeight, tileWidth,
				tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the middle tiles of a vertical 1 by y
	 * tile.
	 * 
	 * @return A sprite that is the middle tile of a vertical tile set.
	 */
	public Sprite getVerticalMiddleTile( ) {
		TextureRegion reg = new TextureRegion( tex, 0, tileHeight * 2,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the bottom tile of a vertical 1 by y
	 * tile.
	 * 
	 * @return A sprite that is the bottom tile of a vertical tile set.
	 */
	public Sprite getVerticalBottomTile( ) {
		TextureRegion reg = new TextureRegion( tex, 0, tileHeight * 3,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the upper left tile of a rectangular x
	 * by y tile.
	 * 
	 * @return A sprite that is the upper left tile of a rectangular tile set.
	 */
	public Sprite getRectangleUpperLeft( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth, tileHeight,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the upper middle tile of a rectangular
	 * x by y tile.
	 * 
	 * @return A sprite that is the upper middle tile of a rectangular tile set.
	 */
	public Sprite getRectangleUpperMiddle( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 2, tileHeight,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the upper right tile of a rectangular x
	 * by y tile.
	 * 
	 * @return A sprite that is the upper right tile of a rectangular tile set.
	 */
	public Sprite getRectangleUpperRight( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 3, tileHeight,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the middle left tile of a rectangular x
	 * by y tile.
	 * 
	 * @return A sprite that is the middle left tile of a rectangular tile set.
	 */
	public Sprite getRectangleMiddleLeft( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth, tileHeight * 2,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the middle center tile of a rectangular
	 * x by y tile.
	 * 
	 * @return A sprite that is the middle center tile of a rectangular tile set.
	 */
	public Sprite getRectangleMiddleCenter( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 2,
				tileHeight * 2, tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the middle right tile of a rectangular
	 * x by y tile.
	 * 
	 * @return A sprite that is the middle right tile of a rectangular tile set.
	 */
	public Sprite getRectangleMiddleRight( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 3,
				tileHeight * 2, tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the bottom left tile of a rectangular x
	 * by y tile.
	 * 
	 * @return A sprite that is the bottom left tile of a rectangular tile set.
	 */
	public Sprite getRectangleBottomLeft( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth, tileHeight * 3,
				tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the bottom middle tile of a rectangular
	 * x by y tile.
	 * 
	 * @return A sprite that is the bottom middle tile of a rectangular tile set.
	 */
	public Sprite getRectangleBottomMiddle( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 2,
				tileHeight * 3, tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}

	/**
	 * Returns the sprite from the set that is the bottom right tile of a rectangular
	 * x by y.
	 * 
	 * @return A sprite that is the bottom right tile of a rectangular tile set.
	 */
	public Sprite getRectangleBottomRight( ) {
		TextureRegion reg = new TextureRegion( tex, tileWidth * 3,
				tileHeight * 3, tileWidth, tileHeight );
		return ( new Sprite( reg ) );
	}
}
