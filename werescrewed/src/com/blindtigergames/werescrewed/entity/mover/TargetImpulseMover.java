package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Use with a dynamic body. Will give an impulse on the body to target a
 * coordinate
 * 
 * @author stew
 * 
 */
public class TargetImpulseMover implements IMover {

	Vector2 targetPixel;
	/**
	 * Where the impulse is applied relative to the center of the body
	 */
	public Vector2 impulsePoint;
	public float impulseStrength;
	/**
	 * If buoyant is true, this mover will act like the object is floating on
	 * water in which an impulse will only be applied to the y axis when the
	 * object is below the target y axis
	 */
	public boolean buoyant;

	/**
	 * Impulse will start to slow down once the body you're trying to move is
	 * within this radius.
	 */
	public float minImpulseRadius;
	private float minImpulseRadiusSquared;

	/**
	 * Full constructor to build a mover that targets a dynamic body to a
	 * certain position.
	 * 
	 * @param targetPixel
	 * @param impulsePoint
	 * @param impulseStrength
	 * @param buoyant
	 * @param minImpulseRadius
	 */
	public TargetImpulseMover( Vector2 targetPixel, Vector2 impulsePoint,
			float impulseStrength, boolean buoyant, float minImpulseRadius ) {
		this.targetPixel = targetPixel;
		this.impulsePoint = impulsePoint;
		this.impulseStrength = impulseStrength;
		this.buoyant = buoyant;
		this.minImpulseRadius = minImpulseRadius;
		this.minImpulseRadiusSquared = minImpulseRadius * minImpulseRadius;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		Vector2 impulseTarget = targetPixel.cpy( ).sub(
				body.getWorldCenter( ).mul( Util.BOX_TO_PIXEL ) );

		// Vector2 newImpulsePoint = impulsePoint.cpy().rotate( body.getAngle(
		// )*Util.RAD_TO_DEG );

		if ( buoyant && impulseTarget.y > 0 ) {
			body.applyLinearImpulse( impulseTarget.mul( 0, impulseStrength ),
					impulsePoint );
		} else {
			float disSq = impulseTarget.len2( );
			float strength = 0;
			if ( disSq > minImpulseRadiusSquared ) {
				strength = impulseStrength;
			} else if ( disSq != 0 ) {
				// scale impulse by distance from center linearly.
				strength = disSq / minImpulseRadiusSquared * impulseStrength;
			}
			// Gdx.app.log( "TargetImpulse:", " "+strength );
			impulseTarget.nor( ).mul( strength );
			body.applyLinearImpulse( impulseTarget, impulsePoint );
		}
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		// TODO Auto-generated method stub

	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

}
