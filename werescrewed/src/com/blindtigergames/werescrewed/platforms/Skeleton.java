package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.*;
import com.blindtigergames.werescrewed.screens.GameScreen;
import com.sun.tools.javac.util.Position;

/**
 * Bone is an entity which is placed onto the Skeleton
 * then when the Skeleton moves, the Bones should follow
 * 
 * @author Ranveer/Stewart
 *
 *
 */

public class Skeleton extends Entity{
	
	public ArrayList<BoneAndJoints> boneAndJoints;
	
	//private Skeleton(){};
	
	public Skeleton( String n, Vector2 pos, Texture tex, World world ){
		super( n, pos, tex , null); //not constructing body class
		this.world = world;
		this.boneAndJoints = new ArrayList<BoneAndJoints>();
		constructSkeleton(pos);
	}
	
	public void constructSkeleton(Vector2 pos){
		//Skeletons have no fixtures!!
		BodyDef skeletonBodyDef = new BodyDef();
        //groundBodyDef.type = BodyType.KinematicBody;
		skeletonBodyDef.position.set( pos.mul( GameScreen.PIXEL_TO_BOX ) );  
		PolygonShape boxPolygonShape = new PolygonShape();
		boxPolygonShape.setAsBox(0.001f, 0.001f);
		
        body = world.createBody( skeletonBodyDef );
        
	}
	
	public void addBoneAndJoint( Entity bone, Joint joint ){
		this.boneAndJoints.add( new BoneAndJoints(bone, joint) );
		
	}
	
	public void addBoneAndJointList(Entity bone, ArrayList<Joint> joints){
		this.boneAndJoints.add( new BoneAndJoints(bone, joints) );
	}
	

	public void sleepSkeleton(){
		body.setActive(false);
		for(BoneAndJoints boneJoint : boneAndJoints){
			//boneJoint.bone.body.setActive(false);
			boneJoint.bone.body.setAwake(false);
		}
	}

	public void wakeSkeleton(){
		body.setActive(true);
		for(BoneAndJoints boneJoint : boneAndJoints){
			//boneJoint.bone.body.setActive(true);
			boneJoint.bone.body.setAwake(true);
		}
	}
	
	@Override
	public void update(){
		mover.move(body);
	}
	
	protected class BoneAndJoints{
		 protected ArrayList<Joint> joints;
		 protected Entity bone;
		 
		 protected BoneAndJoints(Entity bone, ArrayList<Joint> joints ){
			 this.bone = bone;
			 this.joints = joints;
		 }
		 
		 protected BoneAndJoints(Entity bone, Joint joint ){
			 this.bone = bone;
			 joints = new ArrayList<Joint>();
			 this.joints.add(joint);
		 }
	}
}