package com.blindtigergames.werescrewed.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;

public class GleedLoader {
	public Level loadLevelFromFile(String levelName){
		Level out = new Level();
		return out;
	}
	protected Array<Entity> loadLayer(Element element, World world) {
		Array<Entity> out = new Array<Entity>();
		Gdx.app.log("GleedLoader", "loading layer " + element.getAttribute("Name", ""));
		Array<Element> items = element.getChildByName("Items").getChildrenByName("Item");

		for (Element item: items) {
			Array<Element> properties = item.getChildByName("CustomProperties").getChildrenByName("Property");
			String name = item.getAttribute("Name");
			String type = item.getAttribute("xsi:type");
			String def = "";
			Element posElem = item.getChildByName("Position");
			
			Vector2 pos = new Vector2(posElem.getFloat("X"), posElem.getFloat("Y"));
			float rot = 0.0f;
			Vector2 sca = new Vector2(1,1);
			
			for (Element prop: properties){
				if (prop.getAttribute("Name", "").equalsIgnoreCase( "Definition" )){
					def = prop.get( "string" );
				}
			}
			if (def.equals("")){ //Didn't find a definition.
				Gdx.app.log( "GleedLoader", "Entity "+name+" does not have a definition property." );
				if (type.equals("TextureItem")) {
				}
				else if (type.equals("PathItem")) {
				}
				else if (type.equals("RectangleItem")) {
				}
			} else {
				if (def.equals("insert special cases here")){
					
				} else {
					out.add(new Entity(name, EntityDef.getDefinition(def), world, pos, rot, sca));
				}
			}
		}
		return out;
	}
	private Entity loadEntity(Element element) {
		
		return null;
	}
}
