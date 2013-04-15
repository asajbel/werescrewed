package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class ParallaxMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private float speed;
	private float alpha = 0;
	private PuzzleType puzzleType;
	private Camera cameraControl;
	private Vector3 oldCameraPos;
	private boolean moveContinuous;
	private float oneStep = 1.33f;

	/**
	 * builds movement of a single layer of parallax scrolling
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 */
	public ParallaxMover ( Vector2 beginningPoint, Vector2 endingPoint, float speed, float alpha, Camera camera, boolean moveContinuous ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed = Math.abs( speed );
		this.alpha = alpha;
		this.cameraControl = camera;
		this.oldCameraPos = camera.position.cpy( );
		this.moveContinuous = moveContinuous;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		if ( moveContinuous ) {
			alpha += speed;
		} 
		if ( cameraControl != null ) {
			float steps = 0;
			float newPos = 0;
			float oldPos = 0;
			if ( beginningPoint.x != endPoint.x ) {
				newPos = cameraControl.position.x;
				oldPos = oldCameraPos.x;
			} else if ( beginningPoint.y != endPoint.y ) {
				newPos = cameraControl.position.y;
				oldPos = oldCameraPos.y;				
			}
			if ( newPos > oldPos ) {
				steps = ( newPos - oldCameraPos.x ) /oneStep;
				if ( beginningPoint.x > endPoint.x ) {
					alpha += steps * speed;
				} else {
					alpha -= steps * speed;					
				}
			} else if ( newPos < oldPos ) {
				steps = ( oldPos - newPos ) /oneStep;
				if ( beginningPoint.x > endPoint.x ) {
					alpha -= steps * speed;
				} else {
					alpha += steps * speed;					
				}
			}
			oldCameraPos = cameraControl.position.cpy( );
		}
		if ( alpha >= 1 ) {
			//body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
			alpha = 0;
		} else if ( alpha <= 0 ) {
			alpha = 1;
		}
		Vector2 temp = beginningPoint.cpy( );
		beginningPoint.lerp( endPoint, alpha );
		body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
		beginningPoint = temp;			
	}
	
	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		//parallax movers shouldn't be used in puzzles
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
