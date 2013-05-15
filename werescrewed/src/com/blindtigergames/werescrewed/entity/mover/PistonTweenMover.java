package com.blindtigergames.werescrewed.entity.mover;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;

/**
 * This mover will infinitely move a platform in a piston fashion. Use
 * PistonPuzzleTweenMover for pistons that shoot off from puzzle screw
 * 
 * @author stew
 * 
 */
public class PistonTweenMover extends TweenMover implements IMover {

	private Platform pistonPlatform;
	private Vector2 pistonDestination; // where the piston shoots to in piston
										// local coord pixels
	private float timeUp, timeDown;
	private float delayDown, delayUp;
	private float delayOnce;
	// private Tween tween;
	private boolean isGoingUp;

	/**
	 * Construct a piston mover that must take continually loops it's piston
	 * movement.
	 * 
	 * @param pistonPlatform
	 *            Must be a kinematic platform
	 * @param pistonDestination
	 *            end Destination, in local coordinates pixels
	 * @param timeUp
	 *            seconds it takes to reach top (speed of piston)
	 * @param timeDown
	 *            seconds to reach bottom state (speed!!)
	 * @param upDelay
	 *            Rest time after the piston comes back down
	 * @param downDelay
	 *            rest time when piston is at top (destination position)
	 * @param delayOnce
	 *            very initial delay, happens only once before anything else
	 *            happens
	 */
	public PistonTweenMover( Platform pistonPlatform,
			Vector2 pistonDestination, float timeUp, float timeDown,
			float delayUp, float delayDown, float delayOnce ) {
		super( true );
		this.pistonPlatform = pistonPlatform;
		this.pistonDestination = pistonDestination.cpy( );
		this.timeUp = timeUp;
		this.timeDown = timeDown;
		this.delayUp = delayUp;
		this.delayDown = delayDown;
		this.delayOnce = delayOnce;
		addTween( Tween
				.to( pistonPlatform, PlatformAccessor.LOCAL_POS_XY, this.timeUp )
				.delay( this.delayOnce )
				.target( this.pistonDestination.x, this.pistonDestination.y )
				.delay( this.delayUp ).start( ) );
		this.isGoingUp = true;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		super.move( deltaTime, body ); // this updates the tween
		if ( getFirstTween( ).isFinished( ) ) {
			isGoingUp = !isGoingUp;
			destroyAllTweens( );
			if ( isGoingUp ) {
				addTween( Tween
						.to( pistonPlatform, PlatformAccessor.LOCAL_POS_XY,
								this.timeUp )
						.target( this.pistonDestination.x,
								this.pistonDestination.y ).delay( this.delayUp )
						.start( ) );
			} else {
				addTween( Tween
						.to( pistonPlatform, PlatformAccessor.LOCAL_POS_XY,
								this.timeDown ).target( 0f, 0f )
						.delay( this.delayDown ).start( ) );
			}
		}
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		System.out.println( "piston mover run puzzle" );
		// addwaypoint to originposition then reset
		// DON"T USE THIS MOVER FOR THIS
	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}