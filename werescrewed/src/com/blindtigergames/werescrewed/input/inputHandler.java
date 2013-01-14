package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class inputHandler implements InputProcessor {
	public enum Player { ONE, TWO}
	
	public boolean isLeftPressed (Player plyr){
	
		
		return false;
	
	}
	
	
	public boolean isRightPressed (Player plyr){
	
		
		return false;
	
	}
	
	public boolean isJumpPressed (Player plyr){
	
		
		return false;
	
	}
	
	public boolean isDownPressed (Player plyr){
	
		
		return false;
	
	}
	
	public boolean screwPressed (Player plyr){
	
		
		return false;
	
	}
	
	public boolean selectPressed (Player plyr){
	
		
		return false;
	
	}
	
	public boolean cancelPressed (Player plyr){
	
		
		return false;
	
	}


	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
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
	

}
