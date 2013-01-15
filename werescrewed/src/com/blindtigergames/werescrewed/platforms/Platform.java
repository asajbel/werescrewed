package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;

public class Platform extends Entity{
	
	//List of Structural Screws
	//List of joints
	int width, height;
	IMover mover;
	
	public Platform(String n, Vector2 pos, Texture tex)
	{
		super(n, pos, tex);
	}
	
	public void setMover(IMover _mover){
		this.mover = _mover;
	}
	
}