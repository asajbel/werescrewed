package com.blindtigergames.werescrewed.puzzles;

import java.util.HashMap;
import java.util.Map;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class PuzzleManager {

	public PuzzleManager () {
		puzzleEntities = new HashMap< String, Entity >( );
		puzzleMovers = new HashMap< String, IMover >( );
	}
	
	public void update ( Float deltatime ) {
		if( screw1 != null ) {
			runElement ( deltatime );
		}
	}

	public void addEntity( String screwID, Entity puzzlePiece ) {
		puzzleEntities.put( screwID, puzzlePiece );
	}

	public void addMover( String screwID, IMover puzzlePiece ) {
		puzzleMovers.put( screwID, puzzlePiece );
	}

	public void createMaps( Map< String, Entity > pEs,
			Map< String, IMover > movers ) {
		puzzleEntities = pEs;
		puzzleMovers = movers;
	}

	public void runElement( Float deltaTime ) {
		System.out.println( screw1.name);
		if ( screw1.getDepth( ) == screw1.getMaxDepth( ) ) {
			int num = 0;
			String elementID = screw1.name + '_' + num;
			System.out.println( screw1.name + " " +  elementID );
			while ( puzzleEntities.containsKey( elementID )
					&& puzzleMovers.containsKey( elementID ) ) {

				puzzleMovers.get( elementID ).move( deltaTime,
						puzzleEntities.get( elementID ).body );
				num++;
				elementID = screw1.name + '_' + num;
			}
		}
	}

	private PuzzleScrew screw1;
	private Map< String, Entity > puzzleEntities;
	private Map< String, IMover > puzzleMovers;

}
