package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.rope.Rope;

public class RopeBuilder extends GenericEntityBuilder<RopeBuilder> {
	protected float linkWidth;
	protected float linkHeight;
	protected int links;
	
	public RopeBuilder ( World world ){
		super();
		reset();
		super.world( world );
	}
	
	public RopeBuilder attachTo( Entity entity ){
		this.pos.x = entity.getPosition( ).x;
		this.pos.y = entity.getPosition( ).y;
		return this;
	}
	
	public RopeBuilder width ( float width ){
		this.linkWidth = width;
		return this;
	}
	
	public RopeBuilder height ( float height ){
		this.linkHeight = height;
		return this;
	}
	
	public RopeBuilder links ( int links ){
		this.links = links;
		return this;
	}
	
	public Rope buildRope() {
		Rope rope = new Rope( this.name,
							  this.pos,
							  new Vector2 ( linkWidth, linkHeight),
							  this.links,
							  this.tex,
							  this.world );
		return rope;
	}
	
	
	public RopeBuilder reset(){
		super.resetInternal( );
		this.linkWidth = 16.0f;
		this.linkHeight = 64.0f;
		this.links = 5;
		return this;
	}
	
}
