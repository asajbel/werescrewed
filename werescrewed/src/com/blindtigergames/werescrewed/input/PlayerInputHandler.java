/********************************************************
 * InputHandlerPlayer1.java, meant to capture and parse player input
 * Author: Edward Boning
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
	/*
	 * Hooks up the InputProcessor to the input class, so events can be reported
	 */
	public PlayerInputHandler( String playerNumber ) {
		
		if (playerNumber.equals( "player1" ) ){
			up = Keys.W;
			down = Keys.S;
			left = Keys.A;
			right = Keys.D;
			screw = Keys.F;
		} else if (playerNumber.equals( "player2" ) ){
			up = Keys.I;
			down = Keys.K;
			left = Keys.J;
			right = Keys.L;
			screw = Keys.H;
		}
		
	}


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
	 */
	public boolean pausePressed( ) {
		return pauseKeyPressed;
	}

	/**
	 * Returns whether the move left key is pressed
	 * 
	 */
	public boolean leftPressed() {
		return leftPressed;
	}

	/**
	 * Returns whether the move right key is pressed
	 * 
	 */
	public boolean rightPressed() {
		return rightPressed;
	}

	/**
	 * Returns whether the jump key is pressed
	 * 
	 */
	public boolean jumpPressed( ) {
		return jumpPressed;
	}

	/**
	 * Returns whether the move down key is pressed
	 * 
	 */
	public boolean downPressed( ) {
		return downPressed;
	}

	/**
	 * Returns whether the attach to screw key is pressed
	 * 
	 */
	public boolean screwPressed( ) {
		return attachScrewPressed;
	}

	public boolean unscrewPressed( ) {
		return Gdx.input.isKeyPressed( Keys.Z );
	}

	public boolean screwing( ) {
		return rightPressed;
	}

	public boolean unscrewing( ) {
		return leftPressed;
	}



}
