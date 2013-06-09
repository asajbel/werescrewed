package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

public class AnalogRotateMover implements IMover {

	private PuzzleType puzzleType;
	private boolean clockWise = false;
	private float speedSteps = 0;
	private float speed;
	private float lastVal = 0;
	private Body transformBody;

	/**
	 * 
	 * @param startAngle
	 * @param endAngle
	 * @param offValue
	 * @param onValue
	 */
	public AnalogRotateMover( float speed, World world ) {
		this.speed = speed;
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.fixedRotation = false;
		transformBody = world.createBody( bodyDef );
		puzzleType = PuzzleType.PUZZLE_SCREW_CONTROL;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		if ( speedSteps > 0 ) {
			speedSteps--;
			if ( clockWise ) {
				transformBody.setAngularVelocity( -speed );
			} else {
				transformBody.setAngularVelocity( speed );
			}
		} else {
			transformBody.setAngularVelocity( 0 );
			if ( body.getUserData( ) instanceof Platform ) {
				Platform entity;
				entity = ( Platform ) body.getUserData( );
				transformBody.setTransform( body.getPosition( ),
						entity.getLocalRot( ) );
			}
		}
		if ( body.getUserData( ) instanceof Platform ) {
			Platform entity;
			entity = ( Platform ) body.getUserData( );
			entity.setLocalRot( transformBody.getAngle( ) );
		}
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		if ( p.currentMover( ) == null
				|| p.currentMover( ).getMoverType( ) != puzzleType ) {
			p.setMoverAtCurrentState( this );
			transformBody.setTransform( p.getPosition( ), p.getLocalRot( ) );
		}
		if ( screw.getDepth( ) > lastVal ) {
			clockWise = true;
		} else {
			clockWise = false;
		}
		lastVal = screw.getDepth( );
		speedSteps = 10;
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
