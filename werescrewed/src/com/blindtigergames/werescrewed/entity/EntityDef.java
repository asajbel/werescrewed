package com.blindtigergames.werescrewed.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.XmlReader;
import com.blindtigergames.werescrewed.screens.GameScreen;

public class EntityDef {
<<<<<<< HEAD

	// FIELDS
=======
	
	//Methods
		protected EntityDef(String n){
			//Sprite Data
			texture = null;
			initialAnim = "";
			origin = new Vector2(0,0);
			spriteScale = new Vector2(1,1);
			tint = new Color(1.0f,1.0f,1.0f,1.0f);
			//Body Data
			bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			fixtureDefs = new ArrayList<FixtureDef>();
			gravityScale = 1.0f;
			fixedRotation = false;
			
			//Misc Data
			name = n;
		}
	
		protected EntityDef(String n, Texture t, String iA, BodyDef bDef, ArrayList<FixtureDef> fixes){
			this(n);
			//Sprite Data
			texture = t;
			initialAnim = iA;
			
			//Body Data
			bodyDef = bDef;
			if (fixes != null){
				fixtureDefs.addAll( fixes );
			}
		}
		
		@Override
		public void finalize() throws Throwable{
			if (definitions.containsValue(this))
				definitions.remove(this);
			super.finalize();
		}

		protected void loadComplexBody(float density, float friction, float restitution, float scale, String bodyName ){
			String filename = "data/bodies/" + bodyName + ".json";		
			BodyEditorLoader loader = new BodyEditorLoader( Gdx.files.internal(filename));
						
			loader.attachFixture( this, bodyName, density, friction, restitution, scale );
		}
				
	//Sprite Fields (i.e. everything needed to define just the sprite half)
		protected Texture texture;
		protected String initialAnim;
		protected Vector2 origin;
		protected Vector2 spriteScale;
		protected Color tint;
	
	//Body Fields (i.e. everything needed to define just the body half)
		protected BodyDef bodyDef;
		protected ArrayList<FixtureDef> fixtureDefs;
		protected float gravityScale;
		protected boolean fixedRotation;
	
	//Miscellaneous Fields
		protected String name;
>>>>>>> 4d5d609dc881326a78f286c2b51767eb32fe623b
	
	// Sprite Fields (i.e. everything needed to define just the sprite half)
	protected Texture texture;
	protected String initialAnim;
	protected Vector2 origin;
	protected Vector2 spriteScale;
	protected Color tint;

	// Body Fields (i.e. everything needed to define just the body half)
	protected BodyDef bodyDef;
	protected ArrayList< FixtureDef > fixtureDefs;
	protected float gravityScale;
	protected boolean fixedRotation;
	protected float defaultDensity;
	protected float defaultFriction;
	protected float defaultRestitution;

	// Miscellaneous Fields
	protected String name;

	// Static Fields
	protected static HashMap< String, EntityDef > definitions;
	static {
		definitions = new HashMap< String, EntityDef >( );
	}
	
	// METHODS
	
	@SuppressWarnings( "unchecked" )
	protected EntityDef( String n, Texture t, String iA, BodyDef bDef,
			ArrayList< FixtureDef > fixes ) {
		// Sprite Data
		texture = t;
		initialAnim = iA;
		origin = new Vector2( 0, 0 );
		spriteScale = new Vector2( 1, 1 );
		tint = new Color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Body Data
		bodyDef = bDef;
		if ( fixes == null ) {
			fixtureDefs = new ArrayList< FixtureDef >( );
		} else {
			fixtureDefs = ( ArrayList< FixtureDef > ) fixes.clone( );
		}
		gravityScale = 1.0f;
		fixedRotation = false;

		// Misc Data
		name = n;
	}

	@Override
	public void finalize( ) throws Throwable {
		if ( definitions.containsValue( this ) )
			definitions.remove( this );
		super.finalize( );
	}

	protected void loadComplexBody( float density, float friction,
			float restitution, int scale, String bodyName ) {
		String filename = "data/bodies/" + bodyName + ".json";
		BodyEditorLoader loader = new BodyEditorLoader(
				Gdx.files.internal( filename ) );

		loader.attachFixture( this, bodyName, density, friction, restitution,
				scale );
	}

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
				playerfeetShape.setRadius( 10f * GameScreen.PIXEL_TO_BOX );
				FixtureDef playerFixtureDef = makeFixtureDef( 9.9f, 0.05f,
						0.0f, playerfeetShape );
				fixes.add( playerFixtureDef );

				out = new EntityDef( "player", new Texture(
						Gdx.files.internal( "data/player_r_m.png" ) ), "",
						playerBodyDef, fixes );
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
	 * Loads an entity definition from XML.
	 */
<<<<<<< HEAD
	protected static EntityDef loadDefinition( String id ) {	
		// TODO Fill with XML loading code
=======
	protected static EntityDef loadDefinition(String id) {
		String filename = "data/entities/" + id + ".xml";
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element xml = reader.parse( Gdx.files.internal( filename ) );
			EntityDef out = new EntityDef(id);
			
			//Sprite Data
			String texName = xml.get( "texture" );
			out.texture = new Texture ( Gdx.files.internal( texName ));
			out.initialAnim = xml.get("initialAnim");
			out.origin.x = xml.getFloat( "originX" ); out.origin.y = xml.getFloat( "originY" );
			out.spriteScale.x = xml.getFloat( "spriteScaleX" ); out.spriteScale.y = xml.getFloat( "spriteScaleY" );
			out.tint = new Color(xml.getFloat( "tintRed" ),xml.getFloat( "tintGreen" ), xml.getFloat( "tintBlue" ), xml.getFloat( "tintAlpha" ));
			
			//Body Data;
			String bodyName = xml.get( "body" );
			float density = xml.getFloat( "density" );
			float friction = xml.getFloat( "friction" );
			float restitution = xml.getFloat( "restitution" );
			float scale = xml.getFloat("bodyScale");
			
			out.loadComplexBody( density, friction, restitution, scale, bodyName );
			
			return out;
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			Gdx.app.log( "Error", "Loading entity definition " + id + " ", e );
		}
		
>>>>>>> 4d5d609dc881326a78f286c2b51767eb32fe623b
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
