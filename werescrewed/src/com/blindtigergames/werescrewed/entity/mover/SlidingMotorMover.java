package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;

/**
 * Can be used for sliding platforms, pistons, elevators
 * 
 * @author stew
 * 
 */
public class SlidingMotorMover implements IMover {

    PuzzleType type;
    protected PrismaticJoint joint;
    protected boolean loop; // takes priority over loopOnce
    protected boolean loopOnce; // will allow joint to go the full joint length
                                // and back once
    boolean recentlyFlipped;

    public SlidingMotorMover( PuzzleType _type, PrismaticJoint _joint ) {
        // TODO Auto-generated constructor stub
        joint = _joint;
        loop = false;
        loopOnce = true;
        type = _type;
        recentlyFlipped = true;
    }

    @Override
    public void move( float deltaTime, Body body ) {
        // TODO Auto-generated method stub\
        boolean atLowerLimit = joint.getJointTranslation() <= joint
                .getLowerLimit();
        boolean atUpperLimit = joint.getJointTranslation() >= joint
                .getUpperLimit();
        // Gdx.app.log("PrismaticMover",
        // "Current translation: "+joint.getJointTranslation()+
        // ", lower: "+joint.getLowerLimit()+"("+atLowerLimit+")"+", upper: "+joint.getUpperLimit()+"("+atUpperLimit+")");
        if ( atLowerLimit || atUpperLimit ) {
            // Gdx.app.log("PrismaticMover",
            // "at upper/lower limit, recently flipped="+recentlyFlipped);
            if ( !recentlyFlipped ) {
                recentlyFlipped = true;
                joint.setMotorSpeed( -joint.getMotorSpeed() );
                // Gdx.app.log("PrismaticMover", "flipping motor speed");
            }
        } else {
            recentlyFlipped = false;
        }
    }

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        // TODO Auto-generated method stub
        Gdx.app.error( "PrismaticMoverError",
                "This method isn't supported yet. Don't use it." );
    }

}
