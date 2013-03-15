package com.blindtigergames.werescrewed.hazard;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.platforms.Tile;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * @author Jenn
 *	Meant to draw moving saws.
 *  UNFINISHED!!
 *
 */
public class Saws extends Hazard {

	protected Vector < Tile > tiles = new Vector <Tile>();
	protected Vector2 bodypos;
	protected float tileConstant = 16.0f;
	
	public Saws( String name, Vector2 pos, float size,
			World world, boolean isActive ) {
		super( name, pos, null, world, size, size, isActive );
		entityType = EntityType.HAZARD;

		this.world = world;
		this.active = isActive;

		constructBody( pos, size, size );
	}

	@Override
	public void performContact( Player player, Fixture fixture ) {
		/*
		 * Possible Player-Spike collision test (player.x + player.width >
		 * spikes.x) && (player.x < spikes.x + spikes.width) && (player.y <=
		 * spikes.y + spikes.height)
		 */
		if ( fixture == this.body.getFixtureList( ).get( 1 ) ){
			player.killPlayer( );			
		}
		Gdx.app.log( "Hello", "World" );
	}

	private void constructBody( Vector2 position, float height, float width ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;

		bodyDef.position.set( ( position.x + (width * tileConstant )/ 2 ) * Util.PIXEL_TO_BOX,
				( position.y + ( height * tileConstant) / 2 ) * Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( (width * tileConstant ) / 2 * Util.PIXEL_TO_BOX, ( height * tileConstant )  / 2
				* Util.PIXEL_TO_BOX );
		FixtureDef spikeFixtureDef = new FixtureDef( );
		spikeFixtureDef.shape = polygon;
		body.createFixture( spikeFixtureDef );

		polygon.dispose( );

		body.setUserData( this );

		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
	}
}