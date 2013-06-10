package com.blindtigergames.werescrewed.entity.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.blindtigergames.werescrewed.entity.Sprite;

/*******************************************************************************
 * Sprite accessor for interfacing with the Universal tween engine
 * 
 * @author stew
 ******************************************************************************/

public class SpriteAccessor implements TweenAccessor< Sprite > {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;
	public static final int ROTATION = 7;

	@Override
	public int getValues( Sprite target, int tweenType, float[ ] returnValues ) {
		switch ( tweenType ) {
		case POSITION_X:
			returnValues[ 0 ] = target.getX( );
			return 1;
		case POSITION_Y:
			returnValues[ 0 ] = target.getY( );
			return 1;
		case POSITION_XY:
			returnValues[ 0 ] = target.getX( );
			returnValues[ 1 ] = target.getY( );
			return 2;
		case ROTATION:
			returnValues[ 0 ] = target.getRotation( );
			return 1;
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues( Sprite target, int tweenType, float[ ] newValues ) {
		// TODO Auto-generated method stub
		switch ( tweenType ) {
		case POSITION_X:
			target.setX( newValues[0] );
			break;
		case POSITION_Y:
			target.setY( newValues[0] );
			break;
		case POSITION_XY:
			target.setPosition( newValues[0], newValues[1] );
			break;
		case ROTATION:
			target.setRotation( newValues[0] );
			break;
		default:
			assert false;
		}
	}

}
