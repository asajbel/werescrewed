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
	 * @param speed of lerp
	 * @param loop on or off
	 */
	public LerpMover( Vector2 beginningPoint, Vector2 endingPoint, float speed, boolean loop ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed = speed;
		this.loop = loop;
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
		body.setTransform( beginningPoint, 0.0f );
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
		Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y );
		beginningPoint.lerp( endPoint, screwVal );
		p.setLocalPos( beginningPoint.mul( Util.BOX_TO_PIXEL ) );
		// body.setTransform( beginningPoint, 0.0f );
		beginningPoint = temp;
	}
	
	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
