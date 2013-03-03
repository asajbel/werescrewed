package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;

public class EventTriggerBuilder extends GenericEntityBuilder<EventTriggerBuilder>{
	
	//name and pos 
	@SuppressWarnings( "unused" )
	private boolean rectangle;
	@SuppressWarnings( "unused" )
	private boolean circle;
	@SuppressWarnings( "unused" )
	private float radius;
	@SuppressWarnings( "unused" )
	private float width, height;
	@SuppressWarnings( "unused" )
	private boolean offsetAbove, offsetBelow, offsetRight, offsetLeft;
	@SuppressWarnings( "unused" )
	private boolean attachedToEntity;
	
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
	
	public EventTriggerBuilder radius(float radius){
		this.radius = radius;
		return this;
	}
	
	public EventTriggerBuilder height(float height){
		this.height = height;
		return this;
	}
	
	public EventTriggerBuilder width(float width){
		this.width = width;
		return this;
	}
	
	public EventTriggerBuilder circle(){
		this.circle = true;
		this.rectangle = false;
		return this;
	}
	
	public EventTriggerBuilder rectangle(){
		this.rectangle = true;
		this.circle = false;
		return this;
	}
	
	public EventTriggerBuilder setPositionToEntity(Entity entity){
		this.attachedToEntity = true;
		this.pos = entity.getPositionPixel( );
		return this;
	}
	
	public EventTriggerBuilder offsetAbove(){
		this.offsetAbove = true;
		this.offsetBelow = false;
		return this;
	}
	public EventTriggerBuilder offsetBelow(){
		this.offsetBelow = true;
		this.offsetAbove = false;
		return this;
	}
	public EventTriggerBuilder offsetRight(){
		this.offsetRight = true;
		this.offsetLeft = false;
		return this;
	}
	public EventTriggerBuilder offsetLeft(){
		this.offsetLeft = true;
		this.offsetRight = false;
		return this;
	}
}