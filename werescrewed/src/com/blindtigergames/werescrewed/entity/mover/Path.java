package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Path {
	protected Array<Vector2> points;

	public Path( ) {
		points = new Array<Vector2>();
	}
	
	protected float lerp(float s, float f, float i){
		return ((1-i)*s + (i * f));
	}
	
	protected Vector2 lerp (Vector2 s, Vector2 f, float i){
		return new Vector2(lerp(s.x,f.x,i), lerp(s.y,f.y,i));
	}
	
	public Vector2 getPositionAt(float i){
		if (points.size == 0) //No points = no position
			return Vector2.Zero; 
		if (points.size == 1) //One point = always point 0.
			return points.get( 0 );
		int s = (int)(Math.floor( i * points.size ));
		int f = (int)(Math.ceil( i * points.size ));
		if (s == f) // Same point = exact position
			return points.get( s );
		float j = (i * points.size) - s; //Get the decimal part
		return lerp(points.get( s ), points.get( f ), j);
	}
	
}
