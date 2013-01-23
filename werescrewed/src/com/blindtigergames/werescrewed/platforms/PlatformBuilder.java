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

public class PlatformBuilder {
	float width = 1.0f, height = 1.0f;
	float outerWidth, outerHeight;
	float thickX, thickY;
	float scale = 1.0f;
	float density = 1.0f, friction = 0.5f, restitution = 0.1f,
			gravScale = 0.1f;
	float positionX = 0.0f, positionY = 0.0f;
	boolean flipHorizonal = false, flipVertical = false, isOneSided = false;
	Shapes shape = null;
	Texture texture;

	public PlatformBuilder( ) {
	}

	public PlatformBuilder setShape( Shapes shape ) {
		this.shape = shape;
		return this;
	}

	public PlatformBuilder setScale( float scale ) {
		this.scale = scale;
		return this;
	}

	public PlatformBuilder setDimensions( Vector2 dimension ) {
		this.width = dimension.x;
		this.height = dimension.y;
		return this;
	}

	public PlatformBuilder setDimensions( float width, float height ) {
		this.width = width;
		this.height = height;
		return this;
	}

	public PlatformBuilder setPosition( float posX, float posY ) {
		this.positionX = posX;
		this.positionY = posY;
		return this;
	}

	public PlatformBuilder setWidth( float width ) {
		this.width = width;
		return this;
	}

	public PlatformBuilder setHeight( float height ) {
		this.height = height;
		return this;
	}

	public PlatformBuilder setOuterWidth( float outerWidth ) {
		this.outerWidth = outerWidth;
		return this;
	}

	public PlatformBuilder setOuterHeight( float outerHeight ) {
		this.outerHeight = outerHeight;
		return this;
	}

	public PlatformBuilder setDensity( float density ) {
		this.density = density;
		return this;
	}

	public PlatformBuilder setFriction( float friction ) {
		this.friction = friction;
		return this;
	}

	public PlatformBuilder setResitituion( float restitution ) {
		this.restitution = restitution;
		return this;
	}

	public PlatformBuilder setGravScale( float gravscale ) {
		this.gravScale = gravscale;
		return this;
	}

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
	
	public PlatformBuilder setOneSided( boolean oneSide ) {
		this.isOneSided = oneSide;
		return this;
	}

	public RoomPlatform buildRoomPlatform( World world ) {
		RoomPlatform rp = new RoomPlatform( "room", new Vector2( positionX,
				positionY ), this.texture, this.width, this.height, world );

		rp.setDensity( this.density );
		rp.setFriction( this.friction );
		rp.setRestitution( this.restitution );
		rp.setGravScale( this.gravScale );
		return rp;
	}

	public TiledPlatform buildTilePlatform( World world ) {
		TiledPlatform tp = new TiledPlatform( "tile", new Vector2( positionX,
				positionY ), this.texture, this.width, this.height, this.isOneSided, world );

		tp.setDensity( this.density );
		tp.setFriction( this.friction );
		tp.setRestitution( this.restitution );
		tp.setGravScale( this.gravScale );
		return tp;
	}

	public ShapePlatform buildShapePlatform( World world ) {
		ShapePlatform sp = new ShapePlatform( "shape", new Vector2( positionX,
				positionY ), this.texture, world, this.shape, this.width,
				this.height, this.flipHorizonal );

		sp.setDensity( this.density );
		sp.setFriction( this.friction );
		sp.setRestitution( this.restitution );
		sp.setGravScale( this.gravScale );
		return sp;
	}

}