package com.blindtigergames.werescrewed.level;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityCategory;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.EntityBuilder;
import com.blindtigergames.werescrewed.entity.builders.MoverBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.MoverType;
import com.blindtigergames.werescrewed.entity.mover.Path;
import com.blindtigergames.werescrewed.entity.mover.PathPoint;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.util.ArrayHash;

public class GleedLoader {
	protected XmlReader reader;
	protected Level level;
	protected EnumMap< GleedTypeTag, HashMap< String, Item >> items;
	protected HashMap< String, Entity > entities;
	protected HashMap< String, TimelineTweenMover > movers;
	protected HashMap< String, Path > paths;
	protected HashMap< String, Skeleton > skeletons;
	protected HashMap< String, PuzzleScrew > puzzleScrews;
	protected int spawnPoints;

	protected static final float GLEED_TO_GDX_X = 1.0f;
	protected static final float GLEED_TO_GDX_Y = -1.0f;

	public GleedLoader( ) {
		reader = new XmlReader( );
		items = new EnumMap< GleedTypeTag, HashMap< String, Item >>(
				GleedTypeTag.class );
		for ( GleedTypeTag t : GleedTypeTag.values( ) ) {
			items.put( t, new HashMap< String, Item >( ) );
		}
		entities = new HashMap< String, Entity >( );
		movers = new HashMap< String, TimelineTweenMover >( );
		paths = new HashMap< String, Path >( );
		skeletons = new HashMap< String, Skeleton >( );
		puzzleScrews = new HashMap< String, PuzzleScrew >( );
		level = new Level( );
		spawnPoints = 0;

	}

	public Level load( String filename ) {
		skeletons.put( "root", level.root );
		Element root;
		Array< Element > elements = new Array< Element >( );

		try {
			root = reader.parse( Gdx.files.internal( filename ) );
			Array< Element > layers = root.getChildByName( "Layers" )
					.getChildrenByName( "Layer" );
			for ( Element layer : layers ) {
				// Gdx.app.log("GleedLoader", "loading layer " +
				// layer.getAttribute("Name", ""));
				elements = layer.getChildByName( "Items" ).getChildrenByName(
						"Item" );
				// Gdx.app.log("GleedLoader", "Entities Found:"+elements.size);
				Item item;
				// Sorts items into entities, movers, skeletons, etc.

				// Currently (3-5-2013) none of these tags are in the xml
				// all are just considered as GleedTypeTag.ENTITY
				for ( Element e : elements ) {
					item = new Item( e );
					// Make sure we have a valid tag. If not,
					if ( item.gleedTag != null ) {
						items.get( item.gleedTag ).put( item.name, item );
					} else {
						items.get( GleedTypeTag.ENTITY ).put( item.name, item );
					}
				}
			}
		} catch ( IOException e1 ) {
			// Gdx.app.log("GleedLoader",
			// "Error: could not load file "+filename, e1);
			e1.printStackTrace( );
		}
		// Load skeletons first.
		for ( Item i : items.get( GleedTypeTag.SKELETON ).values( ) ) {
			loadSkeleton( i );
		}
		// Then entities
		for ( Item i : items.get( GleedTypeTag.ENTITY ).values( ) ) {
			loadEntity( i );
		}
		return level;
	}

	protected Skeleton loadSkeleton( Item item ) {
		if ( skeletons.containsKey( item.name ) ) {
			return skeletons.get( item.name );
		} else {
			item.checkLocked( );
			Skeleton child = new Skeleton( item.name, item.pos, item.tex,
					level.world );
			Skeleton parent = loadSkeleton( item.skeleton );
			parent.addSkeleton( child );
			skeletons.put( item.name, child );
			return child;
		}
	}

	protected Skeleton loadSkeleton( String name ) {
		if ( skeletons.containsKey( name ) ) {
			return skeletons.get( name );
		} else if ( items.get( GleedTypeTag.SKELETON ).containsKey( name ) ) {
			return loadSkeleton( items.get( GleedTypeTag.SKELETON ).get( name ) );
		}
		return level.root;
	}

	public static final String startTime = "starttime";
	public static final String endTime = "endtime";

