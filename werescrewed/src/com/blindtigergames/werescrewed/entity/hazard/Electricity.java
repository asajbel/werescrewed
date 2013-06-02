package com.blindtigergames.werescrewed.entity.hazard;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEmitter;
import com.blindtigergames.werescrewed.util.Util;

public class Electricity extends Hazard {

	public ParticleEffect particleEffect;
	public Array< ParticleEmitter > particleEmitter;
	protected final float size = 20.0f;
	protected float width, height, hSize, wSize;
	protected boolean isHori;

	public Electricity( String name, Vector2 pos1, Vector2 pos2, World world,
			boolean isActive ) {
		super( name, pos1, null, world,isActive );
		entityType = EntityType.HAZARD;
		hazardType = HazardType.ELECTRICITY;

		this.world = world;
		this.activeHazard = isActive;

		if ( pos1.y == pos2.y ) {
			this.isHori = true;
		} else if ( pos1.x == pos2.x ) {
			this.isHori = false;
		}
		// X distance > Y distance.
		else if ( Math.abs( Math.abs( pos1.x ) - Math.abs( pos2.x ) ) > Math
				.abs( Math.abs( pos1.y ) - Math.abs( pos2.y ) ) ) {
			this.isHori = true;
		}
		// Y distance > X distance.
		else {
			this.isHori = false;
		}

		particleEffect = WereScrewedGame.manager.getParticleEffect( "steam" );// ParticleEffect.loadEffect("steam");
		setWidthHeight( pos1, pos2 );
		constructBody( pos1 );
		for ( ParticleEmitter PE : particleEffect.getEmitters( ) )
			PE.setContinuous( false );
	}

	private void setWidthHeight( Vector2 pos1, Vector2 pos2 ) {
		this.width = -( pos1.x - pos2.x );
		this.height = -( pos1.y - pos2.y );

		if ( isHori ) {
			this.wSize = 0;
			this.hSize = size;

		} else {
			this.wSize = size;
			this.hSize = 0;
		}
	}

	public void constructBody( Vector2 pos1 ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;

		bodyDef.position.set( pos1.x * Util.PIXEL_TO_BOX, pos1.y
				* Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		FixtureDef elecFixtureDef = new FixtureDef( );
		EdgeShape polygon = new EdgeShape( );

		polygon.set( ( wSize ) * Util.PIXEL_TO_BOX, ( hSize )
				* Util.PIXEL_TO_BOX, ( -wSize ) * Util.PIXEL_TO_BOX, ( -hSize )
				* Util.PIXEL_TO_BOX );
		elecFixtureDef.shape = polygon;
		elecFixtureDef.isSensor = true;
		elecFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		elecFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( elecFixtureDef );

		polygon.set( ( wSize ) * Util.PIXEL_TO_BOX, ( hSize )
				* Util.PIXEL_TO_BOX, ( width + wSize ) * Util.PIXEL_TO_BOX,
				( height + hSize ) * Util.PIXEL_TO_BOX );
		elecFixtureDef.shape = polygon;
		elecFixtureDef.isSensor = true;
		elecFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		elecFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( elecFixtureDef );

		polygon.set( ( width + wSize ) * Util.PIXEL_TO_BOX, ( height + hSize )
				* Util.PIXEL_TO_BOX, ( width - wSize ) * Util.PIXEL_TO_BOX,
				( height - hSize ) * Util.PIXEL_TO_BOX );
		elecFixtureDef.shape = polygon;
		elecFixtureDef.isSensor = true;
		elecFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		elecFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( elecFixtureDef );

		polygon.set( ( width - wSize ) * Util.PIXEL_TO_BOX, ( height - hSize )
				* Util.PIXEL_TO_BOX, ( -wSize ) * Util.PIXEL_TO_BOX, ( -hSize )
				* Util.PIXEL_TO_BOX );
		elecFixtureDef.shape = polygon;
		elecFixtureDef.isSensor = true;
		elecFixtureDef.filter.categoryBits = Util.CATEGROY_HAZARD;
		elecFixtureDef.filter.maskBits = Util.CATEGORY_PLAYER;
		body.createFixture( elecFixtureDef );

		polygon.dispose( );

		body.setUserData( this );
	}

	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		particleEffect.draw( batch, deltaTime, camera );
		if ( activeHazard ) {
			particleEffect.start( );
		}
	}
}
