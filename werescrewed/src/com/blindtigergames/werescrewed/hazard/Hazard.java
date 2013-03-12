package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.player.Player;

/**
 * 
 * @author Jenn Makaiwi & Ed Boning.
 *
 */

public class Hazard extends Platform {

	public boolean active;
	
	public Hazard( String name, Vector2 pos, Texture texture, World world, boolean isActive ) {
		super( name, pos, texture, world);
		entityType = EntityType.HAZARD;
	}
	
	public Hazard( String name, EntityDef type, World world,
			Vector2 posPix, float rot, Vector2 scale, Texture texture,
			boolean solid ) {
		super( name, type, world, posPix, rot, scale );
		this.sprite = constructSprite( texture );
		//type used to determine what type of entity for collisions
		entityType = EntityType.HAZARD;
	}

	// Returns current state of hazard. active == true == on / active == false == off
	public boolean isActive ( ) {
		return active;
	}
	
	/**
	 * @param state
	 * 		Determines if boolean active is on (true) or off (false).
	 */
	public void setActive ( boolean state ) {
		this.active = state;
	}
	
	//Performs check to see if a player has collided with a hazard.
	public void performContact ( Player player, Fixture fixture ) {
		player.killPlayer( );
	}
}
