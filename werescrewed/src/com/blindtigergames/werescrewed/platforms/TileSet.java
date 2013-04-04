/**
 * 
 */
package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;

/**
 * Original by Ander
 * Reworked TileSet by Stew.  Used to build a tiledplatform.
 * @author Anders / Stew
 * 
 */
public class TileSet {
	private TextureAtlas atlas;
	private TextureAtlas bleedAtlas;
	@SuppressWarnings( "unused" )
	private int tileHeight, tileWidth;

	
	public TileSet( TextureAtlas atlas, TextureAtlas bleedAtlas ){
		this.atlas = atlas;
		this.bleedAtlas = bleedAtlas;
	}

	public boolean canBleed(){
		return bleedAtlas != null;
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
	
	public Image getSingleTile( ) {
		return ( atlas.createSprite( TiIdx.Single.value+"" ) );
	}

	public Image getHorizontalLeftTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalLeft.value+"" ) );
	}

	public Image getHorizontalMiddleTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalMiddle.value+"" ) );
	}
	public Image getHorizontalRightTile( ) {
		return ( atlas.createSprite( TiIdx.HorizontalRight.value+"" ) );
	}
	public Image getVerticalTopTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalTop.value+"" ) );
	}
	public Image getVerticalMiddleTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalMiddle.value+"" ) );
	}
	public Image getVerticalBottomTile( ) {
		return ( atlas.createSprite( TiIdx.VerticalBottom.value+"" ) );
	}
	public Image getRectangleUpperLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperLeft.value+"" ) );
	}
	public Image getRectangleUpperMiddle( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperMiddle.value+"" ) );
	}
	public Image getRectangleUpperRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleUpperRight.value+"" ) );
	}
	public Image getRectangleMiddleLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleLeft.value+"" ) );
	}
	public Image getRectangleMiddleCenter( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleCenter.value+"" ) );
	}

	public Image getRectangleMiddleRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleMiddleRight.value+"" ) );
	}

	public Image getRectangleBottomLeft( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomLeft.value+"" ) );
	}

	public Image getRectangleBottomMiddle( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomMiddle.value+"" ) );
	}
	public Image getRectangleBottomRight( ) {
		return ( atlas.createSprite( TiIdx.RectangleBottomRight.value+"" ) );
	}
	
	
	
	/**
	 * Bleed sprite functions
	 */
	private Image getBleedSprite(int position){
		String randomColor = WereScrewedGame.manager.getRandomPaletteColor( );

		//System.out.println( "GetRanColor:"+randomColor+position );
		return ( bleedAtlas.createSprite( randomColor+position ) );
	}
	
	public Image getHorizontalLeftTileBleed( ) {
		return getBleedSprite( TiIdx.HorizontalLeft.value );
	}
	public Image getHorizontalMiddleTileBleed( ) {
		return getBleedSprite( TiIdx.HorizontalMiddle.value );
	}
	public Image getHorizontalRightTileBleed( ) {
		return getBleedSprite( TiIdx.HorizontalRight.value );
	}
	public Image getVerticalTopTileBleed( ) {
		return getBleedSprite( TiIdx.VerticalTop.value);
	}
	public Image getVerticalMiddleTileBleed( ) {
		return getBleedSprite( TiIdx.VerticalMiddle.value);
	}
	public Image getVerticalBottomTileBleed( ) {
		return getBleedSprite(  TiIdx.VerticalBottom.value );
	}
	public Image getRectangleUpperLeftBleed( ) {
		return getBleedSprite( TiIdx.RectangleUpperLeft.value );
	}
	public Image getRectangleUpperMiddleBleed( ) {
		return getBleedSprite( TiIdx.RectangleUpperMiddle.value );
	}
	public Image getRectangleUpperRightBleed( ) {
		return getBleedSprite( TiIdx.RectangleUpperRight.value );
	}
	public Image getRectangleMiddleLeftBleed( ) {
		return getBleedSprite( TiIdx.RectangleMiddleLeft.value );
	}
	public Image getRectangleMiddleCenterBleed( ) {
		return getBleedSprite( TiIdx.RectangleMiddleCenter.value);
	}
	public Image getRectangleMiddleRightBleed( ) {
		return getBleedSprite( TiIdx.RectangleMiddleRight.value);
	}
	public Image getRectangleBottomLeftBleed( ) {
		return getBleedSprite( TiIdx.RectangleBottomLeft.value );
	}
	public Image getRectangleBottomMiddleBleed( ) {
		return getBleedSprite(  TiIdx.RectangleBottomMiddle.value );
	}
	public Image getRectangleBottomRightBleed( ) {
		return getBleedSprite(  TiIdx.RectangleBottomRight.value );
	}
}
