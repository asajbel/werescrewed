package com.blindtigergames.werescrewed.entity.particles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;
import com.blindtigergames.werescrewed.util.Util;

public class Steam extends Platform {

	public ParticleEffect particleEffect;
	float width;
	float height;
	boolean started, tempCheckForCollision = true; // remove temp when the
													// particles fit the size
													// correctly;

	/**
	 * Creates a steam vent which moves players up when they collide
	 * 
	 * @param name
	 *            String
	 * @param positionPixels
	 *            Vector2
	 * @param texture
	 *            Texture
	 * @param body
	 *            Body
	 * @param solid
	 *            boolean
	 * @param pixelWidth
	 *            float
	 * @param pixelHeight
	 *            float
	 */
	public Steam( String name, Vector2 positionPixels, float pixelWidth,
			float pixelHeight, World world ) {

		super( name, positionPixels, null, null );
		entityType = EntityType.STEAM;
		width = pixelWidth;
		height = pixelHeight;
		this.world = world;
		particleEffect = WereScrewedGame.manager
				.getParticleEffect( "fastSteam" );// ParticleEffect.loadEffect("steam");
		particleEffect.setOffset( 0f, -pixelHeight + 20 );
		particleEffect.setPosition( positionPixels.x, positionPixels.y );

		// addFrontParticleEffect( "fastSteam", false, true ).setOffset( 0,
		// -pixelHeight+20 );
		// getEffect( "fastSteam" )
		constructBody( positionPixels, pixelHeight, pixelWidth );

		this.active = true;
		particleEffect.start( );
		started = true;
		loadSounds( );
		postLoad( );
	}

	/**
	 * Draws the particles for steam from the base of its body
	 * 
	 * @param batch
	 *            spriteBatch
	 * @param deltaTime
	 *            float
	 */
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {

		if ( this.active ) {

			if ( !started ) {
				// getEffect( "fastSteam" ).start( );
				particleEffect.start( );
				started = true;
			}

			particleEffect.setPosition( this.getPositionPixel( ).x,
					this.getPositionPixel( ).y );
			particleEffect.setEffectAngle( body.getAngle( ) );
			particleEffect.draw( batch, deltaTime, camera );

		} else {
			started = false;
			// getEffect( "fastSteam" ).allowCompletion( );
			particleEffect.allowCompletion( );
			particleEffect.setPosition( this.getPositionPixel( ).x,
					this.getPositionPixel( ).y );
			particleEffect.setAngle( body.getAngle( ) );
			particleEffect.draw( batch, deltaTime, camera );
		}
		super.draw( batch, deltaTime, camera);

	}

	@Override
	public void update( float dT ) {
		SoundRef idle = sounds.getSound( "idle" );
		idle.setVolume( this.isActive() ? 
							SoundManager.getNoiseVolume( ) * idle.calculatePositionalVolume(getPositionPixel( ), Camera.CAMERA_RECT ) 
							: 0f );
		super.update( dT );
	}

	/**
	 * builds body of steam collision rectangle
	 * 
	 * @param position
	 *            Vector2
	 * @param height
	 *            float
	 * @param width
	 *            float
	 */
	private void constructBody( Vector2 position, float height, float width ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( position.x * Util.PIXEL_TO_BOX, position.y
				* Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( width * Util.PIXEL_TO_BOX, height * Util.PIXEL_TO_BOX );
		FixtureDef steamFixtureDef = new FixtureDef( );
		steamFixtureDef.shape = polygon;
		steamFixtureDef.isSensor = true;
		body.createFixture( steamFixtureDef );

		polygon.dispose( );

		body.setUserData( this );

	}

	/**
	 * : GET RID OF TEMPCOLLISION WHEN STEAM PARTICLES MATCH THE BODY ALSO GET
	 * RID OF THE TEMPCOLLISION IN CONTACT LISTENER
	 */
	public void setTempCollision( boolean b ) {
		this.tempCheckForCollision = b;
	}

	public boolean getTempCollision( ) {
		return this.tempCheckForCollision;
	}

	public void loadSounds( ) {
		if ( sounds == null )
			sounds = new SoundManager( );
		SoundRef steamSound = sounds.getSound( "idle",
				WereScrewedGame.dirHandle + "/common/sounds/steam.ogg" );
		steamSound.setRange( 600.f );
		steamSound.setFalloff( 2.0f );
		steamSound.setOffset( new Vector2(0.0f, height / 2.0f) );
	}
}
