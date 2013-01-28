package com.blindtigergames.werescrewed.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityManager {

	protected static HashMap < String, Entity > entityList = new HashMap< String, Entity >( );
	protected static HashMap < String, Skeleton > skeletonList = new HashMap< String, Skeleton >( );
	protected static HashMap < String, Entity > entitiesToAdd = new HashMap < String, Entity >( );
	protected static HashMap < String, Skeleton > skeletonsToAdd = new HashMap < String, Skeleton >( );
	protected static HashMap < String, Entity > entitiesToRemove = new HashMap < String, Entity >( );
	protected static HashMap < String, Skeleton > skeletonsToRemove = new HashMap < String, Skeleton >( );


	
	public EntityManager ( ) { }
	
	//Updates Entities and Skeletons stored in the HashMap
	public void updateEntity( float deltaTime ) {		
		Iterator< Map.Entry< String,Entity > > it = entityList.entrySet( ).iterator( );
		Map.Entry< String, Entity > entityToUpdate;
		while ( it.hasNext( ) ) {
			entityToUpdate = it.next( );
			entityToUpdate.getValue( ).update( deltaTime );
		}
		
		Iterator< Map.Entry< String, Skeleton> > jit = skeletonList.entrySet( ).iterator( );
		Map.Entry< String, Skeleton > skeletonToUpdate;
		while ( jit.hasNext( ) ) {
			skeletonToUpdate = jit.next( );
			skeletonToUpdate.getValue( ).update( deltaTime );
		}
		
		Iterator < Map.Entry < String, Entity > > itAdd = entitiesToAdd.entrySet( ).iterator( );
		Map.Entry < String, Entity > entityToAdd;
		while ( itAdd.hasNext( ) ) {
			entityToAdd = itAdd.next( );
			entityList.put( entityToAdd.getKey( ), entityToAdd.getValue( ) );
		}
		entitiesToAdd.clear( );
		
		Iterator < Map.Entry < String, Skeleton > > jitAdd = skeletonsToAdd.entrySet( ).iterator( );
		Map.Entry < String, Skeleton > skeletonToAdd;
		while ( jitAdd.hasNext( ) ) {
			skeletonToAdd = jitAdd.next( );
			skeletonList.put( skeletonToAdd.getKey( ), skeletonToAdd.getValue( ) );
		}
		skeletonsToAdd.clear( );
		
		Iterator < Map.Entry < String, Entity > > itRemove = entitiesToRemove.entrySet( ). iterator( );
		Map.Entry < String, Entity > entryToRemove;
		while ( itRemove.hasNext( ) ) {
			entryToRemove = itRemove.next( );
			Entity entityToRemove = entryToRemove.getValue( );
			entityList.remove( entryToRemove.getKey( ) );
			System.out.println("class com.blindtigergames.werescrewed.entity.EntityManager: DESTROYING ENTITY BODY");
			entityToRemove.world.destroyBody ( entityToRemove.body );
		}
		entitiesToRemove.clear( );
		
		Iterator < Map.Entry < String, Skeleton > > jitRemove = skeletonsToRemove.entrySet( ). iterator( );
		Map.Entry < String, Skeleton > entrySkelToRemove;
		while ( jitRemove.hasNext( ) ) {
			entrySkelToRemove = jitRemove.next( );
			Skeleton skeletonToRemove = entrySkelToRemove.getValue();
			skeletonToRemove.world.destroyBody ( skeletonToRemove.body );
			System.out.println("class com.blindtigergames.werescrewed.entity.EntityManager: DESTROYING SKELETON BODY");
			skeletonList.remove( entrySkelToRemove.getKey( ) );
		}
		skeletonsToRemove.clear( );
		
	}
	
	//Adds an Entity to the HashMap
	public void addEntity ( String name, Entity type ) {
		entitiesToAdd.put( name,  type );
	}
	
	//Adds a Skeleton to the HashMap
	public void addSkeleton ( String name, Skeleton type ) {
		skeletonsToAdd.put( name, type );
	}
	
	//Removes an Entity from the HashMap
	public void removeEntity ( String name, Entity type ) {
		System.out.println("class com.blindtigergames.werescrewed.entity.EntityManager: adding entity " + name + " to remove list");
		entitiesToRemove.put( name,  type );
	//	entityList.remove( name );
	}
	
	//Removes a Skeleton from the HashMap
	public void removeSkeleton ( String name, Skeleton type ) {
		System.out.println("class com.blindtigergames.werescrewed.entity.EntityManager: adding skeleton " + name + " to remove list");
		skeletonsToRemove.put( name,  type );
	//	skeletonList.remove( name );
	}

}