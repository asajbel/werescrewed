package com.blindtigergames.werescrewed.rope;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.util.Util;

public class Link extends Entity {
	private float width, height;

	public Link( String name, World world, Vector2 pos, Texture texture, Vector2 widthHeight ) {
		super( name, pos, texture, null, true );
		this.world = world;
		this.width = widthHeight.x;
		this.height = widthHeight.y;

		constructBody( pos );
	}

	private void constructBody( Vector2 pos ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.position.set( pos );
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.gravityScale = 0.1f;
		PolygonShape polygonShape = new PolygonShape( );
		polygonShape.setAsBox( width / 2 * Util.PIXEL_TO_BOX,
				height / 2 * Util.PIXEL_TO_BOX );
		FixtureDef fixtureDef = new FixtureDef( );
		fixtureDef.filter.categoryBits = Util.CATEGORY_ROPE;
		fixtureDef.filter.maskBits = Util.CATEGORY_NOTHING;
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 10.0f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.friction = 0.1f;
		body = world.createBody( bodyDef );
		body.createFixture( fixtureDef );

		body.setUserData( this );
	}

	public void createLinkJoint( Link link ) {

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( link.body, body,
				new Vector2( body.getWorldCenter( ).x, body.getWorldCenter( ).y
						- height / 2 * Util.PIXEL_TO_BOX ) );
		revoluteJointDef.enableMotor = false;
		revoluteJointDef.collideConnected = false;
		world.createJoint( revoluteJointDef );
	}
}