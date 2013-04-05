package com.blindtigergames.werescrewed.entity.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Platform Mostly just an inherited class, but complex platform uses that as
 * it's main class
 * 
 * @author Ranveer / Stew
 * 
 */

public class Platform extends Entity {

	// ============================================
	// Fields
	// ============================================
	protected float width, height;
	protected boolean dynamicType = false;
	protected boolean rotate = false;
	public boolean oneSided = false;
	public boolean moveable = false;
	protected ArrayList< Screw > screws;
	// tileConstant is 16 for setasbox function which uses half width/height
	// creates 32x32 objects
	protected final int tileConstant = 16;

	protected PlatformType platType;

	/**
	 * Used for kinematic movement connected to skeleton. Pixels.
	 */
	protected Vector2 localPosition; // in pixels, local coordinate system
	private float localRotation; // in radians, local rot system
	protected Vector2 localLinearVelocity; // in meters/step
	protected float localAngularVelocity; //
	protected Vector2 originPosition; // world position that this platform
										// spawns
										// at, in pixels

	private Vector2 originRelativeToSkeleton; // box meters
	

	// ============================================
	// Constructors
	// ============================================

	/**
	 * General purpose platform constructor for things that don't use an
	 * entitydef. Currently used by PlatformBuilder and Tiled Platform
	 * 
	 * @param name
	 * @param pos
	 * @param tex
	 * @param world
	 */
	public Platform( String name, Vector2 pos, Texture tex, World world ) {
		super( name, pos, tex, null, true );
		this.world = world;
		entityType = EntityType.PLATFORM;
		init( pos );
	}

	/**
	 * Construct platforms using an EntityDef. This is used by
	 * PlatformBuilder.buildComplexBody()
	 * 
	 * @param name
	 * @param type
	 * @param world
	 * @param pos
	 * @param rot
	 * @param scale
	 */
	public Platform( String name, EntityDef type, World world, Vector2 pos,
			float rot, Vector2 scale ) {
		super( name, type, world, pos, rot, scale, null, true );
		entityType = EntityType.PLATFORM;
		init( pos );
	}
	
	/**
	 * Loading a Complex platform, or used to load complex Hazard
	 * 
	 * (no scale or rotation because its defined in entitydef)
	 * @param name
	 * @param type
	 * @param world
	 * @param pos
	 */
	
	public Platform( String name, EntityDef type, World world, Vector2 pos) {
		super( name, type, world, pos, null);
		entityType = EntityType.PLATFORM;
		init( pos );
	}

	/**
	 * Initialize things.
	 * 
	 * @author stew
	 * @param pos
	 */
	void init( Vector2 pos ) {
		screws = new ArrayList< Screw >( );
		localPosition = new Vector2( 0, 0 );
		localLinearVelocity = new Vector2( 0, 0 );
		localRotation = 0;
		originPosition = pos.cpy( );
		platType = PlatformType.DEFAULT; // set to default unless subclass sets
											// it later in a constructor
		originRelativeToSkeleton = new Vector2();
	}

	// ============================================
	// Methods
	// ============================================

	/**
	 * return localPosition Vector2 in PIXELS.
	 * 
	 * @return
	 */
	public Vector2 getLocalPos( ) {
		return localPosition;
	}

	/**
	 * set localPosition Vector2 in PIXELS!!!
	 * 
	 * @param newLocalPos
	 *            in PIXELS
	 */
	public void setLocalPos( Vector2 newLocalPosPixel ) {
		setLocalPos( newLocalPosPixel.x, newLocalPosPixel.y );
	}

	public void setLocalPos( float xPixel, float yPixel ) {
		localPosition.x = xPixel;
		localPosition.y = yPixel;
	}

	/**
	 * returns local rotation in RADIANS
	 */
	public float getLocalRot( ) {
		return localRotation;
	}

	/**
	 * set local rotation in RADIAN
	 * 
	 * @param newLocalRotRadians
	 */
	public void setLocalRot( float newLocalRotRadians ) {
		localRotation = newLocalRotRadians;
	}

	/**
	 * return originPosition Vector2 in PIXELS.
	 * 
	 * @return
	 */
	public Vector2 getOriginPos( ) {
		return originPosition;
	}

	/**
	 * set Origin Position Vector2 in PIXELS!!!
	 * 
	 * @param newLocalPos
	 *            in PIXELS
	 */
	public void setOriginPos( Vector2 newOriginPosPixel ) {
		originPosition.x = newOriginPosPixel.x;
		originPosition.y = newOriginPosPixel.y;
	}

	public void setOriginPos( float xPixel, float yPixel ) {
		originPosition.x = xPixel;
		originPosition.y = yPixel;
	}

	public Vector2 getLocLinearVel( ) {
		return localLinearVelocity;
	}

	public void setLocLinearVel( Vector2 linVelMeters ) {
		localLinearVelocity = linVelMeters.cpy( );
	}

	public void setLocLinearVel( float xMeter, float yMeter ) {
		localLinearVelocity.x = xMeter;
		localLinearVelocity.y = yMeter;
	}

	public float getLocAngularVel( ) {
		return localAngularVelocity;
	}

	public void setLocAngularVel( float angVelMeter ) {
		localAngularVelocity = angVelMeter;
	}

