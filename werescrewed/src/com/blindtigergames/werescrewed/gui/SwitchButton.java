package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.TextButton.ButtonHandler;

public class SwitchButton extends Button{
	private ButtonHandler handler = null;

	/**
	 * Makes a new button instance for a switch 
	 * 
	 * @param caption
	 *            Message on the button
	 * @param font
	 *            Font for the message
	 * @param handler
	 *            What the button should do
	 * @param x
	 *            The x position in pixels
	 * @param y
	 *            The y position in pixels
	 */
	public SwitchButton( String caption, BitmapFont font, TextureRegion button,
			ButtonHandler handler,
			int x, int y ) {
		super( caption, font, button, x, y );
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		this.handler = handler;
		// calculateDimensions( );
	}
	
	/**
	 * Makes a new button instance for a switch 
	 * 
	 * @param caption
	 *            Message on the button
	 * @param font
	 *            Font for the message
	 * @param handler
	 *            What the button should do
	 */
	public SwitchButton( String caption, BitmapFont font, 
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
		//		.isTouched( ) ) )
		//		|| selected ) {
		if ( selected ) {
			selected = false; 
			handler.onClick( );
		}
	}
}
