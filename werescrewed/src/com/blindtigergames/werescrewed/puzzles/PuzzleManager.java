package com.blindtigergames.werescrewed.puzzles;

import java.util.ArrayList;
import java.util.Map;

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
		puzzleEntities = new ArrayList< Entity >( );
		puzzleMovers = new ArrayList< IMover >( );
		puzzleJoints = new ArrayList< JointDef >( );
		this.world = world;
	}

	public void update( float deltaTime ) {
		for ( Entity e : puzzleEntities ) {
			e.update( deltaTime );
			if ( e.mover != null ) {
				//e.mover.move( deltaTime, e.body );
			}
		}
	}

	public void addEntity( Entity puzzlePiece ) {
		puzzleEntities.add( puzzlePiece );
	}

	public void addMover( IMover puzzlePiece ) {
		puzzleMovers.add( puzzlePiece );
	}

	public void addJointDef( JointDef puzzlePiece ) {
		puzzleJoints.add( puzzlePiece );
	}

	public void createMaps( ArrayList< Entity > pEs, ArrayList< IMover > movers ) {
		puzzleEntities = pEs;
		puzzleMovers = movers;
	}

	public void runElement( PuzzleScrew screw1 ) {
		if ( screw1.getDepth( ) != screw1.getMaxDepth( ) ) {
			int index = 0;
			for ( Entity e : puzzleEntities ) {
				if ( e.mover == null ) {
					if ( e.body.getJointList( ).size( ) < 1 ) {
						System.out.println( "the joint is created" );
						PrismaticJoint j = ( PrismaticJoint ) world
								.createJoint( puzzleJoints.get( index ) );
						SlidingMotorMover sm = new SlidingMotorMover(
								PuzzleType.PRISMATIC_SLIDER, j );
						e.setMover( sm );
						e.body.setAwake( true );
						puzzleMovers.add( sm );
					}
				}
				index++;
			}
		}
	}

	private World world;
	private ArrayList< Entity > puzzleEntities;
	private ArrayList< IMover > puzzleMovers;
	private ArrayList< JointDef > puzzleJoints;

}
