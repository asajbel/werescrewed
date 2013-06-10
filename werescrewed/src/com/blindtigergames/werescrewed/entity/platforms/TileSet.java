/**
 * 
 */
package com.blindtigergames.werescrewed.entity.platforms;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;

/**
 * Original by Ander Reworked TileSet by Stew. Used to build a tiledplatform.
 * 
 * @author Anders / Stew
 * 
 */
public class TileSet {
	private TextureAtlas atlas;
	private TextureAtlas bleedAtlas;
	//@SuppressWarnings( "unused" )
	//private int tileHeight, tileWidth;
	int r = 1;

	public TileSet( TextureAtlas atlas, TextureAtlas bleedAtlas ) {
		this.atlas = atlas;
		this.bleedAtlas = bleedAtlas;
		this.bleedAtlas = WereScrewedGame.manager.getAtlas( "common-textures" );
	}

	public boolean canBleed( ) {
		return bleedAtlas != null;
	}

	private enum TiIdx {
		Single( 1 ), HorizontalLeft( 2 ), HorizontalMiddle( 3 ), HorizontalRight(
				4 ), VerticalTop( 5 ), VerticalMiddle( 9 ), VerticalBottom( 13 ), RectangleUpperLeft(
				6 ), RectangleUpperMiddle( 7 ), RectangleUpperRight( 8 ), RectangleMiddleLeft(
				10 ), RectangleMiddleCenter( 11 ), RectangleMiddleRight( 12 ), RectangleBottomLeft(
				14 ), RectangleBottomMiddle( 15 ), RectangleBottomRight( 16 );

		public int value;

		private TiIdx( int value ) {
			this.value = value;
		}
	};

	public Sprite getSingleTile( ) {
		return ( atlas.createSprite( TiIdx.Single.value + "" ) );
	}

	public Sprite getHorizontalLeftTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalLeft.value + "" ) );
	}

	public Sprite getHorizontalMiddleTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalMiddle.value + "" ) );
	}

	public Sprite getHorizontalRightTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalRight.value + "" ) );
	}

	public Sprite getVerticalTopTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalTop.value + "" ) );
	}

	public Sprite getVerticalMiddleTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalMiddle.value + "" ) );
	}

	public Sprite getVerticalBottomTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalBottom.value + "" ) );
	}

	public Sprite getRectangleUpperLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperLeft.value + "" ) );
	}

	public Sprite getRectangleUpperMiddle( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperMiddle.value + "" ) );
	}

	public Sprite getRectangleUpperRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperRight.value + "" ) );
	}

	public Sprite getRectangleMiddleLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleLeft.value + "" ) );
	}

	public Sprite getRectangleMiddleCenter( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleCenter.value + "" ) );
	}

	public Sprite getRectangleMiddleRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleRight.value + "" ) );
	}

	public Sprite getRectangleBottomLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomLeft.value + "" ) );
	}

	public Sprite getRectangleBottomMiddle( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomMiddle.value + "" ) );
	}

	public Sprite getRectangleBottomRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomRight.value + "" ) );
	}

	/**
	 * Bleed sprite functions
	 */
	private Sprite getBleedSprite( int position ) {
		//String randomColor = WereScrewedGame.manager.getRandomPaletteColor( );

		// System.out.println( "GetRanColor:"+randomColor+position );
		// Gdx.app.log( "TileSet",
		// "Attempting to fetch: "+randomColor+" "+position );
		if ( r == 1 )
			r = 2;
		else
			r = 1;
		return ( bleedAtlas.createSprite( "tiletexture" + ( r ) ) );// bleedAtlas.createSprite(
																	// randomColor+position
																	// )
	}

	public Sprite getHorizontalLeftTileBleed( ) {
		return getBleedSprite( TiIdx.HorizontalLeft.value );
	}

	public Sprite getHorizontalMiddleTileBleed( ) {
		return getBleedSprite( TiIdx.HorizontalMiddle.value );
	}

	public Sprite getHorizontalRightTileBleed( ) {
		return getBleedSprite( TiIdx.HorizontalRight.value );
	}

	public Sprite getVerticalTopTileBleed( ) {
		return getBleedSprite( TiIdx.VerticalTop.value );
	}

	public Sprite getVerticalMiddleTileBleed( ) {
		return getBleedSprite( TiIdx.VerticalMiddle.value );
	}

	public Sprite getVerticalBottomTileBleed( ) {
		return getBleedSprite( TiIdx.VerticalBottom.value );
	}

	public Sprite getRectangleUpperLeftBleed( ) {
		return getBleedSprite( TiIdx.RectangleUpperLeft.value );
	}

	public Sprite getRectangleUpperMiddleBleed( ) {
		return getBleedSprite( TiIdx.RectangleUpperMiddle.value );
	}

	public Sprite getRectangleUpperRightBleed( ) {
		return getBleedSprite( TiIdx.RectangleUpperRight.value );
	}

	public Sprite getRectangleMiddleLeftBleed( ) {
		return getBleedSprite( TiIdx.RectangleMiddleLeft.value );
	}

	public Sprite getRectangleMiddleCenterBleed( ) {
		return getBleedSprite( TiIdx.RectangleMiddleCenter.value );
	}

	public Sprite getRectangleMiddleRightBleed( ) {
		return getBleedSprite( TiIdx.RectangleMiddleRight.value );
	}

	public Sprite getRectangleBottomLeftBleed( ) {
		return getBleedSprite( TiIdx.RectangleBottomLeft.value );
	}

	public Sprite getRectangleBottomMiddleBleed( ) {
		return getBleedSprite( TiIdx.RectangleBottomMiddle.value );
	}

	public Sprite getRectangleBottomRightBleed( ) {
		return getBleedSprite( TiIdx.RectangleBottomRight.value );
	}
}
