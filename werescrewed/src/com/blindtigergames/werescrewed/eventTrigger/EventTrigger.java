package com.blindtigergames.werescrewed.eventTrigger;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;


public class EventTrigger extends Entity{
	
	public EventTrigger(String name, Vector2 position, World world){
		super(name, position, null, null, false );
		this.world = world;
	}
}