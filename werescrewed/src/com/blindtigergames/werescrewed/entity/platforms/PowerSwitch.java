package com.blindtigergames.werescrewed.entity.platforms;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;

public class PowerSwitch extends EventTrigger {

	private boolean state;

	/**
	 * creates a PowerSwitch at location position
	 * 
	 * @param name
	 *            String
	 * @param position
	 *            Vector2
	 * @param world
	 *            World
	 */
	public PowerSwitch( String name, Vector2 position, World world ) {
		super( name, world );
		contructRectangleBody( 64, 64, position );
		entityType = EntityType.POWERSWITCH;
	}

	public void doAction( ) {
		// Gdx.app.log("doAction ","");
		if ( state == false ) {
			// Gdx.app.log("doAction: ","if");
			runBeginAction( null );
			state = true;
		} else {
			// Gdx.app.log("doAction ","else");
			runEndAction( );
			state = false;
		}

	}
}
