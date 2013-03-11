package com.blindtigergames.werescrewed.hazard;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.platforms.Tile;
import com.blindtigergames.werescrewed.player.Player;
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
	
	protected Vector < Tile > tiles = new Vector <Tile>();
	protected Vector2 bodypos;
	protected Vector2 defaultPos;
	protected float tileConstant = 32.0f;
	protected boolean hori;
	protected Orientation ori;

	/**
	 * 
	 * @param name
	 * @param pos
	 * @param height
	 * @param width
	 * @param world
	 * @param isActive
	 * 		Determines if hazard is currently active.
	 * @param invert
	 * 		False if spikes face positive direction (up, right),
	 * 		True if spikes face negative direction (down, left).
	 * @param horizontal
	 * 		Meant for 1x1 spikes, determines orientation of spikes
	 * 		i.e. facing up/down or left/right.
	 */
	public Spikes( String name, Vector2 pos, float height, float width,
			World world, boolean isActive, boolean invert, boolean horizontal ) {
		super( name, pos, ( Texture ) WereScrewedGame.manager
				.get( WereScrewedGame.dirHandle + "/common/spikes.png" ),
				world, isActive );
		entityType = EntityType.HAZARD;
		
		if ( height > width ) {
			this.hori = false;
		}
		else if ( width > height ) {
			this.hori = true;
		}
		else {
			this.hori = horizontal;
		}
		
		if ( hori ) {
			if ( invert )
				ori = Orientation.DOWN;
			else
				ori = Orientation.UP;
		}
		else {
			if ( invert )
				ori = Orientation.LEFT;
			else
				ori = Orientation.RIGHT;
		}
		
		this.defaultPos = pos;
		this.world = world;
		this.active = isActive;
		// this.sprite = constructSprite( (Texture) WereScrewedGame.manager
		// .get( WereScrewedGame.dirHandle + "/common/spikes.png" ) );
		constructBody( pos, height, width );
		constructTile( pos, height, width );
	}

	public Orientation getOrientation( ) {
		return this.ori;
	}
	
	@Override
	public void performContact( Player player, Fixture fixture ) {
		if ( fixture == this.body.getFixtureList( ).get( 1 ) && active ){
			player.killPlayer( );			
		}
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
		
		if ( hori ) {
			polygon.setAsBox( ( ( ( width * tileConstant ) / 2 ) - 6 ) * Util.PIXEL_TO_BOX,
								( height * tileConstant )  / 2  * Util.PIXEL_TO_BOX );
		}
		else {
			polygon.setAsBox( ( width * tileConstant ) / 2 * Util.PIXEL_TO_BOX,
								( ( ( height * tileConstant )  / 2) - 6 )  * Util.PIXEL_TO_BOX );
		}
		
		spikeFixtureDef.shape = polygon;
		body.createFixture( spikeFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
		
		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
	}

	private void constructTile( Vector2 position, float height, float width ) {
		Tile insub;
		float offset_x, offset_y;
		
		Sprite temp = constructSprite( ( Texture ) WereScrewedGame.manager
				.get( WereScrewedGame.dirHandle + "/common/spikes.png" ) );
		for ( int i = 0; i < width; i++ ) {
			offset_x = ( i - width / 2 + 1 ) * tileConstant;
			for ( int j = 0; j < height; j++ ) {
				offset_y = ( j - height / 2 + 1 ) * tileConstant;
				temp.setOrigin( offset_x, offset_y );
				temp.setPosition( bodypos.x - offset_x, bodypos.y
						- offset_y );
				temp.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
				insub = new Tile( offset_x, offset_y, temp );
				tiles.add( insub );
			}
		}
	}
	
	@Override
	public void update( float deltaTime ) {
		/*if (active) {
			switch ( ori ) {
			case RIGHT:
				break;
			case LEFT:
				break;
			case UP:
				break;
			case DOWN:
				break;
			default:
				break;
			}
		}
		else {
			switch ( ori ) {
			case RIGHT:
				
				break;
			case LEFT:
				break;
			case UP:
				break;
			case DOWN:
				break;
			default:
				break;
			}
		}*/
	}

	@Override
	public void draw( SpriteBatch batch ) {
		Tile d;
		Iterator< Tile > v = tiles.listIterator( );
		while ( v.hasNext( ) ) {
			d = v.next( );

			switch ( ori ) {
			case RIGHT:
				d.tileSprite.setOrigin( d.tileSprite.getWidth( ) / 2, d.tileSprite.getHeight( ) / 2 );
				d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y 
						- d.yOffset );
				d.tileSprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) - 90 );

				break;
			case LEFT:
				d.tileSprite.setOrigin( d.tileSprite.getWidth( ) / 2, d.tileSprite.getHeight( ) / 2 );
				d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
						- d.yOffset );
				d.tileSprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) + 90 );
				break;
			case UP:
				d.tileSprite.setOrigin( d.tileSprite.getWidth( ) / 2, d.tileSprite.getHeight( ) / 2 );
				d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
						- d.yOffset );
				d.tileSprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
				break;
			case DOWN:
				d.tileSprite.setOrigin( d.tileSprite.getWidth( ) / 2, d.tileSprite.getHeight( ) / 2 );
				d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
						- d.yOffset );
				d.tileSprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) + 180 );
				break;
			default:
				d.tileSprite.setPosition( bodypos.x - d.xOffset, bodypos.y
						- d.yOffset );
				d.tileSprite.setRotation( MathUtils.radiansToDegrees
						* body.getAngle( ) );
			}
				d.tileSprite.draw( batch );
		}
	}
}
