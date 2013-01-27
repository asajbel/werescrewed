package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class StrippedScrew extends Screw {
	public StrippedScrew( String n, Vector2 pos, Texture tex,
			Skeleton skeleton, World world ) {
		super( n, pos, tex, null );
		this.world = world;

		sprite.setColor( Color.ORANGE );
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape.setRadius( ( sprite.getWidth( ) / 2.0f )
				* GameScreen.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = CATEGORY_SCREWS;
		screwFixture.filter.maskBits = 0x0001 | 0x0002;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );

		// add radar sensor to screw
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 1.25f
				* GameScreen.PIXEL_TO_BOX );
		FixtureDef radarFixture = new FixtureDef( );
		radarFixture.shape = radarShape;
		radarFixture.isSensor = true;
		radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw
															// Radar...
		radarFixture.filter.maskBits = 0x0001 | 0x0002;// radar collides with
														// player 1 & 2
		// (player category bits 0x0001)
		body.createFixture( radarFixture );
		/*
		 * // connect the screw to the skeleton; RevoluteJointDef
		 * revoluteJointDef = new RevoluteJointDef( );
		 * revoluteJointDef.initialize( body, skeleton.body, body.getPosition( )
		 * ); revoluteJointDef.enableMotor = false;
		 * revoluteJointDef.maxMotorTorque = 5000.0f;
		 * revoluteJointDef.motorSpeed = 0f; platformToScrew = ( RevoluteJoint )
		 * world .createJoint( revoluteJointDef );
		 */

		// skeleton.addBoneAndJoint( this, platformToScrew );
	}

	@Override
	public void screwLeft( ) {
	}

	@Override
	public void screwRight( ) {
	}

	private RevoluteJoint platformToScrew;
}
