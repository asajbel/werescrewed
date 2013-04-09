package com.blindtigergames.werescrewed.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

/**
 * Stores a list of entities and skeletons for purposes of updating and deleting
 * them.
 * 
 * @author Edward Boning
 * @author Jennifer Makaiwi
 * 
 */

public class EntityManager {
	// Dear god, too many HashMaps!
	protected static HashMap< String, Entity > entityList = new HashMap< String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonList = new HashMap< String, Skeleton >( );
	protected static HashMap< String, Entity > entitiesToAdd = new HashMap< String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonsToAdd = new HashMap< String, Skeleton >( );
	protected static HashMap< String, Entity > entitiesToRemove = new HashMap< String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonsToRemove = new HashMap< String, Skeleton >( );

	/**
	 * Updates Entities and Skeletons stored in the HashMap
	 * 
	 * @param deltaTime
	 *            - variable tracing the change in time between ticks
	 */
	public void update( float deltaTime ) {
		Iterator< Map.Entry< String, Entity > > it = entityList.entrySet( )
				.iterator( );
		Map.Entry< String, Entity > entityToUpdate;
		while ( it.hasNext( ) ) {
			entityToUpdate = it.next( );
			entityToUpdate.getValue( ).update( deltaTime );
		}

		Iterator< Map.Entry< String, Skeleton > > jit = skeletonList.entrySet( )
				.iterator( );
		Map.Entry< String, Skeleton > skeletonToUpdate;
		while ( jit.hasNext( ) ) {
			skeletonToUpdate = jit.next( );
			skeletonToUpdate.getValue( ).update( deltaTime );
		}

		Iterator< Map.Entry< String, Entity > > itAdd = entitiesToAdd
				.entrySet( ).iterator( );
		Map.Entry< String, Entity > entityToAdd;
		while ( itAdd.hasNext( ) ) {
			entityToAdd = itAdd.next( );
			entityList.put( entityToAdd.getKey( ), entityToAdd.getValue( ) );
		}
		entitiesToAdd.clear( );

		Iterator< Map.Entry< String, Skeleton > > jitAdd = skeletonsToAdd
				.entrySet( ).iterator( );
		Map.Entry< String, Skeleton > skeletonToAdd;
		while ( jitAdd.hasNext( ) ) {
			skeletonToAdd = jitAdd.next( );
			skeletonList
					.put( skeletonToAdd.getKey( ), skeletonToAdd.getValue( ) );
		}
		skeletonsToAdd.clear( );

		Iterator< Map.Entry< String, Entity > > itRemove = entitiesToRemove
				.entrySet( ).iterator( );
		Map.Entry< String, Entity > entryToRemove;
		while ( itRemove.hasNext( ) ) {
			entryToRemove = itRemove.next( );
			Entity entityToRemove = entryToRemove.getValue( );
			entityList.remove( entryToRemove.getKey( ) );
			Gdx.app.log(
					"EntityManager",
					"class com.blindtigergames.werescrewed.entity.EntityManager: DESTROYING ENTITY BODY" );
			entityToRemove.world.destroyBody( entityToRemove.body );
		}
		entitiesToRemove.clear( );

		Iterator< Map.Entry< String, Skeleton > > jitRemove = skeletonsToRemove
				.entrySet( ).iterator( );
		Map.Entry< String, Skeleton > entrySkelToRemove;
		while ( jitRemove.hasNext( ) ) {
			entrySkelToRemove = jitRemove.next( );
			Skeleton skeletonToRemove = entrySkelToRemove.getValue( );
			skeletonToRemove.world.destroyBody( skeletonToRemove.body );
			Gdx.app.log(
					"EntityManager",
					"class com.blindtigergames.werescrewed.entity.EntityManager: DESTROYING SKELETON BODY" );
			skeletonList.remove( entrySkelToRemove.getKey( ) );
		}
		skeletonsToRemove.clear( );

	}

	public void draw( SpriteBatch batch, float deltaTime ) {
		Iterator< Map.Entry< String, Entity > > eit = entityList.entrySet( )
				.iterator( );
		Map.Entry< String, Entity > eEntry;
		while ( eit.hasNext( ) ) {
			eEntry = eit.next( );
			eEntry.getValue( ).draw( batch, deltaTime );
		}

		Iterator< Map.Entry< String, Skeleton > > sit = skeletonList.entrySet( )
				.iterator( );
		Map.Entry< String, Skeleton > sEntry;
		while ( sit.hasNext( ) ) {
			sEntry = sit.next( );
			sEntry.getValue( ).draw( batch, deltaTime );
		}
	}

	/**
	 * Adds an Entity to the HashMap
	 * 
	 * @param name
	 *            - name added as a key to the HashMap
	 * @param type
	 *            - Object added as a value to the HashMap
	 */
	public void addEntity( String name, Entity type ) {
		entitiesToAdd.put( name, type );
	}

	/**
	 * Adds a Skeleton to the HashMap
	 * 
	 * @param name
	 *            - name added as a key to the HashMap
	 * @param type
	 *            - Object added as a value to the HashMap
	 */
	public void addSkeleton( String name, Skeleton type ) {
		skeletonsToAdd.put( name, type );
	}

	/**
	 * Removes an Entity from the HashMap
	 * 
	 * @param name
	 *            - name removed from the HashMap
	 * @param type
	 *            - Object value removed from the HashMap
	 */
	public void removeEntity( String name, Entity type ) {
		Gdx.app.log( "EntityManager", "ADDING TO REMOVE LIST" );
		entitiesToRemove.put( name, type );
		// entityList.remove( name );
	}

	/**
	 * Removes a Skeleton from the HashMap
	 * 
	 * @param name
	 *            - name removed from the HashMap
	 * @param type
	 *            - Object value removed from the HashMap
	 */
	public void removeSkeleton( String name, Skeleton type ) {
		Gdx.app.log( "EntityManager",
				"class com.blindtigergames.werescrewed.entity.EntityManager: adding skeleton "
						+ name + " to remove list" );
		skeletonsToRemove.put( name, type );
		// skeletonList.remove( name );
	}

}