package com.blindtigergames.werescrewed.asset;

import java.io.FileNotFoundException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.graphics.TextureAtlasS;
import com.blindtigergames.werescrewed.platforms.TileSet;

/**
 * Wrapper to badlogic's AssetManager with added functionality
 * like texture atlas loading. This can be expanded with 
 * functionality when we see fit.
 * @author stew
 *
 */

public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
	HashMap< String, TextureAtlasS > atlasMap;
	HashMap< String, BitmapFont > fontMap;
	
	public AssetManager(){
		super();
		atlasMap = new HashMap< String, TextureAtlasS >( );
		fontMap = new HashMap<String, BitmapFont>();
	}
	
	/**
	 * Get a TileSet by name. Make sure you use loadAtlas() first.
	 * @param name, no need for parent directory, just the name of the pack file
	 * @return TileSet with your atlas loaded in.
	 */
	public TileSet getTileSet(String name){
		return new TileSet( atlasMap.get( name ) );
	}
	
	public void loadAtlas( String fullPathToAtlas ){
		FileHandle fileHandle = Gdx.files.internal( fullPathToAtlas );
		atlasMap.put( fileHandle.nameWithoutExtension( ), new TextureAtlasS(fileHandle) );
	}
	
	/**
	 * @return if ya don't care about your tileset then use this
	 */
	public TileSet getDefaultTileSet(){
		return new TileSet( atlasMap.get( "TilesetTest" ) );
	}
	
	/**
	 * return a preloaded texture atlas with a name matching the param.
	 * @param atlasName name of texture atlas pack file without the extension
	 * @return Texture Atlas pre-loaded during loading screen.
	 */
	public TextureAtlasS getTextureAtlas( String atlasName ){
		return atlasMap.get( atlasName );
	}
	
	@SuppressWarnings( "unused" )
	public void loadFont( String fullPathToFont ) throws FileNotFoundException{
		FileHandle fileHandle = Gdx.files.internal( fullPathToFont );
		
		BitmapFont font = new BitmapFont(fileHandle, false);
		if ( font == null ){
			throw new FileNotFoundException("When loading a bitmap font path "+fileHandle.path( )+" font file wasn't found");
		}
		fontMap.put( fileHandle.nameWithoutExtension( ), font );
	}
	
	public BitmapFont getFont(String fontName){
		return fontMap.get( fontName );
	}
	
	@Override
	public void dispose(){
		super.dispose( );
		atlasMap.clear( );
	}
}
