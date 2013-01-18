package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Button {
	
	private static final Color NORMAL_COLOR = new Color(1f, 1f, 1f, 0.7f);
	private static final Color HOVER_COLOR = new Color(0f, 1f, 0f, 1f);
	
	private String caption = null;
	private BitmapFont font = null;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private Rectangle bounds = null;
	private ButtonHandler handler = null;

	public Button(String caption, BitmapFont font, ButtonHandler handler, int x, int y) {
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		this.handler = handler;
		calculateDimensions();
	}
	
	public Button(String caption, BitmapFont font, ButtonHandler handler) {
		this(caption, font, handler, 0, 0);
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
		calculateDimensions();
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
		bounds.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
		bounds.y = y - height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void draw(SpriteBatch batch, Camera camera) {
		Color originalColor = font.getColor();
		Vector3 cursorPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(cursorPosition);
		boolean isIntersect = bounds.contains(cursorPosition.x, cursorPosition.y);
		font.setColor(isIntersect ? HOVER_COLOR : NORMAL_COLOR);
		font.draw(batch, caption, x, y);
		font.setColor(originalColor);
		if (isIntersect && (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Buttons.LEFT))) {
			handler.onClick();
		}
	}
	
	private void calculateDimensions() {
		TextBounds dimensions = font.getBounds(caption);
		width = Math.round(dimensions.width);
		height = Math.round(dimensions.height);
		bounds = new Rectangle(x, y - height, width, height);
	}
	
	public static interface ButtonHandler {
		
		public void onClick();
		
	}
	
}
