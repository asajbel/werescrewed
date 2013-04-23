package com.blindtigergames.werescrewed.checkpoints;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
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

	public CheckPoint currentCheckPoint;
	private ResurrectScrew resurrectScrew;
	private ResurrectScrew extraRezScrew;
	private Player player1;
	private Player player2;
	private Entity p1Ghost;
	private Entity p2Ghost;
	private World world;
	private Vector2 chkptOffset = new Vector2( 16, 8 );
	private Vector2 oldChkptPos;

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
		resurrectScrew = null;
		extraRezScrew = null;
	}

	/**
	 * Change the current check point when the player hits a new checkPoint.
	 * Removes the old check point from the list of checkpoints
	 * 
	 * @param checkPoint
	 */
	public void hitNewCheckPoint( CheckPoint checkPoint ) {
		// If the checkpoint hit is not the currently activated one
		if ( currentCheckPoint != checkPoint ) {
			// Deactivate the current checkpoint
			currentCheckPoint.deactivate( );
			oldChkptPos = currentCheckPoint.getPositionPixel( ).cpy( );
			// Then set it to the one the players hit
			currentCheckPoint = checkPoint;
			// If player 1's Ghost is active
			if ( p1Ghost != null ) {
				// And it is moving
				if ( p1Ghost.currentMover( ) instanceof LerpMover ) {
					LerpMover lm = ( LerpMover ) p1Ghost.currentMover( );
					// Change the destination
					lm.changeBeginPos( p1Ghost.getPositionPixel( ) );
					lm.setAlpha( 0 );
					lm.changeEndPos( currentCheckPoint.getPositionPixel( ).sub(
							chkptOffset ) );
					// Adjust the speed
					lm.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
							.sub( p1Ghost.getPositionPixel( ) ).len( ) );
					if ( currentCheckPoint.getPositionPixel( ).x < p1Ghost
							.getPositionPixel( ).x
							&& oldChkptPos.x > p1Ghost.getPositionPixel( ).x ) {
						p1Ghost.sprite.setScale( -1, 1 );
					} else if ( currentCheckPoint.getPositionPixel( ).x > p1Ghost
							.getPositionPixel( ).x
							&& oldChkptPos.x < p1Ghost.getPositionPixel( ).x ) {
						p1Ghost.sprite.setScale( -1, 1 );
					}
				}
			}
			// See player 1
			if ( p2Ghost != null ) {
				if ( p2Ghost.currentMover( ) instanceof LerpMover ) {
					LerpMover lm = ( LerpMover ) p2Ghost.currentMover( );
					lm.changeBeginPos( p2Ghost.getPositionPixel( ) );
					lm.setAlpha( 0 );
					lm.changeEndPos( currentCheckPoint.getPositionPixel( ).sub(
							chkptOffset ) );
					lm.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
							.sub( p2Ghost.getPositionPixel( ) ).len( ) );
					if ( currentCheckPoint.getPositionPixel( ).x < p2Ghost
							.getPositionPixel( ).x
							&& oldChkptPos.x > p2Ghost.getPositionPixel( ).x ) {
						p2Ghost.sprite.setScale( -1, 1 );
					} else if ( currentCheckPoint.getPositionPixel( ).x > p2Ghost
							.getPositionPixel( ).x
							&& oldChkptPos.x < p2Ghost.getPositionPixel( ).x ) {
						p2Ghost.sprite.setScale( -1, 1 );
					}
				}
			}
		}
	}

	public void update( float deltaTime ) {
		// if a single player is dead allow them to re-spawn
		// and create a resurrection screw to let their
		// team-mate re-spawn them
		if ( player1.isPlayerDead( ) || player2.isPlayerDead( ) ) {
			handleDeadPlayer( );
			if ( !player1.isPlayerDead( ) && p1Ghost != null ) {
				p1Ghost.clearAnchors( );
				p1Ghost = null;
			}
			if ( !player2.isPlayerDead( ) && p2Ghost != null ) {
				p2Ghost.clearAnchors( );
				p2Ghost = null;
			}
		} else {
			// if both players are alive then remove the current
			// instance of the resurrection screw
			if ( resurrectScrew != null || extraRezScrew != null ) {
				removeRezScrew( );
			}
			if ( p1Ghost != null ) {
				p1Ghost.clearAnchors( );
				p1Ghost = null;
			}
			if ( p2Ghost != null ) {
				p2Ghost.clearAnchors( );
				p2Ghost = null;
			}
		}
		// update the rez screw if it exists
		if ( resurrectScrew != null ) {
			resurrectScrew.update( deltaTime );
		}
		if ( extraRezScrew != null ) {
			extraRezScrew.update( deltaTime );
		}
		if ( p1Ghost != null ) {
			updateGhost( player1 );
		}
		if ( p2Ghost != null ) {
			updateGhost( player2 );
		}
		oldChkptPos = currentCheckPoint.getPositionPixel( ).cpy( );
	}

	/**
	 * draw a range of three checkpoints and draw the resurrection screw if
	 * there is one
	 * 
	 * @param batch
	 */
	public void draw( SpriteBatch batch, float deltaTime ) {
		if ( resurrectScrew != null ) {
			resurrectScrew.draw( batch, deltaTime );
		}
		if ( extraRezScrew != null ) {
			extraRezScrew.draw( batch, deltaTime );
		}
		if ( p1Ghost != null ) {
			if ( p1Ghost.currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) p1Ghost.currentMover( );
				if ( player1.getState( ) == PlayerState.RespawnMode ) {
					lm.moveStep( );
				}
				p1Ghost.sprite.setPosition( lm.getPos( ) );
				p1Ghost.sprite.draw( batch, 1f );
			}
		}
		if ( p2Ghost != null ) {
			if ( p2Ghost.currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) p2Ghost.currentMover( );
				if ( player2.getState( ) == PlayerState.RespawnMode ) {
					lm.moveStep( );
				}
				p2Ghost.sprite.setPosition( lm.getPos( ) );
				p2Ghost.sprite.draw( batch, 1f );
			}
		}
	}

	/**
	 * when a player dies create a resurrection screw
	 */
	private void handleDeadPlayer( ) {
		// If the primary rez screw doesn't exist
		if ( resurrectScrew == null ) {
			boolean p1HasRezScrew = false;
			boolean p2HasRezScrew = false;
			// If there is no resurrectScrew, but extraRezScrew exists
			if ( extraRezScrew != null ) {
				if ( extraRezScrew.getDeadPlayer( ) == player1 ) {
					// Player 1 has the extra screw
					p1HasRezScrew = true;
				} else {
					// Player 2 has the extra screw
					p2HasRezScrew = true;
				}
			}
			// If player 1 is dead, but doesn't have a screw yet
			if ( player1.isPlayerDead( ) && !p1HasRezScrew ) {
				// create the ghost of the dead player
				buildGhost( player1 );
				// create rez screw
				buildRezScrew( player1 );
			}
			// If player 2 is dead but doesn't have a screw yet
			else if ( player2.isPlayerDead( ) && !p2HasRezScrew ) {
				// create the ghost of the dead player
				buildGhost( player2 );
				// creat rez screw
				buildRezScrew( player2 );
			}
		}
		// If both players are dead, and there is no second rez screw
		if ( player1.isPlayerDead( ) && player2.isPlayerDead( )
				&& extraRezScrew == null ) {
			// If player 1 has the existing rez screw
			if ( resurrectScrew.getDeadPlayer( ) == player1 ) {
				// create the ghost of the dead player
				buildGhost( player2 );
				// create extra rez screw
				buildExtraRezScrew( player2 );
			} else {
				// create the ghost of the dead player
				buildGhost( player1 );
				// create extra rez screw
				buildExtraRezScrew( player1 );
			}
		}
	}

	private void buildGhost( Player player ) {
		if ( player == player1 ) {
			// build ghost entity
			p1Ghost = new Entity( "player1Ghost", player1.getPositionPixel( )
					.cpy( ).add( -64f, 64f ), WereScrewedGame.manager.get(
					WereScrewedGame.dirHandle.path( )
							+ "/common/player_r_m_ghost.png", Texture.class ),
					null, false );
			// build ghost mover
			LerpMover ghostMover = new LerpMover( player1.getPositionPixel( )
					.cpy( ).add( -64f, 64f ), currentCheckPoint
					.getPositionPixel( ).sub( chkptOffset ),
					LinearAxis.DIAGONAL );
			// set speed of ghost mover
			ghostMover.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
					.sub( player1.getPositionPixel( ) ).len( ) );
			p1Ghost.setMoverAtCurrentState( ghostMover );
			// build ghost anchor
			Anchor anchor = new Anchor( player1.getPositionPixel( ),
					new Vector2( 0, 0 ), Player.ANCHOR_BUFFER_SIZE );
			anchor.activate( );
			p1Ghost.addAnchor( anchor );
			// face the direction of the checkpoint
			if ( currentCheckPoint.getPositionPixel( ).x < p1Ghost
					.getPositionPixel( ).x ) {
				p1Ghost.sprite.setScale( -1, 1 );
			}
		} else {
			// build ghost entity
			p2Ghost = new Entity( "player2Ghost", player2.getPositionPixel( )
					.cpy( ).add( -64f, 64f ), WereScrewedGame.manager.get(
					WereScrewedGame.dirHandle.path( )
							+ "/common/player_female_idle_ghost.png",
					Texture.class ), null, false );
			// build ghost mover
			LerpMover ghostMover = new LerpMover( player2.getPositionPixel( )
					.cpy( ).add( -64f, 64f ), currentCheckPoint
					.getPositionPixel( ).sub( chkptOffset ),
					LinearAxis.DIAGONAL );
			ghostMover.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
					.sub( player2.getPositionPixel( ) ).len( ) );
			p2Ghost.setMoverAtCurrentState( ghostMover );
			// build ghost anchor
			Anchor anchor = new Anchor( player2.getPositionPixel( ),
					new Vector2( 0, 0 ), Player.ANCHOR_BUFFER_SIZE );
			anchor.activate( );
			p2Ghost.addAnchor( anchor );
			// face the direction of the checkpoint
			if ( currentCheckPoint.getPositionPixel( ).x < p2Ghost
					.getPositionPixel( ).x ) {
				p2Ghost.sprite.setScale( -1, 1 );
			}
		}
	}

	private void buildRezScrew( Player player ) {
		ScrewBuilder rezzBuilder = new ScrewBuilder( ).screwType(
				ScrewType.SCREW_RESURRECT ).world( world );
		Vector2 screwPos = new Vector2( -100, 150 );
		rezzBuilder.player( player );
		LerpMover screwMover;
		// move the screw to the opposite direction the player is moving
		if ( player.body.getLinearVelocity( ).x < 0 ) {
			screwPos = new Vector2( 270, 150 );
			screwMover = new LerpMover( player.getPositionPixel( ), player
					.getPositionPixel( ).add( screwPos ), LinearAxis.DIAGONAL );
		} else {
			screwMover = new LerpMover( player.getPositionPixel( ), player
					.getPositionPixel( ).add( screwPos )
					.sub( Player.WIDTH, Player.HEIGHT / 3.0f ),
					LinearAxis.DIAGONAL );
		}
		player.body.setLinearVelocity( Vector2.Zero );
		player.body.setType( BodyType.KinematicBody );
		resurrectScrew = rezzBuilder.playerOffset( true )
				.lerpMover( screwMover ).position( screwPos )
				.entity( player.getLastPlatform( ) ).buildRezzScrew( );
	}

	private void buildExtraRezScrew( Player player ) {
		ScrewBuilder rezzBuilder = new ScrewBuilder( ).screwType(
				ScrewType.SCREW_RESURRECT ).world( world );
		Vector2 screwPos = new Vector2( -100, 150 );
		rezzBuilder.player( player );
		LerpMover screwMover;
		if ( player.body.getLinearVelocity( ).x < 0 ) {
			screwPos = new Vector2( 270, 150 );
			screwMover = new LerpMover( player.getPositionPixel( ), player
					.getPositionPixel( ).add( screwPos ), LinearAxis.DIAGONAL );
		} else {
			screwPos = new Vector2( -100, 150 );
			screwMover = new LerpMover( player.getPositionPixel( ), player
					.getPositionPixel( ).add( screwPos )
					.sub( Player.WIDTH, Player.HEIGHT / 3.0f ),
					LinearAxis.DIAGONAL );
		}
		player.body.setLinearVelocity( Vector2.Zero );
		player.body.setType( BodyType.KinematicBody );
		extraRezScrew = rezzBuilder.playerOffset( true )
				.entity( player.getLastPlatform( ) ).lerpMover( screwMover )
				.position( screwPos ).buildRezzScrew( );
	}

	private void updateGhost( Player player ) {
		if ( player == player1 ) {
			if ( p1Ghost.currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) p1Ghost.currentMover( );
				// lm.changeBeginPos( player1.getPositionPixel( ).cpy( )
				// .add( -64f, 64f ) );
				lm.changeEndPos( currentCheckPoint.getPositionPixel( ).sub(
						chkptOffset ) );
				lm.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
						.sub( player1.getPositionPixel( ) ).len( ) );
				if ( currentCheckPoint.getPositionPixel( ).x < p1Ghost
						.getPositionPixel( ).x
						&& oldChkptPos.x > p1Ghost.getPositionPixel( ).x ) {
					p1Ghost.sprite.setScale( -1, 1 );
				} else if ( currentCheckPoint.getPositionPixel( ).x > p1Ghost
						.getPositionPixel( ).x
						&& oldChkptPos.x < p1Ghost.getPositionPixel( ).x ) {
					p1Ghost.sprite.setScale( -1, 1 );
				}
				if ( lm.atEnd( ) ) {
					spawnAtCheckPoint( player1 );
					p1Ghost.clearAnchors( );
					p1Ghost = null;
				} else {
					// p1Ghost.update( deltaTime );
					p1Ghost.updateAnchors( );
				}
			}
		} else {
			if ( p2Ghost.currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) p2Ghost.currentMover( );
				// lm.changeBeginPos( player2.getPositionPixel( ).cpy( )
				// .add( -64f, 64f ) );
				lm.changeEndPos( currentCheckPoint.getPositionPixel( ).sub(
						chkptOffset ) );
				lm.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
						.sub( player2.getPositionPixel( ) ).len( ) );
				if ( currentCheckPoint.getPositionPixel( ).x < p2Ghost
						.getPositionPixel( ).x
						&& oldChkptPos.x > p2Ghost.getPositionPixel( ).x ) {
					p2Ghost.sprite.setScale( -1, 1 );
				} else if ( currentCheckPoint.getPositionPixel( ).x > p2Ghost
						.getPositionPixel( ).x
						&& oldChkptPos.x < p2Ghost.getPositionPixel( ).x ) {
					p2Ghost.sprite.setScale( -1, 1 );
				}
				if ( lm.atEnd( ) ) {
					spawnAtCheckPoint( player2 );
					p2Ghost.clearAnchors( );
					p2Ghost = null;
				} else {
					// p2Ghost.update( deltaTime );
					p2Ghost.updateAnchors( );
				}
			}
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
		player.body.setType( BodyType.DynamicBody );
		player.body.setTransform( currentCheckPoint.body.getPosition( ), 0.0f );
		player.body.setLinearVelocity( Vector2.Zero );
		player.getEffect( "revive" ).restartAt(
				player.getPositionPixel( ).add( 60, -30 ) );
	}

	/**
	 * removes the current instance of an resurrect screw
	 */
	private void removeRezScrew( ) {
		// remove the rez screw if both players are alive
		if ( resurrectScrew != null
				&& !resurrectScrew.getDeadPlayer( ).isPlayerDead( ) ) {
			resurrectScrew.remove( );
			if ( resurrectScrew.isRemoved( ) ) {
				resurrectScrew = null;
			} else {
				resurrectScrew.setRemove( true );
			}
		}
		if ( extraRezScrew != null
				&& !extraRezScrew.getDeadPlayer( ).isPlayerDead( ) ) {
			extraRezScrew.remove( );
			if ( extraRezScrew.isRemoved( ) ) {
				extraRezScrew = null;
			} else {
				extraRezScrew.setRemove( true );
			}
		}
	}

	public void addPlayerOne( Player p1 ) {
		this.player1 = p1;
	}

	public void addPlayerTwo( Player p2 ) {
		this.player2 = p2;
	}
}
