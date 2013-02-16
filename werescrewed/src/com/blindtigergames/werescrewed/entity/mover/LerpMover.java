package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.util.Util;

public class LerpMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private float speed;
	private float alpha = 0;
	private boolean loop;
	private boolean done = false;
	private PuzzleType puzzleType;

	/**
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param loop
	 * @param type does the puzzle override the platforms mover or just move once
	 */
	public LerpMover( Vector2 beginningPoint, Vector2 endingPoint, float speed,
			boolean loop, PuzzleType type ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed = speed;
		this.loop = loop;
		puzzleType = type;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		alpha += speed;
		if ( alpha >= 1 ) {
			if ( loop ) {
				speed *= -1;
			} else {
				done = true;
				alpha = 1;
			}
		} else if ( alpha <= 0 ) {
			speed *= -1;
		}
		Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y );
		beginningPoint.lerp( endPoint, alpha );
		body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
		beginningPoint = temp;
	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		move( deltaTime, body );
	}

	public boolean atEnd( ) {
		return done;
	}

	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		if ( puzzleType == PuzzleType.PUZZLE_SCREW_CONTROL ) {
			Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y );
			beginningPoint.lerp( endPoint, screwVal );
			p.setLocalPos( beginningPoint );
			// body.setTransform( beginningPoint, 0.0f );
			beginningPoint = temp;
		} else if ( puzzleType == PuzzleType.OVERRIDE_ENTITY_MOVER ) {
			if ( p.mover == null ) {
				p.mover = this;
			}
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
