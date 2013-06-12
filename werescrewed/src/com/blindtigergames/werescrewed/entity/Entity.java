package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.animator.IAnimator;
import com.blindtigergames.werescrewed.entity.animator.ISpinemator;
import com.blindtigergames.werescrewed.entity.animator.PlayerAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.level.GleedLoadable;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundType;
import com.blindtigergames.werescrewed.util.ArrayHash;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Anything that can exist. Contains a physics body, and a sprite which may or
 * may not be animated.
 * 
 * @author Kevin / Ranveer
 * 
 */
public class Entity implements GleedLoadable {
	private static final int INITAL_CAPACITY = 3;

	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Vector2 offset;
	public Vector2 bodyOffset;
	public Body body;
	protected World world;
	protected boolean solid;
	public ArrayList< Anchor > anchors;
	protected float energy;
	protected boolean active;
	protected boolean crushing;
	protected boolean visible, drawParticles = true;
	protected boolean maintained;
	protected boolean removeNextStep = false;
	public EntityType entityType;
	public boolean dontPutToSleep = false;
	private ArrayList< IMover > moverArray;
	protected ArrayList< Sprite > fgDecals;
	protected ArrayList< Vector2 > fgDecalOffsets;
	protected ArrayList< Float > fgDecalAngles;
	protected ArrayList< Sprite > bgDecals;
	protected ArrayList< Vector2 > bgDecalOffsets;
	protected ArrayList< Float > bgDecalAngles;
	private RobotState currentRobotState;
	private EnumMap< RobotState, Integer > robotStateMap;
	private ISpinemator spinemator;
	private Vector2 oldPos;
	private float oldAngle;

	private Skeleton parentSkeleton; // pointer to parent skele, set by skeleton

	protected ArrayHash< String, ParticleEffect > behindParticles,
			frontParticles;

	public SoundManager sounds;

	/**
	 * Create entity by definition
	 * 
	 * @param name
	 * @param type
	 * @param world
	 *            in which the entity exists
	 * @param pos
	 *            ition of the entity in the world
	 * @param rot
	 *            ation of the entity
	 * @param scale
	 *            of the entity
	 * @param texture
	 *            (null if defined elsewhere)
	 * @param solid
	 *            boolean determining whether or not the player can stand on it
	 */
	public Entity( String name, EntityDef type, World world,
			Vector2 positionPixels, float rot, Vector2 scale, Texture texture,
			boolean solid ) {
		this.construct( name, solid );
		this.type = type;
		this.world = world;
		if ( !this.type.animatorType.equals( "spine" ) ) {
			if ( type.atlases.size > 0 ) {
				this.sprite = constructSprite( type.atlases.get( 0 ) );
			} else {
				this.sprite = constructSprite( texture );
			}
		}
		this.body = constructBodyByType( );
		setPixelPosition( positionPixels );
		this.anchors = new ArrayList< Anchor >( );
	}

	/**
	 * Same constructor as above but without scale or rotation
	 * 
	 * @param name
	 * @param type
	 * @param world
	 * @param positionPixels
	 * @param texture
	 */
	public Entity( String name, EntityDef type, World world,
			Vector2 positionPixels, Texture texture ) {
		this.construct( name, solid );
		this.type = type;
		this.world = world;

		if ( !this.type.animatorType.equals( "spine" ) ) {
			if ( type.atlases.size > 0 ) {
				this.sprite = constructSprite( type.atlases.get( 0 ) );
			} else {
				this.sprite = constructSprite( texture );
			}
		}
		this.body = constructBodyByType( );
		this.fgDecals = new ArrayList< Sprite >( );
		this.fgDecalOffsets = new ArrayList< Vector2 >( );
		this.bgDecals = new ArrayList< Sprite >( );
		this.bgDecalOffsets = new ArrayList< Vector2 >( );
		setPixelPosition( positionPixels );
		this.anchors = new ArrayList< Anchor >( );
	}

