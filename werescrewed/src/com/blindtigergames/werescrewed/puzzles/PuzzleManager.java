package com.blindtigergames.werescrewed.puzzles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.platforms.Platform;

/**
 * puzzle screw class for moving puzzle pieces
 * 
 * @author Dennis
 * 
 */

public class PuzzleManager {

	public PuzzleManager( World world ) {
		puzzleEntities = new ArrayList< Entity >( );
		puzzleMovers = new HashMap< String, IMover >( );
	}

	public void update( float deltaTime ) {
		for ( Entity e : puzzleEntities ) {
			e.update( deltaTime );
			if ( e.mover != null ) {
				e.mover.move( deltaTime, e.body );
			}
		}
	}

	/**
	 * adds an entity to be manipulated by the puzzle screw
	 * 
	 * @param Entity
	 *            puzzlePiece
	 */
	public void addEntity( Entity puzzlePiece ) {
		puzzleEntities.add( puzzlePiece );
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
	public void addMover( String key, IMover puzzlePiece ) {
		puzzleMovers.put( key, puzzlePiece );
	}

	/**
	 * 
	 * @param pEs
	 * @param movers
	 */

	public void createLists( ArrayList< Entity > pEs,
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
	public void runElement( float screwVal ) {
		for ( Entity e : puzzleEntities ) {
			if ( e.mover == null ) {
				if ( puzzleMovers.containsKey( e.name ) ) {
					LerpMover lm = ( LerpMover ) puzzleMovers.get( e.name );
					lm.runPuzzleMovement( screwVal, ( Platform ) e );
				}
			}
		}
	}

	private ArrayList< Entity > puzzleEntities;
	private Map< String, IMover > puzzleMovers;

}
