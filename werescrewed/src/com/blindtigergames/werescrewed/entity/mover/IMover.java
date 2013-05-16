package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

/*******************************************************************************
 * IMover Interface - Move a Box2D body
 * 
 ******************************************************************************/
public interface IMover {

	/**
	 * Calculates movement then applies to body
	 * 
	 * @param Body
	 *            - The body to apply the movement to
	 * @param SteeringOutput
	 *            - Optional additional movement to apply to body
	 */
	void move( float deltaTime, Body body );

	/**
	 * is called from a puzzleManager and not applied to the body every step
	 * 
	 * @param screwVal
	 *            - the 0 - maxdepth value of the screw
	 * @param platform
	 *            - the platform the will be altered by this mover
	 */
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p );

	/**
	 * returns the behavior for which this mover will be used in a puzzle
	 * 
	 * @return MoverType
	 */
	public PuzzleType getMoverType( );
}
