package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;
import java.util.HashMap;

import aurelienribon.bodyeditor.BodyEditorLoader;
import aurelienribon.bodyeditor.BodyEditorLoader.CircleModel;
import aurelienribon.bodyeditor.BodyEditorLoader.PolygonModel;
import aurelienribon.bodyeditor.BodyEditorLoader.RigidBodyModel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;
import com.blindtigergames.werescrewed.screens.GameScreen;

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
			if (fixes == null){
				fixtureDefs = new ArrayList<FixtureDef>();
			} else {
				fixtureDefs = (ArrayList<FixtureDef>) fixes.clone();
			}
			gravityScale = 1.0f;
			fixedRotation = false;
			
			//Misc Data
			name = n;
		}
		
		@Override
		public void finalize() throws Throwable{
			if (definitions.containsValue(this))
				definitions.remove(this);
			super.finalize();
		}

		protected void loadComplexBody(float density, float friction, float restitution, int scale, String bodyName ){
			String filename = "data/bodies/" + bodyName + ".json";			
			BodyEditorLoader loader = new BodyEditorLoader( Gdx.files.internal(filename) );
			Vector2 origin = new Vector2(0f,0f);
			RigidBodyModel rbModel = loader.getInternalModel().rigidBodies.get(name);
			if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");
			
			PolygonShape polygonShape = new PolygonShape();
			FixtureDef fd;
			CircleShape circleShape = new CircleShape();
			
			for (int i=0, n=rbModel.polygons.size(); i<n; i++) {
				PolygonModel polygon = rbModel.polygons.get(i);
				Vector2[] vertices = polygon.vertices.toArray(new Vector2[0]);

				for (int ii=0, nn=vertices.length; ii<nn; ii++) {
					vertices[ii] = vertices[ii].set(polygon.vertices.get(ii)).mul(scale);
					vertices[ii].sub(origin);
				}

				polygonShape.set(vertices);
				fd = makeFixtureDef(density, friction, restitution, polygonShape);
				fixtureDefs.add(fd);
			}

			for (int i=0, n=rbModel.circles.size(); i<n; i++) {
				CircleModel circle = rbModel.circles.get(i);
				Vector2 center = circle.center.mul(scale);
				float radius = circle.radius * scale;

				circleShape.setPosition(center);
				circleShape.setRadius(radius);
				fd = makeFixtureDef(density, friction, restitution, circleShape);
				fixtureDefs.add(fd);
			}
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
		protected float gravityScale;
		protected boolean fixedRotation;
		protected float defaultDensity;
		protected float defaultFriction;
		protected float defaultRestitution;
	
	//Miscellaneous Fields
		protected String name;
	
	//Static Methods and Fields
	protected static HashMap<String, EntityDef> definitions;
	static {
		definitions = new HashMap<String,EntityDef>();
		//Since the XML loader isn't done yet, create default entity definitions here, and put them in the hashmap.

		//Player
			BodyDef playerBodyDef = new BodyDef();
			playerBodyDef.type = BodyType.DynamicBody;
			playerBodyDef.fixedRotation = true;
			ArrayList<FixtureDef> fixes = new ArrayList<FixtureDef>();
	        	        
			CircleShape playerfeetShape = new CircleShape();
			playerfeetShape.setRadius(10f * GameScreen.PIXEL_TO_BOX);
			FixtureDef playerFixtureDef = makeFixtureDef(9.9f, 0.05f, 0.01f, playerfeetShape);			
	        fixes.add(playerFixtureDef);
	        
			EntityDef playerDef = new EntityDef("player", new Texture(Gdx.files.internal("data/player_r_m.png")), "", playerBodyDef, fixes);
			definitions.put("player", playerDef);
		
		//Bottle
			BodyDef bottleBodyDef = new BodyDef();
			bottleBodyDef.type = BodyType.DynamicBody;
			EntityDef bottleDef = new EntityDef("bottle", null, "", bottleBodyDef, null);
			bottleDef.loadComplexBody(1.0f, 0.5f, 0.0f, 1, "bottle");
			definitions.put("bottle", bottleDef);
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
	
	protected static FixtureDef makeFixtureDef(float density, float friction, float restitution, Shape shape){
		FixtureDef out = new FixtureDef();
		out.density = density;
		out.friction = friction;
		out.restitution = restitution;
		out.shape = shape;
		return out;
	}

}
