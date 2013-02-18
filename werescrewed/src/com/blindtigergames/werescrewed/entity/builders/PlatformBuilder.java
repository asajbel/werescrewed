package com.blindtigergames.werescrewed.entity.builders;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.PlatformType;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;

/**
 * PlatformBuilder should make building platforms a lot simpler and clearer
 * Work in progress
 * @author Ranveer, Stew
 * 
 */

// Later should be loaded in by file
public class PlatformBuilder extends GenericEntityBuilder<PlatformBuilder> {
	protected float 	width;
	protected float 	height;
	protected float 	thickX;
	protected float 	thickY;
	protected float 	scale;
	protected float 	density;
	protected float 	friction;
	protected float 	restitution;
	protected float 	gravScale;
	protected boolean 	flipHorizonal;
	protected boolean 	flipVertical;
	protected boolean 	isOneSided;
	protected boolean 	moveable;
	protected BodyType 	bodyType;
	
/**
 * 
 * @param world - Box2d world, only needs to be set once
 * @return PlatformBuilder
 */
	
	public PlatformBuilder( World world ) {
		super();
		reset();
		super.world(world);
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
 * @param density - float used for density, default is 1.0f
 * @return PlatformBuilder
 */
	public PlatformBuilder density( float density ) {
		this.density = density;
		return this;
	}

/**
 * 	
 * @param friction - float friction, default is 0.5f
 * @return PlatformBuilder
 */
	public PlatformBuilder friction( float friction ) {
		this.friction = friction;
		return this;
	}

/**
 * 	
 * @param restitution - float restitution, default is 0.0f
 * @return PlatformBuilder
 */
	public PlatformBuilder restitution( float restitution ) {
		this.restitution = restitution;
		return this;
	}

/**
 * 
 * @param gravscale - float gravity scale, default is 0.1f
 * @return PlatformBuilder
 */
	public PlatformBuilder gravityScale( float gravscale ) {
		this.gravScale = gravscale;
		return this;
	}

	public PlatformBuilder flipHorizontal( boolean flipHori ) {
		this.flipHorizonal = flipHori;
		return this;
	}

	public PlatformBuilder flipVertical( boolean flipVert ) {
		this.flipVertical = flipVert;
		return this;
	}
	
/**
 * 
 * @param oneSide - boolean set platform to oneside, default false
 * @return PlatformBuilder
 */
	public PlatformBuilder oneSided( boolean oneSide ) {
		this.isOneSided = oneSide;
		return this;
	}

/**
 * 
 * @param moving - boolean tells if platform could move, default false
 * @return PlatformBuilder
 */
	
	public PlatformBuilder moveable( boolean moving ) {
		this.moveable = moving;
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
		this.moveable = false;
		this.tex = null;
		this.name = "No name";
		this.bodyType = BodyType.KinematicBody;
		return this;
	}
	
	public PlatformBuilder dynamic(){
		bodyType = BodyType.DynamicBody;
		return this;
	}
	
	public PlatformBuilder staticBody(){
		bodyType = BodyType.StaticBody;
		return this;
	}
	
	public PlatformBuilder kinematic(){
		bodyType = BodyType.KinematicBody;
		return this;
	}
	
	@Override
	public PlatformBuilder properties(HashMap<String,String> props){
		super.properties( props );
		
		return this;
	}
	
/**
 * builds tile platform with specified numbers
 * @return TiledPlatform
 */
	public TiledPlatform buildTilePlatform( ) {
		TiledPlatform tp = new TiledPlatform( this.name, 
				                              this.pos, 
				                              this.tex, 
				                              this.width, 
				                              this.height, 
				                              this.isOneSided, 
				                              this.moveable, 
				                              this.world);
		tp.body.setType( bodyType );
		tp.setDensity( this.density );
		tp.setFriction( this.friction );
		tp.setRestitution( this.restitution );
		tp.setGravScale( this.gravScale );
		return tp;
	}
	/**
	 * builds complex platform from available data.
	 * @return ComplexPlatform
	 */
	public Platform buildComplexPlatform( ) {
		Platform cp = new Platform( this.name,
									this.type,
									this.world,
									this.pos,
									this.rot,
									new Vector2(this.scale,this.scale));
		
		cp.setPlatformType(PlatformType.COMPLEX);
		cp.body.setType( bodyType );
		cp.setDensity( this.density );
		cp.setFriction( this.friction );
		cp.setRestitution( this.restitution );
		cp.setGravScale( this.gravScale );
		return cp;
	}

}