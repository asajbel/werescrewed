package com.blindtigergames.werescrewed.puzzles;

import java.util.Map;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;

public class PuzzleManager {

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

	public void runElement( Float deltaTime, String screwID, float value ) {
		int num = 0;
		String elementID = screwID + '_' + num;
		while ( puzzleEntities.containsKey( elementID )
				&& puzzleMovers.containsKey( elementID ) ) {

			puzzleMovers.get( elementID ).move( deltaTime,
					puzzleEntities.get( elementID ).body );
			num++;
			elementID = screwID + '_' + num;
		}
	}

	private Map< String, Entity > puzzleEntities;
	private Map< String, IMover > puzzleMovers;

}
