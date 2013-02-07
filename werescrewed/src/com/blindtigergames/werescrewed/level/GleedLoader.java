package com.blindtigergames.werescrewed.level;

import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.builders.EntityBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screens.Screen;

public class GleedLoader {	
	protected XmlReader reader;
	protected Level level;
	
	public GleedLoader(){
		reader = new XmlReader();
		level = null;
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
		Array<Element> items = element.getChildByName("Items").getChildrenByName("Item");
		Gdx.app.log("GleedLoader", "Entities Found:"+items.size);
		for (Element item: items) {
			loadElement(item);
		}
	}
	
	protected void loadElement(Element item){
		HashMap<String,String> props = getCustomProperties(item);
		if (props.containsKey(GleedTypeTags.tag)){
			GleedTypeTags tag = GleedTypeTags.fromString( props.get( GleedTypeTags.tag ) );
			if (tag.equals(GleedTypeTags.MOVER)){
				loadMover(item, props);
				return;
			}
		}
		loadEntity(item, props);
	}
	
	protected static String getName(Element item){
		return item.getAttribute("Name");
	}

	protected static Vector2 getPosition(Element item){
		Element posElem = item.getChildByName("Position");
		return new Vector2(posElem.getFloat("X"), posElem.getFloat("Y")).mul( -1 );
	}
	
	protected void loadMover(Element item, HashMap<String,String> props){
		String name = getName(item);
		Vector2 pos = getPosition(item);
		/*
		 * 1. Check to make sure we're loading a path
		 * 2. See if it loops
		 * 3. Look for a velocity value
		 */
	}
	
	protected void loadEntity(Element item, HashMap<String,String> props) {
		String name = item.getAttribute("Name");
		Element posElem = item.getChildByName("Position");
		Vector2 pos = new Vector2(posElem.getFloat("X"), posElem.getFloat("Y")).mul( Screen.PIXEL_TO_BOX );
		if (props.containsKey( defTag )){
			String defName = props.get( defTag );
			EntityDef def = EntityDef.getDefinition( defName );
			if (def != null){
				if (def.getCategory( ).equals( tileCat )){ //Insert special cases here.\
					
					int w = Integer.decode(props.get("TileWidth"));
					int h = Integer.decode(props.get("TileHeight"));
					
					TiledPlatform tp = new PlatformBuilder(level.world)
					.name( name )
					.type( def )
					.position( pos.x, pos.y )
					.dimensions( w, h )
					.texture( def.getTexture() )
					.resitituion( 0.0f )
					.buildTilePlatform( );
					Gdx.app.log("GleedLoader", "Platform loaded:"+tp.name);
					level.entities.addEntity( name, tp );
					level.root.addPlatformFixed( tp );
				} else {
					if (def.getCategory( ).equals( playerCat )){
						level.player.setPosition( pos );
					} else {
						Entity e = new EntityBuilder()
								.type(def)
								.name(name)
								.world(level.world)
								.position(pos)
								.properties(props)
								.build();
						Gdx.app.log("GleedLoader", "Entity loaded:"+name);
						level.entities.addEntity( name, e );
					}
				}
			} else {
				Gdx.app.log("GleedLoader", "Warning: "+name+"'s listed definition, '"+defName+"' is not a known EntityDef.");
			}
		} else {
			Gdx.app.log("GleedLoader", "Warning: "+name+" does not have a valid '"+defTag+"' tag.");
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

	protected static final String typeTag = "Type";
	protected static final String defTag = "Definition";
	protected static final String playerCat = "Player";
	protected static final String tileCat = "TiledPlatform";	
}
