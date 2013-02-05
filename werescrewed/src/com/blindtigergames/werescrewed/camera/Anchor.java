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
	
	static protected final Vector2 DEFAULT_BUFFER = new Vector2(128f, 128f);

	public Anchor(boolean setSpecial, Vector2 setPosition, Vector2 setBuffer) {
		this.special = setSpecial;
		this.position = setPosition;
		this.buffer = setBuffer;
	}
	
	public Rectangle getBufferRectangle() {
		return new Rectangle(position.x - (buffer.x / 2),
							position.y - (buffer.y / 2),
							buffer.x, buffer.y);
	}
	
	public void setPosition(Vector2 newPosition) {
		this.position = newPosition;
		this.positionBox.x = newPosition.x * Camera.PIXEL_TO_BOX;
		this.positionBox.y = newPosition.y * Camera.PIXEL_TO_BOX;
	}
	
	public void setPositionBox(Vector2 newPosition) {
		this.positionBox = newPosition;
		this.position.x = newPosition.x * Camera.BOX_TO_PIXEL;
		this.position.y = newPosition.y * Camera.BOX_TO_PIXEL;
	}
	
	public void setBuffer(Vector2 buffer) {
		this.buffer = buffer;
	}
}
