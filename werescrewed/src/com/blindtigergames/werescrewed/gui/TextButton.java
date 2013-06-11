package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class TextButton extends Button {

	private ButtonHandler handler = null;

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
	public TextButton( String caption, BitmapFont font, TextureRegion button,
			ButtonHandler handler, int x, int y ) {
		super( caption, font, button, x, y );
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		this.handler = handler;
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
	public TextButton( String caption, BitmapFont font, 
			TextureRegion button, ButtonHandler handler ) {
		this( caption, font, button, handler, 0, 0 );
	}

	public void draw( SpriteBatch batch, Camera camera ) {
		super.draw( batch, camera );

		Vector3 cursorPosition = new Vector3( Gdx.input.getX( ),
				Gdx.input.getY( ), 0 );
		camera.unproject( cursorPosition );
		boolean isIntersect = bounds.contains( cursorPosition.x,
				cursorPosition.y );
		
		//if ( ( isIntersect && !WereScrewedGame.isMouseClicked( ) && ( Gdx.input
		//		.isTouched( ) || Gdx.input.isButtonPressed( Buttons.LEFT ) ) )
		//		|| selected ) {
		if ( selected ) {
			selected = false;
			handler.onClick( );
		}
	}

	public static interface ButtonHandler {
		public void onClick( );

	}

}
