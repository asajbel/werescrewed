package com.blindtigergames.werescrewed.entity.builders;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.screws.*;
import com.blindtigergames.werescrewed.skeleton.Skeleton;

public class ScrewBuilder extends GenericEntityBuilder< ScrewBuilder > {
	protected ScrewType screwType;
	protected Entity entity;
	protected Skeleton skeleton;
	protected int max, startDepth;
	protected boolean resetable;
	
	public ScrewBuilder(){
		super();
		this.screwType = ScrewType.SCREW_STRIPPED;
		this.entity = null;
		this.skeleton = null;
		this.max = 100;
		this.startDepth = 0;
	}
	
	@Override
	public Entity build(){
		return buildScrew();
	}
	/**
	 * Sets the screw type for the created screw.
	 * This includes null, if you really want that.
	 * 
	 * @param t
	 * @return this
	 */
	public ScrewBuilder screwType(ScrewType t){
		this.screwType = t;
		return this;
	}
	/**
	 * Sets the screw type using a string.
	 * If the string doesn't return a valid enum,
	 * nothing gets changed.
	 * 
	 * @param s
	 * @return this
	 */
	public ScrewBuilder screwType(String s){
		ScrewType t = ScrewType.fromString( s );
		if (t != null)
			return this.screwType(t);
		return this;
	}
	
	/**
	 * Sets screw properties from a hashmap
	 * Will likely be used for the Gleed2D loader.
	 */
	@Override
	public ScrewBuilder properties(HashMap<String,String> props){
		super.properties( props );
		this.screwType(props.get( "ScrewType" ));
		this.max(Integer.decode( props.get("ScrewMax") ));
		return this;
	}
	
	public ScrewBuilder entity(Entity e){
		this.entity = e;
		return this;
	}
	
	public ScrewBuilder skeleton(Skeleton s){
		this.skeleton = s;
		return this;
	}
	
	public ScrewBuilder max(int m){
		this.max = m;
		return this;		
	}
	
	public ScrewBuilder startDepth(int d){
		this.startDepth = d;
		return this;		
	}
	
	public ScrewBuilder resetable(boolean r){
		this.resetable = r;
		return this;
	}
	
	@Override
	public boolean canBuild(){
		return (world != null);
	}
	
	public Screw buildScrew(){
		Screw out = null;
		if (screwType.equals( ScrewType.SCREW_STRIPPED )){
			out = this.buildStrippedScrew( );
		} else if (screwType.equals( ScrewType.SCREW_STRUCTURAL )){
			out = this.buildStructureScrew( );
		} else if (screwType.equals( ScrewType.SCREW_PUZZLE )){
			out = this.buildPuzzleScrew( );
		} else if (screwType.equals( ScrewType.SCREW_BOSS )){
			out = this.buildBossScrew( ); 
		}
		return out;
	}
	
	public StrippedScrew buildStrippedScrew(){
		StrippedScrew out = null;
		if (canBuild() && entity != null)
			out = new StrippedScrew(name, world, pos, entity);
		return out;
	}
	
	public StructureScrew buildStructureScrew(){
		StructureScrew out = null;
		if (canBuild() && entity != null && skeleton != null)
			out = new StructureScrew(name, pos, max, entity, skeleton, world);
		return out;
	}
	
	public PuzzleScrew buildPuzzleScrew(){
		PuzzleScrew out = null;
		if (canBuild() && entity != null)
			out = new PuzzleScrew(name, pos, max, entity, world, startDepth, resetable);
		return out;
	}
	
	public BossScrew buildBossScrew(){
		BossScrew out = null;
		if (canBuild() && entity != null && skeleton != null)
			out = new BossScrew(name, pos, max, entity, skeleton, world);
		return out;
	}
}
