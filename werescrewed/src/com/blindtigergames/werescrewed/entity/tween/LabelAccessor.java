package com.blindtigergames.werescrewed.entity.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.blindtigergames.werescrewed.gui.Label;

/*******************************************************************************
 * Label GUI accessor for interfacing with the Universal tween engine
 * 
 * @author stew
 ******************************************************************************/

public class LabelAccessor implements TweenAccessor< Label > {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;

	@Override
	public int getValues( Label target, int tweenType, float[ ] returnValues ) {
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
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues( Label target, int tweenType, float[ ] newValues ) {
		// TODO Auto-generated method stub
		switch ( tweenType ) {
		case POSITION_X:
			target.setX( (int)newValues[0] );
			break;
		case POSITION_Y:
			target.setY( (int)newValues[0] );
			break;
		case POSITION_XY:
			target.setX( (int)newValues[0]);
			target.setY( (int)newValues[1]);
			break;
		default:
			assert false;
		}
	}

}
