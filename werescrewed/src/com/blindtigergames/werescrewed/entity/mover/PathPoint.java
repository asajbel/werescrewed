package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;

public class PathPoint {
	Vector2 position;
	float angle;
	float time;

	public PathPoint( ) {
		position = new Vector2( );
		angle = 0.0f;
		time = 1.0f;
	}

	public PathPoint position( Vector2 p ) {
		position.x = p.x;
		position.y = p.y;
		return this;
	}

	public PathPoint rotation( float a ) {
		angle = a;
		return this;
	}

	public PathPoint time( float t ) {
		time = t;
		return this;
	}

}
