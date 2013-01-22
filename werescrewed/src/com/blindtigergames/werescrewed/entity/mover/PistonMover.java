package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;

/**
 * Applies an impulse to an object to give it a one time push Can be looped if
 * given a time
 * 
 * @author stew
 * 
 */
public class PistonMover implements IMover {

    // create a sliding joint

    protected PrismaticJoint joint;
    boolean isExploding;
    float motorSpeed;
    float restTime, time;
    boolean isPuzzlePiston;
    boolean hasBeenExploded;

    public PistonMover( PrismaticJoint _joint ) {
        this( _joint, -1 );
        isPuzzlePiston = true;
    }

    public PistonMover( PrismaticJoint _joint, float _restTime ) {
        this.joint = _joint;
        isExploding = false;
        motorSpeed = this.joint.getMotorSpeed();
        restTime = _restTime;
        time = 0;
        isPuzzlePiston = false;
        hasBeenExploded = false;
    }

    public void explode() {
        hasBeenExploded = true;
    }

    @Override
    public void move( float deltaTime, Body body ) {

        boolean atUpperLimit = joint.getJointTranslation() >= joint
                .getUpperLimit();

        if ( isPuzzlePiston ) {
            if ( hasBeenExploded ) {
                joint.setMotorSpeed( -joint.getMotorSpeed() ); // flip speed of
                                                               // motor so it
                                                               // pops.
                hasBeenExploded = false;
            }
            if ( atUpperLimit ) {
                joint.setMotorSpeed( -joint.getMotorSpeed() );
            }

        } else {
            time += deltaTime; // if it's a puzzle piston, we don't need to
                               // manage time.

            boolean atLowerLimit = joint.getJointTranslation() <= joint
                    .getLowerLimit();

            // Gdx.app.log("PrismaticMover",
            // "Current translation: "+joint.getJointTranslation()+
            // ", lower: "+joint.getLowerLimit()+"("+atLowerLimit+")"+", upper: "+joint.getUpperLimit()+"("+atUpperLimit+")");
            if ( atLowerLimit ) {
                if ( time >= restTime ) {
                    isExploding = true;
                    joint.setMotorSpeed( -joint.getMotorSpeed() );
                    time = 0;
                }
            } else if ( atUpperLimit ) {
                isExploding = false;
                joint.setMotorSpeed( -joint.getMotorSpeed() );
            }
        }
    }

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        // TODO Auto-generated method stub

    }

}
