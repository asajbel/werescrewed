package com.blindtigergames.werescrewed.entity.animator;

import com.blindtigergames.werescrewed.entity.I_Updateable;

public interface IAnimator extends I_Updateable {
	@Override
	public void update(float dT);
	
	//Returns the region specified by the animator.
	public String getRegion();
	
	//Sets the region specified by the animator.
	public void setRegion(String r);

	/*
	 * Gets and sets the index of the animator.
	 * 
	 * This maps to the internal index of the TextureAtlas, and thus
	 * it will usually be the current frame number, but NOT ALWAYS.
	 * The SimpleFrameAnimator, for instance, always returns index 0,
	 * because frames are represented by region name, not index.
	 * 
	 * If you want to mess with the internal frame number, use getFrame/setFrame
	 * instead.
	 */
	public int getIndex();
	public void setIndex(int i);

	
	/*
	 * Gets and sets the internal frame of the animator.
	 * 
	 * Use these INSTEAD OF getIndex/setIndex if you want to use the
	 * INTERNAL representation of frames.
	 */
	public int getFrame();
	public void setFrame(int i);
	
	public void reset();
}
