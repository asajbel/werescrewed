package com.blindtigergames.werescrewed.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class EntityManager extends Entity{

	protected static HashMap< String, Entity > entityList = new HashMap< String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonList = new HashMap< String, Skeleton >( );
	protected static HashMap< String, Entity > entitiesToAdd = new HashMap < String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonsToAdd = new HashMap < String, Skeleton >( );
	protected static HashMap < String, Entity > entitiesToRemove = new HashMap < String, Entity >( );
	protected static HashMap < String, Skeleton > skeletonsToRemove = new HashMap < String, Skeleton >( );


	// Updates Entities and Skeletons stored in the HashMap
	@Override
	public void update( float deltaTime ) {
		Iterator< Map.Entry< String, Entity > > eit = entityList.entrySet( )
				.iterator( );
		Map.Entry< String, Entity > eEntry;
		while ( eit.hasNext( ) ) {
			eEntry = eit.next( );
			eEntry.getValue( ).update( deltaTime );
		}

		Iterator< Map.Entry< String, Skeleton > > sit = skeletonList.entrySet( )
				.iterator( );
		Map.Entry< String, Skeleton > sEntry;
		while ( sit.hasNext( ) ) {
			sEntry = sit.next( );
			sEntry.getValue( ).update( deltaTime );
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
	//		entityToRemove.world.destroyBody ( entityToRemove.body );
			System.out.println("DESTROYING BODY");
		}
		entitiesToRemove.clear( );
		
		Iterator < Map.Entry < String, Skeleton > > jitRemove = skeletonsToRemove.entrySet( ). iterator( );
		Map.Entry < String, Skeleton > entrySkelToRemove;
		while ( jitRemove.hasNext( ) ) {
			entrySkelToRemove = jitRemove.next( );
			Skeleton skeletonToRemove = entrySkelToRemove.getValue();
	//		skeletonToRemove.world.destroyBody ( skeletonToRemove.body );
			skeletonList.remove( entrySkelToRemove.getKey( ) );
		}
		skeletonsToRemove.clear( );
		
	}
	
	//Adds an Entity to the HashMap
	public void addEntity ( String name, Entity type ) {
		entitiesToAdd.put( name,  type );
	
	@Override
	public void draw(SpriteBatch batch){
		Iterator< Map.Entry< String, Entity > > eit = entityList.entrySet( )
				.iterator( );
		Map.Entry< String, Entity > eEntry;
		while ( eit.hasNext( ) ) {
			eEntry = eit.next( );
			eEntry.getValue( ).draw( batch );
		}

		Iterator< Map.Entry< String, Skeleton > > sit = skeletonList.entrySet( )
				.iterator( );
		Map.Entry< String, Skeleton > sEntry;
		while ( sit.hasNext( ) ) {
			sEntry = sit.next( );
			sEntry.getValue( ).draw( batch );
		}
	}

	// Adds an Entity to the HashMap
	public void addEntity( String name, Entity type ) {
		entityList.put( name, type );
	}
	
	//Adds a Skeleton to the HashMap
	public void addSkeleton ( String name, Skeleton type ) {
		skeletonsToAdd.put( name, type );
	}
	
	//Removes an Entity from the HashMap
	public void removeEntity ( String name, Entity type ) {
		System.out.println ("ADDING TO REMOVE LIST");
		entitiesToRemove.put( name,  type );
	//	entityList.remove( name );
	}
	
	//Removes a Skeleton from the HashMap
	public void removeSkeleton ( String name, Skeleton type ) {
		skeletonsToRemove.put( name,  type );
	//	skeletonList.remove( name );
	}

}