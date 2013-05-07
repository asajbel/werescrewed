package com.blindtigergames.werescrewed.screens.transitions;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenType;

public class FadeOutTransition extends TransitionEffect {

	FadeOutTransition( float duration ) {
		super( duration );
	}

	@Override
	public void render( Screen current, ScreenType next, float delta ) {
		current.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, getAlpha( ) );
		// draw a quad over the screen using the color
	}
}
