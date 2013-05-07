package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class TextButton extends Button {
	
	private static final Color NORMAL_COLOR = new Color(1f, 1f, 1f, 0.7f);
	private static final Color HOVER_COLOR = new Color(0f, 128f, 255f, 1f);
	
	private static TextureRegion screwTex = WereScrewedGame.manager
			.getAtlas( "common-textures" ).findRegion( "flat_head_circular" );
	private Sprite screwL = new Sprite( screwTex );
	private Sprite screwR = new Sprite( screwTex );
	private float screwSize = screwL.getHeight( );
	private ButtonHandler handler = null;
	
	/**
	 * makes a new button instance
	 * 
	 * @param caption String
	 * @param font BitmapFont
	 * @param handler ButtonHandler
	 * @param x int
	 * @param y int
	 */
	public TextButton( String caption, BitmapFont font, ButtonHandler handler, int x, int y ) {
		super( caption, font, x, y );
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		this.handler = handler;
		calculateDimensions( );
	}
	
	/**
	 * makes a new button instance
	 * 
	 * @param caption String
	 * @param font BitmapFont
	 * @param handler ButtonHandler
	 */
	public TextButton( String caption, BitmapFont font, ButtonHandler handler ) {
		this( caption, font, handler, 0, 0 );
	}
	
	public void draw(SpriteBatch batch, Camera camera) {
		Color originalColor = font.getColor();
		Vector3 cursorPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(cursorPosition);
		boolean isIntersect = bounds.contains(cursorPosition.x, cursorPosition.y);
		font.setColor(colored ? HOVER_COLOR : NORMAL_COLOR);
		font.draw(batch, caption, x, y);
		font.setColor(originalColor);
		//Screw Adjustments
		screwL.setPosition( x - 50, y - height - 10 );
		screwR.setPosition( x + width + 10, y - height - 10 );
		screwL.setSize( screwSize / 2, screwSize / 2 );
		screwR.setSize( screwSize / 2, screwSize / 2 );
		screwL.setOrigin( screwL.getWidth( ) / 2, screwL.getHeight( ) / 2 );
		screwR.setOrigin( screwR.getWidth( ) / 2, screwR.getHeight( ) / 2 );
		if ( colored ) {
			screwL.draw( batch );
			screwR.draw( batch );
			screwL.rotate( 5.0f );
			screwR.rotate( 5.0f );
		}
		if ((isIntersect && (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Buttons.LEFT))) || selected) {
			selected = false;
			handler.onClick();
		}
	}
	
	public static interface ButtonHandler {
		public void onClick();
		
	}
	
}
