package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

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
import com.blindtigergames.werescrewed.puzzles.PuzzleManager;
import com.blindtigergames.werescrewed.util.Util;

/**
 * screws used to move platforms with different types of control look into mover
 * types for various different possibilities
 * 
 * @author Dennis
 * 
 */

public class PuzzleScrew extends Screw {
	public PuzzleManager puzzleManager;
	private boolean resetAble;
	private Entity screwInterface;
	private SimpleFrameAnimator screwUIAnimator;
	private int startFrame = 15;
	private int lastMotionFrame = 14;

	public PuzzleScrew( String name, Vector2 pos, int max, Entity entity,
			World world, int startDepth, boolean resetable,
			Vector2 detachDirection ) {
		super( name, pos, null );
		this.world = world;
		this.detachDirection = detachDirection;
		this.entity = entity;
		if ( entity != null ) {
			this.entityAngle = entity.getAngle( ) * Util.RAD_TO_DEG;
		}
		if ( Math.abs( detachDirection.y ) > Math.abs( detachDirection.x ) ) {
			upDownDetach = true;
		} else {
			upDownDetach = false;
		}
		maxDepth = max;
		this.startDepth = depth = startDepth;
		if ( startDepth == max ) {
			startFrame = 25;
		}
		resetAble = resetable;
		puzzleManager = new PuzzleManager( this.name );
		screwType = ScrewType.SCREW_PUZZLE;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< Joint >( );
		screwInterface = new Entity( name + "_screwInterface", pos, null, null,
				false );
		TextureAtlas atlas = WereScrewedGame.manager.getTextureAtlas( "screwInterface" );
		screwUIAnimator = new SimpleFrameAnimator( ).speed(0f )
				.loop( LoopBehavior.STOP ).time( 0.0f ).startFrame( 0 )
				.maxFrames( 35 );
		Sprite spr = new Sprite( atlas, screwUIAnimator );
		spr.setOrigin( spr.getWidth( )/2.0f, spr.getHeight( )/2.0f );
		screwInterface.changeSprite( spr );
		sprite.setColor( 16f / 255f, 215f / 255f, 96f / 255f, 1.0f );
		constructBody( pos );
		if ( sprite != null )
			sprite.rotate( ( float ) ( Math.random( ) * 360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )
				* Util.DEG_TO_RAD );
		rotation = (int) ( body.getAngle( ) * Util.RAD_TO_DEG );
		addStructureJoint( entity );
	}

	public PuzzleScrew( String name, Vector2 pos, int max, World world,
			int startDepth, boolean resetable ) {
		super( name, pos, null );
		this.world = world;
		maxDepth = max;
		this.startDepth = depth = startDepth;
		if ( startDepth == max ) {
			startFrame = 25;
		}
		resetAble = resetable;
		puzzleManager = new PuzzleManager( this.name );
		screwType = ScrewType.SCREW_PUZZLE;
		entityType = EntityType.SCREW;
		extraJoints = new ArrayList< Joint >( );
		sprite.setColor( 16f / 255f, 215f / 255f, 96f / 255f, 1.0f );
		screwInterface = new Entity( name + "_screwInterface", pos, null, null,
				false );
		TextureAtlas atlas = WereScrewedGame.manager.getTextureAtlas( "screwInterface" );
		screwUIAnimator = new SimpleFrameAnimator( ).speed(1f )
				.loop( LoopBehavior.STOP ).time( 0.0f ).startFrame( 0 )
				.maxFrames( 35 );
		Sprite spr = new Sprite( atlas, screwUIAnimator );
		spr.setOrigin( spr.getWidth( )/2.0f, spr.getHeight( )/2.0f );
		screwInterface.changeSprite( spr );
		constructBody( pos );
		if ( sprite != null )
			sprite.rotate( ( float ) ( Math.random( ) * 360 ) );
		body.setTransform( body.getPosition( ), sprite.getRotation( )
				* Util.DEG_TO_RAD );
		rotation = (int) ( body.getAngle( ) * Util.RAD_TO_DEG );
	}

