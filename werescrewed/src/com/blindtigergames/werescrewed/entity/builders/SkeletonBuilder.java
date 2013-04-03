package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.ActivateSkeleton;
import com.blindtigergames.werescrewed.entity.action.DeactivateSkeleton;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;

public class SkeletonBuilder extends GenericEntityBuilder<SkeletonBuilder>{

	protected Array< Vector2 > polyVertsFG, polyVertsBG;

	protected float 	density;
	private BodyType 	bodyType;
	protected boolean 	onBGverts;
	protected Texture 	texBackground, texForeground, texBody;
	protected boolean	hasDeactivateTrigger;
	
	public SkeletonBuilder(World world){
		super();
		reset( );
		super.world = world;
	}

	
	@Override
	public SkeletonBuilder reset(){
		super.reset( );
		this.polyVertsFG = null;
		this.polyVertsBG = null;
		this.bodyType = BodyType.KinematicBody;
		this.density = 1.0f;
		this.onBGverts = true;
		this.texBackground = null;
		this.texForeground = null;
		this.texBody = null;
		this.hasDeactivateTrigger = false;
		return this;
	}
	
	/**
	 * All following verts added will set to the background polysprite of this skeleton
	 * This is true by default
	 * @return
	 */
	public SkeletonBuilder bg(){
		this.onBGverts = true;
		return this;
	}
	
	/**
	 * All following verts will apply to the foreground polysprite
	 * @return
	 */
	public SkeletonBuilder fg(){
		this.onBGverts = false;
		return this;
	}
	
	public SkeletonBuilder hasDeactiveTrigger(boolean hasTrigger){
		this.hasDeactivateTrigger = hasTrigger;
		return this;
	}

	
	public SkeletonBuilder texForeground(Texture fgTex){
		this.texForeground = fgTex;
		return this;
	}
	
	public SkeletonBuilder texBackground(Texture bgTex){
		this.texBackground = bgTex;
		return this;
	}
	
	public SkeletonBuilder texBody(Texture bodyTex){
		this.texBody = bodyTex;
		return this;
	}
	
	/**
	 * Set the entire vertice list for the polySprite on the next built skeleton
	 * @param verts array of verts in pixels.
	 * @return
	 */
	public SkeletonBuilder setVerts(Array< Vector2 > verts){
		if ( onBGverts ){
			this.polyVertsBG = verts;
		}else{
			this.polyVertsFG = verts;
		}
		return this;
	}

	/**
	 * Add a vertice to the polySprite for this skeleton
	 * @param vert, (x,y) in pixels
	 * @return
	 */
	public SkeletonBuilder vert(Vector2 vert){
		Array< Vector2 > vertList;
		if ( onBGverts ){
			if ( polyVertsBG == null ){
				polyVertsBG = new Array< Vector2 >();
			}
			vertList = polyVertsBG;
		}else{
			if ( polyVertsFG == null ){
				polyVertsFG = new Array< Vector2 >();
			}
			vertList = polyVertsFG;
		}
		vertList.add( vert );
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
		Skeleton out = new Skeleton( name, pos, null, super.world, bodyType );
		if ( polyVertsFG != null && texForeground != null ){
			out.fgSprite = new PolySprite( texForeground, polyVertsFG );
		}
		if ( polyVertsBG != null && texBackground != null){
			System.out.println( name+": bbuilding bg polysprite" );
			out.bgSprite = new PolySprite( texBackground, polyVertsBG );
		}
		
		//out.body.setType( bodyType );
		out.setDensity( this.density );
		
		if ( hasDeactivateTrigger && polyVertsBG != null ){
			EventTriggerBuilder etb = new EventTriggerBuilder( world );
			EventTrigger et = etb.name( name+"-activator" ).skelePolygon( polyVertsBG )
					.position( pos ).addEntity( out )
					.beginAction( new ActivateSkeleton( ) )
					.endAction( new DeactivateSkeleton( ) ).repeatable( )
					.twoPlayersToDeactivate( ).build( );
			out.addEventTrigger( et );
			Gdx.app.log( "SkeletonBuilder", "I just built an event trigger" );
		}
		
		return out;
	}
}
