package com.blindtigergames.werescrewed.entity.animator;

public class SimpleFrameAnimator implements IAnimator {

	protected int frame;
	protected float speed, time;
	protected int start, frames;
	protected int atlas;

	public enum LoopBehavior {
		STOP( 0 ), LOOP( 1 ), YOYO( 2 );
		int value;

		LoopBehavior( int v ) {
			value = v;
		}

		public int toInt( ) {
			return value;
		}

		public static LoopBehavior fromInt( int v ) {
			for ( LoopBehavior b : LoopBehavior.values( ) ) {
				if ( b.value == v )
					return b;
			}
			return STOP;
		}

	}

	protected LoopBehavior loop;
	protected String prefix;

	public SimpleFrameAnimator( ) {
		frame = 0;
		speed = 1.0f;
		time = 0.0f;
		start = 0;
		frames = 0;
		loop = LoopBehavior.LOOP;
		prefix = "";
		atlas = 0;
	}

	public SimpleFrameAnimator speed( float s ) {
		speed = s;
		return this;
	}

	public SimpleFrameAnimator time( float t ) {
		time = t;
		return this;
	}

	public SimpleFrameAnimator startFrame( int s ) {
		start = s;
		return this;
	}

	public SimpleFrameAnimator maxFrames( int f ) {
		// Gdx.app.log("SimpleFrameAnimator", "Setting max frames to: "+f);
		frames = f;
		return this;
	}

	public SimpleFrameAnimator frame( int f ) {
		frame = f;
		return this;
	}

	public SimpleFrameAnimator loop( LoopBehavior b ) {
		loop = b;
		return this;
	}

	public SimpleFrameAnimator prefix( String p ) {
		prefix = p;
		return this;
	}

	public SimpleFrameAnimator atlas( int a ) {
		setAtlas( a );
		return this;
	}

	@Override
	public void update( float dT ) {
		incrementTime( dT );
		frame = ( int ) Math.floor( time * frames );
		// Gdx.app.log( "SimpleFrameAnimator", "Time: "+time+" Frame: "+frame );
	}

	@Override
	public String getRegion( ) {
		// TODO Auto-generated method stub
		return prefix + Integer.toString( frame + start );
	}

	@Override
	public void setRegion( String r ) {
		setIndex( Integer.valueOf( r ) );
	}

	@Override
	public int getIndex( ) {
		return -1;
	}

	@Override
	public void setIndex( int f ) {
	}

	@Override
	public int getFrame( ) {
		return frame + start;
	}

	public void setFrame( int f ) {
		frame = f - start;
		time = ( float ) frame / frames;
	}

	@Override
	public void setAtlas( int a ) {
		atlas = a;
	}

	@Override
	public int getAtlas( ) {
		return atlas;
	}

	@Override
	public float getTime( ) {
		return time;
	}

	@Override
	public void reset( ) {
		frame = 0;
		time = 0.0f;
		speed = 1.0f;
	}

	public boolean isStopped( ) {
		return ( speed == 0.0f );
	}

	protected void incrementTime( float dT ) {
		time += ( dT * speed );
		if ( time < 0.0f ) {
			switch ( loop ) {
			case STOP:
				time = 0.0f;
				speed = 0.0f;
				break;
			case LOOP:
				time -= Math.floor( time );
				break;
			case YOYO:
				time = 0.0f;
				speed *= -1.0f;
				break;
			}
		}
		if ( time > 1.0f ) {
			switch ( loop ) {
			case STOP:
				time = 1.0f;
				speed = 0.0f;
				break;
			case LOOP:
				time -= Math.floor( time );
				break;
			case YOYO:
				time = 1.0f;
				speed *= -1.0f;
				break;
			}
		}
	}
}