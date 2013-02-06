package com.blindtigergames.werescrewed.screws;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class BossScrew extends Screw {

	public BossScrew( String name, Vector2 pos, int max, Texture tex,
			Entity platform, Skeleton skeleton ) {
		super( name, pos, tex);
		maxDepth = max;
		depth = max;

		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape.setRadius( ( sprite.getWidth( ) / 2.0f )
				* Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS; 
		screwFixture.filter.maskBits = Util.CATEGORY_PLAYER | Util.CATEGORY_SUBPLAYER;
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );
		
		// add radar sensor to screw
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 2 );
		FixtureDef radarFixture = new FixtureDef( );
		radarFixture.shape = radarShape;
		radarFixture.isSensor = true;
		radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS; 
		radarFixture.filter.maskBits = Util.CATEGORY_PLAYER | Util.CATEGORY_SUBPLAYER;
		radarShape.dispose( );
		body.createFixture( radarFixture );

		// connect the screw to the platform;
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( this.body, skeleton.body,
				this.body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		platformJoint = ( RevoluteJoint ) world
				.createJoint( revoluteJointDef );

		revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( platform.body, skeleton.body,
				platform.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		screwToSkel = ( RevoluteJoint ) world.createJoint( revoluteJointDef );
	}

	/**
	 * attaches any other object between this screw and the main entity that
	 * this screw is attached
	 * 
	 * @param entity
	 */
	public void addStructureJoint( Entity entity ) {
		// connect other structure to structure screw
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		RevoluteJoint screwJoint = ( RevoluteJoint ) world
				.createJoint( revoluteJointDef );
		extraJoints.add( screwJoint );
	}
	
	@Override
	public void screwLeft( ) {
		body.setAngularVelocity( 15 );
		depth--;
		rotation += 10;
		screwStep = depth + 5;
		if ( depth == 0 && screwToSkel != null ) {
			world.destroyJoint( platformJoint );
			world.destroyJoint( screwToSkel );
			depth = -1;
		}
	}

	@Override
	public void screwRight( ) {
		if ( depth < maxDepth ) {
			body.setAngularVelocity( -15 );
			depth++;
			rotation -= 10;
			screwStep = depth + 6;
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		sprite.setRotation( rotation );
		if ( depth != screwStep ) {
			screwStep--;
		}
		if ( depth == screwStep ) {
			body.setAngularVelocity( 0 );
		}
	}

	private ArrayList< RevoluteJoint > extraJoints;
	private RevoluteJoint screwToSkel;
	private RevoluteJoint platformJoint;
}
