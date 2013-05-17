package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class CannonLaunchMover implements IMover {

	Skeleton cannon;
	float impulseStrength;
	float delay;

	public CannonLaunchMover( Skeleton cannon, float impulseStrength,
			float delaySeconds ) {
		this.impulseStrength = impulseStrength;
		this.cannon = cannon;
		this.delay = delaySeconds;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		delay -= deltaTime;
		if ( delay <= 0f ) {
			// Gdx.app.log( "CannonLaunchMover", "LAUNCHING!" );
			Vector2 impulseDirection = Util.PointOnCircle( impulseStrength,
					cannon.body.getAngle( ) + Util.HALF_PI, new Vector2( ) );
			body.applyLinearImpulse( impulseDirection, body.getWorldCenter( ) );
			( ( Entity ) ( body.getUserData( ) ) ).setMoverNullAtCurrentState( ); // delete
																					// this
																					// mover!
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