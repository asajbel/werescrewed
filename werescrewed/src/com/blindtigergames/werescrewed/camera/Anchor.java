package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/*******************************************************************************
 * Movable anchors which the camera will work to keep within screen influenced
 * by priority. Priority is a function of position and weight
 * @author Edward Ramirez
 ******************************************************************************/
public class Anchor {
	protected Vector2 position;
	protected int weight;
	protected int bufferWidth;
	protected int bufferHeight;
	
	static private final int DEFAULT_WEIGHT = 50;
	static private final int DEFAULT_BUFFER = 256;

	public Anchor(Vector2 setPosition) {
		this.position = setPosition;
		this.weight = DEFAULT_WEIGHT;
		this.bufferWidth = DEFAULT_BUFFER;
		this.bufferHeight = DEFAULT_BUFFER;
	}
	
	public Anchor(Vector2 setPosition, int setWeight, int setBufferWidth) {
		this.position = setPosition;
		this.weight = setWeight;
		this.bufferWidth = setBufferWidth;
		this.bufferHeight = setBufferWidth;
	}
	
	public Anchor(Vector2 setPosition, int setWeight, int setBufferWidth, int setBufferHeight) {
		this.position = setPosition;
		this.weight = setWeight;
		this.bufferWidth = setBufferWidth;
		this.bufferHeight = setBufferHeight;
	}
	
	public Rectangle getBufferRectangle() {
		return new Rectangle(position.x - (bufferWidth / 2),
							position.y - (bufferHeight / 2),
							bufferWidth, bufferHeight);
	}
	
	public void setPosition(Vector2 newPosition) {
		this.position = newPosition;
	}
}
