package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.screens.GameScreen;

//an Entity is anything that can exist, it has a position and a texture
public class Entity {
	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Vector2 offset;
	public Body body;
	protected World world;
	public IMover mover;
	private boolean solid;
	protected Anchor anchor;

	public Entity( String name, EntityDef type, World world, Vector2 pos,
			float rot, Vector2 scale, Texture texture, boolean solid ) {
		this.name = name;
		this.type = type;
		this.world = world;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.solid = solid;
		constructSprite( texture );
		constructBody( pos );
	}

	public Entity( String name, Vector2 pos, Texture texture, Body body,
			boolean solid ) {
		this.offset = new Vector2( 0.0f, 0.0f );
		this.solid = solid;
		this.name = name;
		if ( texture != null )
			constructSprite( texture );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			sprite.setScale( GameScreen.PIXEL_TO_BOX );
		}
		setPosition( pos );
	}

	public void setPosition( float x, float y ) {
		if ( body != null ) {
			body.setTransform( x, y, body.getAngle( ) );
		} else if ( sprite != null ) {
			sprite.setPosition( x, y );
		}
	}

	public void setPosition( Vector2 pos ) {
		setPosition( pos.x, pos.y );
	}

	public Vector2 getPosition( ) {
		return body.getPosition( );
	}

	public void Move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector );
		setPosition( pos );
	}

	public void draw( SpriteBatch batch ) {
		if ( sprite != null ) {
			Vector2 bodyPos = body.getPosition( ).mul( GameScreen.BOX_TO_PIXEL );
			sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
			sprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
			sprite.draw( batch );
		}
	}

	public void update( float deltaTime ) {
		if ( body != null && mover != null ) {
			mover.move( deltaTime, body );
		}
	}

	protected String generateName( ) {
		return type.name;
	}

	/**
	 * Builds a sprite from a texture. If the texture is null, it attempts to
	 * load one from the XML definitions
	 */
	protected void constructSprite( Texture texture ) {
		// I have plans to make this a return value
		// Sprite sprite;
		Vector2 origin;
		boolean loadTex;

		loadTex = ( texture == null && type != null && type.texture != null );

		if ( loadTex ) {
			texture = type.texture;
		}
		this.sprite = new Sprite( texture );
		if ( loadTex ) {
			origin = new Vector2( type.origin.x, type.origin.y );
			this.sprite.setScale( type.spriteScale.x, type.spriteScale.y );
		} else {
			origin = new Vector2( this.sprite.getWidth( ) / 2,
					this.sprite.getHeight( ) / 2 );
			this.offset.set( this.sprite.getWidth( ) / 2,
					this.sprite.getHeight( ) / 2 );
		}
		this.sprite.setOrigin( origin.x, origin.y );
	}

	protected void constructBody( Vector2 pos ) {
		if ( type != null ) {
			body = world.createBody( type.bodyDef );
			body.setUserData( this );
			for ( FixtureDef fix : type.fixtureDefs ) {
				body.createFixture( fix );
			}
			setPosition( pos.x, pos.y );
		}
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
	 * Sets body awake, used in
	 * 
	 * @param solid
	 */
	public void setAwake( ) {
		body.setAwake( true );
	}

}