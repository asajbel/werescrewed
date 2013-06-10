package com.blindtigergames.werescrewed.joint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.util.Util;

public class RevoluteJointBuilder {

	/**
	 * Required parameters
	 */
	World world;
	Entity entityA;
	Entity entityB;
	Vector2 anchor;

	// Default parameter values
	boolean enableLimit;
	float lowerAngle;
	float upperAngle;
	boolean enableMotor;
	float maxMotorTorque;
	float motorSpeed;
	boolean collideConnected;

	/**
	 * empty constructor is private to force passing in world when building this
	 * joint
	 */
//	private RevoluteJointBuilder( ) {
//
//	};

	/**
	 * These are the required parameters for a prismatic joint
	 */
	public RevoluteJointBuilder( World _world ) {
		// TODO Auto-generated constructor stub
		this.world = _world;
		reset( );
	}

	public RevoluteJointBuilder reset( ) {
		enableLimit = false;
		lowerAngle = 0.0f;
		upperAngle = 90 * Util.DEG_TO_RAD;
		enableMotor = false;
		maxMotorTorque = 500;// high max motor force yields a very strong motor
		motorSpeed = 1; // 1 is relatively slow
		collideConnected = true;
		return this;
	}

	/**
	 * Creates revolute joint, adds it to world
	 * 
	 * @return
	 */
	public RevoluteJoint build( ) {
		if ( entityB == null || entityA == null ) {
			Gdx.app.error( "RevoluteJointBuilder",
					"You didn't initialize both of the entities, you doofus!" );
		}
		if ( anchor == null ) {
			anchor = entityB.body.getWorldCenter( );
		}
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( entityA.body, entityB.body, anchor );
		revoluteJointDef.enableLimit = enableLimit;
		revoluteJointDef.lowerAngle = lowerAngle;
		revoluteJointDef.upperAngle = upperAngle;
		revoluteJointDef.collideConnected = collideConnected;
		revoluteJointDef.enableMotor = enableMotor;
		revoluteJointDef.maxMotorTorque = maxMotorTorque;// high max motor force
															// yields a

		// very strong motor
		revoluteJointDef.motorSpeed = motorSpeed;

		RevoluteJoint joint = ( RevoluteJoint ) world
				.createJoint( revoluteJointDef );

		return joint;
	}

	/**
	 * First body to attact the joint to
	 */
	public RevoluteJointBuilder entityA( Entity bodyA ) {
		this.entityA = bodyA;
		return this;
	}

	/**
	 * bodyB is required to properly build this joint
	 */
	public RevoluteJointBuilder entityB( Entity _bodyB ) {
		this.entityB = _bodyB;
		return this;
	}

	/**
	 * Optional, default anchor is the center of entityB.
	 * 
	 * @param _anchor
	 */
	public RevoluteJointBuilder anchor( Vector2 _anchor ) {
		this.anchor = _anchor;
		return this;
	}

	/**
	 * Optional, default is no limit
	 */
	public RevoluteJointBuilder limit( boolean hasLimit ) {
		this.enableLimit = hasLimit;
		return this;
	}

	/**
	 * Optional, default is the platforms initial position
	 * 
	 * @param limit
	 */
	public RevoluteJointBuilder lower( float angle ) {
		this.lowerAngle = angle;
		return this;
	}

	/**
	 * Optional, default is 1
	 * 
	 * @param limit
	 */
	public RevoluteJointBuilder upper( float angle ) {
		this.upperAngle = angle;
		return this;
	}

	/**
	 * Optional, default is false!
	 * 
	 * @param hasMotor
	 */
	public RevoluteJointBuilder motor( boolean hasMotor ) {
		this.enableMotor = hasMotor;
		return this;
	}

	/**
	 * Optional, default is
	 * 
	 * @param _maxMotorForce
	 */
	public RevoluteJointBuilder maxTorque( float _torque ) {
		this.maxMotorTorque = _torque;
		return this;
	}

	public RevoluteJointBuilder motorSpeed( float _motorSpeed ) {
		this.motorSpeed = _motorSpeed;
		return this;
	}

	public RevoluteJointBuilder collideConnected( boolean collideConnected ) {
		this.collideConnected = collideConnected;
		return this;
	}
}
