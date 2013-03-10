package com.blindtigergames.werescrewed.joint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class RevoluteJointBuilder {

    /**
     * Required parameters
     */
    World world;
    Skeleton skeleton;
    Entity bodyB;
    Vector2 anchor;

    // Default parameter values
    boolean enableLimit = false;
    float lowerAngle = 0.0f;
    float upperAngle = 90 * Util.DEG_TO_RAD;
    boolean enableMotor = false;
    float maxMotorTorque = 500;// high max motor force yields a very strong motor
    float motorSpeed = 1; // 1 is relatively slow

    /**
     * empty constructor is private to force passing in world when building this
     * joint
     */
    @SuppressWarnings("unused")
    private RevoluteJointBuilder() {
        
    };

    /**
     * These are the required parameters for a prismatic joint
     */
    public RevoluteJointBuilder( World _world ) {
        // TODO Auto-generated constructor stub
        this.world = _world;
    }

    /**
     * Creates prismatic joint, adds it to world, and to the skeleton
     * @return
     */
    public RevoluteJoint build() {
        if ( bodyB == null || skeleton == null ) {
            Gdx.app.error( "RevoluteJointBuilder",
                    "You didn't initialize bodyB and/or skeleton, you doofus!" );
        }
        if ( anchor == null ) {
            anchor = bodyB.body.getWorldCenter();
        }
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize( skeleton.body, bodyB.body, anchor );
        revoluteJointDef.enableLimit = enableLimit;
        revoluteJointDef.lowerAngle = lowerAngle;
        revoluteJointDef.upperAngle = upperAngle;
        revoluteJointDef.enableMotor = enableMotor;
        revoluteJointDef.maxMotorTorque = maxMotorTorque;// high max motor force
                                                        // yields a
        // very strong motor
        revoluteJointDef.motorSpeed = motorSpeed;

        RevoluteJoint joint = (RevoluteJoint) world
                .createJoint( revoluteJointDef );
        
        //Take this line out if you don't want skeleton to keep track of children
        //skeleton.addBoneAndJoint( bodyB, joint );
        
        return joint;
    }

    /**
     * Enforce Skeleton, since I'm under the impression everything is attached
     * to a skeleton
     * 
     * @param _skeleton
     */
    public RevoluteJointBuilder skeleton( Skeleton _skeleton ) {
        this.skeleton = _skeleton;
        return this;
    }

    /**
     * bodyB is required to properly build this joint
     */
    public RevoluteJointBuilder bodyB( Entity _bodyB ) {
        this.bodyB = _bodyB;
        return this;
    }

    /**
     * Optional, default anchor is the center of bodyB.
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
}
