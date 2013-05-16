/********************************************************
 * InputHandlerPlayer1.java, meant to capture and parse player input
 * Author: Edward Boning
 * 
 ********************************************************/

package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class InputHandlerPlayer2 implements InputProcessor {

	private boolean pauseKeyPressed;

	private boolean leftPressed;
	private boolean rightPressed;
	private boolean jumpPressed;
	private boolean downPressed;

	private boolean attachScrewPressed;

	/*
	 * Hooks up the InputProcessor to the input class, so events can be reported
	 */
	public InputHandlerPlayer2( ) {
		Gdx.input.setInputProcessor( this );
	}

	public void update( ) {
		pauseKeyPressed = Gdx.input.isKeyPressed( Keys.ESCAPE );

		leftPressed = Gdx.input.isKeyPressed( Keys.DPAD_LEFT );
		rightPressed = Gdx.input.isKeyPressed( Keys.DPAD_RIGHT );
		jumpPressed = Gdx.input.isKeyPressed( Keys.DPAD_UP );
		downPressed = Gdx.input.isKeyPressed( Keys.DPAD_DOWN );

		leftPressed = Gdx.input.isKeyPressed( Keys.A );
		rightPressed = Gdx.input.isKeyPressed( Keys.D );
		jumpPressed = Gdx.input.isKeyPressed( Keys.W );
		downPressed = Gdx.input.isKeyPressed( Keys.S );
		Gdx.input.isKeyPressed( Keys.Q );

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
	public boolean leftPressed( ) {
		return leftPressed;
	}

	/**
	 * Returns whether the move right key is pressed
	 * 
	 */
	public boolean rightPressed( ) {
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
		return rightPressed( );
	}

	public boolean unscrewing( ) {
		return leftPressed( );
	}

	@Override
	public boolean keyDown( int keycode ) {
		if ( keycode == Keys.ESCAPE ) {
			pauseKeyPressed = true;
		}
		if ( keycode == Keys.I ) {
			jumpPressed = true;
		}
		if ( keycode == Keys.J ) {
			leftPressed = true;
		}
		if ( keycode == Keys.K ) {
			downPressed = true;
		}
		if ( keycode == Keys.L ) {
			rightPressed = true;
		}
		if ( keycode == Keys.H ) {
			attachScrewPressed = true;
		}
		return true;
	}

	@Override
	public boolean keyUp( int keycode ) {
		if ( keycode == Keys.ESCAPE ) {
			pauseKeyPressed = false;
		}

		if ( keycode == Keys.I ) {
			jumpPressed = false;
		}
		if ( keycode == Keys.J ) {
			leftPressed = false;
		}
		if ( keycode == Keys.K ) {
			downPressed = false;
		}
		if ( keycode == Keys.L ) {
			rightPressed = false;
		}
		if ( keycode == Keys.H ) {
			attachScrewPressed = false;
		}

		return true;
	}

	@Override
	public boolean keyTyped( char character ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged( int screenX, int screenY, int pointer ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved( int screenX, int screenY ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled( int amount ) {
		// TODO Auto-generated method stub
		return false;
	}

}
