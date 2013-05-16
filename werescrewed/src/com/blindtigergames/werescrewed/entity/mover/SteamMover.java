package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;

public class SteamMover implements IMover {

	float rangeWidth;
	float rangeHeight;
	Vector2 localPosition;
	int emissionDirection;
	private boolean done = false;

	public SteamMover( float height, float width, int direction ) {
		rangeHeight = height;
		rangeWidth = width;
		emissionDirection = direction;
		localPosition = new Vector2( 0f, 0f );
	}

	@Override
	public void move( float deltaTime, Body body ) {
		body.applyLinearImpulse( new Vector2( 0f, 1f ), body.getWorldCenter( ) );

	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		// TODO Auto-generated method stub

	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return true when particle is out of range
	 */
	public boolean atEnd( ) {
		return done;
	}

}