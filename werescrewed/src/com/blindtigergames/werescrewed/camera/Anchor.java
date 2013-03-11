package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.util.Util;

/*******************************************************************************
 * Anchors give the camera (via the AnchorList) a position and buffer to always
 * keep within the screen and to translate towards.
 * 
 * @author Edward Ramirez
 ******************************************************************************/
public class Anchor {
	public boolean special;
	protected Vector2 position;
	protected Vector2 positionBox;
	protected Vector2 buffer;
	protected boolean activated;
	// In steps
	protected int timer;
	static protected final Vector2 DEFAULT_BUFFER = new Vector2( 128f, 128f );

	/**
	 * Create a new Anchor
	 * 
	 * @param position
	 *            starting position of anchor (in pixels)
	 * 
	 */
	public Anchor( Vector2 position ) {
		this( false, position, DEFAULT_BUFFER );
	}

	/**
	 * Create a new Anchor
	 * 
	 * @param position
	 *            starting position of anchor (in pixels)
	 * @param buffer
	 *            buffer around anchor which must always stay within view
	 * 
	 */
	public Anchor( Vector2 position, Vector2 buffer ) {
		this( false, position, buffer );
	}

	/**
	 * Create a new Anchor
	 * 
	 * @param special
	 *            set true if this is a player anchor, false otherwise
	 * @param position
	 *            starting position of anchor (in pixels)
	 * @param buffer
	 *            buffer around anchor which must always stay within view
	 */
	public Anchor( boolean special, Vector2 position, Vector2 buffer ) {
		this.special = special;
		this.position = position;
		this.positionBox = new Vector2( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		this.buffer = buffer;
		this.activated = false;
		this.timer = 0;
	}

	/**
	 * get the buffer as a rectangle in pixels
	 * 
	 * @return
	 */
	public Rectangle getBufferRectangle( ) {
		return new Rectangle( position.x - buffer.x, position.y - buffer.y,
				2 * buffer.x, 2 * buffer.y );
	}

	/**
	 * set the position Vector2 in pixels
	 * 
	 * @param newPosition
	 *            in pixels
	 */
	public void setPosition( Vector2 newPosition ) {
		this.position.x = newPosition.x;
		this.position.y = newPosition.y;
		this.positionBox.x = newPosition.x * Util.PIXEL_TO_BOX;
		this.positionBox.y = newPosition.y * Util.PIXEL_TO_BOX;
	}

	/**
	 * set the position Vector2 in box units (meters)
	 * 
	 * @param newPosition
	 *            in meters
	 */
	public void setPositionBox( Vector2 newPosition ) {
		this.positionBox.x = newPosition.x;
		this.positionBox.y = newPosition.y;
		this.position.x = newPosition.x * Util.BOX_TO_PIXEL;
		this.position.y = newPosition.y * Util.BOX_TO_PIXEL;
	}

	/**
	 * set the buffer
	 * 
	 * @param buffer
	 *            a Vector2 of the buffer's width and height
	 */
	public void setBuffer( Vector2 buffer ) {
		this.buffer = buffer;
	}

	/**
	 * activate the anchor
	 */
	public void activate( ) {
		activated = true;
	}

	/**
	 * deactivate the anchor
	 */
	public void deactivate( ) {
		activated = false;
	}

	/**
	 * 
	 * @return time left in timer (in steps)
	 */
	public int getTimer( ) {
		return timer;
	}

	/**
	 * Decrease the timer by one.
	 */
	public void decrementTimer( ) {
		timer--;
	}

	/**
	 * Reset the timer.
	 * 
	 * @param timer New time
	 */
	public void setTimer( int timer ) {
		this.timer = timer;
	}
}
