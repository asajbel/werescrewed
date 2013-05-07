package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;

public class SetTutorialAction implements IAction{

	Texture tutorialTexture = null;
	
	public SetTutorialAction( Texture tex ){
		tutorialTexture = tex;
	}
	
	@Override
	public void act( ) {
		
	}

	@Override
	public void act( Entity entity ) {
		if(entity.entityType != EntityType.PLAYER){
			Player player = (Player) entity;
			player.setTutorial( tutorialTexture );
			player.setDrawTutorial( true );
		}
	}

	@Override
	public ActionType getActionType( ) {
		return ActionType.FORPLAYER;
	}
	
}