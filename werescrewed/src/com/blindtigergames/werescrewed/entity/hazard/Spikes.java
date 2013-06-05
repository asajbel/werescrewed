package com.blindtigergames.werescrewed.entity.hazard;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.platforms.Tile;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * An extension of the Hazard class that makes Spike-type Hazards. Spikes are of
 * varying length and only hurt the player if they touch the top part of the
 * spike, not the side.
 * 
 * @author Jenn Makaiwi & Ed Boning.
 * 
 */

// */\/\/\/\/\/\/\/\/\/\/\/\/\/\
// Just your standard spikes.
public class Spikes extends Hazard {

	private TextureRegion texRotated, texUnRotated;

	protected Vector< Tile > tiles = new Vector< Tile >( );
	protected Vector2 bodypos;
	protected float tileConstant = 64.0f;
	protected boolean hori;
	protected Orientation ori;
	protected TextureRegion tex;
	protected Fixture pointyEnd;

	/**
	 * 
	 * @param name
	 * @param pos
	 * @param height
	 * @param width
	 * @param world
	 * @param isActive
	 *            Determines if hazard is currently active.
	 * @param invert
	 *            False if spikes face positive direction (up, right), True if
	 *            spikes face negative direction (down, left).
	 * @param horizontal
	 *            Meant for 1x1 spikes, determines orientation of spikes i.e.
	 *            facing up/down or left/right.
	 */
	public Spikes( String name, Vector2 pos, float width, float height,
			World world, boolean isActive, boolean invert, boolean horizontal ) {
		super( name, pos, null, world, isActive );
		entityType = EntityType.HAZARD;
		hazardType = HazardType.SPIKES;
		
		texUnRotated = WereScrewedGame.manager
				.getAtlas( "common-textures" ).findRegion( "spikes" );
		texRotated = WereScrewedGame.manager
				.getAtlas( "common-textures" ).findRegion( "spikes_rotated" );

		if ( height > width ) {
			this.hori = false;
			tex = texRotated;
		} else if ( width > height ) {
			this.hori = true;
			tex = texUnRotated;
		} else {
			this.hori = horizontal;
			tex = texUnRotated;
		}

		if ( hori ) {
			if ( invert )
				ori = Orientation.DOWN;
			else
				ori = Orientation.UP;
		} else {
			if ( invert )
				ori = Orientation.LEFT;
			else
				ori = Orientation.RIGHT;
		}

		this.world = world;
		this.activeHazard = isActive;
		constructBody( height, width, pos );
		constructTile( pos, height, width );
	}

	public Orientation getOrientation( ) {
		return this.ori;
	}

	// @Override
	// public void performContact( Player player, Fixture fixture ) {
	// if ( fixture == this.body.getFixtureList( ).get( 0 ) && activeHazard ) {
	// player.killPlayer( );
	// }
	// }

	/**
	 * 
	 * @param position
	 *            Vector2
	 * @param height
	 *            float
	 * @param width
	 *            float
	 */
	private void constructBody( float height, float width, Vector2 position ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;

		PolygonShape polygon = new PolygonShape( );
		FixtureDef spikeFixtureDef = new FixtureDef( );
		if ( hori ) {

			bodyDef.position.set( ( position.x ) * Util.PIXEL_TO_BOX,
					( position.y ) * Util.PIXEL_TO_BOX );

			polygon.setAsBox( ( width * tileConstant ) / 2 * Util.PIXEL_TO_BOX,
					( height * tileConstant ) / 4 * Util.PIXEL_TO_BOX );

			spikeFixtureDef.shape = polygon;
			spikeFixtureDef.isSensor = true;
			spikeFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
			spikeFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;

		} else {

			bodyDef.position.set( ( position.x ) * Util.PIXEL_TO_BOX,
					( position.y ) * Util.PIXEL_TO_BOX );

			polygon.setAsBox( ( width * tileConstant ) / 4 * Util.PIXEL_TO_BOX,
					( height * tileConstant ) / 2 * Util.PIXEL_TO_BOX );

			spikeFixtureDef.shape = polygon;
			spikeFixtureDef.isSensor = true;
			spikeFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
			spikeFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		}

		body = world.createBody( bodyDef );
		pointyEnd = body.createFixture( spikeFixtureDef );
		body.setGravityScale( 0.1f );

		polygon.dispose( );

		body.setUserData( this );

		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );

	}

	private void constructTile( Vector2 position, float height, float width ) {
		Tile insub;
		float offset_x, offset_y;
		switch ( ori ) {
		case DOWN:
			tex.flip( false, true );
			break;
		case LEFT:
			tex.flip( true, false );
			break;
		case RIGHT:
			break;
		case UP:
			break;
		default:
			break;

		}

		Sprite temp;
		for ( int i = 0; i < width; i++ ) {
			if ( hori )
				offset_x = ( i - width / 2 + 1 ) * tileConstant;
			else
				offset_x = ( i - width / 2 + 1 ) * tileConstant - 16f;
			for ( int j = 0; j < height; j++ ) {
				temp = new Sprite( tex );
				if ( hori )
					offset_y = ( j - height / 2 + 1 ) * tileConstant - 16f;
				else
					offset_y = ( j - height / 2 + 1 ) * tileConstant;

				temp.setOrigin( offset_x, offset_y );
				temp.setPosition( bodypos.x - offset_x, bodypos.y - offset_y );
				temp.rotate( MathUtils.radiansToDegrees * body.getAngle( ) );
				insub = new Tile( offset_x, offset_y, temp );
				tiles.add( insub );
			}
		}
	}

	@Override
	public void update( float deltaTime ) {
		if ( active ) {
			super.update( deltaTime );
			bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		}
	}

	@Override
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		Tile d;
		Iterator< Tile > v = tiles.listIterator( );
		while ( v.hasNext( ) ) {
			d = v.next( );
			d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
					- d.yOffset );
			d.tileSprite.setRotation( MathUtils.radiansToDegrees
					* body.getAngle( ) );
			if ( d.tileSprite.getBoundingRectangle( ).overlaps(
					camera.getBounds( ) ) ) {
				d.tileSprite.draw( batch );
			}
		}
	}
}
