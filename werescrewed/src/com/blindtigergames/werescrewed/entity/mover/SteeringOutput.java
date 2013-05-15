package com.blindtigergames.werescrewed.entity.mover;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Simple 2d kinematic movement storage class.
 * 
 * @param velocity
 *            - Vector2 storage for velocity / linear impulse
 * @param rotation
 *            - float storage for angular velocity / angular impulse
 * 
 * @author stew
 * 
 */
public class SteeringOutput {

	public Vector2 velocity; // linear velocity
	public float rotation; // angular velocity

	public SteeringOutput( Vector2 _velocity, float _rotation ) {
		this.velocity = _velocity;
		this.rotation = _rotation;
	}

	public SteeringOutput add( SteeringOutput that ) {
		velocity.add( that.velocity );
		rotation += that.rotation;
		return this;
	}

	public SteeringOutput add( ArrayList< SteeringOutput > them ) {
		for ( SteeringOutput s : them ) {
			velocity.add( s.velocity );
			rotation += s.rotation;
		}
		return this;
	}

	public void applySteering( Body body ) {
		// For Kinematic & Static bodies, we set the velocity
		BodyType type = body.getType( );
		if ( type == BodyType.KinematicBody || type == BodyType.StaticBody ) {
			body.setLinearVelocity( velocity );
			body.setAngularVelocity( rotation );
		} else {
			// body is dynamic, so we apply an impulse
			body.applyLinearImpulse( velocity, body.getWorldCenter( ) );
			body.applyAngularImpulse( rotation );
		}
	}
}
