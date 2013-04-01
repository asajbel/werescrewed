package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;

public class TrophyScreen implements com.badlogic.gdx.Screen{

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private Label[] player1 = null;
	private Label[] player2 = null;
	private Label next = null;
	private int trophyLength = 6;
	private int lineHeight = 0;
	
	public TrophyScreen ( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		player1 = new Label[trophyLength];
		player2 = new Label[trophyLength];
		next = new Label( "Press ENTER to continue ", font );
		
		addNewTrophy( );
	}
	
	//Function that puts Trophies players earn into label to display on screen.
	//For some reason the list has to be made in reverse or else the labels 
	// appear in the wrong order.
	private void addNewTrophy ( ) {
		//These functions are temporary and should be removed/changed in the future.
		player1[5] = new Label( "Player 1", font );
		player1[4] = new Label( "Best Hat", font ); 
		player1[3] = new Label( "Most Toes", font ); 
		player1[2] = new Label( "Biggest Lips", font ); 
		player1[1] = new Label( "Most Likely to Combust", font ); 
		player1[0] = new Label( "Prettiest Nostril", font );
		 
		player2[5] = new Label( "Player 2", font ); 
		player2[4] = new Label( "Most Likely to Get Screwed", font ); 
		player2[3] = new Label( "Best Jumper", font ); 
		player2[2] = new Label( "Hairest Legs", font ); 
		player2[1] = new Label( "Biggest Screwdriver", font ); 
		player2[0] = new Label( "Most Eyeballs", font );
	}
	
	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.8f, 0.6f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		
		batch.begin( );
		for ( int i = 0; i < trophyLength; i++ ) {
			player1[i].draw(  batch );
			player2[i].draw(  batch );
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
		for ( int j = 0; j < trophyLength; j++ ) {
			player1[j].setX( centerX / 2 - player1[j].getWidth( ) / 2 );
			player1[j].setY( centerY + lineHeight * ( j+1 ) );
			
			player2[j].setX( centerX - player2[j].getWidth( ) / 2 );
			player2[j].setY( centerY + lineHeight * ( j+1 ) );
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
