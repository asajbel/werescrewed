package com.blindtigergames.werescrewed.asset;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
	
	public AssetManager(){
		super();
		atlasMap = new HashMap< String, TextureAtlasS >( );
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
	
	@Override
	public void dispose(){
		super.dispose( );
		atlasMap.clear( );
	}
}
