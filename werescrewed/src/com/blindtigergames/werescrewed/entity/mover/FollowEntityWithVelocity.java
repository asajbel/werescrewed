package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class FollowEntityWithVelocity implements IMover {

	private Vector2 currentPoint;
	private Vector2 offset;
	private Entity entity;
	private Vector2 speed;
	private float alpha = 0;
	private boolean below = false;
	private boolean toTheLeft = false;
	private boolean pastUp = false;
	private boolean pastBy = false;
	private PuzzleType puzzleType;

	/**
	 * use this contructor of lerp mover to create a auto moving lerp either an
	 * initial mover or turned on/off by a puzzle screw with a certain amount of
	 * loops or continuosly looping
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param loop
	 *            continuosly looping or not
	 * @param LinearAxis
	 *            which axis this moves on
	 * @param loopTime
	 *            how many loops this goes through
	 */
	public FollowEntityWithVelocity( Vector2 beginningPoint, Entity entity,
			Vector2 offset, Vector2 speed ) {
		this.currentPoint = beginningPoint.cpy( );
		this.offset = offset.cpy( );
		this.entity = entity;
		if ( beginningPoint.x < entity.getPositionPixel( ).x ) {
			toTheLeft = true;
		}
		if ( beginningPoint.y < entity.getPositionPixel( ).y ) {
			below = true;
		}
		this.speed = speed;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		currentPoint = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		float speedX = entity.getPositionPixel( ).x - currentPoint.x;
		if ( currentPoint.x > entity.getPositionPixel( ).x && toTheLeft ) {
			speed.x = speedX * 0.1f;
			toTheLeft = false;
		} else if ( currentPoint.x < entity.getPositionPixel( ).x
				&& !toTheLeft ) {
			speed.x = speedX * 0.1f;
			toTheLeft = true;
		}
		float speedY = entity.getPositionPixel( ).y - currentPoint.y;
		if ( currentPoint.y > entity.getPositionPixel( ).y && below ) {
			speed.y = speedY * 0.1f;
			below = false;
		} else if ( currentPoint.y < entity.getPositionPixel( ).y && !below ) {
			speed.y = speedY * 0.1f;
			below = true;
		}
		Gdx.app.log( "Follow entity with velocity", speed.toString( ));
		body.setLinearVelocity( speed );
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		alpha = screwVal;
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
