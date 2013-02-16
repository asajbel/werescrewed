package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Gdx;
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

	private boolean isConnected;

	private boolean pausePressed;
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean jumpPressed;
	private boolean downPressed;
	private boolean upPressed;
	private boolean analogUsed;
	private boolean screwingPressed;
	private boolean unscrewingPressed;

	private boolean attachScrewPressed;

	// axisX and axisY represent the point where the analog stick is
	private float axisX, axisY, axisRX, axisRY;
	int angleInt, prevAngle, currAngle;
	double angle;

	// Using xbox face button names. B, X, Y are unused for now
	private final static int buttonA = 0;
	// private final static int buttonB = 1;
	// private final static int buttonX = 2;
	// private final static int buttonY = 3;
	private final static int bumperR = 5;
	// private final static int trigger = 4;
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
		 * 
		 * trigger is button 4: left is positive, right is negative
		 */
		//System.out.println("axis: " + controller.getAxis( 1 ) ); // 0 = vertical, 1 =
		// horizontal
		if ( buttonIndex == 4 ) {
			if ( axisPoint < -0.3f || axisPoint > 0.3f )
				attachScrewPressed = true;
			else
				attachScrewPressed = false;
		}
		axisY = controller.getAxis( 0 );
		axisX = controller.getAxis( 1 );
		axisRX = controller.getAxis( 2 );
		axisRY = controller.getAxis( 3 );
		
		if ( axisY < 0.2f && axisY > -0.2f ) {
			upPressed = false;
			downPressed = false;
			analogUsed = false;
		}
		if ( axisX < 0.2f && axisX > -0.2f ) {
			rightPressed = false;
			leftPressed = false;
			analogUsed = false;
		}
		if ( axisX > 0.2f ) {
			rightPressed = true;
			analogUsed = true;
		}
		if ( axisX < -0.2f ) {
			leftPressed = true;
			analogUsed = true;
		}
		if ( axisY > 0.2f ) {
			downPressed = true;
			analogUsed = true;
		}
		if ( axisY < -0.2f ) {
			upPressed = true;
			analogUsed = true;
		}
		
		if(!( (axisRX < 0.01 && axisRY < 0.01)
				&& (axisRX > -0.01 && axisRY > -0.01)))
			rightStickScrew(controller);
		else {
			screwingPressed = false;
			unscrewingPressed = false;
		}
		
		System.out.println("sc: " + screwing() + ", unsc: " + unscrewing() );
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
		isConnected = true;

	}

	/**
	 * This function is called when a controller is disconnected
	 */
	@Override
	public void disconnected( Controller controller ) {
		isConnected = false;
		Gdx.app.log( null, "A CONTROLLER WAS DISCONNECTED" );

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
	 * Returns if controller is connected
	 * 
	 * @return boolean
	 * @author Ranveer
	 */

	public boolean isConnected( ) {
		return isConnected;
	}

	/**
	 * Checks to see if player is using analog
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean analogUsed( ) {
		return analogUsed;
	}

	/**
	 * Returns the x point of the analog stick
	 * 
	 * @return float
	 * @author Ranveer
	 */
	public float analogAxisX( ) {
		return axisX;
	}

	/**
	 * Returns the y point of the analog stick
	 * 
	 * @return float
	 * @author Ranveer
	 */
	public float analogAxisY( ) {
		return axisY;
	}

	/**
	 * Returns whether the pause button is pressed.
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean pausePressed( ) {
		return pausePressed;
	}

	/**
	 * Returns whether the move left button is pressed
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean leftPressed( ) {
		return leftPressed;
	}

	/**
	 * Returns whether the move right button is pressed
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean rightPressed( ) {
		return rightPressed;
	}

	/**
	 * Returns whether the jump button is pressed
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean jumpPressed( ) {
		return jumpPressed;
	}

	/**
	 * Returns whether the move down button is pressed
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean downPressed( ) {
		return downPressed;
	}

	/**
	 * Returns whether the up button is pressed
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean upPressed( ) {
		return upPressed;
	}

	/**
	 * Returns whether the attach to screw button is pressed
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean screwPressed( ) {
		return attachScrewPressed;
	}

	/**
	 * Returns whether trying to screw clockwise (righty tighty)
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean screwing( ) {
		return rightPressed || screwingPressed;
	}

	/**
	 * Returns whether trying to screw counter-clockwise (lefty loosely)
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean unscrewing( ) {
		return leftPressed || unscrewingPressed;
	}

	/**
	 * Function returns the what index the controller is (player 1 or player 2)
	 * 
	 * @param controller
	 * @return int
	 */

	@SuppressWarnings( "unused" )
	private int indexOf( Controller controller ) {
		return Controllers.getControllers( ).indexOf( controller, true );
	}

	private void rightStickScrew(Controller controller){
		prevAngle = currAngle;
		angleInt = (int) Math.toDegrees(Math.atan2( -axisRX, -axisRY )) + 180;
		currAngle = angleInt;
		//System.out.println("Currangle: " + currAngle + " prevAngle: " + prevAngle);
		//System.out.println("RX: " + axisRX + " RY: " + axisRY);
		
		if( currAngle  > prevAngle ){
			screwingPressed = true;
			unscrewingPressed = false;
		}
		else if ( currAngle < prevAngle ){
			unscrewingPressed = true;
			screwingPressed = false;
		}
	
	}
}