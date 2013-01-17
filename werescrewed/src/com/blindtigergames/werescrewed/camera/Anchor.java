package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/*******************************************************************************
 * Movable anchors which the camera will work to keep within screen influenced
 * by priority. Priority is a function of position and weight
 * @author Edward Ramirez
 ******************************************************************************/
public class Anchor {
	protected Vector2 position;
	protected int weight;
	protected Vector2 buffer;
	
	static private final int DEFAULT_WEIGHT = 50;
	static private final Vector2 DEFAULT_BUFFER = new Vector2(256f, 256f);

	public Anchor(Vector2 setPosition) {
		this.position = setPosition;
		this.weight = DEFAULT_WEIGHT;
		this.buffer = DEFAULT_BUFFER;
	}
	
	public Anchor(Vector2 setPosition, int setWeight) {
		this.position = setPosition;
		this.weight = setWeight;
		this.buffer = DEFAULT_BUFFER;
	}
	
	public Anchor(Vector2 setPosition, int setWeight, int setBufferWidth) {
		this.position = setPosition;
		this.weight = setWeight;
		this.buffer = new Vector2(setBufferWidth, setBufferWidth);
	}
	
	public Anchor(Vector2 setPosition, int setWeight, Vector2 setBuffer) {
		this.position = setPosition;
		this.weight = setWeight;
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
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setBuffer(Vector2 buffer) {
		this.buffer = buffer;
	}
}
