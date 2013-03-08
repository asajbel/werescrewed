package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.util.Util;

public class ParallaxMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private float speed;
	private float alpha = 0;
	private PuzzleType puzzleType;

	/**
	 * builds movement of a single layer of parallax scrolling
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 */
	public ParallaxMover ( Vector2 beginningPoint, Vector2 endingPoint, float speed, float alpha ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed = Math.abs( speed );
		this.alpha = alpha;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		alpha += speed;
		if ( alpha >= 1 ) {
			//body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
			alpha = 0;
		} 
		Vector2 temp = beginningPoint.cpy( );
		beginningPoint.lerp( endPoint, alpha );
		body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
		beginningPoint = temp;
	}
	
	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		//parallax movers shouldn't be used in puzzles
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
