package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;



public class TrophyScreen implements com.badlogic.gdx.Screen{

	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label[] player1 = null;
	private Label[] player2 = null;
	private Label next = null;
	private int lineHeight = 0;
	
	public TrophyScreen ( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		//player1 = new Label[p1Trophy.length];
		//player2 = new Label[p2Trophy.length];
		next = new Label( "Press ENTER to continue ", font );
		
		/*for (int i = 0; i < p1Trophy.length; i++ ) {
			addNewTrophy( i, p1Trophy[i], 1 );
		}
		
		for (int j = 0; j < p2Trophy.length; j++ ) {
			addNewTrophy( j, p2Trophy[j], 2 );
		}*/
	}
	
	private void addNewTrophy ( int index, String trophy, int player ) {
		if ( player == 1 ) {
			player1[index] = new Label( trophy, font );
		}
		else if ( player == 2 ) {
			player2[index] = new Label( trophy, font );
		}
	}
	
	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.0f, 0.5f, 0.5f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		
		batch.begin( );
		for (int i = 0; i < player1.length; i++ ) {
			player1[i].draw(  batch );
		}
		for (int j = 0; j < player2.length; j++ ) {
			player2[j].draw(  batch );
		}
		next.draw( batch );
		batch.end( );
		
		if(Gdx.input.isKeyPressed( Keys.ENTER )){
			ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		for (int i = 0; i < player1.length; i++ ) {
			player1[i].setX( centerX - player1[i].getWidth( ) / 4 );
			player1[i].setY( centerY + lineHeight + ( i*2 ) );
		}
		for (int j = 0; j < player2.length; j++ ) {
			player2[j].setX( centerX - player2[j].getWidth( ) * 4 );
			player2[j].setY( centerY + lineHeight + ( j*2 ) );
		}
		next.setX( centerX - next.getWidth( ) / 2 );
		next.setY( 20 + next.getHeight( ) );
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
	public void resume( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub
		
	}
}
