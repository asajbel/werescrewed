package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.animator.IAnimator;
import com.blindtigergames.werescrewed.entity.animator.PlayerAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.level.GleedLoadable;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Anything that can exist. Contains a physics body, and a sprite which may or
 * may not be animated.
 * 
 * @author Kevin / Ranveer
 * 
 */
public class Entity implements GleedLoadable {
	private static final int INITAL_CAPACITY = 3;

	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Vector2 offset;
	public Vector2 bodyOffset;
	public Body body;
	protected World world;
	protected boolean solid;
	protected Anchor anchor;
	protected float energy;
	protected boolean active;
	protected boolean crushing;
	protected boolean visible;
	protected boolean maintained;
	protected boolean removeNextStep = false;
	public EntityType entityType;
	private ArrayList< IMover > moverArray;
	protected ArrayList< Sprite > decals;
	protected ArrayList< Vector2 > decalOffsets;
	protected ArrayList< Float > decalAngles;
	private RobotState currentRobotState;
	private EnumMap< RobotState, Integer > robotStateMap;

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
			Vector2 positionPixels, float rot, Vector2 scale, Texture texture,
			boolean solid ) {
		this.construct( name, solid );
		this.type = type;
		this.world = world;
		if (type.atlases.size > 0 ){
			this.sprite = constructSprite( type.atlases.get( 0 ) );
		} else {
			this.sprite = constructSprite( texture );
		}
		this.body = constructBodyByType( );
		setPixelPosition( positionPixels );
	}

	/**
	 * Same constructor as above but without scale or rotation
	 * 
	 * @param name
	 * @param type
	 * @param world
	 * @param positionPixels
	 * @param texture
	 */
	public Entity( String name, EntityDef type, World world, Vector2 positionPixels, Texture texture) {
		this.construct( name, solid );
		this.type = type;
		this.world = world;
		if (type.atlases.size > 0 ){
			this.sprite = constructSprite( type.atlases.get( 0 ) );
		} else {
			this.sprite = constructSprite( texture );
		}
		this.body = constructBodyByType( );
		this.decals = new ArrayList< Sprite >( );
		this.decalOffsets = new ArrayList< Vector2 >( );
		setPixelPosition( positionPixels );
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
	public Entity( String name, Vector2 positionPixels, Texture texture,
			Body body, boolean solid ) {
		this.construct( name, solid );
		this.sprite = constructSprite( texture );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			// sprite.setScale( Util.PIXEL_TO_BOX );
		}
		this.setPixelPosition( positionPixels );
	}

	/**
	 * Construct an entity that uses a PolySprite
	 * 
	 * @param name
	 *            , oh you know.
	 * @param positionPixels
	 *            POSITION IN PIXELS
	 * @param texture
	 *            texture to fill the polysprite with
	 * @param verts
	 *            an Array<Vector2> of vertex points of the poly. Must be
	 *            concave or it will look weird.
	 * @param body
	 *            - same old
	 * @param solid
	 *            same as it always was.
	 * @author stew
	 */
	public Entity( String name, Vector2 positionPixels, Texture texture,
			Array< Vector2 > verts, Body body, boolean solid ) {
		this.construct( name, solid );
		this.sprite = new PolySprite( texture, verts );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
		}
		this.setPixelPosition( positionPixels );
	}

	/**
	 * Common sub-constructor that applies to all Entity() constructors.
	 */
	protected void construct( String name, boolean solid ) {
		this.name = name;
		this.solid = solid;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.bodyOffset = new Vector2( 0.0f, 0.0f );
		this.energy = 1.0f;
		this.maintained = true;
		this.visible = true;
		this.active = true;
		this.decals = new ArrayList< Sprite >( );
		this.decalOffsets = new ArrayList< Vector2 >( );
		this.decalAngles = new ArrayList< Float >( );
		setUpRobotState( );
	}

	/**
	 * Set position of the body in meters.
	 * 
	 * @param xMeters
	 * @param yMeters
	 */
	public void setPosition( float xMeters, float yMeters ) {
		xMeters -= bodyOffset.x;
		yMeters -= bodyOffset.y;
		if ( body != null ) {
			body.setTransform( xMeters, yMeters, body.getAngle( ) );
		} else if ( sprite != null ) {
			sprite.setPosition( xMeters * Util.BOX_TO_PIXEL, yMeters
					* Util.BOX_TO_PIXEL );
		}
	}

	public void setPixelPosition( float x, float y ) {
		setPosition( x * Util.PIXEL_TO_BOX, y * Util.PIXEL_TO_BOX );
	}

	public void setPixelPosition( Vector2 pixels ) {
		if ( pixels != null )
			setPixelPosition( pixels.x, pixels.y );
	}

	/**
	 * Set position by meters!!
	 * 
	 * @param positionMeters
	 */
	public void setPosition( Vector2 positionMeters ) {
		setPosition( positionMeters.x, positionMeters.y );
	}

	/**
	 * returns body position in meters.
	 * 
	 * @return Vector2 in meters of bodie's world origin
	 */
	public Vector2 getPosition( ) {
		return body.getPosition( ).add( bodyOffset );
	}

	/**
	 * Use this position when setting relative position of platforms for paths
	 * targets. ie you set a platform at (x,y) in meters, but the path takes in
	 * pixels, so do something like platform. getPositionPixel().add(0,600)
	 * 
	 * @return world position of origin in PIXELS
	 */
	public Vector2 getPositionPixel( ) {
		return body.getPosition( ).cpy( ).mul( Util.BOX_TO_PIXEL );
	}

	public void move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector );
		setPosition( pos );
	}

	public void draw( SpriteBatch batch, float deltaTime ) {
		if ( sprite != null && visible && !removeNextStep ) {
			sprite.draw( batch );
		}
		// drawOrigin(batch);
		drawDecals(batch);
	}

	public void drawOrigin( SpriteBatch batch ) {
		float axisSize = 128.0f;
		ShapeRenderer shapes = new ShapeRenderer( );
		shapes.setProjectionMatrix( batch.getProjectionMatrix( ) );
		Vector2 pos = getPosition( ).mul( Util.BOX_TO_PIXEL );
		shapes.begin( ShapeType.Line );
		shapes.setColor( 1.0f, 0.0f, 0.0f, 1.0f );
		shapes.line( pos.x, pos.y, pos.x + axisSize, pos.y ); // Red: X-axis
		shapes.setColor( 0.0f, 0.0f, 1.0f, 1.0f );
		shapes.line( pos.x, pos.y, pos.x, pos.y + axisSize ); // Blue: Y-axis
		if ( sprite != null ) {
			shapes.setColor( 0.0f, 1.0f, 0.0f, 1.0f );
			shapes.line( pos.x, pos.y, pos.x - sprite.getOriginX( ), pos.y
					- sprite.getOriginY( ) ); // Green: Sprite Origin
		}
		shapes.end( );
	}

	/**
	 * this is called from event triggers during a world step so you dont remove
	 * box2d data while the world is locked
	 */
	public void setRemoveNextStep( ) {
		removeNextStep = true;
	}

	/**
	 * if this entity is to be removed next step return true used by skeletons
	 * to remove from thier list after updating this entity so that box2d is
	 * removed first
	 * 
	 * @return
	 */
	public boolean getRemoveNextStep( ) {
		return removeNextStep;
	}

	public void remove( ) {
		if ( body != null ) {
			while ( body.getJointList( ).iterator( ).hasNext( ) ) {
				world.destroyJoint( body.getJointList( ).get( 0 ).joint );
			}
			world.destroyBody( body );
		}
	}

	public void update( float deltaTime ) {
		if ( removeNextStep ) {
			remove( );
		} else if ( body != null ) {
			// animation stuff may go here
			Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
			if ( sprite != null ) {
				sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
				sprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
			}
			if ( body != null && anchor != null ) {
				updateAnchor( );
			}
			// animation stuff may go here
			bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
			if ( sprite != null ) {
				sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
				sprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
				sprite.update( deltaTime );
			}
			updateDecals(deltaTime);
		}
	}

	/**
	 * Update the mover of this entity, if it exists. Now separated from
	 * update() so that it can be called whenever skeleton wants.
	 * 
	 * @param deltaTime
	 */
	public void updateMover( float deltaTime ) {
		if ( active ) {
			if ( body != null ) {
				if ( currentMover( ) != null ) {
					currentMover( ).move( deltaTime, body );
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
	 * @param texture
	 *            from which a sprite can be generated, or null, if loading
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
			this.offset.set( type.origin.x, type.origin.x );
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
	/**
	 * Builds a sprite from a TextureAtlas. If the texture is null, it attempts to
	 * load one from the XML definitions
	 * 
	 * @param texture
	 *            from which a sprite can be generated, or null, if loading
	 * @return the loaded/generated sprite, or null if neither applies
	 */
	protected Sprite constructSprite( TextureAtlas atlas ) {
		Sprite sprite;
		Vector2 origin;

		IAnimator anim;
		
		if (type.animatorType.equals( "player" )){
			anim = new PlayerAnimator(type.atlases, (Player)this);			
		} else {
			anim = new SimpleFrameAnimator().maxFrames( type.atlases.get( 0 ).getRegions( ).size );
		}
		sprite = new Sprite(type.atlases, anim);
		sprite.setScale( type.spriteScale.x, type.spriteScale.y );
		origin = new Vector2( type.origin.x, type.origin.y );		
		sprite.setOrigin( origin.x, origin.y );
		return sprite;
	}
	public void Move( Vector2 vector ) {
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
	 * This function adds a mover to the entity, YOU MUST SPECIFIY WHICH STATE
	 * IT IS ASSOCIATED WITH EITHER IDLE, DOCILE, HOSTILE
	 * 
	 * This fucntions also replaces the mover associated with that robotstate,
	 * so you cannot get that old mover back
	 * 
	 * @param mover
	 *            - Imover
	 * @param robotState
	 *            - for example: RobotState.Idle
	 * @author Ranveer
	 */
	public void addMover( IMover mover, RobotState robotState ) {
		int index = robotStateMap.get( robotState );
		moverArray.set( index, mover );
	}

	/**
	 * Changes robotState from current to the argument
	 * 
	 * @param robotState
	 *            - for example: RobotState.IDLE
	 * @author Ranveer
	 */
	public void setCurrentMover( RobotState robotState ) {
		currentRobotState = robotState;
	}

	/**
	 * Sets the mover associated with the argument's robotstate to null.
	 * Warning, this gets rid of old mover
	 * 
	 * @param robotState
	 *            - for example: RobotState.IDLE
	 * @author Ranveer
	 */
	public void setMoverNull( RobotState robotState ) {
		int index = robotStateMap.get( robotState );
		moverArray.set( index, null );
	}

	/**
	 * Sets the current state's mover to null Warning, this gets rid of old
	 * mover
	 * 
	 * @author Ranveer
	 */
	public void setMoverNullAtCurrentState( ) {
		int index = robotStateMap.get( currentRobotState );
		moverArray.set( index, null );
	}

	/**
	 * Replaces current state's mover with the argument
	 * 
	 * @param mover
	 * @author Ranveer
	 */
	public void setMoverAtCurrentState( IMover mover ) {
		int index = robotStateMap.get( currentRobotState );
		moverArray.set( index, mover );
	}

	/**
	 * gets the current RobotState of the entity example: p.getCurrentState() ==
	 * RobotState.IDLE
	 * 
	 * @return RobotState
	 * @author Ranveer
	 */
	public RobotState getCurrentState( ) {
		return currentRobotState;
	}

	/**
	 * gets the current mover, in the current robotstate
	 * 
	 * @return IMover
	 * @author Ranveer
	 */
	public IMover currentMover( ) {
		return moverArray.get( robotStateMap.get( currentRobotState ) );
	}

	/**
	 * Determines if entity is solid, which means the player can jump off of it
	 * 
	 * @return boolean
	 */
	public boolean isSolid( ) {
		return this.solid;
	}

	/**
	 * sets entity to either solid or not, determines whether player can jump
	 * off of it
	 * 
	 * @param solid
	 *            - boolean
	 */
	public void setSolid( boolean solid ) {
		this.solid = solid;
	}

	/**
	 * Sets the energy of the current body. Energy is a new property for
	 * Entities that is meant to scale impulses. It currently does nothing, but
	 * it's here if someone wants to use it.
	 * 
	 * @param energy
	 */
	public void setEnergy( float energy ) {
		this.energy = energy;
	}

	public float getEnergy( ) {
		return energy;
	}

	/**
	 * Sets body awake, used in
	 * 
	 * @param solid
	 */
	public void setAwake( ) {
		body.setAwake( true );
	}

	/**
	 * Determines whether an entity should be deleted on next update or not
	 * 
	 * @param m
	 *            - boolean
	 */
	public void setMaintained( boolean m ) {
		maintained = m;
	}

	/**
	 * checks whether an entity is currently being maintained
	 * 
	 * @return boolean
	 */
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

	/**
	 * returns whether an entity is visible, or on screen
	 * 
	 * @return boolean
	 */
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

	/**
	 * checks whether if the current mover is active, or being updated or not
	 * 
	 * @return boolean
	 */
	public boolean isActive( ) {
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
				// set category
				filter.categoryBits = category;
				// set mask
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

	/**
	 * sets the Density of all fixtures associated with this entity
	 * 
	 * @param density
	 *            - float
	 */
	public void setDensity( float density ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setDensity( density );
		}

	}

	/**
	 * sets the friction of all fixtures associated with this entity
	 * 
	 * @param friction
	 *            - float
	 */
	public void setFriction( float friction ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setFriction( friction );
		}
	}

	/**
	 * sets the restituion of all fixtures associated with this entity
	 * 
	 * @param restitution
	 *            - float
	 */
	public void setRestitution( float restitution ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setRestitution( restitution );
		}
	}

	/**
	 * sets the gravity scale of this entity
	 * 
	 * @param scale
	 *            - float
	 */
	public void setGravScale( float scale ) {
		if ( body != null ) {
			body.setGravityScale( scale );
		}
	}

	/**
	 * Return whether this entity's body is kinematic.
	 * 
	 * @author stew
	 * @return false if Dynamic static or has no body otherwise true
	 */
	public boolean isKinematic( ) {
		if ( body != null ) {
			return ( body.getType( ) == BodyType.KinematicBody );
		}
		return false;
	}

	/**
	 * Get the sprite width of this entity
	 * 
	 * @return Pixel float width of sprite
	 */
	public float getPixelWidth( ) {
		if ( sprite != null ) {
			return sprite.getWidth( );
		}
		return Float.NaN;
	}

	/**
	 * gets the type of entity
	 */
	public EntityType getEntityType( ) {
		return entityType;
	}

	/**
	 * Get the sprite height of this entity
	 * 
	 * @return Pixel float height of sprite
	 */
	public float getPixelHeight( ) {
		if ( sprite != null ) {
			return sprite.getHeight( );
		}
		return Float.NaN;
	}

	/**
	 * Get the sprite meter width of this entity
	 * 
	 * @return METER float width of sprite
	 */
	public float getMeterWidth( ) {
		if ( sprite != null ) {
			return sprite.getWidth( ) * Util.PIXEL_TO_BOX;
		}
		return Float.NaN;
	}

	/**
	 * Get the sprite METER height of this entity
	 * 
	 * @return METER float height of sprite
	 */
	public float getMeterHeight( ) {
		if ( sprite != null ) {
			return sprite.getHeight( ) * Util.PIXEL_TO_BOX;
		}
		return Float.NaN;
	}

	/**
	 * for debug
	 * 
	 * @author stew
	 */
	public String toString( ) {
		return "Entity[" + name + "] pos:" + body.getPosition( )
				+ ", body.active:" + body.isActive( ) + ", body.awake:"
				+ body.isAwake( );
	}

	public void createAnchor( ) {
		Vector2 centPos = new Vector2( body.getWorldCenter( ).x
				* Util.BOX_TO_PIXEL, body.getWorldCenter( ).y
				* Util.BOX_TO_PIXEL );
		this.anchor = new Anchor( centPos );
		AnchorList.getInstance( ).addAnchor( anchor );
	}

	/**
	 * Sets up moverArray and fills it with null and set up the EnumMap Idle = 0
	 * Docile = 1 Hostile = 2
	 * 
	 * and sets this entity's default state as IDLE
	 * 
	 * Will be optimized soon
	 * 
	 * @author Ranveer
	 */
	private void setUpRobotState( ) {
		moverArray = new ArrayList< IMover >( );
		for ( int i = 0; i < INITAL_CAPACITY; ++i )
			moverArray.add( null );
		robotStateMap = new EnumMap< RobotState, Integer >( RobotState.class );
		robotStateMap.put( RobotState.IDLE, 0 );
		robotStateMap.put( RobotState.DOCILE, 1 );
		robotStateMap.put( RobotState.HOSTILE, 2 );
		// robotStateMap.put( RobotState.CUSTOM1, 3 );
		// robotStateMap.put( RobotState.CUSTOM2, 4 );
		// robotStateMap.put( RobotState.CUSTOM3, 5 );

		// Initalize to idle
		currentRobotState = RobotState.IDLE;
	}

	/**
	 * 
	 */
	public void addDecal( Sprite s, Vector2 offset, float angle){
		this.decals.add( s );
		this.decalOffsets.add( offset );
		this.decalAngles.add( angle );
	}
	
	public void addDecal( Sprite s, Vector2 offset ) {
		addDecal( s, offset, 0.0f);
	}

	public void addDecal( Sprite s ) {
		addDecal( s, new Vector2( s.getX( ), s.getY( ) ) );
	}

	public void updateDecals( float deltaTime ) {
		Vector2 bodyPos = this.getPositionPixel( );
		float angle = this.getAngle( ), cos = ( float ) Math.cos( angle ), sin = ( float ) Math
				.sin( angle );
		float x, y, r;
		Vector2 offset;
		Sprite decal;
		for ( int i = 0; i < decals.size( ); i++ ) {
			offset = decalOffsets.get( i );
			decal = decals.get( i );
			r = decalAngles.get( i );
			x = bodyPos.x + (offset.x * cos) - (offset.y * sin);
			y = bodyPos.y + (offset.y * cos) + (offset.x * sin);
			decal.setPosition( x , y );
			decal.setRotation( r + (angle * Util.RAD_TO_DEG) );
		}
	}

	/**
	 * 
	 */
	public void drawDecals( SpriteBatch batch ) {
		for ( Sprite decal : decals ) {
			decal.draw( batch );
		}
	}

	public float getAngle( ) {
		if ( body != null )
			return body.getAngle( );
		return sprite.getRotation( );
	}

	/**
	 * prints Fixture's index in FixtureList.
	 * 
	 * @param fix
	 *            Fixture
	 */
	public void getFixtureIndex( Fixture fix ) {
		for ( int i = 0; i < body.getFixtureList( ).size( ); i++ ) {
			if ( fix == body.getFixtureList( ).get( i ) ) {
				Gdx.app.log( name + " FixtureListIndex: ", "" + i );
				return;
			}
		}
	}

	/**
	 * returns whether an entity can crush the player
	 * 
	 * @return boolean
	 */
	public boolean getCrushing( ) {
		return crushing;
	}

	/**
	 * sets flag to determine if an entity can crush, also turns off OneSided
	 * 
	 * @param value
	 *            boolean
	 */
	public void setCrushing( boolean value ) {
		crushing = value;
	}
	
	/**
	 * Careful. You generally won't directly call this. Root skeleton can delete entire skeletons
	 * so it would be better to just use RootSkeleton.destroySkeleton()
	 * @author stew
	 */
	public void dispose(){
		body.getWorld( ).destroyBody( body );
	}
}