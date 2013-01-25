package com.blindtigergames.werescrewed.puzzles;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 * 
 */

public class PuzzleManager {

	public PuzzleManager( World world ) {
		puzzleEntities = new HashMap< String, Entity >( );
		puzzleMovers = new HashMap< String, IMover >( );
		puzzleJoints = new HashMap< String, JointDef >( );
		this.world = world;
	}

	public void addEntity( String screwID, Entity puzzlePiece ) {
		puzzleEntities.put( screwID, puzzlePiece );
	}

	public void addMover( String screwID, IMover puzzlePiece ) {
		puzzleMovers.put( screwID, puzzlePiece );
	}

	public void addJointDef( String screwID, JointDef puzzlePiece ) {
		puzzleJoints.put( screwID, puzzlePiece );
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
			while ( puzzleEntities.containsKey( elementID ) ) {
				// System.out.println( "the puzzle entity exists" );
				if ( puzzleMovers.containsKey( elementID ) ) {
					puzzleEntities.get( elementID ).setMover(
							puzzleMovers.get( elementID ) );
				} else if ( puzzleJoints.containsKey( elementID ) ) {
					if ( puzzleEntities.get( elementID ).body.getJointList( )
							.size( ) < 1 ) {
						System.out.println( "the joint is created" );
						PrismaticJoint j = ( PrismaticJoint ) world
								.createJoint( puzzleJoints.get( elementID ) );
						puzzleEntities.get( elementID ).setMover(
								new SlidingMotorMover(
										PuzzleType.PRISMATIC_SLIDER, j ) );
						puzzleEntities.get( elementID ).body.setAwake( true );
					} else {
						// puzzleMovers.get( elementID ).move(
						// ( float ) screw1.getDepth( ) / (float)
						// screw1.getMaxDepth( ),
						// puzzleEntities.get( elementID ).body );
						puzzleEntities.get( elementID ).body
								.setLinearVelocity( new Vector2( -1.0f, 0.0f ) );
					}
				}
				num++;
				elementID = screw1.name + '_' + num;
			}
		}
	}

	private World world;
	private Map< String, Entity > puzzleEntities;
	private Map< String, IMover > puzzleMovers;
	private Map< String, JointDef > puzzleJoints;

}
