package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * My controller listener class grabs input from controllers
 * 
 * @author Ranveer
 * 
 */

public class MyControllerListener implements ControllerListener {

	/**
	 * Function returns the what index the controller is (player 1 or player 2)
	 * 
	 * @author Ranveer
	 * @param controller
	 * @return int
	 */
	public int indexOf( Controller controller ) {
		return Controllers.getControllers( ).indexOf( controller, true );
	}

	@Override
	public boolean axisMoved( Controller controller, int buttonIndex,
			float axisPoint ) {

		/*
		 * for control sticks left stick button 0 - vertical - top is -1.0,
		 * bottom is 1.0 button 1 - horizontal - left is -1.0, right is 1.0
		 * 
		 * right stick button 2 - vertical button 3 - horizontal
		 */
		return false;
	}

	@Override
	public boolean buttonDown( Controller controller, int buttonIndex ) {
		// System.out.println("#" + indexOf(controller) + ", button " +
		// buttonIndex + " down");
		return false;
	}

	@Override
	public boolean buttonUp( Controller controller, int buttonIndex ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connected( Controller controller ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected( Controller controller ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean povMoved( Controller controller, int buttonIndex,
			PovDirection direction ) {
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

}