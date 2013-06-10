package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.entity.Entity;

public class AnchorActivateAction implements IAction {

	Anchor anchor = null;
	int timer = -1;
	
	public AnchorActivateAction( Anchor a ) {
		anchor = a;
	}
	
	public AnchorActivateAction( Anchor a, int time ) {
		anchor = a;
		timer = time;
	}

	@Override
	public void act( ) {
		AnchorList.getInstance( ).deactivateAllAnchors( false );
		anchor.setTimer( timer );
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