package com.blindtigergames.werescrewed.entity.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.blindtigergames.werescrewed.entity.Entity;

/*******************************************************************************
 * Entity accessor for interfacing with the Universal tween engine
 * 
 * @author stew
 ******************************************************************************/

public class EntityAccessor implements TweenAccessor< Entity > {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;
	public static final int VELOCITY_X = 4;
	public static final int VELOCITY_Y = 5;
	public static final int VELOCITY_XY = 6;
	public static final int ROTATION = 7;
	public static final int ANGULAR_VEL = 8;

	@Override
	public int getValues( Entity target, int tweenType, float[ ] returnValues ) {
		switch ( tweenType ) {
		case POSITION_X:
			returnValues[ 0 ] = target.getPosition( ).x;
			return 1;
		case POSITION_Y:
			returnValues[ 0 ] = target.getPosition( ).y;
			return 1;
		case POSITION_XY:
			returnValues[ 0 ] = target.getPosition( ).x;
			returnValues[ 1 ] = target.getPosition( ).y;
			return 2;
		case VELOCITY_X:
			returnValues[ 0 ] = target.body.getLinearVelocity( ).x;
			return 1;
		case VELOCITY_Y:
			returnValues[ 0 ] = target.body.getLinearVelocity( ).y;
			return 1;
		case VELOCITY_XY:
			returnValues[ 0 ] = target.body.getLinearVelocity( ).x;
			returnValues[ 1 ] = target.body.getLinearVelocity( ).y;
			return 2;
		case ROTATION:
			returnValues[ 0 ] = target.body.getAngle( );
			return 1;
		case ANGULAR_VEL:
			returnValues[ 0 ] = target.body.getAngularVelocity( );
			return 1;
		default:
			// Gdx.app.log( "EntityAccessor",
			// "You tries using Entity Accessor get with a wrong tween type." );
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues( Entity target, int tweenType, float[ ] newValues ) {
		// TODO Auto-generated method stub
		switch ( tweenType ) {
		case POSITION_X:
			target.body.setTransform( newValues[ 0 ],
					target.body.getPosition( ).y, target.body.getAngle( ) );
			break;
		case POSITION_Y:
			target.body.setTransform( target.body.getPosition( ).x,
					newValues[ 0 ], target.body.getAngle( ) );
			break;
		case POSITION_XY:
			target.body.setTransform( newValues[ 0 ], newValues[ 1 ],
					target.body.getAngle( ) );
			break;
		case VELOCITY_X:
			target.body.setLinearVelocity( newValues[ 0 ],
					target.body.getLinearVelocity( ).y );
			break;
		case VELOCITY_Y:
			target.body.setLinearVelocity( target.body.getLinearVelocity( ).x,
					newValues[ 0 ] );
			break;
		case VELOCITY_XY:
			target.body.setLinearVelocity( newValues[ 0 ], newValues[ 1 ] );
			break;
		case ROTATION:
			target.body
					.setTransform( target.body.getPosition( ), newValues[ 0 ] );
			break;
		case ANGULAR_VEL:
			target.body.setAngularVelocity( newValues[ 0 ] );
			break;
		default:
			// Gdx.app.log( "EntityAccessor",
			// "You tries using Entity Accessor set with a wrong tween type." );
			assert false;
		}
	}

}