	/**
	 * Create entity by body. Debug constructor: Should be removed eventually.
	 * 
	 * @param name
	 * @param positionPixels
	 *            ition of the entity in the world in PIXELS
	 * @param texture
	 *            (null if defined elsewhere)
	 * @param body
	 *            defined body of the entity
	 * @param solid
	 *            boolean determining whether or not the player can stand on it
	 */
	public Entity( String name, Vector2 positionPixels, Texture texture,
			Body body, boolean solid ) {
		this.construct( name, solid );
		this.sprite = constructSprite( texture );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			// sprite.setScale( Util.PIXEL_TO_BOX );
		}
		this.setPixelPosition( positionPixels );
		this.anchors = new ArrayList< Anchor >( );
	}

	public Entity( String name, Vector2 positionPixels, TextureRegion texture,
			Body body, boolean solid, float rotation ) {
		this.construct( name, solid );
		if ( texture != null )
			this.sprite = constructSprite( texture );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			// sprite.setScale( Util.PIXEL_TO_BOX );
		}
		this.setPixelPosition( positionPixels );
		this.anchors = new ArrayList< Anchor >( );
	}

	public Entity( String name, Vector2 positionPixels, boolean solid,
			ISpinemator spinemator, Body body ) {
		this.construct( name, solid );
		this.spinemator = spinemator;
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
			// sprite.setScale( Util.PIXEL_TO_BOX );
		}
		this.setPixelPosition( positionPixels );
		this.anchors = new ArrayList< Anchor >( );
	}

	/**
	 * Construct an entity that uses a PolySprite
	 * 
	 * @param name
	 * @param positionPixels
	 *            POSITION IN PIXELS
	 * @param texture
	 *            texture to fill the polysprite with
	 * @param verts
	 *            an Array<Vector2> of vertex points of the poly. Must be
	 *            concave or it will look weird.
	 * @param body
	 *            - same old
	 * @param solid
	 *            same as it always was.
	 * @author stew
	 */
	public Entity( String name, Vector2 positionPixels, Texture texture,
			Array< Vector2 > verts, Body body, boolean solid ) {
		this.construct( name, solid );
		this.sprite = new PolySprite( texture, verts );
		this.body = body;
		if ( body != null ) {
			world = body.getWorld( );
		}
		this.setPixelPosition( positionPixels );
	}

	/**
	 * Common sub-constructor that applies to all Entity() constructors.
	 */
	protected void construct( String name, boolean solid ) {
		this.name = name;
		this.solid = solid;
		this.offset = new Vector2( 0.0f, 0.0f );
		this.bodyOffset = new Vector2( 0.0f, 0.0f );
		this.energy = 1.0f;
		this.maintained = true;
		this.visible = true;
		this.active = true;
		this.fgDecals = new ArrayList< Sprite >( );
		this.fgDecalOffsets = new ArrayList< Vector2 >( );
		this.fgDecalAngles = new ArrayList< Float >( );
		this.bgDecals = new ArrayList< Sprite >( );
		this.bgDecalOffsets = new ArrayList< Vector2 >( );
		this.bgDecalAngles = new ArrayList< Float >( );
		this.sounds = null;
		this.crushing = false;
		setUpRobotState( );
	}

	/**
	 * Set position of the body in meters.
	 * 
	 * @param xMeters
	 * @param yMeters
	 */
	public void setPosition( float xMeters, float yMeters ) {
		xMeters -= bodyOffset.x;
		yMeters -= bodyOffset.y;
		if ( body != null ) {
			body.setTransform( xMeters, yMeters, body.getAngle( ) );
		} else if ( sprite != null ) {
			sprite.setPosition( xMeters * Util.BOX_TO_PIXEL, yMeters
					* Util.BOX_TO_PIXEL );
		} else if ( spinemator != null ) {
			spinemator.setPosition( xMeters * Util.BOX_TO_PIXEL, yMeters
					* Util.BOX_TO_PIXEL );
		}
	}

	public void setPixelPosition( float x, float y ) {
		setPosition( x * Util.PIXEL_TO_BOX, y * Util.PIXEL_TO_BOX );
	}

	public void setPixelPosition( Vector2 pixels ) {
		if ( pixels != null )
			setPixelPosition( pixels.x, pixels.y );
	}

	/**
	 * Set position by meters!!
	 * 
	 * @param positionMeters
	 */
	public void setPosition( Vector2 positionMeters ) {
		setPosition( positionMeters.x, positionMeters.y );
	}

	/**
	 * returns body position in meters.
	 * 
	 * @return Vector2 in meters of bodie's world origin
	 */
	public Vector2 getPosition( ) {
		return body.getPosition( ).add( bodyOffset );
	}

	/**
	 * Use this position when setting relative position of platforms for paths
	 * targets. ie you set a platform at (x,y) in meters, but the path takes in
	 * pixels, so do something like platform. getPositionPixel().add(0,600)
	 * 
	 * @return world position of origin in PIXELS
	 */
	public Vector2 getPositionPixel( ) {
		if ( body != null ) {
			return body.getPosition( ).cpy( ).mul( Util.BOX_TO_PIXEL );
		}
		if ( sprite != null ) {
			return new Vector2( sprite.getX( ), sprite.getY( ) );
		}
		if ( spinemator != null ) {
			return spinemator.getPosition( );
		}
		return Vector2.Zero;
	}

	public void move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector );
		setPosition( pos );
	}

	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		if ( drawParticles )
			drawParticles( behindParticles, batch, camera );
		if ( visible ) {
			if ( sprite != null && !removeNextStep ) {
				sprite.draw( batch );
			}
			if ( spinemator != null )
				spinemator.draw( batch );

		}
		if ( drawParticles )
			drawParticles( frontParticles, batch, camera );
	}

	protected void drawParticles( ArrayHash< String, ParticleEffect > map,
			SpriteBatch batch, Camera camera ) {
		if ( map != null ) {
			for ( String key : map.keySet( ) ) {
				for ( ParticleEffect e : map.getAll( key ) ) {
					if ( !e.isComplete( ) ) {
						e.draw( batch, camera );
					}
				}
			}
		}
	}

	public void drawOrigin( SpriteBatch batch ) {
		float axisSize = 128.0f;
		ShapeRenderer shapes = new ShapeRenderer( );
		shapes.setProjectionMatrix( batch.getProjectionMatrix( ) );
		Vector2 pos = getPosition( ).mul( Util.BOX_TO_PIXEL );
		shapes.begin( ShapeType.Line );
		shapes.setColor( 1.0f, 0.0f, 0.0f, 1.0f );
		shapes.line( pos.x, pos.y, pos.x + axisSize, pos.y ); // Red: X-axis
		shapes.setColor( 0.0f, 0.0f, 1.0f, 1.0f );
		shapes.line( pos.x, pos.y, pos.x, pos.y + axisSize ); // Blue: Y-axis
		if ( sprite != null ) {
			shapes.setColor( 0.0f, 1.0f, 0.0f, 1.0f );
			shapes.line( pos.x, pos.y, pos.x - sprite.getOriginX( ), pos.y
					- sprite.getOriginY( ) ); // Green: Sprite Origin
		}
		shapes.end( );
	}

	/**
	 * this is called from event triggers during a world step so you dont remove
	 * box2d data while the world is locked
	 */
	public void setRemoveNextStep( ) {
		removeNextStep = true;
	}

	/**
	 * if this entity is to be removed next step return true used by skeletons
	 * to remove from thier list after updating this entity so that box2d is
	 * removed first
	 * 
	 * @return
	 */
	public boolean getRemoveNextStep( ) {
		return removeNextStep;
	}

	public void remove( ) {
		if ( body != null ) {
			while ( body.getJointList( ).iterator( ).hasNext( ) ) {
				world.destroyJoint( body.getJointList( ).get( 0 ).joint );
			}
			world.destroyBody( body );
			body = null;
		}
	}

	public void update( float deltaTime ) {
		if ( body != null ) {
			// animation stuff may go here
			Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );

			if ( sprite != null ) {
				sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
				sprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
				sprite.update( deltaTime );
			}
			if ( anchors != null && anchors.size( ) != 0 ) {
				updateAnchors( );
			}
			updateDecals( deltaTime );
		}
		if ( spinemator != null ) {
			spinemator.update( deltaTime );
		}

		updateParticleEffect( deltaTime, frontParticles );
		updateParticleEffect( deltaTime, behindParticles );

		if ( sounds != null ) {
			if ( sounds.hasSound( "idle" ) ) {
				SoundRef ref = sounds.getSound( "idle" );
				ref.setVolume( SoundManager.getNoiseVolume( ) * ref.calculatePositionalVolume( this.getPositionPixel(),  Camera.CAMERA_RECT ) );
			}
			handleMovementSounds( deltaTime );
			sounds.update( deltaTime );
		}
	}

	/**
	 * Polymorphic method to update particle effects
	 * 
	 * @author stew
	 */
	private void updateParticleEffect( float deltaTime,
			ArrayHash< String, ParticleEffect > map ) {
		Array< ParticleEffect > removals = null;
		if ( map != null ) {
			Vector2 pos = getPositionPixel( );
			for ( String key : map.keySet( ) ) {
				for ( ParticleEffect emitter : map.getAll( key ) ) {
					if ( emitter.updatePositionOnUpdate ) {
						if ( !emitter.offsetFromParent.equals( Vector2.Zero )
								&& body != null ) {
							Vector2 newPos = new Vector2(
									emitter.offsetFromParent ).rotate( body
									.getAngle( ) * Util.RAD_TO_DEG );
							emitter.setPosition( pos.x + newPos.x, pos.y
									+ newPos.y );
						} else {
							emitter.setPosition( pos.x, pos.y );
						}
						if ( emitter.updateAngleWithParent ) {
							if ( body != null ) {
								emitter.setEffectAngle( body.getAngle( ) );
							} else if ( sprite != null ) {
								emitter.setEffectAngle( sprite.getRotation( )
										* Util.DEG_TO_RAD );
							}
						}
					}
					if ( !emitter.isComplete( ) ) {
						emitter.update( deltaTime );
					} else if ( emitter.removeOnComplete ) {
						if ( removals == null )
							removals = new Array< ParticleEffect >( );
						removals.add( emitter );
					}
				}
				if ( removals != null ) {
					for ( ParticleEffect e : removals )
						map.remove( e.effectName, e );
				}
			}
		}
	}

	/**
	 * Update the mover of this entity, if it exists. Now separated from
	 * update() so that it can be called whenever skeleton wants.
	 * 
	 * @param deltaTime
	 */
	public boolean updateMover( float deltaTime ) {
		if ( active ) {
			if ( body != null ) {
				if ( currentMover( ) != null ) {
					Vector2 oldPos = body.getPosition( );
					currentMover( ).move( deltaTime, body );
					Vector2 newPos = body.getPosition( );
					if ( oldPos.x != newPos.x || oldPos.y != newPos.y ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void updateSounds( float deltaTime ) {
		sounds.update( deltaTime );
		if ( body != null ) {
			Vector2 absVelocity = body.getLinearVelocity( );
			float absAngVelocity = body.getAngularVelocity( );
			if ( sounds.hasSound( "linearvelocity" ) ) {
				// These properties should be moved into SoundRef. They're just
				// here for testing.
				Vector2 lineScale = new Vector2( 1.0f, 1.0f );
				Vector2 relativeVelocity;
				if ( getParentSkeleton( ) != null ) {
					relativeVelocity = body
							.getLinearVelocityFromLocalPoint( getParentSkeleton( )
									.getPosition( ) );
				} else {
					relativeVelocity = absVelocity.cpy( );
				}
				float pitchZero = 0.5f;
				float pitchRange = -0.5f;
				float pitchExp = 1.0f;
				float volumeZero = 1.0f;
				float volumeRange = -1.0f;
				float volumeExp = 1.0f;
				relativeVelocity.x *= lineScale.x;
				relativeVelocity.y *= lineScale.y;
				float contrib = Math.max(
						Math.min( relativeVelocity.len( ), 0.0f ), 1.0f );
				sounds.setSoundPitch(
						"linearvelocity",
						pitchZero
								+ ( float ) Math.pow( ( pitchRange * contrib ),
										pitchExp ) );
				sounds.setSoundVolume(
						"linearvelocity",
						volumeZero
								+ ( float ) Math.pow(
										( volumeRange * contrib ), volumeExp ) );
			}
			if ( sounds.hasSound( "angularvelocity" ) ) {
				float angScale = 1.0f;
				float relativeAngVelocity = absAngVelocity;
				if ( getParentSkeleton( ) != null ) {
					relativeAngVelocity -= getParentSkeleton( ).body
							.getAngularVelocity( );
				}
				relativeAngVelocity *= angScale;
				float pitchZero = 0.5f;
				float pitchRange = -0.5f;
				float pitchExp = 1.0f;
				float volumeZero = 1.0f;
				float volumeRange = -1.0f;
				float volumeExp = 1.0f;
				float contrib = Math.max(
						Math.min( relativeAngVelocity, 0.0f ), 1.0f );
				sounds.setSoundPitch(
						"angularvelocity",
						pitchZero
								+ ( float ) Math.pow( ( pitchRange * contrib ),
										pitchExp ) );
				sounds.setSoundVolume(
						"angularvelocity",
						volumeZero
								+ ( float ) Math.pow(
										( volumeRange * contrib ), volumeExp ) );
			}
			if ( sounds.hasSound( "abslinearvelocity" ) ) {
				Vector2 lineScale = new Vector2( 1.0f, 1.0f );
				Vector2 relativeVelocity;
				relativeVelocity = absVelocity.cpy( );
				float pitchZero = 0.5f;
				float pitchRange = -0.5f;
				float pitchExp = 1.0f;
				float volumeZero = 1.0f;
				float volumeRange = -1.0f;
				float volumeExp = 1.0f;
				relativeVelocity.x *= lineScale.x;
				relativeVelocity.y *= lineScale.y;
				float contrib = Math.max(
						Math.min( relativeVelocity.len( ), 0.0f ), 1.0f );
				sounds.setSoundPitch(
						"abslinearvelocity",
						pitchZero
								+ ( float ) Math.pow( ( pitchRange * contrib ),
										pitchExp ) );
				sounds.setSoundVolume(
						"abslinearvelocity",
						volumeZero
								+ ( float ) Math.pow(
										( volumeRange * contrib ), volumeExp ) );
			}
			if ( sounds.hasSound( "absangularvelocity" ) ) {
				float angScale = 1.0f;
				float relativeAngVelocity = absAngVelocity * angScale;
				float pitchZero = 0.5f;
				float pitchRange = -0.5f;
				float pitchExp = 1.0f;
				float volumeZero = 1.0f;
				float volumeRange = -1.0f;
				float volumeExp = 1.0f;
				float contrib = Math.max(
						Math.min( relativeAngVelocity, 0.0f ), 1.0f );
				sounds.setSoundPitch(
						"absangularvelocity",
						pitchZero
								+ ( float ) Math.pow( ( pitchRange * contrib ),
										pitchExp ) );
				sounds.setSoundVolume(
						"absangularvelocity",
						volumeZero
								+ ( float ) Math.pow(
										( volumeRange * contrib ), volumeExp ) );
			}
		}
	}

	public boolean isTimeLineMoverFinished( ) {
		if ( currentMover( ) instanceof TimelineTweenMover ) {
			return ( ( TimelineTweenMover ) currentMover( ) ).timeline
					.isFinished( );
		}
		return false;

	}

	public boolean isTimeLineMoverStarted( ) {
		if ( currentMover( ) instanceof TimelineTweenMover ) {
			return ( ( TimelineTweenMover ) currentMover( ) ).timeline
					.isStarted( );
		}
		return false;

	}

	protected String generateName( ) {
		return type.getName( );
	}

	/**
	 * Builds a sprite from a texture. If the texture is null, it attempts to
	 * load one from the XML definitions
	 * 
	 * @param texture
	 *            from which a sprite can be generated, or null, if loading
	 * @return the loaded/generated sprite, or null if neither applies
	 */
	protected Sprite constructSprite( Texture texture ) {
		Sprite sprite;
		Vector2 origin;
		boolean loadTex;
		boolean nullTex;

		// Check if the passed texture is null
		nullTex = texture == null;

		// Check if we're loading texture
		loadTex = ( nullTex && type != null && type.texture != null );

		if ( loadTex ) {
			// If we are, load it up
			texture = type.texture;
		} else if ( nullTex ) {
			// If we aren't, but the texture is still null, return null before
			// error occurs at Sprite constructor (can't pass in null)
			return null;
		}

		// Either the passed in or loaded texture defines a new Sprite
		sprite = new Sprite( texture );

		if ( loadTex ) {
			// Definitions for loaded sprites
			origin = new Vector2( type.origin.x, type.origin.y );
			sprite.setScale( type.spriteScale.x, type.spriteScale.y );
			this.offset.set( type.origin.x, type.origin.x );
		} else {
			// Definitions for non-loaded sprites
			origin = new Vector2( sprite.getWidth( ) / 2,
					sprite.getHeight( ) / 2 );

			// Arbitrary offset :(
			this.offset.set( sprite.getWidth( ) / 2, sprite.getHeight( ) / 2 );
		}
		sprite.setOrigin( origin.x, origin.y );
		return sprite;
	}

	public Sprite constructSprite( TextureRegion region ) {
		Sprite sprite;

		sprite = new Sprite( region );

		sprite.setOrigin( sprite.getWidth( ) / 2.0f, sprite.getHeight( ) / 2.0f );
		this.offset = new Vector2( sprite.getOriginX( ), sprite.getOriginY( ) );

		return sprite;
	}

	/**
	 * Builds a sprite from a TextureAtlas. If the texture is null, it attempts
	 * to load one from the XML definitions
	 * 
	 * @param texture
	 *            from which a sprite can be generated, or null, if loading
	 * @return the loaded/generated sprite, or null if neither applies
	 */
	protected Sprite constructSprite( TextureAtlas atlas ) {
		Sprite sprite;
		Vector2 origin;

		IAnimator anim;

		if ( type.animatorType.equals( "player" ) ) {
			anim = new PlayerAnimator( type.atlases, ( Player ) this );
		} else {
			anim = new SimpleFrameAnimator( ).maxFrames( type.atlases.get( 0 )
					.getRegions( ).size );
		}
		sprite = new Sprite( type.atlases, anim );
		sprite.setScale( type.spriteScale.x, type.spriteScale.y );
		origin = new Vector2( type.origin.x, type.origin.y );
		sprite.setOrigin( origin.x, origin.y );
		return sprite;
	}

	public void Move( Vector2 vector ) {
		Vector2 pos = body.getPosition( ).add( vector.mul( Util.PIXEL_TO_BOX ) );
		setPosition( pos );
	}

	/**
	 * Builds the body associated with the entity's type.
	 * 
	 * @return the loaded body, or null, if type is null
	 */
	protected Body constructBodyByType( ) {
		Body newBody;
		if ( type != null ) {
			newBody = world.createBody( type.bodyDef );
			newBody.setUserData( this );
			for ( FixtureDef fix : type.fixtureDefs ) {
				newBody.createFixture( fix );
			}
		} else {
			return null;
		}
		return newBody;
	}

	/**
	 * This function adds a mover to the entity, YOU MUST SPECIFIY WHICH STATE
	 * IT IS ASSOCIATED WITH EITHER IDLE, DOCILE, HOSTILE
	 * 
	 * This fucntions also replaces the mover associated with that robotstate,
	 * so you cannot get that old mover back
	 * 
	 * @param mover
	 *            - Imover
	 * @param robotState
	 *            - for example: RobotState.Idle
	 * @author Ranveer
	 */
	public void addMover( IMover mover, RobotState robotState ) {
		int index = robotStateMap.get( robotState );
		moverArray.set( index, mover );
	}

	public void addMover( IMover mover ) {
		addMover( mover, RobotState.IDLE );
	}

	/**
	 * Changes robotState from current to the argument
	 * 
	 * @param robotState
	 *            - for example: RobotState.IDLE
	 * @author Ranveer
	 */
	public void setCurrentMover( RobotState robotState ) {
		currentRobotState = robotState;
	}

	/**
	 * Sets the mover associated with the argument's robotstate to null.
	 * Warning, this gets rid of old mover
	 * 
	 * @param robotState
	 *            - for example: RobotState.IDLE
	 * @author Ranveer
	 */
	public void setMoverNull( RobotState robotState ) {
		int index = robotStateMap.get( robotState );
		moverArray.set( index, null );
	}

	/**
	 * Sets the current state's mover to null Warning, this gets rid of old
	 * mover
	 * 
	 * @author Ranveer
	 */
	public void setMoverNullAtCurrentState( ) {
		int index = robotStateMap.get( currentRobotState );
		moverArray.set( index, null );
	}

	/**
	 * Replaces current state's mover with the argument
	 * 
	 * @param mover
	 * @author Ranveer
	 */
	public void setMoverAtCurrentState( IMover mover ) {
		int index = robotStateMap.get( currentRobotState );
		moverArray.set( index, mover );
	}

	/**
	 * gets the current RobotState of the entity example: p.getCurrentState() ==
	 * RobotState.IDLE
	 * 
	 * @return RobotState
	 * @author Ranveer
	 */
	public RobotState getCurrentState( ) {
		return currentRobotState;
	}

	/**
	 * gets the current mover, in the current robotstate
	 * 
	 * @return IMover
	 * @author Ranveer
	 */
	public IMover currentMover( ) {
		return moverArray.get( robotStateMap.get( currentRobotState ) );
	}

	/**
	 * Determines if entity is solid, which means the player can jump off of it
	 * 
	 * @return boolean
	 */
	public boolean isSolid( ) {
		return this.solid;
	}

	/**
	 * sets entity to either solid or not, determines whether player can jump
	 * off of it
	 * 
	 * @param solid
	 *            - boolean
	 */
	public void setSolid( boolean solid ) {
		this.solid = solid;
	}

	/**
	 * Sets the energy of the current body. Energy is a new property for
	 * Entities that is meant to scale impulses. It currently does nothing, but
	 * it's here if someone wants to use it.
	 * 
	 * @param energy
	 */
	public void setEnergy( float energy ) {
		this.energy = energy;
	}

	public float getEnergy( ) {
		return energy;
	}

	/**
	 * Sets body awake, used in
	 * 
	 * @param solid
	 */
	public void setAwake( ) {
		body.setAwake( true );
	}

	/**
	 * Determines whether an entity should be deleted on next update or not
	 * 
	 * @param m
	 *            - boolean
	 */
	public void setMaintained( boolean m ) {
		maintained = m;
	}

	/**
	 * checks whether an entity is currently being maintained
	 * 
	 * @return boolean
	 */
	public boolean isMaintained( ) {
		return maintained;
	}

	/**
	 * Set visibility of both the entity and the particles.
	 * 
	 * @param v
	 *            - boolean
	 */
	public void setVisible( boolean v ) {
		visible = v;
		drawParticles = v;
	}

	/**
	 * returns whether an entity is visible, or on screen
	 * 
	 * @return boolean
	 */
	public boolean isVisible( ) {
		return visible;
	}

	/**
	 * Set drawing of the entity and the particles separately
	 * 
	 * @param isVisible
	 * @param drawParticles
	 */
	public void setVisible( boolean isVisible, boolean drawParticles ) {
		this.visible = isVisible;
		this.drawParticles = drawParticles;
	}

	public boolean isDrawingParticles( ) {
		return drawParticles;
	}

	/**
	 * Determines whether an entity should be updated or not.
	 * 
	 * @param a
	 *            - boolean
	 */
	public void setActive( boolean a ) {
		active = a;
	}

	/**
	 * checks whether if the current mover is active, or being updated or not
	 * 
	 * @return boolean
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Change the sprite to be displayed on the entity
	 * 
	 * @param newSprite
	 *            The new sprite that will be displayed on top of the entity
	 */
	public void changeSprite( Sprite newSprite ) {
		this.sprite = newSprite;
	}

	/**
	 * updates the entity's anchor
	 * 
	 * @author Edward Ramirez and Dan Malear
	 */
	public void updateAnchors( ) {
		if ( body != null ) {
			for ( Anchor anchor : anchors ) {
				anchor.setPositionBox( body.getWorldCenter( ) );
			}
		} else if ( sprite != null ) {
			for ( Anchor anchor : anchors ) {
				anchor.setPosition( new Vector2( sprite.getX( ), sprite.getY( ) ) );
			}
		} else if ( spinemator != null ) {
			for ( Anchor anchor : anchors ) {
				anchor.setPosition( spinemator.getPosition( ) );
			}
		}
	}

	/**
	 * set the bodies category collision bits
	 * 
	 * @param
	 */
	public void setCategoryMask( short category, short mask ) {
		if ( body != null ) {
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setSensor( false );
				filter = f.getFilterData( );
				// set category
				filter.categoryBits = category;
				// set mask
				filter.maskBits = mask;
				f.setFilterData( filter );
			}
		}
	}

	/**
	 * This is a quick-n-dirty fix for complex body collisions. Hopefully we'll
	 * get to a point where we don't need it. There's probably some overlap
	 * between mine and Dennis' functions, I'll try to sort it out on next
	 * update.
	 */
	public void quickfixCollisions( ) {
		Filter filter;
		for ( Fixture f : body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			// move player to another category so other objects stop
			// colliding
			filter.categoryBits = Util.CATEGORY_PLATFORMS;
			// player still collides with sensor of screw
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}

	}
	
	/**
	 * Make this entity's body collide with nothing.
	 */
	public void noCollide( ) {
		Filter filter;
		for ( Fixture f : body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			filter.categoryBits = Util.CATEGORY_IGNORE;
			filter.maskBits = Util.CATEGORY_NOTHING;
			f.setFilterData( filter );
		}

	}

	/**
	 * sets the Density of all fixtures associated with this entity
	 * 
	 * @param density
	 *            - float
	 */
	public void setDensity( float density ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setDensity( density );
		}

	}

	/**
	 * sets the friction of all fixtures associated with this entity
	 * 
	 * @param friction
	 *            - float
	 */
	public void setFriction( float friction ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setFriction( friction );
		}
	}

	/**
	 * sets the restituion of all fixtures associated with this entity
	 * 
	 * @param restitution
	 *            - float
	 */
	public void setRestitution( float restitution ) {
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setRestitution( restitution );
		}
	}

	/**
	 * sets the gravity scale of this entity
	 * 
	 * @param scale
	 *            - float
	 */
	public void setGravScale( float scale ) {
		if ( body != null ) {
			body.setGravityScale( scale );
		}
	}

	/**
	 * Return whether this entity's body is kinematic.
	 * 
	 * @author stew
	 * @return false if Dynamic static or has no body otherwise true
	 */
	public boolean isKinematic( ) {
		if ( body != null ) {
			return ( body.getType( ) == BodyType.KinematicBody );
		}
		return false;
	}

	/**
	 * Get the sprite width of this entity
	 * 
	 * @return Pixel float width of sprite
	 */
	public float getPixelWidth( ) {
		if ( sprite != null ) {
			return sprite.getWidth( );
		}
		return Float.NaN;
	}

	/**
	 * gets the type of entity
	 */
	public EntityType getEntityType( ) {
		return entityType;
	}

	/**
	 * Get the sprite height of this entity
	 * 
	 * @return Pixel float height of sprite
	 */
	public float getPixelHeight( ) {
		if ( sprite != null ) {
			return sprite.getHeight( );
		}
		return Float.NaN;
	}

	/**
	 * Get the sprite meter width of this entity
	 * 
	 * @return METER float width of sprite
	 */
	public float getMeterWidth( ) {
		if ( sprite != null ) {
			return sprite.getWidth( ) * Util.PIXEL_TO_BOX;
		}
		return Float.NaN;
	}

	/**
	 * Get the sprite METER height of this entity
	 * 
	 * @return METER float height of sprite
	 */
	public float getMeterHeight( ) {
		if ( sprite != null ) {
			return sprite.getHeight( ) * Util.PIXEL_TO_BOX;
		}
		return Float.NaN;
	}

	/**
	 * for debug
	 * 
	 * @author stew
	 */
	public String printDebug( ) {
		return "Entity[" + name + "] pos:" + body.getPosition( )
				+ ", body.active:" + body.isActive( ) + ", body.awake:"
				+ body.isAwake( );
	}

	/**
	 * Adds an anchor to the Entity AND ADDS IT TO ANCHORLIST.
	 * 
	 * @param anchor
	 */
	public void addAnchor( Anchor anchor ) {
		AnchorList.getInstance( ).addAnchor( anchor );
		this.anchors.add( anchor );
	}

	/**
	 * Removes an anchor by reference AND DELETES IT FRM ANCHORLIST.
	 * 
	 * @param anchor
	 */
	public void removeAnchor( Anchor anchor ) {
		AnchorList.getInstance( ).removeAnchor( anchor );
		int index = this.anchors.indexOf( anchor );
		this.anchors.remove( index );
	}

	/**
	 * Removes an anchor by index (not recommended). ALSO DELETES IT FROM
	 * ANCHORLIST.
	 * 
	 * @param index
	 */
	public void removeAnchor( int index ) {
		Anchor anchor = this.anchors.get( index );
		this.removeAnchor( anchor );
	}

	/**
	 * Clears out all the anchors AND REMOVES THEM FROM ANCHORLIST.
	 */
	public void clearAnchors( ) {
		for ( Anchor anchor : anchors ) {
			AnchorList.getInstance( ).removeAnchor( anchor );
		}
		this.anchors.clear( );
	}

	/**
	 * Sets up moverArray and fills it with null and set up the EnumMap Idle = 0
	 * Docile = 1 Hostile = 2
	 * 
	 * and sets this entity's default state as IDLE
	 * 
	 * Will be optimized soon
	 * 
	 * @author Ranveer
	 */
	private void setUpRobotState( ) {
		moverArray = new ArrayList< IMover >( );
		for ( int i = 0; i < INITAL_CAPACITY; ++i )
			moverArray.add( null );
		robotStateMap = new EnumMap< RobotState, Integer >( RobotState.class );
		robotStateMap.put( RobotState.IDLE, 0 );
		robotStateMap.put( RobotState.DOCILE, 1 );
		robotStateMap.put( RobotState.HOSTILE, 2 );
		// robotStateMap.put( RobotState.CUSTOM1, 3 );
		// robotStateMap.put( RobotState.CUSTOM2, 4 );
		// robotStateMap.put( RobotState.CUSTOM3, 5 );

		// Initalize to idle
		currentRobotState = RobotState.IDLE;
	}

	private void insertDecal( boolean isBG, Sprite s, Vector2 offset,
			float angle, boolean isBack ) {
		if ( s == null ) {
			throw new RuntimeException(
					"Entity.insertDecal(): Adding null sprite! NO!" );
		}
		ArrayList< Sprite > decalList = ( isBG ) ? bgDecals : fgDecals;
		ArrayList< Float > angleList = ( isBG ) ? bgDecalAngles : fgDecalAngles;
		ArrayList< Vector2 > offsetList = ( isBG ) ? bgDecalOffsets
				: fgDecalOffsets;
		if ( isBack ) {
			decalList.add( 0, s );
			angleList.add( 0, angle );
			offsetList.add( 0, offset );
		} else {
			decalList.add( s );
			angleList.add( angle );
			offsetList.add( offset );
		}
	}

	public void addBGDecal( Sprite s, Vector2 offset, float angle ) {
		insertDecal( true, s, offset, angle, false );
	}

	/**
	 * @param angle
	 *            in radian
	 */
	public void addFGDecal( Sprite s, Vector2 offset, float angle ) {
		insertDecal( false, s, offset, angle, false );
	}

	public void addBGDecal( Sprite s, Vector2 offset ) {
		addBGDecal( s, offset, 0.0f );
	}

	public void addFGDecal( Sprite s, Vector2 offset ) {
		addFGDecal( s, offset, 0.0f );
	}

	public void addFGDecalBack( Sprite s, Vector2 offset ) {
		insertDecal( false, s, offset, 0.0f, true );
	}

	public void addBGDecalBack( Sprite s, Vector2 offset ) {
		insertDecal( true, s, offset, 0.0f, true );
	}

	public void addBGDecal( Sprite s ) {
		addBGDecal( s, new Vector2( s.getX( ), s.getY( ) ) );
	}

	public void addFGDecal( Sprite s ) {
		addFGDecal( s, new Vector2( s.getX( ), s.getY( ) ) );
	}

	public void clearAllDecals( ) {
		fgDecalAngles.clear( );
		fgDecalOffsets.clear( );
		fgDecals.clear( );
		bgDecalAngles.clear( );
		bgDecalOffsets.clear( );
		bgDecals.clear( );
	}

	public void updateDecals( float deltaTime ) {
		Vector2 bodyPos = this.getPositionPixel( );
		float angle = this.getAngle( );
		if ( bodyPos != oldPos
				|| angle != oldAngle || this.currentMover( ) != null
				|| ( this.getParentSkeleton( ) != null && ( this
						.getParentSkeleton( ).hasMoved( ) || this
						.getParentSkeleton( ).hasRotated( )
						|| this.getParentSkeleton( ).currentMover( ) != null ) ) ) {
			oldPos = bodyPos;
			oldAngle = angle;
			float cos = ( float ) Math.cos( angle ), sin = ( float ) Math
					.sin( angle );
			float x, y, r;
			Vector2 offset;
			Sprite decal;
			float a = angle * Util.RAD_TO_DEG;
			for ( int i = 0; i < fgDecals.size( ); i++ ) {
				offset = fgDecalOffsets.get( i );
				decal = fgDecals.get( i );
				r = fgDecalAngles.get( i );
				x = bodyPos.x + ( ( offset.x ) * cos ) - ( ( offset.y ) * sin );
				y = bodyPos.y + ( ( offset.y ) * cos ) + ( ( offset.x ) * sin );
				decal.setPosition( x + decal.getOriginX( ),
						y + decal.getOriginY( ) );
				decal.setRotation( r + a );
			}
			for ( int i = 0; i < bgDecals.size( ); i++ ) {
				offset = bgDecalOffsets.get( i );
				decal = bgDecals.get( i );
				r = bgDecalAngles.get( i );
				x = bodyPos.x + ( ( offset.x ) * cos ) - ( ( offset.y ) * sin );
				y = bodyPos.y + ( ( offset.y ) * cos ) + ( ( offset.x ) * sin );
				decal.setPosition( x + decal.getOriginX( ),
						y + decal.getOriginY( ) );
				decal.setRotation( r + a );
			}
		}
	}

	/**
	 * 
	 * @param batch
	 * @param camera
	 */
	public void drawFGDecals( SpriteBatch batch, Camera camera ) {
		for ( Sprite decal : fgDecals ) {
			if ( decal.alpha >= 0.25 ) {
				if ( decal.getBoundingRectangle( )
						.overlaps( camera.getBounds( ) ) ) {
					decal.draw( batch );
				}
			}
		}
	}

	/**
	 * 
	 * @param batch
	 * @param camera
	 */
	public void drawBGDecals( SpriteBatch batch, Camera camera ) {
		for ( Sprite decal : bgDecals ) {
			if ( decal.getBoundingRectangle( ).overlaps( camera.getBounds( ) ) ) {
				decal.draw( batch );
			}
		}
	}

	public int getFGListSize( ) {
		return fgDecals.size( );
	}

	public int getBGListSize( ) {
		return fgDecals.size( );
	}

	/**
	 * 
	 * @param spriteBounds
	 * @param camBounds
	 * @param spritePos
	 * @param cameraPos
	 * @return
	 */
	protected boolean insideRadius( Rectangle spriteBounds,
			Rectangle camBounds, Vector2 spritePos, Vector3 cameraPos ) {
		float magDecalRadiusX = ( spriteBounds.width / 2.0f
				* spriteBounds.width / 2.0f );
		float magDecalRadiusY = ( spriteBounds.height / 2.0f
				* spriteBounds.height / 2.0f );
		float magDecalRadius = magDecalRadiusX + magDecalRadiusY;

		float magCamRadiusX = ( camBounds.width / 2.0f * camBounds.width / 2.0f );
		float magCamRadiusY = ( camBounds.height / 2.0f * camBounds.height / 2.0f );
		float magCamRadius = magCamRadiusX + magCamRadiusY;

		float dx = cameraPos.x - spritePos.x;
		float dy = cameraPos.y - spritePos.y;
		float mag = dx * dx + dy * dy;
		float sumRadius = magDecalRadius + magCamRadius;
		if ( sumRadius >= mag ) {
			return true;
		}
		return false;
	}

	public float getAngle( ) {
		if ( body != null )
			return body.getAngle( );
		return sprite.getRotation( );
	}

	/**
	 * prints Fixture's index in FixtureList.
	 * 
	 * @param fix
	 *            Fixture
	 */
	public void getFixtureIndex( Fixture fix ) {
		for ( int i = 0; i < body.getFixtureList( ).size( ); i++ ) {
			if ( fix == body.getFixtureList( ).get( i ) ) {
				// Gdx.app.log( name + " FixtureListIndex: ", "" + i );
				return;
			}
		}
	}

	public Skeleton getParentSkeleton( ) {
		return parentSkeleton;
	}

	public void setParentSkeleton( Skeleton parentSkeleton ) {
		this.parentSkeleton = parentSkeleton;
	}

	/**
	 * returns whether an entity can crush the player
	 * 
	 * @return boolean
	 */
	public boolean getCrushing( ) {
		return crushing;
	}

	/**
	 * sets flag to determine if an entity can crush, also turns off OneSided
	 * 
	 * @param value
	 *            boolean
	 */
	public void setCrushing( boolean value ) {
		crushing = value;
	}

	/**
	 * Careful. You generally won't directly call this. Root skeleton can delete
	 * entire skeletons so it would be better to just use
	 * RootSkeleton.destroySkeleton()
	 * 
	 * @author stew
	 */
	public void dispose( ) {
		body.getWorld( ).destroyBody( body );
		sounds.dispose( );
	}

	public void setGroupIndex( short index ) {
		Filter filter = new Filter( );
		filter.groupIndex = index;
		if ( body != null ) {
			for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
				body.getFixtureList( ).get( i ).setFilterData( filter );
		}
	}

	public ParticleEffect addBehindParticleEffect( String name,
			boolean removeOnComplete, boolean updateWithParent ) {
		if ( behindParticles == null ) {
			behindParticles = new ArrayHash< String, ParticleEffect >( );
		}
		return addParticleEffect( name, behindParticles, removeOnComplete,
				updateWithParent );
	}

	public ParticleEffect addFrontParticleEffect( String name,
			boolean removeOnComplete, boolean updateWithParent ) {
		if ( frontParticles == null ) {
			frontParticles = new ArrayHash< String, ParticleEffect >( );
		}
		return addParticleEffect( name, frontParticles, removeOnComplete,
				updateWithParent );
	}

	/**
	 * Polymorphic method to add particle effects. Used by multiple public
	 * methods.
	 * 
	 * @author stew
	 */
	private ParticleEffect addParticleEffect( String name,
			ArrayHash< String, ParticleEffect > map, boolean removeOnComplete,
			boolean updateWithParent ) {
		ParticleEffect effect = WereScrewedGame.manager
				.getParticleEffect( name );
		effect.removeOnComplete = removeOnComplete;
		effect.updatePositionOnUpdate = updateWithParent;
		map.put( name, effect );
		return effect;
	}

	/**
	 * @param name
	 *            of particle effect
	 * @return Returns the first effect with the given name.
	 * @author stew
	 */
	public ParticleEffect getEffect( String name, int index ) {
		ParticleEffect out = null;
		if ( behindParticles != null )
			out = behindParticles.get( name, index );
		if ( out == null && frontParticles != null ) {
			out = frontParticles.get( name, index );
			if ( out == null ) {
				throw new NullPointerException(
						"No particle effect exists with name: " + name );
			}
		}
		return out;
	}

	/**
	 * @param name
	 *            of particle effect
	 * @return Returns the first effect with the given name.
	 */
	public ParticleEffect getEffect( String name ) {
		return getEffect( name, 0 );
	}

	public void setSoundManager( SoundManager s ) {
		sounds = s;
	}

	public boolean hasSoundManager( ) {
		return sounds != null;
	}

	public SoundManager getSoundManager( ) {
		return sounds;
	}

	// Idle sound
	public void idleSound( ) {
		if ( sounds != null && sounds.hasSound( "idle" ) ) {
			SoundRef ref = sounds.getSound( "idle" );
			ref.setType( SoundType.NOISE );
			ref.setVolume( 0.0f );
			ref.loop( false );
			// Gdx.app.log( name, "Starting Idle Sound" );
		}
	}

	/**
	 * Add a fixture from a gleed path. This ignores the last vert.
	 * 
	 * @param loadedVerts
	 *            are world coordinate vertices.
	 * @param positionPixel
	 */
	public void addFixture( Array< Vector2 > loadedVerts, Vector2 positionPixel ) {

		PolygonShape polygon = new PolygonShape( );
		Vector2[ ] verts = new Vector2[ loadedVerts.size - 1 ];

		// MAKE SURE START POINT IS IN THE MIDDLE
		// AND SECOND AND END POINT ARE THE SAME POSITION
		int i = 0;
		for ( int j = 0; j < loadedVerts.size; j++ ) {
			if ( j == loadedVerts.size - 1 )
				continue;
			Vector2 v = loadedVerts.get( j );
			verts[ i ] = new Vector2(
					( v.x + positionPixel.x - getPositionPixel( ).x )
							* Util.PIXEL_TO_BOX,
					( v.y + positionPixel.y - getPositionPixel( ).y )
							* Util.PIXEL_TO_BOX );
			++i;
		}
		polygon.set( verts );

		FixtureDef fixtureDef = new FixtureDef( );
		fixtureDef.shape = polygon;

		body.createFixture( fixtureDef );
		polygon.dispose( );

		// Possible bug, changes all collision bits to that of the first body
		Filter data = body.getFixtureList( ).get( 0 ).getFilterData( );
		Filter filter;
		for ( Fixture f : body.getFixtureList( ) ) {
			filter = f.getFilterData( );
			// move player to another category so other objects stop
			// colliding
			filter.categoryBits = data.categoryBits;
			// player still collides with sensor of screw
			filter.maskBits = data.maskBits;
			f.setFilterData( filter );
		}
		body.setType( body.getType( ) );

		//if ( entityType == EntityType.PLATFORM ) {
		//	Platform p = ( Platform ) this;
		//}
	}

	// Call this function after all your sprites, sounds, etc. are loaded.
	public void postLoad( ) {
		idleSound( );
	}

	public void collide( Object that, Contact contact ) {
		this.collide( );
	}

	// This function handles collision when we do not care what the other object
	// is.
	public void collide( ) {
		if ( sounds != null && sounds.hasSound( "collision" ) ) {
			sounds.playSound( "collision" );
		}
	}

	public boolean hasDecals( ) {
		return ( fgDecals.size( ) > 0 || bgDecals.size( ) > 0 );
	}

	protected static final float MIN_LINEAR = 0.1f;
	protected static final float MIN_ANGULAR = 0.5f;
	protected static final float MOVEMENT_SOUND_DELAY = 0.05f;

	public void handleMovementSounds( float dT ) {
		Vector2 soundPos = getPositionPixel( );
		float vol;
		float pitch;
		String soundTag;
		int soundId;
		Vector2 vel = body.getLinearVelocity( );
		float aVel = body.getAngularVelocity( );
		// horizontal
		if ( vel.x > 0.0f ) {
			soundTag = "left";
		} else {
			soundTag = "right";
		}
		if ( sounds.hasSound( soundTag ) ) {
			vol = Math.abs( vel.x )
					* sounds.calculatePositionalVolume( soundTag, soundPos,
							Camera.CAMERA_RECT );
			soundId = sounds.randomSoundId( soundTag );
			pitch = sounds
					.getPitchInRange( soundTag, soundId, Math.abs( vel.x ) );
			if ( vol > MIN_LINEAR ) {
				sounds.playSound( soundTag, soundId, MOVEMENT_SOUND_DELAY, vol,
						pitch );
			}
		}
		// vertical
		if ( vel.y > 0.0f ) {
			soundTag = "up";
		} else {
			soundTag = "down";
		}
		if ( sounds.hasSound( soundTag ) ) {
			vol = Math.abs( vel.y )
					* sounds.calculatePositionalVolume( soundTag, soundPos,
							Camera.CAMERA_RECT );
			soundId = sounds.randomSoundId( soundTag );
			pitch = sounds
					.getPitchInRange( soundTag, soundId, Math.abs( vel.y ) );
			if ( vol > MIN_LINEAR ) {
				sounds.playSound( soundTag, soundId, MOVEMENT_SOUND_DELAY, vol,
						pitch );
			}
		}
		// angular
		if ( aVel > 0.0f ) {
			soundTag = "ccw";
		} else {
			soundTag = "cw";
		}
		if ( sounds.hasSound( soundTag ) ) {
			vol = Math.abs( aVel )
					* sounds.calculatePositionalVolume( soundTag, soundPos,
							Camera.CAMERA_RECT );
			soundId = sounds.randomSoundId( soundTag );
			pitch = sounds
					.getPitchInRange( soundTag, soundId, Math.abs( aVel ) );
			if ( vol > MIN_LINEAR ) {
				sounds.playSound( soundTag, soundId, MOVEMENT_SOUND_DELAY, vol,
						pitch );
			}
		}
	}

	public ISpinemator getSpinemator( ) {
		return spinemator;
	}

	public void setSpinemator( ISpinemator spinemator ) {
		this.spinemator = spinemator;
	}

	/**
	 * A sudo virtual function that inheriting classes can override and add
	 * whatever reset code
	 * 
	 * @author stew
	 */
	public void reset( ) {

	}

}