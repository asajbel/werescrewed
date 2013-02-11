package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.util.Util;

public class Rope extends Entity {

	public Rope( String name, Vector2 pos, Texture texture, World world ) {
		super( name, pos, texture, null, true );
		
		
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set( pos );
		bodyDef.gravityScale = .07f;
		FixtureDef fixDef = new FixtureDef();
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox( .03f, 0.05f );
		fixDef.shape = polygonShape;
		fixDef.density = 0.1f;
		body = world.createBody( bodyDef );
		body.createFixture( fixDef );
		Body link = body;
		
		for ( int i = 1; i <= 10; i++ ){
			bodyDef = new BodyDef();
			bodyDef.position.set( new Vector2 ( pos.x, pos.y - i * 0.1f) );
			bodyDef.type = BodyType.DynamicBody;
			polygonShape.setAsBox( 0.01f, 0.05f );
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.isSensor = false;
			fixtureDef.density = .5f;
			body = world.createBody( bodyDef );
			body.createFixture( fixtureDef );
			RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
//			revoluteJointDef.initialize( link, body, body.getPosition() );
			revoluteJointDef.initialize( link, body, new Vector2 ( pos.x, pos.y - i * 0.1f + 0.05f ) );
			revoluteJointDef.enableMotor = false;
			revoluteJointDef.enableLimit = true;
			revoluteJointDef.lowerAngle = 45 * MathUtils.degreesToRadians;
			revoluteJointDef.upperAngle = 180 * MathUtils.degreesToRadians;
			world.createJoint( revoluteJointDef );	
			body.setUserData( this );
			link = body;
		}
		bodyDef.position.set(  new Vector2 ( pos.x, pos.y - 11 * 0.1f) );
		FixtureDef fixtureDef = new FixtureDef();
		polygonShape.setAsBox( 0.03f, 0.05f );
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = false;
		fixtureDef.density = 0.5f;
		body = world.createBody( bodyDef );
		body.createFixture( fixtureDef );
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( link, body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		revoluteJointDef.enableLimit = true;
		revoluteJointDef.lowerAngle = 45 * MathUtils.degreesToRadians;
		revoluteJointDef.upperAngle = 180 * MathUtils.degreesToRadians;
		world.createJoint( revoluteJointDef );
	
		// TODO Auto-generated constructor stub
	
	}
	public void update( float deltatime ) {
		super.update ( deltatime);
		
	}
	
}
