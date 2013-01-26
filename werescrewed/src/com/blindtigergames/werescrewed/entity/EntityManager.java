package com.blindtigergames.werescrewed.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.entity.Skeleton;

public class EntityManager extends Entity {

	protected static HashMap< String, Entity > entityList = new HashMap< String, Entity >( );
	protected static HashMap< String, Skeleton > skeletonList = new HashMap< String, Skeleton >( );

	public EntityManager( ) {
	}

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
	}
	
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

	// Adds a Skeleton to the HashMap
	public void addSkeleton( String name, Skeleton type ) {
		skeletonList.put( name, type );
	}

	// Removes an Entity from the HashMap
	public void removeEntity( String name ) {
		entityList.remove( name );
	}

	// Removes a Skeleton from the HashMap
	public void removeSkeleton( String name ) {
		skeletonList.remove( name );
	}

}