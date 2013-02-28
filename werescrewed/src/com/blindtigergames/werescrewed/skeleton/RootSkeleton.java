package com.blindtigergames.werescrewed.skeleton;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;

public class RootSkeleton extends Skeleton {

	private ArrayList<Entity> looseEntity;
	
	public RootSkeleton( String name, Vector2 positionPix, Texture tex, World world ) {
		super( name, positionPix, tex, world );
		looseEntity = new ArrayList< Entity >();
	}
	
	 @Override
	 public void update( float deltaTime ) {
		 
		//update root skeleton imover
	        updateMover( deltaTime );
	        //followed by children skeleton imovers
	        updateChildSkeletonMovers( deltaTime );
	        //update all children platform IMovers on their imover local coord system
	        updateEntityMovers( deltaTime );
	        
	        //recursively move all children skeletons by this moved updated pos*rot.
	        setPosRotChildSkeletons( deltaTime );
	        
	        //Now we can rotate all kinematic entities connected by updated skeleton rot / position
	        //setPosRotAllKinematicPlatforms(deltaTime);
	        
	        //Update children animations and stuff
	        updateChildren( deltaTime );
		 
		 for ( Entity entity : looseEntity ){
			 entity.update( deltaTime );
		 }
		 super.update( deltaTime );
	 }

    @Override
    public void draw( SpriteBatch batch ){
    	//possible bug: the draw order
    	for ( Entity entity : looseEntity ){
    		entity.draw( batch );
    	}
    	super.draw( batch );
    }
}
