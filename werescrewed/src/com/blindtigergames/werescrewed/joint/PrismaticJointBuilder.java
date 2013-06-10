package com.blindtigergames.werescrewed.joint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.entity.Entity;

public class PrismaticJointBuilder {

	/**
	 * Required parameters
	 */
	World world;
	Entity bodyA;
	Entity bodyB;
	Vector2 anchor;
	Vector2 axis;

	// Default parameter values
	boolean enableLimit = false;
	float lowerTranslation = 0.0f;
	float upperTranslation = 1.0f;
	boolean enableMotor = false;

	float maxMotorForce = 500;// high max motor force yields a
								// very strong motor
	float motorSpeed = 1; // 1 is reletively slow

	/**
	 * empty constructor is private to force passing in world when building this
	 * joint
	 */
//	private PrismaticJointBuilder( ) {
//	};

	/**
	 * These are the required parameters for a prismatic joint
	 */
	public PrismaticJointBuilder( World _world ) {
		// TODO Auto-generated constructor stub
		this.world = _world;
	}

	/**
	 * Creates prismatic joint, adds it to world
	 * 
	 * @return
	 */
	public PrismaticJoint build( ) {
		if ( bodyB == null || bodyA == null ) {
			Gdx.app.error( "PrismaticJointBuilder",
					"You didn't initialize bodyB and/or skeleton, you doofus!" );
		}
		if ( anchor == null ) {
			anchor = bodyB.body.getWorldCenter( );
		}
		if ( axis == null ) {
			axis = new Vector2( 1, 0 ); // default value if axis isn't specified
		}
		PrismaticJointDef prismaticJointDef = new PrismaticJointDef( );
		prismaticJointDef.initialize( bodyA.body, bodyB.body, anchor, axis );
		prismaticJointDef.enableLimit = enableLimit;
		prismaticJointDef.lowerTranslation = lowerTranslation;
		prismaticJointDef.upperTranslation = upperTranslation;
		prismaticJointDef.enableMotor = enableMotor;
		prismaticJointDef.maxMotorForce = maxMotorForce;// high max motor force
														// yields a
		// very strong motor
		prismaticJointDef.motorSpeed = motorSpeed;

		PrismaticJoint joint = ( PrismaticJoint ) world
				.createJoint( prismaticJointDef );

		return joint;
	}

	/**
	 * FIrst body involved in joint
	 */
	public PrismaticJointBuilder bodyA( Entity bodyA ) {
		this.bodyA = bodyA;
		return this;
	}

	/**
	 * bodyB is required to properly build this joint
	 */
	public PrismaticJointBuilder bodyB( Entity _bodyB ) {
		this.bodyB = _bodyB;
		return this;
	}

	/**
	 * Optional, default anchor is the center of bodyB.
	 * 
	 * @param _anchor
	 */
	public PrismaticJointBuilder anchor( Vector2 _anchor ) {
		this.anchor = _anchor;
		return this;
	}

	/**
	 * Optional, default is no limit
	 */
	public PrismaticJointBuilder limit( boolean hasLimit ) {
		this.enableLimit = hasLimit;
		return this;
	}

	/**
	 * Optional, default is the platforms initial position
	 * 
	 * @param limit
	 */
	public PrismaticJointBuilder lower( float limit ) {
		this.lowerTranslation = limit;
		return this;
	}

	/**
	 * Optional, default is 1
	 * 
	 * @param limit
	 */
	public PrismaticJointBuilder upper( float limit ) {
		this.upperTranslation = limit;
		return this;
	}

	/**
	 * Optional, default is false!
	 * 
	 * @param hasMotor
	 */
	public PrismaticJointBuilder motor( boolean hasMotor ) {
		this.enableMotor = hasMotor;
		return this;
	}

	/**
	 * Optional, default axis is (1,0)
	 * 
	 * @param _axis
	 */
	public PrismaticJointBuilder axis( Vector2 _axis ) {
		this.axis = _axis;
		return this;
	}

	/**
	 * Optional, default axis is (1,0)
	 * 
	 * @param x
	 * @param y
	 */
	public PrismaticJointBuilder axis( float x, float y ) {
		this.axis = new Vector2( x, y );
		return this;
	}

	/**
	 * Optional, default is
	 * 
	 * @param _maxMotorForce
	 */
	public PrismaticJointBuilder maxMotor( float _maxMotorForce ) {
		this.maxMotorForce = _maxMotorForce;
		return this;
	}

	public PrismaticJointBuilder motorSpeed( float _motorSpeed ) {
		this.motorSpeed = _motorSpeed;
		return this;
	}
}
