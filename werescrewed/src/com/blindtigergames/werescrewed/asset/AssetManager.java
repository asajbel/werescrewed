package com.blindtigergames.werescrewed.asset;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.platforms.TileSet;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;

/**
 * Wrapper to badlogic's AssetManager with added functionality like texture
 * atlas loading. This can be expanded with functionality when we see fit.
 * 
 * @author stew
 * 
 */

public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
	HashMap< String, TextureAtlas > atlasMap;
	HashMap< String, BitmapFont > fontMap;
	ArrayList< String > palette;
	HashMap< String, ParticleEffect > particleEffects;
	HashMap< String, SkeletonData > spineSkeletons;
	HashMap< Class< ? >, String > dummyAssets;
	Texture robotTexBG;
	Texture robotOutlineTex;
	Texture robotTexFG;
	private int particleEffectCt = 0;
	private static Color tileColor;

	// TODO: set default values for this

	public AssetManager( ) {
		super( );
		atlasMap = new HashMap< String, TextureAtlas >( );
		fontMap = new HashMap< String, BitmapFont >( );
		palette = new ArrayList< String >( );
		particleEffects = new HashMap< String, ParticleEffect >( );
		dummyAssets = new HashMap< Class< ? >, String >( );
		tileColor = new Color(1f,1f,1f,1f);
		robotTexBG=null;
		robotOutlineTex=null;
		robotTexFG=null;
	}

	/**
	 * Get a TileSet by name. Make sure you use loadAtlas() first.
	 * 
	 * @param name
	 *            , no need for parent directory, just the name of the pack file
	 *            w/o extension
	 * @return TileSet with your atlas loaded in.
	 */
	public TileSet getTileSet( String name ) {
		return new TileSet( atlasMap.get( name ),
				atlasMap.get( name + "-bleed" ) );
	}

	/**
	 * gets the atlas map as an Array
	 */
	public Object[] getAtlases( ) {
		return atlasMap.keySet( ).toArray( );
	}
	
	/**
	 * unloads a texture atlas
	 */
	public void unloadAtlas( String name ) {
		if ( atlasMap.containsKey( name ) )
			atlasMap.remove( name ).dispose( );
	}
	
	/**
	 * in the level file do <colorName>.palette for each color of a level's
	 * palette These colors will be used to select bleeding effects on tiles.
	 * 
	 * @param colorName
	 */
	public void addToPalette( String colorName ) {
		palette.add( colorName );
	}

	/**
	 * Returns a random color name specified in the level index using
	 * <colorname>.palette
	 */
	public String getRandomPaletteColor( ) {
		return palette.get( WereScrewedGame.random.nextInt( palette.size( ) ) );
	}

	public TextureAtlas loadAtlas( String fullPathToAtlas ) {
		FileHandle fileHandle = Gdx.files.internal( fullPathToAtlas );
		TextureAtlas newAtlas = new TextureAtlas(
				fileHandle );
		String name = fileHandle.nameWithoutExtension( );
		TextureAtlas old = atlasMap.get( name );
		if(old!=null)
			atlasMap.remove( name ).dispose( );
		atlasMap.put( name, newAtlas );
		return newAtlas;
	}

	public TextureAtlas getAtlas( String atlasPackName ) {
		TextureAtlas out = atlasMap.get( atlasPackName );
		if ( out == null ) {
			throw new RuntimeException(
					"AssetManager:getAtlas() no texture atlas by name "
							+ atlasPackName + " is loaded" );
		}
		return atlasMap.get( atlasPackName );
	}

	/**
	 * @return if ya don't care about your tileset then use this
	 */
	public TileSet getDefaultTileSet( ) {
		return new TileSet( atlasMap.get( "TilesetTest" ), null );
	}

	/**
	 * return a preloaded texture atlas with a name matching the param.
	 * 
	 * @param atlasName
	 *            name of texture atlas pack file without the extension
	 * @return Texture Atlas pre-loaded during loading screen.
	 */
	public TextureAtlas getTextureAtlas( String atlasName ) {
		return atlasMap.get( atlasName );
	}

	public void loadFont( String fullPathToFont ) {
		FileHandle fileHandle = Gdx.files.internal( fullPathToFont );

		BitmapFont font = new BitmapFont( fileHandle, false );
		// if ( font == null ) {
		// Gdx.app.log( "AssetManager", "Font is null! " + fileHandle.path( ) );
		// }
		fontMap.put( fileHandle.nameWithoutExtension( ), font );
		// Gdx.app.log( "AssetManager", "Size"+fontMap.size(
		// )+" "+fileHandle.nameWithoutExtension( ) );
	}

	public boolean isFontLoaded( String fontName ) {
		return fontMap.get( fontName ) != null;
	}

	public BitmapFont getFont( String fontName ) {
		return fontMap.get( fontName );
	}

	public boolean isAtlasLoaded( String atlasName ) {
		return atlasMap.containsKey( atlasName );
	}

	public void loadParticleEffect( String particleEffectName ) {
		ParticleEffect effect = new ParticleEffect( );
		effect.load(
				Gdx.files.internal( "data/particles/" + particleEffectName
						+ ".p" ),
				WereScrewedGame.manager.getAtlas( "particles" ) );
		effect.name = particleEffectName;
		effect.effectName = particleEffectName;
		particleEffects.put( particleEffectName, effect );
	}

	/**
	 * Return an instance of the named particle effect!
	 * stew
	 * @param particleEffectName
	 * @return
	 */
	public ParticleEffect getParticleEffect( String particleEffectName ) {
		ParticleEffect out = particleEffects.get( particleEffectName );
		if ( out == null )
			throw new RuntimeException(
					"AssetManager: no particle effect is loaded with the name '"
							+ particleEffectName + "'" );
		out = new ParticleEffect(out);
		out.name = out.effectName+"_instance"+(particleEffectCt++);
		return new ParticleEffect( out );
	}

	public void loadSpineSkeleton( String skeletonName, TextureAtlas atlas ) {
		SkeletonBinary sb = new SkeletonBinary( atlas );
		SkeletonData sd = sb.readSkeletonData( Gdx.files
				.internal( "data/common/spine/" + skeletonName + ".skel" ) );
		spineSkeletons.put( skeletonName, sd );
	}

	/**
	 * Return the Skeleton data of the named skeleton
	 * 
	 * @param skeletonName
	 *            Name of the skeleton file minus .skel;
	 * @return
	 */
	public SkeletonData getSpineSkeleton( String skeletonName ) {
		SkeletonData out = spineSkeletons.get( skeletonName );
		if ( out == null )
			throw new RuntimeException(
					"AssetManager: no SkeletonData is loaded with the name '"
							+ skeletonName + "'" );
		return out;
	}

	@Override
	public void dispose( ) {
		super.dispose( );
		for(TextureAtlas atlas : atlasMap.values( ))
			atlas.dispose( );
		atlasMap.clear( );
	}

	/**
	 * @param fileName
	 *            the asset file name
	 * @return the asset
	 */
	@Override
	public synchronized < T > T get( String fileName, Class< T > type ) {
		return get( fileName, type, true );
	}

	public synchronized < T > T get( String fileName, Class< T > type,
			boolean loadDummies ) {
		try {
			return super.get( fileName, type );
		} catch ( GdxRuntimeException err ) {
			if ( loadDummies && dummyAssets.containsKey( type )
					&& !dummyAssets.get( type ).equalsIgnoreCase( fileName ) ) {
				// Gdx.app.log( "AssetManager", err.getMessage(
				// )+"("+type.getSimpleName( )+")");
				return get( dummyAssets.get( type ) );
			} else {
				throw err;
			}
		}
	}

	public void loadDummyAssets( ) {
		// String filename = WereScrewedGame.dirHandle + "/common/fail.png";
		dummyAssets.put( Texture.class, WereScrewedGame.dirHandle
				+ "/common/fail.png" );
		dummyAssets.put( Sound.class, WereScrewedGame.dirHandle
				+ "/common/fail.wav" );
		for ( Class< ? > type : dummyAssets.keySet( ) ) {
			this.load( dummyAssets.get( type ), type );
		}
	}
	
	/**
	 * Get a random rivet by name. then do commom-textures.createSprite(random
	 * rivet name).For alphabot.
	 * 
	 * @author Stew
	 * @return
	 */
	public String getRandomRivetName( ) {
		return "rivet" + ( WereScrewedGame.random.nextInt( 4 ) + 1 );
	}

	public void setLevelRobotBGTex( Texture tex ) {
		robotTexBG = tex;
	}

	public Texture getLevelRobotBGTex( ) {
		return robotTexBG;
	}

	public void setLevelRobotFGTex( Texture tex ) {
		robotTexFG = tex;
	}

	public Texture getLevelRobotFGTex( ) {
		return robotTexFG;
	}

	public void setLevelRobotOutlineTex( Texture tex ) {
		robotOutlineTex = tex;
	}

	public Texture getLevelRobotOutlineTex( ) {
		return robotOutlineTex;
	}
	
	/** Clears and disposes all assets and the preloading queue. */
	public synchronized void clear () {
		super.clear();
		for(TextureAtlas atlas : atlasMap.values( ))
			atlas.dispose( );
		atlasMap.clear( );
	}
	

	/**
	 * @param r [0-255]
	 * @param g [0-255]
	 * @param b [0-255]
	 */
	public void setTileColor(int r, int g, int b){
		tileColor = new Color(r/255f,g/255f,b/255f,1f);
	}
	
	public Color getTileColor(){
		return tileColor;
	}
}
