package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class FollowEntityWithVelocity implements IMover {

	private Vector2 currentPoint;
	private Entity entity;
	private PuzzleType puzzleType;
	private float timesFlewByX;
	private float timesFlewByY;
	private Boolean greaterThanX;
	private Boolean greaterThanY;
	float speedX;
	float speedY;

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
	public FollowEntityWithVelocity( Vector2 beginningPoint, Entity entity ) {
		this.currentPoint = beginningPoint.cpy( );
		this.entity = entity;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
		timesFlewByX = 1f;
		timesFlewByY = 1f;
		speedX = (entity.getPositionPixel( ).x - currentPoint.x )* timesFlewByX;
		speedY = (entity.getPositionPixel( ).y - currentPoint.y )* timesFlewByY;
		greaterThanX = (beginningPoint.x>entity.getPositionPixel( ).x) ? true : false;
		greaterThanY = (beginningPoint.y>entity.getPositionPixel( ).y) ? true : false;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		currentPoint = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		Boolean stillGreaterThanX = (currentPoint.x>entity.getPositionPixel( ).x) ? Boolean.TRUE : Boolean.FALSE;
		Boolean stillGreaterThanY = (currentPoint.y>entity.getPositionPixel( ).y) ? Boolean.TRUE : Boolean.FALSE;
		if ( !stillGreaterThanX.equals( greaterThanX ) ) {
			speedX = (entity.getPositionPixel( ).x - currentPoint.x );
		}
		if ( !stillGreaterThanY.equals( greaterThanY ) ) {
			speedY = (entity.getPositionPixel( ).y - currentPoint.y );
		}
		
		body.setLinearVelocity( speedX * Util.PIXEL_TO_BOX, speedY * Util.PIXEL_TO_BOX );
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		//dont use for puzzles
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
	
	/**
	 * change the entity that the body follows
	 */
	public void changeEntityToFollow( Entity entity ) {
		this.entity = entity;
		speedX = (entity.getPositionPixel( ).x - currentPoint.x )* timesFlewByX;
		speedY = (entity.getPositionPixel( ).y - currentPoint.y )* timesFlewByY;
		greaterThanX = (currentPoint.x>entity.getPositionPixel( ).x) ? true : false;
		greaterThanY = (currentPoint.y>entity.getPositionPixel( ).y) ? true : false;
	}
}
