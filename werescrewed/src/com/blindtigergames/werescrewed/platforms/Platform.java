package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Platform
 * Mostly just an inherited class, but complex platform uses that as it's main class
 * @author Ranveer / Stew
 * 
 */

public class Platform extends Entity {
	
	
	//============================================
	// Fields
	//============================================
	protected float width, height;
	protected boolean dynamicType = false;
	protected boolean rotate = false;
	protected boolean oneSided = false;
	protected boolean moveable = false;
	protected ArrayList< Screw > screws;
	// tileConstant is 16 for setasbox function which uses half width/height
	// creates 32x32 objects
	protected final int tileConstant = 16;
	
	protected PlatformType platType;
	
	/**
	 * Used for kinematic movement connected to skeleton
	 */
	protected Vector2 localPosition; //in pixels
	protected float localRotation; // in radians

	
	//============================================
	// Constructors
	//============================================
	
	/**
	 * General purpose platform constructor for things that don't use an entitydef.
	 * Currently used by PlatformBuilder and Tiled Platform
	 * @param name
	 * @param pos
	 * @param tex
	 * @param world
	 */
	public Platform( String name, Vector2 pos, Texture tex, World world ) {
		super( name, pos, tex, null, true );
		this.world = world;
		init(pos);
	}

<<<<<<< HEAD
	public Platform( String name, EntityDef type, World world, Vector2 pos, float rot,
			Vector2 scale, float anchRadius ) {
		super( name, type, world, pos, rot, scale, null, true, anchRadius );
		screws = new ArrayList< Screw >( );
	}

	public Platform( String name, EntityDef type, World world, Vector2 pos, float rot,
			Vector2 scale, float anchRadius, Texture tex ) {
		super( name, type, world, pos, rot, scale, tex, true, anchRadius );
=======
	/**
	 * Construct platforms using an EntityDef. This is used by PlatformBuilder.buildComplexBody()
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
		init(pos);
	}
	
	/**
	 * Initialize things.
	 * @author stew
	 * @param pos
	 */
	void init(Vector2 pos){
>>>>>>> origin/master
		screws = new ArrayList< Screw >( );
		localPosition = pos.cpy( );//pos.mul( Util.PIXEL_TO_BOX );
		localRotation = 0;
		platType = PlatformType.DEFAULT; //set to default unless subclass sets it later in a constructor
	}

	
	//============================================
	// Methods
	//============================================
	
	/**
	 * return localPosition Vector2 in PIXELS.
	 * @return
	 */
	public Vector2 getLocalPos(){
		return localPosition;
	}
	
	/**
	 * set localPosition Vector2 in PIXELS!!!
	 * @param newLocalPos in PIXELS
	 */
	public void setLocalPos( Vector2 newLocalPosPixel ){
		localPosition.x = newLocalPosPixel.x;
		localPosition.y = newLocalPosPixel.y;
	}
	
	public void setLocalPos( float xPixel, float yPixel ){
		localPosition.x = xPixel;
		localPosition.y = yPixel;
	}
	
	/**
	 * returns local rotation in RADIANS
	 */
	public float getLocalRot(){
		return localRotation;
	}
	
	/**
	 * set local rotation in RADIAN
	 * @param newLocalRotRadians
	 */
	public void setLocalRot( float newLocalRotRadians ){
		localRotation = newLocalRotRadians;
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

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		body.setActive( true );
		body.setAwake( true );
		for ( Screw s : screws ) {
			s.update( deltaTime );
		}
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

		//TODO: Why de-activate this??
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
	 * @return PLATFORMTYPE
	 */
	public PlatformType getPlatformType(){
		return platType;
	}
	
	/**
	 * Set this platforms type!!
	 * @author stew
	 * @param newPlatformType
	 */
	public void setPlatformType(PlatformType newPlatformType){
		platType = newPlatformType;
	}
	
	/**
	 * Set the position and angle of the kinematic platform based on the parent
	 * skeleton's pos/rot. Use originPos & originalLocalRot
	 * 
	 * @param skeleton
	 * @author stew
	 */
	public void setPosRotFromSkeleton( Skeleton skeleton ) {
		// originPos has already been updated by it's IMover by this point
		// TODO: modify this if imover uses pixels or box2d meters
		float radiusFromSkeleton = localPosition.cpy( ).mul( Util.PIXEL_TO_BOX ).len( );
		// update angle between platform and skeleton
		float newAngleFromSkeleton = skeleton.body.getAngle( )
				+ Util.angleBetweenPoints( Vector2.Zero, localPosition );
		Vector2 skeleOrigin = skeleton.body.getPosition( );
		
		float newRotation = localRotation + skeleton.body.getAngle( );
		Vector2 newPos = Util.PointOnCircle( radiusFromSkeleton,
				newAngleFromSkeleton, skeleOrigin );
		
		body.setTransform( newPos, newRotation );
	}
}