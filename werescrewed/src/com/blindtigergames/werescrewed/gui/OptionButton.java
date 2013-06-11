package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class OptionButton extends Button {

	private OptionControl control = null;

	/**
	 * makes a new button instance
	 * 
	 * @param caption
	 *            String
	 * @param font
	 *            BitmapFont
	 * @param handler
	 *            ButtonHandler
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public OptionButton( String caption, BitmapFont font, TextureRegion button,
			OptionControl control, int x, int y ) {
		super( caption, font, button, x, y );
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		this.control = control;
		// calculateDimensions( );
	}

	/**
	 * makes a new button instance
	 * 
	 * @param caption
	 *            String
	 * @param font
	 *            BitmapFont
	 * @param handler
	 *            ButtonHandler
	 */
	public OptionButton( String caption, BitmapFont font, 
			TextureRegion button, OptionControl control ) {
		this( caption, font, button, control, 0, 0 );
	}

	@Override
	public void setX( int x ) {
		this.x = x;
		bounds.x = x;
		control.setX( x * 4 );
	}

	@Override
	public void setY( int y ) {
		this.y = y;
		bounds.y = y - height * 2;
		control.setY( y - height * 2 + 20 );
	}

	public OptionControl getOption( ) {
		return control;
	}

	public void draw( SpriteBatch batch, Camera camera ) {
		super.draw( batch, camera );

		Vector3 cursorPosition = new Vector3( Gdx.input.getX( ),
				Gdx.input.getY( ), 0 );
		camera.unproject( cursorPosition );
		boolean isIntersect = bounds.contains( cursorPosition.x,
				cursorPosition.y );
		
		control.draw( batch );
		//if ( ( isIntersect && ( Gdx.input.isTouched( ) || Gdx.input
		//		.isButtonPressed( Buttons.LEFT ) ) ) || selected ) {
		if ( selected ) {
			selected = false;
			control.setActive( true );
		}

		if ( colored )
			control.setActive( true );
		else
			control.setActive( false );
	}
}
