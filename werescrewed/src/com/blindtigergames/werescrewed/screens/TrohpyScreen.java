package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;

public class TrohpyScreen implements com.badlogic.gdx.Screen{

	private Label player1 = null;
	private Label player2 = null;
	private SpriteBatch batch = null;
	private BitmapFont font = null;;
	
	public TrohpyScreen ( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		player1 = new Label( "Mission Complete!", font );
		player2 = new Label( "Mission Complete!", font );
	}
	
	@Override
	public void render( float arg0 ) {
		// TODO Auto-generated method stub
		
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
