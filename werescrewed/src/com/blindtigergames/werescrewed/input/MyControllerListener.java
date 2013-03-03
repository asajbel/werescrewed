package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.input.mappings.Mapping;

/**
 * My controller listener class grabs input from controllers Booleans are
 * checked by player and the buttons are checked by this listener
 * 
 * @author Ranveer
 * 
 */

public class MyControllerListener implements ControllerListener {

	private boolean isConnected;

	// Buttons and Dpad
	private boolean pausePressed;
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean jumpPressed;
	private boolean downPressed;
	private boolean upPressed;
	private boolean analogUsed;
	private boolean screwingPressed;
	private boolean unscrewingPressed;
	private boolean grabPressed;

	// Screwing booleans
	private boolean prevScrewingPressed;
	private boolean prevUnscrewingPressed;
	private boolean attachScrewPressed;

	// Three modes for screwing (testing)
	private boolean debugScrewMode1 = true;
	private boolean debugScrewMode2 = false;
	private boolean debugScrewMode3 = false;

	// Used for analog stick, L = left, R = right
	private float axisLX;
	private float axisLY;
	private float axisRX;
	private float axisRY;

	// Angles and directions for screwing
	private int prevRightAnalogAngle = 0;
	private int currRightAnalogAngle = 0;
	private int prevDirection;
	private int currDirection = 0;
	private int screwCounter = 0;

	// Used for screwing/unscrewing with right analog stick
	private final static int SCREW_UP = 1;
	private final static int SCREW_RIGHT = 2;
	private final static int SCREW_DOWN = 3;
	private final static int SCREW_LEFT = 4;
	private final static int SCREW_COUNTER = 10;
	private final static int SCREW_ANGLE_DIFF = 10;

	// Analog deadzone and center
	private final static float DEADZONE = 0.2f;
	private final static float TRIGGER_DEADZONE = 0.3f;
	private final static float ANALOG_CENTER = 0.7f;

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

		if ( buttonIndex == Mapping.AXIS_RIGHT_TRIGGER ) {
			if ( axisPoint < -TRIGGER_DEADZONE || axisPoint > TRIGGER_DEADZONE )
				attachScrewPressed = true;
			else
				attachScrewPressed = false;
		}

//		Gdx.app.log( controller.getName( ) + " axis",
//				String.valueOf( buttonIndex ) );

		axisLY = controller.getAxis( Mapping.AXIS_LEFT_Y );
		axisLX = controller.getAxis( Mapping.AXIS_LEFT_X );
		axisRY = controller.getAxis( Mapping.AXIS_RIGHT_Y );
		axisRX = controller.getAxis( Mapping.AXIS_RIGHT_X );

		// Resetting Analog stick
		if ( buttonIndex == Mapping.AXIS_LEFT_Y
				&& ( axisLY < DEADZONE && axisLY > -DEADZONE ) ) {
			upPressed = false;
			downPressed = false;
			analogUsed = false;
		}
		if ( buttonIndex == Mapping.AXIS_LEFT_X
				&& ( axisLX < DEADZONE && axisLX > -DEADZONE ) ) {
			rightPressed = false;
			leftPressed = false;
			analogUsed = false;
		}

		// Setting Analog Direction
		if ( axisLX > DEADZONE ) {
			rightPressed = true;
			analogUsed = true;
		}
		if ( axisLX < -DEADZONE ) {
			leftPressed = true;
			analogUsed = true;
		}
		if ( axisLY > DEADZONE ) {
			downPressed = true;
			analogUsed = true;
		}
		if ( axisLY < -DEADZONE ) {
			upPressed = true;
			analogUsed = true;
		}

		// Resetting Right Stick
		if ( ( axisRX < ANALOG_CENTER && axisRY < ANALOG_CENTER )
				&& ( axisRX > -ANALOG_CENTER && axisRY > -ANALOG_CENTER ) ) {
			screwingPressed = false;
			unscrewingPressed = false;
			currDirection = 0;
			prevDirection = 0;
			screwCounter = 0;
			prevRightAnalogAngle = 0;
			currRightAnalogAngle = 0;
		} else
			// Updating Right Stick Screwing
			rightStickScrew( );

