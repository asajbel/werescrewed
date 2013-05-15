package com.blindtigergames.werescrewed.gui;

import aurelienribon.tweenengine.TweenAccessor;

public class ButtonTweenAccessor implements TweenAccessor< Button > {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;

	@Override
	public int getValues( Button target, int tweenType, float[ ] returnValues ) {
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
			// Gdx.app.log( "ButtonAccessor",
			// "You tries using Button Accessor get with a wrong tween type." );
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues( Button target, int tweenType, float[ ] newValues ) {
		// TODO Auto-generated method stub
		switch ( tweenType ) {
		case POSITION_X:
			target.setX( ( int ) newValues[ 0 ] );
			break;
		case POSITION_Y:
			target.setY( ( int ) newValues[ 0 ] );
			break;
		case POSITION_XY:
			target.setX( ( int ) newValues[ 0 ] );
			target.setY( ( int ) newValues[ 1 ] );
			break;
		default:
			// Gdx.app.log( "ButtonAccessor",
			// "You tries using Entity Accessor set with a wrong tween type." );
			assert false;
		}
	}
}

/*
 * 
 * package com.blindtigergames.werescrewed.entity.tween;
 * 
 * 
 * 
 * import com.badlogic.gdx.Gdx; import
 * com.blindtigergames.werescrewed.entity.Entity;
 * 
 * public class EntityAccessor {
 * 
 * 
 * 
 * 
 * 
 * }
 */