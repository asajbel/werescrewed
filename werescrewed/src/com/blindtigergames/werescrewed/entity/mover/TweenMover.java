package com.blindtigergames.werescrewed.entity.mover;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;

/**
 * General purpose mover for building an arbitrary path.
 * Set the tween to repeat forever using Tween.INFINITE.
 * @author stew
 *
 */

public class TweenMover implements IMover {

    ArrayList<Tween> tweens;

    public TweenMover(Tween tween) {
    	tweens = new ArrayList<Tween>();
    	tweens.add(tween);
    }
    
    public TweenMover(Tween... tween){
    	tweens = new ArrayList<Tween>();
    	for ( Tween t : tween ){
    		tweens.add(t);
    	}
    }

    @Override
    public void move( float deltaTime, Body body ) {
    	//TODO: if a tween is shut off, the body is moved, then the tween is turned back on
    	//It will jump. Need to add a notification so tween knows how to get back to prev path
        for ( Tween t : tweens ){
        	t.update( deltaTime );
        }
    }

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        
    }

	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addTween(Tween tween){
		tweens.add(tween);
	}
}
