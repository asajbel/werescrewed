package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.util.Util;

/**
 * screws that do not un-screw 
 * they are only used for climbing or
 * tacking entities together
 * 
 * @author Dennis
 * 
 */

public class StrippedScrew extends Screw {

	/**
	 *  create a stripped screw thats attached to an entity
	 * @param name
	 * @param pos
	 * @param entity
	 * @param world
	 */
	public StrippedScrew( String name, Vector2 pos, Entity entity, World world, Vector2 detachDirection ) {
		super( name, pos, null );
		this.world = world;
		this.detachDirection = detachDirection;
		this.entity = entity;
		if ( entity != null ) {
			this.entityAngle = entity.getAngle( )*Util.RAD_TO_DEG;
		}
		if ( detachDirection != null && Math.abs( detachDirection.y ) > Math.abs( detachDirection.x ) ) {
			upDownDetach = true;
		} else {
			upDownDetach = false;
		}
		screwType = ScrewType.SCREW_STRIPPED;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< Joint >( );

		sprite.setColor( 255f/255f, 112f/255f, 52f/255f, 1.0f ); //rust color pulled off a hexdecimal chart
		sprite.setOrigin( 0.0f, 0.0f );

		constructBody( pos );
		if(sprite!=null)sprite.rotate( ( float ) ( Math.random( )*360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )*Util.DEG_TO_RAD );
		addStructureJoint( entity );

	}
	
	/**
	 * create a stripped screw that isn't connected to an entity
	 * @param name
	 * @param pos
	 * @param world
	 */
	public StrippedScrew( String name, Vector2 pos, World world) {
		super( name, pos, null );
		this.world = world;
		screwType = ScrewType.SCREW_STRIPPED;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< Joint >( );

		sprite.setColor( 255f/255f, 112f/255f, 52f/255f, 1.0f ); //rust color pulled off a hexdecimal chart
		sprite.setOrigin( 0.0f, 0.0f );

		constructBody( pos );
		if(sprite!=null)sprite.rotate( ( float ) ( Math.random( )*360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )*Util.DEG_TO_RAD );
	}

	/**
	 * create screw body
	 * @param pos
	 */
	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.gravityScale = 0.07f;
		screwBodyDef.fixedRotation = false;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape
				.setRadius( ( sprite.getWidth( ) / 2.0f ) * Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.density = 4f;
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		screwFixture.isSensor = true;
		screwFixture.shape = screwShape;
		body.createFixture( screwFixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		//we may want a radar depending on the size of the sprite...
		// add radar sensor to screw this is needed/not needed depending on the size of the screw
//		CircleShape radarShape = new CircleShape( );
//		radarShape.setRadius( sprite.getWidth( ) * 1.05f * Util.PIXEL_TO_BOX );
//		FixtureDef radarFixture = new FixtureDef( );
//		radarFixture.shape = radarShape;
//		radarFixture.isSensor = true;
//		radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
//		radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
//				| Util.CATEGORY_SUBPLAYER;
//		body.createFixture( radarFixture );
//		radarShape.dispose( );
		screwShape.dispose( );

	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( entity != null && ( getDetachDirection( ).x != 0 || getDetachDirection( ).y != 0 ) ) {
			if ( upDownDetach ) {
				detachDirection.x = (float)Math.sin( entity.getAngle( ) );
				detachDirection.y = Math.signum( detachDirection.y ) * (float)Math.cos( entity.getAngle( ) );
			} else {
				detachDirection.x = Math.signum( detachDirection.y ) * (float)Math.cos( entity.getAngle( ) );
				detachDirection.y = (float)Math.sin( entity.getAngle( ) );					
			}
		}
	}
	
	/**
	 * This particular draw is needed because I needed stripped screws to be able to 
	 * rotate in place, I am refering to the whole body rotating, not just screwing/unscrewing
	 * 
	 * This will probably be put into Screw when need be
	 * 
	 * @author Ranveer
	 */
	@Override 
	public void draw(SpriteBatch batch, float deltaTime){
		float xpos =  body.getPosition( ).x - (this.sprite.getWidth( )/2 * Util.PIXEL_TO_BOX);
		float ypos =  body.getPosition( ).y - (this.sprite.getHeight( )/2 * Util.PIXEL_TO_BOX);
		
		this.sprite.setOrigin( this.sprite.getWidth( ) / 2, this.sprite.getHeight( ) / 2);
		this.sprite.setPosition( xpos * Util.BOX_TO_PIXEL, ypos * Util.BOX_TO_PIXEL);
		this.sprite.setRotation(  MathUtils.radiansToDegrees
				* body.getAngle( ) );
		
		this.sprite.draw( batch );
	}
	

}
