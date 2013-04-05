package com.blindtigergames.werescrewed.entity.animator;

import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.I_Drawable;

public interface ISpinemator extends I_Drawable {
	void update(float delta);
	
	void setPosition(Vector2 pos); 
	
	void setScale(Vector2 scale);
}
