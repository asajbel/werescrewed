package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class OptionButton extends Button {
	
	private static final Color NORMAL_COLOR = new Color(1f, 1f, 1f, 0.7f);
	private static final Color HOVER_COLOR = new Color(0f, 128f, 255f, 1f);
	
	private OptionControl control = null;

	/**
	 * makes a new button instance
	 * 
	 * @param caption String
	 * @param font BitmapFont
	 * @param handler ButtonHandler
	 * @param x int
	 * @param y int
	 */
	public OptionButton(String caption, BitmapFont font, OptionControl control, int x, int y) {
		super( caption, font, x, y  );
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		this.control = control;
		calculateDimensions( );
	}
	
	/**
	 * makes a new button instance
	 * 
	 * @param caption String
	 * @param font BitmapFont
	 * @param handler ButtonHandler
	 */
	public OptionButton(String caption, BitmapFont font, OptionControl control ) {
		this(caption, font, control, 0, 0);
	}
	@Override
	public void setX(int x) {
		this.x = x;
		bounds.x = x;
		control.setX( x + 200 );
	}
	@Override
	public void setY(int y) {
		this.y = y;
		bounds.y = y - height;
		control.setY( y - height );
	}
	
	public OptionControl getOption ( ) {
		return control;
	}
	
	public void draw(SpriteBatch batch, Camera camera) {
		Color originalColor = font.getColor();
		Vector3 cursorPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(cursorPosition);
		boolean isIntersect = bounds.contains(cursorPosition.x, cursorPosition.y);
		box.setPosition( x , y - height - 20 );
		box.draw( batch );
		font.setColor(colored ? HOVER_COLOR : NORMAL_COLOR);
		font.draw(batch, caption, x, y);
		font.setColor(originalColor);
		control.draw( batch );
		if ((isIntersect && (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Buttons.LEFT))) || selected) {
			selected = false;
			control.setActive( true );
		}
	}
}
