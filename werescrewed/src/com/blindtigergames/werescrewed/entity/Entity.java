package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
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
			sprite.draw( batch );
		}
	}

	public void update( float deltaTime ) {
		if ( body != null && sprite != null ) {
			Vector2 bodyPos = body.getPosition( ).mul( GameScreen.BOX_TO_PIXEL );
			sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
			// sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
			sprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );

			if ( mover != null )
				mover.move( deltaTime, body );
		}
	}

	protected String generateName( ) {
		return type.name;
	}

	protected void constructSprite( ) {
		if ( type != null && type.texture != null ) {
			sprite = new Sprite( type.texture );
			sprite.setOrigin( type.origin.x, type.origin.y );
			sprite.setScale( type.spriteScale.x, type.spriteScale.y );
		}
	}

	protected void constructSprite( Texture texture ) {
		Sprite sprite;
		if ( texture == null ) {
			if ( type != null && type.texture != null ) {
				this.sprite = new Sprite( type.texture );
				this.sprite.setOrigin( type.origin.x, type.origin.y );
				this.sprite.setScale( type.spriteScale.x, type.spriteScale.y );
			}
		} else {
			this.sprite = new Sprite( texture );
			this.sprite.setOrigin( this.sprite.getWidth( ) / 2,
					this.sprite.getHeight( ) / 2 );
			this.offset.set( this.sprite.getWidth( ) / 2,
					this.sprite.getHeight( ) / 2 );
		}
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
	 * @param _mover
	 */
	public void setMover( IMover _mover ) {
		this.mover = _mover;
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