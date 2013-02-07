package com.blindtigergames.werescrewed.entity.builders;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.PlatformType;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.ShapePlatform;
import com.blindtigergames.werescrewed.platforms.Shapes;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;

/**
 * PlatformBuilder should make building platforms a lot simpler and clearer
 * Work in progress
 * @author Ranveer
 * 
 */

// Later should be loaded in by file
public class PlatformBuilder extends GenericEntityBuilder<PlatformBuilder> {
	protected float width = 1.0f;
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
	boolean flipHorizonal = false;
	boolean flipVertical = false;
	boolean isOneSided = false;
	protected PlatformType pType = PlatformType.SHAPE;
	Shapes shape = null;
	
/**
 * 
 * @param world - Box2d world, only needs to be set once
 * @return PlatformBuilder
 */
	
	public PlatformBuilder( World world ) {
		super();
		super.world(world);
	}
	
/**
 * 
 * @param shape - Pick a shape to create (trapezoid, rhombus, etc)
 * 	Default is null
 * @return PlatformBuilder
 */
	public PlatformBuilder shape( Shapes shape ) {
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
 * @param w - set width with a float, default is 1
 * @return PlatformBuilder
 */	
	public PlatformBuilder width( float w ) {
		this.width = w;
		return this;
	}
/**
 * 
 * @param h - set height with a float, default is 1
 * @return PlatformBuilder
 */		
	public PlatformBuilder height( float h ) {
		this.height = h;
		return this;
	}
/**
 * 
 * @param dimension - set width/height with Vector2, default is (1,1)
 * @return PlatformBuilder
 */
	public PlatformBuilder dimensions( Vector2 dimension ) {
		return this.width( dimension.x ).height( dimension.y );
	}
	
/**
 * 
 * @param width - float width of platform
 * @param height - float height of platform
 * @return PlatformBuilder
 */
	public PlatformBuilder dimensions( float width, float height ) {
		return this.width( width ).height( height );
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
		this.tex = tex;
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
	 * resets all the values to its default, use between builds
	 */
	@Override
	public PlatformBuilder reset(){
		super.resetInternal();
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
		this.flipHorizonal = false;
		this.flipVertical = false;
		this.isOneSided = false;
		this.shape = null;
		this.tex = null;
		this.name = "No name";
		return this;
	}
	
	@Override
	public PlatformBuilder properties(HashMap<String,String> props){
		super.properties( props );
		
		return this;
	}
	
	@Override
	public Platform build(){
		return null;
	}

/**
 * builds room platform according to specs	
 * @return RoomPlatform
 */
	public RoomPlatform buildRoomPlatform( ) {
		RoomPlatform rp = new RoomPlatform( this.name, this.pos, this.tex, this.width, this.height, world );

		rp.setDensity( this.density );
		rp.setFriction( this.friction );
		rp.setRestitution( this.restitution );
		rp.setGravScale( this.gravScale );
		return rp;
	}

/**
 * builds tile platform with specified numbers
 * @return TiledPlatform
 */
	public TiledPlatform buildTilePlatform( ) {
		TiledPlatform tp = new TiledPlatform( this.name, this.pos, this.tex, this.width, this.height, this.isOneSided, world );

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
		ShapePlatform sp = new ShapePlatform( this.name, this.pos, this.tex, world, this.shape, this.width,
				this.height, this.flipHorizonal );

		sp.setDensity( this.density );
		sp.setFriction( this.friction );
		sp.setRestitution( this.restitution );
		sp.setGravScale( this.gravScale );
		return sp;
	}

}