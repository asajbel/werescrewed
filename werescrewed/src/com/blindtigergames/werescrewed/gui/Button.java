package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class Button {

	protected static final Color NORMAL_COLOR = new Color( 0.24f, 0.24f, 0.24f,
			1f );
	protected static final Color HOVER_COLOR = new Color( 0f, 0.5f, 0.8f, 1f );

	protected String caption = null;
	protected BitmapFont font = null;
	protected int x = 0;
	protected int y = 0;
	protected int width = 287; // width of button image
	protected int height = 92; // height of button image
	protected int capWidth = 0;
	protected int capHeight = 0;
	protected int smallCapWidth = 0;
	protected int smallCapHeight = 0;
	protected Rectangle bounds = null;
	protected boolean selected = false;
	protected boolean colored = false;
	protected Sprite box = null;
	protected boolean scaled = false; // Used to tell when button has hit
										// appropriate size
	protected int scaleSize = 35;
	protected int scaleX = width - scaleSize; // Used for scaling sprite
	protected int scaleY = height - scaleSize;
	protected float xPos = 0.5f * -scaleSize;
	protected float yPos = 0.5f * -scaleSize;
	//private boolean once = true;

	/**
	 * makes a new button instance
	 * 
	 * @param caption
	 *            String
	 * @param font
	 *            BitmapFont
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public Button( String caption, BitmapFont font, TextureRegion button, int x, int y ) {
		this.caption = caption;
		this.font = font;
		this.x = x;
		this.y = y;
		
		box = new Sprite( button );
		
		bounds = new Rectangle( x, y - height, width, height );
		calculateDimensions( );
	}

	/**
	 * makes a new button instance
	 * 
	 * @param caption
	 *            String
	 * @param font
	 *            BitmapFont
	 */
	public Button( String caption, BitmapFont font, TextureRegion button ) {
		this( caption, font, button, 0, 0 );
	}

	/**
	 * Gets phrase the button will say on screen
	 * 
	 * @return String
	 */
	public String getCaption( ) {
		return caption;
	}

	/**
	 * Changes phrase the button will say on screen
	 * 
	 * @param caption
	 *            String
	 */
	public void setCaption( String caption ) {
		this.caption = caption;
		calculateDimensions( );
	}

	public int getX( ) {
		return x;
	}

	public void setX( int x ) {
		this.x = x;
		bounds.x = x;
	}

	public int getY( ) {
		return y;
	}

	public void setY( int y ) {
		this.y = y;
		bounds.y = y - height * 2;
	}

	public int getWidth( ) {
		return width;
	}

	public int getHeight( ) {
		return height;
	}

	/**
	 * used to press button
	 * 
	 * @param value
	 *            boolean
	 */
	public void setSelected( boolean value ) {
		selected = value;
	}

	/**
	 * determines if button has been pressed
	 * 
	 * @return boolean
	 */
	public boolean isSelected( ) {
		return selected;
	}

	/**
	 * determines if button will be colored
	 * 
	 * @param value
	 *            boolean
	 */
	public void setColored( boolean value ) {
		colored = value;
		scaled = false;
	}

	/**
	 * determines if button will be colored
	 * 
	 * @return boolean
	 */
	public boolean isColored( ) {
		return colored;
	}

	public void draw( SpriteBatch batch, Camera camera ) {
		Color originalColor = font.getColor( );

		box.setPosition( x - xPos, y - height * 2 - yPos );
		if ( !scaled ) {
			setScale( );
		}
		box.setSize( scaleX, scaleY );
		box.setOrigin( box.getWidth( ) / 2, box.getHeight( ) / 2 );
		box.draw( batch );

		//font.setColor( colored ? HOVER_COLOR : NORMAL_COLOR );
		
		if ( colored ) {
			font.setColor( HOVER_COLOR );
			font.setScale( 1f );
			font.draw( batch, caption, x - capWidth / 2 + width / 2 + 5, y - height
					- capHeight / 2 );
		}
		else {
			font.setColor( NORMAL_COLOR );
			font.setScale( 0.5f );
			font.draw( batch, caption, x - smallCapWidth / 2 + width / 2 + 5, 
					y - height - smallCapHeight * 2 + 5 );
		}
		font.setColor( originalColor );
	}

	protected void setScale( ) {
		if ( colored ) {
			scaleX += 3;
			scaleY += 3;
			xPos += 1.5f;
			yPos += 1.5f;
			if ( scaleX >= ( width ) && scaleY >= ( height ) ) {
				scaled = true;
				scaleX = width;
				scaleY = height;
				xPos = 0;
				yPos = 0;
			}
		} else {
			scaleX--;
			scaleY--;
			xPos -= 0.5f;
			yPos -= 0.5f;
			if ( scaleX <= ( width - scaleSize ) && scaleY <= ( height - scaleSize ) ) {
				scaled = true;
				scaleX = width - scaleSize;
				scaleY = height - scaleSize;
				xPos = 0.5f * -scaleSize;
				yPos = 0.5f * -scaleSize;
			}
		}
	}

	protected void calculateDimensions( ) {
		font.setScale( 1f );
		TextBounds dimensions = font.getBounds( caption );
		capWidth = Math.round( dimensions.width );
		capHeight = Math.round( dimensions.height );
		
		smallCapWidth = Math.round( capWidth / 2 );
		smallCapHeight = Math.round( capHeight / 2 );
	}
}
