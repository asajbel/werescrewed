package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class Slider extends OptionControl {

	private Sprite volume = null;
	private Sprite screw = null;
	private float xPos = 0;   //X position of screw sprite
	private float yPos = 0;   //Y position of screw sprite, should not change after initializing
	private float maxPos = 0; // The farthest the screw on the slider can go to the right
	private float minPos = 0; // The farthest the screw on the slider can go to the left
	
	public Slider( int min, int max, int current, int x, int y ) {
		super( min, max, current, x, y );
		Texture  slidTex = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/menu/slider.png", Texture.class );
		Texture screwTex = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/menu/screw.png", Texture.class );
		volume = new Sprite( slidTex );
		screw = new Sprite( screwTex );
		maxPos = x + 150;
		minPos = x + 50;
	}
	
	public Slider( int min, int max, int current ) {
		this( min, max, current, 0, 0 );
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
		maxPos = x + 150;
		minPos = x + 50;
	}
	
	public float getXPos( ) {
		return xPos;
	}
	
	public void setXPos( float newX ) {
		xPos = newX;
	}

	public float getYPos( ) {
		return yPos;
	}
	
	public void setYPos( float newY ) {
		yPos = newY;
	}
	
	public float getMaxPos ( ) {
		return maxPos;
	}
	
	public float getMinPos ( ) {
		return minPos;
	}
	
	public void moveLeft( ) {
		if ( curValue > minValue || this.xPos > minPos ) {
			curValue--;
			xPos -= 2.0f;
			screw.setX( xPos );
		}
	}
	
	public void moveRight( ) {
		if ( curValue < maxValue || this.xPos < maxPos ) {
			curValue++;
			xPos += 2.0f;
			screw.setX( xPos );
		}
	}
	
	public void draw( SpriteBatch batch ) {
		//volume.setOrigin( volume.getWidth( ) / 2, volume.getHeight( ) / 2 );
		volume.setPosition( x, y );
		volume.draw( batch );
		screw.setPosition( xPos, yPos );
		screw.draw( batch );
		if ( activated ) {
			screw.rotate( 3.0f );
		}
		
	}
}
