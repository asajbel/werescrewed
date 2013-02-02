package com.blindtigergames.werescrewed.util;

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
	
	/**
	 * Collision Categories and masks for every 
	 * object that needs them
	 */
	public static final short CATEGORY_SCREWS = 0x0008;
	public static final short CATEGORY_PLAYER = 0x0001;
	public static final short CATEGORY_SUBPLAYER = 0x0010;
	public static final short DYNAMIC_OBJECTS = 0x0004;
	public static final short KINEMATIC_OBJECTS = 0x0005;
}
