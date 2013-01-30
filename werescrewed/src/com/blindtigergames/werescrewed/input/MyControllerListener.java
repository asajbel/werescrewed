package com.blindtigergames.werescrewed.input;


import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class MyControllerListener implements ControllerListener{

	
	public int indexOf(Controller controller) {
		return Controllers.getControllers().indexOf(controller, true);
	}
	
	@Override
	public boolean accelerometerMoved( Controller arg0, int arg1, Vector3 arg2 ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean axisMoved( Controller arg0, int arg1, float arg2 ) {
		//System.out.println("#" + indexOf(arg0) + ", button " + arg1 + " down");
		//System.out.println( "axis:" + arg2 );
		
		/*
		 * for control sticks
		 * left stick
		 * button 0 - vertical - top is -1.0, bottom is 1.0
		 * button 1 - horizontal - left is -1.0, right is 1.0
		 * 
		 * right stick
		 * button 2 - vertical
		 * button 3 - horizontal
		 */
		return false;
	}

	@Override
	public boolean buttonDown( Controller controller, int buttonIndex ) {
		//System.out.println("#" + indexOf(controller) + ", button " + buttonIndex + " down");
		return false;
	}

	@Override
	public boolean buttonUp( Controller arg0, int arg1 ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connected( Controller arg0 ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected( Controller arg0 ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean povMoved( Controller arg0, int arg1, PovDirection arg2 ) {
		//System.out.println("#" + indexOf(arg0) + ", button " + arg1 + " down");
		return false;
	}

	@Override
	public boolean xSliderMoved( Controller arg0, int arg1, boolean arg2 ) {
		//System.out.println("#" + indexOf(arg0) + ", button " + arg1 + " down");
		return false;
	}

	@Override
	public boolean ySliderMoved( Controller arg0, int arg1, boolean arg2 ) {
		//System.out.println("#" + indexOf(arg0) + ", button " + arg1 + " down");
		return false;
	}
	
}