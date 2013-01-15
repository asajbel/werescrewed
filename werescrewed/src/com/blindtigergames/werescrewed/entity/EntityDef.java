package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class EntityDef {
	
	//Methods
		@SuppressWarnings("unchecked")
		protected EntityDef(String n, Texture t, String iA, BodyDef bDef, ArrayList<FixtureDef> fixes){
			//Sprite Data
			texture = t;
			initialAnim = iA;
			origin = new Vector2(0,0);
			spriteScale = new Vector2(1,1);
			tint = new Color(1.0f,1.0f,1.0f,1.0f);
			
			//Body Data
			bodyDef = bDef;
			fixtureDefs = (ArrayList<FixtureDef>) fixes.clone();
			
			//Misc Data
			name = n;
		}
		
		@Override
		public void finalize() throws Throwable{
			if (definitions.containsValue(this))
				definitions.remove(this);
			super.finalize();
		}

	//Sprite Fields (i.e. everything needed to define just the sprite half)
		protected Texture texture;
		protected String initialAnim;
		protected Vector2 origin;
		protected Vector2 spriteScale;
		protected Color tint;
	
	//Body Fields (i.e. everything needed to define just the body half)
		protected BodyDef bodyDef;
		protected ArrayList<FixtureDef> fixtureDefs;
	
	//Miscellaneous Fields
		protected String name;
	
	//Static Methods and Fields
	protected static HashMap<String, EntityDef> definitions;
	static {
		definitions = new HashMap<String,EntityDef>();
		//Since the XML loader isn't done yet, create default entity definitions here, and put them in the hashmap.

		//Player
			BodyDef bDef = new BodyDef();
			ArrayList<FixtureDef> fixes = new ArrayList<FixtureDef>();
	        
	        //Add the box forming the upper part of the player's body.
			PolygonShape bodyBox = new PolygonShape();
	        bodyBox.setAsBox(25f,25f);
	        
			FixtureDef fixtureDef = new FixtureDef();  
	        fixtureDef.shape = bodyBox;  
	        fixtureDef.density = 1.0f;  
	        fixtureDef.friction = 0.0f;  
	        fixtureDef.restitution = 1;
	
	        fixes.add(fixtureDef);
	
	        //Add the circle forming the lower part of the player's body.
			CircleShape circle = new CircleShape();		
			circle.setRadius(25f);
			circle.setPosition(new Vector2(0, -25f));
	
	        fixtureDef = new FixtureDef();  
	        fixtureDef.shape = circle;
	        fixtureDef.density = 1.0f;  
	        fixtureDef.friction = 0.0f;  
	        fixtureDef.restitution = 1;
			
	        fixes.add(fixtureDef);
	        
			EntityDef playerDef = new EntityDef("player", new Texture(Gdx.files.internal("data/libgdx.png")), "", bDef, fixes);
			definitions.put("player", playerDef);
    }
	
	public static EntityDef getDefinition(String id){
		if (definitions.containsKey(id)){
			return definitions.get(id); //If we already have a definition, use it.
		} else {
			EntityDef out = EntityDef.loadDefinition(id); //Otherwise, try loading from XML.
			if (out != null)
				definitions.put(id, out); //If we get a new definition, store it for later use.
			return out;
		}
	}
	
	/*
	 * Loads an entity definition from XML.
	 * TODO Fill with XML loading code
	 */
	protected static EntityDef loadDefinition(String id) {
		return null;
	}
}