	public void addScrew( Screw s ) {
		screws.add( s );
	}

	@Override
	public void setAwake( ) {
		body.setAwake( true );
		for ( Screw s : screws )
			s.body.setAwake( true );
	}

	@SuppressWarnings( "unused" )
	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		for ( Screw s : screws ) {
			s.update( deltaTime );
		}
		if ( removeNextStep ){
			remove( );
		}
	}

	/**
	 * removes the bodies and joints
	 */
	@Override
	public void remove( ) {
		for ( Screw s : screws ) {
			s.remove( );
		}
		super.remove( );
	}

	/**
	 * Swap from kinematic to dynamic.
	 */
	public void changeType( ) {
		dynamicType = !dynamicType;
		if ( dynamicType ) {
			body.setType( BodyType.DynamicBody );
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.DYNAMIC_OBJECTS;
				// player now collides with everything
				filter.maskBits = Util.CATEGORY_EVERYTHING;
				f.setFilterData( filter );
			}
		} else {
			body.setType( BodyType.KinematicBody );
			Filter filter = new Filter( );
			for ( Fixture f : body.getFixtureList( ) ) {
				filter = f.getFilterData( );
				// move player back to original category
				filter.categoryBits = Util.KINEMATIC_OBJECTS;
				// player now collides with everything
				filter.maskBits = Util.CATEGORY_EVERYTHING;
				f.setFilterData( filter );
			}
		}

		body.setActive( false );
	}

	// This function sets the platform to 180* no matter what angle it currently
	// is
	public void setHorizontal( ) {
		body.setTransform( body.getPosition( ), ( float ) Math.toRadians( 90 ) );
	}

	// This function sets platform to 90*
	public void setVertical( ) {
		body.setTransform( body.getPosition( ), ( float ) Math.toRadians( 180 ) );
	}

	public boolean getOneSided( ) {
		return oneSided;
	}

	public void setOneSided( boolean value ) {
		oneSided = value;
	}

	protected void rotate( ) {
		body.setAngularVelocity( 1f );
	}

	protected void rotateBy90( ) {
		float bodyAngle = body.getAngle( );
		body.setTransform( body.getPosition( ), bodyAngle + 90 );
	}

	/**
	 * Returns the private member platform type for casting or whatever
	 * 
	 * @return PLATFORMTYPE
	 */
	public PlatformType getPlatformType( ) {
		return platType;
	}

	/**
	 * Set this platforms type!!
	 * 
	 * @author stew
	 * @param newPlatformType
	 */
	public void setPlatformType( PlatformType newPlatformType ) {
		platType = newPlatformType;
	}

	/**
	 * Set the position and angle of the kinematic platform based on the parent
	 * skeleton's pos/rot. Now better than ever!
	 * 
	 * @param frameRate which is typically 1/deltaTime.
	 * @param skeleton
	 * 
	 * @author stew
	 */
	public void setTargetPosRotFromSkeleton( float frameRate, Skeleton skeleton ) {
		if ( skeleton != null ){
		Vector2 posOnSkeleLocalMeter = originRelativeToSkeleton.cpy( ).add(
				 localPosition.cpy().mul( Util.PIXEL_TO_BOX ) );
		float radiusFromSkeletonMeters = posOnSkeleLocalMeter.len( );
		float newAngleFromSkeleton = skeleton.body.getAngle( )
				+ Util.angleBetweenPoints( Vector2.Zero, posOnSkeleLocalMeter );

		Vector2 targetPosition = Util.PointOnCircle(
				radiusFromSkeletonMeters, newAngleFromSkeleton,
				skeleton.getPosition( ) ).sub(body.getPosition( ));
		float targetRotation = localRotation + skeleton.body.getAngle( ) - body.getAngle( );
		
		body.setLinearVelocity( targetPosition.mul( frameRate ) );
		body.setAngularVelocity(  targetRotation * frameRate );
		}
	}

	@Override
	public void setCrushing( boolean value ) {
		crushing = value;
		oneSided = false;
	}

	

	public Vector2 getOriginRelativeToSkeleton( ) {
		return originRelativeToSkeleton;
	}

	public void setOriginRelativeToSkeleton( Vector2 originRelativeToSkeleton ) {
		this.originRelativeToSkeleton = originRelativeToSkeleton;
	}


	public void constructBodyFromVerts( Array<Vector2> loadedVerts, Vector2 positionPixel ){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ));
		body = world.createBody( bodyDef );
		
		PolygonShape polygon = new PolygonShape();
		Vector2[] verts = new Vector2[loadedVerts.size -1];

		//MAKE SURE START POINT IS IN THE MIDDLE
		//AND SECOND AND END POINT ARE THE SAME POSITION
		int i = 0;
		for(int j = 0; j < loadedVerts.size; j++){
			if(j == loadedVerts.size - 1) continue;
			Vector2 v = loadedVerts.get( j );
			verts[i] = new Vector2(v.x * Util.PIXEL_TO_BOX, v.y * Util.PIXEL_TO_BOX);
			++i;
		}
		polygon.set( verts );
		
		FixtureDef fixture = new FixtureDef( );
		fixture.shape = polygon;
		
		body.createFixture( fixture );
		body.setUserData( this );

		polygon.dispose( );
	}

}