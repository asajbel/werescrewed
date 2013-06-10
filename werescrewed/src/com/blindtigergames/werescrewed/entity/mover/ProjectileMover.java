package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

public class ProjectileMover implements IMover {

//	@SuppressWarnings( "unused" )
//	private PuzzleType puzzleType;
//	@SuppressWarnings( "unused" )
//	private float spawn;
	private float onVal;
	private Body[ ] elementList;
	private int elementIndex = 0;

	private int numElements;
	private Vector2 origin;

	/**
	 * 
	 * @param on
	 * @param spawnTime
	 * @param elementNumber
	 * @param spawnOrigin
	 */

	public ProjectileMover( int on, float spawnTime, int elementNumber,
			Vector2 spawnOrigin ) {
		//puzzleType = PuzzleType.ON_OFF_MOVER;
		origin = spawnOrigin;
		//spawn = spawnTime;
		numElements = elementNumber;
		elementList = new Body[ numElements ];
		if ( on <= 1 && on >= 0 ) {
			onVal = on;
		} else {
			throw new IllegalArgumentException(
					"onValue has to be in the range 0-1" );
		}
	}

	@Override
	public void move( float deltaTime, Body body ) {

	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		if ( screwVal <= onVal ) {
			if ( elementList[ elementIndex ] == null ) {
				//@SuppressWarnings( "unused" )
				//Body body;
				BodyDef bodyDef = new BodyDef( );
				bodyDef.position.set( origin );
				bodyDef.type = BodyType.DynamicBody;
				bodyDef.gravityScale = 0.1f;
				PolygonShape polygonShape = new PolygonShape( );
				polygonShape.setRadius( .1f );
				FixtureDef fixtureDef = new FixtureDef( );
				fixtureDef.shape = polygonShape;
				fixtureDef.density = 10.0f;
				fixtureDef.restitution = 0.0f;
				fixtureDef.friction = 0.1f;
				/*
				 * body = Util.world.createBody( bodyDef ); body.createFixture(
				 * fixtureDef ); elementList[elementIndex] = body;
				 */
			} else {
				elementList[ elementIndex ].setTransform( origin, 0f );
			}
			elementIndex++;
			elementIndex = elementIndex % numElements;
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
}
