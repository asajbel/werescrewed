package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.player.Player;

/**
 * 
 * An extension of the Hazard class that makes Spike-type Hazards.
 * Spikes are of varying length and only hurt the player if they 
 * touch the top part of the spike, not the side.
 * 
 * @author Jenn Makaiwi & Ed Boning.
 *
 */

//  */\/\/\/\/\/\/\/\/\/\/\/\/\/\
//Just your standard spikes.
public class Spikes extends Hazard {

	private float height, width;
	
	public Spikes( String name, Vector2 pos, float height, float width, World world, boolean isActive ) {
		super( name, pos, height, width, world, isActive );
		this.height = height;
		this.width = width;
	}

	@Override
	public void performContact ( Player player ) {
		/*Possible Player-Spike collision test
		  (player.x + player.width > spikes.x) &&
		   (player.x < spikes.x + spikes.width) && 
		   (player.y <= spikes.y + spikes.height)
		*/
		player.killPlayer( );
	}
	
	@Override
	public void update( float deltaTime ) {
		// TODO: Write method to make SOMETHING appear on Hazard Test Screen.
	}

	@Override
	public void draw( SpriteBatch batch ) {
		// TODO: Write method to make SOMETHING appear on Hazard Test Screen.
	}
}
