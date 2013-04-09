package com.blindtigergames.werescrewed.puzzles;

import java.util.HashMap;
import java.util.Map;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;

public class PuzzleManager {

	public PuzzleManager( String name ) {
		puzzleScrews = new HashMap< String, Screw >( );
		puzzleEntities = new HashMap< String, Entity >( );
		puzzleMovers = new HashMap< String, IMover >( );
		puzzleName = name;
	}

	public void update( float deltaTime ) {

	}

	/**
	 * adds a screw to the map of screws
	 */
	public void addScrew( PuzzleScrew puzzlePiece ) {
		puzzleScrews.put( puzzleName + "_" + puzzleScrews.size( ), puzzlePiece );
	}

	/**
	 * adds an entity to be manipulated by the puzzle screw
	 * 
	 * @param Entity
	 *            puzzlePiece
	 */
	public void addEntity( Entity puzzlePiece ) {
		puzzleEntities.put( puzzleName + "_" + puzzleEntities.size( ),
				puzzlePiece );
	}

	/**
	 * adds a mover to manipulate the specific entity
	 * 
	 * @param key
	 *            for the mover in the map, use the name of the entity it
	 *            applies to.
	 * @param IMover
	 *            puzzlePiece
	 */
	public void addMover( IMover puzzlePiece ) {
		puzzleMovers.put( puzzleName + "_" + puzzleMovers.size( ), puzzlePiece );
	}

	/**
	 * 
	 * @param pEs
	 * @param movers
	 */
	public void createLists( Map< String, Entity > pEs,
			Map< String, IMover > movers ) {
		puzzleEntities = pEs;
		puzzleMovers = movers;
	}

	/**
	 * applies movement to an entity or turns on/off movement for an entities
	 * mover by using Entity.applyPuzzleMovement ( puzzleScrew.depth /
	 * puzzleScrew.maxDepth ) puzzleScrew.depth / puzzleScrew.maxDepth can
	 * either be a percentage of movement or it can be a boolean < 0.5f is yes >
	 * 0.5f is no
	 */
	public void runElement( Screw screw, float screwVal ) {
		int elementID = 0;
		String keyVal = puzzleName + "_" + elementID;
		while ( puzzleEntities.containsKey( keyVal ) ) {
			if ( puzzleMovers.containsKey( keyVal ) ) {
				puzzleMovers.get( keyVal ).runPuzzleMovement( screw, screwVal,
						( Platform ) puzzleEntities.get( keyVal ) );
			}
			elementID++;
			keyVal = puzzleName + "_" + elementID;
		}
		elementID = 0;
		keyVal = puzzleName + "_" + elementID;
		while ( puzzleScrews.containsKey( keyVal ) ) {
			if ( puzzleScrews.get( keyVal ).getScrewType( ) == ScrewType.SCREW_PUZZLE ) {
				PuzzleScrew pscrew = ( PuzzleScrew ) puzzleScrews.get( keyVal );
				pscrew.fixConcurrentScrew( screw );
				elementID++;
				keyVal = puzzleName + "_" + elementID;
			}
		}
	}

	private Map< String, Screw > puzzleScrews;
	private Map< String, Entity > puzzleEntities;
	private Map< String, IMover > puzzleMovers;
	private String puzzleName;
}
