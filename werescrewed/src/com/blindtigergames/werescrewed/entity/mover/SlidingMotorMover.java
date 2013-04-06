package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

/**
 * Can be used for sliding platforms, pistons, elevators This should be attached
 * to an Entity, not used for puzzles
 * 
 * @author stew
 * 
 */
public class SlidingMotorMover implements IMover {

	PuzzleType type;
	protected PrismaticJoint joint;
	protected boolean loop; // takes priority over loopOnce
	// TODO: finish loop once functionality
	protected boolean loopOnce; // will allow joint to go the full joint length
								// and back once
	boolean recentlyFlipped;

	public SlidingMotorMover( PuzzleType _type, PrismaticJoint _joint ) {
		// TODO Auto-generated constructor stub
		joint = _joint;
		loop = false;
		loopOnce = true;
		type = _type;
		recentlyFlipped = true;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		boolean atLowerLimit = joint.getJointTranslation( ) <= joint
				.getLowerLimit( );
		boolean atUpperLimit = joint.getJointTranslation( ) >= joint
				.getUpperLimit( );
		if ( atLowerLimit || atUpperLimit ) {
			if ( !recentlyFlipped ) {
				recentlyFlipped = true;
				joint.setMotorSpeed( -joint.getMotorSpeed( ) );
			}
		} else {
			recentlyFlipped = false;
		}
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {

	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
