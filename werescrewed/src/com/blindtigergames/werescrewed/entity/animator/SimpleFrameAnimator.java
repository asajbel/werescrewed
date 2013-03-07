package com.blindtigergames.werescrewed.entity.animator;

import com.badlogic.gdx.Gdx;

public class SimpleFrameAnimator implements IAnimator {

	protected int frame;
	protected float speed, time;
	protected int start, frames;
	
	public enum LoopBehavior{
		STOP(0),
		LOOP(1),
		YOYO(2);
		int value;
		LoopBehavior(int v){
			value = v;
		}
		public int toInt(){
			return value;
		}
		public LoopBehavior fromInt(int v){
			for (LoopBehavior b: LoopBehavior.values( )){
				if (b.value == v)
					return b;
			}
			return STOP;
		}
		
	}
	protected LoopBehavior loop;
	
	
	public SimpleFrameAnimator( ) {
		frame = 0;
		speed = 1.0f;
		time = 0.0f;
		start = 0; frames = 0;
		loop = LoopBehavior.LOOP;
	}


	public SimpleFrameAnimator speed(float s){
		speed = s;
		return this;
	}
	
	public SimpleFrameAnimator time(float t){
		time = t;
		return this;
	}
	
	public SimpleFrameAnimator startFrame(int s){
		start = s;
		return this;
	}

	public SimpleFrameAnimator maxFrames(int f){
		frames = f;
		return this;
	}
	
	public SimpleFrameAnimator frame(int f){
		frame = f;
		return this;
	}

	public SimpleFrameAnimator loop(LoopBehavior b){
		loop = b;
		return this;
	}
	
	@Override
	public void update( float dT ) {
		time += (dT * speed);
		if (frames > 0){
			frame = validFrame( (int)Math.floor( time / frames) );
		} else {
			frame = 0;
		}
		Gdx.app.log( "SimpleFrameAnimator", "Time:"+time+" Frame"+frame );
	}

	@Override
	public String getRegion( ) {
		// TODO Auto-generated method stub
		return Integer.toString(frame + start);
	}

	@Override
	public void setRegion( String r ) {
		setFrameNumber(Integer.valueOf( r ));
	}

	@Override
	public int getFrameNumber( ) {
		return frame + start;
	}

	@Override
	public void setFrameNumber( int f ) {
		frame = f - start;		
	}

	public int validFrame( int f ){
		if (f < 0){
			if (loop == LoopBehavior.LOOP){
				f = frames;
			} else {
				f = 0;
				if (loop == LoopBehavior.YOYO){
					speed *= -1.0f;
				} else {
					speed = 0.0f;
				}
			}
		}
		if (f > frames){
			if (loop == LoopBehavior.LOOP){
				f = 0;
			} else {
				f = frames;
				if (loop == LoopBehavior.YOYO){
					speed *= -1.0f;
				} else {
					speed = 0.0f;
				}
			}
		}
		return f;
	}
	
}
