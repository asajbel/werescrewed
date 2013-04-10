package com.blindtigergames.werescrewed.entity.animator;

/**
 * Interface for Spine based animations
 * 
 * @author Anders Sajbel
 */
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.I_Drawable;

public interface ISpinemator extends I_Drawable {
	/**
	 * Update Function for spine animations
	 * 
	 * @param delta
	 *            The time difference between updates
	 */
	void update( float delta );

	/**
	 * Sets the position of a spine animation
	 * 
	 * @param pos
	 *            Two dimensional vector of floats in pixels to place spine
	 *            skeleton
	 */
	void setPosition( Vector2 pos );

	/**
	 * Sets the scale of a spine animation
	 * 
	 * @param scale
	 *            Two dimensional vector of floats that is the factor to scale
	 *            the skeleton
	 */
	void setScale( Vector2 scale );
}
