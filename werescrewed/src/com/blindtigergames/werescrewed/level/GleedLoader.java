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
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TweenMover;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.ScrewType;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class GleedLoader {	
	protected XmlReader reader;
	protected Level level;
	protected Array<Element> elements;
	protected EnumMap<GleedTypeTag, HashMap<String, Item>> items;
	protected HashMap<String,Entity> entities;
	protected HashMap<String,TimelineTweenMover> movers;
	protected HashMap<String,Skeleton> skeletons;
	
	protected static final float GLEED_TO_GDX_X = 1.0f;
	protected static final float GLEED_TO_GDX_Y = -1.0f;
	
	public GleedLoader(){
		reader = new XmlReader();
		level = null;
		items = new EnumMap<GleedTypeTag, HashMap<String, Item>>(GleedTypeTag.class);
		for (GleedTypeTag t : GleedTypeTag.values() ){
			items.put(t, new HashMap<String, Item>());
		}
		entities = new HashMap<String, Entity>();
		movers = new HashMap<String, TimelineTweenMover>();
		skeletons = new HashMap<String, Skeleton>();
	}
	
	public Level load(String filename){
		level = new Level();
		skeletons.put( "root", level.root );
		Element root;
		try {
			root = reader.parse(Gdx.files.internal( filename ));
			Array<Element> layers = root.getChildByName("Layers").getChildrenByName( "Layer" );
			for (Element layer : layers){
				Gdx.app.log("GleedLoader", "loading layer " + layer.getAttribute("Name", ""));
				elements = layer.getChildByName("Items").getChildrenByName( "Item" );
				Gdx.app.log("GleedLoader", "Entities Found:"+elements.size);
				Item item;
				//Sorts items into entities, movers, skeletons, etc.
				for (Element e: elements) {
					item = new Item(e);
					//Make sure we have a valid tag. If not, 
					if (item.tag != null){
						items.get( item.tag ).put(item.name, item );
					} else {
						items.get(GleedTypeTag.ENTITY).put(item.name, item );
					}
				}
			}
		} catch ( IOException e1 ) {
			Gdx.app.log("GleedLoader", "Error: could not load file "+filename, e1);
			e1.printStackTrace();
		}
		//Load skeletons first.
		for (Item i: items.get( GleedTypeTag.SKELETON ).values()){
			loadSkeleton(i);
		}
		//Then movers.
		for (Item i: items.get( GleedTypeTag.MOVER ).values()){
			loadMover(i);
		}
		//And finally, entities.
		for (Item i: items.get( GleedTypeTag.ENTITY ).values()){
			loadEntity(i);
		}
		return level;
	}
	
	
	@SuppressWarnings( "unused" )
	protected Skeleton loadSkeleton(Item item){
		if (skeletons.containsKey( item.name )){
			return skeletons.get( item.name );
		} else {
			item.checkLocked( );
			Skeleton child = new Skeleton(item.name, item.pos, item.tex, level.world);
			Skeleton parent = loadSkeleton(item.skeleton);
			parent.addSkeleton( child );
			skeletons.put( item.name, child );
			return child;
		}
	}
	
	protected Skeleton loadSkeleton(String name){
		if (skeletons.containsKey( name )){
			return skeletons.get( name );
		} else if (items.get( GleedTypeTag.SKELETON ).containsKey(name )){
			return loadSkeleton(items.get( GleedTypeTag.SKELETON ).get( name ));
		}
		return level.root;
	}
	
	protected TimelineTweenMover loadMover (Item item){
		if (movers.containsKey( item.name )){
			return movers.get( item.name );
		} else if (item.gleedType.equals( "PathItem" )){
			Array<Element> pointElems = item.element.getChildByName( "LocalPoints" ).getChildrenByName( "Vector2" );
			Gdx.app.log("GleedLoader", "Loading Path Mover:"+pointElems.size+" points.");
			Array<Vector2> points = new Array<Vector2>(pointElems.size);
			Array<Float> times = new Array<Float>(pointElems.size);
			PathBuilder pBuilder = new PathBuilder().begin( );
			
			Element vElem; Vector2 point; String timeTag;
			int frontPoint = 0; float frontTime = 0.0f;
			//Set first and last point times with separate tags.
			//If tags are not available, assume they will be at 0.0f and 1.0f, respectively.
			//As points are loaded, these values may get overridden; this is fine.
			for (int i = 0; i < pointElems.size; i++){
				times.add( -1.0f );
			}
			if (item.props.containsKey( "StartTime" )){
				times.set(frontPoint, Float.parseFloat( item.props.get( "StartTime") ) );
				frontTime = times.get(0);
			} else {
				times.set(frontPoint, frontTime); //By default, the first point should be at time 0.
			}
			if (item.props.containsKey( "EndTime" )){
				times.set(pointElems.size-1, Float.parseFloat( item.props.get( "EndTime") ) );	
			} else {
				times.set(pointElems.size-1, 1.0f);
			}
			for (int i = 0; i < pointElems.size; i++){
				vElem = pointElems.get( i );
				point = new Vector2(vElem.getFloat( "X" ), vElem.getFloat( "Y" ));
				points.add( point );
				Gdx.app.log( "GleedLoader", "Point "+i+" has coordinates "+point.toString( )+".");
				timeTag = "Point"+i+"Time";
				if (item.props.containsKey( timeTag )){
					float time = Float.parseFloat( item.props.get( timeTag ));
					if (time >= 0.0f){
						times.set( i, time );
					}
				}
				if (times.get( i ) >= 0.0f){
					Gdx.app.log( "GleedLoader", "Point "+i+" has time "+times.get(i)+".");
					if (i > frontPoint+1){
						float div = (times.get( i ) - frontTime)/(float)(i - frontPoint);
						for (int j = i-1; j > frontPoint; j--){
							times.set(j, frontTime + div*(j-frontPoint));
							Gdx.app.log( "GleedLoader", "Backtracking: Setting point "+j+" to time "+times.get(j)+".");
						}
						frontPoint = i;
						frontTime = times.get( i );
					}
				}
			}
			for (int i = 0; i < points.size; i++){
				pBuilder.target( points.get(i).x, points.get(i).y, times.get(i).floatValue( ) );
			}
			TimelineTweenMover out = pBuilder.build( );
			movers.put(item.name, out);
			return out;
		}
		RuntimeException notPath = new RuntimeException(item.name+" is defined as a mover but is not a path object. Only paths can be defined as movers.");
		Gdx.app.log("GleedLoader", "", notPath);
		throw notPath;
	}
	
	protected TimelineTweenMover loadMover(String name){
		if (movers.containsKey( name )){
			return movers.get(name);
		} else if (items.get( GleedTypeTag.MOVER ).containsKey( name )){
			return loadMover(items.get( GleedTypeTag.MOVER ).get( name ));
		}
		return null;
	}
	
	protected void loadEntity(Item item) {
		if (item.props.containsKey( defTag )){
			String defName = item.props.get( defTag );
			if (ScrewType.fromString( defName ) != null){
				ScrewType sType = ScrewType.fromString( defName );
				switch (sType){
				case SCREW_STRIPPED:
					StrippedScrew s = new ScrewBuilder()
					.name( item.name )
					.position( item.pos )
					.world( level.world )
					.entity( level.root )
					.screwType( ScrewType.fromString( defName ))
					.properties( item.props )
					.buildStrippedScrew();
					Gdx.app.log("GleedLoader", "Building stripped screw "+ item.name + " at " + item.pos.toString( ));
					level.root.addScrewForDraw( s );
					break;
				case SCREW_STRUCTURAL:
					break;
				case SCREW_PUZZLE:
					break;
				case SCREW_BOSS:
					break;
				default:
					break;
				}
			} else {
				EntityDef def = EntityDef.getDefinition( defName );
				if (def != null){
					if (def.getCategory( ).equals( tileCat )){ //Insert special cases here.\
						//Align the platform's origin with the coordinates from Gleed2D.
						item.pos.x += item.sca.x/2.0f;
						item.pos.y -= item.sca.y/2.0f;
						
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
					//	tp.setPixelPosition(item.pos);
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
					//	cp.setPixelPosition(item.pos);
	
						Gdx.app.log("GleedLoader", "Platform loaded:"+cp.name);
						level.entities.addEntity( item.name, cp );
						if (item.props.containsKey( "Dynamic" )){
							level.root.addDynamicPlatform( cp );
						} else {
							level.root.addKinematicPlatform( cp );
						}
					} else if (def.getCategory( ).equals( playerCat )){
						//level.player.setPixelPosition( item.pos );
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
						//e.setPixelPosition(item.pos);
						level.entities.addEntity( item.name, e );
					}
					Gdx.app.log("GleedLoader", "Position:"+item.pos.x+","+item.pos.y);
				} else {
					Gdx.app.log("GleedLoader", "Warning: "+item.name+"'s listed definition, '"+defName+"' is not a known EntityDef.");
				}
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
	
	protected Skeleton getSkeleton(Item item){
		if (item.props.containsKey( "Skeleton" )){
			String skeletonName = item.props.get( "Skeleton" );
			if (!skeletonName.equals( "root" )){
				return loadSkeleton(skeletonName);
			}
		}
		return level.root;
	}
	
	protected class Item {
		public Item(Element e){
			element = e;
			name = getName(e);
			gleedType = getGleedType(e);
			props = getCustomProperties(e);
			def = props.get( "Definition" );
			if (props.containsKey( "Skeleton" ))
				skeleton = props.get( "Skeleton" );
			else skeleton = "root";
			tag = GleedTypeTag.fromString( props.get( GleedTypeTag.tag ) );
			pos = getPosition(e);
			sca = getScale(e);
			tex = getTexture(e);
			locked = false;
		}
		public Element element;
		String name, gleedType, def;
		public GleedTypeTag tag;
		public HashMap<String,String> props;
		public Vector2 pos;
		public Vector2 origin;
		public Vector2 sca;
		public Texture tex;
		public String skeleton;
		public boolean locked;
		public void checkLocked(){
			if (locked){
				RuntimeException oops = new RuntimeException("Cyclic Reference");
				Gdx.app.log("GleedLoader", "While loading:"+name, oops);
				throw oops;
			}
			locked = true;
		}
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
			} else if (getGleedType(item).equals( "PathItem" )){
				float left = 0.0f, right = 0.0f, top = 0.0f, bottom = 0.0f;
				//Fill this out later
				out.x = left-right;
				out.y = top-bottom;
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
