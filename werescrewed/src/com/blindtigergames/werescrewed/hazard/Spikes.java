package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

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
	
	public Spikes( String name, Vector2 pos, float height, float width, World world, boolean isActive ) {
		super( name, pos, height, width, world, isActive );
		entityType = EntityType.HAZARD;
		
		this.world = world;
		this.active = isActive;
		constructBody( pos, height, width );
	}

	@Override
	public void performContact ( Player player ) {
		/*Possible Player-Spike collision test
		  (player.x + player.width > spikes.x) &&
		   (player.x < spikes.x + spikes.width) && 
		   (player.y <= spikes.y + spikes.height)
		*/
		player.killPlayer( );
		Gdx.app.log( "Hello", "World" );
	}
	
	private void constructBody(Vector2 position, float height, float width){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( width * Util.PIXEL_TO_BOX, height * Util.PIXEL_TO_BOX );
		FixtureDef steamFixtureDef = new FixtureDef( );
		steamFixtureDef.shape = polygon;
		body.createFixture( steamFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
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
