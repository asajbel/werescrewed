package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * @param name blah blah
 * 
 * @author Dennis
 *
 */

public class StructureScrew extends Screw {
	public RevoluteJoint screwJoint;
	
	public StructureScrew(int max, Body platform){
		maxDepth = max;
		depth = max;		
		
		//add radar sensor to screw
		CircleShape radarShape = new CircleShape();
		radarShape.setRadius(sprite.getWidth()*2);
	    FixtureDef radarFixture = new FixtureDef();
	    radarFixture.shape = radarShape;
	    radarFixture.isSensor = true;
	    radarFixture.filter.categoryBits = 0x0003; // category of Screw Radar...
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
	    
	    RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
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
			body.setType(BodyType.DynamicBody);
			world.destroyJoint(screwJoint);
		}
	}
	private int maxDepth;
}
