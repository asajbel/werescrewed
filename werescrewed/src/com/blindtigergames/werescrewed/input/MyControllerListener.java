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

	private boolean prevScrewingPressed, prevUnscrewingPressed;
	private boolean attachScrewPressed;

	private boolean debugScrewMode1 = true, debugScrewMode2, debugScrewMode3;
	// axisX and axisY represent the point where the analog stick is
	private float axisX, axisY, axisRX, axisRY;
	int angleInt, prevAngle = 0, currAngle = 0;
	int prevDirection, currDir;
	double angle;
	int currDirectionXAxis;
	int currDirectionYAxis;
	int currDirection = 0;
	int screwCounter = 0;

	// Using xbox face button names. B, X, Y are unused for now
	private final static int BUTTON_A = 0;
	private final static int BUTTON_B = 1;
	private final static int BUTTON_X = 2;
	private final static int BUTTON_Y = 3;
	private final static int BUMPER_RIGHT = 5;
	private final static int TRIGGER = 4;
	private final static int SELECT = 6;
	private final static int PAUSE = 7;
	
	private final static int SCREW_UP = 1;
	private final static int SCREW_RIGHT = 2;
	private final static int SCREW_DOWN = 3;
	private final static int SCREW_LEFT = 4;
	private final static int SCREW_COUNTER = 10;
	
	private final static float DEADZONE = 0.2f;
	private final static float ANALOG_CENTER = 0.01f;

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
		if ( buttonIndex == TRIGGER ) {
			if ( axisPoint < -0.3f || axisPoint > 0.3f )
				attachScrewPressed = true;
			else
				attachScrewPressed = false;
		}
		axisY = controller.getAxis( 0 );
		axisX = controller.getAxis( 1 );
		axisRX = controller.getAxis( 3 );
		axisRY = controller.getAxis( 2 );
		
		if ( axisY < DEADZONE && axisY > -DEADZONE ) {
			upPressed = false;
			downPressed = false;
			analogUsed = false;
		}
		if ( axisX < DEADZONE && axisX > -DEADZONE ) {
			rightPressed = false;
			leftPressed = false;
			analogUsed = false;
		}
		if ( axisX > DEADZONE ) {
			rightPressed = true;
			analogUsed = true;
		}
		if ( axisX < -DEADZONE ) {
			leftPressed = true;
			analogUsed = true;
		}
		if ( axisY > DEADZONE ) {
			downPressed = true;
			analogUsed = true;
		}
		if ( axisY < -DEADZONE ) {
			upPressed = true;
			analogUsed = true;
		}
		
		if(!( (axisRX < ANALOG_CENTER && axisRY < ANALOG_CENTER)
				&& (axisRX > -ANALOG_CENTER && axisRY > -ANALOG_CENTER)))
			rightStickScrew(controller);
		else {
			screwingPressed = false;
			unscrewingPressed = false;
			currDirection = 0;
			prevDirection = 0;
			screwCounter = 0;
			prevAngle = 0;
			currAngle = 0;
		}
		
		//System.out.println("sc: " + screwing() + ", unsc: " + unscrewing() );
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

		if( buttonIndex == SELECT){
			if(debugScrewMode1){
				 debugScrewMode1 = false;
				 debugScrewMode2 = true;
			}
			else if(debugScrewMode2){
				debugScrewMode2 = false;
				debugScrewMode3 = true;
			}
			else if( debugScrewMode3 ){
				debugScrewMode3 = false;
				debugScrewMode1 = true;
			}
			Gdx.app.log( "debug", "1: " + debugScrewMode1 + "2: " + debugScrewMode2 + "3: " + debugScrewMode3 );
		}
		

		if ( buttonIndex == BUTTON_A )
			jumpPressed = true;
		if ( buttonIndex == BUMPER_RIGHT )
			attachScrewPressed = true;
		if ( buttonIndex == PAUSE )
			pausePressed = true;
		return false;
		
	}

	/**
	 * This function checks when a button is released and sets the boolean to
	 * false
	 */

	@Override
	public boolean buttonUp( Controller controller, int buttonIndex ) {
		if ( buttonIndex == BUTTON_A )
			jumpPressed = false;
		if ( buttonIndex == BUMPER_RIGHT )
			attachScrewPressed = false;
		if ( buttonIndex == PAUSE )
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
		prevScrewingPressed = screwingPressed;
		if((prevScrewingPressed == screwingPressed))
			screwCounter++;
		if(screwCounter > SCREW_COUNTER) {
			screwingPressed = false;
			screwCounter = 0;
		}
		return screwingPressed;
	}

	/**
	 * Returns whether trying to screw counter-clockwise (lefty loosely)
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean unscrewing( ) {
		prevUnscrewingPressed = unscrewingPressed;
		if((prevUnscrewingPressed == unscrewingPressed))
			screwCounter++;
		if(screwCounter > SCREW_COUNTER) {
			unscrewingPressed = false;
			screwCounter = 0;
		}
		return unscrewingPressed;
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
		
		
		angleInt = (int) Math.toDegrees(Math.atan2( -axisRX, -axisRY )) + 180;
		currAngle = angleInt;
		//System.out.println("Currangle: " + currAngle + " prevAngle: " + prevAngle);
		//System.out.println("RX: " + axisRX + " RY: " + axisRY);


	if(debugScrewMode1) {
		if( prevAngle == 0 )
			prevAngle = currAngle;
		if( currAngle - prevAngle > 2 ){ 
			screwingPressed = true;
			unscrewingPressed = false;
			prevAngle = currAngle;
		}
		else if ( prevAngle - currAngle > 2 ){ 
			unscrewingPressed = true;
			screwingPressed = false;
			prevAngle = currAngle;
		}
	} else if (debugScrewMode2) {
		if( prevAngle == 0 )
			prevAngle = currAngle;
		
		if( currAngle > prevAngle ){ 
			screwingPressed = true;
			unscrewingPressed = false;
			prevAngle = currAngle;
		}
		else if ( prevAngle > currAngle ){ 
			unscrewingPressed = true;
			screwingPressed = false;
			prevAngle = currAngle;
		}
	} else if (debugScrewMode3) {
		
		//System.out.println( "rx: " + (int)axisRX + " ry: " + (int)axisRY);
		//System.out.println( "temp: " + temp + " Prev: " + prevDir + "counter: " + counter);

		prevAngle = currAngle;
		
		currDirection = 0;
		
		if((int) axisRX == 1.0f){
			currDirection = SCREW_RIGHT;
		}
		if((int) axisRX == -1.0f){
			currDirection = SCREW_LEFT;
		}
		if((int) axisRY == 1.0f ){
			currDirection = SCREW_DOWN;
		}
		if((int) axisRY == -1.0f ){
			currDirection = SCREW_UP;
		}
		
		if( screwCounter > 20 ) {
			unscrewingPressed = false;
			screwingPressed = false;
			currDirection = 0;
			screwCounter = 0;
		}
	
		if(currDirection == SCREW_UP){
			if(prevDirection == SCREW_LEFT){
				screwingPressed = true;
				unscrewingPressed = false;
				screwCounter = 0;
			}
			if(prevDirection == SCREW_RIGHT){
				screwingPressed = false;
				unscrewingPressed = true;
				screwCounter = 0;
			}
		}
		
		if(currDirection == SCREW_LEFT){
			if(prevDirection == SCREW_UP){
				screwingPressed = false;
				unscrewingPressed = true;
				
				screwCounter = 0;
			}
			if(prevDirection == SCREW_DOWN){
				screwingPressed = true;
				unscrewingPressed = false;
				screwCounter = 0;
			}
		}
		if(currDirection == SCREW_RIGHT){
			if(prevDirection == SCREW_UP){
				screwingPressed = true;
				unscrewingPressed = false;
				screwCounter = 0;
			}
			if(prevDirection == SCREW_DOWN){
				screwingPressed = false;
				unscrewingPressed = true;
				screwCounter = 0;
			}
		}
		if(currDirection == SCREW_DOWN){
			if(prevDirection == SCREW_RIGHT){
				screwingPressed = true;
				unscrewingPressed = false;
				screwCounter = 0;
			}
			if(prevDirection == SCREW_LEFT){
				screwingPressed = false;
				unscrewingPressed = true;
				screwCounter = 0;
			}
		}
		
		if((int) axisRX == 1.0f){
			prevDirection = SCREW_RIGHT;
		}
		if((int) axisRX == -1.0f){
			prevDirection = SCREW_LEFT;
		}
		if((int) axisRY == 1.0f ){
			prevDirection = SCREW_DOWN;
		}
		if((int) axisRY == -1.0f ){
			prevDirection = SCREW_UP;
		}
		
		//System.out.println( "sc: " + screwingPressed + ", unsc: " + unscrewingPressed );
		
		
		}
	}
}