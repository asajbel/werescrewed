package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Game;

public class TransitionScreen implements com.badlogic.gdx.Screen {

	Game game;
	ScreenType cur;
	ScreenType next;
	
	TransitionScreen( ScreenType cur, ScreenType next ) {
		this.cur = cur;
		this.next = next;
	}
	
	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render( float arg0 ) {
		// TODO Auto-generated method stub
		ScreenManager.getInstance( ).dispose( cur );
		ScreenManager.getInstance( ).show( next );
	}

	@Override
	public void resize( int arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub
		
	}

}
