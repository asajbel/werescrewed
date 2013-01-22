package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EntityManager extends Entity{

	//protected ArrayList keys = new ArrayList();
	protected static HashMap< String, Entity > entityList = new HashMap< String, Entity >();
	protected static HashMap< String, Entity > skeletonList = new HashMap< String, Entity >();
	
	public EntityManager ( ) { }
	
	public void updateEntity() {
		Entity temp = new Entity();
		Set< String > keys = entityList.keySet( );
		for ( int i = 0; i < entityList.size(); i++ ) {
			temp = entityList.get( keys );
		}
		temp = null;
		keys.clear( );
	}
	
	//Adds an Entity to the HashMap
	public void addEntity ( String name, Entity type ) {
		entityList.put( name,  type );
	}
	
	//Removes an Entity from the HashMap
	public void removeEntity ( String name ) {
		entityList.remove( name );
	}
	
}
