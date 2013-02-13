package com.blindtigergames.werescrewed.entity.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.platforms.Platform;

/*******************************************************************************
 * Platform accessor for interfacing with the Universal tween engine.
 * You will only use this for platforms so the cast to Platform is guaranteed.
 * 
 * @author stew
 ******************************************************************************/

public class PlatformAccessor extends EntityAccessor implements TweenAccessor< Entity > {

	public static final int LOCAL_POS_X = 1;
	public static final int LOCAL_POS_Y = 2;
	public static final int LOCAL_POS_XY = 3;
	public static final int LOCAL_ROT = 4;

	public int getValues( Entity target, int tweenType, float[ ] returnValues ) {
		Platform platform = ( Platform )target;
		switch ( tweenType ) {
		case LOCAL_POS_X:
			returnValues[ 0 ] = platform.getLocalPos( ).x;
			return 1;
		case LOCAL_POS_Y:
			returnValues[ 0 ] = platform.getLocalPos( ).x;
			return 1;
		case LOCAL_POS_XY:
			returnValues[ 0 ] = platform.getLocalPos( ).x;
			returnValues[ 1 ] = platform.getLocalPos( ).y;
			return 2;
		case LOCAL_ROT:
			returnValues[ 0 ] = platform.getLocalRot( );
			return 1;
		default:
			//default to using parent Entity Accessor or breaking
			if ( super.getValues( target, tweenType, returnValues ) < 0 ) {
				assert false;
			}
			return -1;
		}
	}

	public void setValues( Entity target, int tweenType, float[ ] newValues ) {
		Platform platform = ( Platform )target;
		switch ( tweenType ){
		case LOCAL_POS_X:
			platform.setLocalPos( newValues[0], platform.getLocalPos( ).y );
			break;
		case LOCAL_POS_Y:
			platform.setLocalPos( platform.getLocalPos( ).x, newValues[0] );
			break;
		case LOCAL_POS_XY:
			platform.setLocalPos( newValues[0], newValues[1] );
			break;
		case LOCAL_ROT:
			platform.setLocalRot( newValues[0] );
			break;
		default:
			//default to using parent Entity Accessor or breaking
			super.setValues( target, tweenType, newValues );
			assert false;
		}
	}

}