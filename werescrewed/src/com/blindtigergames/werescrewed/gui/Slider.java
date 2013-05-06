package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class Slider extends OptionControl {

	private static TextureRegion screwTex = WereScrewedGame.manager
			.getAtlas( "common-textures" ).findRegion( "flat_head_circular" );
	private Sprite volume = new Sprite( screwTex );
	private float xPos = 0;   //X position of sprite
	private float yPos = 0;   //Y position of sprite, should not change after initializing
	
	public Slider( int min, int max, int current, int x, int y ) {
		super( min, max, current, x, y );
	}
	
	public Slider( int min, int max, int current ) {
		super( min, max, current );
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
	
	public void moveLeft( ) {
		if ( curValue > minValue ) {
			curValue--;
			xPos -= 2.0f;
			volume.setX( xPos );
		}
	}
	
	public void moveRight( ) {
		if ( curValue < maxValue ) {
			curValue++;
			xPos += 2.0f;
			volume.setX( xPos );
		}
	}
	
	public void draw(SpriteBatch batch ) {
		volume.setPosition( xPos, yPos );
		volume.draw( batch );
		if ( activated ) {
			volume.rotate( 3.0f );
		}
		
	}
}
