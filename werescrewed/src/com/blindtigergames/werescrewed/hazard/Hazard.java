package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;

/**
 * 
 * @author Jenn Makaiwi & Ed Boning.
 *
 */

public class Hazard extends Entity {

	public boolean active;
	
	public Hazard( String name, EntityDef type, World world,
			Vector2 posPix, float rot, Vector2 scale, Texture texture,
			boolean solid, float anchRadius ) {
		super( name, type, world, posPix, rot, scale, texture, solid,
				anchRadius );
		//type used to determine what type of entity for collisions
		entityType = EntityType.HAZARD;
	}

	//Performs check to see if a player has collided with a hazard.
	public void performContact ( Player player ) {
		player.killPlayer( );
	}
	
	public boolean isActive ( ) {
		return active;
	}
}
