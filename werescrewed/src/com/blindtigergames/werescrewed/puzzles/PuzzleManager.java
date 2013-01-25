package com.blindtigergames.werescrewed.puzzles;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.physics.box2d.JointDef;
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

	public PuzzleManager( ) {
		puzzleEntities = new HashMap< String, Entity >( );
		puzzleMovers = new HashMap< String, IMover >( );
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

	public void runElement( PuzzleScrew screw1 ) {
		if ( screw1.getDepth( ) != screw1.getMaxDepth( ) ) {
			int num = 0;
			String elementID = screw1.name + '_' + num;
			while ( puzzleEntities.containsKey( elementID )
					&& puzzleMovers.containsKey( elementID ) ) {

				System.out.println( screw1.name + " " + elementID );
				puzzleEntities.get( elementID ).setMover(
						puzzleMovers.get( elementID ) );
				num++;
				elementID = screw1.name + '_' + num;
			}
		}
	}

	private PuzzleScrew screw1;
	private Map< String, Entity > puzzleEntities;
	private Map< String, IMover > puzzleMovers;
	private Map< String, JointDef > puzzleJoints;

}
