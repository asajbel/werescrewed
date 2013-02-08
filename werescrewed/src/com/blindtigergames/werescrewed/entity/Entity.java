package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Anything that can exist. Contains a physics body, and a sprite which may or
 * may not be animated.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Entity {
	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Vector2 offset;
	public Body body;
	protected World world;
	public IMover mover;
	protected boolean solid;
	protected float energy;
	/**
	 * Create entity by definition
	 * 
	 * @param name
	 * @param type
	 * @param world
	 *            in which the entity exists
	 * @param pos
	 *            ition of the entity in the world
	 * @param rot
	 *            ation of the entity
	 * @param scale
	 *            of the entity
	 * @param texture
	 *            (null if defined elsewhere)
	 * @param solid
	 *            boolean determining whether or not the player can stand on it
	 */
	public Entity( String name, EntityDef type, World world, Vector2 pos,
			float rot, Vector2 scale, Texture texture, boolean solid ) {
		this.name = name;
		this.type = type;
		this.world = world;
		this.solid = solid;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.sprite = constructSprite( texture );
		this.body = constructBody( );
		this.energy = 1.0f;
		setPosition( pos );
	}
	
	/*public Entity(String n, EntityDef d, World w, Vector2 pos,
			float rot, Vector2 sca)
	{
		this();
		name = n;
		type = d;
		world = w;
		constructSprite();
		constructBody(pos.x, pos.y, sca.x, sca.y);
	}*/

	/**
	 * Create entity by body. Debug constructor: Should be removed eventually.
	 * 
	 * @param name
	 * @param pos
	 *            ition of the entity in the world
	 * @param texture
	 *            (null if defined elsewhere)
	 * @param body
	 *            defined body of the entity
	 * @param solid
	 *            boolean determining whether or not the player can stand on it
	 */
	public Entity( String name, Vector2 pos, Texture texture, Body body,
			boolean solid ) {
		this.name = name;
		this.solid = solid;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.sprite = constructSprite( texture );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			sprite.setScale( Util.PIXEL_TO_BOX );
		}
		setPosition( pos );
		this.energy = 1.0f;
	}
	
	public void setPosition(float x, float y){
		//x *= Util.PIXEL_TO_BOX;
		//y *= Util.PIXEL_TO_BOX;
		if (body != null){
			body.setTransform(x, y, body.getAngle());
		} else if (sprite != null){
			sprite.setPosition(x, y);
		}
	}
	
	public void setPosition( Vector2 pos ) {
		setPosition(pos.x,pos.y);
	}

	public Vector2 getPosition( ) {
		return body.getPosition( );
	}

	public void move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector );
		setPosition( pos );
	}

	public void draw( SpriteBatch batch ) {
		if ( sprite != null ) {
			Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
			sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
			sprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
			sprite.draw( batch );
		}
	}

	public void update( float deltaTime ) {
		//animation stuff may go here
	}
	
	/**
	 * Update the mover of this entity, if it exists.
	 * Now separated from update() so that it can be called whenever skeleton wants.
	 * @param deltaTime
	 */
	public void updateMover( float deltaTime ){
		if ( body != null && mover != null ) {
			mover.move( deltaTime, body );
		}
	}

	protected String generateName( ) {
		return type.getName( );
	}

	/**
	 * Builds a sprite from a texture. If the texture is null, it attempts to
	 * load one from the XML definitions
	 * 
	 * @param texture from which a sprite can be generated, or null, if loading 
	 * @return the loaded/generated sprite, or null if neither applies
	 */
	protected Sprite constructSprite( Texture texture ) {
		Sprite sprite;
		Vector2 origin;
		boolean loadTex;
		boolean nullTex;

		// Check if the passed texture is null
		nullTex = texture == null;

		// Check if we're loading texture
		loadTex = ( nullTex && type != null && type.texture != null );

		if ( loadTex ) {
			// If we are, load it up
			texture = type.texture;
		} else if ( nullTex ) {
			// If we aren't, but the texture is still null, return null before
			// error occurs at Sprite constructor (can't pass in null)
			return null;
		}

		// Either the passed in or loaded texture defines a new Sprite
		sprite = new Sprite( texture );

		if ( loadTex ) {
			// Definitions for loaded sprites
			origin = new Vector2( type.origin.x, type.origin.y );
			sprite.setScale( type.spriteScale.x, type.spriteScale.y );
		} else {
			// Definitions for non-loaded sprites
			origin = new Vector2( sprite.getWidth( ) / 2,
					sprite.getHeight( ) / 2 );

			// Arbitrary offset :(
			this.offset.set( sprite.getWidth( ) / 2, sprite.getHeight( ) / 2 );
		}
		sprite.setOrigin( origin.x, origin.y );
		return sprite;
	}
	
    public void Move(Vector2 vector)
    {
    	Vector2 pos = body.getPosition().add(vector.mul( Util.PIXEL_TO_BOX ));
    	setPosition(pos);
    }


	/**
	 * Builds the body associated with the entity's type.
	 * 
	 * @return the loaded body, or null, if type is null
	 */
	protected Body constructBody( ) {
		Body body;
		if ( type != null ) {
			body = world.createBody( type.bodyDef );
			body.setUserData( this );
			for ( FixtureDef fix : type.fixtureDefs ) {
				body.createFixture( fix );
			}
		} else {
			return null;
		}
		return body;
	}

	/**
	 * Set the mover of this entity!
	 * 
	 * @param mover
	 */
	public void setMover( IMover mover ) {
		this.mover = mover;
	}

	public boolean isSolid( ) {
		return this.solid;
	}

	public void setSolid( boolean solid ) {
		this.solid = solid;
	}

	public void setEnergy( float energy){
		this.energy = energy;
	}

	public float getEnergy(){ return energy; }
	
	/**
	 * Sets body awake, used in
	 * 
	 * @param solid
	 */
	public void setAwake( ) {
		body.setAwake( true );
	}

	
	/**
	 * Change the sprite to be displayed on the entity
	 * 
	 * @param newSprite
	 * 		The new sprite that will be displayed on top of the entity
	 */
	public void changeSprite(Sprite newSprite){
		this.sprite = newSprite;
	}
	
	
	public void setDensity( float d ) {
		if ( body != null ){
		for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
			body.getFixtureList( ).get( i ).setDensity( d );
		}

	}

	public void setFriction( float f ) {
		if ( body != null ){
		for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
			body.getFixtureList( ).get( i ).setFriction( f );
		}
	}

	public void setRestitution( float r ) {
		if ( body != null ){
		for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
			body.getFixtureList( ).get( i ).setRestitution( r );
		}
	}

	public void setGravScale( float g ) {
		if ( body != null ){
			body.setGravityScale( g );
		}
	}
}