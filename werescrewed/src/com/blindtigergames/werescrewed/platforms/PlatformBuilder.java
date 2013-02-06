package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * PlatformBuilder should make building platforms a lot simpler and clearer
 * Work in progress
 * @author Ranveer
 * 
 */

// Later should be loaded in by file
public class PlatformBuilder {
	float width = 1.0f;
	float height = 1.0f;
	float outerWidth = 1.0f;
	float outerHeight = 1.0f;
	float thickX = 1.0f;
	float thickY = 1.0f;
	float scale = 1.0f;
	float density = 1.0f;
	float friction = 0.5f;
	float restitution = 0.1f;
	float gravScale = 0.1f;
	float positionX = 0.0f;
	float positionY = 0.0f;
	boolean flipHorizonal = false;
	boolean flipVertical = false;
	boolean isOneSided = false;
	boolean moveable = false;
	Shapes shape = null;
	Texture texture = null;
	World world = null;
	String name = "No name";
	
/**
 * 
 * @param world - Box2d world, only needs to be set once
 * @return PlatformBuilder
 */
	
	public PlatformBuilder( World world ) {
		this.world = world;
	}
	
/**
 * 
 * @param shape - Pick a shape to create (trapezoid, rhombus, etc)
 * 	Default is null
 * @return PlatformBuilder
 */
	public PlatformBuilder setShape( Shapes shape ) {
		this.shape = shape;
		return this;
	}
/**
 * 
 * @param scale - set scale of the whole platform, default is 1
 * @return PlatformBuilder
 */
	public PlatformBuilder setScale( float scale ) {
		this.scale = scale;
		return this;
	}
	
/**
 * 
 * @param dimension - set width/height with Vector2, default is (1,1)
 * @return PlatformBuilder
 */
	public PlatformBuilder setDimensions( Vector2 dimension ) {
		this.width = dimension.x;
		this.height = dimension.y;
		return this;
	}
	
/**
 * 
 * @param width - float width of platform
 * @param height - float height of platform
 * @return PlatformBuilder
 */
	public PlatformBuilder setDimensions( float width, float height ) {
		this.width = width;
		this.height = height;
		return this;
	}

/**
 * 
 * @param name - String name of platform, default is "noname"
 * @return PlatformBuilder
 */
	public PlatformBuilder setName( String name ){
		this.name = name;
		return this;
	}
	
/**
 * 
 * @param posX - float position X, default is 0
 * @param posY - float position Y, default is 0
 * @return PlatformBuilder
 */
	public PlatformBuilder setPosition( float posX, float posY ) {
		this.positionX = posX;
		this.positionY = posY;
		return this;
	}

/**
 * 
 * @param width - float width, default 1
 * @return PlatformBuilder
 */
	public PlatformBuilder setWidth( float width ) {
		this.width = width;
		return this;
	}

/**
 * 
 * @param height - float height, default 1
 * @return PlatformBuilder
 */
	public PlatformBuilder setHeight( float height ) {
		this.height = height;
		return this;
	}

/**
 * 
 * @param outerWidth - float used for cross shape
 * @return PlatformBuilder
 */
	public PlatformBuilder setOuterWidth( float outerWidth ) {
		this.outerWidth = outerWidth;
		return this;
	}

	/**
	 * 
	 * @param outerHeight - float used for cross shape
	 * @return PlatformBuilder
	 */
	public PlatformBuilder setOuterHeight( float outerHeight ) {
		this.outerHeight = outerHeight;
		return this;
	}

/**
 * 	
 * @param density - float used for density, default is 1.0f
 * @return PlatformBuilder
 */
	public PlatformBuilder setDensity( float density ) {
		this.density = density;
		return this;
	}

/**
 * 	
 * @param friction - float friction, default is 0.5f
 * @return PlatformBuilder
 */
	public PlatformBuilder setFriction( float friction ) {
		this.friction = friction;
		return this;
	}

/**
 * 	
 * @param restitution - float restitution, default is 0.0f
 * @return PlatformBuilder
 */
	public PlatformBuilder setResitituion( float restitution ) {
		this.restitution = restitution;
		return this;
	}

/**
 * 
 * @param gravscale - float gravity scale, default is 0.1f
 * @return PlatformBuilder
 */
	public PlatformBuilder setGravScale( float gravscale ) {
		this.gravScale = gravscale;
		return this;
	}

/**
 * 	
 * @param tex - Texture for the platform, default is null
 * @return
 */
	public PlatformBuilder setTexture( Texture tex ) {
		this.texture = tex;
		return this;
	}

	public PlatformBuilder setFlipHorizontal( boolean flipHori ) {
		this.flipHorizonal = flipHori;
		return this;
	}

	public PlatformBuilder setFlipVertical( boolean flipVert ) {
		this.flipVertical = flipVert;
		return this;
	}
	
/**
 * 
 * @param oneSide - boolean set platform to oneside, default false
 * @return PlatformBuilder
 */
	public PlatformBuilder setOneSided( boolean oneSide ) {
		this.isOneSided = oneSide;
		return this;
	}

/**
 * 
 * @param moving - boolean tells if platform could move, default false
 * @return PlatformBuilder
 */
	
	public PlatformBuilder setMoveable( boolean moving ) {
		this.moveable = moving;
		return this;
	}

	/**
	 * resets all the values to its default, use between builds
	 */
	public void reset(){
		this.width = 1.0f;
		this.height = 1.0f;
		this.outerWidth = 1.0f;
		this.outerHeight = 1.0f;
		this.thickX = 1.0f;
		this.thickY = 1.0f;
		this.scale = 1.0f;
		this.density = 1.0f;
		this.friction = 0.5f;
		this.restitution = 0.1f;
		this.gravScale = 0.1f;
		this.positionX = 0.0f;
		this.positionY = 0.0f;
		this.flipHorizonal = false;
		this.flipVertical = false;
		this.isOneSided = false;
		this.moveable = false;
		this.shape = null;
		this.texture = null;
		this.name = "No name";
	}

/**
 * builds tile platform with specified numbers
 * @return TiledPlatform
 */
	public TiledPlatform buildTilePlatform( ) {
		TiledPlatform tp = new TiledPlatform( this.name, new Vector2(
				positionX, positionY ), this.texture, this.width, this.height,
				this.isOneSided, this.moveable, world );

		tp.setDensity( this.density );
		tp.setFriction( this.friction );
		tp.setRestitution( this.restitution );
		tp.setGravScale( this.gravScale );
		return tp;
	}

	/**
	 * builds shape platform
	 * @return ShapePlatform
	 */
	public ShapePlatform buildShapePlatform( ) {
		ShapePlatform sp = new ShapePlatform( this.name, new Vector2( positionX,
				positionY ), this.texture, world, this.shape, this.width,
				this.height, this.flipHorizonal );

		sp.setDensity( this.density );
		sp.setFriction( this.friction );
		sp.setRestitution( this.restitution );
		sp.setGravScale( this.gravScale );
		return sp;
	}

}