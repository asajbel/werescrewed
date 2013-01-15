/********************************************************
 * InputHandler.java, meant to capture and parse player input
 * Author: Edward Boning
 * 
 ********************************************************/

package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {

	public enum player_t { ONE, TWO}
	
	public boolean leftPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1LeftPressed;
			case TWO:
				return p2LeftPressed;
		}
	}
	
	public boolean rightPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1RightPressed;
			case TWO:
				return p2RightPressed;
		}
	}
	
	public boolean jumpPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1JumpPressed;
			case TWO:
				return p2JumpPressed;
		}
	}
	
	public boolean downPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1DownPressed;
			case TWO:
				return p2DownPressed;
		}
	}
	
	public boolean screwPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1ScrewPressed;
			case TWO:
				return p2ScrewPressed;
		}
	}
	
	
	@Override
	public boolean keyDown( int keycode ) {
		if ( keycode == Keys.W ) {
			p1JumpPressed = true;
			oldP1JumpPressed = false;
		}
		if ( keycode == Keys.A ) {
			p1LeftPressed = true;
			oldP1LeftPressed = false;
		}
		if ( keycode == Keys.S ) {
			p1DownPressed = true;
			oldP1DownPressed = false;
		}
		if ( keycode == Keys.D ) {
			p1RightPressed = true;
			oldP1RightPressed = false;
		}
		if ( keycode == Keys.Q ) {
			p1ScrewPressed = true;
			oldP1ScrewPressed = false;
		}
		
		if ( keycode == Keys.I ) {
			p2JumpPressed = true;
			oldP2JumpPressed = false;
		}
		if ( keycode == Keys.J ) {
			p2LeftPressed = true;
			oldP2LeftPressed = false;
		}
		if ( keycode == Keys.K ) {
			p2DownPressed = true;
			oldP2DownPressed = false;
		}
		if ( keycode == Keys.L ) {
			p2RightPressed = true;
			oldP2RightPressed = false;
		}
		if ( keycode == Keys.U ) {
			p2ScrewPressed = true;
			oldP2ScrewPressed = false;
		}
		
		return true;
	}

	@Override
	public boolean keyUp ( int keycode ) {
		if ( keycode == Keys.W ) {
			p1JumpPressed = false;
			oldP1JumpPressed = true;
		}
		if ( keycode == Keys.A ) {
			p1LeftPressed = false;
			oldP1LeftPressed = true;
		}
		if ( keycode == Keys.S ) {
			p1DownPressed = false;
			oldP1DownPressed = true;
		}
		if ( keycode == Keys.D ) {
			p1RightPressed = false;
			oldP1RightPressed = true;
		}
		if ( keycode == Keys.Q ) {
			p1ScrewPressed = false;
			oldP1ScrewPressed = true;
		}
		
		if ( keycode == Keys.I ) {
			p2JumpPressed = false;
			oldP2JumpPressed = true;
		}
		if ( keycode == Keys.J ) {
			p2LeftPressed = false;
			oldP2LeftPressed = true;
		}
		if ( keycode == Keys.K ) {
			p2DownPressed = false;
			oldP2DownPressed = true;
		}
		if ( keycode == Keys.L ) {
			p2RightPressed = false;
			oldP2RightPressed = true;
		}
		if ( keycode == Keys.U ) {
			p2ScrewPressed = false;
			oldP2ScrewPressed = true;
		}
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	
	private boolean p1LeftPressed;
	private boolean oldP1LeftPressed;
	private boolean p1RightPressed;
	private boolean oldP1RightPressed;
	private boolean p1JumpPressed;
	private boolean oldP1JumpPressed;
	private boolean p1DownPressed;
	private boolean oldP1DownPressed;
	private boolean p1ScrewPressed;
	private boolean oldP1ScrewPressed;
	private boolean p1ScrewingClockwise;
	private boolean p1ScrewingCounterClockwise;
	
	private boolean p2LeftPressed;
	private boolean oldP2LeftPressed;
	private boolean p2RightPressed;
	private boolean oldP2RightPressed;
	private boolean p2JumpPressed;
	private boolean oldP2JumpPressed;
	private boolean p2DownPressed;
	private boolean oldP2DownPressed;
	private boolean p2ScrewPressed;
	private boolean oldP2ScrewPressed;
	private boolean p2ScrewingClockwise;
	private boolean p2ScrewingCounterClockwise;
	
}
