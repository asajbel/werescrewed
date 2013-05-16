package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;

/**
 * SteeringBuilder allows you to easily construct a SteeringOuput in a way such
 * as: SteeringOutput output = new
 * SteeringBuilder().vel(0.1,5.0).rot(0.5).build();
 * 
 * @author stew
 * 
 */

public class SteeringBuilder {

	private Vector2 _velocity;
	private float _rotation = 0;

	public SteeringBuilder( ) {
		_velocity = new Vector2( );
	}

	public SteeringOutput build( ) {
		return new SteeringOutput( _velocity, _rotation );
	}

	public SteeringBuilder vel( Vector2 _velocity ) {
		this._velocity.x = _velocity.x;
		this._velocity.y = _velocity.y;
		return this;
	}

	public SteeringBuilder vel( float _velocityX, float _velocityY ) {
		this._velocity.x = _velocityX;
		this._velocity.y = _velocityY;
		return this;
	}

	public SteeringBuilder rot( float _rotation ) {
		this._rotation = _rotation;
		return this;
	}
}
