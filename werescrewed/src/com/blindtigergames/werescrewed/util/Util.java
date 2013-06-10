package com.blindtigergames.werescrewed.util;

import java.util.Random;

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
	public static final float TWO_PI = 2 * PI;
	public static final float HALF_PI = PI / 2;
	public static final float FOURTH_PI = PI / 4;
	public static final float MIN_VALUE = 0x0.000002P-126f; // 1.4e-45f

	/**
	 * Collision Categories and masks for every object that needs them
	 */
	public static final short CATEGORY_PLAYER = 0x0002;
	public static final short CATEGORY_SUBPLAYER = 0x0004;
	public static final short CATEGORY_CHECKPOINTS = 0x0008;
	public static final short CATEGORY_PLATFORMS = 0x0010;
	public static final short CATEGORY_SUBPLATFORM = 0x0020;
	public static final short CATEGORY_SCREWS = 0x0040;
	public static final short CATEGORY_ROPE = 0x0080;
	public static final short CATEGROY_HAZARD = 0x0100;
	public static final short CATEGORY_SKELS = 0x2000;
	public static final short CATEGORY_IGNORE = 0x1000;
	public static final short CATEGORY_NOTHING = 0x0000;
	public static final short CATEGORY_EVERYTHING = -1;
	
	public static final Random r = new Random();

	/**
	 * Size outside of a skeleton that turns it on / off
	 */
	public static final float SKELETON_ACTIVE_BORDER = 2000f;// in pixels

	/**
	 * PointOnCircle()
	 * 
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
	 * Finds the angle between 2 points in radians. Won't ever return NaN. TODO:
	 * this may have bugs for pi and 0.
	 * 
	 * @param pointA
	 *            as vector2
	 * @param pointB
	 *            as vector2
	 * @return float angle in radians
	 * @author stew
	 */
	public static float angleBetweenPoints( Vector2 pointA, Vector2 pointB ) {
		float angle = ( float ) Math.atan2( ( pointB.y - pointA.y ),
				( pointB.x - pointA.x ) );
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
	
	public static float binom(){
		return r.nextFloat( )-r.nextFloat( );
	}
	
	public static int clamp(int n, int l, int h)
	{
	    return (n > h ? h : (n < l ? l : n));
	}

	public static float clamp(float n, float l, float h)
	{
	    return (n > h ? h : (n < l ? l : n));
	}
}
