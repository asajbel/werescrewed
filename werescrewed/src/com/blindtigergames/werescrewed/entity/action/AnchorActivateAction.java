package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.Entity;

public class AnchorActivateAction implements IAction {

	Anchor anchor = null;

	public AnchorActivateAction( Anchor a ) {
		anchor = a;
	}

	@Override
	public void act( ) {
		AnchorList.getInstance( ).deactivateAllAnchors( false );
		anchor.activate( );
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