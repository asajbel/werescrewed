package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.util.Util;

/**
 * screws that are used to hold removable pieces together they can be un-screwed
 * and will fall
 * 
 * @author Dennis
 * 
 */

public class StructureScrew extends Screw {
	private int fallTimeout;
	private boolean lerpUp = true;
	private float alpha = 0.0f;
	private Entity screwInterface;
	private SimpleFrameAnimator screwUIAnimator;
	private int startFrame = 15;
	private int lastMotionFrame = 14;

	public StructureScrew( String name, Vector2 pos, int max, Entity entity,
			World world, Vector2 detachDirection ) {
		super( name, pos, null );
		this.world = world;
		this.detachDirection = detachDirection;
		this.entity = entity;
		if ( entity != null ) {
			this.entityAngle = entity.getAngle( ) * Util.RAD_TO_DEG;
		}
		if ( detachDirection != null
				&& Math.abs( detachDirection.y ) > Math.abs( detachDirection.x ) ) {
			upDownDetach = true;
		} else {
			upDownDetach = false;
		}
		maxDepth = max;
		depth = max;
		rotation = 0;
		fallTimeout = 140;
		extraJoints = new ArrayList< Joint >( );
		screwType = ScrewType.SCREW_STRUCTURAL;
		entityType = EntityType.SCREW;
		entity.body.setFixedRotation( false );
		screwInterface = new Entity( name + "_screwInterface", pos, null, null,
				false );
		TextureAtlas atlas = WereScrewedGame.manager
				.getTextureAtlas( "screwInterface" );
		screwUIAnimator = new SimpleFrameAnimator( ).speed( 1f )
				.loop( LoopBehavior.STOP ).time( 0.0f ).startFrame( 0 )
				.maxFrames( 35 );
		Sprite spr = new Sprite( atlas, screwUIAnimator );
		spr.setOrigin( spr.getWidth( ) / 2.0f, spr.getHeight( ) / 2.0f );
		screwInterface.changeSprite( spr );
		constuctBody( pos );
		if ( sprite != null )
			sprite.rotate( ( float ) ( Math.random( ) * 360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )
				* Util.DEG_TO_RAD );
		rotation = ( int ) ( body.getAngle( ) * Util.RAD_TO_DEG );
		addStructureJoint( entity );
	}

	public StructureScrew( String name, Vector2 pos, int max, World world,
			Vector2 detachDirection ) {
		super( name, pos, null );
		this.world = world;
		this.detachDirection = detachDirection;
		if ( detachDirection != null
				&& Math.abs( detachDirection.y ) > Math.abs( detachDirection.x ) ) {
			upDownDetach = true;
		} else {
			upDownDetach = false;
		}
		maxDepth = max;
		depth = max;
		rotation = 0;
		fallTimeout = 140;
		extraJoints = new ArrayList< Joint >( );
		screwType = ScrewType.SCREW_STRUCTURAL;
		entityType = EntityType.SCREW;
		screwInterface = new Entity( name + "_screwInterface", pos, null, null,
				false );
		TextureAtlas atlas = WereScrewedGame.manager
				.getTextureAtlas( "screwInterface" );
		screwUIAnimator = new SimpleFrameAnimator( ).speed( 1f )
				.loop( LoopBehavior.STOP ).time( 0.0f ).startFrame( 0 )
				.maxFrames( 35 );
		Sprite spr = new Sprite( atlas, screwUIAnimator );
		spr.setOrigin( spr.getWidth( ) / 2.0f, spr.getHeight( ) / 2.0f );
		screwInterface.changeSprite( spr );
		constuctBody( pos );
		if ( sprite != null )
			sprite.rotate( ( float ) ( Math.random( ) * 360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )
				* Util.DEG_TO_RAD );
		rotation = ( int ) ( body.getAngle( ) * Util.RAD_TO_DEG );
	}

	@Override
	public void screwRight( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( depth < maxDepth && depth > 0 ) {
			diff = startRegion - region;
			newDiff = diff - prevDiff;
			if ( newDiff < -10 ) {
				newDiff = 0;
			}
			prevDiff = diff;

			body.setAngularVelocity( -1 );
			depth += newDiff;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 6;
		}

	}

	@Override
	public void screwLeft( ) {
		if ( depth > -10 ) {
			body.setAngularVelocity( 1 );
			depth -= 2;
			rotation += 10;
			screwStep = depth + 5;
		}
	}

