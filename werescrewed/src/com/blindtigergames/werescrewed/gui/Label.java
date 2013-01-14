package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class Label {
	
	private static final Color COLOR = new Color(1f, 1f, 1f, 1f);
	
	private String caption = null;
	private BitmapFont font = null;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	
	public Label(String caption, BitmapFont font, int x, int y) {
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		calculateDimensions();
	}
	
	public Label(String caption, BitmapFont font) {
		this(caption, font, 0, 0);
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
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void draw(SpriteBatch batch) {
		Color originalColor = font.getColor();
		font.setColor(COLOR);
		font.draw(batch, caption, x, y);
		font.setColor(originalColor);
	}
	
	private void calculateDimensions() {
		TextBounds dimensions = font.getBounds(caption);
		width = Math.round(dimensions.width);
		height = Math.round(dimensions.height);
	}

}