		return false;
	}

	/**
	 * This function checks which button is hit on the controller and sets the
	 * appropriate boolean
	 */
	@Override
	public boolean buttonDown( Controller controller, int buttonIndex ) {
//		Gdx.app.log( controller.getName( ) + " Down",
//				String.valueOf( buttonIndex ) );

		// Switching between Screwing/Unscrewing Modes
		if ( buttonIndex == Mapping.BUTTON_SELECT ) {
			if ( debugScrewMode1 ) {
				debugScrewMode1 = false;
				debugScrewMode2 = true;
			} else if ( debugScrewMode2 ) {
				debugScrewMode2 = false;
				debugScrewMode3 = true;
			} else if ( debugScrewMode3 ) {
				debugScrewMode3 = false;
				debugScrewMode1 = true;
			}
			Gdx.app.log( "debug", "1: " + debugScrewMode1 + "2: "
					+ debugScrewMode2 + "3: " + debugScrewMode3 );
		}

		// Setting jump/pause/bumper
		if ( buttonIndex == Mapping.BUTTON_FACE_BOT )
			jumpPressed = true;
		if ( buttonIndex == Mapping.BUTTON_R1 )
			attachScrewPressed = true;
		if ( buttonIndex == Mapping.BUTTON_START )
			pausePressed = true;
		if ( buttonIndex == Mapping.BUTTON_FACE_LEFT )
			grabPressed = true;

		if ( buttonIndex == Mapping.BUTTON_DPAD_DOWN )
			downPressed = true;
		if ( buttonIndex == Mapping.BUTTON_DPAD_LEFT )
			leftPressed = true;
		if ( buttonIndex == Mapping.BUTTON_DPAD_RIGHT )
			rightPressed = true;
		if ( buttonIndex == Mapping.BUTTON_DPAD_UP )
			upPressed = true;

		return false;

	}

	/**
	 * This function checks when a button is released and sets the boolean to
	 * false
	 */

	@Override
	public boolean buttonUp( Controller controller, int buttonIndex ) {
//		Gdx.app.log( controller.getName( ) + " up",
//				String.valueOf( buttonIndex ) );

		// Resetting buttons
		if ( buttonIndex == Mapping.BUTTON_FACE_BOT )
			jumpPressed = false;
		if ( buttonIndex == Mapping.BUTTON_R1 )
			attachScrewPressed = false;
		if ( buttonIndex == Mapping.BUTTON_START )
			pausePressed = false;
		if ( buttonIndex == Mapping.BUTTON_FACE_LEFT )
			grabPressed = false;

		if ( buttonIndex == Mapping.BUTTON_DPAD_DOWN )
			downPressed = false;
		if ( buttonIndex == Mapping.BUTTON_DPAD_LEFT )
			leftPressed = false;
		if ( buttonIndex == Mapping.BUTTON_DPAD_RIGHT )
			rightPressed = false;
		if ( buttonIndex == Mapping.BUTTON_DPAD_UP )
			upPressed = false;

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
	 * This function checks the Dpad to see which direction is hit it uses a
	 * enum called PovDirection (east, west, north, south)
	 */
	@Override
	public boolean povMoved( Controller controller, int buttonIndex,
			PovDirection direction ) {

//		Gdx.app.log( controller.getName( ) + " pov",
//				String.valueOf( buttonIndex ) );

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
	 * Returns the x point of the left analog stick
	 * 
	 * @return float
	 * @author Ranveer
	 */
	public float analogLeftAxisX( ) {
		return axisLX;
	}

	/**
	 * Returns the y point of the left analog stick
	 * 
	 * @return float
	 * @author Ranveer
	 */
	public float analogLeftAxisY( ) {
		return axisLY;
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
	 * Returns whether the button to grab a player is pressed
	 * 
	 * @return boolean
	 * @author dennis
	 */
	public boolean isGrabPressed( ) {
		return grabPressed;
	}

	/**
	 * Returns whether trying to screw clockwise (righty tighty) with right
	 * stick
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean screwing( ) {
		prevScrewingPressed = screwingPressed;
		if ( ( prevScrewingPressed == screwingPressed ) )
			screwCounter++;
		if ( screwCounter > SCREW_COUNTER ) {
			screwingPressed = false;
			screwCounter = 0;
		}
		return screwingPressed;
	}

	/**
	 * Returns whether trying to screw counter-clockwise (lefty loosely) with
	 * right stick
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean unscrewing( ) {
		prevUnscrewingPressed = unscrewingPressed;
		if ( ( prevUnscrewingPressed == unscrewingPressed ) )
			screwCounter++;
		if ( screwCounter > SCREW_COUNTER ) {
			unscrewingPressed = false;
			screwCounter = 0;
		}
		return unscrewingPressed;
	}

	public double getLeftAnalogAngle( ) {
		if ( axisLX < 0.1f && axisLX > -0.1f ) {
			if ( axisLY < 0.1f && axisLY > -0.1f )
				return 0.0;
		}

		return Math.toDegrees( Math.atan2( -axisLX, -axisLY ) ) + 180;
	}
	
	public double getRightAnalogAngle(){
		return currRightAnalogAngle;
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

	/**
	 * This function checks to see Right stick's state and then sets the screw
	 * or unscrew boolean
	 * 
	 * There are currently three modes of doing it, after testing we should
	 * narrow it down
	 * 
	 */
	private void rightStickScrew( ) {
		//System.out.println( "x: " + axisRX + ", Y: " + axisRY );
		axisRY *= -1;
		currRightAnalogAngle = ( int ) Math.toDegrees( Math.atan2( -axisRY, -axisRX ) ) + 180;


//		System.out.println("screwing: "+ screwing( )
//				+ " unscrewing: " + unscrewing() 
//				+ " currAngle: "+ currRightAnalogAngle
//				+ " prevAngle: " + prevRightAnalogAngle);
		
		// First mode is sets the Previous Angle when it is moved from the
		// center
		// then when the stick is moved it checks to see if its different from
		// where
		// it started, if so then it is screwing/unscrewing
		if ( debugScrewMode1 ) {
			if ( currRightAnalogAngle - prevRightAnalogAngle > 250 ){
				screwingPressed = true;
				unscrewingPressed = false;
				prevRightAnalogAngle = currRightAnalogAngle;
			}
			else if ( currRightAnalogAngle - prevRightAnalogAngle < -250 ){
				screwingPressed = false;
				unscrewingPressed = true;
				prevRightAnalogAngle = currRightAnalogAngle;
			}
			else if ( currRightAnalogAngle < prevRightAnalogAngle ) {
				screwingPressed = true;
				unscrewingPressed = false;
				prevRightAnalogAngle = currRightAnalogAngle;
			} else if ( prevRightAnalogAngle < currRightAnalogAngle ) {
				unscrewingPressed = true;
				screwingPressed = false;
				prevRightAnalogAngle = currRightAnalogAngle;
			}
			else{
				Gdx.app.log( "analog:", "uh oh" );
			}
		}
		else if ( debugScrewMode2 ) {
			if ( prevRightAnalogAngle == 0 )
				prevRightAnalogAngle = currRightAnalogAngle;
			if ( currRightAnalogAngle - prevRightAnalogAngle > SCREW_ANGLE_DIFF ) {
				screwingPressed = false;
				unscrewingPressed = true;
				prevRightAnalogAngle = currRightAnalogAngle;
			} else if ( prevRightAnalogAngle - currRightAnalogAngle > SCREW_ANGLE_DIFF ) {
				unscrewingPressed = false;
				screwingPressed = true;
				prevRightAnalogAngle = currRightAnalogAngle;
			}
		}

		// Last mode checks when the stick has past specific points
		// meaning top (0, -1) right (1, 0) left (-1, 0) and bottom (0, 1)
		// then it sets the current direction, updates screwing, then sets the
		// prev direction
		else if ( debugScrewMode3 ) {

			prevRightAnalogAngle = currRightAnalogAngle;

			currDirection = 0;

			if ( ( int ) axisRX == 1.0f ) {
				currDirection = SCREW_RIGHT;
			}
			if ( ( int ) axisRX == -1.0f ) {
				currDirection = SCREW_LEFT;
			}
			if ( ( int ) axisRY == 1.0f ) {
				currDirection = SCREW_DOWN;
			}
			if ( ( int ) axisRY == -1.0f ) {
				currDirection = SCREW_UP;
			}

			if ( screwCounter > SCREW_COUNTER ) {
				unscrewingPressed = false;
				screwingPressed = false;
				currDirection = 0;
				screwCounter = 0;
			}

			if ( currDirection == SCREW_UP ) {
				if ( prevDirection == SCREW_LEFT ) {
					screwingPressed = true;
					unscrewingPressed = false;
					screwCounter = 0;
				}
				if ( prevDirection == SCREW_RIGHT ) {
					screwingPressed = false;
					unscrewingPressed = true;
					screwCounter = 0;
				}
			}

			if ( currDirection == SCREW_LEFT ) {
				if ( prevDirection == SCREW_UP ) {
					screwingPressed = false;
					unscrewingPressed = true;

					screwCounter = 0;
				}
				if ( prevDirection == SCREW_DOWN ) {
					screwingPressed = true;
					unscrewingPressed = false;
					screwCounter = 0;
				}
			}

			if ( currDirection == SCREW_RIGHT ) {
				if ( prevDirection == SCREW_UP ) {
					screwingPressed = true;
					unscrewingPressed = false;
					screwCounter = 0;
				}
				if ( prevDirection == SCREW_DOWN ) {
					screwingPressed = false;
					unscrewingPressed = true;
					screwCounter = 0;
				}
			}

			if ( currDirection == SCREW_DOWN ) {
				if ( prevDirection == SCREW_RIGHT ) {
					screwingPressed = true;
					unscrewingPressed = false;
					screwCounter = 0;
				}
				if ( prevDirection == SCREW_LEFT ) {
					screwingPressed = false;
					unscrewingPressed = true;
					screwCounter = 0;
				}
			}

			if ( ( int ) axisRX == 1.0f ) {
				prevDirection = SCREW_RIGHT;
			}
			if ( ( int ) axisRX == -1.0f ) {
				prevDirection = SCREW_LEFT;
			}
			if ( ( int ) axisRY == 1.0f ) {
				prevDirection = SCREW_DOWN;
			}
			if ( ( int ) axisRY == -1.0f ) {
				prevDirection = SCREW_UP;
			}

		}
	}
}