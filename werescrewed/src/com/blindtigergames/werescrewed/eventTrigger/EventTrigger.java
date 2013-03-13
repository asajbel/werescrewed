package com.blindtigergames.werescrewed.eventTrigger;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.IAction;
import com.blindtigergames.werescrewed.hazard.Hazard;
import com.blindtigergames.werescrewed.particles.Steam;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;


public class EventTrigger extends Entity{
	
	private boolean repeatable = false;
	private boolean repeatBeginTriggeredOnce = false;
	private boolean repeatEndTriggeredOnce = false;
	private boolean activated = false;
	private boolean beginTriggeredOnce = false, endTriggeredOnce = false;
	private boolean twoPlayersToActivate = false, twoPlayersToDeactivate = false;
	private boolean playerOneContact = false, playerTwoContact = false;
	private ArrayList<Entity> entityList;
	private IAction beginAction, endAction;
	private boolean actOnEntity = false;
	private int playerOneCount = 0, playerTwoCount = 0;
	
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
	
	public void constructVertBody(Array<Vector2> vertices, Vector2 positionPixel){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ));
		body = world.createBody( bodyDef );
		
		PolygonShape polygon = new PolygonShape();
		Vector2[] verts = new Vector2[vertices.size -1];
		System.out.println(verts.length);
		int i = 0;
		for(int j = 0; j < vertices.size; j++){
			if(j == vertices.size - 1) continue;
			Vector2 v = vertices.get( j );
			verts[i] = new Vector2(v.x * Util.PIXEL_TO_BOX, v.y * Util.PIXEL_TO_BOX);
			System.out.println( "v: " + v + " verts[" + i + "] " + verts[i] );
			++i;
		}
		polygon.set( verts );
		
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
	public boolean isActingOnEntity(){
		return actOnEntity;
	}
	
	public void setActingOnEntity(boolean b){
		actOnEntity = b;
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
	 * sets boolean to see if this event takes two players to turn on
	 * @author Ranveer
	 */
	public void setTwoPlayersToActivate(boolean b){
		twoPlayersToActivate = b;
	}
	/**
	 * returns boolean to see if this event takes two players to turn on
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean takeTwoPlayersToActivate(){
		return twoPlayersToActivate;
	}
	
	/**
	 * sets boolean to see if this event takes two players to turn off
	 * @author Ranveer
	 */
	public void setTwoPlayersToDeactivate(boolean b){
		twoPlayersToDeactivate = b;
	}
	/**
	 * returns boolean to see if this event takes two players to turn off
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean takeTwoPlayersToDeactivate(){
		return twoPlayersToDeactivate;
	}
	
	/**
	 * returns boolean to see if player one is colliding with this event trigger
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean playerOneColliding(){
		return playerOneContact;
	}
	
	/**
	 * returns boolean to see if player two is colliding with this event trigger
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean playerTwoColliding(){
		return playerTwoContact;
	}
	
	/**
	 * Checks if player is currently colliding with this
	 * EventTrigger
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean isActivated(){
		return activated;
	}
	
	/**
	 * Sets activated to true or false, depending on player collision
	 * and depending on if it takes two players to activate/deactivate
	 * and if the event is repeatable or not
	 * @param active - boolean
	 * @author Ranveer
	 */
	public void setActivated(boolean active, String name){
		if(active == true){

			// If it takes two players to turn it on
			if(twoPlayersToActivate)
			{
				if(!playerOneContact)
				{
					if(name.equals( "player1" ))
					{
						playerOneContact = true;
					}
				}
				if(!playerTwoContact)
				{
					if(name.equals( "player2" ))
					{
						playerTwoContact = true;
					}
				}
				//When both players collide, then turn it active
				if(playerOneContact && playerTwoContact){
					this.activated = true;
				}
			//Else it takes only 1 player to turn it on
			}else{
				if(name.equals( "player2" ))
				{
					playerTwoContact = true;
				}
				if(name.equals( "player1" ))
				{
					playerOneContact = true;
				}
				this.activated = true;
			}
		}
		else if(active == false)
		{
//			//If the event isn't repeatable, then it should never turn back to false
//			if(!repeatable){
//				if(triggeredOnce){
//					return;
//					//Not repeatable so can't turn it off
//					// later it will be deleted here
//				}
//			}
//			else{
//				//Else if it is repeatable, check if we ran it already
//				// then check if it takes two players to deactivate or one player
//				if(repeatTriggeredOnce)
//				{
					if(twoPlayersToDeactivate)
					{
						if(playerOneContact)
						{
							if(name.equals( "player1" ))
							{
								playerOneContact = false;
							}
						}
						if(playerTwoContact)
						{
							if(name.equals( "player2" ))
							{
								playerTwoContact = false;
							}
						}
						
						if(!playerOneContact && !playerTwoContact)
						{
							repeatEndTriggeredOnce = false;
							repeatBeginTriggeredOnce = false;
							this.activated = false;
						}
					}
					//else takes 1 player to deactivate
					else
					{
						if(name.equals( "player1" ))
						{
							playerOneContact = false;
							playerOneCount++;
						}
						if(name.equals( "player2" ))
						{
							playerTwoContact = false;
							playerTwoCount++;
						}
						if(playerOneCount == 2){
							if(repeatEndTriggeredOnce){
								if(!playerOneContact && !playerTwoContact)
								{
									repeatEndTriggeredOnce = false;
								}
							}
							
							repeatBeginTriggeredOnce = false;
							this.activated = false;
							playerOneCount = 0;
						}
						if( playerTwoCount == 2){
							if(repeatEndTriggeredOnce){
								if(!playerOneContact && !playerTwoContact)
								{
									repeatEndTriggeredOnce = false;
								}
							}
							repeatBeginTriggeredOnce = false;
							this.activated = false;
							playerTwoCount = 0;
						}
					}
				//}
			//}
			
		}
	}
	
	
	/**
	 * random prints, no real need for update function yet
	 * @param - float deltaTime
	 * @author Ranveer
	 */
	public void update( float deltaTime ){
		System.out.println( "p1: " + playerOneContact 
				+ ", p2: " + playerTwoContact 
				+ ", activated: " + this.activated
				+ "repeatBegin: " + repeatBeginTriggeredOnce
				+ "repeatEnd: " + repeatEndTriggeredOnce);
		
		//System.out.println( repeatTriggeredOnce );
	}
	
	/**
	 * triggers the beginning Action depending on if it takes two players to
	 * run it or just one player
	 * @author Ranveer
	 */
	public void triggerBeginEvent(){
		if(twoPlayersToActivate){
			if(playerOneContact && playerTwoContact){
				runBeginAction();
			}
		}
		else{
			runBeginAction();
		}
	}
	
	/**
	 * triggers the ending Action depending on if it takes two players to
	 * run it or just one player
	 * @author Ranveer
	 */
	public void triggerEndEvent(){
		if(twoPlayersToDeactivate){
			if(!playerOneContact && !playerTwoContact){
				runEndAction();
			}
		}
		else{
			runEndAction();
		}
	}
	
	/**
	 * Add entity to Event Trigger
	 * @param entity - Entity
	 * @author Ranveer
	 */
	public void addEntityToTrigger(Entity entity){
		entityList.add( entity );
	}
	
	/**
	 * Add IAction as a Begin Action
	 * @param action - IAction
	 * @author Ranveer
	 */
	public void addBeginIAction(IAction action){
		this.beginAction = action;
	}
	
	/**
	 * Add IAction as a End Action
	 * @param action - IAction
	 * @author Ranveer
	 */
	public void addEndIAction(IAction action){
		this.endAction = action;
	}
	
	/**
	 * gets the beginning action
	 * couldn't run on any arbitrary entities
	 * if they need to be in a list
	 * used for removing entities
	 */
	public IAction getBeginAction( ) {
		return beginAction;
	}
	
	/**
	 * calls act() with its begin action on every entity in this list
	 * depending on if its repeatable or not
	 * @author Ranveer
	 */
	private void runBeginAction(){
		if(!repeatable){
			if(!beginTriggeredOnce){
				if(actOnEntity){
					for(Entity e : entityList){
						if(beginAction != null){
							beginAction.act( e );
							Gdx.app.log( this.name,  " begin action" );
						}
					}
				}else{
					if(beginAction != null){
						beginAction.act( );
					}
				}
				beginTriggeredOnce = true;
			}
		} 
		if(repeatable){
			if(!repeatBeginTriggeredOnce){
				if(actOnEntity){
					for(Entity e : entityList){
						if(beginAction != null){
							beginAction.act( e );
							Gdx.app.log( this.name,  " begin action" );
						}
					}
				}else{
					if(beginAction != null){
						beginAction.act( );
					}
				}
				repeatBeginTriggeredOnce = true;
			}
		}
	}
	
	/**
	 * calls act() with its end action on every entity in this list
	 * depending on if its repeatable or not
	 * @author Ranveer
	 */
	private void runEndAction(){
		if(!repeatable){
			if(!endTriggeredOnce){
				if(actOnEntity){
					for(Entity e : entityList){
						if(endAction != null){
							endAction.act( e );
							Gdx.app.log( this.name,  " end action" );
						}
					}
				}else{
					if(endAction != null){
						endAction.act( );
					}
				}
				endTriggeredOnce = true;
			}
		} 
		if(repeatable){
			if(!repeatEndTriggeredOnce){
				if(actOnEntity){
					for(Entity e : entityList){
						if(endAction != null){
							endAction.act( e );
							Gdx.app.log( this.name,  " end action" );
						}
					}
				}else{
					if(endAction != null){
						endAction.act( );
					}
				}
				repeatEndTriggeredOnce = true;
			}
		}
	}
	/**
	 * UNUSED
	 * 
	 * THe idea for this function was to call actions for certain 
	 * types, but instead I changed IAction so that each 
	 * implementation of IAction would do the casting itself
	 * What this means is we have to be careful which entities are in which
	 * EventTriggers with the correct IActions
	 * 
	 * Check out the IAction: HazardActivateAction.java 
	 * to see the correct use
	 * @param entity
	 */
	@SuppressWarnings( "unused" )
	private void applyAction(Entity entity){
		switch(entity.getEntityType( )){
		case ENTITY:
			beginAction.act(entity);
			break;
		case HAZARD:
			beginAction.act( (Hazard) entity );
			break;
		case PLATFORM:
			beginAction.act( (Platform) entity );
			break;
		case STEAM:
			beginAction.act( (Steam) entity );
			break;
		case SKELETON:
			beginAction.act( (Skeleton) entity );
			break;
		case PLAYER:
			beginAction.act( (Player) entity );
			break;
		default:
			Gdx.app.log("Cannot apply action to", entity.toString( ));
		}
	}
}