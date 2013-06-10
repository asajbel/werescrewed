package com.blindtigergames.werescrewed.entity.hazard;

import java.util.Vector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.platforms.Tile;
import com.blindtigergames.werescrewed.util.Util;

/**
 * 
 * @author Jenn Meant to draw moving saws. UNFINISHED!!
 * 
 */
public class Enemy extends Hazard {

	protected Vector< Tile > tiles = new Vector< Tile >( );
	protected Vector2 bodypos;
	protected float tileConstant = 16.0f;

	public Enemy( String name, Vector2 pos, float radius, World world,
			boolean isActive ) {
		super( name, pos, null, world, isActive );
		entityType = EntityType.HAZARD;
		hazardType = HazardType.ENEMY;

		this.world = world;
		this.active = isActive;

		constructBody( pos, radius );
		
		Sprite s = WereScrewedGame.manager.getAtlas( "common-textures" ).createSprite( "power_screw" );
		float scale = radius*2 / s.getWidth( );
		s.setScale( scale );
		
		
		addBGDecal( s, new Vector2(-radius,-radius) );
		
		addFrontParticleEffect( "/fire/fire_tiny", false, true ).updateAngleWithParent=false;
	}


	public void constructBody( Vector2 position, float radius ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.DynamicBody;

		bodyDef.position.set( ( position.x + ( width * tileConstant ) / 2 )
				* Util.PIXEL_TO_BOX,
				( position.y + ( radius * tileConstant ) / 2 )
						* Util.PIXEL_TO_BOX );
		body = world.createBody( bodyDef );

		CircleShape circle = new CircleShape();
		circle.setRadius( radius*Util.PIXEL_TO_BOX );
		FixtureDef spikeFixtureDef = new FixtureDef( );
		spikeFixtureDef.density = 2f;
		spikeFixtureDef.shape = circle;
		spikeFixtureDef.filter.categoryBits =  Util.CATEGORY_EVERYTHING;
		spikeFixtureDef.filter.maskBits = Util.CATEGORY_EVERYTHING;
		body.createFixture( spikeFixtureDef );
		
		body.setGravityScale( 0.1f );

		circle.dispose( );

		body.setUserData( this );

		bodypos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
	}

	/**
	 * Set speed to zero and prepare enemy for repsawn at particle emitter.
	 * @author stew
	 */
	@Override
	public void reset(){
		super.reset();
		if (this.body != null){
			this.body.setAngularVelocity( 0 );
			this.body.setLinearVelocity( 0, 0 );
		}
		this.addFrontParticleEffect( "smoke_puff_small", true, false ).restartAt( getPositionPixel( ) );
	}
}