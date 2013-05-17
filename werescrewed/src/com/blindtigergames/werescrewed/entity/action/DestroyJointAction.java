package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;

public class DestroyJointAction implements IAction {

	Joint joint = null;

	public DestroyJointAction( Joint rj ) {
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

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}