package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.util.Util;

public class LerpFromCurrentPos implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private float speed;
	private float alpha = 0;
	private boolean done = false;
	private PuzzleType puzzleType;
	private LinearAxis axis;

	/**
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param loop
	 * @param type does the puzzle override the platforms mover or just move once
	 */
	public LerpFromCurrentPos ( Vector2 endingPoint, float speed,
			boolean loop, PuzzleType type, LinearAxis axis ) {
		this.endPoint = endingPoint.cpy( );
		this.speed = speed;
		this.axis = axis;
		puzzleType = type;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		alpha += speed;
		if ( alpha >= 1 ) {
				done = true;
				alpha = 1;
		} else if ( alpha <= 0 ) {
			done = true;
			alpha = -1;
		}
		beginningPoint = body.getPosition( ).cpy( );
		beginningPoint.lerp( endPoint, alpha );
		if ( axis == LinearAxis.VERTICAL ) {
			body.setTransform( body.getPosition( ).x, beginningPoint.y * Util.PIXEL_TO_BOX, 0.0f );
		} else if ( axis == LinearAxis.HORIZONTAL ) {
			body.setTransform( beginningPoint.x * Util.PIXEL_TO_BOX, body.getPosition( ).y, 0.0f );			
		} else {
			body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );			
		}
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
			beginningPoint = p.body.getPosition( ).cpy( ).mul( Util.BOX_TO_PIXEL );
			beginningPoint.lerp( endPoint, screwVal );
			if ( axis == LinearAxis.VERTICAL ) {
				p.setLocalPos( p.getLocalPos( ).x, beginningPoint.y );
			} else if ( axis == LinearAxis.HORIZONTAL ) {
				p.setLocalPos( beginningPoint.x, p.getLocalPos( ).y );			
			} else {
				p.setLocalPos( beginningPoint.y, 0.0f );		
			}
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
