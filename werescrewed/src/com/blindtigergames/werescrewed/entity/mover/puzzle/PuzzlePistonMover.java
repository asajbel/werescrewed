package com.blindtigergames.werescrewed.entity.mover.puzzle;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

/**
 * Flips motor speed on prismatic joint so a piston will shoot up and back to
 * rest when explode() is called. This should remain attached to an entity.
 * 
 * @author stew
 * 
 */
public class PuzzlePistonMover implements IMover {

	// create a sliding joint

	protected PrismaticJoint joint;
	boolean isExploding;
	float motorSpeed;
	boolean hasBeenExploded;

	public PuzzlePistonMover( PrismaticJoint _joint ) {
		isExploding = false;
		motorSpeed = this.joint.getMotorSpeed( );
		hasBeenExploded = false;
	}

	public void explode( ) {
		hasBeenExploded = true;
	}

	@Override
	public void move( float deltaTime, Body body ) {

		boolean atUpperLimit = joint.getJointTranslation( ) >= joint
				.getUpperLimit( );
		if ( hasBeenExploded ) {
			joint.setMotorSpeed( -joint.getMotorSpeed( ) ); // flip speed of
															// motor so it
															// pops.
			hasBeenExploded = false;
		}
		if ( atUpperLimit ) {
			joint.setMotorSpeed( -joint.getMotorSpeed( ) );
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
