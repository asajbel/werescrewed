package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.util.Util;

/**
 * screws that do not un-screw 
 * they are only used for climbing or
 * tacking entities together
 * 
 * @author Dennis
 * 
 */

public class StrippedScrew extends Screw {

	public StrippedScrew( String name, World world, Vector2 pos, Entity entity ) {
		super( name, pos, null );
		this.world = world;
		screwType = ScrewType.STRIPPED;
		entityType = EntityType.SCREW;

		sprite.setColor( 255f/255f, 112f/255f, 52f/255f, 1.0f ); //rust color pulled off a hexdecimal chart
		sprite.setOrigin( 0.0f, 0.0f );

		constructBody( pos );
		connectScrewToEntity( entity );

	}

	@Override
	public void screwLeft( ) {
	}

	@Override
	public void screwRight( ) {
	}

	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape
				.setRadius( ( sprite.getWidth( ) / 2.0f ) * Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.density = 4f;
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		screwFixture.isSensor = true;
		screwFixture.shape = screwShape;
		body.createFixture( screwFixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		//we may want a radar depending on the size of the sprite...
		// add radar sensor to screw this is needed/not needed depending on the size of the screw
//		CircleShape radarShape = new CircleShape( );
//		radarShape.setRadius( sprite.getWidth( ) * 1.05f * Util.PIXEL_TO_BOX );
//		FixtureDef radarFixture = new FixtureDef( );
//		radarFixture.shape = radarShape;
//		radarFixture.isSensor = true;
//		radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
//		radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
//				| Util.CATEGORY_SUBPLAYER;
//		body.createFixture( radarFixture );
//		radarShape.dispose( );
		screwShape.dispose( );

	}

	public void connectScrewToEntity( Entity entity ) {
		// connect the screw to the entity
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}

}
