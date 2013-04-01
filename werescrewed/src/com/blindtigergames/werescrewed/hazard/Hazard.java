package com.blindtigergames.werescrewed.hazard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * @author Jenn Makaiwi & Ed Boning.
 *
 */

public class Hazard extends Platform {

	public boolean activeHazard;
	
	public Hazard( String name, Vector2 pos, Texture texture, World world, 
			float width, float height, boolean isHazardActive ) {
		super( name, pos, texture, world);
		entityType = EntityType.HAZARD;
		
		this.world = world;
		this.activeHazard = isHazardActive;
	}
	
	/**
	 * Used to load in a Complex hazard (via entityDef)
	 * 
	 * @param name - String
	 * @param type - EntityDef
	 * @param world - World
	 * @param posPix - position in pixels
	 */
	public Hazard( String name, EntityDef type, World world,
			Vector2 posPix) {
		super( name, type, world, posPix);
		
		entityType = EntityType.HAZARD;
		activeHazard = true;
	}

	
	/**
	 * Returns current state of hazard. active == true == on / active == false == off
	 */
	public boolean isActiveHazard ( ) {
		return activeHazard;
	}
	
	/**
	 * @param state
	 * 		Determines if boolean active is on (true) or off (false).
	 */
	public void setActiveHazard ( boolean state ) {
		this.activeHazard = state;
		Gdx.app.log( "State changed", " " + activeHazard );
	}
	
	public void constructBody( Vector2 position, float width, float height ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( width * Util.PIXEL_TO_BOX, height * Util.PIXEL_TO_BOX );
		FixtureDef hazardFixtureDef = new FixtureDef( );
		hazardFixtureDef.shape = polygon;
		body.createFixture( hazardFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
	}
	
	/**
	 * Performs check to see if a player has collided with a hazard.
	 * @param player Player
	 * @param fixture Fixture
	 */
	public void performContact ( Player player, Fixture fixture ) {
		if (activeHazard) {
			player.killPlayer( );
		}
	}
}
