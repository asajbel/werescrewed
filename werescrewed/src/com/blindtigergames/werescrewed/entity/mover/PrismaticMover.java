package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;

public class PrismaticMover implements IMover{
	
	protected PrismaticJoint joint;
	protected boolean loop;	//takes priority over loopOnce
	protected boolean loopOnce; //will allow joint to go the full joint length and back once

	public PrismaticMover( PrismaticJoint _joint ) {
		// TODO Auto-generated constructor stub
		joint = _joint;
		loop = false;
		loopOnce = true;
	}
	
	@Override
	public void move(Body body) {
		// TODO Auto-generated method stub\
		boolean atLowerLimit = joint.getJointTranslation() <= joint.getLowerLimit();
		boolean atUpperLimit = joint.getJointTranslation() >= joint.getUpperLimit();
		if ( atLowerLimit || atLowerLimit ){
			//joint.setMotorSpeed(-joint.getMotorSpeed());
		}
		
	}

	@Override
	public void move(Body body, SteeringOutput steering) {
		// TODO Auto-generated method stub
		Gdx.app.error("PrismaticMoverError", "This method isn't supported yet. Don't use it.");
	}
	
	
}
