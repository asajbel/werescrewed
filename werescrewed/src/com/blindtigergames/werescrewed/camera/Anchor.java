package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/*******************************************************************************
 * Movable anchors which the camera will work to keep within screen influenced
 * by priority. Priority is a function of position and weight
 * @author Edward Ramirez
 ******************************************************************************/
public class Anchor {
	public boolean special;
	protected Vector2 position;
	protected Vector2 positionBox;
	protected Vector2 buffer;
	protected boolean activated;
	
	static protected final Vector2 DEFAULT_BUFFER = new Vector2(128f, 128f);

	/**
	 * Create a new Anchor
	 * @param setPosition starting position of anchor
	 */
	public Anchor(Vector2 setPosition) {
		this(false, setPosition, DEFAULT_BUFFER);
	}
	
	/**
	 * Create a new Anchor
	 * @param setPosition starting position of anchor
	 * @param setBuffer buffer around anchor which must always stay within view
	 */
	public Anchor(Vector2 setPosition, Vector2 setBuffer) {
		this( false, setPosition, setBuffer );
	}
	
	/**
	 * Create a new Anchor
	 * @param setSpecial set true if this is a player anchor, false otherwise
	 * @param setPosition starting position of anchor
	 * @param setBuffer buffer around anchor which must always stay within view
	 */
	public Anchor(boolean setSpecial, Vector2 setPosition, Vector2 setBuffer) {
		this.special = setSpecial;
		this.position = setPosition;
		this.positionBox = new Vector2(setPosition.x * Camera.PIXEL_TO_BOX, setPosition.y * Camera.PIXEL_TO_BOX);
		this.buffer = setBuffer;
		this.activated = false;
	}
	
	public Rectangle getBufferRectangle() {
		return new Rectangle(position.x - (buffer.x / 2),
							position.y - (buffer.y / 2),
							buffer.x, buffer.y);
	}
	
	public void setPosition(Vector2 newPosition) {
		this.position.x = newPosition.x;
		this.position.y = newPosition.y;
		this.positionBox.x = newPosition.x * Camera.PIXEL_TO_BOX;
		this.positionBox.y = newPosition.y * Camera.PIXEL_TO_BOX;
	}
	
	public void setPositionBox(Vector2 newPosition) {
		this.positionBox.x = newPosition.x;
		this.positionBox.y = newPosition.y;
		this.position.x = newPosition.x * Camera.BOX_TO_PIXEL;
		this.position.y = newPosition.y * Camera.BOX_TO_PIXEL;
	}
	
	public void setBuffer(Vector2 buffer) {
		this.buffer = buffer;
	}
	
	public void activate() {
		activated = true;
	}
	
	public void deactivate() {
		activated = false;
	}
}
