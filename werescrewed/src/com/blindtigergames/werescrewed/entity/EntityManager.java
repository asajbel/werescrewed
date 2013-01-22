package com.blindtigergames.werescrewed.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.blindtigergames.werescrewed.platforms.Skeleton;

public class EntityManager extends Entity{

	protected static HashMap< String, Entity > entityList = new HashMap< String, Entity >();
	protected static HashMap< String, Skeleton > skeletonList = new HashMap< String, Skeleton >();
	
	public EntityManager ( ) { }
	
	//Updates Entities and Skeletons stored in the HashMap
	public void updateEntity() {		
		Iterator< Map.Entry< String,Entity > > it = entityList.entrySet( ).iterator( );
		Map.Entry< String, Entity > derp;
		while ( it.hasNext( ) ) {
			derp = it.next( );
			derp.getValue( ).update( );
		}
		
		Iterator< Map.Entry< String, Skeleton> > jit = skeletonList.entrySet( ).iterator( );
		Map.Entry< String, Skeleton > herp;
		while ( jit.hasNext( ) ) {
			herp = jit.next( );
			herp.getValue( ).update( );
		}
	}
	
	//Adds an Entity to the HashMap
	public void addEntity ( String name, Entity type ) {
		entityList.put( name,  type );
	}
	
	//Adds a Skeleton to the HashMap
	public void addSkeleton ( String name, Skeleton type ) {
		skeletonList.put( name, type );
	}
	
	//Removes an Entity from the HashMap
	public void removeEntity ( String name ) {
		entityList.remove( name );
	}
	
	//Removes a Skeleton from the HashMap
	public void removeSkeleton ( String name ) {
		skeletonList.remove( name );
	}
	
}