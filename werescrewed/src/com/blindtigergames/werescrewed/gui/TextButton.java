package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	public TextButton( String caption, BitmapFont font, ButtonHandler handler,
			int x, int y ) {
		super( caption, font, x, y );
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
	public TextButton( String caption, BitmapFont font, ButtonHandler handler ) {
		this( caption, font, handler, 0, 0 );
	}

	public void draw( SpriteBatch batch, Camera camera ) {
		Color originalColor = font.getColor( );
		Vector3 cursorPosition = new Vector3( Gdx.input.getX( ),
				Gdx.input.getY( ), 0 );
		camera.unproject( cursorPosition );
		boolean isIntersect = bounds.contains( cursorPosition.x,
				cursorPosition.y );

		box.setPosition( x - xPos, y - height * 2 - yPos );
		if ( !scaled ) {
			setScale( );
		}
		box.setSize( scaleX, scaleY );
		box.setOrigin( box.getWidth( ) / 2, box.getHeight( ) / 2 );
		box.draw( batch );

		font.setColor( colored ? HOVER_COLOR : NORMAL_COLOR );
		font.draw( batch, caption, x - capWidth / 2 + width / 2 + 5, y - height
				- capHeight / 2 );
		font.setColor( originalColor );

		if ( ( isIntersect && !WereScrewedGame.isMouseClicked( ) && ( Gdx.input
				.isTouched( ) || Gdx.input.isButtonPressed( Buttons.LEFT ) ) )
				|| selected ) {
			selected = false;
			handler.onClick( );
		}
	}

	public static interface ButtonHandler {
		public void onClick( );

	}

}