	protected TimelineTweenMover loadMover( Item item, Entity entity ) {
		item.checkLocked( );
		if ( movers.containsKey( item.name ) ) {
			return movers.get( item.name );
		} else if ( item.gleedType.equals( "PathItem" ) ) {
			Array< Element > pointElems = item.element.getChildByName(
					"LocalPoints" ).getChildrenByName( "Vector2" );
			// Gdx.app.log("GleedLoader",
			// "Loading Path Mover:"+pointElems.size+" points.");
			Array< Vector2 > points = new Array< Vector2 >( pointElems.size );
			Array< Float > times = new Array< Float >( pointElems.size );
			PathBuilder pBuilder = new PathBuilder( )
					.begin( ( Platform ) entity );
			Path path = new Path( );

			Element vElem;
			Vector2 point;
			String timeTag;
			int frontPoint = 0;
			float frontTime = 0.0f;
			// Set first and last point times with separate tags.
			// If tags are not available, assume they will be at 0.0f and 1.0f,
			// respectively.
			// As points are loaded, these values may get overridden; this is
			// fine.
			for ( int i = 0; i < pointElems.size; i++ ) {
				times.add( -1.0f );
			}
			if ( item.props.containsKey( startTime ) ) {
				times.set( frontPoint,
						Float.parseFloat( item.props.get( startTime ) ) );
				frontTime = times.get( 0 );
			} else {
				times.set( frontPoint, frontTime ); // By default, the first
													// point should be at time
													// 0.
			}
			if ( item.props.containsKey( "EndTime" ) ) {
				times.set( pointElems.size - 1,
						Float.parseFloat( item.props.get( endTime ) ) );
			} else {
				times.set( pointElems.size - 1, 1.0f );
			}
			for ( int i = 0; i < pointElems.size; i++ ) {
				vElem = pointElems.get( i );
				point = new Vector2( vElem.getFloat( "X" ) * GLEED_TO_GDX_X,
						vElem.getFloat( "Y" ) * GLEED_TO_GDX_Y );
				points.add( point );
				// Gdx.app.log( "GleedLoader",
				// "Point "+i+" has coordinates "+point.toString( )+".");
				timeTag = "point" + i + "time";
				if ( item.props.containsKey( timeTag ) ) {
					float time = Float.parseFloat( item.props.get( timeTag ) );
					if ( time >= 0.0f ) {
						times.set( i, time );
					}
				}
				if ( times.get( i ) >= 0.0f ) {
					// Gdx.app.log( "GleedLoader",
					// "Point "+i+" has time "+times.get(i)+".");
					if ( i > frontPoint + 1 ) {
						float div = ( times.get( i ) - frontTime )
								/ ( float ) ( i - frontPoint );
						for ( int j = i - 1; j > frontPoint; j-- ) {
							times.set( j, frontTime + div * ( j - frontPoint ) );
							// Gdx.app.log( "GleedLoader",
							// "Backtracking: Setting point "+j+" to time "+times.get(j)+".");
						}
						frontPoint = i;
						frontTime = times.get( i );
					}
				}
			}
			for ( int i = 0; i < points.size; i++ ) {
				pBuilder.target( points.get( i ).x, points.get( i ).y, times
						.get( i ).floatValue( ) );
				path.addPoint( new PathPoint( ).position( points.get( i ) )
						.time( times.get( i ) ) );
			}
			TimelineTweenMover out = pBuilder.build( );
			movers.put( item.name, out );
			paths.put( item.name, path );
			return out;
		}
		RuntimeException notPath = new RuntimeException(
				item.name
						+ " is defined as a mover but is not a path object. Only paths can be defined as movers." );
		// Gdx.app.log("GleedLoader", "", notPath);
		throw notPath;
	}

	protected TimelineTweenMover loadMover( String name, Entity entity ) {
		if ( movers.containsKey( name ) ) {
			return movers.get( name );
		} else if ( items.get( GleedTypeTag.MOVER ).containsKey( name ) ) {
			return loadMover( items.get( GleedTypeTag.MOVER ).get( name ),
					entity );
		}
		return null;
	}

	protected Entity loadEntity( String name ) {
		if ( entities.containsKey( name ) ) {
			return entities.get( name );
		} else if ( items.get( GleedTypeTag.ENTITY ).containsKey( name ) ) {
			return loadEntity( items.get( GleedTypeTag.ENTITY ).get( name ) );
		}
		return null;
	}

