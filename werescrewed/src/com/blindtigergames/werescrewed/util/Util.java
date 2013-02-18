package com.blindtigergames.werescrewed.util;

import com.badlogic.gdx.math.Vector2;

/**
 * A utility class that has random useful things like PI definitions and such
 * 
 * @author stew
 * 
 */

public class Util {

	/***
	 * Box2D to pixels conversion <br />
	 * <br />
	 * This number means 1 meter equals 256 pixels. That means the biggest
	 * in-game object (10 meters) we can use is 2560 pixels wide, which is much
	 * bigger than our max screen resolution so it should be enough.
	 */
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
	public static final float DEG_TO_RAD = 0.0174532925199432957f;
	public static final float RAD_TO_DEG = 57.295779513082320876f;
	public static final float PI = 3.141592653589793f;
	public static final float TWO_PI = 2*3.141592653589793f;
	
	/**
	 * Collision Categories and masks for every 
	 * object that needs them
	 */
	public static final short CATEGORY_PLAYER = 0x0002;
	public static final short CATEGORY_SUBPLAYER = 0x0004;
	public static final short DYNAMIC_OBJECTS = 0x0003;
	public static final short KINEMATIC_OBJECTS = 0x0010;
	public static final short CATEGORY_SUBPLATFORM = 0x0020;
	public static final short CATEGORY_SCREWS = 0x0040;
	public static final short CATEGORY_ROPE = 0x0080;
	public static final short CATEGORY_NOTHING = 0x0000;
	public static final short CATEGORY_EVERYTHING = -1;
	
	/**
	 * PointOnCircle()
	 * @author stew
	 * @param radius
	 *            FLOAT
	 * @param angleInRadians
	 *            YO
	 * @param origin
	 *            derp
	 * @return
	 */
	public static Vector2 PointOnCircle( float radius, float angleInRadians,
			Vector2 origin ) {
		// Convert from degrees to radians via multiplication by PI/180
		float x = ( float ) ( radius * Math.cos( angleInRadians ) ) + origin.x;
		float y = ( float ) ( radius * Math.sin( angleInRadians ) ) + origin.y;

		return new Vector2( x, y );
	}

	/**
	 * Finds the angle between 2 points in radians. Correctly returns the angle
	 * rather than NaN since atan() only has a range between -pi/2 -> pi/2
	 * TODO: this may have bugs for pi and 0.
	 * 
	 * @param pointA
	 *            as vector2
	 * @param pointB
	 *            as vector2
	 * @return float angle in radians
	 * @author stew
	 */
	public static float angleBetweenPoints( Vector2 pointA, Vector2 pointB ) {
		float angle = ( float ) Math.atan( ( pointB.y - pointA.y )
				/ ( pointB.x - pointA.x ) );
		if ( Float.isNaN( angle ) ) {
			if ( pointA.y > pointB.y ) { // pointA is above pointB
				return ( float ) Math.PI / 2;
			} else if ( pointB.y > pointA.y ) {// pointA is below pointB
				return ( float ) ( 3 * Math.PI / 2 );
			} else {// pointA & B are the same!!
				return 0; // better than NaN!
			}
		} else {
			return angle;
		}
	}
	
}
