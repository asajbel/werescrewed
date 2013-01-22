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
	}
	
	public void setBuffer(Vector2 buffer) {
		this.buffer = buffer;
	}
}
