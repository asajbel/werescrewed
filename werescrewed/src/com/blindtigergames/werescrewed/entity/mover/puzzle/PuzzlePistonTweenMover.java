package com.blindtigergames.werescrewed.entity.mover.puzzle;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.TweenMover;
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
public class PuzzlePistonTweenMover extends TweenMover implements IMover {

	private Platform pistonPlatform;
	private Vector2 pistonDestination; // where the piston shoots to in piston
										// local coord pixels
	private float timeUp, timeDown;
	private float delayDown, delayUp;
	//@SuppressWarnings( "unused" )
	//private float delayOnce;
	private boolean isAtMaxScrewValue;

	// private Tween tween;
	// private boolean isGoingUp;

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
	 * @param delayUp
	 *            Rest time after the piston comes back down
	 * @param downDelay
	 *            limits how fast player can shoot piston once it's back downt o
	 *            rest it must wait this long
	 */
	public PuzzlePistonTweenMover( Platform pistonPlatform,
			Vector2 pistonDestination, float timeUp, float timeDown,
			float delayUp, float delayDown ) {
		super( false ); // we don't want to run synchronously
		this.pistonPlatform = pistonPlatform;
		this.pistonDestination = pistonDestination.cpy( );
		this.timeUp = timeUp;
		this.timeDown = timeDown;
		this.delayUp = delayUp;
		this.delayDown = delayDown;
		this.isAtMaxScrewValue = false;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		super.move( deltaTime, body ); // this updates the tween
		// System.out.println("updating puzzle piston tween mover");
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		// addwaypoint to originposition then reset
		if ( p.currentMover( ) == null && hasNoTweens( ) ) {
			if ( ( screwVal >= 0.9 && !isAtMaxScrewValue )
					|| ( screwVal <= 0.1 && isAtMaxScrewValue ) ) {
				isAtMaxScrewValue = !isAtMaxScrewValue;
				p.setMoverAtCurrentState( this );
				addTween( Tween
						.to( pistonPlatform, PlatformAccessor.LOCAL_POS_XY,
								this.timeUp )
						.target( this.pistonDestination.x,
								this.pistonDestination.y ).delay( this.delayUp )
						.ease( TweenEquations.easeNone ).start( ) );
				addTween( Tween
						.to( pistonPlatform, PlatformAccessor.LOCAL_POS_XY,
								this.timeDown ).target( 0, 0 )
						.delay( this.delayDown ).ease( TweenEquations.easeNone )
						.start( ) );
			}
		} else if ( hasNoTweens( ) ) {
			p.setMoverNullAtCurrentState( );
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return PuzzleType.ON_OFF_MOVER;
	}
}