	/**
	 * screwing left calls the puzzle manager element and applies the screw
	 * value to whatever movement is required
	 */
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
			if(depth < 0) depth = 0;
			spriteRegion += region;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 5;
			puzzleManager.runElement( this, ( float ) depth
					/ ( ( float ) maxDepth ) );
		}

	}

	@Override
	public void screwLeft( ) {
		if ( depth > 0 ) {
			body.setAngularVelocity( 1 );
			depth -= 2;
			rotation += 10;
			screwStep = depth + 5;
			puzzleManager.runElement( this, ( float ) depth
					/ ( ( float ) maxDepth ) );
		}
	}

	/**
	 * screwing right calls the puzzle manager element and applies the screw
	 * value to whatever movement is required
	 */
	@Override
	public void screwRight( int region, boolean switchedDirections ) {
		if ( switchedDirections ) {
			startRegion = region;
			prevDiff = 0;
		}

		if ( depth < maxDepth ) {
			diff = startRegion - region;
			newDiff = diff - prevDiff;
			if ( newDiff < -10 ) {
				newDiff = 0;
			}
			prevDiff = diff;

			body.setAngularVelocity( -1 );
			depth += newDiff;
			if(depth > maxDepth) depth = maxDepth;
			if ( diff != 0 ) {
				rotation += ( -newDiff * 5 );
			}
			screwStep = depth + 6;
			puzzleManager.runElement( this, ( float ) depth
					/ ( ( float ) maxDepth ) );
		}

	}

	@Override
	public void screwRight( ) {
		if ( depth < maxDepth ) {
			body.setAngularVelocity( -1 );
			depth += 2;
			rotation -= 10;
			screwStep = depth + 6;
			puzzleManager.runElement( this, ( float ) depth
					/ ( ( float ) maxDepth ) );
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
					detachDirection.x = Math.signum( detachDirection.y )
							* ( float ) Math.cos( entity.getAngle( ) );
					detachDirection.y = ( float ) Math.sin( entity.getAngle( ) );
				}
			}
			puzzleManager.update( deltaTime );
			sprite.setRotation( rotation );
			if ( depth != screwStep ) {
				screwStep--;
			}
			if ( depth == screwStep ) {
				body.setAngularVelocity( 0 );
			}
			if ( playerAttached ) {
				if ( screwInterface.sprite.getAnimator( ).getFrame( ) == 0 ) 
				{
					screwUIAnimator.speed( 1 );
				} else if ( screwInterface.sprite.getAnimator( ).getFrame( ) > lastMotionFrame ){
					screwUIAnimator.speed( 0 );
					int value = (int ) ( ( (float) depth / (float)maxDepth ) * 10f ) + startFrame;
					screwUIAnimator.setFrame( value );
				}
			} else {
				if ( screwInterface.sprite.getAnimator( ).getFrame( ) > lastMotionFrame ){
					screwUIAnimator.setFrame( lastMotionFrame );
				}
				screwUIAnimator.speed( -1 );
			}
			screwInterface.sprite.setPosition( this.getPositionPixel( ).sub( interfaceOffset ) );
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
	
	/**
	 * resets this screw back to its initial position
	 * 
	 * @param pos
	 */
	public void resetScrew( ) {
		if ( resetAble ) {
			depth = startDepth;
		}
	}
	
	/**
	 * fixes puzzle mechanics when this screw is being used in a puzzle with
	 * multiple puzzle screws
	 * 
	 * @param otherScrew
	 */
	public void fixConcurrentScrew( Screw otherScrew ) {
		depth = otherScrew.getDepth( );
	}

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
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		body.setUserData( this );

		// add radar sensor to screw
		// CircleShape radarShape = new CircleShape( );
		// radarShape.setRadius( sprite.getWidth( ) * 1.25f * Util.PIXEL_TO_BOX
		// );
		// FixtureDef radarFixture = new FixtureDef( );
		// radarFixture.shape = radarShape;
		// radarFixture.isSensor = true;
		// radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
		// | Util.CATEGORY_SUBPLAYER;
		// body.createFixture( radarFixture );

		// You dont dispose the fixturedef, you dispose the shape
		// radarShape.dispose( );
		screwShape.dispose( );
	}

}
