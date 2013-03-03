package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.hazard.Hazard;
import com.blindtigergames.werescrewed.player.Player;

public class ActivateMoverAction implements IAction{

	@Override
	public void act( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Entity entity ) {
		entity.setActive( true );
		
	}

	@Override
	public void act( Player player ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void act( Hazard hazard ) {
		// TODO Auto-generated method stub
		
	}
	
}