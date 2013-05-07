package com.blindtigergames.werescrewed.screens;

public class TransitionScreen extends Screen {

	Screen current;
	Screen next;

	int currentTransitionEffect = 0;
	//ArrayList<TransitionEffect> transitionEffects;

	/*TransitionScreen( Screen current, Screen next, ArrayList<TransitionEffect> transitionEffects ) {
		this.current = current;
		this.next = next;
		this.transitionEffects = transitionEffects;
	}

	void render( ) {
		if ( currentTransitionEffect >= transitionEffects.size( ) ) {
			return;
		}

		transitionEffects.get( currentTransitionEffect ).update( getDelta( ) );
		transitionEffects.get( currentTransitionEffect ).render( current, next );

		if ( transitionEffects.get(currentTransitionEffect).isFinished( ) )
			currentTransitionEffect++;
	}
	*/
}
