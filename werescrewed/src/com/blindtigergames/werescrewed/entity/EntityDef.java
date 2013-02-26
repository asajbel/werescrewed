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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.util.BodyEditorLoader;
import com.blindtigergames.werescrewed.util.Util;

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
	protected Texture texture;
	protected String tileSetName;
	protected String initialAnim;
	protected Vector2 origin;
	protected Vector2 spriteScale;
	protected Color tint;

	// Body Fields (i.e. everything needed to define just the body half)
	protected BodyDef bodyDef;
	protected ArrayList< FixtureDef > fixtureDefs;
	protected float gravityScale;
	protected boolean fixedRotation;

	// Miscellaneous Fields
	protected String name;
	private String category;

	// CONSTANTS

	// Static constants
	public static final String NO_CATEGORY = "Entity";

	// Static initialization
	static {
		definitions = new HashMap< String, EntityDef >( );
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
		tileSetName = null;
		initialAnim = "";
		origin = new Vector2( 0, 0 );
		spriteScale = new Vector2( 1, 1 );
		tint = new Color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Body Data
		bodyDef = new BodyDef( );
		bodyDef.type = BodyType.DynamicBody;
		fixtureDefs = new ArrayList< FixtureDef >( );
		gravityScale = 1.0f;
		fixedRotation = false;

		// Misc Data
		setName( name );
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

	public String getCategory( ) {
		return category;
	}

	public void setCategory( String category ) {
		this.category = category;
	}

	public Texture getTexture( ) {
		if ( texture == null ){
			return WereScrewedGame.manager.getTextureAtlas( tileSetName ).getTextures( ).iterator( ).next( );
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
		String filename = WereScrewedGame.dirHandle + "/bodies/" + bodyName + ".json";
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
			// Since the XML loader isn't done yet, create default entity
			// definitions here, and put them in the hashmap.

			if ( id.equals( "player" ) ) { // Player
				BodyDef playerBodyDef = new BodyDef( );
				playerBodyDef.type = BodyType.DynamicBody;
				playerBodyDef.fixedRotation = true;
				ArrayList< FixtureDef > fixes = new ArrayList< FixtureDef >( );

				CircleShape playerfeetShape = new CircleShape( );
				playerfeetShape.setRadius( 10f * Util.PIXEL_TO_BOX );
				FixtureDef playerFixtureDef = makeFixtureDef( 9.9f, 0.0f, 0.0f,
						playerfeetShape );
				fixes.add( playerFixtureDef );

				out = new EntityDef( "player",
						WereScrewedGame.manager.get(WereScrewedGame.dirHandle.path( )  + "/common/"
						+ "player_r_m.png", Texture.class), "", playerBodyDef, fixes );
			} else if ( id.equals( "bottle" ) ) { // Bottle
				BodyDef bottleBodyDef = new BodyDef( );
				bottleBodyDef.type = BodyType.DynamicBody;
				out = new EntityDef( "bottle", null, "", bottleBodyDef, null );
				out.loadComplexBody( 1.0f, 0.5f, 0.0f, 1, "bottle" );
			} else {
				out = EntityDef.loadDefinition( id ); // Otherwise, try loading
														// from XML.
			}
			if ( out != null )
				definitions.put( id, out ); // If we get a new definition, store
											// it for later use.
			return out;
		}
	}

	/**
	 * Loads a definition from XML
	 * 
	 * @param id file name for the XML file to load
	 * @return The loaded definition
	 */
	protected static EntityDef loadDefinition( String id ) {
		Gdx.app.log( "EntityDef", "Loading EntityDef: "+id);
		String filename = WereScrewedGame.dirHandle.path( )  + "/entities/" + id + ".xml";		
		Gdx.app.log( "EntityDef", "Filename: "+filename);
		try {
			XmlReader reader = new XmlReader( );
			XmlReader.Element xml = reader
					.parse( Gdx.files.internal( filename ) );
			EntityDef out = new EntityDef( id );

			// Category Data
			out.setCategory( xml.get( "category", NO_CATEGORY ) );
			// Sprite Data
			String texName = null;
			String tileSetName = null;
			try{
				texName = xml.get( "texture" );
			}catch(GdxRuntimeException e){
				tileSetName = xml.get( "tileset" );
			}
			if (texName != null ){
				out.setTexture(WereScrewedGame.manager.get(
					WereScrewedGame.dirHandle.path( )  + "/" + texName, Texture.class));
			}
			out.tileSetName = tileSetName;
			out.initialAnim = xml.get( "initialAnim" );
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

			return out;
		} catch ( IOException e ) {
			Gdx.app.log( "Error", "Loading entity definition " + id + " ", e );
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

}
