package com.blindtigergames.werescrewed.eventTrigger;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.action.IAction;
import com.blindtigergames.werescrewed.util.Util;


public class EventTrigger extends Entity{
	
	private boolean repeatable = false;
	private boolean repeatTriggeredOnce = false;
	private boolean activated = false;
	private boolean triggeredOnce = false;
	private ArrayList<Entity> entityList;
	private IAction action;
	
	public EventTrigger(String name, World world){
		super(name, null, null, null, false );
		this.world = world;
		entityType = EntityType.EVENTTRIGGER;
		entityList = new ArrayList<Entity>();
	}
	
	public void constructCircleBody(float radiusPixel, Vector2 positionPixel){
		
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ));
		body = world.createBody( bodyDef );
		
		CircleShape circle = new CircleShape( );
		circle.setRadius( radiusPixel * Util.PIXEL_TO_BOX );
		
		FixtureDef fixture = new FixtureDef( );
		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixture.isSensor = true;
		fixture.shape = circle;
		
		body.createFixture( fixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		circle.dispose( );
	}
	
	public void contructRectangleBody(float heightPixels, float widthPixels, Vector2 positionPixel){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ));
		body = world.createBody( bodyDef );
		
		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox( heightPixels/2 * Util.PIXEL_TO_BOX, widthPixels/2 * Util.PIXEL_TO_BOX );
		
		FixtureDef fixture = new FixtureDef( );
		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixture.isSensor = true;
		fixture.shape = polygon;
		
		body.createFixture( fixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		polygon.dispose( );
	}
	
	/**
	 * checks whether this EventTrigger is repeatable,
	 * which means player can exit the sensor and then collide again
	 * and the event will trigger again
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean isRepeatable(){
		return repeatable;
	}
	
	/**
	 * sets this EventTrigger to repeatable or not,
	 * if repeatable this event will trigger when the player
	 * stops contact and then collides again
	 * @param repeatable - boolean
	 * @author Ranveer
	 */
	public void setRepeatable(boolean repeatable){
		this.repeatable = repeatable;
	}
	
	/**
	 * Checks if player is currently colliding with this
	 * EventTrigger
	 * @return boolean
	 */
	public boolean isActivated(){
		return activated;
	}
	
	/**
	 * Sets activated to true or false, depending on player collision
	 * @param active - boolean
	 */
	public void setActivated(boolean active){
		this.activated = active;
		if(active == false){
			repeatTriggeredOnce = false;
		}
	}
	
	public void update( float deltaTime ){
		//TODO: what to update?
	}
	
	public void triggerEvent(){
		if(!repeatable){
			if(!triggeredOnce){
				for(Entity e : entityList){
					action.act( e );
				}
				triggeredOnce = true;
			}
		} 
		if(repeatable){
			if(!repeatTriggeredOnce){
				for(Entity e : entityList){
					action.act( e );
				}
				repeatTriggeredOnce = true;
			}
		}
	}
	

	
	public void addEntityToTrigger(Entity entity){
		entityList.add( entity );
	}
	
	public void addIAction(IAction action){
		this.action = action;
	}
}