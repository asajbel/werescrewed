package com.blindtigergames.werescrewed.checkpoints;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;

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
	private ResurrectScrew extraRezScrew;
	private Player player1;
	private Player player2;
	private World world;
	private int currentCheckPoint;
	private int p1holdTime = 0;
	private int p2holdTime = 0;
	private int respawnTime = 100;
	private boolean player1Mover = false;
	private boolean player2Mover = false;

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
		if ( checkP != checkPoints.get( currentCheckPoint ) ) {
			for ( int i = 0; i < checkPoints.size( ); i++ ) {
				if ( checkPoints.get( i ) == checkP ) {
					currentCheckPoint = i;
					break;
				}
			}
		}
	}

	public void update( float deltaTime ) {
		// if both players are dead
		// automatically re-spawn at the last checkpoint
		if ( player1.isPlayerDead( ) && player2.isPlayerDead( ) ) {
			// create a rez screw if it doesn't already exist
			handleDeadPlayer( );
			// handle dead player input to allow them to re-spawn
			handleDeadPlayerInput( player1 );
			handleDeadPlayerInput( player2 );
		}
		// if a single player is dead allow them to re-spawn
		// and create a resurrection screw to let their
		// team-mate re-spawn them
		else if ( player1.isPlayerDead( ) ) {
			// create a rez screw if it doesn't already exist
			handleDeadPlayer( );
			// handle dead player input to allow them to re-spawn
			handleDeadPlayerInput( player1 );
		} else if ( player2.isPlayerDead( ) ) {
			// create a rez screw if it doesn't already exist
			handleDeadPlayer( );
			// handle dead player input to allow them to re-spawn
			handleDeadPlayerInput( player2 );
		} else {
			// if both players are alive then remove the current
			// instance of the resurrection screw
			removeRezScrew( );
		}
		for ( int i = 0; i < checkPoints.size( ); i++ ) {
			if ( i != currentCheckPoint ) {
				// deactivate all the checkpoints that are not
				// the current checkpoint
				checkPoints.get( i ).deactivate( );
			}
			checkPoints.get( i ).update( deltaTime );
		}
		// update the rez screw if it exists
		if ( resurrectScrew != null ) {
			resurrectScrew.update( deltaTime );
		}
		if ( extraRezScrew != null ) {
			extraRezScrew.update( deltaTime );
		}
	}

	/**
	 * draw a range of three checkpoints and draw the resurrection screw if
	 * there is one
	 * 
	 * @param batch
	 */
	public void draw( SpriteBatch batch, float deltaTime ) {
		for ( CheckPoint c : checkPoints ) {
			c.draw( batch, deltaTime );
		}
		if ( resurrectScrew != null ) {
			resurrectScrew.draw( batch, deltaTime );
		}
		if ( extraRezScrew != null ) {
			extraRezScrew.draw( batch, deltaTime );
		}
	}

	/**
	 * when a player dies create a resurrection screw
	 */
	private void handleDeadPlayer( ) {
		if ( resurrectScrew == null ) {
			Entity entity = null;
			// find the entity that the current checkpoint is jointed to
			// and use it to connect the rez screw to
			for ( JointEdge j : checkPoints.get( currentCheckPoint ).body
					.getJointList( ) ) {
				entity = ( Entity ) j.joint.getBodyB( ).getUserData( );
			}
			ScrewBuilder rezzBuilder = new ScrewBuilder( )
					.screwType( ScrewType.SCREW_RESURRECT ).entity( entity )
					.world( world );
			Vector2 screwPos = new Vector2( -100, 150 );
			boolean isp1onExtraRezScrew = false;
			boolean isp2onExtraRezScrew = false;
			if ( extraRezScrew != null ) {
				if ( extraRezScrew.getDeadPlayer( ) == player1 ) {
					isp1onExtraRezScrew = true;
				} else {
					isp2onExtraRezScrew = true;
				}
			}
			if ( player1.isPlayerDead( ) && !isp1onExtraRezScrew ) {
				// create new rez screw and attach
				// it to player1 as the dead player
				rezzBuilder.player( player1 );
				// get the players direction and offset to the opposite of that
				if ( player1.body.getLinearVelocity( ).x < 0 ) {
					screwPos = new Vector2( 200, 150 );
				} else {
					screwPos = new Vector2( -100, 150 );
				}
				player1.body.setType( BodyType.KinematicBody );
				player1.body.setLinearVelocity( Vector2.Zero );
				resurrectScrew = rezzBuilder.playerOffset( true )
						.position( screwPos ).buildRezzScrew( );
			} else if ( player2.isPlayerDead( ) && !isp2onExtraRezScrew ) {
				// create new rez screw and attach
				// it to player2 as the dead player
				rezzBuilder.player( player2 );
				// get the players direction and offset to the opposite of that
				if ( player2.body.getLinearVelocity( ).x < 0 ) {
					screwPos = new Vector2( 200, 150 );
				} else {
					screwPos = new Vector2( -100, 150 );
				}
				player2.body.setType( BodyType.KinematicBody );
				player2.body.setLinearVelocity( Vector2.Zero );
				resurrectScrew = rezzBuilder.playerOffset( true )
						.position( screwPos ).buildRezzScrew( );
			}
		}
		if ( player1.isPlayerDead( ) && player2.isPlayerDead( )
				&& extraRezScrew == null ) {
			Entity entity = null;
			// find the entity that the current checkpoint is jointed to
			// and use it to connect the rez screw to
			for ( JointEdge j : checkPoints.get( currentCheckPoint ).body
					.getJointList( ) ) {
				entity = ( Entity ) j.joint.getBodyB( ).getUserData( );
			}
			ScrewBuilder rezzBuilder = new ScrewBuilder( )
					.screwType( ScrewType.SCREW_RESURRECT ).entity( entity )
					.world( world );
			Vector2 screwPos = new Vector2( -100, 150 );
			if ( resurrectScrew.getDeadPlayer( ) == player1 ) {
				// create new rez screw and attach
				// it to player1 as the dead player
				rezzBuilder.player( player2 );
				// get the players direction and offset to the opposite of that
				if ( player2.body.getLinearVelocity( ).x < 0 ) {
					screwPos = new Vector2( 200, 150 );
				} else {
					screwPos = new Vector2( -100, 150 );
				}
				player2.body.setType( BodyType.KinematicBody );
				player2.body.setLinearVelocity( Vector2.Zero );
			} else {
				// create new rez screw and attach
				// it to player2 as the dead player
				rezzBuilder.player( player1 );
				// get the players direction and offset to the opposite of that
				if ( player1.body.getLinearVelocity( ).x < 0 ) {
					screwPos = new Vector2( 200, 150 );
				} else {
					screwPos = new Vector2( -100, 150 );
				}
				player1.body.setType( BodyType.KinematicBody );
				player1.body.setLinearVelocity( Vector2.Zero );
			}
			extraRezScrew = rezzBuilder.playerOffset( true )
					.position( screwPos ).buildRezzScrew( );
		}
	}

	/**
	 * if a dead player is holding down the respawn button
	 * 
	 * @param player
	 */
	private void handleDeadPlayerInput( Player player ) {
		boolean playerMover = false;
		if ( player.getState( ) == PlayerState.GrabMode ) {
			int holdTime = 0;
			if ( player == player1 ) {
				p1holdTime++;
				holdTime = p1holdTime;
				playerMover = player1Mover;
			} else {
				p2holdTime++;
				holdTime = p2holdTime;
				playerMover = player2Mover;
			}
			if ( holdTime > respawnTime ) {
				playerMover = false;
				player.setMoverNullAtCurrentState( );
				// if the dead player has held the re-spawn button
				// respawn them at the current check point
				spawnAtCheckPoint( player );
				if ( player == player1 ) {
					p1holdTime = 0;
				} else {
					p2holdTime = 0;
				}
			} else if ( !playerMover ) {
				//TODO replace this mover with an animation
				player.setActive( true );
				player.setMoverAtCurrentState( new LerpMover( player
						.getPositionPixel( ).sub(
								player.sprite.getWidth( ) / 32.0f, 0 ), player
						.getPositionPixel( ).add(
								player.sprite.getWidth( ) / 32.0f, 0 ), .2f,
						true, LinearAxis.HORIZONTAL, 0 ) );
				playerMover = true;
			} else {
				player.updateMover( 0 );
			}
		} else {
			// if the player lets go reset the time
			if ( player == player1 ) {
				p1holdTime = 0;
			} else {
				p2holdTime = 0;
			}
			playerMover = false;
			player.setMoverNullAtCurrentState( );
		}
		if ( player == player1 ) {
			player1Mover = playerMover;
		} else {
			player2Mover = playerMover;
		}
	}

	/**
	 * respawn the player at the check point
	 * 
	 * @param player
	 */
	private void spawnAtCheckPoint( Player player ) {
		// bring the player back to life
		player.respawnPlayer( );
		// remove the instance of the rez screw
		removeRezScrew( );
		// move the player to the current checkpoint
		player.body.setTransform( checkPoints.get( currentCheckPoint )
				.getPosition( ), 0.0f );
		player.body.setLinearVelocity( Vector2.Zero );
		player.body.setType( BodyType.DynamicBody );
	}

	/**
	 * removes the current instance of an resurrect screw
	 */
	private void removeRezScrew( ) {
		// remove the rez screw if both players are alive
		if ( resurrectScrew != null
				&& !resurrectScrew.getDeadPlayer( ).isPlayerDead( ) ) {
			if ( !resurrectScrew.isPlayerAttached( ) ) {
				resurrectScrew.remove( );
				if ( resurrectScrew.isRemoved( ) ) {
					resurrectScrew = null;
				}
			} else {
				resurrectScrew.setRemove( true );
			}
		}
		if ( extraRezScrew != null
				&& !extraRezScrew.getDeadPlayer( ).isPlayerDead( ) ) {
			if ( !extraRezScrew.isPlayerAttached( ) ) {
				extraRezScrew.remove( );
				if ( extraRezScrew.isRemoved( ) ) {
					extraRezScrew = null;
				}
			} else {
				extraRezScrew.setRemove( true );
			}
		}
	}

}
