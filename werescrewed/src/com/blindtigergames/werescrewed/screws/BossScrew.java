package com.blindtigergames.werescrewed.screws;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class BossScrew extends Screw {

	/**
	 * 
	 * @param name
	 * @param pos
	 * @param max
	 * @param entity
	 * @param world
	 */
	public BossScrew( String name, Vector2 pos, int max, Entity entity, World world ) {
		super( name, pos, null );
		this.world = world;
		maxDepth = max;
		depth = max;
		rotation = 0;
		fallTimeout = 140;
		extraJoints = new ArrayList< Joint >( );
		screwType = ScrewType.SCREW_BOSS;
		entityType = EntityType.SCREW;

		sprite.setColor( 244f/255f, 215f/255f, 7f/255f, 1.0f);
		
		constuctBody( pos );
		addStructureJoint( entity );
	}


	@Override
	public void screwLeft( int region, boolean switchedDirections ) {
		if ( playerCount == 1 ) {
			if ( depth > 0 ) {
				body.setAngularVelocity( 15 );
				depth--;
				rotation = region * 5;
				screwStep = depth + 5;
			}
		} else {
			playerCount++;
		}
	}
	
	
	@Override
	public void screwLeft( ) {
		if ( playerCount == 1 ) {
			if ( depth > 0 ) {
				body.setAngularVelocity( 15 );
				depth--;
				rotation += 10;
				screwStep = depth + 5;
			}
		} else {
			playerCount++;
		}
	}

	@Override
	public void screwRight( ) {
		if ( playerCount == 1 ) {
			if ( depth < maxDepth ) {
				body.setAngularVelocity( -1 );
				depth++;
				rotation -= 10;
				screwStep = depth + 6;
			}
		} else {
			playerCount++;
		}
	}

	@Override
	public void screwRight( int region, boolean switchedDirections ) {
		if ( playerCount == 1 ) {
			if ( depth < maxDepth ) {
				body.setAngularVelocity( -1 );
				depth++;
				rotation -= 10;
				screwStep = depth + 6;
			}
		} else {
			playerCount++;
		}

	}	

	@Override
	public boolean endLevelFlag( ) {
		return endFlag;
	}
	
	@Override
	public int getDepth( ) {
		return depth;
	}
	
	/**
	 * boss screws allow two players to attach
	 * this function should always return false
	 * @return playerAttached
	 */
	public boolean isPlayerAttached( ) {
		return false;
	}
	
	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( !removed ) {
		Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
		sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
		if ( depth == 0 ) {
			if ( fallTimeout == 0 ) {
				for ( Joint j : extraJoints ) {
					world.destroyJoint( j );
				}
				Gdx.app.log( "Boss Screw Removed", "End Level" );
				endFlag = true;
				// if the number of joints is less than 3 set to dynamic body
				// a joint for the screw and a joint to the skeleton or less
			}
			fallTimeout--;
		} else {
			fallTimeout = 70;
		}
		if ( depth > 0 ) {
//			sprite.setPosition(
//					sprite.getX( )
//							+ ( .25f * ( float ) ( ( maxDepth - depth ) * ( Math
//									.cos( body.getAngle( ) ) ) ) ),
//					sprite.getY( )
//							+ ( .25f * ( float ) ( ( maxDepth - depth ) * ( Math
//									.sin( body.getAngle( ) ) ) ) ) );
		} else if ( fallTimeout > 0 ) {
			sprite.setPosition( sprite.getX( ) - 8f, sprite.getY( ) );
			Vector2 spritePos = new Vector2( sprite.getX( ), sprite.getY( ) );
			Vector2 target1 = new Vector2( sprite.getX( ) + 8f, sprite.getY( ) );
			if ( fallTimeout % ( maxDepth / 5.0f ) == 0 ) {
				if ( lerpUp ) {
					lerpUp = false;
				} else {
					lerpUp = true;
				}
			}
			if ( lerpUp ) {
				alpha += 1f / ( maxDepth / 5.0f );
			} else {
				alpha -= 1f / ( maxDepth / 5.0f );
			}
			spritePos.lerp( target1, alpha );
			sprite.setPosition( spritePos.x, spritePos.y );
		}
		sprite.setRotation( rotation );
		if ( depth != screwStep ) {
			screwStep--;
		}
		if ( depth == screwStep ) {
			body.setAngularVelocity( 0 );
		}
		playerCount = 0;
		}
	}

	@Override
	public void draw( SpriteBatch batch ) {
		if ( sprite != null ) {
			sprite.draw( batch );
		}
	}
	
	private void constuctBody( Vector2 pos ) {
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
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		screwFixture.density = 0.5f;
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_PLAYER
				| Util.CATEGORY_SUBPLAYER;
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );

		//we may want a radar depending on the size of the sprite...
		// add radar sensor to screw
//		CircleShape radarShape = new CircleShape( );
//		radarShape.setRadius( sprite.getWidth( ) * 1.1f * Util.PIXEL_TO_BOX );
//		FixtureDef radarFixture = new FixtureDef( );
//		radarFixture.shape = radarShape;
//		radarFixture.isSensor = true;
//		radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
//		radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
//				| Util.CATEGORY_SUBPLAYER;
//		body.createFixture( radarFixture );
//		radarShape.dispose( );
	}
	
	private int fallTimeout;
	private boolean lerpUp = true;
	private float alpha = 0.0f;
	private boolean endFlag = false;
	private int playerCount = 0;
}
