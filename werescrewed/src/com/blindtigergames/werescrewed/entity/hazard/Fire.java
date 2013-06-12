package com.blindtigergames.werescrewed.entity.hazard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;
import com.blindtigergames.werescrewed.util.Util;

public class Fire extends Hazard {

	public ParticleEffect particleEffect;
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
		super( name, pos, null, world, isActive );
		entityType = EntityType.HAZARD;
		hazardType = HazardType.FIRE;

		this.width = width;
		this.height = height;
		this.world = world;
		this.activeHazard = isActive;
		particleEffect = WereScrewedGame.manager.getParticleEffect( "fire_new" );
		particleEffect.setPosition( pos.x, pos.y );

		// addFrontParticleEffect( "fire", false, true );
		constructBody( pos );
		particleEffect.start( );

		// for( ParticleEmitter PE: particleEmitter)
		// PE.setContinuous( false );

		// Sound s = WereScrewedGame.manager.get( "/data/sjfdsi.mp3",
		// Sound.class );
		loadSounds();
	}

	void constructBody( Vector2 pos ){
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( width / 2 * Util.PIXEL_TO_BOX, height / 2
				* Util.PIXEL_TO_BOX );

		FixtureDef fixture = new FixtureDef( );
		fixture.isSensor = true;
		fixture.filter.categoryBits = Util.CATEGROY_HAZARD;
		fixture.filter.maskBits = Util.CATEGORY_PLAYER;
		fixture.shape = polygon;

		body.createFixture( fixture );
		body.setUserData( this );

		polygon.dispose( );
		
	}
	/**
	 * flips vertical direction of fire
	 */
	public void flip( ) {
		 particleEffect.flipY( );
		//getEffect( "steam" ).flipY( );
		if ( upsideDown ) {
			//this.setLocalRot( (float) Math.PI);
			//this.setPreviousTransformation( );
	
		} else {
			//this.setLocalRot( 0f );
		}
		upsideDown = !upsideDown;
	}
	public void update( float deltaTime){
		SoundRef idle = sounds.getSound( "idle" );
		idle.setVolume( this.activeHazard ? 
							SoundManager.getNoiseVolume( ) * idle.calculatePositionalVolume(getPositionPixel( ), Camera.CAMERA_RECT ) 
							: 0f );
		sounds.update( deltaTime );
	}
	/**
	 * draws fire particles
	 * 
	 * @param batch
	 *            SpriteBatch
	 * @param deltaTime
	 *            float
	 */
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
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
			//particleEffect.setAngle( body.getAngle( ) );
			particleEffect.draw( batch, deltaTime, camera );

		} else {
			started = false;
			// getEffect( "fire" ).allowCompletion( );
			particleEffect.allowCompletion( );
			particleEffect.setPosition( this.getPositionPixel( ).x,
					this.getPositionPixel( ).y );
			//particleEffect.setAngle( body.getAngle( ) );
			particleEffect.draw( batch, deltaTime, camera );
		}
		super.draw( batch, deltaTime, camera );
	}
		
	public void loadSounds( ) {
		if ( sounds == null )
			sounds = new SoundManager( );
		SoundRef fireSound = sounds.getSound( "idle",
				WereScrewedGame.dirHandle + "/common/sounds/flames.ogg" );
		fireSound.setRange( 1000.f );
		fireSound.setFalloff( 2.0f );
		fireSound.setOffset( new Vector2(0.0f, height / 2.0f) );
		fireSound.setVolume( 0.0f );
		SoundManager.addSoundToLoops( fireSound );
	}
}
