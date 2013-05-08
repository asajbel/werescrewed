package com.blindtigergames.werescrewed.screens.transitions;

import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenType;

public class TransitionEffect {

	private boolean finish = false;
	private float alpha = 0.0f;
	private float duration = 0.0f;

	TransitionEffect(float dur) { 
		this.duration = dur;
	}
	// returns a value between 0 and 1 representing the level of completion of the transition.
	protected float getAlpha( ) { 
		if ( !isFinished( ) && alpha <= 1 ) {
			alpha += duration;
		}
		return alpha;
	}

	public void update( float delta ) { 
		
	}

	public boolean isFinished( ) { 
		return finish;
	}
	
	public void render( Screen current, ScreenType next, float delta ) {
		
	}
}
