package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;

public class ScrewDestroyJointAction implements IAction {

	@Override
	public void act( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act( Entity entity ) {
		StructureScrew s = ( StructureScrew ) entity;
		World w = s.body.getWorld( );
		for ( Joint j : s.extraJoints ) {
			w.destroyJoint( j );
		}

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}