package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.puzzles.PuzzleManager;
import com.blindtigergames.werescrewed.util.Util;

/**
 * blah blah
 * 
 * @author Dennis
 * 
 */

public class PuzzleScrew extends Screw {
	public PuzzleManager puzzleManager;
	
	public PuzzleScrew( String name, Vector2 pos, int max,
			Skeleton skeleton, World world ) {
		super( name, pos, null, null );
		this.world = world;
		maxDepth = max;
		depth = max;
		puzzleManager = new PuzzleManager( world );

		sprite.setColor( Color.GREEN );
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
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );

		// add radar sensor to screw
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 1.25f
				* Util.PIXEL_TO_BOX );
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

	@Override
	public void screwLeft( ) {
		if ( depth > 0 ) {
			body.setAngularVelocity( 15 );
			depth--;
			rotation += 10;
			screwStep = depth + 5;
			puzzleManager.runElement( this );
		}
	}

	@Override
	public void screwRight( ) {
		if ( depth < maxDepth ) {
			body.setAngularVelocity( -15 );
			depth++;
			rotation -= 10;
			screwStep = depth + 6;
			puzzleManager.runElement( this );
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		puzzleManager.update( deltaTime );
		sprite.setRotation( rotation );
		if ( depth != screwStep ) {
			screwStep--;
		}
		if ( depth == screwStep ) {
			body.setAngularVelocity( 0 );
		}
	}

	public int getMaxDepth( ) {
		return maxDepth;
	}

	private RevoluteJoint platformToScrew;

}
