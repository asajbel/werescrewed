package com.blindtigergames.werescrewed.entity;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.mover.IMover;

/*
 * Okay, I finally got fed up with the way entites are constructed right now, 
 * so I'm making a builder class like the platform builder.
 * 
 * This will also help once categories are implemented with EntityDef, since
 * we can switch constructors depending on the definition's category.
 * 
*/		

public class EntityBuilder{
	//Common to all builders
	protected String name;
	protected Vector2 pos;
	protected float rot;
	protected Vector2 sca;
	protected IMover mover;
	protected boolean solid;

	//Used for type+world construction
	protected EntityDef type;
	protected World world;
	
	//Used for texture+body construction
	protected Texture tex;
	protected Body body;
	
	public EntityBuilder(){
		resetInternal();
	}
	
	protected void resetInternal(){
		name = "";
		pos = new Vector2(0,0);
		rot = 0.0f;
		sca = new Vector2(1,1);
		solid = true;
		mover = null;
	}
	//Simply resets the builder to initial state ant returns it.
	public EntityBuilder reset(){
		resetInternal();
		return this;
	}
	
	public EntityBuilder name(String n){
		name = n;
		return this;
	}

	public EntityBuilder type(EntityDef def){
		type = def;
		if (type.getCategory().equals( "Player" )){
			return new PlayerBuilder().copy(this);
		}
		return this;

	}
	
	//Used if only a string is passed in.
	public EntityBuilder type(String def){
		return type(EntityDef.getDefinition( def ));
	}
	
	public EntityBuilder world(World w){
		world = w;
		return this;
	}
	
	public EntityBuilder body(Body b){
		body = b;
		world = b.getWorld( );
		return this;
	}
	
	public EntityBuilder texture(Texture t){
		tex = t;
		return this;
	}
	
	public EntityBuilder position(Vector2 p){
		return positionX(p.x).positionY(p.y);
	}
	
	public EntityBuilder positionX(float x){
		pos.x = x;
		return this;
	}
	
	public EntityBuilder positionY(float y){
		pos.y = y;
		return this;
	}
	
	public EntityBuilder rotation(float r){
		rot = r;
		return this;
	}
	
	public EntityBuilder solid(boolean s){
		solid = s;
		return this;
	}
	/**
	 * Loads an entity's special properties from a hashmap.
	 * For generic entities, this does nothing. This is basically a placeholder for subclasses to inherit.
	 */
	public EntityBuilder properties(HashMap<String,String> props){
		return this;
	}
	
	public EntityBuilder copy(EntityBuilder that){
		name = that.name;
		pos = that.pos;
		rot = that.rot;
		sca = that.sca;
		solid = that.solid;
		mover = that.mover;
		type = that.type;
		world = that.world;
		tex = that.tex;
		body = that.body;
		return this;	
	}
	
	protected boolean canBuild(){
		if (world == null) return false;
		if (type == null && body == null) return false;
		return true;
	}
	
	public Entity build(){
		Entity out = null;
		if (canBuild()){
			if (type != null){
				out = new Entity(name, type, world, pos, rot, sca, tex, solid);
			} else {
				out = new Entity(name, pos, tex, body, solid);
			}
			if (mover != null){
				out.setMover( mover );
			}
		}
		return out;
	}
	protected static final String nameTag = "Name";
	protected static final String typeTag = "Definition";
	protected static final String xTag = "X";
	protected static final String yTag = "Y";
	protected static final String aTag = "Angle";	

}
