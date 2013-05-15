package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;

public class DebugPrintAction implements IAction {

	String printString;

	public DebugPrintAction( String printString ) {
		this.printString = printString;
	}

	@Override
	public void act( ) {
		// Gdx.app.log( "DebugPrintAction.act()", printString );
	}

	@Override
	public void act( Entity entity ) {
		// Gdx.app.log( "DebugPrintAction.act("+entity.name+")", printString );
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}