package com.blindtigergames.werescrewed.level;

import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screens.GameScreen;
import com.blindtigergames.werescrewed.screens.Screen;

public class GleedLoader {
	
	protected static XmlReader reader;
	protected static Texture tileTex;
	static{
		reader = new XmlReader();
		tileTex = new Texture( Gdx.files.internal( "data/rletter.png" ) );
	}

	public static Level loadLevelFromFile(String filename){
		Level out = new Level();
		Element root;
		try {
			root = reader.parse(Gdx.files.internal( filename ));
			Element rootLayer = root.getChildByNameRecursive( "Layer" );
			Array<Entity> entities = loadLayer(rootLayer, out.world);
			for (Entity e : entities){
				if (e != null)
					out.entities.addEntity( e.name, e );
			}
		} catch ( IOException e1 ) {
			Gdx.app.log("GleedLoader", "Error: could not load file "+filename, e1);
			e1.printStackTrace();
		}
		return out;
	}
	
	protected static Array<Entity> loadLayer(Element element, World world) {
		Array<Entity> out = new Array<Entity>();
		Gdx.app.log("GleedLoader", "loading layer " + element.getAttribute("Name", ""));
		Array<Element> items = element.getChildByName("Items").getChildrenByName("Item");
		Gdx.app.log("GleedLoader", "Entities Found:"+items.size);
		Entity e; String def;
		for (Element item: items) {
			def = getDefinition(item);
			if (def != null){
				if (def.equals( playerTag )){
					
				} else {
					e = loadEntity(item,world);
					if (e != null)
						out.add(e);
				}
			}
		}
		return out;
	}
	
	protected static Entity loadEntity(Element item, World world) {
			HashMap<String,String> props = getCustomProperties(item);
			String name = item.getAttribute("Name");
			Element posElem = item.getChildByName("Position");
			Vector2 pos = new Vector2(posElem.getFloat("X"), posElem.getFloat("Y")).mul( Screen.PIXEL_TO_BOX );
			//Vector2 pos = new Vector2(0.0f, posElem.getFloat("Y")).mul( Screen.PIXEL_TO_BOX );
			float rot = 0.0f;
			Vector2 sca = new Vector2(1,1);
			
			if (props.containsKey( defTag )){
				String def = props.get( defTag );
				if (def.equals("tiledPlatform")){ //Insert special cases here.\
					int w = Integer.decode(props.get("TileWidth"));
					int h = Integer.decode(props.get("TileHeight"));
					
					TiledPlatform tp = new PlatformBuilder(world)
					.setPosition( pos.x, pos.y )
					.setDimensions( w, h )
					.setTexture( tileTex )
					.setResitituion( 0.0f )
					.buildTilePlatform( );
					Gdx.app.log("GleedLoader", "Platform loaded:"+tp.name);
					return tp;
				} else {
					Entity e = new Entity(name, EntityDef.getDefinition( def ), world, pos, rot, sca, null, true); 
					Gdx.app.log("GleedLoader", "Entity loaded:"+e.name);
					return e;
				}
			}
		return null;
	}
	
	protected static String getDefinition(Element item){
		HashMap<String,String> props = getCustomProperties(item);
		return props.get( defTag );
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
	protected static final String defTag = "Definition";
	protected static final String playerTag = "playerTest";
	protected static final String tileTag = "tiledPlatform";
	
}
