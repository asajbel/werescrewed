package com.blindtigergames.werescrewed.gui;

import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class OptionControl {

	protected int minValue = 0;
	protected int maxValue = 0;
	protected int curValue = 0;
	protected int x = 0;
	protected int y = 0;
	protected float alpha = 1.0f;
	protected boolean activated = false;

	protected static Texture tex = null;

	public OptionControl( int min, int max, int current, int x, int y ) {
		this.minValue = min;
		this.maxValue = max;
		this.curValue = current;
		this.x = x;
		this.y = y;
	}

	public OptionControl( int min, int max, int current ) {
		this( min, max, current, 0, 0 );
	}

	public int getMinValue( ) {
		return minValue;
	}

	public void setMinValue( int value ) {
		minValue = value;
	}

	public int getMaxValue( ) {
		return maxValue;
	}

	public void setMaxValue( int value ) {
		maxValue = value;
	}

	public int getCurrentValue( ) {
		return curValue;
	}

	public void setCurrentValue( int value ) {
		curValue = value;
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

	public boolean isActive( ) {
		return activated;
	}

	public void setActive( boolean value ) {
		activated = value;
	}

	public void draw( SpriteBatch batch ) {

	}
}
