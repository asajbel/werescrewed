/********************************************************
 * InputHandlerPlayer1.java, meant to capture and parse player input
 * Author: Edward Boning
 * 
 ********************************************************/

package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.*;

public class InputHandlerPlayer1 implements InputProcessor {

	private int p1LastKeyPressed;

	private boolean pauseKeyPressed;

	private boolean p1LeftPressed;
	private boolean p1RightPressed;
	private boolean p1JumpPressed;
	private boolean p1DownPressed;
	private boolean p1ScrewPressed;
	private boolean p1ScrewingClockwise;
	private boolean p1ScrewingCounterClockwise;


	/*
	 * Hooks up the InputProcessor to the input class, so events can be reported
	 */
	public InputHandlerPlayer1( ) {
		Gdx.input.setInputProcessor( this );
	}

	public void update( ) {
		pauseKeyPressed = Gdx.input.isKeyPressed( Keys.ESCAPE );
		
		p1LeftPressed = Gdx.input.isKeyPressed( Keys.DPAD_LEFT );
		p1RightPressed = Gdx.input.isKeyPressed( Keys.DPAD_RIGHT );
		p1JumpPressed = Gdx.input.isKeyPressed( Keys.DPAD_UP );
		p1DownPressed = Gdx.input.isKeyPressed( Keys.DPAD_DOWN );

		p1LeftPressed = Gdx.input.isKeyPressed( Keys.A );
		p1RightPressed = Gdx.input.isKeyPressed( Keys.D );
		p1JumpPressed = Gdx.input.isKeyPressed( Keys.W );
		p1DownPressed = Gdx.input.isKeyPressed( Keys.S );
		p1ScrewPressed = Gdx.input.isKeyPressed( Keys.Q );

	}

	/*
	 * Returns whether the pause key is pressed.
	 */
	public boolean pausePressed( ) {
		return pauseKeyPressed;
	}

	/*
	 * Returns whether the move left key is pressed
	 * 
	 * @param player - Selector to poll for the proper player
	 */
	public boolean leftPressed() {
		return p1LeftPressed;
	}

	/*
	 * Returns whether the move right key is pressed
	 * 
	 * @param player - Selector to poll for the proper player
	 */
	public boolean rightPressed() {
		return p1RightPressed;
	}

	/*
	 * Returns whether the jump key is pressed
	 * 
	 * @param player - Selector to poll for the proper player
	 */
	public boolean jumpPressed( ) {
		return p1JumpPressed;
	}

	/*
	 * Returns whether the move down key is pressed
	 * 
	 * @param player - Selector to poll for the proper player
	 */
	public boolean downPressed( ) {
		return p1DownPressed;
	}

	/*
	 * Returns whether the screw key is pressed
	 * 
	 * @param player - Selector to poll for the proper player
	 */
	public boolean screwPressed( ) {
		return Gdx.input.isKeyPressed( Keys.X );
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

		if ( keycode == Keys.W ) {
			p1JumpPressed = true;
			p1LastKeyPressed = keycode;
		}
		if ( keycode == Keys.A ) {
			p1LeftPressed = true;
			p1LastKeyPressed = keycode;
		}
		if ( keycode == Keys.S ) {
			p1DownPressed = true;
			p1LastKeyPressed = keycode;
		}
		if ( keycode == Keys.D ) {
			p1RightPressed = true;
			p1LastKeyPressed = keycode;
		}
		if ( keycode == Keys.Q ) {
			p1ScrewPressed = true;
			p1LastKeyPressed = keycode;
		}

		return true;
	}

	@Override
	public boolean keyUp( int keycode ) {
		if ( keycode == Keys.ESCAPE ) {
			pauseKeyPressed = false;
		}

		if ( keycode == Keys.W ) {
			p1JumpPressed = false;
		}
		if ( keycode == Keys.A ) {
			p1LeftPressed = false;
		}
		if ( keycode == Keys.S ) {
			p1DownPressed = false;
		}
		if ( keycode == Keys.D ) {
			p1RightPressed = false;
		}
		if ( keycode == Keys.Q ) {
			p1ScrewPressed = false;
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
