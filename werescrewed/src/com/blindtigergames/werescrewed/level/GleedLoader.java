package com.blindtigergames.werescrewed.level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.builders.EntityBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class GleedLoader {	
	protected XmlReader reader;
	protected Level level;
	protected HashMap<String,Element> elements;
	protected EnumMap<GleedTypeTag, ArrayList<Item>> items;
	protected static final float GLEED_TO_GDX_X = 1.0f;
	protected static final float GLEED_TO_GDX_Y = -1.0f;
	
	public GleedLoader(){
		reader = new XmlReader();
		level = null;
		items = new EnumMap<GleedTypeTag, ArrayList<Item>>(GleedTypeTag.class);
		for (GleedTypeTag t : GleedTypeTag.values() ){
			items.put(t, new ArrayList<Item>());
		}
	}
	
	public Level load(String filename){
		level = new Level();
		Element root;
		try {
			root = reader.parse(Gdx.files.internal( filename ));
			Element rootLayer = root.getChildByNameRecursive( "Layer" );
			loadLayer(rootLayer);
		} catch ( IOException e1 ) {
			Gdx.app.log("GleedLoader", "Error: could not load file "+filename, e1);
			e1.printStackTrace();
		}
		return level;
	}
	
	protected void loadLayer(Element element) {
		Gdx.app.log("GleedLoader", "loading layer " + element.getAttribute("Name", ""));
		elements = getChildrenByNameHash(element.getChildByName("Items"), "Item", "Name");
		Gdx.app.log("GleedLoader", "Entities Found:"+elements.values().size());
		Item item;
		//Sorts items into entities, movers, skeletons, etc.
		for (Element e: elements.values()) {
			item = new Item(e);
			//Make sure we have a valid tag. If not, 
			if (item.tag != null){
				items.get( item.tag ).add( item );
			} else {
				items.get(GleedTypeTag.ENTITY).add( item );
			}
		}
		
		//Load skeletons first.
		for (Item i: items.get(GleedTypeTag.SKELETON)){
			loadSkeleton(i);
		}
		//Then movers.
		for (Item i: items.get(GleedTypeTag.MOVER)){
			loadMover(i);
		}
		//And finally, entities.
		for (Item i: items.get(GleedTypeTag.ENTITY)){
			loadEntity(i);
		}		
	}
	
	@SuppressWarnings( "unused" )
	protected void loadSkeleton(Item item){
		Skeleton out = new Skeleton(item.name, item.pos, item.tex, level.world);
	}
	
	@SuppressWarnings( "unused" )
	protected void loadMover(Item item){
		if (item.gleedType.equals( "PathItem" )){
			ArrayList<Vector2> points;
			
		}
		/*
		 * 1. Check to make sure we're loading a path
		 * 2. See if it loops
		 * 3. Look for a velocity value
		 */
	}
	
	protected void loadEntity(Item item) {
		if (item.props.containsKey( defTag )){
			String defName = item.props.get( defTag );
			EntityDef def = EntityDef.getDefinition( defName );
			if (def != null){
				if (def.getCategory( ).equals( tileCat )){ //Insert special cases here.\
					float tileX = def.getTexture( ).getWidth( )/4.0f;
					float tileY = def.getTexture( ).getWidth( )/4.0f;
					if (tileX > 0 && tileY > 0)
						item.sca = item.sca.div( tileX, tileY );
					TiledPlatform tp = new PlatformBuilder(level.world)
					.name( item.name )
					.type( def )
					.position( item.pos.x, item.pos.y )
					.dimensions( item.sca.x, item.sca.y )
					.texture( def.getTexture() )
					.solid( true )
					.buildTilePlatform( );
					Gdx.app.log("GleedLoader", "Platform loaded:"+tp.name);
					tp.setPixelPosition(item.pos);
					level.entities.addEntity( item.name, tp );
					if (item.props.containsKey( "Dynamic" )){
						level.root.addDynamicPlatform( tp );
					} else {
						level.root.addKinematicPlatform( tp );
					}
				} else if (def.getCategory( ).equals( complexCat )) {
					Platform cp = new PlatformBuilder(level.world)
					.name( item.name )
					.type( def )
					.position( item.pos.x, item.pos.y )
					.texture( def.getTexture() )
					.solid( true )
					.buildComplexPlatform( );
					cp.setPixelPosition(item.pos);

					Gdx.app.log("GleedLoader", "Platform loaded:"+cp.name);
					level.entities.addEntity( item.name, cp );
					if (item.props.containsKey( "Dynamic" )){
						level.root.addDynamicPlatform( cp );
					} else {
						level.root.addKinematicPlatform( cp );
					}
				} else if (def.getCategory( ).equals( playerCat )){
					level.player.setPixelPosition( item.pos );
					Gdx.app.log("GleedLoader", "Player Spawnpoint:"+item.pos.toString( ));
				} else {
					Entity e = new EntityBuilder()
							.type(def)
							.name(item.name)
							.world(level.world)
							.position(item.pos)
							.properties(item.props)
							.build();
					Gdx.app.log("GleedLoader", "Entity loaded:"+item.name);
					e.setPixelPosition(item.pos);
					level.entities.addEntity( item.name, e );
				}
				Gdx.app.log("GleedLoader", "Position:"+item.pos.x+","+item.pos.y);
			} else {
				Gdx.app.log("GleedLoader", "Warning: "+item.name+"'s listed definition, '"+defName+"' is not a known EntityDef.");
			}
		} else {
			Gdx.app.log("GleedLoader", "Warning: "+item.name+" does not have a valid '"+defTag+"' tag.");
		}
	}
	
	public Level getLevel(){return level;}
	
	protected static EntityDef getDefinition(Element item){
		HashMap<String,String> props = getCustomProperties(item);
		return EntityDef.getDefinition(props.get( defTag ));
	}
	
	protected static HashMap<String,String> getCustomProperties(Element e){
		HashMap<String,String> out = new HashMap<String,String>();
		Array<Element> properties = e.getChildByName("CustomProperties").getChildrenByName("Property");
		String name; String value;
		for (Element prop: properties){
			name = prop.getAttribute("Name");
			value = prop.get("string");
			out.put(name,value);
		}
		return out;
	}
	
	protected static HashMap<String, Element> getChildrenByNameHash(Element e, String tag, String nameTag){
		HashMap<String,Element> out = new HashMap<String,Element>();
		Array<Element> properties = e.getChildrenByName(tag);
		String name;
		for (Element prop: properties){
			name = prop.getAttribute(nameTag);
			out.put(name,prop);
		}
		return out;
	}
	
	protected class Item {
		public Item(Element e){
			element = e;
			name = getName(e);
			gleedType = getGleedType(e);
			props = getCustomProperties(e);
			tag = GleedTypeTag.fromString( props.get( GleedTypeTag.tag ) );
			pos = getPosition(e);
			sca = getScale(e);
			tex = getTexture(e);
		}
		public Element element;
		String name, gleedType;
		public GleedTypeTag tag;
		public HashMap<String,String> props;
		public Vector2 pos;
		public Vector2 origin;
		public Vector2 sca;
		public Texture tex;
	}

	protected static String getName(Element item){
		return item.getAttribute("Name");
	}

	protected static Vector2 getPosition(Element item){
		Element posElem = item.getChildByName("Position");
		return new Vector2(posElem.getFloat("X")*GLEED_TO_GDX_X, posElem.getFloat("Y")*GLEED_TO_GDX_Y);
	}

	protected static String getGleedType(Element item){
		return item.get( "xsi:type" );
	}
	
	protected static Vector2 getScale(Element item){
		Vector2 out = new Vector2(1.0f,1.0f);
		try{
			if (getGleedType(item).equals( "CircleItem" )){
				out.x = out.y = item.getFloat( "Radius" )*2.0f;
			} else {
				out.x = item.getFloat( "Width" );
				out.y = item.getFloat( "Height" );
			}
		} finally {
		}
		return out;
	}
	
	protected static Texture getTexture(Element item){
		return null;
	}	
	
	protected static Vector2 getGleedOrigin(Element item){
		Vector2 out = new Vector2(0.0f,0.0f);
		return out;
	}
	
	protected static final String typeTag = "Type";
	protected static final String defTag = "Definition";
	protected static final String playerCat = "Player";
	protected static final String tileCat = "TiledPlatform";	
	protected static final String complexCat = "ComplexPlatform";	

}
