package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/*******************************************************************************
 * Moveable anchors which the camera will work to keep within screen influenced
 * by priority. Priority is a function of position and weight
 * @author Edward Ramirez
 ******************************************************************************/
public class Anchor {
	public Vector3 position;
	public float weight;
	public float priority;
	public Rectangle buffer;

	public Anchor(Vector3 setPosition, float setWeight, Rectangle setBuffer) {
		this.position = setPosition;
		this.weight = setWeight;
		this.buffer = setBuffer;
	}
}
