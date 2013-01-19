package com.blindtigergames.werescrewed.entity.mover.tween;

import com.badlogic.gdx.physics.box2d.Body;

import aurelienribon.tweenengine.TweenAccessor;

/*******************************************************************************
 * Box2D Body accessor for interfacing with the Universal tween engine
 * @author stew
 ******************************************************************************/

public class BodyAccessor implements TweenAccessor<Body>{

	public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int VELOCITY_X = 4;
    public static final int VELOCITY_Y = 5;
    public static final int VELOCITY_XY = 6;
    public static final int ROTATION = 7;
    public static final int ANGULAR_VEL = 8;
	
	@Override
	public int getValues(Body target, int tweenType, float[] returnValues) {
		switch (tweenType) {
        case POSITION_X: returnValues[0] = target.getPosition().x; return 1;
        case POSITION_Y: returnValues[0] = target.getPosition().y; return 1;
        case POSITION_XY:
            returnValues[0] =  target.getPosition().x;
            returnValues[1] = target.getPosition().y;
            return 2;
        case VELOCITY_X: returnValues[0] = target.getLinearVelocity().x; return 1;
        case VELOCITY_Y: returnValues[0] = target.getLinearVelocity().y; return 1;
        case VELOCITY_XY:
            returnValues[0] =  target.getLinearVelocity().x;
            returnValues[1] = target.getLinearVelocity().y;
            return 2;
        case ROTATION: returnValues[0] = target.getAngle(); return 1;
        case ANGULAR_VEL: returnValues[0] = target.getAngularVelocity(); return 1;
        default: assert false; return -1;
    }
	}

	@Override
	public void setValues(Body target, int tweenType, float[] newValues) {
		// TODO Auto-generated method stub
		
	}

}