	/**
	 * This funtion loads all sorts of objects after reading in the Item item
	 * (defined by the xml file) under GleedTypeTag.ENTITY
	 * 
	 * @param item
	 *            - Item
	 * @return Entity
	 */
	protected static final String puzzleTag = "puzzle";

	protected Entity loadEntity( Item item ) {
		Entity out = null;
		boolean isPlatform = false;
		if ( item.hasDefTag( ) ) {
			// First check if the item's definition is a type of screw
			if ( ScrewType.fromString( item.defName ) != null ) {
				loadScrew( item );
			} else {

				/**
				 * check if it has a name, then get its EntityDef (if not
				 * loaded, it loads the xml) then check if its category is a
				 * tiled/complex plat
				 */

				if ( item.isDefined( ) ) {
					if ( item.getDefinition( ).getCategory( ) == EntityCategory.TILED_PLATFORM ) {
						out = loadTiledPlatform( item );
						isPlatform = true;
					} else if ( item.getDefinition( ).getCategory( ) == EntityCategory.COMPLEX_PLATFORM ) {
						out = loadComplexPlatform( item );
						isPlatform = true;
					} else if ( item.getDefinition( ).getCategory( ) == EntityCategory.PLAYER ) {
						// loadPlayerSpawnPoint(item);
					} else {
						out = loadGeneralEntity( item );
					}
				}
			}
		}
		if ( out != null ) {
			entities.put( item.name, out );

			// Load movers
			String moverName;
			IMover mover;
			for ( RobotState state : RobotState.values( ) ) {
				String tag = "mover" + state.toString( ).toLowerCase( );
				if ( item.props.containsKey( tag ) ) {
					moverName = item.props.get( tag );
					mover = null;
					if ( MoverType.fromString( moverName ) != null ) {
						mover = new MoverBuilder(level.world ).fromString( moverName )
								.build( );
					} else if ( isPlatform ) {
						mover = loadMover( moverName, out );
					}
					if ( mover != null ) {
						// Gdx.app.log( "GleedLoader",
						// "Attaching mover ["+moverName+"] to "+item.name+"."
						// );
						out.addMover( mover, state );
						out.setCurrentMover( RobotState.IDLE );
						out.setActive( true );
					}
				}
			}
			// Attach to puzzle manager
			if ( item.props.containsKey( puzzleTag ) ) {
				for ( String puzzleString : item.props.getAll( puzzleTag ) ) {
					Array< String > tokens = new Array< String >(
							puzzleString.split( "\\s+" ) );
					String puzzleName = "";
					moverName = "";
					String token;
					for ( int t = 0; t < tokens.size; t++ ) {
						token = tokens.get( t );
						if ( t == 0 ) {
							puzzleName = token;
						} else {
							moverName = moverName.concat( token );
						}
					}
					if ( !puzzleName.equals( item.name ) ) {
						PuzzleScrew puzzle = loadPuzzle( puzzleName );
						if ( puzzle != null ) {
							mover = null;
							if ( MoverType.fromString( moverName ) != null ) {
								mover = new MoverBuilder(level.world ).fromString(
										moverName ).build( );
							} else if ( isPlatform ) {
								mover = loadMover( moverName, out );
							}
							if ( mover != null ) {
								// Gdx.app.log( "GleedLoader",
								// "Attaching "+item.name+" to puzzle screw "+puzzle.name+" with mover string ["+moverName+"]"
								// );
								puzzle.puzzleManager.addEntity( out );
								puzzle.puzzleManager.addMover( mover );
							}
						}
					}
				}
			}
		}
		return out;
	}

	protected static final String dynamicTag = "dynamic";

