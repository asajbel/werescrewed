package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.*;

/**
 * Bone is an entity which is placed onto the Skeleton
 * then when the Skeleton moves, the Bones should follow 
 * @param name blah blah
 * 
 * @author Ranveer/Stewart
 *
 *
 */

public class Skeleton extends Entity{
	
	public ArrayList<BoneAndJoints> boneAndJoints;
	
	
	public Skeleton(String n, Vector2 pos, Texture tex, World world ){
		super( n, pos, tex , null);
		this.world = world;
		this.boneAndJoints = new ArrayList<BoneAndJoints>();
		
	    BodyDef skelDef = new BodyDef();
	    skelDef.type = BodyType.StaticBody;
	    skelDef.position.set(pos);
	    body = world.createBody(skelDef);

	}
	
	public void constructSkeleton(){
		
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
			boneJoint.bone.body.setActive(false);
		}
	}

	public void wakeSkeleton(){
		body.setActive(true);
		for(BoneAndJoints boneJoint : boneAndJoints){
			boneJoint.bone.body.setActive(true);
		}
	}
	
	@Override
	public void update(){
		
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