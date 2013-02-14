package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.puzzles.PuzzleManager;
import com.blindtigergames.werescrewed.util.Util;

/**
 * blah blah
 * 
 * @author Dennis
 * 
 */

public class PuzzleScrew extends Screw {
	public PuzzleManager puzzleManager;
	private int threshold;

	public PuzzleScrew( String name, Vector2 pos, int max, Entity entity,
			World world ) {
		super( name, pos, null );
		this.world = world;
		maxDepth = threshold = depth = max;
		puzzleManager = new PuzzleManager( world );
		screwType = ScrewType.PUZZLE;
		
		sprite.setColor( Color.GREEN );

		constructBody( pos );
		connectScrewToEntity( entity );
	}

	/**
	 * creates Puzzle Screw with binary
	 * 
	 * @param max
	 *            screwable amount
	 * @param th
	 *            threshold for binary action
	 */
	public PuzzleScrew( String name, Vector2 pos, int max, Entity entity,
			World world, int th ) {
		this( name, pos, max, entity, world );
		threshold = th;
	}

	@Override
	public void screwLeft( ) {
		if ( depth > 0 ) {
			body.setAngularVelocity( 15 );
			depth--;
			rotation += 10;
			screwStep = depth + 5;
			puzzleManager
					.runElement( 1f - ( ( float ) depth / ( ( float ) maxDepth ) ) );
		}
	}

	@Override
	public void screwRight( ) {
		if ( depth < maxDepth ) {
			body.setAngularVelocity( -15 );
			depth++;
			rotation -= 10;
			screwStep = depth + 6;
			puzzleManager
					.runElement( 1f - ( ( float ) depth / ( ( float ) maxDepth ) ) );
			Gdx.app.log( name + " depth", "" + depth );
		}
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		puzzleManager.update( deltaTime );
		sprite.setRotation( rotation );
		if ( depth != screwStep ) {
			screwStep--;
		}
		if ( depth == screwStep ) {
			body.setAngularVelocity( 0 );
		}
	}

	/**
	 * public access to get max depth of a screw
	 * 
	 * @return value of maxDepth
	 */
	public int getMaxDepth( ) {
		return maxDepth;
	}

	/**
	 * checks if binary puzzle screw is active
	 * 
	 * @return if screwed past threshold
	 */

	public boolean isActive( ) {
		return threshold <= depth;
	}

	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos );
		screwBodyDef.gravityScale = 0.07f;
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
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 1.25f * Util.PIXEL_TO_BOX );
		FixtureDef radarFixture = new FixtureDef( );
		radarFixture.shape = radarShape;
		radarFixture.isSensor = true;
		radarFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		radarFixture.filter.maskBits = Util.CATEGORY_PLAYER
				| Util.CATEGORY_SUBPLAYER;
		body.createFixture( radarFixture );

		// You dont dispose the fixturedef, you dispose the shape
		radarShape.dispose( );
		screwShape.dispose( );
	}

	private void connectScrewToEntity( Entity entity ) {
		// connect the screw to the entity;
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}
}
