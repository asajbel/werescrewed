package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;

/**
 * Moves an entity on a prismatic joint using a motor FOREVER. Will not stop
 * This mover assumes you have created a prismatic joint already
 * @author stew
 * 
 */
public class PistonMover implements IMover {

    protected PrismaticJoint joint;
    private float motorSpeed;
    private float restTime;
    private float time;
    private float delay;

    /**
     * Construct a PistonMover with rest & delay
     * @param _joint
     * @param _restTime
     * @param _delay
     */
    public PistonMover( PrismaticJoint _joint, float _restTime, float _delay ){
        this.joint = _joint;
        motorSpeed = this.joint.getMotorSpeed();
        restTime = _restTime;
        delay = _delay;
        time = 0.0f;
    }
    
    /**
     * Use this if you want a rest but no delay
     * @param _joint
     * @param _restTime
     */
    public PistonMover( PrismaticJoint _joint, float _restTime ) {
        this( _joint, _restTime, 0.0f );
    }
    
    /**
     * Use this constructor if you want the piston to not have any rest/delay
     * @param _joint
     */
    public PistonMover( PrismaticJoint _joint ) {
        this( _joint, -1.0f, 0.0f );
    }

    //TODO: Make the piston movement look nicer
    @Override
    public void move( float deltaTime, Body body ) {
        time += deltaTime;
        
        boolean atLowerLimit = joint.getJointTranslation() <= joint
                .getLowerLimit();
        boolean atUpperLimit = joint.getJointTranslation() >= joint
                .getUpperLimit();

        if ( atLowerLimit ) {
            if ( time >= restTime + delay  ) {
                if ( delay != 0.0 ) delay = 0.0f; //Only use delay on the very first round
                joint.setMotorSpeed( -joint.getMotorSpeed() );
                time = 0;
            }
        } else if ( atUpperLimit ) {
            joint.setMotorSpeed( -joint.getMotorSpeed() );
        }
    }
    

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        // TODO Auto-generated method stub

    }

}
