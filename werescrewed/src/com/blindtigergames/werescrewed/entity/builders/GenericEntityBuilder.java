package com.blindtigergames.werescrewed.entity.builders;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.mover.IMover;

/**
 * EntityBuilder is meant to simplify creating entities and
 * allow for extension through inheritance and polymorphism. 
 * Will probably be a constant work-in-progress as new Entity 
 * classes are added.
 * 
 * I added this generic version of EntityBuilder to better allow
 * for different types of builders. Now new subclasses of EntityBuilder
 * don't have to redefine its parent's methods; you just have to specify 
 * the new type in the "extends" tag, and the generic will handle the rest for you.
 * @author Kevin
 * 
 */
public class GenericEntityBuilder <B extends GenericEntityBuilder<?>>{
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
	
	public GenericEntityBuilder(){
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
	@SuppressWarnings( "unchecked" )
	public B reset(){
		resetInternal();
		return ( B ) this;
	}
	
	/**
	 * 
	 * @param name - String name of platform, default is "noname"
	 * @return EntityBuilder
	 */
	@SuppressWarnings( "unchecked" )
	public B name(String n){
		name = n;
		return (B)this;
	}

	@SuppressWarnings( "unchecked" )
	public B type(EntityDef def){
		type = def;
		if (type.getCategory().equals( "Player" )){
			return (B)new PlayerBuilder().copy(this);
		}
		return (B)this;

	}
	
	//Used if only a string is passed in.
	public B type(String def){
		return (B)type(EntityDef.getDefinition( def ));
	}
	
	@SuppressWarnings( "unchecked" )
	public B world(World w){
		world = w;
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )
	public B body(Body b){
		body = b;
		world = b.getWorld( );
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )
	public B texture(Texture t){
		tex = t;
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )
	public B position(Vector2 p){
		return (B)positionX(p.x).positionY(p.y);
	}
	
	@SuppressWarnings( "unchecked" )	
	public B position( float x, float y ) {
		return (B)positionX(x).positionY(y);
	}
	
	@SuppressWarnings( "unchecked" )
	public B positionX(float x){
		pos.x = x;
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )
	public B positionY(float y){
		pos.y = y;
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )
	public B rotation(float r){
		rot = r;
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )	
	public B solid(boolean s){
		solid = s;
		return (B)this;
	}
	/**
	 * Loads an entity's special properties from a hashmap.
	 * For generic entities, this does nothing. This is basically a placeholder for subclasses to inherit.
	 */
	@SuppressWarnings( "unchecked" )
	public B properties(HashMap<String,String> props){
		return (B)this;
	}
	
	@SuppressWarnings( "unchecked" )
	public B copy(GenericEntityBuilder<?> that){
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
		return (B)this;	
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
