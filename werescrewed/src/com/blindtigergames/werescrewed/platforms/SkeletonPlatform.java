package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @param name blah blah
 * 
 * @author Ranveer
 *
 */

public class SkeletonPlatform extends Platform{
	
	public ArrayList<Platform> platList;
	
	public SkeletonPlatform(String n, Vector2 pos, Texture tex, World world, Platform... platforms ){
		super( n, pos, tex , null);
		this.world = world;
		platList = new ArrayList<Platform>();
		for(Platform p : platforms)
			platList.add(p);
	}
	
	public void constructSkeleton(){
		
	}
}