	protected TiledPlatform loadTiledPlatform( Item item ) {
		TiledPlatform out = null;
		// Align the platform's origin with the coordinates from Gleed2D.
		Vector2 pos = new Vector2( );
		Vector2 sca = new Vector2( );
		pos.x = item.pos.x + ( item.sca.x / 2.0f * GLEED_TO_GDX_X );
		pos.y = item.pos.y + ( item.sca.y / 2.0f * GLEED_TO_GDX_Y );

		boolean isDynamic = false;
		if ( item.props.containsKey( "dynamic" ) )
			isDynamic = true;

		// Its as simple as this to get custom values
		// either it will give me the right string or it if doesn't exist: null
		// System.out.println( "custom props: " + item.props.get( "definition" )
		// );

		float tileX = item.getDefinition( ).getTexture( ).getWidth( ) / 4.0f;
		float tileY = item.getDefinition( ).getTexture( ).getWidth( ) / 4.0f;
		if ( tileX > 0 && tileY > 0 )
			sca = item.sca.div( tileX, tileY );

		out = new PlatformBuilder( level.world ).name( item.name )
				.type( item.getDefinition( ) ).position( pos ).dimensions( sca )
				.texture( item.getDefinition( ).getTexture( ) ).solid( true )
				.dynamic( isDynamic ).buildTilePlatform( );

		Skeleton parent = loadSkeleton( item.skeleton );
		if ( isDynamic ) {
			// Gdx.app.log("GleedLoader",
			// "Tiled Dynamic platform loaded:"+out.name);
			parent.addDynamicPlatform( out );
		} else {
			// Gdx.app.log("GleedLoader",
			// "Tiled Kinematic platform loaded:"+out.name);
			parent.addKinematicPlatform( out );
		}
		return out;
	}

	protected Platform loadComplexPlatform( Item item ) {
		Platform out = new PlatformBuilder( level.world ).name( item.name )
				.type( item.getDefinition( ) )
				.position( item.pos.x, item.pos.y )
				.texture( item.getDefinition( ).getTexture( ) ).solid( true )
				.buildComplexPlatform( );

		// Gdx.app.log("GleedLoader", "Complex Platform loaded:"+item.name);
		Skeleton parent = loadSkeleton( item.skeleton );
		if ( item.props.containsKey( "dynamic" ) ) {
			parent.addDynamicPlatform( out );
		} else {
			parent.addKinematicPlatform( out );
		}
		return out;
	}

	// protected void loadPlayerSpawnPoint(Item item){
	// level.players.get(spawnPoints).setPixelPosition( item.pos ); //Kevin: Who
	// commented this out?
	// Gdx.app.log("GleedLoader", "Player Spawnpoint:"+item.pos.toString( ));
	// }

	protected Entity loadGeneralEntity( Item item ) {
		Entity out = new EntityBuilder( ).type( item.getDefinition( ) )
				.name( item.name ).world( level.world ).position( item.pos )
				.properties( item.props ).build( );
		// Gdx.app.log("GleedLoader", "General Entity loaded:"+item.name);
		return out;
	}

	protected static final String screwTargetTag = "target";

	public Screw loadScrew( Item item ) {
		;
		ScrewType sType = ScrewType.fromString( item.defName );
		ScrewBuilder builder = new ScrewBuilder( ).name( item.name )
				.position( item.pos ).world( level.world ).screwType( sType )
				.properties( item.props );
		Skeleton parent = loadSkeleton( item.skeleton );
		builder.skeleton( parent );
		if ( item.props.containsKey( screwTargetTag ) ) {
			builder.entity( loadEntity( item.props.get( screwTargetTag ) ) );
		} else {
			builder.entity( parent );
		}
		Screw out = null;
		switch ( sType ) {
		case SCREW_PUZZLE:
			// Gdx.app.log("GleedLoader", "Building puzzle screw "+ item.name +
			// " at " + item.pos.toString( ));
			PuzzleScrew p = builder.buildPuzzleScrew( );
			puzzleScrews.put( item.name, p );
			out = p;
			break;
		default:
			out = builder.buildScrew( );
			break;
		}
		return out;
	}

	public PuzzleScrew loadPuzzle( String name ) {
		if ( puzzleScrews.containsKey( name ) ) {
			return puzzleScrews.get( name );
		} else if ( items.get( GleedTypeTag.ENTITY ).containsKey( name ) ) {
			loadEntity( name ); // In loading the entity, we should get the
								// puzzle screw loaded.
			if ( puzzleScrews.containsKey( name ) ) {
				return puzzleScrews.get( name );
			}
		}
		return null;
	}

	public Level getLevel( ) {
		return level;
	}

	@SuppressWarnings( "unchecked" )
	protected static ArrayHash< String, String > getCustomProperties( Element e ) {
		@SuppressWarnings( "rawtypes" )
		ArrayHash out = new ArrayHash< String, String >( );
		Array< Element > properties = e.getChildByName( "CustomProperties" )
				.getChildrenByName( "Property" );
		String name;
		String value;
		for ( Element prop : properties ) {
			name = prop.getAttribute( "Name" ).toLowerCase( );
			value = prop.get( "string", "<no value>" );
			out.put( name, value );
		}
		return out;
	}

