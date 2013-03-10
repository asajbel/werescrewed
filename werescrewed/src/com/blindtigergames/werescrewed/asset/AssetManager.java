package com.blindtigergames.werescrewed.asset;

import java.io.FileNotFoundException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.platforms.TileSet;

/**
 * Wrapper to badlogic's AssetManager with added functionality
 * like texture atlas loading. This can be expanded with 
 * functionality when we see fit.
 * @author stew
 *
 */

public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
	HashMap< String, TextureAtlas > atlasMap;
	HashMap< String, BitmapFont > fontMap;
	
	public AssetManager(){
		super();
		atlasMap = new HashMap< String, TextureAtlas >( );
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
		atlasMap.put( fileHandle.nameWithoutExtension( ), new TextureAtlas(fileHandle) );
	}
	
	public TextureAtlas getAtlas(String atlasPackName){
		return atlasMap.get( atlasPackName );
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
	public TextureAtlas getTextureAtlas( String atlasName ){
		return atlasMap.get( atlasName );
	}
	
	@SuppressWarnings( "unused" )
	public void loadFont( String fullPathToFont ){
		FileHandle fileHandle = Gdx.files.internal( fullPathToFont );
		
		BitmapFont font = new BitmapFont(fileHandle, false);
		if ( font == null ){
			Gdx.app.log( "AssetManager", "Font is null! "+fileHandle.path( ) );
		}
		fontMap.put( fileHandle.nameWithoutExtension( ), font );
		//Gdx.app.log( "AssetManager", "Size"+fontMap.size( )+" "+fileHandle.nameWithoutExtension( ) );
	}
	
	public boolean isFontLoaded(String fontName){
		return fontMap.get( fontName ) != null;
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
