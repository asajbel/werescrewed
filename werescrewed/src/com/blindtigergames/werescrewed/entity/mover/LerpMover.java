package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class LerpMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private float speed;
	private float alpha = 0;

	public LerpMover( Vector2 bp, Vector2 ep, float speed ) {
		beginningPoint = new Vector2( bp.x, bp.y );
		endPoint = new Vector2( ep.x, ep.y );
		this.speed = speed;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		alpha += speed;
		if ( alpha >= 1 ) {
			speed *= -1;
		} else if ( alpha <= 0 ) {
			speed *= -1;
		}
		Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y);
		beginningPoint.lerp( endPoint, alpha );
		body.setTransform( beginningPoint, 0.0f );
		beginningPoint = temp;
	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
	}
	
	public void runPuzzleMovement( float screwVal, Body body ) {
		Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y);
		Gdx.app.log( "puzzle manager", "" + screwVal );
		beginningPoint.lerp( endPoint, screwVal );
		body.setTransform( beginningPoint, 0.0f );
		beginningPoint = temp;
	}
}
