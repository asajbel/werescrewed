package com.blindtigergames.werescrewed.checkpoints;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;
import com.blindtigergames.werescrewed.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.util.Util;

/**
 * handles all of the progress through checkpoints also handles re-spawning with
 * either checkpoints or resurrection screws
 * 
 * @author Dennis Foley
 * 
 */
public class ProgressManager {

	private ArrayList< CheckPoint > checkPoints;
	private ResurrectScrew resurrectScrew;
	private Player player1;
	private Player player2;
	private World world;
	private boolean changeCheckPoint = false;
	private int currentCheckPoint;
	private int holdTime = 0;
	private int respawnTime = 100;

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param world
	 */
	public ProgressManager( Player p1, Player p2, World world ) {
		this.player1 = p1;
		this.player2 = p2;
		this.world = world;
		checkPoints = new ArrayList< CheckPoint >( );
		resurrectScrew = null;
		currentCheckPoint = 0;
	}

	/**
	 * adds a checkPoint to the list of checkpoints should be in order of
	 * progress through the level
	 * 
	 * @param checkP
	 */
	public void addCheckPoint( CheckPoint checkP ) {
		checkPoints.add( checkP );
	}

	/**
	 * change the current check point when the player hits a new checkPoint it
	 * removes the old check point from the list of checkpoints
	 * 
	 * @param checkP
	 */
	public void hitNewCheckPoint( CheckPoint checkP ) {
		for ( int i = 0; i < checkPoints.size( ); i++ ) {
			if ( checkPoints.get( i ) == checkP ) {
				if ( i > currentCheckPoint ) {
					changeCheckPoint = true;
					currentCheckPoint = i;
				}
				break;
			}
		}
	}

	public void update( float deltaTime ) {
		// trying to remove the bodies and joints of previous checkpoints
//		if ( changeCheckPoint ) {
//			for ( int i = 0; i < currentCheckPoint; i++ ) {
//				if ( checkPoints.get( i ).body.getJointList( ).size( ) > 0 ) {
//					checkPoints.get( i ).removeJoints( );
//				} else {
//					checkPoints.get( i ).removeBody( );
//				}
//			}
//			changeCheckPoint = false;
//		}
		if ( player1.isPlayerDead( ) && player2.isPlayerDead( ) ) {
			spawnAtCheckPoint( player1 );
			spawnAtCheckPoint( player2 );
			holdTime = 0;
		}
		if ( player1.isPlayerDead( ) ) {
			handleDeadPlayer( );
			handleDeadPlayerInput( player1 );
		}
		if ( player2.isPlayerDead( ) ) {
			handleDeadPlayer( );
			handleDeadPlayerInput( player2 );
		}
		if ( !player1.isPlayerDead( ) && !player2.isPlayerDead( )
				&& resurrectScrew != null
				&& resurrectScrew.body.getJointList( ).size( ) > 0 ) {
			resurrectScrew.removeJoints( );
		}
		int start = currentCheckPoint;
		int end = currentCheckPoint + 1;
		if ( end >= checkPoints.size( ) ) {
			end = checkPoints.size( ) - 1;
		}
		for ( int i = start; i <= end; i++ ) {
			checkPoints.get( i ).update( deltaTime );
		}
		if ( resurrectScrew != null ) {
			resurrectScrew.update( deltaTime );
		}
	}

	/**
	 * draw a range of three checkpoints and draw the resurrection screw if
	 * there is one
	 * 
	 * @param batch
	 */
	public void draw( SpriteBatch batch ) {
		int start = currentCheckPoint;
		int end = currentCheckPoint + 2;
		if ( end >= checkPoints.size( ) ) {
			end = checkPoints.size( ) - 1;
		}
		for ( int i = start; i <= end; i++ ) {
			checkPoints.get( i ).draw( batch );
		}
		if ( resurrectScrew != null ) {
			resurrectScrew.draw( batch );
		}
	}

	/**
	 * when a player dies create a resurrection screw
	 */
	private void handleDeadPlayer( ) {
		if ( resurrectScrew == null ) {
			Entity entity = null;
			for ( JointEdge j : checkPoints.get( currentCheckPoint ).body
					.getJointList( ) ) {
				entity = ( Entity ) j.joint.getBodyB( ).getUserData( );
			}
			if ( player1.isPlayerDead( ) ) {
				resurrectScrew = new ResurrectScrew( player1.getPosition( )
						.add( 0, 256f * Util.PIXEL_TO_BOX )
						.mul( Util.BOX_TO_PIXEL ), entity, world, player1 );
			} else {
				resurrectScrew = new ResurrectScrew( player2.getPosition( )
						.add( 0, 256f * Util.PIXEL_TO_BOX )
						.mul( Util.BOX_TO_PIXEL ), entity, world, player2 );
			}
		}
	}

	/**
	 * if a dead player is holding down the respawn button
	 * 
	 * @param player
	 */
	private void handleDeadPlayerInput( Player player ) {
		if ( player.getState( ) == PlayerState.GrabMode ) {
			holdTime++;
			if ( holdTime > respawnTime ) {
				spawnAtCheckPoint( player );
				holdTime = 0;
			}
		} else {
			holdTime = 0;
		}
	}

	/**
	 * respawn the player at the check point
	 * 
	 * @param player
	 */
	private void spawnAtCheckPoint( Player player ) {
		player.body.setTransform( checkPoints.get( currentCheckPoint )
				.getPosition( ), 0.0f );
		player.respawnPlayer( );
	}

}
