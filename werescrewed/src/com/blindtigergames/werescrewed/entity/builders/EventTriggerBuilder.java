package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.physics.box2d.World;

public class EventTriggerBuilder extends GenericEntityBuilder<EventTriggerBuilder>{
	
	//name and pos 
	private boolean rectangle;
	private boolean circle;
	private float radius;
	private float width, height;
	
	public EventTriggerBuilder( World world ) {
		super();
		reset();
		super.world(world);
	}
	
	@Override
	public EventTriggerBuilder reset(){
		super.resetInternal();
		this.rectangle = false;
		this.circle = true;
		this.radius = 100f;
		this.width = 100f;
		this.height = 100f;
		return this;
	}
}