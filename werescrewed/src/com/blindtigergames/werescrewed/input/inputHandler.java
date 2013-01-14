package com.blindtigergames.werescrewed.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public interface inputHandler extends InputProcessor {
	public enum Player { ONE, TWO}
	
	public boolean isLeftPressed (Player plyr);
		
	public boolean isRightPressed (Player plyr);
	
	public boolean isJumpPressed (Player plyr);
	
	public boolean isDownPressed (Player plyr);
	
	public boolean screwPressed (Player plyr);
	
	public boolean selectPressed (Player plyr);
	
	public boolean cancelPressed (Player plyr);



	@Override
	public boolean keyDown(int keycode);


	@Override
	public boolean keyUp(int keycode);


	@Override
	public boolean keyTyped(char character);


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button);


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button);


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer);


	@Override
	public boolean mouseMoved(int screenX, int screenY);


	@Override
	public boolean scrolled(int amount);
	

}
