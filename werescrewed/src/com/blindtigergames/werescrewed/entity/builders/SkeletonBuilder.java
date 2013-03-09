package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class SkeletonBuilder extends GenericEntityBuilder<SkeletonBuilder>{

	protected Array< Vector2 > polyVerts;

	protected float 	density;
	private BodyType bodyType;
	
	public SkeletonBuilder(World world){
		super();
		reset( );
		super.world = world;
	}

	
	@Override
	public SkeletonBuilder reset(){
		super.reset( );
		this.polyVerts = null;
		this.bodyType = BodyType.KinematicBody;
		this.density = 1.0f;
		return this;
	}
	
	/**
	 * Set the entire vertice list for the polySprite on the next built skeleton
	 * @param verts array of verts in pixels.
	 * @return
	 */
	public SkeletonBuilder setVerts(Array< Vector2 > verts){
		this.polyVerts = verts;
		return this;
	}

	/**
	 * Add a vertice to the polySprite for this skeleton
	 * @param vert, (x,y) in pixels
	 * @return
	 */
	public SkeletonBuilder vert(Vector2 vert){
		if ( polyVerts == null ){
			polyVerts = new Array< Vector2 >();
		}
		polyVerts.add( vert );
		return this;
	}
	
	/**
	 * Add a vertice to the polySprite for this skeleton
	 * @param x x-position in pixels
	 * @param y y-position in pixels.
	 * @return
	 */
	public SkeletonBuilder vert(float x, float y){
		return this.vert( new Vector2( x,y ));
	}
	
	public SkeletonBuilder dynamic( boolean d ) {
		if (d){
			return this.dynamic( );
		}
		return this.kinematic( );
	}
	
	public SkeletonBuilder dynamic(){
		bodyType = BodyType.DynamicBody;
		return this;
	}
	
	public SkeletonBuilder staticBody(){
		bodyType = BodyType.StaticBody;
		return this;
	}
	
	public SkeletonBuilder kinematic(){
		bodyType = BodyType.KinematicBody;
		return this;
	}
	
	/**
	 * 	
	 * @param density - float used for density, default is 1.0f
	 * @return SkeletonBuilder
	 */
		public SkeletonBuilder density( float density ) {
			this.density = density;
			return this;
		}
	
	/**
	 * Builds a friggin root skeleton, what do you want jeese.
	 */
	public RootSkeleton buildRoot(){
		return new RootSkeleton( "root", new Vector2(), null, world );
	}	
	
	@Override
	public Skeleton build(){
		Skeleton out = new Skeleton( name, pos, (polyVerts==null)?tex:null, super.world );
		if ( polyVerts != null ){
			out.sprite = new PolySprite( tex, polyVerts );
		}
		out.body.setType( bodyType );
		out.setDensity( this.density );
		return out;
	}
}
