package com.blindtigergames.werescrewed.entity.action;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.hazard.Hazard;
import com.blindtigergames.werescrewed.player.Player;

public interface IAction{
	
	public void act();
	
	public void act(Entity entity);
	
	public void act(Player player);
	
	public void act(Hazard hazard);
	
}