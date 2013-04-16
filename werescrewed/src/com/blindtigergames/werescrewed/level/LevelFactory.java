package com.blindtigergames.werescrewed.level;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityCategory;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.DestoryPlatformJointAction;
import com.blindtigergames.werescrewed.entity.action.EntityActivateMoverAction;
import com.blindtigergames.werescrewed.entity.action.EntityDeactivateMoverAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.MoverBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.RopeBuilder;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.PistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.mover.MoverType;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.screws.BossScrew;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.rope.Rope;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.entity.hazard.builders.HazardBuilder;
import com.blindtigergames.werescrewed.util.ArrayHash;
import com.blindtigergames.werescrewed.util.Util;

public class LevelFactory {
	protected XmlReader reader;
	protected Level level;
	protected String levelName;
	protected EnumMap< GleedTypeTag, LinkedHashMap< String, Item >> items;
	public static LinkedHashMap< String, Entity > entities;
	protected LinkedHashMap< String, TimelineTweenMover > movers;
	protected LinkedHashMap< String, Skeleton > skeletons;
	protected LinkedHashMap< String, PuzzleScrew > puzzleScrews;
	protected LinkedHashMap< String, Array< Vector2 > > polySprites;
	protected int spawnPoints;

	protected static final float GLEED_TO_GDX_X = 1.0f;
	protected static final float GLEED_TO_GDX_Y = -1.0f;
	protected static final String targetTag = "target";
	protected static final String startTime = "starttime";
	protected static final String endTime = "endtime";
	protected static final String puzzleTag = "puzzle";
	protected static final String dynamicTag = "dynamic";
	protected static final String decalTag = "decal";
	protected static final String decalBGTag = "bgdecal";
	protected static final String angleTag = "angle";
	protected static final String imageTag = "image";
	protected static final String gleedImageTag = "image";

	public LevelFactory( ) {
		reader = new XmlReader( );
		items = new EnumMap< GleedTypeTag, LinkedHashMap< String, Item >>(
				GleedTypeTag.class );
		for ( GleedTypeTag t : GleedTypeTag.values( ) ) {
			items.put( t, new LinkedHashMap< String, Item >( ) );
		}
		entities = new LinkedHashMap< String, Entity >( );
		movers = new LinkedHashMap< String, TimelineTweenMover >( );
		skeletons = new LinkedHashMap< String, Skeleton >( );
		puzzleScrews = new LinkedHashMap< String, PuzzleScrew >( );
		polySprites = new LinkedHashMap< String, Array< Vector2 > >( );
		level = new Level( );
		spawnPoints = 0;

	}

