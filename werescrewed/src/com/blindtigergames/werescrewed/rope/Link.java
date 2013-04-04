package com.blindtigergames.werescrewed.rope;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Image;
import com.blindtigergames.werescrewed.util.Util;

public class Link extends Entity {
	private float width, height;
	private float xOffset, yOffset;
	
	public Link( String name, World world, Vector2 pos, Texture texture, Vector2 widthHeight ) {
		super( name, pos, texture, null, true );
		this.world = world;
		this.width = widthHeight.x;
		this.height = widthHeight.y;

		constructBody( pos );
		Image temp = constructSprite( ( Texture ) WereScrewedGame.manager
				.get( WereScrewedGame.dirHandle + "/common/chainlink.png" ) );
		this.xOffset = (temp.getWidth( )/2);//+this.width/2;
		this.yOffset = (temp.getHeight( )/2);//+this.height/2;
		
		this.changeSprite( temp );
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
		fixtureDef.density = 1f;
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
	
	public float getWidth(){
		return this.width;
	}
	
	public float getHeight(){
		return this.height;
	}
	
	
	@Override 
	public void draw(SpriteBatch batch){
		float xpos =  body.getPosition( ).x - (xOffset * Util.PIXEL_TO_BOX);
		float ypos =  body.getPosition( ).y - (yOffset * Util.PIXEL_TO_BOX);
		
		//this.sprite.setOrigin( this.sprite.getWidth( ) / 2, this.sprite.getHeight( ) / 2);
		this.sprite.setPosition( xpos * Util.BOX_TO_PIXEL, ypos * Util.BOX_TO_PIXEL);
		this.sprite.setRotation(  MathUtils.radiansToDegrees
				* body.getAngle( ) );
		
		this.sprite.draw( batch, 1.0f );
	}
	
}