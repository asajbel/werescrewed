package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;

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
	private ArrayList<Body> pieces;
	
	public Rope( String name, Vector2 pos, Texture texture, World world ) {
		super( name, pos, texture, null, true );
		
		pieces = new ArrayList<Body>( );
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set( pos );
		bodyDef.gravityScale = .01f;
		FixtureDef fixDef = new FixtureDef();
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox( 16.0f * Util.PIXEL_TO_BOX, 16.0f * Util.PIXEL_TO_BOX );
		fixDef.shape = polygonShape;
		fixDef.density = 0.1f;
		body = world.createBody( bodyDef );
		body.createFixture( fixDef );
		pieces.add( body );
		//link = body;
		
		for ( int i = 0; i < 10; ++i ){
			bodyDef = new BodyDef();
			bodyDef.position.set( new Vector2 ( pieces.get( pieces.size( )-1 ).getWorldCenter( ).x, 
					pieces.get( pieces.size( )-1 ).getWorldCenter( ).y  - 32.0f * Util.PIXEL_TO_BOX) );
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
			revoluteJointDef.initialize( pieces.get( pieces.size( )-1 ), body, new Vector2 ( body.getWorldCenter( ).x,
					body.getWorldCenter().y + 16.0f * Util.PIXEL_TO_BOX ) );
			revoluteJointDef.enableMotor = false;
			revoluteJointDef.collideConnected = false;
			world.createJoint( revoluteJointDef );	
			body.setUserData( this );
			//link = body;
			pieces.add( body );

		}
		
		//polygonShape.dispose( );
		
		
		bodyDef.position.set(  new Vector2 ( pieces.get( pieces.size( )-1 ).getWorldCenter( ).x,
				pieces.get( pieces.size( )-1 ).getWorldCenter().y  - 32.0f * Util.PIXEL_TO_BOX ) );
		FixtureDef fixtureDef = new FixtureDef();
		polygonShape.setAsBox(  64.0f * Util.PIXEL_TO_BOX, 16.0f * Util.PIXEL_TO_BOX );
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = false;
		fixtureDef.density = 10.0f;
		body = world.createBody( bodyDef );
		body.createFixture( fixtureDef );
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( pieces.get( pieces.size( )-1 ), body,  new Vector2 ( body.getWorldCenter( ).x,
				body.getWorldCenter().y + 16.0f * Util.PIXEL_TO_BOX ) );
		revoluteJointDef.enableMotor = false;
		revoluteJointDef.enableLimit = true;
		world.createJoint( revoluteJointDef );
		
		pieces.add(body);
	
		//for( Body b : pieces)
			//System.out.println( b.toString( ));
		// TODO Auto-generated constructor stub
	
	}
	
	@Override
	public void update( float deltatime ) {
		if(Gdx.input.isKeyPressed( Keys.O ))
			pieces.get( pieces.size( )-1 ).applyLinearImpulse( new Vector2(5.5f, 0.0f),
					pieces.get( pieces.size( )-1 ).getWorldCenter( ) );
		
		//System.out.println(body.toString( ));
		
	}
	
}
