	package com.blindtigergames.werescrewed.entity.mover;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;

/**
 * General purpose mover for building an arbitrary path.
 * Set the tween to repeat forever using Tween.INFINITE.
 * @author stew
 *
 */

public class TweenMover implements IMover {

	private boolean runSynchronously; //run tweens all at the same time(true) OR one at a time(false) 
    protected ArrayList<Tween> tweens; //active tweens that will be updated

    /**
     * 
     * @param runSynchronously run tweens all at the same time(true) OR one at a time(false)
     * @param tween
     */
    public TweenMover(boolean runSynchronously, Tween tween) {
    	init(runSynchronously);
    	tweens.add(tween);
    }
    
    /**
     * 
     * @param runSynchronously run tweens all at the same time(true) OR one at a time(false)
     * @param tween...
     */
    public TweenMover(boolean runSynchronously, Tween... tween){
    	init(runSynchronously);
    	for ( Tween t : tween ){
    		tweens.add(t);
    	}
    }
    
    private void init(boolean runSynchronously){
    	this.runSynchronously = runSynchronously;
    	this.tweens = new ArrayList<Tween>();
    }

    @Override
    public void move( float deltaTime, Body body ) {
    	//TODO: if a tween is shut off, the body is moved, then the tween is turned back on
    	//It will jump. Need to add a notification so tween knows how to get back to prev path
        if ( runSynchronously ){
	    	for ( Tween t : tweens ){
	        	t.update( deltaTime );
	        }
        }else{ //run tweens one at a time
        	if ( tweens.size( ) > 0 ){
        		Tween t = tweens.get(0);
        		t.update( deltaTime );
        		if ( t.isFinished( ) ){
        			t.kill( );
        			tweens.remove( t );
        		}
        	}
        }
    }

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        
    }

	@Override
	public void runPuzzleMovement( float screwVal, Platform p ) {
		// TODO Auto-generated method stub
		Gdx.app.log( "tweenmover", "" );
	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addTween(Tween tween){
		tweens.add(tween);
	}
	
	public void destroyAllTweens(){
		for(Tween t : tweens ){
			t.kill();
		}
		tweens.clear( );
	}
	
	/**
	 * Crashes if there aren't any tweens in the list
	 * @return
	 */
	protected Tween getFirstTween(){
		if ( tweens.size( ) > 0 ){
			return tweens.get( 0 );
		} else {
			throw new IndexOutOfBoundsException("TweenMover: No tweens in the list when calling getFirstTween()");
		}
	}
	
	protected boolean hasNoTweens(){
		return (tweens.size( ) == 0);
	}
}
