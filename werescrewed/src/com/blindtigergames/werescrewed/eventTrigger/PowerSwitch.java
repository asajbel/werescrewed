package com.blindtigergames.werescrewed.eventTrigger;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
import com.blindtigergames.werescrewed.entity.action.IAction;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.util.Util;


public class PowerSwitch extends EventTrigger{

	private boolean state;
	
	/**
	 * creates a PowerSwitch at location position
	 * 
	 * @param name String
	 * @param position Vector2
	 * @param world World
	 */
	public PowerSwitch( String name, Vector2 position, World world ) {
		super( name, world );
		contructRectangleBody(64, 64, position);
		entityType = EntityType.POWERSWITCH;
	}
	
	public void doAction(){
		Gdx.app.log("doAction ","");
		if(state == false){
			Gdx.app.log("doAction: ","if");
			runBeginAction();
			state = true;
		}
		else{
			Gdx.app.log("doAction ","else");
			runEndAction();
			state = false;
		}
		
	}
}



