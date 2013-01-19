package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.platforms.Skeleton;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 *
 */

public class StructureScrew extends Screw {
	
	public StructureScrew( String n, Vector2 pos, Texture tex, 
			int max, Entity platform, Skeleton skeleton, World world ){
		super( n, pos, tex );
		maxDepth = max;
		depth = max;
		rotation = 1080;
		
		//create the screw body
	    BodyDef screwBodyDef = new BodyDef();
	    screwBodyDef.type = BodyType.KinematicBody;
	    screwBodyDef.position.set(pos);
	    body = world.createBody(screwBodyDef);
	    CircleShape screwShape = new CircleShape();
	    screwShape.setRadius((sprite.getWidth()/2.0f)*GameScreen.PIXEL_TO_BOX);
	    FixtureDef screwFixture = new FixtureDef();
	    screwFixture.isSensor = true;
	    screwFixture.filter.categoryBits = CATEGORY_SCREWS;
	    screwFixture.filter.maskBits = -1;
	    body.createFixture(screwShape,0.0f);
	    screwShape.dispose();
		offset.x = -16f;
		offset.y = -16f;
		body.setUserData( this );
	    
		//add radar sensor to screw
		CircleShape radarShape = new CircleShape();
		radarShape.setRadius(sprite.getWidth()*GameScreen.PIXEL_TO_BOX);
	    FixtureDef radarFixture = new FixtureDef();
	    radarFixture.shape = radarShape;
	    radarFixture.isSensor = true;
	    radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw Radar...
	    radarFixture.filter.maskBits = 0x0001;//radar only collides with player (player category bits 0x0001)
		body.createFixture(radarFixture);
		radarShape.dispose();
		  	    
	    BodyDef skelDef = new BodyDef();
	    skelDef.type = BodyType.StaticBody;
	    skelDef.position.set(pos);
	    Body skelBody = world.createBody(skelDef);
	    
		//skeleton.addBoneAndJoint(platform, screwJoint);
	    //connect the platform to the screw and the skeleton
	    RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
	    revoluteJointDef.initialize(platform.body, skelBody, platform.getPosition());  
	    revoluteJointDef.enableMotor = true;
	    revoluteJointDef.maxMotorTorque = 5000.0f;
	    revoluteJointDef.motorSpeed = 0f;
	    screwJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
	}

	public void update(){
		super.update();
		sprite.setRotation(rotation);
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
			if(body.getJointList().size() > 0){
				world.destroyJoint(screwJoint);
			}
			depth = -1;
		}
	}
	private RevoluteJoint screwJoint;
	private int maxDepth;
}
