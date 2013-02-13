package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.util.Util;

public class Rope extends Entity {
	
	Body tail;
	Body link;

	public Rope( String name, Vector2 pos, Texture texture, World world ) {
		super( name, pos, texture, null, true );
		

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set( pos );
		bodyDef.gravityScale = .01f;
		FixtureDef fixDef = new FixtureDef();
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox( .05f, 0.03f );
		fixDef.shape = polygonShape;
		fixDef.density = 0.1f;
		body = world.createBody( bodyDef );
		body.createFixture( fixDef );
		link = body;
		
		for ( int i = 1; i <= 10; i++ ){
			bodyDef = new BodyDef();
			bodyDef.position.set( new Vector2 ( pos.x, pos.y - i * 32.0f * Util.PIXEL_TO_BOX) );
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.gravityScale = 0.1f;
			polygonShape.setAsBox( 4.0f * Util.PIXEL_TO_BOX, 16.0f * Util.PIXEL_TO_BOX );
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.density = 10.0f;
			fixtureDef.restitution = 0.0f;
			fixtureDef.friction = 0.5f;
			body = world.createBody( bodyDef );
			body.createFixture( fixtureDef );
			
			RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
			revoluteJointDef.initialize( link, body, new Vector2 ( body.getWorldCenter( ).x,
					body.getWorldCenter().y + 16.0f * Util.PIXEL_TO_BOX ) );
			revoluteJointDef.enableMotor = false;
			revoluteJointDef.collideConnected = false;
			world.createJoint( revoluteJointDef );	
			body.setUserData( this );
			link = body;
		}
		polygonShape.dispose( );
		
//		bodyDef.position.set(  new Vector2 ( pos.x, pos.y - 11 * 0.1f) );
//		FixtureDef fixtureDef = new FixtureDef();
//		polygonShape.setAsBox( 0.03f, 0.05f );
//		fixtureDef.shape = polygonShape;
//		fixtureDef.isSensor = false;
//		fixtureDef.density = 0.5f;
//		body = world.createBody( bodyDef );
//		body.createFixture( fixtureDef );
//		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
//		revoluteJointDef.initialize( link, body, body.getPosition( ) );
//		revoluteJointDef.enableMotor = false;
//		revoluteJointDef.enableLimit = true;
//		revoluteJointDef.lowerAngle = 45 * MathUtils.degreesToRadians;
//		revoluteJointDef.upperAngle = 180 * MathUtils.degreesToRadians;
//		world.createJoint( revoluteJointDef );
	
		// TODO Auto-generated constructor stub
	
	}
	
	@Override
	public void update( float deltatime ) {
		if(Gdx.input.isKeyPressed( Keys.O ))
			body.applyLinearImpulse( new Vector2(5.5f, 0.0f), body.getWorldCenter( ) );
		
		//System.out.println(body.toString( ));
		
	}
	
}
