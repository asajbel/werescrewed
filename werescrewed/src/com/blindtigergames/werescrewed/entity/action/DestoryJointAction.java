package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.entity.Entity;

public class DestoryJointAction implements IAction{

	Joint joint = null;
	public DestoryJointAction( Joint rj){
		joint = rj;
	}
	@Override
	public void act( ) {
		World w = joint.getBodyA( ).getWorld( );
		w.destroyJoint( joint );
		
	}

	@Override
	public void act( Entity entity ) {
		// TODO Auto-generated method stub
		
	}
	
}