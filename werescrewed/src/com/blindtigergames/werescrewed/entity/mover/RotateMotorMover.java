package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.platforms.Platform;

/**
 * This class may be totally uneccesary, for now let's see 
 * if functionality needs to be added.
 * @author stew
 *
 */
public class RotateMotorMover implements IMover {

	RevoluteJoint joint;
	
	public RotateMotorMover( RevoluteJoint _joint ) {
		// TODO Auto-generated constructor stub
		this.joint = _joint;
	}
	
	public void flipRotation(){
		joint.setMotorSpeed( -joint.getMotorSpeed( ) );
	}
	
	@Override
	public void move( float deltaTime, Body body ) {
		//It shouldn't

	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {

	}

}
