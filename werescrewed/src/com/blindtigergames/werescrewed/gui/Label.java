package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class Label {

	//@SuppressWarnings( "unused" )
	//private static final Color COLOR = new Color( 1f, 1f, 1f, 1f );

	private String caption = null;
	private BitmapFont font = null;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private Color originalColor, selectedColor;
	private boolean selected = false;

	public Label( String caption, BitmapFont font, int x, int y ) {
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		calculateDimensions( );
		originalColor = font.getColor( );
		selectedColor = new Color( );
		selectedColor.add( Color.GREEN );
	}

	public Label( String caption, BitmapFont font ) {
		this( caption, font, 0, 0 );
	}

	public String getCaption( ) {
		return caption;
	}

	public void setCaption( String caption ) {
		this.caption = caption;
		calculateDimensions( );
	}

	public int getX( ) {
		return x;
	}

	public void setX( int x ) {
		this.x = x;
	}

	public int getY( ) {
		return y;
	}

	public void setY( int y ) {
		this.y = y;
	}

	public int getWidth( ) {
		return width;
	}

	public int getHeight( ) {
		return height;
	}

	public void select( ) {
		this.selected = true;
	}

	public void unselect( ) {
		this.selected = false;
	}

	public void draw( SpriteBatch batch ) {
		// font.setColor(COLOR);

		if ( selected )
			font.setColor( selectedColor );
		else
			font.setColor( originalColor );

		font.draw( batch, caption, x, y );
	}

	private void calculateDimensions( ) {
		TextBounds dimensions = font.getBounds( caption );
		width = Math.round( dimensions.width );
		height = Math.round( dimensions.height );
	}

	public void setText( String newText ) {
		caption = newText;
	}

}
