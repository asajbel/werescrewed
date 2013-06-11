package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundType;

public class Slider extends OptionControl {

	private Sprite volume = null;
	private Sprite screw = null;
	private float xPos = 0;   //X position of screw sprite
	private float yPos = 0;   //Y position of screw sprite, should not change after initializing
	private float maxPos = 0; // The farthest the screw on the slider can go to the right
	private float minPos = 0; // The farthest the screw on the slider can go to the left
	private final float SHIFT = 1.2f;
	private SoundType type = null;
	
	public Slider( int min, int max, int current, int x, int y, 
			SoundType type, TextureRegion slidTex, TextureRegion screwTex ) {
		super( min, max, current, x, y );
		//Texture slidTex = WereScrewedGame.manager.get(
		//		WereScrewedGame.dirHandle + "/menu/slider.png", Texture.class );
		//Texture screwTex = WereScrewedGame.manager.get(
		//		WereScrewedGame.dirHandle + "/menu/screw.png", Texture.class );
		volume = new Sprite( slidTex );
		screw = new Sprite( screwTex );
		maxPos = x + 180;
		minPos = x + 60;
		this.type = type;
	}
	
	public Slider( int min, int max, int current, SoundType type, 
			TextureRegion slidTex, TextureRegion screwTex ) {
		this( min, max, current, 0, 0, type, slidTex, screwTex );
	}

	@Override
	public void setX( int x ) {
		this.x = x;
		maxPos = x + 180;
		minPos = x + 60;
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
	
	// Returns farthest right position the screw can go
	public float getMaxPos ( ) {
		return maxPos;
	}
	
	// Returns farthest left position the screw can go
	public float getMinPos ( ) {
		return minPos;
	}

	// Decreases volume
	public void moveLeft( ) {
		if ( SoundManager.globalVolume.get( type ) > 0.0f ) {
			xPos -= SHIFT;
			if ( xPos < minPos )
				xPos = minPos;
			screw.setX( xPos );
			float val = SoundManager.globalVolume.get( type ) - 0.01f;
			if ( val < 0 ) {
				val = 0f;
			}
			SoundManager.globalVolume.put( type, val ); 
			WereScrewedGame.getPrefs( ).setSoundValue( type.name( ), val ); 
		}
	}

	// Increases volume
	public void moveRight( ) {
		if ( SoundManager.globalVolume.get( type ) < 1.0f ) {
			xPos += SHIFT;
			if ( xPos > maxPos )
				xPos = maxPos;
			screw.setX( xPos );
			float val = SoundManager.globalVolume.get( type ) + 0.01f;
			if ( val > 1.0 ){
				val = 1.0f;
			}
			SoundManager.globalVolume.put( type, val ); 
			WereScrewedGame.getPrefs( ).setSoundValue( type.name( ), val );
		}
	}

	private void setAlpha( ) {
		if ( activated )
			alpha = 1.0f;
		else
			alpha = 0.6f;
	}

	public void draw( SpriteBatch batch ) {
		// volume.setOrigin( volume.getWidth( ) / 2, volume.getHeight( ) / 2 );
		setAlpha( );
		volume.setPosition( x, y );
		volume.draw( batch, alpha );
		screw.setPosition( xPos, yPos );
		screw.draw( batch, alpha );
		if ( activated ) {
			screw.rotate( 3.0f );
		}

	}
}
