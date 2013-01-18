/********************************************************
 * InputHandler.java, meant to capture and parse player input
 * Author: Edward Boning
 * 
 ********************************************************/

package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {

	public enum player_t { ONE, TWO}
	
	/*
	 * Hooks up the InputProcessor to the input class, so events can be reported
	 */
	public InputHandler (){
		Gdx.input.setInputProcessor(this);
	}
	
	public void update(){
		pauseKeyPressed = Gdx.input.isKeyPressed( Keys.ESCAPE );
		
		p1LeftPressed = Gdx.input.isKeyPressed( Keys.A );
		p1RightPressed = Gdx.input.isKeyPressed( Keys.D );
		p1JumpPressed = Gdx.input.isKeyPressed( Keys.W );
		p1DownPressed = Gdx.input.isKeyPressed( Keys.S );
		p1ScrewPressed = Gdx.input.isKeyPressed( Keys.Q );
		
		p2LeftPressed = Gdx.input.isKeyPressed( Keys.J );
		p2RightPressed = Gdx.input.isKeyPressed( Keys.L );
		p2JumpPressed = Gdx.input.isKeyPressed( Keys.I );
		p2DownPressed = Gdx.input.isKeyPressed( Keys.K );
		p2ScrewPressed = Gdx.input.isKeyPressed( Keys.U );
		
	}
	
	/*
	 * Returns whether the pause key is pressed.
	 */
	public boolean pausePressed () {
		return pauseKeyPressed;
	}
	
	/*
	 * Returns whether the move left key is pressed
	 * @param player - Selector to poll for the proper player
	 */
	public boolean leftPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1LeftPressed;
			case TWO:
				return p2LeftPressed;
		}
	}
	
	/*
	 * Returns whether the move right key is pressed
	 * @param player - Selector to poll for the proper player
	 */
	public boolean rightPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1RightPressed;
			case TWO:
				return p2RightPressed;
		}
	}
	
	/*
	 * Returns whether the jump key is pressed
	 * @param player - Selector to poll for the proper player
	 */
	public boolean jumpPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1JumpPressed;
			case TWO:
				return p2JumpPressed;
		}
	}
	
	/*
	 * Returns whether the move down key is pressed
	 * @param player - Selector to poll for the proper player
	 */
	public boolean downPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return p1DownPressed;
			case TWO:
				return p2DownPressed;
		}
	}
	
	/*
	 * Returns whether the screw key is pressed
	 * @param player - Selector to poll for the proper player
	 */
	public boolean screwPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return Gdx.input.isKeyPressed( Keys.X );
			case TWO:
				return Gdx.input.isKeyPressed( Keys.M );
		}
	}
	
	public boolean unscrewPressed ( player_t player ){
		switch ( player ){
			default: return false;
			case ONE:
				return Gdx.input.isKeyPressed ( Keys.Z );
			case TWO:
				return Gdx.input.isKeyPressed ( Keys.N );
		
		}
	}
	
	public boolean screwing ( player_t player ) {
		switch ( player ){
			default: return false;
			case ONE:
				return ( ( p1LastKeyPressed == Keys.W && rightPressed ( player_t.ONE ) )
						|| ( p1LastKeyPressed == Keys.D && jumpPressed ( player_t.ONE ) ) );
			case TWO:
				return ( ( p2LastKeyPressed == Keys.I && rightPressed ( player_t.TWO ) )
						|| ( p2LastKeyPressed == Keys.L && jumpPressed ( player_t.TWO ) ) );
		}
	}
	
	public boolean unscrewing ( player_t player ) {
		switch ( player ){
			default: return false;
			case ONE:
				return ( ( p1LastKeyPressed == Keys.W && leftPressed ( player_t.ONE ) )
						|| ( p1LastKeyPressed == Keys.A && jumpPressed ( player_t.ONE ) ) );
			case TWO:
				return ( ( p2LastKeyPressed == Keys.I && leftPressed ( player_t.TWO ) )
						|| ( p2LastKeyPressed == Keys.J && jumpPressed ( player_t.TWO ) ) );
		}
	}
	
	
	@Override
	public boolean keyDown( int keycode ) {
		if ( keycode == Keys.ESCAPE) {
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
		
		if ( keycode == Keys.I ) {
			p2JumpPressed = true;
			p2LastKeyPressed = keycode;
		}
		if ( keycode == Keys.J ) {
			p2LeftPressed = true;
			p2LastKeyPressed = keycode;
		}
		if ( keycode == Keys.K ) {
			p2DownPressed = true;
			p2LastKeyPressed = keycode;
		}
		if ( keycode == Keys.L ) {
			p2RightPressed = true;
			p2LastKeyPressed = keycode;
		}
		if ( keycode == Keys.U ) {
			p2ScrewPressed = true;
			p2LastKeyPressed = keycode;
		}
		
		return true;
	}

	@Override
	public boolean keyUp ( int keycode ) {
		if ( keycode == Keys.ESCAPE) {
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
		
		if ( keycode == Keys.I ) {
			p2JumpPressed = false;
		}
		if ( keycode == Keys.J ) {
			p2LeftPressed = false;
		}
		if ( keycode == Keys.K ) {
			p2DownPressed = false;
		}
		if ( keycode == Keys.L ) {
			p2RightPressed = false;
		}
		if ( keycode == Keys.U ) {
			p2ScrewPressed = false;
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
	private int p1LastKeyPressed;
	private int p2LastKeyPressed;

	private boolean pauseKeyPressed;
	
	private boolean p1LeftPressed;
	private boolean p1RightPressed;
	private boolean p1JumpPressed;
	private boolean p1DownPressed;
	private boolean p1ScrewPressed;
	private boolean p1ScrewingClockwise;
	private boolean p1ScrewingCounterClockwise;
	
	private boolean p2LeftPressed;
	private boolean p2RightPressed;
	private boolean p2JumpPressed;
	private boolean p2DownPressed;
	private boolean p2ScrewPressed;
	private boolean p2ScrewingClockwise;
	private boolean p2ScrewingCounterClockwise;
	
}