	public Level load( String filename ) {
		levelName = filename;
		// skeletons.put( "root", level.root );
		Element root;
		Array< Element > elements = new Array< Element >( );

		try {
			root = reader.parse( Gdx.files.internal( filename ) );
			Array< Element > layers = root.getChildByName( "Layers" )
					.getChildrenByName( "Layer" );
			for ( Element layer : layers ) {
				Gdx.app.log( "GleedLoader",
						"loading layer " + layer.getAttribute( "Name", "" ) );
				elements = layer.getChildByName( "Items" ).getChildrenByName(
						"Item" );
				Gdx.app.log( "GleedLoader", "Entities Found:" + elements.size );
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
						Gdx.app.log( "LevelFactory, Putting in items hashmap",
								item.name );
					}
				}
			}
		} catch ( IOException e1 ) {
			Gdx.app.log( "GleedLoader", "Error: could not load file "
					+ filename, e1 );
			e1.printStackTrace( );
		}
		// Load camera first.
		for ( Item i : items.get( GleedTypeTag.ENTITY ).values( ) ) {
			String bluePrints = i.defName;
			if ( bluePrints.equals( "camera" ) ) {
				placeCamera( i );
			}
		}
		// Load skeletons second.
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
			// add the skeleton to the skeleton layer for drawing
			if ( child.bgSprite != null ) {
				level.skelBGList.add( child );
			}
			if ( child.fgSprite != null ) {
				level.skelFGList.add( child );
			}
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
	protected Entity loadEntity( Item item ) {
		Entity out = null;

		// for example, player or tiledplatform or screw
		String bluePrints = item.defName;
		Gdx.app.log( "LevelFactory, bluePrints ", bluePrints );

		if ( bluePrints.equals( "skeleton" ) ) {
			out = constructSkeleton( item );
		} else if ( bluePrints.equals( decalTag ) ) {
			constructDecal( item );
		} else if ( bluePrints.equals( "player" ) ) {
			constructPlayer( item );
		} else if ( bluePrints.equals( "tiledPlatform" ) ) {
			out = constructTiledPlatform( item );
		} else if ( bluePrints.equals( "customPlatform" ) ) {
			out = constructCustomPlatform( item );
		} else if ( bluePrints.equals( "screw" ) ) {
			out = constructScrew( item );
		} else if ( bluePrints.equals( "pathmover" ) ) {
			constructPath( item );
		} else if ( bluePrints.equals( "rope" ) ) {
			constructRope( item );
		} else if ( bluePrints.equals( "checkpoint" ) ) {
			constructCheckpoint( item );
		} else if ( bluePrints.equals( "eventtrigger" ) ) {
			constructEventTrigger( item );
		} else if ( bluePrints.equals( "hazard" ) ) {
			out = constructHazard( item );
		} else if(bluePrints.equals("fixture")){ 
			constructFixture(item);
		} else if ( !bluePrints.equals( "camera" )
				&& item.getDefinition( ).getCategory( ) == EntityCategory.COMPLEX_PLATFORM ) {
			out = loadComplexPlatform( item );
		}

		// TODO add functionality for sorting these decals into foreground or
		// background decals
		// This either requires adding variables to the specific decal in gleed
		// to what layer it is on
		// or make two different loading keys such as fgdecal or bgdecal and
		// handle them seperatley
		if ( out != null ) {
			// if ( item.propes.containsKey( "fgdecal" ) ) {
			// level.entityFGList.add( decal );
			// }
			// if ( item.propes.containsKey( "bgdecal" ) ) {
			// level.entityBGList.add( decal );
			// }
			if ( item.props.containsKey( "decal" )
					|| item.props.containsKey( "bgdecal" ) ) {
				Array< String > tokens;
				String decalImage;
				Vector2 decalPosition = new Vector2( );
				Sprite decal;
				float r = 0.0f;
				for ( String decalData : item.props.getAll( "decal" ) ) {
					tokens = new Array< String >( decalData.split( "\\s+" ) );
					if ( tokens.size > 2 ) {
						decalImage = WereScrewedGame.dirHandle + tokens.get( 0 );
						decalPosition.x = Float.parseFloat( tokens.get( 1 ) );
						decalPosition.y = Float.parseFloat( tokens.get( 2 ) );
						decal = new Sprite( WereScrewedGame.manager.get(
								decalImage, Texture.class ) );
						decal.setOrigin( 0.0f, 0.0f );
						Vector2 size = new Vector2( 1.0f, 1.0f );
						if ( tokens.size > 3 ) {
							r = Float.parseFloat( tokens.get( 3 ) );
						}
						if ( tokens.size > 4 ) {
							size.x = Float.parseFloat( tokens.get( 4 ) );
							if ( tokens.size > 5 ) {
								size.y = Float.parseFloat( tokens.get( 5 ) );
							} else {
								size.y = size.x;
							}
						}
						decal.setScale( size.x, size.y );
						out.addFGDecal( decal, decalPosition, r );
						Gdx.app.log(
								"LoadEntity",
								"Creating foreground decal for [" + item.name
										+ "]. Image:" + decalImage
										+ " Position:"
										+ decalPosition.toString( ) );
					}
				}
				for ( String decalData : item.props.getAll( "bgdecal" ) ) {
					tokens = new Array< String >( decalData.split( "\\s+" ) );
					if ( tokens.size > 2 ) {
						decalImage = WereScrewedGame.dirHandle + tokens.get( 0 );
						decalPosition.x = Float.parseFloat( tokens.get( 1 ) );
						decalPosition.y = Float.parseFloat( tokens.get( 2 ) );
						decal = new Sprite( WereScrewedGame.manager.get(
								decalImage, Texture.class ) );
						decal.setOrigin( 0.0f, 0.0f );
						Vector2 size = new Vector2( 1.0f, 1.0f );
						if ( tokens.size > 3 ) {
							r = Float.parseFloat( tokens.get( 3 ) );
						}
						if ( tokens.size > 4 ) {
							size.x = Float.parseFloat( tokens.get( 4 ) );
							if ( tokens.size > 5 ) {
								size.y = Float.parseFloat( tokens.get( 5 ) );
							} else {
								size.y = size.x;
							}
						}
						decal.setScale( size.x, size.y );
						out.addBGDecal( decal, decalPosition, r );
						Gdx.app.log(
								"LoadEntity",
								"Creating background decal for [" + item.name
										+ "]. Image:" + decalImage
										+ " Position:"
										+ decalPosition.toString( ) );
					}
				}
			}
			out = addAnchors( item, out );
		}

		return out;
	}

	private Entity addAnchors( Item item, Entity out ) {
		RuntimeException exception = new RuntimeException(
				"Anchor incorrectly defined. Be sure the format is: \"bufferWidth, bufferHeight, offsetX, offsetY\"" );
		Anchor anchor;
		for ( int i = 1; i < 50; i++ ) {
			if ( item.props.containsKey( "anchor" + i ) ) {
				String string = item.props.get( "anchor" + i );
				String sValues[] = string.split( ", " );
				if ( sValues.length != 4 )
					throw exception;
				float values[] = new float[ 4 ];
				int j = 0;
				for ( String value : sValues ) {
					try {
						values[ j ] = Float.parseFloat( value );
						j++;
					} catch ( NumberFormatException e ) {
						throw exception;
					}
				}
				float offsetX = values[ 0 ];
				float offsetY = values[ 1 ];
				float bufferWidth = values[ 2 ];
				float bufferHeight = values[ 3 ];
				anchor = new Anchor( item.pos, new Vector2( offsetX, offsetY ),
						new Vector2( bufferWidth, bufferHeight ) );
				AnchorList.getInstance( ).addAnchor( anchor );
				out.addAnchor( anchor );
				anchor.activate( );
			} else {
				break;
			}
		}
		return out;
	}

	private Sprite constructDecal( Item item ) {
		Entity target = level.root;
		String targetName = "root";
		if ( item.getProps( ).containsKey( targetTag ) ) {
			targetName = item.getProps( ).get( targetTag );
			if ( this.items.get( GleedTypeTag.SKELETON ).containsKey(
					targetName ) ) {
				if ( loadSkeleton( targetName ) != null )
					target = loadSkeleton( targetName );
			} else if ( this.items.get( GleedTypeTag.ENTITY ).containsKey(
					targetName ) ) {
				if ( loadEntity( targetName ) != null )
					target = loadEntity( targetName );
			}
		}
		Sprite decal = null;
		Vector2 scale = new Vector2( 1.0f, 1.0f );
		if ( !item.getImageName( ).equals( "" ) ) {
			Texture tex = WereScrewedGame.manager.get(
					WereScrewedGame.dirHandle + item.getImageName( ),
					Texture.class );
			if ( item.getGleedType( ).equals( "PathItem" ) ) {
				Array< Element > pointElems = item.element.getChildByName(
						"LocalPoints" ).getChildrenByName( "Vector2" );
				Array< Vector2 > points = new Array< Vector2 >( );
				for ( Element e : pointElems ) {
					Vector2 v = new Vector2(
							e.getFloat( "X" ) * GLEED_TO_GDX_X,
							e.getFloat( "Y" ) * GLEED_TO_GDX_Y );
					points.add( v );
				}
				decal = new PolySprite( tex, points );
			} else {
				decal = new Sprite( tex );
				decal.setOrigin( 0.0f, 0.0f );
				scale.x = item.sca.x / tex.getWidth( );
				scale.y = item.sca.y / tex.getHeight( );
			}
		} else {
			Gdx.app.log( "LoadDecal", "Could not find texture tag." );
		}
		if ( decal != null ) {
			// Set position and rotation relative to the target.
			Vector2 targetPos = target.getPositionPixel( );
			float targetRot = target.getAngle( );

			Vector2 pos = item.pos.sub( targetPos );
			pos.y -= item.sca.y;

			float rot = item.rot - targetRot;

			decal.setScale( scale.x, scale.y );
			target.addFGDecal( decal, pos, rot );
			Gdx.app.log( "LoadDecal", "Attaching decal " + item.name + " to "
					+ targetName + "." );
			target.updateDecals( 0.0f );
			Gdx.app.log( "LoadDecal",
					"(X: " + decal.getX( ) + " Y: " + decal.getY( ) + " R: "
							+ decal.getRotation( ) + " sX: " + item.sca.x
							+ " sY: " + item.sca.y + ")" );
		}
		return decal;
	}

	private Skeleton constructSkeleton( Item item ) {
		Skeleton skeleton = null;
		if ( item.name.equals( "RootSkeleton" ) ) {
			level.root = new RootSkeleton( item.name, item.pos, null,
					level.world );
			skeletons.put( item.name, level.root );
			entities.put( item.name, level.root );

		} else {
			// attach skeleton to skeleton
			SkeletonBuilder skeleBuilder = new SkeletonBuilder( level.world );
			skeleBuilder.name( item.name ).position( item.pos ).texture( null );

			Array< Vector2 > polySprite = contstructSkeletonPoly( item );

			if ( item.props.containsKey( "invisible" ) ){
				skeleBuilder.invisibleVerts( polySprite );
			}
			else if ( item.props.containsKey( "noforeground" ) ) {
				skeleBuilder
						.bg( )
						.setVerts( polySprite )
						.texBackground(
								WereScrewedGame.manager
										.get( WereScrewedGame.dirHandle
												+ "/common/robot/alphabot_texture_skin.png",
												Texture.class ) );
			} else {
				skeleBuilder
						.bg( )
						.setVerts( polySprite )
						.texBackground(
								WereScrewedGame.manager
										.get( WereScrewedGame.dirHandle
												+ "/common/robot/alphabot_texture_skin.png",
												Texture.class ) ).fg( )
						.setVerts( polySprite );
				// .texForeground( WereScrewedGame.manager.get
				// (WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_skin.png",
				// Texture.class ));;
			}

			if ( item.props.containsKey( "dynamic" ) ) {
				skeleBuilder.dynamic( );
			}
			
			if ( item.props.containsKey("fade_fg_decals")){
				skeleBuilder.fadeFgDecals( true );
			}

			skeleton = skeleBuilder.build( );

			// IMover mover = null;
			// if(item.props.containsKey( "mover" )){
			// String movername = item.props.get( "mover" );
			// if (MoverType.fromString( movername ) != null){
			// mover = new MoverBuilder()
			// .fromString(movername)
			// .applyTo( skeleton )
			// .build( );
			// Gdx.app.log("LevelFactory", "attaching :" + movername +
			// " to skeleton");
			//
			// // ROTATETWEEN("rotatetween"),
			// // LERP("lerpmover")
			// }
			// }
			//
			// skeleton.addMover( mover, RobotState.IDLE );
			skeletons.put( item.name, skeleton );
			entities.put( item.name, skeleton );
			if ( item.props.containsKey( "attachtoskeleton" ) ) {
				String parentSkeleton = item.props.get( "attachtoskeleton" );
				Skeleton parent = skeletons.get( parentSkeleton );

				parent.addSkeleton( skeleton );
			}

			if ( item.props.containsKey( "jointto" ) ) {

				String parentSkeleton = item.props.get( "jointto" );
				Skeleton parent = skeletons.get( parentSkeleton );

				RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
				revoluteJointDef.initialize( skeleton.body, parent.body,
						skeleton.getPosition( ) );

				if ( item.props.containsKey( "degreelimit" ) ) {
					int limit = Integer.parseInt( item.props
							.get( "degreelimit" ) );
					revoluteJointDef.enableLimit = true;
					revoluteJointDef.upperAngle = limit * Util.DEG_TO_RAD;
					revoluteJointDef.lowerAngle = -limit * Util.DEG_TO_RAD;
				}

				level.world.createJoint( revoluteJointDef );

			}
			// add the skeleton to the skeleton layer for drawing
			if ( skeleton.bgSprite != null ) {
				level.skelBGList.add( skeleton );
			}
			if ( skeleton.fgSprite != null ) {
				level.skelFGList.add( skeleton );
			}
		}

		Gdx.app.log( "LevelFactory, Skeleton constucted ", item.name );

		return skeleton;
	}

	private void constructPlayer( Item item ) {
		if ( item.name.equals( "playerOne" ) ) {
			level.player1 = new PlayerBuilder( ).name( "player1" )
					.world( level.world ).position( item.pos )
					.definition( "red_male" ).buildPlayer( );
			entities.put( "player1", level.player1 );
		} else if ( item.name.equals( "playerTwo" ) ) {

			level.player2 = new PlayerBuilder( ).name( "player2" )
					.world( level.world ).position( item.pos )
					.definition( "red_female" ).buildPlayer( );
			entities.put( "player2", level.player2 );
		}

	}

	private void placeCamera( Item item ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;

		level.camera = new Camera( item.pos, width, height, level.world );

		// level.camera.camera.lookAt( item.pos.x, item.pos.y, 0f );
		// add position to camera later
	}
	
	private void constructFixture(Item item){
		Array< Vector2 > verts = constructArray( item );
		Entity addFixtureTo = entities.get( item.props.get( "fixtureof" ) );
		addFixtureTo.addFixture( verts, item.pos );
		
	}

	private TiledPlatform constructTiledPlatform( Item item ) {
		TiledPlatform out = null;
		float width = item.element.getFloat( "Width" );
		float height = item.element.getFloat( "Height" );
		int tileWidth = ( int ) ( width / Platform.tile );
		int tileHeight = ( int ) ( height / Platform.tile );

		float xPos = item.pos.x + ( width / 2 );
		float yPos = item.pos.y - ( height / 2 );

		PlatformBuilder pb = new PlatformBuilder( level.world );

		boolean isDynamic = false;
		if ( item.props.containsKey( "dynamic" ) ) {
			isDynamic = true;
		}

		boolean isCrushable = false;
		if ( item.props.containsKey( "crushable" ) ) {
			isCrushable = true;
		}

		pb.name( item.name ).position( new Vector2( xPos, yPos ) )
				.dimensions( new Vector2( tileWidth, tileHeight ) )
				.tileSet( "alphabot" );

		if ( isDynamic )
			pb.dynamic( );
		else
			pb.kinematic( );

		out = pb.buildTilePlatform( );

		entities.put( item.name, out );
		out.setCrushing( isCrushable );

		if ( item.props.containsKey( "onesided" ) ) {
			out.oneSided = true;
		}

		IMover mover = null;
		if ( item.props.containsKey( "mover" ) ) {

			// new PistonTweenMover( piston, new Vector2(
			// 0, -350 ), 0.5f, 3f, 1f, 0f, 1f ), RobotState.IDLE
			String movername = item.props.get( "mover" );
			if ( movername.equals( "pistonmover" ) ) {

				float delay = 0f;
				if ( item.props.containsKey( "delay" ) ) {
					delay = Float.parseFloat( item.props.get( "delay" ) );
				}

				float distance = 100f;
				if ( item.props.containsKey( "distance" ) ) {
					distance = Float.parseFloat( item.props.get( "distance" ) );
				}

				mover = new PistonTweenMover( out, new Vector2( 0, distance ),
						0.5f, 3f, 1f, 0f, delay );
			} else if ( MoverType.fromString( movername ) != null ) {
				mover = new MoverBuilder( ).fromString( movername )
						.applyTo( out ).build( );
				Gdx.app.log( "LevelFactory", "attaching :" + movername
						+ " to platform" );

				// ROTATETWEEN("rotatetween"),
				// LERP("lerpmover")
			}
		}

		out.addMover( mover, RobotState.IDLE );

		Skeleton parent = loadSkeleton( item.skeleton );

		if ( isDynamic ) {
			Gdx.app.log( "LevelFactory", "Tiled Dynamic platform loaded:"
					+ out.name );
			out.quickfixCollisions( );
			parent.addDynamicPlatform( out );

			if ( item.props.containsKey( "jointtoskeleton" ) ) {
				out.addJointToSkeleton( parent );
			}
		} else {
			Gdx.app.log( "LevelFactory", "Tiled Kinematic platform loaded:"
					+ out.name );

			parent.addKinematicPlatform( out );
			out.setCategoryMask( Util.CATEGORY_PLATFORMS,
					Util.CATEGORY_EVERYTHING );
		}
		return out;
	}

	private Platform constructCustomPlatform( Item item ) {
		Platform out = null;
		// float width = item.element.getFloat( "Width" );
		// float height = item.element.getFloat( "Height" );
		// float tileWidth = width / 32f;
		// float tileHeight = height / 32f;

		// float xPos = item.pos.x + (width/2);
		// float yPos = item.pos.y - (height/2);

		PlatformBuilder pb = new PlatformBuilder( level.world );

		boolean isDynamic = false;
		if ( item.props.containsKey( "dynamic" ) ) {
			isDynamic = true;
		}

		boolean isCrushable = false;
		if ( item.props.containsKey( "crushable" ) ) {
			isCrushable = true;
		}

		pb.name( item.name ).position( item.pos ).tileSet( "alphabot32" );

		if ( isDynamic )
			pb.dynamic( );
		else
			pb.kinematic( );

		Array< Vector2 > verts = constructArray( item );

		pb.setVerts( verts );
		out = pb.buildCustomPlatform( );

		entities.put( item.name, out );
		out.setCrushing( isCrushable );

		if ( item.props.containsKey( "onesided" ) ) {
			out.oneSided = true;
		}

		IMover mover = null;
		if ( item.props.containsKey( "mover" ) ) {
			String movername = item.props.get( "mover" );
			if ( MoverType.fromString( movername ) != null ) {
				mover = new MoverBuilder( ).fromString( movername )
						.applyTo( out ).build( );
				Gdx.app.log( "LevelFactory", "attaching :" + movername
						+ " to platform" );

				// ROTATETWEEN("rotatetween"),
				// LERP("lerpmover")
			}
		}

		out.addMover( mover, RobotState.IDLE );

		Skeleton parent = loadSkeleton( item.skeleton );

		if ( isDynamic ) {
			Gdx.app.log( "LevelFactory", "Tiled Dynamic platform loaded:"
					+ out.name );
			out.quickfixCollisions( );
			parent.addDynamicPlatform( out );
		} else {
			Gdx.app.log( "LevelFactory", "Tiled Kinematic platform loaded:"
					+ out.name );

			parent.addKinematicPlatform( out );
			out.setCategoryMask( Util.CATEGORY_PLATFORMS,
					Util.CATEGORY_EVERYTHING );
		}
		return out;
	}

	protected Platform loadComplexPlatform( Item item ) {
		Platform out = null;

		boolean isDynamic = false;
		if ( item.props.containsKey( "dynamic" ) ) {
			isDynamic = true;
		}

		boolean isCrushable = false;
		if ( item.props.containsKey( "crushable" ) ) {
			isCrushable = true;
		}

		boolean rotatingcenter = false;
		boolean motor = false;
		float speed = 1f;
		if ( item.props.containsKey( "rotatingcenter" ) ) {
			rotatingcenter = true;
			if ( item.props.get( "rotatingcenter" ).equals( "motor" ) ) {
				motor = true;
				speed = Float.parseFloat( item.props.get( "motorspeed" ) );

			}
		}

		out = new PlatformBuilder( level.world ).name( item.name )
				.type( item.getDefinition( ) )
				.position( item.pos.x, item.pos.y )
				.texture( item.getDefinition( ).getTexture( ) ).solid( true )
				.dynamic( isDynamic ).buildComplexPlatform( );

		entities.put( item.name, out );

		out.setCrushing( isCrushable );

		Gdx.app.log( "GleedLoader", "Complex Platform loaded:" + item.name );
		Skeleton parent = loadSkeleton( item.skeleton );
		if ( isDynamic ) {
			out.quickfixCollisions( );
			if ( rotatingcenter ) {
				if ( motor ) {
					parent.addPlatformRotatingCenterWithMot( out, speed );
				} else {
					parent.addPlatformRotatingCenter( out );
				}
			}
			parent.addDynamicPlatform( out );
		} else {
			parent.addKinematicPlatform( out );
		}
		return out;
	}

	private Screw constructScrew( Item item ) {

		// ScrewTypes:
		// SCREW_COSMETIC("ScrewCosmetic"),
		// SCREW_STRIPPED("ScrewStripped"),
		// SCREW_STRUCTURAL("ScrewStructural"),
		// SCREW_PUZZLE("ScrewPuzzle"),
		// SCREW_RESURRECT("ScrewResurrect"),
		// SCREW_BOSS("ScrewBoss");

		ScrewType sType = ScrewType.fromString( item.props.get( "screwtype" ) );
		ScrewBuilder builder = new ScrewBuilder( ).name( item.name )
				.position( item.pos ).world( level.world ).screwType( sType );

		Skeleton parent = loadSkeleton( item.skeleton );
		builder.skeleton( parent );

		if ( item.props.containsKey( "maxdepth" ) ) {
			int maxDepth = Integer.parseInt( item.props.get( "maxdepth" ) );
			builder.max( maxDepth );
		}

		Screw out = null;
		switch ( sType ) {
		case SCREW_PUZZLE:
			Gdx.app.log( "LevelFactory", "Building puzzle screw " + item.name
					+ " at " + item.pos.toString( ) );

			PuzzleScrew p = builder.buildPuzzleScrew( );

			Entity attach = null;
			if ( item.props.containsKey( "controlthis" ) ) {
				String s = item.props.get( "controlthis" );
				attach = entities.get( s );
				Gdx.app.log( "LevelFactory", "attaching :" + attach.name
						+ " to puzzle screw" );

				p.puzzleManager.addEntity( attach );
			}

			if ( item.props.containsKey( "jointto" ) ) {
				String s = item.props.get( "jointto" );
				Entity plat = entities.get( s );

				p.addStructureJoint( plat );
			} else {
				p.addStructureJoint( parent );
			}

			IMover mover = null;
			if ( item.props.containsKey( "mover" ) ) {
				String movername = item.props.get( "mover" );

				// TODO: add all movers to this mover builder
				if ( MoverType.fromString( movername ) != null ) {

					MoverBuilder moverBuilder = new MoverBuilder( ).fromString(
							movername ).applyTo( attach );

					if ( movername.equals( "lerpmover" ) ) {
						if ( item.props.containsKey( "distance" ) ) {
							float dist = Float.parseFloat( item.props
									.get( "distance" ) );
							moverBuilder.distance( dist );
						}

						if ( item.props.containsKey( "vertical" ) ) {
							moverBuilder.vertical( );
						} else if ( item.props.containsKey( "horizontal" ) ) {
							moverBuilder.horizontal( );
						}
					} else if ( movername.equals( "puzzlerotatetween" ) ) {

						Entity attach2 = null;
						if ( item.props.containsKey( "controlthis2" ) ) {
							String s = item.props.get( "controlthis2" );
							attach2 = entities.get( s );
							Gdx.app.log( "LevelFactory", "attaching :"
									+ attach2.name + " to puzzle screw" );

							p.puzzleManager.addEntity( attach2 );

							// HUGE NOTE: I HAVEN'T ADDED STUFF TO THIS MOVER
							// IN ITS BUILDER SO I'M HARDCODING IT FOR NOW
							p.puzzleManager
									.addMover( new PuzzleRotateTweenMover( 1,
											Util.PI / 2, true,
											PuzzleType.ON_OFF_MOVER ) );

							Gdx.app.log( "LevelFactory", "attaching :"
									+ movername + " to puzzle screw" );
						}
					}
					mover = moverBuilder.build( );
					Gdx.app.log( "LevelFactory", "attaching :" + movername
							+ " to puzzle screw" );

					p.puzzleManager.addMover( mover );
					// ROTATETWEEN("rotatetween"),
					// LERP("lerpmover")
				}
			}

			if ( item.props.containsKey( "addscrew" ) ) {
				String screw = item.props.get( "addscrew" );
				PuzzleScrew puzzleScrew = ( PuzzleScrew ) entities.get( screw );

				p.puzzleManager.addScrew( puzzleScrew );

			}
			puzzleScrews.put( item.name, p );
			entities.put( item.name, p );

			out = p;
			break;
		case SCREW_STRIPPED:
			Gdx.app.log( "LevelFactory", "Building stripped screw " + item.name
					+ " at " + item.pos.toString( ) );
			StrippedScrew s = builder.buildStrippedScrew( );
			entities.put( item.name, s );
			out = s;
			break;
		case SCREW_STRUCTURAL:
			Gdx.app.log( "LevelFactory", "Building structural screw "
					+ item.name + " at " + item.pos.toString( ) );

			StructureScrew ss = builder.buildStructureScrew( );

			if ( item.props.containsKey( "targetrev" ) ) {

				String thisthing = item.props.get( "targetrev" );
				Entity target = entities.get( thisthing );

				ss.addStructureJoint( target );
			}

			if ( item.props.containsKey( "targetrev2" ) ) {

				String thisthing = item.props.get( "targetrev2" );
				Entity target = entities.get( thisthing );

				ss.addStructureJoint( target );
			}

			if ( item.props.containsKey( "skeltargetrev" ) ) {

				String thisthing = item.props.get( "skeltargetrev" );
				Skeleton target = skeletons.get( thisthing );

				ss.addStructureJoint( target );
			}

			if ( item.props.containsKey( "targetweld" ) ) {

				String thisthing = item.props.get( "targetweld" );
				Entity target = entities.get( thisthing );

				ss.addWeldJoint( target );
			}

			if ( item.props.containsKey( "targetweld2" ) ) {

				String thisthing = item.props.get( "targetweld2" );
				Entity target = entities.get( thisthing );

				ss.addWeldJoint( target );
			}

			entities.put( item.name, ss );
			out = ss;

			break;
		case SCREW_COSMETIC:
			Gdx.app.log( "LevelFactory", "Building cosmetic screw " + item.name
					+ " at " + item.pos.toString( ) );
			Screw screw = builder.buildScrew( );
			entities.put( item.name, screw );
			out = screw;
			break;
		case SCREW_BOSS:
			Gdx.app.log( "LevelFactory", "Building boss screw " + item.name
					+ " at " + item.pos.toString( ) );
			BossScrew bs = builder.buildBossScrew( );
			entities.put( item.name, bs );
			out = bs;
			break;
		default:
			out = builder.buildScrew( );
			break;
		}

		return out;
	}

	public void constructCheckpoint( Item item ) {

		if ( level.progressManager == null ) {
			level.progressManager = new ProgressManager( level.player1,
					level.player2, level.world );
		}
		String skel = item.skeleton;
		Skeleton parent = loadSkeleton( skel );
		CheckPoint chkpt = new CheckPoint( item.name, item.pos, parent,
				level.world, level.progressManager, levelName );
		// level.progressManager
		// add checkpointto skeleton not progress manager
		parent.addCheckPoint( chkpt );
		// chkpt.setParentSkeleton( parent );
	}

	/**
	 * This function is for paths(in gleed) that represent pathmovers in the
	 * game
	 * 
	 * @param item
	 */
	public void constructPath( Item item ) {

		Array< Element > pointElems = item.element.getChildByName(
				"LocalPoints" ).getChildrenByName( "Vector2" );
		Gdx.app.log( "LevelFactory", "Loading Path Mover:" + pointElems.size
				+ " points." );
		Array< Vector2 > pathPoints = new Array< Vector2 >( pointElems.size );
		Array< Float > times = new Array< Float >( pointElems.size );

		Platform p = ( Platform ) loadEntity( item.props.get( "applyto" ) );
		PathBuilder pBuilder = new PathBuilder( ).begin( p );

		Element vElem;
		Vector2 point;
		String timeTag;

		// Set first and last point times with separate tags.
		// If tags are not available, assume they will be at 0.0f and 1.0f,
		// respectively.
		// As points are loaded, these values may get overridden; this is fine.
		for ( int i = 0; i < pointElems.size; i++ ) {
			times.add( 1.0f );
		}

		if ( item.props.containsKey( "delay" ) ) {
			float delay = Float.parseFloat( item.props.get( "delay" ) );
			pBuilder.delay( delay );
			Gdx.app.log( "LevelFactory", "path has delay " + delay );
		}

		// Starts at one because first point on a path should start at 0,0 by
		// default
		for ( int i = 1; i < pointElems.size; i++ ) {
			vElem = pointElems.get( i );
			point = new Vector2( vElem.getFloat( "X" ) * GLEED_TO_GDX_X,
					vElem.getFloat( "Y" ) * GLEED_TO_GDX_Y );
			pathPoints.add( point );
			// Gdx.app.log( "LevelFactory Path", "Point " + i +
			// " has coordinates "
			// + point.toString( ) + "." );
		}
		for ( int i = 1; i <= pathPoints.size; ++i ) {
			timeTag = "point" + i + "time";
			if ( item.props.containsKey( timeTag ) ) {
				float time = Float.parseFloat( item.props.get( timeTag ) );
				if ( time >= 0.0f ) {
					times.set( i - 1, time );
				}
			}
			Gdx.app.log( "LevelFactory",
					"Point " + i + " has time " + times.get( i ) + "." );
		}
		for ( int i = 0; i < pathPoints.size; i++ ) {
			pBuilder.target( pathPoints.get( i ).x, pathPoints.get( i ).y,
					times.get( i ).floatValue( ) );
		}

		TimelineTweenMover out = pBuilder.build( );
		movers.put( item.name, out );
		p.addMover( out, RobotState.IDLE );

		if ( item.props.containsKey( "inactive" ) )
			p.setActive( false );
		else
			p.setActive( true );
	}

	public void constructRope( Item item ) {
		RopeBuilder ropeBuilder = new RopeBuilder( level.world );
		ropeBuilder.name( item.name ).position( item.pos.x, item.pos.y )
				.createScrew( );

		if ( item.props.containsKey( "links" ) ) {
			int links = Integer.parseInt( item.props.get( "links" ) );
			ropeBuilder.links( links );
		}

		// if(item.props.containsKey( "numberofscrews" )){
		// int num = Integer.parseInt( item.props.get("numberofscrews") );
		// ropeBuilder.createScrew(num);
		// }

		boolean attachToEntity = false;
		if ( item.props.containsKey( "attachedto" ) ) {
			Entity e = loadEntity( item.props.get( "attachedto" ) );
			ropeBuilder.attachToTop( e );
			attachToEntity = true;
		}

		Skeleton parent = loadSkeleton( item.skeleton );
		Rope rope = ropeBuilder.buildRope( );
		// if its attached to an entity, then send in false so it doesn't
		// joint itself to the skeleton
		parent.addRope( rope, !attachToEntity );
	}

	public Array< Vector2 > contstructSkeletonPoly( Item item ) {

		Array< Element > pointElems = item.element.getChildByName(
				"LocalPoints" ).getChildrenByName( "Vector2" );
		Gdx.app.log( "LevelFactory", "Loading PolySprite:" + pointElems.size
				+ " points." );
		Array< Vector2 > pathPoints = new Array< Vector2 >( pointElems.size );

		Element vElem;
		Vector2 point;
		for ( int i = 1; i < pointElems.size; i++ ) {
			vElem = pointElems.get( i );
			point = new Vector2( vElem.getFloat( "X" ) * GLEED_TO_GDX_X,
					vElem.getFloat( "Y" ) * GLEED_TO_GDX_Y );
			pathPoints.add( point );
			// Gdx.app.log( "LevelFactory SkelePoly", "Point " + i +
			// " has coordinates "
			// + point.toString( ) + "." );

		}

		return pathPoints;

	}

	public void constructEventTrigger( Item item ) {
		String skelAttach = item.skeleton;
		Skeleton parent = loadSkeleton( skelAttach );

		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );

		etb.name( item.name ).position( item.pos );

		if ( item.props.containsKey( "applyto" ) ) {
			String connectTo = item.props.get( "applyto" );

			Entity entity = loadEntity( connectTo );
			etb.addEntity( entity );
		}

		if ( item.props.containsKey( "applyto1" ) ) {
			String connectTo = item.props.get( "applyto1" );

			Entity entity = loadEntity( connectTo );
			etb.addEntity( entity );
		}

		if ( item.props.containsKey( "beginaction" ) ) {
			String action = item.props.get( "beginaction" );
			if ( action.equals( "destoryjoint" ) ) {
				etb.beginAction( new DestoryPlatformJointAction( ) );
			} else {
				etb.beginAction( new EntityActivateMoverAction( ) );
			}
		}

		if ( item.props.containsKey( "endaction" ) ) {
			etb.beginAction( new EntityDeactivateMoverAction( ) );
		}

		if ( item.props.containsKey( "twoplayerstoactivate" ) ) {
			etb.twoPlayersToActivate( );
		}
		if ( item.props.containsKey( "twoplayerstodeactivate" ) ) {
			etb.twoPlayersToDeactivate( );
		}

		if ( item.props.containsKey( "repeatable" ) ) {
			etb.repeatable( );
		}

		if ( item.props.containsKey( "circle" ) ) {
			float radius = Float.parseFloat( item.props.get( "circle" ) );
			etb.circle( ).radius( radius );
		} else {

			Array< Vector2 > verts = constructArray( item );

			etb.setVerts( verts );
		}

		etb.extraBorder( 0f );

		EventTrigger et = etb.build( );
		entities.put( item.name, et );
		parent.addEventTrigger( et );
	}

	/**
	 * Used to get path points from an item from the XML file
	 * 
	 * @param item
	 * @return
	 */
	public Array< Vector2 > constructArray( Item item ) {

		Array< Element > pointElems = item.element.getChildByName(
				"LocalPoints" ).getChildrenByName( "Vector2" );
		Gdx.app.log( "LevelFactory", "Loading Array[]:" + pointElems.size
				+ " points." );
		Array< Vector2 > pathPoints = new Array< Vector2 >( pointElems.size );

		Element vElem;
		Vector2 point;
		for ( int i = 1; i < pointElems.size; i++ ) {
			vElem = pointElems.get( i );
			point = new Vector2( vElem.getFloat( "X" ) * GLEED_TO_GDX_X,
					vElem.getFloat( "Y" ) * GLEED_TO_GDX_Y );
			pathPoints.add( point );
			// Gdx.app.log( "LevelFactory Array", "Point " + i +
			// " has coordinates "
			// + point.toString( ) + "." );

		}

		return pathPoints;

	}

	public Hazard constructHazard( Item item ) {

		String skelAttach = item.skeleton;
		Skeleton parent = loadSkeleton( skelAttach );

		float width = item.element.getFloat( "Width" );
		float height = item.element.getFloat( "Height" );
		float tileWidth = width / 32f;
		float tileHeight = height / 32f;

		float xPos = item.pos.x + ( width / 2 );
		float yPos = item.pos.y - ( height / 2 );

		HazardBuilder hazardBuilder = new HazardBuilder( level.world );

		hazardBuilder.position( new Vector2( xPos, yPos ) )
				.dimensions( tileWidth, tileHeight ).active( );

		if ( item.props.containsKey( "right" ) ) {
			hazardBuilder.right( );
		} else if ( item.props.containsKey( "down" ) ) {
			hazardBuilder.down( );
		} else if ( item.props.containsKey( "left" ) ) {
			hazardBuilder.left( );
		} else {
			hazardBuilder.up( );
		}

		Hazard hazard = hazardBuilder.buildSpikes( );
		parent.addKinematicPlatform( hazard );

		return hazard;
	}

	public Level getLevel( ) {
		return level;
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
			name = getName( );
			gleedType = getGleedType( );
			props = getProps( );
			if ( props.containsKey( "definition" ) ) { // EntityDef.tag )){
				defName = props.get( "definition" ); // EntityDef.tag );
			} else {
				defName = "";
			}
			def = null;
			if ( props.containsKey( "attachtoskeleton" ) ) {
				skeleton = props.get( "attachtoskeleton" );
				Gdx.app.log( "LevelFactory, attaching skeleton " + skeleton,
						"to " + name );
			}
			gleedTag = GleedTypeTag.fromString( props.get( "type" ) ); // GleedTypeTag.tag
																		// ) );
			pos = getPosition( );
			rot = getAngle( );
			sca = getScale( );
			image = getImageName( );
			locked = false;
		}

		public Element element;

		// name refers to the first name, right after xsi:type
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
		private ArrayHash props;
		public Vector2 pos;
		public float rot;
		public Vector2 origin;
		public Vector2 sca;
		public Texture tex;
		public String skeleton;
		public String image;
		public boolean locked;

		public void checkLocked( ) {
			if ( locked ) {
				RuntimeException oops = new RuntimeException(
						"Cyclic Reference" );
				Gdx.app.log( "GleedLoader", "While loading:" + name, oops );
				throw oops;
			}
			locked = true;
		}

		protected ArrayHash getProps( ) {
			if ( props == null ) {
				props = new ArrayHash( );
				Array< Element > properties = element.getChildByName(
						"CustomProperties" ).getChildrenByName( "Property" );
				String name;
				String value;
				for ( Element prop : properties ) {
					name = prop.getAttribute( "Name" ).toLowerCase( );
					value = prop.get( "string", "<no value>" );
					props.add( name, value );
				}
			}
			return props;
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

		protected String getName( ) {
			return element.getAttribute( "Name" );
		}

		protected Vector2 getPosition( ) {
			Element posElem = element.getChildByName( "Position" );
			return new Vector2( posElem.getFloat( "X" ) * GLEED_TO_GDX_X,
					posElem.getFloat( "Y" ) * GLEED_TO_GDX_Y );
		}

		protected float getAngle( ) {
			if ( getProps( ).containsKey( angleTag ) ) {
				return Float.parseFloat( getProps( ).get( angleTag ) );
			}
			return 0.0f;
		}

		protected String getGleedType( ) {
			return element.get( "xsi:type" );
		}

		protected Vector2 getScale( ) {
			Vector2 out = new Vector2( 1.0f, 1.0f );
			try {
				if ( getGleedType( ).equals( "CircleItem" ) ) {
					out.x = out.y = element.getFloat( "Radius" ) * 2.0f;
				} else if ( getGleedType( ).equals( "PathItem" ) ) {
					float left = 0.0f, right = 0.0f, top = 0.0f, bottom = 0.0f;
					// Fill this out later
					out.x = left - right;
					out.y = top - bottom;
				} else if ( element.get( "xsi:type" ).equals( "RectangleItem" ) ) {
					out.x = element.getFloat( "Width" );
					out.y = element.getFloat( "Height" );
				} else if ( element.get( "xsi:type" ).equals( "TextureItem" ) ) {
					// out.x = element.getFloat( "Width" );
					// out.y = element.getFloat( "Height" );
				}
			} finally {
			}
			return out;
		}

		protected String getImageName( ) {
			if ( getProps( ).containsKey( imageTag ) ) {
				return getProps( ).get( imageTag );
			} else {
				return element.getAttribute( gleedImageTag, "" );
			}
		}

		protected Vector2 getGleedOrigin( ) {
			Vector2 out = new Vector2( 0.0f, 0.0f );
			return out;
		}
	}

	protected static final String typeTag = "Type";
	protected static final String defTag = "Definition";

}