	@Override
	public void screwLeft( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( depth > 0 ) {
			diff = startRegion - region;
			newDiff = diff - prevDiff;
			if ( newDiff > 10 ) {
				newDiff = 0;
			}
			prevDiff = diff;

			body.setAngularVelocity( 1 );
			depth += newDiff;
			spriteRegion += region;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 5;
		}

	}

	@Override
	public void screwRight( ) {
		if ( depth < maxDepth && depth > 0 ) {
			body.setAngularVelocity( -1 );
			depth += 2;
			rotation -= 10;
			screwStep = depth + 6;
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( !removed ) {
			if ( entity != null
					&& ( getDetachDirection( ).x != 0 || getDetachDirection( ).y != 0 ) ) {
				if ( upDownDetach ) {
					detachDirection.x = ( float ) Math.sin( entity.getAngle( ) );
					detachDirection.y = Math.signum( detachDirection.y )
							* ( float ) Math.cos( entity.getAngle( ) );
				} else {
					detachDirection.x = Math.signum( detachDirection.x )
							* ( float ) Math.cos( entity.getAngle( ) );
					detachDirection.y = ( float ) Math.sin( entity.getAngle( ) );
				}
			}
			Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );
			sprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
			if ( depth <= 0 ) {
				if ( fallTimeout == 0 ) {
					for ( Joint j : extraJoints ) {
						world.destroyJoint( j );
					}
				}
				fallTimeout--;
			} else {
				fallTimeout = 70;
			}
			if ( depth > 0 ) {
				// sprite.setPosition(
				// sprite.getX( )
				// + ( .25f * ( float ) ( ( maxDepth - depth ) * ( Math
				// .cos( body.getAngle( ) ) ) ) ),
				// sprite.getY( )
				// + ( .25f * ( float ) ( ( maxDepth - depth ) * ( Math
				// .sin( body.getAngle( ) ) ) ) ) );
			} else if ( fallTimeout > 0 ) {
				sprite.setPosition( sprite.getX( ) - 8f, sprite.getY( ) );
				Vector2 spritePos = new Vector2( sprite.getX( ), sprite.getY( ) );
				Vector2 target1 = new Vector2( sprite.getX( ) + 8f,
						sprite.getY( ) );
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
			if ( playerAttached ) {
				if ( screwInterface.sprite.getAnimator( ).getFrame( ) == 0 ) {
					screwUIAnimator.speed( 1 );
				} else if ( screwInterface.sprite.getAnimator( ).getFrame( ) > lastMotionFrame ) {
					screwUIAnimator.speed( 0 );
					if ( depth >= 0 ) {
						int value = ( int ) ( ( ( float ) depth / ( float ) maxDepth ) * 10f )
								+ startFrame;
						screwUIAnimator.setFrame( value );
					}
				}
			} else {
				if ( screwInterface.sprite.getAnimator( ).getFrame( ) > lastMotionFrame ) {
					screwUIAnimator.setFrame( lastMotionFrame );
				}
				screwUIAnimator.speed( -1 );
			}
			screwInterface.sprite.setPosition( this.getPositionPixel( ).sub(
					interfaceOffset ) );
			screwInterface.sprite.update( deltaTime );
			screwUIAnimator.update( deltaTime );
		}
	}

	@Override
	public void draw( SpriteBatch batch, float deltaTime ) {
		drawBGDecals( batch );
		screwInterface.sprite.draw( batch );
		drawParticles( behindParticles, batch );
		if ( sprite != null && visible && !removeNextStep ) {
			sprite.draw( batch );
		}
		// drawOrigin(batch);
		drawFGDecals( batch );
		if ( spinemator != null )
			spinemator.draw( batch );
		drawParticles( frontParticles, batch );
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

		// we may want a radar depending on the size of the sprite...
		// add radar sensor to screw
		// CircleShape radarShape = new CircleShape( );
		// radarShape.setRadius( sprite.getWidth( ) * 1.1f * Util.PIXEL_TO_BOX
		// );
		// FixtureDef radarFixture = new FixtureDef( );
		// radarFixture.shape = radarShape;
		// radarFixture.isSensor = true;
		// radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
		// | Util.CATEGORY_SUBPLAYER;
		// body.createFixture( radarFixture );
		// radarShape.dispose( );
	}
}
