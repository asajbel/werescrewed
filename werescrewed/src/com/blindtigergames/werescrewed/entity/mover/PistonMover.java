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

    public PistonMover( PrismaticJoint _joint, float _restTime ) {
        this.joint = _joint;
        isExploding = false;
        motorSpeed = this.joint.getMotorSpeed();
        restTime = _restTime;
        time = 0;
    }
    
    public PistonMover( PrismaticJoint _joint ) {
        this.joint = _joint;
        isExploding = false;
        motorSpeed = this.joint.getMotorSpeed();
        restTime = -1;
        time = 0;
        isPuzzlePiston = true;
    }

    @Override
    public void move( float deltaTime, Body body ) {
        time += deltaTime;
        
        
        boolean atLowerLimit = joint.getJointTranslation() <= joint
                .getLowerLimit();
        boolean atUpperLimit = joint.getJointTranslation() >= joint
                .getUpperLimit();

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
    

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        // TODO Auto-generated method stub

    }

}
