/**
 * 
 */
package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Kevin
 *	
 * @notes Right now, this class is identical to the regular sprite class. I'm, just using this as a placeholder until
 * 		  we want to implement animations.
 */

public class AnimatedSprite extends Sprite implements I_Updateable, I_Drawable {

	/**
	 * 
	 */
	public AnimatedSprite() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * @param texture
	 */
	public AnimatedSprite(Texture texture) {
		super(texture);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param region
	 */
	public AnimatedSprite(TextureRegion region) {
		super(region);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sprite
	 */
	public AnimatedSprite(Sprite sprite) {
		super(sprite);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param texture
	 * @param srcWidth
	 * @param srcHeight
	 */
	public AnimatedSprite(Texture texture, int srcWidth, int srcHeight) {
		super(texture, srcWidth, srcHeight);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param texture
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 */
	public AnimatedSprite(Texture texture, int srcX, int srcY, int srcWidth,
			int srcHeight) {
		super(texture, srcX, srcY, srcWidth, srcHeight);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param region
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 */
	public AnimatedSprite(TextureRegion region, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		super(region, srcX, srcY, srcWidth, srcHeight);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(float dT) {
		// TODO Auto-generated method stub
		
	}


}
