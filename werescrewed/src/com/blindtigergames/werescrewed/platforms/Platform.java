package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

/**
 * @param name
 * 
 * 
 * @author Ranveer
 * 
 */

public class Platform extends Entity {

	protected enum PlatformType{PLATFORM,TILED,COMPLEX,SHAPE};
	
	IMover mover;

	protected float width, height;
	protected boolean dynamicType = false;
	protected boolean rotate = false;
	protected boolean oneSided = false;
	protected ArrayList< Screw > screws;
	// tileConstant is 16 for setasbox function which uses half width/height
	// creates 32x32 objects
	protected final int tileConstant = 16;
	
	protected PlatformType platType;

	/**
	 * Used for kinematic movement connected to skeleton
	 */
	protected Vector2 origin;
	
	/**
	 * Used for kinematic movement connected to skeleton
	 */
	protected Vector2 localPosition;
	protected float localRotation; // in radians

	public Platform( String name, Vector2 pos, Texture tex, World world ) {
		super( name, pos, tex, null, true );
		this.world = world;
		screws = new ArrayList< Screw >( );
		init(pos);
	}

	public Platform( String name, EntityDef type, World world, Vector2 pos, float rot,
			Vector2 scale ) {
		super( name, type, world, pos, rot, scale, null, true );
		screws = new ArrayList< Screw >( );
		init(pos);
	}

	public Platform( String name, EntityDef type, World world, Vector2 pos, float rot,
			Vector2 scale, Texture tex ) {
		super( name, type, world, pos, rot, scale, tex, true );
		screws = new ArrayList< Screw >( );
		init(pos);
	}
	
	void init(Vector2 pos){
		localPosition = pos.mul( Util.PIXEL_TO_BOX );
		localRotation = 0;
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

		body.setActive( true );

		super.update( deltaTime );

		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			setOneSided( !getOneSided( ) );
			System.out.println( getOneSided( ) );
		}
		for ( Screw s : screws ) {
			s.update( deltaTime );
		}
	}

	public void changeType( ) {
		dynamicType = !dynamicType;
		if ( dynamicType ) {
			body.setType( BodyType.DynamicBody );
			FixtureDef fix = new FixtureDef( );
			fix.filter.categoryBits = Util.DYNAMIC_OBJECTS; 
			fix.filter.maskBits = -1;
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setFilterData( fix.filter );
			}
		} else {
			body.setType( BodyType.KinematicBody );
			FixtureDef fix = new FixtureDef( );
			fix.filter.categoryBits = Util.KINEMATIC_OBJECTS; 
			fix.filter.maskBits = -1;
			for ( Fixture f : body.getFixtureList( ) ) {
				f.setFilterData( fix.filter );
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
	 * Set the position and angle of the kinematic platform based on the parent
	 * skeleton's pos/rot. Use originPos & originalLocalRot
	 * 
	 * @param skeleton
	 * @author stew
	 */
	public void setPosRotFromSkeleton( Skeleton skeleton ) {
		// originPos has already been updated by it's IMover by this point
		// TODO: modify this if imover uses pixels or box2d meters
		float radiusFromSkeleton = localPosition.len( );
		// update angle between platform and skeleton
		float newAngleFromSkeleton = skeleton.body.getAngle( )
				+ Util.angleBetweenPoints( Vector2.Zero, localPosition );
		Vector2 skeleOrigin = skeleton.body.getPosition( );
		
		float newRotation = localRotation + skeleton.body.getAngle( );
		Vector2 newPos = Util.PointOnCircle( radiusFromSkeleton,
				newAngleFromSkeleton, skeleOrigin );
		
		body.setTransform( newPos, newRotation );
		//Gdx.app.log( "Platform['"+name+"']", "newPos: "+newPos );
	}
}