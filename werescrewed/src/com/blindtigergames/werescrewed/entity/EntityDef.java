package com.blindtigergames.werescrewed.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.util.ArrayHash;
import com.blindtigergames.werescrewed.util.BodyEditorLoader;

/**
 * Contains code for loading external files with definitions for Entities
 * 
 * @author Blind Tiger Games
 * 
 */
public class EntityDef {

	// FIELDS

	// Static Fields
	protected static HashMap< String, EntityDef > definitions;

	// Sprite Fields (i.e. everything needed to define just the sprite half)
	protected String texName = "";
	protected Texture texture;
	protected String atlasName;
	protected Array< TextureAtlas > atlases;
	protected String animatorType;
	protected String initialAnim;
	protected Vector2 origin;
	protected Vector2 spriteScale;
	protected Color tint;
	protected String skeleton;
	protected String color;

	// Body Fields (i.e. everything needed to define just the body half)
	protected BodyDef bodyDef;
	protected ArrayList< FixtureDef > fixtureDefs;
	protected float gravityScale;
	protected boolean fixedRotation;

	// Miscellaneous Fields
	protected String name;
	protected EntityCategory category;
	protected ArrayHash< String, String > properties;

	// CONSTANTS
	public static final String tag = "definition";
	// Static initialization
	static {
		definitions = new HashMap< String, EntityDef >( );
	}

	public static void clearDefs(){
		definitions.clear( );
	}
	
	public static void reloadDefs(){
		for (EntityDef def: definitions.values( )){
			def.loadTexture( );
		}
	}
	// METHODS

	/**
	 * Generic constructor: defaults to null texture, (0,0) origin, (1,1) scale,
	 * and neutral tint
	 * 
	 * @param name
	 *            used for loading
	 */
	protected EntityDef( String name ) {
		// Sprite Data
		setTexture( null );
		initialAnim = "";
		origin = new Vector2( 0, 0 );
		spriteScale = new Vector2( 1, 1 );
		tint = new Color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Animation Data
		atlases = new Array< TextureAtlas >( );
		animatorType = "";

		// Body Data
		bodyDef = new BodyDef( );
		bodyDef.type = BodyType.DynamicBody;
		fixtureDefs = new ArrayList< FixtureDef >( );
		gravityScale = 1.0f;
		fixedRotation = false;

		// Misc Data
		setName( name );
		category = null;
		properties = new ArrayHash< String, String >( );
	}

	/**
	 * 
	 * @param name
	 *            used for loading
	 * @param tex
	 *            ture of the sprite
	 * @param initAnim
	 * @param bDef
	 *            body definition to load
	 * @param fixes
	 *            fixtures of the body
	 */
	protected EntityDef( String name, Texture tex, String initAnim,
			BodyDef bDef, ArrayList< FixtureDef > fixes ) {
		this( name );
		// Sprite Data
		setTexture( tex );
		initialAnim = initAnim;

		// Body Data
		bodyDef = bDef;
		if ( fixes != null ) {
			fixtureDefs.addAll( fixes );
		}
	}

	@Override
	public void finalize( ) throws Throwable {
		if ( definitions.containsValue( this ) )
			definitions.remove( this );
		super.finalize( );
	}

