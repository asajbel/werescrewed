/********************************************************
 * PlayerInputHandler.java, meant to capture and parse player input
 * Author: Edward Boning and Ranveer Dhaliwal
 * 
 ********************************************************/

package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class PlayerInputHandler {

	private boolean pauseKeyPressed;

	private boolean leftPressed;
	private boolean rightPressed;
	private boolean jumpPressed;
	private boolean downPressed;

	private boolean attachScrewPressed;

	private int up, down, left, right, screw;

	/**
	 * Hooks up the player to the input class, so events can be reported
	 * 
	 * @param String
	 *            playerNumber - only "player1" or "player2"
	 */
	public PlayerInputHandler( String playerNumber ) {

		if ( playerNumber.equals( "player1" ) ) {
			up = Keys.W;
			down = Keys.S;
			left = Keys.A;
			right = Keys.D;
			screw = Keys.F;
		} else if ( playerNumber.equals( "player2" ) ) {
			up = Keys.UP;                //I
			down = Keys.DOWN;            //K
			left = Keys.LEFT;            //J
			right = Keys.RIGHT;          //L
			screw = Keys.SHIFT_RIGHT;    //H
		}

	}

	/**
	 * update checks the keyboard keys to see if they are pressed or not
	 */

	public void update( ) {
		pauseKeyPressed = Gdx.input.isKeyPressed( Keys.ESCAPE );

		leftPressed = Gdx.input.isKeyPressed( left );
		rightPressed = Gdx.input.isKeyPressed( right );
		jumpPressed = Gdx.input.isKeyPressed( up );
		downPressed = Gdx.input.isKeyPressed( down );
		attachScrewPressed = Gdx.input.isKeyPressed( screw );

	}

	/**
	 * Returns whether the pause key is pressed.
	 * 
	 * @return boolean
	 */
	public boolean pausePressed( ) {
		return pauseKeyPressed;
	}

	/**
	 * Returns whether the move left key is pressed
	 * 
	 * @return boolean
	 */
	public boolean leftPressed( ) {
		return leftPressed;
	}

	/**
	 * Returns whether the move right key is pressed
	 * 
	 * @return boolean
	 */
	public boolean rightPressed( ) {
		return rightPressed;
	}

	/**
	 * Returns whether the jump key is pressed
	 * 
	 * @return boolean
	 */
	public boolean jumpPressed( ) {
		return jumpPressed;
	}

	/**
	 * Returns whether the move down key is pressed
	 * 
	 * @return boolean
	 */
	public boolean downPressed( ) {
		return downPressed;
	}

	/**
	 * Returns whether the attach to screw key is pressed
	 * 
	 * @return boolean
	 */
	public boolean screwPressed( ) {
		return attachScrewPressed;
	}

	public boolean unscrewPressed( ) {
		return Gdx.input.isKeyPressed( Keys.Z );
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

}
