package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 *
 */

public class StructureScrew extends Screw {
	public RevoluteJoint screwJoint;
	
	public StructureScrew( String n, Vector2 pos, Texture tex, int max, Body platform, World world){
		super( n, pos, tex );
		this.world = world;
		maxDepth = max;
		depth = max;
		
		sprite.setScale(GameScreen.PIXEL_TO_BOX);
		
		//create the screw body
	    BodyDef screwBodyDef = new BodyDef();
	    screwBodyDef.type = BodyType.KinematicBody;
	    screwBodyDef.position.set(pos);
	    body = world.createBody(screwBodyDef);
	    CircleShape screwShape = new CircleShape();
	    screwShape.setRadius(sprite.getWidth()*GameScreen.PIXEL_TO_BOX);
	    body.createFixture(screwShape,0.0f);
	    screwShape.dispose();
	    
		//add radar sensor to screw
		CircleShape radarShape = new CircleShape();
		radarShape.setRadius(sprite.getWidth()*2*GameScreen.PIXEL_TO_BOX);
	    FixtureDef radarFixture = new FixtureDef();
	    radarFixture.shape = radarShape;
	    radarFixture.isSensor = true;
	    radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw Radar...
	    radarFixture.filter.maskBits = 0x0001;//radar only collides with player (player category bits 0x0001)
		body.createFixture(radarFixture);
		radarShape.dispose();
		  
	    BodyDef jointBodyDef = new BodyDef();
	    jointBodyDef.type = BodyType.StaticBody;
	    jointBodyDef.position.set(body.getPosition());
	    Body jointBody = world.createBody(jointBodyDef);
	    PolygonShape jointPolygonShape = new PolygonShape();
	    jointPolygonShape.setAsBox(0, 0);
	    jointBody.createFixture(jointPolygonShape,0.0f);
	    jointPolygonShape.dispose();
	    
	    //connect the platform to the screw and the skeleton
	    RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
	    revoluteJointDef.initialize(platform, jointBody, platform.getPosition());  
	    revoluteJointDef.enableMotor = true;
	    revoluteJointDef.maxMotorTorque = 5000.0f;
	    revoluteJointDef.motorSpeed = 50f;
	    screwJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
	}

	public void update(){
		super.update();
		if ( depth != screwStep ){
			screwStep--;
		}
		if( depth == screwStep ){
			body.setAngularVelocity(0);
		}
		if ( depth > maxDepth ) {
			depth = maxDepth;
		}
		if ( depth == 0 ) {
			body.setType(BodyType.DynamicBody);
			world.destroyJoint(screwJoint);
			depth = -1;
		}
	}
	private int maxDepth;
}
