package com.blindtigergames.werescrewed.entity.animator;

import com.blindtigergames.werescrewed.entity.I_Updateable;

public interface IAnimator extends I_Updateable {
	@Override
	public void update(float dT);
	public String getRegion();
	public void setRegion(String r);

	public int getFrameNumber();
	public void setFrameNumber(int f);
}
