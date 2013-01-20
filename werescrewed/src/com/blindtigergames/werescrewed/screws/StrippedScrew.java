package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.platforms.Skeleton;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class StrippedScrew extends Screw {
	public StrippedScrew( String n, Vector2 pos, Texture tex, Skeleton skeleton ) {
		super( n, pos, tex, null );

		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.StaticBody;
		screwBodyDef.position.set( pos );
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape.setRadius( sprite.getWidth( ) );
		body.createFixture( screwShape, 0.0f );
		screwShape.dispose( );
		sprite.setScale( GameScreen.PIXEL_TO_BOX );

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

		// connect the screw to the skeleton;
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, skeleton.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		revoluteJointDef.maxMotorTorque = 5000.0f;
		revoluteJointDef.motorSpeed = 0f;
		platformToScrew = ( RevoluteJoint ) world
				.createJoint( revoluteJointDef );

		skeleton.addBoneAndJoint( this, platformToScrew );
	}

	public void screwLeft( ) {
	}

	public void screwRight( ) {
	}

	private RevoluteJoint platformToScrew;
}
