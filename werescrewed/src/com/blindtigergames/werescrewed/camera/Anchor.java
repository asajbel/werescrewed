package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.util.Util;

/*******************************************************************************
 * Anchors give the camera (via the AnchorList) a position and buffer to always
 * keep within the screen and to translate towards.
 * 
 * @author Edward Ramirez and Dan Malear
 ******************************************************************************/
public class Anchor {
	protected Vector2 position;
	protected Vector2 positionBox;
	protected Vector2 offset;
	protected Vector2 offsetBox;
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
		this( position, new Vector2( 0, 0 ), DEFAULT_BUFFER );
	}

	/**
	 * Create a new Anchor
	 * 
	 * @param position
	 *            starting position of anchor (in pixels)
	 * 
	 */
	public Anchor( Vector2 position, Vector2 offset ) {
		this( position, offset, DEFAULT_BUFFER );
	}

	/**
	 * Create a new Anchor
	 * 
	 * @param position
	 *            starting position of anchor (in pixels)
	 * @param buffer
	 *            buffer around anchor which must always stay within view
	 */
	public Anchor( Vector2 position, Vector2 offset, Vector2 buffer ) {
		this.offset = offset;
		this.position = position.add( this.offset );
		this.positionBox = new Vector2( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		this.offsetBox = new Vector2( offset.x * Util.PIXEL_TO_BOX, offset.y
				* Util.PIXEL_TO_BOX );
		this.positionBox = this.positionBox.add( this.offsetBox );
		this.buffer = buffer;
		this.activated = false;
		this.timer = -1;
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
		this.position.x = newPosition.x + this.offset.x;
		this.position.y = newPosition.y + this.offset.y;
		this.positionBox.x = newPosition.x * Util.PIXEL_TO_BOX + this.offsetBox.x;
		this.positionBox.y = newPosition.y * Util.PIXEL_TO_BOX + this.offsetBox.y;
	}

	/**
	 * set the position Vector2 in box units (meters)
	 * 
	 * @param newPosition
	 *            in meters
	 */
	public void setPositionBox( Vector2 newPosition ) {
		this.positionBox.x = newPosition.x + this.offsetBox.x;
		this.positionBox.y = newPosition.y + this.offsetBox.y;
		this.position.x = newPosition.x * Util.BOX_TO_PIXEL + this.offset.x;
		this.position.y = newPosition.y * Util.BOX_TO_PIXEL + this.offset.y;
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
	 * @param timer
	 *            New time
	 */
	public void setTimer( int timer ) {
		this.timer = timer;
	}

	/**
	 * Set the offset from the center of parent object
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffset( float x, float y ) {
		this.offset.x = x;
		this.offset.y = y;
	}
}
