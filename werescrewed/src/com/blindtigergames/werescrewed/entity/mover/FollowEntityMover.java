package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class FollowEntityMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 offset;
	private Entity entity;
	private float speed;
	private float alpha = 0;
	private boolean done = false;
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
	public FollowEntityMover( Vector2 beginningPoint, Entity entity,
			Vector2 offset, float speed ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.offset = offset.cpy( );
		this.entity = entity;
		this.speed = speed;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		alpha += speed;
		if ( alpha >= 1 ) {
			done = true;
			alpha = 1;
		} else if ( alpha < 0 ) {
			done = true;
		}
		Vector2 temp = beginningPoint.cpy( );
		beginningPoint.lerp( entity.getPositionPixel( ).add( offset ), alpha );
		body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
		beginningPoint = temp;
	}

	public boolean atEnd( ) {
		return done;
	}

	public boolean atStart( ) {
		return alpha == 0;
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
