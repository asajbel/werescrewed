package com.blindtigergames.werescrewed.joint;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;

/**
 * This is deprecated for now, but I'm leaving it in in case I use it for the
 * future.
 * 
 * @author stew
 * 
 */
public class JointFactory {

	/**
	 * Use PrismaticJointBuilder instead of this
	 * 
	 * @param skeletonBody
	 * @param bodyB
	 * @param bodyBAnchor
	 * @param axis
	 * @param upperTranslation
	 * @param motorSpeed
	 * @return
	 */
	public static PrismaticJointDef constructSlidingJointDef(
			Body skeletonBody, Body bodyB, Vector2 bodyBAnchor, Vector2 axis,
			float upperTranslation, float motorSpeed ) {
		PrismaticJointDef prismaticJointDef = new PrismaticJointDef( );
		prismaticJointDef.initialize( skeletonBody, bodyB, bodyBAnchor, axis );
		prismaticJointDef.enableLimit = true;
		prismaticJointDef.lowerTranslation = 0;
		prismaticJointDef.upperTranslation = upperTranslation;
		prismaticJointDef.enableMotor = true;
		prismaticJointDef.maxMotorForce = 500;// high max motor force yields a
												// very strong motor
		prismaticJointDef.motorSpeed = motorSpeed;
		return prismaticJointDef;
	}

}