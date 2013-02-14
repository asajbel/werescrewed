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
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

public class Rope extends Entity {
	
	Body tail;
	Body link;
	StrippedScrew screw;
	private ArrayList<Body> pieces;
	private float linkWidth, linkHeight;
	
	public Rope( String name, Vector2 pos, Vector2 widthHeight, int links, Texture texture, World world ) {
		super( name, pos, texture, null, true );
		
		pieces = new ArrayList<Body>( );
		
		constructRope ( name, pos, widthHeight, links, world);
		
		screw = new StrippedScrew (  "rope screw", world, new Vector2 (pos.x,  pos.y - widthHeight.y * Util.PIXEL_TO_BOX * links), this );
		
	
	}
	
	public Rope( String name, Entity entity, Vector2 widthHeight, int links, Texture texture, World world ) {
		super( name, entity.getPosition( ), texture, null, true );
		
		pieces = new ArrayList<Body>( );
		
		constructRope (name, entity.getPosition( ), widthHeight, links, world );
		
		screw = new StrippedScrew (  "rope screw", world, new Vector2 (entity.getPosition( ).x,  entity.getPosition( ).y - widthHeight.y * Util.PIXEL_TO_BOX * links), this );
		
	
	}
	
	private void constructRope (String name, Vector2 pos, Vector2 widthHeight, int links, World world){
		
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
		
		for ( int i = 0; i < links; ++i ){
			bodyDef = new BodyDef();
			bodyDef.position.set( new Vector2 ( pieces.get( pieces.size( ) - 1 ).getWorldCenter( ).x, 
					pieces.get( pieces.size( ) - 1 ).getWorldCenter( ).y  - widthHeight.y * Util.PIXEL_TO_BOX) );
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.gravityScale = 0.1f;
			polygonShape.setAsBox( widthHeight.x / 2 * Util.PIXEL_TO_BOX, widthHeight.y / 2 * Util.PIXEL_TO_BOX );
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.density = 10.0f;
			fixtureDef.restitution = 0.0f;
			fixtureDef.friction = 0.5f;
			body = world.createBody( bodyDef );
			body.createFixture( fixtureDef );
			
			
			RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
			revoluteJointDef.initialize( pieces.get( pieces.size( )-1 ), body, new Vector2 ( body.getWorldCenter( ).x,
					body.getWorldCenter().y + widthHeight.y / 2 * Util.PIXEL_TO_BOX ) );
			revoluteJointDef.enableMotor = false;
			revoluteJointDef.collideConnected = false;
			world.createJoint( revoluteJointDef );	
			body.setUserData( this );
			pieces.add( body );

		}
		
	}
	
	@Override
	public void update( float deltatime ) {
		if(Gdx.input.isKeyPressed( Keys.O ))
			pieces.get( pieces.size( )-1 ).applyLinearImpulse( new Vector2(5.5f, 0.0f),
					pieces.get( pieces.size( )-1 ).getWorldCenter( ) );
		
		
	}
	
}
