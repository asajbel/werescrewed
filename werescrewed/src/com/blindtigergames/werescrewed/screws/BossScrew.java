package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 *
 */

public class BossScrew extends Screw {
	public RevoluteJoint screwJoint;
	
	public BossScrew( String n, Vector2 pos, Texture tex, int max, Body platform ){
		super( n, pos, tex);
		maxDepth = max;
		depth = max;	
		
		//create the screw body
	    BodyDef screwBodyDef = new BodyDef();
	    screwBodyDef.type = BodyType.DynamicBody;
	    screwBodyDef.position.set(pos);
	    body = world.createBody(screwBodyDef);
	    CircleShape screwShape = new CircleShape();
	    screwShape.setRadius(sprite.getWidth());
	    body.createFixture(screwShape,0.0f);
	    screwShape.dispose();
		sprite.setScale(GameScreen.PIXEL_TO_BOX);
	    
		//connect the screw to the platform;
	    RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
	    revoluteJointDef.initialize(body, platform, body.getPosition());  
	    revoluteJointDef.enableMotor = true;
	    revoluteJointDef.maxMotorTorque = 5000.0f;
	    revoluteJointDef.motorSpeed = 50f;
	    platformToScrew = (RevoluteJoint) world.createJoint(revoluteJointDef);
	    
		//add radar sensor to screw
		CircleShape radarShape = new CircleShape();
		radarShape.setRadius(sprite.getWidth()*2);
	    FixtureDef radarFixture = new FixtureDef();
	    radarFixture.shape = radarShape;
	    radarFixture.isSensor = true;
	    radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw Radar...
	    radarFixture.filter.maskBits = 0x0001;//radar only collides with player (player category bits 0x0001)
		body.createFixture(radarFixture);
		
	    BodyDef jointBodyDef = new BodyDef();
	    jointBodyDef.type = BodyType.StaticBody;
	    jointBodyDef.position.set(platform.getPosition().x,platform.getPosition().y+100);
	    Body jointBody = world.createBody(jointBodyDef);
	    PolygonShape jointPolygonShape = new PolygonShape();
	    jointPolygonShape.setAsBox(0, 0);
	    jointBody.createFixture(jointPolygonShape,0.0f);
	    jointPolygonShape.dispose();
	    
	    revoluteJointDef = new RevoluteJointDef();
	    revoluteJointDef.initialize(platform, jointBody, platform.getPosition());  
	    revoluteJointDef.enableMotor = true;
	    revoluteJointDef.maxMotorTorque = 5000.0f;
	    revoluteJointDef.motorSpeed = 50f;
	    screwJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
	}

	public void update(){
		if ( depth > maxDepth ) {
			depth = maxDepth;
		}
		if ( depth <= 0 ) {
			world.destroyJoint(platformToScrew);
			world.destroyJoint(screwJoint);
		}
	}

	private int maxDepth;
	private RevoluteJoint platformToScrew;
}
