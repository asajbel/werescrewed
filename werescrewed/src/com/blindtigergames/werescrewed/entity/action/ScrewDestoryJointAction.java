package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.screws.StructureScrew;

public class ScrewDestoryJointAction implements IAction{

	@Override
	public void act( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Entity entity ) {
		StructureScrew s = (StructureScrew) entity;
		World w = s.body.getWorld( );
		for ( RevoluteJoint j : s.extraJoints ) {
			w.destroyJoint( j );
		}
		
	}
	
}