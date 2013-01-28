package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class BossScrew extends Screw {

	public BossScrew( String n, Vector2 pos, int max, Body bod,
			Entity platform, Skeleton skeleton ) {
		super( n, pos, bod );
		maxDepth = max;
		depth = max;

		// add radar sensor to screw
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 2 );
		FixtureDef radarFixture = new FixtureDef( );
		radarFixture.shape = radarShape;
		radarFixture.isSensor = true;
		radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw
															// Radar...
		radarFixture.filter.maskBits = 0x0001;// radar only collides with player
												// (player category bits 0x0001)
		body.createFixture( radarFixture );
		body.setUserData( this );

		// connect the screw to the platform;
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, skeleton.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.maxMotorTorque = 5000.0f;
		revoluteJointDef.motorSpeed = 50f;
		platformToScrew = ( RevoluteJoint ) world
				.createJoint( revoluteJointDef );

		revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( platform.body, skeleton.body,
				platform.getPosition( ) );
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.maxMotorTorque = 5000.0f;
		revoluteJointDef.motorSpeed = 50f;
		screwJoint = ( RevoluteJoint ) world.createJoint( revoluteJointDef );

		// connect the entities to the skeleton
		//skeleton.addBoneAndJoint( this, platformToScrew );
		//skeleton.addBoneAndJoint( platform, screwJoint );
	}

	@Override
	public void screwLeft( ) {
		body.setAngularVelocity( 15 );
		depth--;
		rotation += 10;
		screwStep = depth + 5;
		if ( depth == 0 && screwJoint != null ) {
			world.destroyJoint( platformToScrew );
			world.destroyJoint( screwJoint );
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

	private RevoluteJoint screwJoint;
	private RevoluteJoint platformToScrew;
}
