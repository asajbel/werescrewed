package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Anything that can exist. Contains a physics body, and a sprite which may or
 * may not be animated.
 * 
 * @author Kevin
 * 
 */
public class Entity {
	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Vector2 offset;
	public Vector2 bodyOffset;
	public Body body;
	protected World world;
	public IMover mover;
	protected boolean solid;
	protected Anchor anchor;
	protected float energy;
	protected boolean active;
	protected boolean visible;
	protected boolean maintained;

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
	public Entity( String name, EntityDef type, World world, 
			Vector2 pos, float rot, Vector2 scale, Texture texture, boolean solid, 
            float anchRadius) {
		this.construct( name, pos, solid );
		this.type = type;
		this.world = world;
		this.sprite = constructSprite( texture );
		this.body = constructBodyByType( );
		setPixelPosition( pos );
		if ( anchRadius >= 0 ) {
			Vector2 centPos = new Vector2( body.getWorldCenter( ).x
					* Util.BOX_TO_PIXEL, body.getWorldCenter( ).y
					* Util.BOX_TO_PIXEL );
			this.anchor = new Anchor( centPos, world, anchRadius );
			AnchorList.getInstance( ).addAnchor( anchor );
		}
	}
	
	/**
	 * Create entity by body. Debug constructor: Should be removed eventually.
	 * 
	 * @param name
	 * @param positionPixels
	 *            ition of the entity in the world in PIXELS
	 * @param texture
	 *            (null if defined elsewhere)
	 * @param body
	 *            defined body of the entity
	 * @param solid
	 *            boolean determining whether or not the player can stand on it
	 */
	public Entity( String name, Vector2 positionPixels, Texture texture, Body body,
			boolean solid ) {
		this.construct( name, positionPixels, solid );
		this.sprite = constructSprite( texture );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			sprite.setScale( Util.PIXEL_TO_BOX );
		}
		this.setPixelPosition( positionPixels );
	}

	/**
	 * Common sub-constructor that applies to all Entity() constructors.
	 */
	protected void construct(String name, Vector2 pos, boolean solid){
		this.name = name;
		this.solid = solid;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.bodyOffset = new Vector2( 0.0f, 0.0f );
		this.energy = 1.0f;
		this.maintained = true;
		this.visible = true;
		this.active = true;
	}

	/**
	 * Set position of the body in meters.
	 * @param xMeters
	 * @param yMeters
	 */
	public void setPosition(float x, float y){
		x -= bodyOffset.x;
		y -= bodyOffset.y;
		if (body != null){
			body.setTransform(x, y, body.getAngle());
		} else if (sprite != null){
			sprite.setPosition(x, y);
		}
	}

	public void setPixelPosition(float x, float y){
		setPosition(x * Util.PIXEL_TO_BOX, y * Util.PIXEL_TO_BOX);	
	}

	public void setPixelPosition(Vector2 pixels){
		setPixelPosition(pixels.x, pixels.y);	
	}
	
	/**
	 * Set position by  meters!!
	 * @param positionMeters
	 */
	public void setPosition( Vector2 positionMeters ) {
		setPosition(positionMeters.x,positionMeters.y);
	}

	/**
	 * returns body position in meters.
	 * @return Vector2 in meters of bodie's world origin
	 */
	public Vector2 getPosition( ) {
		return body.getPosition( ).add( bodyOffset );
	}
	
	/**
	 * Use this position when setting relative position of platforms
	 * for paths targets. ie you set a platform at (x,y) in meters,
	 * but the path takes in pixels, so do something like platform.
	 * getPositionPixel().add(0,600)
	 * @return world position of origin in PIXELS
	 */
	public Vector2 getPositionPixel(){
		return body.getPosition( ).cpy().mul( Util.BOX_TO_PIXEL );
	}

	public void move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector );
		setPosition( pos );
	}

	public void draw( SpriteBatch batch ) {
		if ( sprite != null && visible ) {
			sprite.draw( batch );
		}
		drawOrigin(batch);
	}

	public void drawOrigin(SpriteBatch batch){
		float axisSize = 128.0f;
		ShapeRenderer shapes = new ShapeRenderer();
		shapes.setProjectionMatrix( batch.getProjectionMatrix( ) );
		Vector2 pos = getPosition().mul( Util.BOX_TO_PIXEL );
		shapes.begin( ShapeType.Line );
		shapes.setColor( 1.0f, 0.0f, 0.0f, 1.0f );
		shapes.line(pos.x, pos.y, pos.x+axisSize, pos.y); //Red:  X-axis
		shapes.setColor( 0.0f, 0.0f, 1.0f, 1.0f );
		shapes.line(pos.x, pos.y, pos.x, pos.y+axisSize); //Blue: Y-axis
		if (sprite != null){
			shapes.setColor( 0.0f, 1.0f, 0.0f, 1.0f );
			shapes.line(pos.x, pos.y, pos.x + sprite.getOriginX( ), pos.y + sprite.getOriginY( )); //Green: Sprite Origin
		}
		shapes.end();
	}
	
	public void update( float deltaTime ) {
		//animation stuff may go here
		Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
		sprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
	}
	
	/**
	 * Update the mover of this entity, if it exists.
	 * Now separated from update() so that it can be called whenever skeleton wants.
	 * @param deltaTime
	 */
	public void updateMover( float deltaTime ) {
		if ( active ) {
			if ( body != null ) {
				if ( mover != null ) {
					mover.move( deltaTime, body );
				}
				if ( anchor != null ) {
					updateAnchor( );
				}
			}
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
	
	public void Move( Vector2 vector ) 
	{
		Vector2 pos = body.getPosition( ).add( vector.mul( Util.PIXEL_TO_BOX ) );
		setPosition( pos );
	}


	/**
	 * Builds the body associated with the entity's type.
	 * 
	 * @return the loaded body, or null, if type is null
	 */
	protected Body constructBodyByType( ) {
		Body newBody;
		if ( type != null ) {
			newBody = world.createBody( type.bodyDef );
			newBody.setUserData( this );
			for ( FixtureDef fix : type.fixtureDefs ) {
				newBody.createFixture( fix );
			}
		} else {
			return null;
		}
		return newBody;
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

	/**
	 * Sets the energy of the current body.
	 * Energy is a new property for Entities that is meant
	 * to scale impulses. It currently does nothing, but it's here
	 * if someone wants to use it.
	 * 
	 * @param energy
	 */
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
	 * Determines whether an entity should be deleted
	 * on next update or not
	 * 
	 * @param m
	 *            - boolean
	 */
	public void setMaintained( boolean m ) {
		maintained = m;
	}

	public boolean isMaintained( ) {
		return maintained;
	}

	/**
	 * Determines whether an entity should be drawn or not.
	 * 
	 * @param v
	 *            - boolean
	 */
	public void setVisible( boolean v ) {
		visible = v;
	}

	public boolean isVisible( ) {
		return visible;
	}

	/**
	 * Determines whether an entity should be updated or not.
	 * 
	 * @param a
	 *            - boolean
	 */
	public void setActive( boolean a ) {
		active = a;
	}
	public boolean isActive(){
		return active;
	}

	/**
	 * Change the sprite to be displayed on the entity
	 * 
	 * @param newSprite
	 *            The new sprite that will be displayed on top of the entity
	 */
	public void changeSprite( Sprite newSprite ) {
		this.sprite = newSprite;
	}

	/**
	 * updates the player's anchor
	 * 
	 * @author Edward Ramirez
	 */
	private void updateAnchor( ) {
		anchor.setPositionBox( body.getWorldCenter( ) );
	}

	/**
	 * set the bodies category collision bits
	 * 
	 * @param
	 */
	public void setCategoryMask( short category, short mask ) {
		if ( body != null ) {
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setSensor( false );
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = category;
				// player now collides with everything
				filter.maskBits = mask;
				f.setFilterData( filter );
			}
		}
	}

	/**
	 * This is a quick-n-dirty fix for complex body collisions. Hopefully we'll
	 * get to a point where we don't need it. There's probably some overlap
	 * between mine and Dennis' functions, I'll try to sort it out on next
	 * update.
	 */
	public void quickfixCollisions( ) {
		Filter filter;
		for ( Fixture f : body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			// move player to another category so other objects stop
			// colliding
			filter.categoryBits = Util.DYNAMIC_OBJECTS;
			// player still collides with sensor of screw
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}

	}

	public void setDensity( float d ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setDensity( d );
		}

	}

	public void setFriction( float f ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setFriction( f );
		}
	}

	public void setRestitution( float r ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setRestitution( r );
		}
	}

	public void setGravScale( float g ) {
		if ( body != null ) {
			body.setGravityScale( g );
		}
	}

	public String toString( ) {
		return "Entity[" + name + "] pos:" + body.getPosition( )
				+ ", body.active:" + body.isActive( ) + ", body.awake:"
				+ body.isAwake( );
	}
	
}