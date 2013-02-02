package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.util.Util;

/**
 * blah blah
 * 
 * @author Dennis
 * 
 */

public class StrippedScrew extends Screw {

	public StrippedScrew( String name, World world, Vector2 pos,
			Entity entity ) {
		super( name, pos, null );
		this.world = world;

		sprite.setColor( Color.ORANGE );
		sprite.setOrigin( 0.0f, 0.0f );
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape
				.setRadius( ( sprite.getWidth( ) / 2.0f ) * Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = 0x0001 | 0x0002;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );

		// add radar sensor to screw
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 1.25f * Util.PIXEL_TO_BOX );
		FixtureDef radarFixture = new FixtureDef( );
		radarFixture.shape = radarShape;
		radarFixture.isSensor = true;
		radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS; // category of Screw
															// Radar...
		radarFixture.filter.maskBits = 0x0001 | 0x0002;// radar collides with
														// player 1 & 2
		body.createFixture( radarFixture );

		// connect the screw to the entity
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}

	@Override
	public void screwLeft( ) {
	}

	@Override
	public void screwRight( ) {
	}

}