	public String getName( ) {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public EntityCategory getCategory( ) {
		return this.category;
	}

	public void setCategory( EntityCategory cat ) {
		this.category = cat;
	}

	public void setCategory( String catName ) {
		EntityCategory cat = EntityCategory.fromString( catName );
		if ( cat != null )
			setCategory( cat );
	}

	public Texture getTexture( ) {
		if ( texture == null && atlases.size > 0 ) {
			texture = atlases.get( 0 ).getTextures( ).iterator( ).next( );
		}
		return texture;
	}

	public void setTexture( Texture texture ) {
		this.texture = texture;
	}

	public void addFixtureDef( FixtureDef def ) {
		fixtureDefs.add( def );
	}

	/**
	 * Loads the body elements for the current Entity
	 * 
	 * @param density
	 * @param friction
	 * @param restitution
	 * @param scale
	 * @param bodyName
	 *            .json file name for the body
	 */
	protected void loadComplexBody( float density, float friction,
			float restitution, float scale, String bodyName ) {
		String filename = WereScrewedGame.dirHandle + "/bodies/" + bodyName
				+ ".json";
		BodyEditorLoader loader = new BodyEditorLoader(
				Gdx.files.internal( filename ) );

		loader.attachFixture( this, bodyName, density, friction, restitution,
				scale );
	}

	/**
	 * Loads the definition from the HashMap, if it exists. If not, load it up.
	 * 
	 * @param id
	 *            for the HashMap
	 * @return the definition of the entity
	 */
	public static EntityDef getDefinition( String id ) {
		if ( definitions.containsKey( id ) ) {
			return definitions.get( id ); // If we already have a definition,
											// use it.
		} else {
			EntityDef out = null;
			out = EntityDef.loadDefinition( id );
			if ( out != null )
				definitions.put( id, out ); // If we get a new definition, store
											// it for later use.
			return out;
		}
	}

	/**
	 * Loads a definition from XML
	 * 
	 * @param id
	 *            file name for the XML file to load
	 * @return The loaded definition
	 */
	protected static EntityDef loadDefinition( String id ) {
		// Gdx.app.log( "EntityDef", "Loading EntityDef: " + id );
		String filename = WereScrewedGame.dirHandle.path( ) + "/entities/" + id
				+ ".xml";
		// Gdx.app.log( "EntityDef", "Filename: " + filename );
		try {
			XmlReader reader = new XmlReader( );
			XmlReader.Element xml = reader
					.parse( Gdx.files.internal( filename ) );
			EntityDef out = new EntityDef( id );

			// Category Data, look for the row called category in xml
			out.setCategory( xml.get( "category", "" ) ); // EntityCategory.tag,
															// "" ) );
			// Sprite Data
			//String atlasName = null;
			out.texName = xml.get( "texture", "" );
			for ( Element atlasElem : xml.getChildrenByName( "atlas" ) ) {
				// Gdx.app.log( "EntityDef",
				// "Getting texture atlas " + atlasElem.getText( ) );
				out.atlases.add( WereScrewedGame.manager.getAtlas( atlasElem
						.getText( ) ) );
			}
			if ( out.atlases.size < 1 ) {
				out.texName = xml.get( "texture", "" );
				out.loadTexture( );
			}
			out.atlasName = xml.get( "atlas", "" );
			out.color = xml.get( "color", "" );
			out.skeleton = xml.get( "gender", "" );
			out.initialAnim = xml.get( "initialAnim" );
			out.animatorType = xml.get( "animator", "" );
			out.origin.x = xml.getFloat( "originX" );
			out.origin.y = xml.getFloat( "originY" );
			out.spriteScale.x = xml.getFloat( "spriteScaleX" );
			out.spriteScale.y = xml.getFloat( "spriteScaleY" );
			out.tint = new Color( xml.getFloat( "tintRed" ),
					xml.getFloat( "tintGreen" ), xml.getFloat( "tintBlue" ),
					xml.getFloat( "tintAlpha" ) );

			// Body Data;
			String bodyName = xml.get( "body" );
			float density = xml.getFloat( "density" );
			float friction = xml.getFloat( "friction" );
			float restitution = xml.getFloat( "restitution" );
			float scale = xml.getFloat( "bodyScale" );
			out.fixedRotation = xml.getBoolean( "fixedRotation", false );

			out.loadComplexBody( density, friction, restitution, scale,
					bodyName );

			// Sound Data
			Array< Element > sounds = xml.getChildrenByName( "sound" );
			if ( sounds.size > 0 ) {
				for ( Element soundElem : sounds ) {
					if ( soundElem.getText( ).length( ) > 0 ) {
						out.properties.put( "sound", soundElem.getText( ) );
					}
				}
			}
			return out;
		} catch ( IOException e ) {
			// Gdx.app.log( "Error", "Loading entity definition " + id + " ", e
			// );
		}

		return null;
	}

	protected static FixtureDef makeFixtureDef( float density, float friction,
			float restitution, Shape shape ) {
		FixtureDef out = new FixtureDef( );
		out.density = density;
		out.friction = friction;
		out.restitution = restitution;
		out.shape = shape;
		return out;
	}

	public boolean isAnimatorType( String anim ) {
		if ( animatorType.equals( anim ) ) {
			return true;
		}
		return false;
	}

	public String getAtlasName( ) {
		return atlasName;
	}

	public String getSkeleton( ) {
		return skeleton;
	}

	public String getInitialAnimation( ) {
		return initialAnim;
	}

	public Vector2 getScale( ) {
		return spriteScale;
	}

	public ArrayHash< String, String > getProperties( ) {
		return properties;
	}

	public void setScale( float x ) {
		spriteScale.x = x;
	}

	public void setScaleY( float y ) {
		spriteScale.y = y;
	}

	public void setScale( float x, float y ) {
		spriteScale.x = x;
		spriteScale.y = y;
	}
	
	public void loadTexture(){
		if (!texName.equals( "" )){
			texture = WereScrewedGame.manager.get( WereScrewedGame.dirHandle.path( ) + "/" + texName,
					Texture.class ) ;
		}
	}
}
