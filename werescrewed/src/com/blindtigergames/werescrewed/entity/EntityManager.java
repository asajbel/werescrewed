package com.blindtigergames.werescrewed.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.blindtigergames.werescrewed.entity.Skeleton;

public class EntityManager extends Entity{

	protected static HashMap< String, Entity > entityList = new HashMap< String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonList = new HashMap< String, Skeleton >( );
	protected static HashMap< String, Entity > entitiesToAdd = new HashMap < String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonsToAdd = new HashMap < String, Skeleton >( );
	protected static HashMap < String, Entity > entitiesToRemove = new HashMap < String, Entity >( );
	protected static HashMap < String, Skeleton > skeletonsToRemove = new HashMap < String, Skeleton >( );


	
	public EntityManager ( ) { }
	
	//Updates Entities and Skeletons stored in the HashMap
	public void updateEntity( float deltaTime ) {		
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
		Map.Entry < String, Entity > entityToRemove;
		while ( itRemove.hasNext( ) ) {
			entityToRemove = itRemove.next( );
			entityList.remove( entityToRemove.getKey( ) );
		}
		entitiesToRemove.clear( );
		
		Iterator < Map.Entry < String, Skeleton > > jitRemove = skeletonsToRemove.entrySet( ). iterator( );
		Map.Entry < String, Skeleton > skeletonToRemove;
		while ( jitRemove.hasNext( ) ) {
			skeletonToRemove = jitRemove.next( );
			skeletonList.remove( skeletonToRemove.getKey( ) );
		}
		skeletonsToRemove.clear( );
		
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
		entitiesToRemove.put( name,  type );
	//	entityList.remove( name );
	}
	
	//Removes a Skeleton from the HashMap
	public void removeSkeleton ( String name, Skeleton type ) {
		skeletonsToRemove.put( name,  type );
	//	skeletonList.remove( name );
	}

}