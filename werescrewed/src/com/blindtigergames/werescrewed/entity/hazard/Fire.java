package com.blindtigergames.werescrewed.entity.hazard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.util.Util;

public class Fire extends Hazard {

	public ParticleEffect particleEffect;
	// public Array< ParticleEmitter > particleEmitter;
	protected float width;
	protected float height;
	boolean upsideDown = true, started = true;

	/**
	 * Constructor for fire
	 * 
	 * @param name
	 *            String
	 * @param pos
	 *            Vector2
	 * @param world
	 *            World
	 * @param isActive
	 *            boolean
	 * @param pixelWidth
	 *            float
	 * @param pixelHeight
	 *            float
	 */
	public Fire( String name, Vector2 pos, float width, float height,
			World world, boolean isActive ) {
		super( name, pos, null, world, width, height, isActive );
		entityType = EntityType.HAZARD;
		hazardType = HazardType.FIRE;

		this.width = width;
		this.height = height;
		this.world = world;
		this.activeHazard = isActive;
		particleEffect = WereScrewedGame.manager.getParticleEffect( "fire" );
		// particleEffect.setOffset(0f, -height);
		particleEffect.setPosition( pos.x, pos.y );

		// addFrontParticleEffect( "fire", false, true );
		constructBody( pos );
		particleEffect.start( );

		// for( ParticleEmitter PE: particleEmitter)
		// PE.setContinuous( false );

		// Sound s = WereScrewedGame.manager.get( "/data/sjfdsi.mp3",
		// Sound.class );
	}

	/**
	 * sets up physics bodies
	 * 
	 * @param position
	 *            Vector2
	 */
	private void constructBody( Vector2 position ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX, position.y
				* Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		FixtureDef fireFixtureDef = new FixtureDef( );
		EdgeShape polygon = new EdgeShape( );
		polygon.set( ( 0 ) * Util.PIXEL_TO_BOX, ( 0 ) * Util.PIXEL_TO_BOX,
				( width ) * Util.PIXEL_TO_BOX, ( height ) * Util.PIXEL_TO_BOX );
		fireFixtureDef.shape = polygon;
		fireFixtureDef.isSensor = true;
		fireFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		fireFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( fireFixtureDef );
		polygon.set( ( 0 ) * Util.PIXEL_TO_BOX, ( 0 ) * Util.PIXEL_TO_BOX,
				( width * -1 ) * Util.PIXEL_TO_BOX, ( height )
						* Util.PIXEL_TO_BOX );
		fireFixtureDef.shape = polygon;
		fireFixtureDef.isSensor = true;
		fireFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		fireFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( fireFixtureDef );
		polygon.set( ( width ) * Util.PIXEL_TO_BOX, ( height )
				* Util.PIXEL_TO_BOX, ( width * -1 ) * Util.PIXEL_TO_BOX,
				( height ) * Util.PIXEL_TO_BOX );
		fireFixtureDef.shape = polygon;
		fireFixtureDef.isSensor = true;
		fireFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		fireFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( fireFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
	}

	/**
	 * flips vertical direction of fire
	 */
	public void flip( ) {
		// particleEffect.flipY( );
		getEffect( "steam" ).flipY( );
		if ( upsideDown ) {
			body.setTransform( body.getPosition( ), ( float ) Math.PI );
		} else {
			body.setTransform( body.getPosition( ), 0.0f );
		}
		upsideDown = !upsideDown;
	}

	/**
	 * draws fire particles
	 * 
	 * @param batch
	 *            SpriteBatch
	 * @param deltaTime
	 *            float
	 */
	public void draw( SpriteBatch batch, float deltaTime ) {

		if ( Gdx.input.isKeyPressed( Input.Keys.BACKSLASH ) )
			this.activeHazard = false;

		if ( this.activeHazard ) {

			if ( !started ) {
				// getEffect( "fire" ).start( );
				particleEffect.start( );
				started = true;
			}

			particleEffect.setPosition( this.getPositionPixel( ).x,
					this.getPositionPixel( ).y );
			particleEffect.setAngle( body.getAngle( ) );
			particleEffect.draw( batch, deltaTime );

		} else {
			started = false;
			// getEffect( "fire" ).allowCompletion( );
			particleEffect.allowCompletion( );
			particleEffect.setPosition( this.getPositionPixel( ).x,
					this.getPositionPixel( ).y );
			particleEffect.setAngle( body.getAngle( ) );
			particleEffect.draw( batch, deltaTime );
		}
		super.draw( batch, deltaTime );
	}

}
