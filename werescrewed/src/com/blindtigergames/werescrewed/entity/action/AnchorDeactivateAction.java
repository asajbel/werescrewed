package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;

public class AnchorDeactivateAction implements IAction {

	Anchor anchor = null;

	public AnchorDeactivateAction( Anchor a ) {
		anchor = a;
	}

	@Override
	public void act( ) {
		anchor.deactivate( );

	}

	@Override
	public void act( Entity entity ) {
		// entity.anchor.activate()
		// TODO: figure out better way to make anchors

	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}

}