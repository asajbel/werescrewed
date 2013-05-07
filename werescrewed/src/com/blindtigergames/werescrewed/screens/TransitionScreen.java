package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.blindtigergames.werescrewed.screens.transitions.TransitionEffect;

public class TransitionScreen extends Screen {

	Screen current;
	ScreenType next;

	int currentTransitionEffect = 0;
	float delta = 0.0f;
	TransitionEffect transitionEffect;

	TransitionScreen( Screen current, ScreenType next, TransitionEffect transitionEffect, float delta ) {
		this.current = current;
		this.next = next;
		this.transitionEffect = transitionEffect;
		this.delta = delta;
	}

	void render( ) {
		//transitionEffects.update( delta );
		transitionEffect.render( current, next, delta );

		if ( transitionEffect.isFinished( ) )
			ScreenManager.getInstance( ).show( next );
	}
}
