package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * My controller listener class grabs input from controllers Booleans are
 * checked by player and the buttons are checked by this listener
 * 
 * @author Ranveer
 * 
 */

public class MyControllerListener implements ControllerListener {

	private boolean pausePressed;

	private boolean leftPressed;
	private boolean rightPressed;
	private boolean jumpPressed;
	private boolean downPressed;
	private boolean upPressed;

	private boolean attachScrewPressed;

	// Using xbox face button names
	private final static int buttonA = 0;
	// private final static int buttonB = 1;
	// private final static int buttonX = 2;
	// private final static int buttonY = 3;
	private final static int bumperR = 5;
	private final static int pause = 7;

	/**
	 * This function checks the analog sticks to see if they moved
	 * 
	 * @param controller
	 *            - which controller moved
	 * @param buttonIndex
	 *            - which axis
	 * @param axisPoint
	 *            - which quadrant is the stick in
	 */
	@Override
	public boolean axisMoved( Controller controller, int buttonIndex,
			float axisPoint ) {
		// System.out.println( indexOf(controller) + ":" + " button: " +
		// buttonIndex+ ", axis: " + axisPoint );
		/*
		 * for control sticks left stick button 0 - vertical - top is -1.0,
		 * bottom is 1.0 button 1 - horizontal - left is -1.0, right is 1.0
		 * 
		 * right stick button 2 - vertical button 3 - horizontal
		 */
		return false;
	}

	/**
	 * This function checks which button is hit on the controller and sets the
	 * appropriate boolean
	 */
	@Override
	public boolean buttonDown( Controller controller, int buttonIndex ) {
		// System.out.println("#" + indexOf(controller) + ", button " +
		// buttonIndex + " down");

		if ( buttonIndex == buttonA )
			jumpPressed = true;
		if ( buttonIndex == bumperR )
			attachScrewPressed = true;
		if ( buttonIndex == pause )
			pausePressed = true;
		return false;
	}

	/**
	 * This function checks when a button is released and sets the boolean to
	 * false
	 */

	@Override
	public boolean buttonUp( Controller controller, int buttonIndex ) {
		if ( buttonIndex == buttonA )
			jumpPressed = false;
		if ( buttonIndex == bumperR )
			attachScrewPressed = false;
		if ( buttonIndex == pause )
			pausePressed = false;

		return false;
	}

	/**
	 * This function is called when a new controller is inserted
	 */
	@Override
	public void connected( Controller controller ) {
		// TODO Auto-generated method stub

	}

	/**
	 * This function is called when a controller is disconnected
	 */
	@Override
	public void disconnected( Controller controller ) {
		// TODO Auto-generated method stub

	}

	/**
	 * This function checks the dpad to see which direction is hit it uses a
	 * enum called PovDirection (east, west, north, south)
	 */
	@Override
	public boolean povMoved( Controller controller, int buttonIndex,
			PovDirection direction ) {
		if ( direction == PovDirection.center ) {
			rightPressed = false;
			leftPressed = false;
			upPressed = false;
			downPressed = false;
		}
		if ( direction == PovDirection.east )
			rightPressed = true;
		if ( direction == PovDirection.west )
			leftPressed = true;
		if ( direction == PovDirection.north )
			upPressed = true;
		if ( direction == PovDirection.south )
			downPressed = true;
		return false;
	}

	// Unused
	@Override
	public boolean xSliderMoved( Controller controller, int sliderCode,
			boolean value ) {
		// TODO Auto-generated method stub
		return false;
	}

	// Unused
	@Override
	public boolean ySliderMoved( Controller controller, int sliderCode,
			boolean value ) {
		// TODO Auto-generated method stub
		return false;
	}

	// Unused
	@Override
	public boolean accelerometerMoved( Controller controller,
			int accelerometerCode, Vector3 value ) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns whether the pause button is pressed.
	 * 
	 * @return boolean
	 */
	public boolean pausePressed( ) {
		return pausePressed;
	}

	/**
	 * Returns whether the move left button is pressed
	 * 
	 * @return boolean
	 */
	public boolean leftPressed( ) {
		return leftPressed;
	}

	/**
	 * Returns whether the move right button is pressed
	 * 
	 * @return boolean
	 */
	public boolean rightPressed( ) {
		return rightPressed;
	}

	/**
	 * Returns whether the jump button is pressed
	 * 
	 * @return boolean
	 */
	public boolean jumpPressed( ) {
		return jumpPressed;
	}

	/**
	 * Returns whether the move down button is pressed
	 * 
	 * @return boolean
	 */
	public boolean downPressed( ) {
		return downPressed;
	}

	/**
	 * Returns whether the up button is pressed
	 * 
	 * @return boolean
	 */
	public boolean upPressed( ) {
		return upPressed;
	}

	/**
	 * Returns whether the attach to screw button is pressed
	 * 
	 * @return boolean
	 */
	public boolean screwPressed( ) {
		return attachScrewPressed;
	}

	/**
	 * Returns whether trying to screw clockwise (righty tighty)
	 * 
	 * @return boolean
	 */
	public boolean screwing( ) {
		return rightPressed;
	}

	/**
	 * Returns whether trying to screw counter-clockwise (lefty loosely)
	 * 
	 * @return boolean
	 */
	public boolean unscrewing( ) {
		return leftPressed;
	}

	/**
	 * Function returns the what index the controller is (player 1 or player 2)
	 * 
	 * @author Ranveer
	 * @param controller
	 * @return int
	 */

	@SuppressWarnings( "unused" )
	private int indexOf( Controller controller ) {
		return Controllers.getControllers( ).indexOf( controller, true );
	}

}