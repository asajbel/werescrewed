package com.blindtigergames.werescrewed.entity.builders;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.*;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.ArrayHash;

public class ScrewBuilder extends GenericEntityBuilder< ScrewBuilder > {
	protected ScrewType screwType;
	protected Entity entity;
	protected Skeleton skeleton;
	protected int max, startDepth;
	protected boolean resetable;
	protected Player player;
	protected boolean playerOffset;
	
	public ScrewBuilder(){
		super();
		this.screwType = ScrewType.SCREW_COSMETIC;
		this.entity = null;
		this.skeleton = null;
		this.max = 100;
		this.startDepth = 0;
		this.player = null;
		this.playerOffset = false;
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
	protected static final String screwTypeTag = "screwtype";
	protected static final String screwMaxTag = "screwmax";
	protected static final String screwResetTag = "resetable";
	@Override
	public ScrewBuilder properties(ArrayHash props){
		super.properties( props );
		if (props.containsKey( screwTypeTag )){
			this.screwType(props.get( screwTypeTag ));
		}
		if (props.containsKey( screwMaxTag )){
			this.max(Integer.decode( props.get( screwMaxTag ) ));
		}
		if (props.containsKey( screwResetTag )){
			if (props.get( screwResetTag ).equalsIgnoreCase( "false" ) 
				|| props.get( screwResetTag ).equalsIgnoreCase( "no" ) 
				|| props.get( screwResetTag ).equals( "0" ))
			{
				this.resetable( false );
			} else {
				this.resetable( true );
			}
		}
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
	
	public ScrewBuilder player(Player p){
		this.player = p;
		return this;
	}

	public ScrewBuilder playerOffset(boolean o){
		this.playerOffset = o;
		return this;
	}
	
	@Override
	public boolean canBuild(){
		return (world != null);
	}
	
	public Screw buildScrew(){
		Screw out = null;
		switch(screwType){
			case SCREW_STRIPPED:
				out = this.buildStrippedScrew( );
				break;
			case SCREW_STRUCTURAL:
				out = this.buildStructureScrew( );
				break;
			case SCREW_PUZZLE:
				out = this.buildPuzzleScrew( );
				break;
			case SCREW_BOSS:
				out = this.buildBossScrew( ); 
				break;
			case SCREW_RESURRECT:
				out = this.buildRezzScrew( );
				break;
			case SCREW_COSMETIC:
				out = this.buildCosmeticScrew( );
			default:
				break;
		}
		if (screwType.equals( ScrewType.SCREW_STRIPPED )){
		} else if (screwType.equals( ScrewType.SCREW_STRUCTURAL )){
		} else if (screwType.equals( ScrewType.SCREW_PUZZLE )){
		} else if (screwType.equals( ScrewType.SCREW_BOSS )){
		}
		return out;
	}
	public Screw buildCosmeticScrew(){
		Screw out = null;
		if (canBuild() && entity != null && skeleton != null){
			out = new Screw(name, pos, entity, world);
			if (skeleton != null){
				skeleton.addScrew(out);
				skeleton.addScrewForDraw( out );
			}
		}
		return out;
	}
	public StrippedScrew buildStrippedScrew(){
		StrippedScrew out = null;
		if (canBuild() && entity != null){
			out = new StrippedScrew(name, pos, entity, world);
			if (skeleton != null){
				skeleton.addStrippedScrew( out );
			}
		}
		return out;
	}
	
	public StructureScrew buildStructureScrew(){
		StructureScrew out = null;
		if (canBuild() && entity != null && skeleton != null){
			out = new StructureScrew(name, pos, max, entity, skeleton, world);
			if (skeleton != null){
				skeleton.addScrew(out);
				skeleton.addScrewForDraw( out );
			}
		}
		return out;
	}
	
	public PuzzleScrew buildPuzzleScrew(){
		PuzzleScrew out = null;
		if (canBuild() && entity != null){
			out = new PuzzleScrew(name, pos, max, entity, world, startDepth, resetable);
			if (skeleton != null){
				skeleton.addScrew(out);
				skeleton.addScrewForDraw( out );
			}
		}
		return out;
	}
	
	public BossScrew buildBossScrew(){
		BossScrew out = null;
		if (canBuild() && entity != null && skeleton != null){
			out = new BossScrew(name, pos, max, entity, skeleton, world);
			if (skeleton != null){
				skeleton.addScrew(out);
				skeleton.addScrewForDraw( out );
			}
		}
		return out;
	}
	
	public ResurrectScrew buildRezzScrew( ) {
		ResurrectScrew out = null;
		if (canBuild() && entity != null && player != null){
			Vector2 finalPos;
			if (this.playerOffset){
				finalPos = this.pos.add( player.getPositionPixel( ) );
			} else {
				finalPos = this.pos;
			}
			out = new ResurrectScrew( finalPos, this.entity, this.world,
					this.player );
		}
		return out;
	}

}
