package com.blindtigergames.werescrewed.input.mappings;

import java.lang.reflect.Field;

import com.badlogic.gdx.controllers.Controller;

/**
 * Button and axis mapping {@link Controller}.
 * @author Anders
 *
 */
public class Mapping {
	public static final String ID = "Android Direct Input Game Controller";
	public static final int BUTTON_X;
	public static final int BUTTON_S;
	public static final int BUTTON_O;
	public static final int BUTTON_T;
	public static final int BUTTON_DPAD_UP;
	public static final int BUTTON_DPAD_DOWN;
	public static final int BUTTON_DPAD_RIGHT;
	public static final int BUTTON_DPAD_LEFT;
	public static final int BUTTON_L1;
	public static final int BUTTON_L2;
	public static final int BUTTON_L3;
	public static final int BUTTON_R1;
	public static final int BUTTON_R2;
	public static final int BUTTON_R3;
	public static final int AXIS_LEFT_X;
	public static final int AXIS_LEFT_Y;
	public static final int AXIS_LEFT_TRIGGER;
	public static final int AXIS_RIGHT_X;
	public static final int AXIS_RIGHT_Y;
	public static final int AXIS_RIGHT_TRIGGER;
	public static final int BUTTON_START;
	public static final int BUTTON_SELECT; 
	/** whether the app is running on a real Ouya device **/
	public static final boolean runningOnAndroid;
	
	static {
		boolean isAndroid = false;
		try {
			Class<?> buildClass = Class.forName("android.os.Build");
			Field deviceField = buildClass.getDeclaredField("DEVICE");
			isAndroid = "cardhu".equals(deviceField.get(null));
		} catch(Exception e) {
		}
		runningOnAndroid = isAndroid;
		
		if(isAndroid) {
			BUTTON_X = 96;
			BUTTON_S = 99;
			BUTTON_T = 100;
			BUTTON_O = 97;
			BUTTON_DPAD_UP = 19;
			BUTTON_DPAD_DOWN = 20;
			BUTTON_DPAD_RIGHT = 22;
			BUTTON_DPAD_LEFT = 21;
			BUTTON_L1 = 104;
			BUTTON_L2 = 102;
			BUTTON_L3 = 106;
			BUTTON_R1 = 105;
			BUTTON_R2 = 103;
			BUTTON_R3 = 107;
			AXIS_LEFT_X = 0;
			AXIS_LEFT_Y = 1;
			AXIS_LEFT_TRIGGER = 2;
			AXIS_RIGHT_X = 3;
			AXIS_RIGHT_Y = 4;
			AXIS_RIGHT_TRIGGER = 5;
			BUTTON_SELECT = 4;
			BUTTON_START = 108;
		} else {
			BUTTON_X = 0;
			BUTTON_O = 1;
			BUTTON_S = 2;
			BUTTON_T = 3;
			BUTTON_DPAD_UP = 104;
			BUTTON_DPAD_DOWN = 105;
			BUTTON_DPAD_RIGHT = 108;
			BUTTON_DPAD_LEFT = 109;
			BUTTON_L1 = 7;
			BUTTON_L2 = 6;
			BUTTON_L3 = 10;
			BUTTON_R1 = 5;
			BUTTON_R2 = 4;
			BUTTON_R3 = 11;
			AXIS_LEFT_X = 1;
			AXIS_LEFT_Y = 0;
			AXIS_LEFT_TRIGGER = 2;
			AXIS_RIGHT_X = 3;
			AXIS_RIGHT_Y = 2;
			AXIS_RIGHT_TRIGGER = 4;
			BUTTON_SELECT = 6;
			BUTTON_START = 7;
		}
	}
}
