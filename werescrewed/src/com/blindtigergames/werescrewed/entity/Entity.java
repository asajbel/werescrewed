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

/**
 * Anything that exists in the game world.
 * 
 * @author Blind Tiger Games
 * 
 */
public class Entity {
	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Body body;
	protected World world;
	public IMover mover;
<<<<<<< HEAD
	private boolean solid;

	public Entity( String name, EntityDef type, World world, Vector2 pos,
			float rot, Vector2 scale, Texture texture, boolean solid ) {
		this.name = name;
		this.type = type;
		this.world = world;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.solid = solid;
		if ( texture != null )
			constructSprite( texture );
		else
			constructSprite( );
		constructBody( pos.x, pos.y, scale.x, scale.y );
	}

	public Entity( String n, Vector2 pos, Texture tex, Body bod, boolean solid ) {
		this.offset = new Vector2( 0.0f, 0.0f );
		this.solid = solid;
		name = n;
		if ( tex != null )
			constructSprite( tex );
		body = bod;
		if ( bod != null ) {
			world = bod.getWorld( );
			sprite.setScale( GameScreen.PIXEL_TO_BOX );
		}
=======
	protected boolean solid;

	/**
	 * Generic constructor. Should not be used without subsequent modification
	 * (or probably at all).
	 */
	public Entity( ) {
		this( "I AM ERROR", null, null, false, new Vector2( 0.0f, 0.0f ), 0.0f,
				new Vector2( 1.0f, 1.0f ), null );
	}

	/**
	 * Constructor with all available variables.
	 * 
	 * @param name
	 *            of Entity for loading purposes
	 * @param def
	 *            inition of the Entity as an EntityDef
	 * @param world
	 *            in which the Entity exists
	 * @param solid
	 *            a boolean that represents whether or not a player can jump off
	 *            of the Entity
	 * @param pos
	 *            ition of the Entity in the world
	 * @param rot
	 *            ation of the Entity initially
	 * @param scale
	 *            of the Entity sprite
	 * @param tex
	 *            ture of the Entity (null if undefined)
	 */
	public Entity( String name, EntityDef def, World world, boolean solid,
			Vector2 pos, float rot, Vector2 scale, Texture tex ) {
		this.name = name;
		this.type = def;
		this.world = world;
		this.solid = solid;
		this.sprite = constructSprite( tex, scale );
		this.body = constructBody( );
		setPosition( pos );
	}

	public void setPosition( Vector2 pos ) {
		if ( body != null ) {
			this.body.setTransform( pos.x, pos.y, body.getAngle( ) );
		} else if ( sprite != null ) {
			sprite.setPosition( pos.x, pos.y );
		}
	}

	public Vector2 getPosition( ) {
		return body.getPosition( );
	}

	public void Move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector );
>>>>>>> 7ea9392a83464bbd1e547df5b3ec94a7a78e51f9
		setPosition( pos );
	}

	public void draw( SpriteBatch batch ) {
		if ( sprite != null ) {
			sprite.draw( batch );
		}
	}

<<<<<<< HEAD
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
=======
	public void update( float deltaTime ) {
		if ( body != null && sprite != null ) {
			Vector2 bodyPos = body.getPosition( ).mul( GameScreen.BOX_TO_PIXEL );
			sprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
			sprite.setPosition( bodyPos.x - sprite.getWidth( ) / 2, bodyPos.y
					- sprite.getHeight( ) / 2 );
>>>>>>> 7ea9392a83464bbd1e547df5b3ec94a7a78e51f9

			if ( mover != null )
				mover.move( deltaTime, body );
		}
	}

	protected String generateName( ) {
		return type.name;
	}

<<<<<<< HEAD
	protected void constructSprite( ) {
		if ( type != null && type.texture != null ) {
			sprite = new Sprite( type.texture );
			sprite.setOrigin( type.origin.x, type.origin.y );
			sprite.setScale( type.spriteScale.x, type.spriteScale.y );
=======
	protected Sprite constructSprite( Texture tex, Vector2 scale ) {
		Sprite sprite;
		if ( tex == null ) {
			if ( type != null && type.texture != null ) {
				sprite = new Sprite( type.texture );
				sprite.setScale( type.spriteScale.x, type.spriteScale.y );
				sprite.setOrigin( sprite.getWidth( ) / 2, sprite.getHeight( ) / 2 );
			} else {
				return null;
			}
		} else {
			sprite = new Sprite( tex );
			sprite.setScale( scale.x, scale.y );
			sprite.setOrigin( sprite.getWidth( ) / 2, sprite.getHeight( ) / 2 );
>>>>>>> 7ea9392a83464bbd1e547df5b3ec94a7a78e51f9
		}
		return sprite;
	}

<<<<<<< HEAD
	protected void constructSprite( Texture tex ) {
		sprite = new Sprite( tex );
		sprite.setOrigin( sprite.getWidth( ) / 2, sprite.getHeight( ) / 2 );
		offset.set( sprite.getWidth( ) / 2, sprite.getHeight( ) / 2 );
	}

	protected void constructBody( float x, float y, float width, float height ) {
=======
	protected Body constructBody( ) {
		Body body = null;
>>>>>>> 7ea9392a83464bbd1e547df5b3ec94a7a78e51f9
		if ( type != null ) {
			body = world.createBody( type.bodyDef );
			body.setUserData( this );
			for ( FixtureDef fix : type.fixtureDefs ) {
				body.createFixture( fix );
			}
		}
		return body;

	}

	/**
	 * Set the mover of this entity!
	 * 
<<<<<<< HEAD
	 * @param _mover
=======
	 * @param mover
	 */
	public void setMover( IMover mover ) {
		this.mover = mover;
	}

	/**
	 * 
	 * @return whether or not the player can jump off of the Entity
	 */
	public boolean isSolid( ) {
		return solid;
	}

	/**
	 * Sets whether or not the player can jump off of the Entity
	 * 
	 * @param solid
	 */
	public void setSolid( boolean solid ) {
		this.solid = solid;
	}
	/**
	 * Sets body awake, used in
	 * 
	 * @param solid
>>>>>>> 7ea9392a83464bbd1e547df5b3ec94a7a78e51f9
	 */
	public void setAwake(){
		body.setAwake( true );
	}

	public boolean isSolid( ) {
		return this.solid;
	}

	public void setSolid( boolean solid ) {
		this.solid = solid;
	}
}