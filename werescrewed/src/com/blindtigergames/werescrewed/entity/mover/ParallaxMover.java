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
	private LinearAxis axis;
	private boolean loopRepeat=true;
	private float alphaOffset;

	/**
	 * builds movement of a single layer of parallax scrolling
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed -- percentage of total distance to travel
	 * @param alpha -- STARTING POSITION percent
	 * @param camera, level camera usually
	 * @param moveContinuous -- always move alpha by speed each update
	 */
	public ParallaxMover( Vector2 beginningPoint, Vector2 endingPoint, 
			float speed, float alpha, Camera camera, boolean moveContinuous,
			LinearAxis axis, float alphaOffset ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed =  speed;
		this.alphaOffset=alphaOffset;
		this.alpha = alpha;
		this.cameraControl = camera;
		if(camera!=null)
			this.oldCameraPos = camera.position.cpy( );
		this.moveContinuous = moveContinuous;
		this.axis = axis;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
	}
	
	/**
	 * Build it with an alpha offset..
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param alpha
	 * @param camera
	 * @param moveContinuous
	 * @param axis
	 */
	public ParallaxMover( Vector2 beginningPoint, Vector2 endingPoint, 
			float speed, float alpha, Camera camera, boolean moveContinuous,
			LinearAxis axis ) {
		this( beginningPoint, endingPoint, speed, alpha, camera, moveContinuous, axis, 0f );
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
			if ( axis == LinearAxis.HORIZONTAL ) {
				newPos = cameraControl.position.x;
				oldPos = oldCameraPos.x;
			} else if ( axis == LinearAxis.VERTICAL ) {
				newPos = cameraControl.position.y;
				oldPos = oldCameraPos.y;
			}
			if ( oldPos != newPos ) {
				steps = ( newPos - oldPos ) / oneStep;
				if ( axis == LinearAxis.HORIZONTAL ) {
					if ( beginningPoint.x > endPoint.x ) {
						alpha += steps * speed;
					} else {
						alpha -= steps * speed;
					}
				} else {
					if ( beginningPoint.y > endPoint.y ) {
						alpha += steps * speed;
					} else {
						alpha -= steps * speed;
					}
				}
			}
			oldCameraPos = cameraControl.position.cpy( );
		}
		if(loopRepeat){
			if ( alpha >= 1 ) {
				alpha = 0+alphaOffset;
			} else if ( alpha <= 0 ) {
				alpha = 1+alphaOffset;
			}
		}
		Vector2 temp = beginningPoint.cpy( );
		beginningPoint.lerp( endPoint, alpha );
		body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
		beginningPoint = temp.cpy( );
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		// parallax movers shouldn't be used in puzzles
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
	
	public void setLoopRepeat(boolean hasLoopRepeat){
		this.loopRepeat=hasLoopRepeat;
	}
	
	public void setAlphaOffset(float alphaOffset){
		this.alphaOffset = alphaOffset;
	}
}
