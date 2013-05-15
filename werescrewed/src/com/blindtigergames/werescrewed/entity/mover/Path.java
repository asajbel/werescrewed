package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Path {
	protected Array< PathPoint > points;

	public Path( ) {
		points = new Array< PathPoint >( );
	}

	public void addPoint( Vector2 p ) {
		this.setPoint( points.size, p );
	}

	public void addPoint( PathPoint p ) {
		this.setPoint( points.size, p );
	}

	public void setPoint( int index, Vector2 p ) {
		this.setPoint( index, new PathPoint( ).position( p ) );
	}

	public void setPoint( int index, PathPoint p ) {
		if ( index > points.size )
			points.ensureCapacity( index );
		points.insert( index, p );
	}

	public Vector2 getPoint( int index ) {
		return points.get( index ).position;
	}

	protected float lerp( float s, float f, float i ) {
		return ( ( 1 - i ) * s + ( i * f ) );
	}

	protected Vector2 lerp( Vector2 s, Vector2 f, float i ) {
		return new Vector2( lerp( s.x, f.x, i ), lerp( s.y, f.y, i ) );
	}

	protected Vector2 lerp( PathPoint s, PathPoint f, float i ) {
		return lerp( s.position, f.position, i );
	}

	public Vector2 getPositionAt( float i ) {
		if ( points.size == 0 ) // No points = no position
			return Vector2.Zero;
		if ( points.size == 1 ) // One point = always point 0.
			return points.get( 0 ).position;
		int s = ( int ) ( Math.floor( i * points.size ) );
		int f = ( int ) ( Math.ceil( i * points.size ) );
		if ( s == f ) // Same point = exact position
			return points.get( s ).position;
		float j = ( i * points.size ) - s; // Get the decimal part
		return lerp( points.get( s ), points.get( f ), j );
	}

	public Vector2 getPositionFromPoint( int s, float i ) {
		return lerp( points.get( s ), points.get( s + ( int ) Math.ceil( i ) ),
				i - ( float ) Math.floor( i ) );
	}

}
