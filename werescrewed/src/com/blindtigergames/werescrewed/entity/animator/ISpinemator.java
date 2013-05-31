package com.blindtigergames.werescrewed.entity.animator;

/**
 * Interface for Spine based animations
 * 
 * @author Anders Sajbel
 */
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.I_Drawable;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.esotericsoftware.spine.SkeletonData;

public interface ISpinemator extends I_Drawable {
	/**
	 * Update Function for spine animations.
	 * 
	 * @param delta
	 *            The time difference between updates
	 */
	void update( float delta );

	/**
	 * Sets the position of a spine animation.
	 * 
	 * @param pos
	 *            Two dimensional vector of floats in pixels to place spine
	 *            skeleton
	 */
	void setPosition( Vector2 pos );

	/**
	 * Sets the position of a spine animation.
	 * 
	 * @param x
	 *            x position in pixels to place spine skeleton
	 * @param y
	 *            y position in pixels to place spine skeleton
	 */
	void setPosition( float x, float y );

	/**
	 * Sets the scale of a spine animation
	 * 
	 * @param scale
	 *            Two dimensional vector of floats that is the factor to scale.
	 *            the skeleton
	 */
	void setScale( Vector2 scale );

	/**
	 * Returns atlas with all of this spine body's parts.
	 * 
	 * @return TextureAtlas
	 */
	TextureAtlas getBodyAtlas( );

	/**
	 * Returns the position of the root bone relative to the world in pixels.
	 * 
	 * @return Vector2
	 * 
	 */
	Vector2 getPosition( );

	/**
	 * Returns the x position of the root bone relative to the world in pixels.
	 * 
	 * @return float
	 */
	float getX( );

	/**
	 * Returns the y position of the root bone relative to the world in pixels.
	 * 
	 * @return float
	 */
	float getY( );

	/**
	 * Flips the animation along the x axis.
	 * 
	 * @param flipX
	 */
	void flipX( boolean flipX );

	/**
	 * Flips the animation along the y axis.
	 * 
	 * @param flipY
	 */
	void flipY( boolean flipY );

	/**
	 * Change the animation for the Spinemator.
	 * 
	 * @param animName
	 *            Name of the animation in the spine project
	 * @param loop
	 *            True if the animation should loop.
	 */
	void changeAnimation( String animName, boolean loop );
	
	/**
	 * Returns the current animation name
	 * 
	 * @return Name of the current animation
	 */
	String getCurrentAnimation( );
	
	/**
	 * Returns the duration of the current animation
	 * 
	 * @return 
	 */
	float getAnimationDuration( ); 
	
	/**
	 * Sets the rotation of the spine
	 * 
	 * @param angle
	 */
	void setRotation( float angle );
	
	/**
	 * Returns the skeleton data of the spine animation
	 * 
	 * @return SkeletonData
	 */
	SkeletonData getSkeletonData( );
	
}