	protected static HashMap< String, Element > getChildrenByNameHash(
			Element e, String tag, String nameTag ) {
		HashMap< String, Element > out = new HashMap< String, Element >( );
		Array< Element > properties = e.getChildrenByName( tag );
		String name;
		for ( Element prop : properties ) {
			name = prop.getAttribute( nameTag );
			out.put( name, prop );
		}
		return out;
	}

	protected class Item {
		public Item( Element e ) {
			element = e;
			name = getName( e );
			gleedType = getGleedType( e );
			props = getCustomProperties( e );
			if ( props.containsKey( EntityDef.tag ) ) {
				defName = props.get( EntityDef.tag );
			} else {
				defName = "";
			}
			def = null;
			if ( props.containsKey( "skeleton" ) )
				skeleton = props.get( "skeleton" );
			else
				skeleton = "root";
			gleedTag = GleedTypeTag.fromString( props.get( "type" ) ); // GleedTypeTag.tag
																		// ) );
			pos = getPosition( e );
			sca = getScale( e );
			tex = getTexture( e );
			locked = false;
		}

		public Element element;

		public String name;
		// In the xml, gleedType refers to type. for example: CircleItem,
		// RectangleItem
		public String gleedType;

		// defName refers the string under the name under the CustomProperties
		// <Property Name="Definition"...
		// <string>tiledPlatform</string> <======= that is the defName
		public String defName;
		private EntityDef def;
		public GleedTypeTag gleedTag;
		public ArrayHash< String, String > props;
		public Vector2 pos;
		public Vector2 origin;
		public Vector2 sca;
		public Texture tex;
		public String skeleton;
		public boolean locked;

		public void checkLocked( ) {
			if ( locked ) {
				RuntimeException oops = new RuntimeException(
						"Cyclic Reference" );
				// Gdx.app.log("GleedLoader", "While loading:"+name, oops);
				throw oops;
			}
			locked = true;
		}

		/**
		 * getDefinition loads the correct XML file with the same time
		 * (complexTest) complexText loads the bottle, gearSmall would load the
		 * gear Remember to set them to kinematic or they just fall
		 * 
		 * @return EntityDef
		 */
		public EntityDef getDefinition( ) {
			if ( def == null )
				def = EntityDef.getDefinition( defName );
			return def;
		}

		/**
		 * checks if xml has a name under Definition
		 * 
		 * @return boolean
		 */
		public boolean hasDefTag( ) {
			return !defName.equals( "" );
		}

		public boolean isDefined( ) {
			return getDefinition( ) != null;
		}
	}

	protected static String getName( Element item ) {
		return item.getAttribute( "Name" );
	}

	protected static Vector2 getPosition( Element item ) {
		Element posElem = item.getChildByName( "Position" );
		return new Vector2( posElem.getFloat( "X" ) * GLEED_TO_GDX_X,
				posElem.getFloat( "Y" ) * GLEED_TO_GDX_Y );
	}

	protected static String getGleedType( Element item ) {
		return item.get( "xsi:type" );
	}

	protected static Vector2 getScale( Element item ) {
		Vector2 out = new Vector2( 1.0f, 1.0f );
		try {
			if ( getGleedType( item ).equals( "CircleItem" ) ) {
				out.x = out.y = item.getFloat( "Radius" ) * 2.0f;
			} else if ( getGleedType( item ).equals( "PathItem" ) ) {
				float left = 0.0f, right = 0.0f, top = 0.0f, bottom = 0.0f;
				// Fill this out later
				out.x = left - right;
				out.y = top - bottom;
			} else {
				out.x = item.getFloat( "Width" );
				out.y = item.getFloat( "Height" );
			}
		} finally {
		}
		return out;
	}

	protected static Texture getTexture( Element item ) {
		return null;
	}

	protected static Vector2 getGleedOrigin( Element item ) {
		Vector2 out = new Vector2( 0.0f, 0.0f );
		return out;
	}

	protected static final String typeTag = "Type";
	protected static final String defTag = "Definition";

}
