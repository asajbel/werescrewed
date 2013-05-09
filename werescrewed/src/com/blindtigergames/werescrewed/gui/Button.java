package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Button {
	
	private static final Color NORMAL_COLOR = new Color(1f, 1f, 1f, 0.7f);
	private static final Color HOVER_COLOR = new Color(0f, 128f, 255f, 1f);
	
	protected String caption = null;
	protected BitmapFont font = null;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected Rectangle bounds = null;
	protected boolean selected = false;
	protected boolean colored = false;
	protected Sprite box = null;

	/**
	 * makes a new button instance
	 * 
	 * @param caption String
	 * @param font BitmapFont
	 * @param handler ButtonHandler
	 * @param x int
	 * @param y int
	 */
	public Button( String caption, BitmapFont font, int x, int y ) {
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		Texture back = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/menu/button.png", Texture.class );
		box = new Sprite( back );
		calculateDimensions( );
	}
	
	/**
	 * makes a new button instance
	 * 
	 * @param caption String
	 * @param font BitmapFont
	 * @param handler ButtonHandler
	 */
	public Button( String caption, BitmapFont font ) {
		this( caption, font, 0, 0 );
	}
	
	/**
	 * Gets phrase the button will say on screen
	 * @return String
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Changes phrase the button will say on screen
	 * 
	 * @param caption String
	 */
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
	
	/**
	 * used to press button
	 * 
	 * @param value boolean
	 */
	public void setSelected( boolean value ){
		selected = value;
	}
	
	/**
	 * determines if button has been pressed
	 * 
	 * @return boolean
	 */
	public boolean isSelected(){
		return selected;
	}
	
	/**
	 * determines if button will be colored
	 * 
	 * @param value boolean
	 */
	public void setColored( boolean value ){
		colored = value;
	}
	
	/**
	 * determines if button will be colored
	 * 
	 * @return boolean
	 */
	public boolean isColored(){
		return colored;
	}
	
	public void draw(SpriteBatch batch, Camera camera) {
		Color originalColor = font.getColor();
		Vector3 cursorPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(cursorPosition);
		boolean isIntersect = bounds.contains(cursorPosition.x, cursorPosition.y);
		font.setColor(colored ? HOVER_COLOR : NORMAL_COLOR);
		font.draw(batch, caption, x, y);
		font.setColor(originalColor);
		box.draw( batch );
	}
	
	protected void calculateDimensions() {
		TextBounds dimensions = font.getBounds(caption);
		width = Math.round(dimensions.width);
		height = Math.round(dimensions.height);
		bounds = new Rectangle(x, y - height, width, height);
		
	}	
}
