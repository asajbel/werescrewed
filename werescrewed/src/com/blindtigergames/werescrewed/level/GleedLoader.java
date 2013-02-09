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
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.util.Util;

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
		return new Vector2(posElem.getFloat("X"), posElem.getFloat("Y")*-1.0f);
	}
	
	@SuppressWarnings( "unused" )
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
		Vector2 pos = getPosition(item);
		if (props.containsKey( defTag )){
			String defName = props.get( defTag );
			EntityDef def = EntityDef.getDefinition( defName );
			if (def != null){
				if (def.getCategory( ).equals( tileCat )){ //Insert special cases here.\
					
					float w = (item.getFloat( "Width" ));
					float h = (item.getFloat( "Height" ));
					float tileX = def.getTexture( ).getWidth( )/4.0f;
					float tileY = def.getTexture( ).getWidth( )/4.0f;
					if (tileX > 0)
						w = w / tileX;
					if (tileY > 0)
						h = h / tileY;
					
					TiledPlatform tp = new PlatformBuilder(level.world)
					.name( name )
					.type( def )
					.position( pos.x, pos.y )
					.dimensions( (int)w, (int)h )
					.texture( def.getTexture() )
					.solid( true )
					.buildTilePlatform( );
					Gdx.app.log("GleedLoader", "Platform loaded:"+tp.name);
					level.entities.addEntity( name, tp );
					level.root.addPlatformFixed( tp );
				} else if (def.getCategory( ).equals( complexCat )) {
					ComplexPlatform cp = new PlatformBuilder(level.world)
					.name( name )
					.type( def )
					.position( pos.x, pos.y )
					.texture( def.getTexture() )
					.solid( true )
					.buildComplexPlatform( );
					Gdx.app.log("GleedLoader", "Platform loaded:"+cp.name);
					level.entities.addEntity( name, cp );
					level.root.addPlatformFixed( cp );
				} else if (def.getCategory( ).equals( playerCat )){
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
				Gdx.app.log("GleedLoader", "Position:"+pos.x+","+pos.y);
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
	protected static final String complexCat = "ComplexPlatform";	

}
