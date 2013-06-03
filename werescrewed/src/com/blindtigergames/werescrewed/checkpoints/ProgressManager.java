package com.blindtigergames.werescrewed.checkpoints;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.animator.SimpleSpinemator;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.mover.FollowEntityWithVelocity;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.screws.ResurrectScrew;
import com.blindtigergames.werescrewed.entity.screws.ScrewType;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;
import com.blindtigergames.werescrewed.util.Util;

/**
 * handles all of the progress through checkpoints also handles re-spawning with
 * either checkpoints or resurrection screws
 * 
 * @author Dennis Foley
 * 
 */
public class ProgressManager {

	public CheckPoint currentCheckPoint;
	private HashMap< String, Player > players;
	private HashMap< String, ResurrectScrew > rezScrewMap;
	private HashMap< String, Entity > ghostMap;
	public ArrayList< CheckPoint > checkPoints;
	// private HashMap< String, TextureRegion > ghostTextures;
	private World world;
	private Vector2 oldChkptPos;
	private final Vector2 chkptOffset = new Vector2( 16, 8 );
	private final Vector2 hoverOffset = new Vector2( -64, 64 );
	private final Vector2 screwLeftOffset = new Vector2( -240, 150 );
	private final Vector2 screwRightOffset = new Vector2( 270, 150 );
	private float animTime = 0f;
	private float rezDelay = Float.MAX_VALUE;
	boolean noPlayersDead = false;

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param world
	 */
	public ProgressManager( Player p1, Player p2, World world ) {
		players = new HashMap< String, Player >( );
		rezScrewMap = new HashMap< String, ResurrectScrew >( );
		ghostMap = new HashMap< String, Entity >( );
		checkPoints = new ArrayList< CheckPoint >( );
		// ghostTextures = new HashMap< String, TextureRegion >( );
		// TextureAtlas atlas = WereScrewedGame.manager.getAtlas(
		// "common-textures");
		if ( p1 != null ) {
			players.put( p1.name, p1 );
			// ghostTextures.put(
			// p1.name,
			// atlas.findRegion( "player_r_m_ghost" ) );
		}
		if ( p2 != null ) {
			players.put( p2.name, p2 );
			// ghostTextures
			// .put( p2.name, atlas.findRegion( "player_female_idle_ghost" ) );
		}
		this.world = world;
	}

	/**
	 * Change the current check point when the player hits a new checkPoint.
	 * Removes the old check point from the list of checkpoints
	 * 
	 * @param checkPoint
	 */
	public void hitNewCheckPoint( CheckPoint checkPoint, Player player ) {
		// If the checkpoint hit is not the currently activated one
		if ( currentCheckPoint != checkPoint
				&& player.body.getType( ) != BodyType.KinematicBody
				&& !player.isPlayerDead( ) ) {
			// Deactivate the current checkpoint
			currentCheckPoint.deactivate( );
			oldChkptPos = currentCheckPoint.getPositionPixel( ).cpy( );
			if ( !noPlayersDead ) {
				checkPoint.getSpinemator( ).changeAnimation( "wait", true );
			}
			// Then set it to the one the players hit
			currentCheckPoint = checkPoint;
			// If player 1's Ghost is active
			for ( Entity ghost : ghostMap.values( ) ) {
				if ( ghost.currentMover( ) instanceof LerpMover ) {
					LerpMover lm = ( LerpMover ) ghost.currentMover( );
					// Change the destination
					lm.changeBeginPos( ghost.getPositionPixel( ) );
					lm.setAlpha( 0 );
					lm.changeEndPos( currentCheckPoint.getPositionPixel( ).sub(
							chkptOffset ) );
					// Adjust the speed
					lm.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
							.sub( ghost.getPositionPixel( ) ).len( ) );
					if ( currentCheckPoint.getPositionPixel( ).x < ghost
							.getPositionPixel( ).x
							&& oldChkptPos.x > ghost.getPositionPixel( ).x ) {
						ghost.getSpinemator( ).flipX( true );
					} else if ( currentCheckPoint.getPositionPixel( ).x > ghost
							.getPositionPixel( ).x
							&& oldChkptPos.x < ghost.getPositionPixel( ).x ) {
						ghost.getSpinemator( ).flipX( false );
					}
				}
			}
		} else {
//			if ( player.body.getType( ) == BodyType.KinematicBody && !player.isPlayerDead( ) ) {
//				player.setMoverAtCurrentState( null );
//				player.setDeadPlayerHitCheckpnt( true );
//			}
		}
	}
	

	/**
	 * end hit checkpoint
	 */
	public void endHitCheckPoint( CheckPoint chkpt, Player player ) {
		//if ( chkpt == currentCheckPoint ) {
		//	player.setDeadPlayerHitCheckpnt( false );
		//}
	}
	
	public boolean isPlayerCollidingWithCurrentChkpt ( Player player ) {
		return currentCheckPoint.body.getFixtureList( ).get( 0 ).testPoint( player.getPosition( ) );
	}
	
	/**
	 * 
	 * @param deltaTime
	 */
	public void update( float deltaTime ) {
		animTime += deltaTime;
		noPlayersDead = true;
		int index = 0;
		while ( this.currentCheckPoint == null && index < checkPoints.size( ) ) {
			if ( checkPoints.get( index ) != null ) {
				this.currentCheckPoint = checkPoints.get( index );
			} else {
				checkPoints.remove( index );
			}
			index++;
		}
		for ( Player player : players.values( ) ) {
			if ( player.isAutoRezzing( ) ) {
				player.body.setLinearVelocity( Vector2.Zero );
				player.body.setType( BodyType.KinematicBody );
				this.startSpawn( player );
			}
			if ( player.body.getType( ) == BodyType.KinematicBody && 
					isPlayerCollidingWithCurrentChkpt( player ) ) {
				wait( player );
				//Gdx.app.log( " progress 1", "player is waiting to rez");
			}
			if ( !player.isPlayerDead( ) && ghostMap.containsKey( player.name ) ) {
				ghostMap.get( player.name ).clearAnchors( );
				ghostMap.remove( player.name );
			} else if ( player.isPlayerDead( ) ) {
				if ( !rezScrewMap.containsKey( player.name ) ) {
					handleDeadPlayer( player );
				} else {
					ResurrectScrew rezScrew = rezScrewMap.get( player.name );
					if ( ( rezScrew.entityJoint.getBodyA( ) != null && rezScrew.entityJoint.getBodyA( ) != rezScrew.body
							&& rezScrew.entityJoint.getBodyA( ) != null && ( !rezScrew.entityJoint
							.getBodyA( ).isActive( ) || !rezScrew.entityJoint
							.getBodyA( ).isAwake( ) ) )
							|| ( rezScrew.entityJoint.getBodyB( ) != null && rezScrew.entityJoint.getBodyB( ) != rezScrew.body
									&& rezScrew.entityJoint.getBodyA( ) != null && ( !rezScrew.entityJoint
									.getBodyA( ).isActive( ) || !rezScrew.entityJoint
									.getBodyA( ).isAwake( ) ) ) ) {
						rezScrewMap.get( player.name ).connectScrewToEntity(
								currentCheckPoint );
					}
				}
				noPlayersDead = false;
			}
			if ( player.isRezzing( ) ) {
				//Gdx.app.log( " progress 2", "player is rezzing");
				if ( player.getRezTime( ) < rezDelay ) {
					//Gdx.app.log( " progress 3", "player is rezzing");
					player.setRezTime( player.getRezTime( ) + deltaTime );
				} else {
					//Gdx.app.log( " progress 2", "player is spawning");
					spawnAtCheckPoint( player );
				}
			}
		}
		if ( noPlayersDead ) {
			removeRezScrew( );
		}

		// update the rez screw if it exists
		for ( ResurrectScrew rezScrew : rezScrewMap.values( ) ) {
			rezScrew.update( deltaTime );
		}
		updateGhosts( deltaTime );

		oldChkptPos = currentCheckPoint.getPositionPixel( ).cpy( );

		if ( !currentCheckPoint.getSpinemator( ).getCurrentAnimation( )
				.equals( "on-idle" )
				&& !currentCheckPoint.getSpinemator( ).getCurrentAnimation( )
						.equals( "wait" )
				&& animTime > currentCheckPoint.getSpinemator( )
						.getAnimationDuration( ) ) {
			if ( noPlayersDead ) {
				currentCheckPoint.getSpinemator( ).changeAnimation( "on-idle",
						true );
			} else {
				currentCheckPoint.getSpinemator( ).changeAnimation( "wait",
						true );
			}
			animTime = 0f;
		}
	}

	/**
	 * draw a range of three checkpoints and draw the resurrection screw if
	 * there is one
	 * 
	 * @param batch
	 */
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		for ( ResurrectScrew rezScrew : rezScrewMap.values( ) ) {
			rezScrew.draw( batch, deltaTime, camera );
		}
		for ( String key : ghostMap.keySet( ) ) {
			if ( ghostMap.get( key ).currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) ghostMap.get( key ).currentMover( );
				if ( players.get( key ).getState( ) == PlayerState.RespawnMode ) {
					lm.moveStep( );
				}
				ghostMap.get( key ).getSpinemator( ).setPosition( lm.getPos( ) );
				ghostMap.get( key ).draw( batch, deltaTime, camera );
			}
		}
	}

	/**
	 * when a player dies create a resurrection screw
	 */
	private void handleDeadPlayer( Player player ) {
		if ( !rezScrewMap.containsKey( player.name ) ) {
			buildRezScrew( player );
		}
		if ( !ghostMap.containsKey( player.name ) ) {
			currentCheckPoint.getSpinemator( ).changeAnimation( "wait", true );
			animTime = 0f;
			buildGhost( player );
		}
	}

	/**
	 * 
	 * @param player
	 */
	private void buildGhost( Player player ) {
		Entity ghost;
		// build ghost entity
		// Gdx.app.log("ghost:", player.name);
		SimpleSpinemator spine = new SimpleSpinemator( player.getSpinemator( )
				.getSkeletonData( ), "Ghost", true );

		ghost = new Entity( player.name + "Ghost", player.getPositionPixel( )
				.cpy( ).add( -64f, 64f ), false, spine, null );
		// build ghost mover
		LerpMover ghostMover = new LerpMover( player.getPositionPixel( ).cpy( )
				.add( hoverOffset ), currentCheckPoint.getPositionPixel( ).sub(
				chkptOffset ), LinearAxis.DIAGONAL );
		// set speed of ghost mover
		ghostMover.setSpeed( 10f / currentCheckPoint.getPositionPixel( )
				.sub( player.getPositionPixel( ) ).len( ) );
		ghost.setMoverAtCurrentState( ghostMover );
		// build ghost anchor
		Anchor anchor = new Anchor( player.getPositionPixel( ), new Vector2( 0,
				0 ), Player.ANCHOR_BUFFER_SIZE );
		anchor.activate( );
		ghost.addAnchor( anchor );
		// face the direction of the checkpoint
		if ( currentCheckPoint.getPositionPixel( ).x < ghost.getPositionPixel( ).x ) {
			ghost.getSpinemator( ).flipX( true );
		}
		ghostMap.put( player.name, ghost );
	}

	/**
	 * 
	 * @param player
	 */
	private void buildRezScrew( Player player ) {
		ScrewBuilder rezzBuilder = new ScrewBuilder( ).screwType(
				ScrewType.SCREW_RESURRECT ).world( world );
		Vector2 screwPos = new Vector2( screwLeftOffset );
		rezzBuilder.player( player );
		LerpMover screwMover;
		// move the screw to the opposite direction the player is moving
		if ( player.body.getLinearVelocity( ).x < 0 ) {
			screwPos = new Vector2( screwRightOffset );
			screwMover = new LerpMover( player.getPositionPixel( ), player
					.getPositionPixel( ).add( screwPos ), LinearAxis.DIAGONAL );
		} else {
			screwMover = new LerpMover( player.getPositionPixel( ), player
					.getPositionPixel( ).add( screwPos ).sub( screwPos ),
					LinearAxis.DIAGONAL );
		}
		player.body.setLinearVelocity( Vector2.Zero );
		player.body.setType( BodyType.KinematicBody );
		Entity jointE = player.getLastPlatform( );
		if ( jointE == null ) {
			jointE = currentCheckPoint;
		}
		rezScrewMap.put( player.name, rezzBuilder.playerOffset( true )
				.lerpMover( screwMover ).position( screwPos ).entity( jointE )
				.buildRezzScrew( ) );
	}

	/**
	 * 
	 */
	private void updateGhosts( float deltaTime ) {
		for ( String key : new ArrayList< String >( ghostMap.keySet( ) ) ) {
			if ( ghostMap.get( key ).currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) ghostMap.get( key ).currentMover( );
				lm.changeEndPos( currentCheckPoint.getPositionPixel( ).sub(
						chkptOffset ) );
				lm.setSpeed( 20f / currentCheckPoint.getPositionPixel( )
						.sub( players.get( key ).getPositionPixel( ) ).len( ) );
				if ( currentCheckPoint.getPositionPixel( ).x < ghostMap.get(
						key ).getPositionPixel( ).x
						&& oldChkptPos.x > ghostMap.get( key )
								.getPositionPixel( ).x ) {
					ghostMap.get( key ).getSpinemator( ).flipX( true );
				} else if ( currentCheckPoint.getPositionPixel( ).x > ghostMap
						.get( key ).getPositionPixel( ).x
						&& oldChkptPos.x < ghostMap.get( key )
								.getPositionPixel( ).x ) {
					ghostMap.get( key ).getSpinemator( ).flipX( false );
				}
				if ( lm.atEnd( ) ) {
					startSpawn( players.get( key ) );
					ghostMap.get( key ).clearAnchors( );
					ghostMap.remove( key );
				} else {
					ghostMap.get( key ).updateAnchors( );
					ghostMap.get( key ).update( deltaTime );
				}
			}
		}
	}

	private void startSpawn( Player player ) {
		removeRezScrew( );
		player.setRezTime( 0f );
		player.respawnPlayer( );
		Vector2 rezPoint = new Vector2( currentCheckPoint.getPositionPixel( ) );
		// rezPoint.add( -60 * Util.PIXEL_TO_BOX , 60f * Util.PIXEL_TO_BOX );

		Vector2 diff = rezPoint.sub( player.getPositionPixel( ) ).mul( 0.25f );

		player.setMoverAtCurrentState( new FollowEntityWithVelocity( player
				.getPositionPixel( ), currentCheckPoint, Vector2.Zero, diff ) );
		// player.deactivateAnchors( );
		// player.body.setLinearVelocity( diff );
		player.setVisible( false, true );

		if ( rezScrewMap.containsKey( player.name ) ) {
			rezScrewMap.get( player.name ).remove( );
			if ( rezScrewMap.get( player.name ).isRemoved( ) ) {
				rezScrewMap.remove( player.name );
			}
		}

	}

	private void wait( Player player ) {
		currentCheckPoint.getSpinemator( ).changeAnimation( "birth", false );
		rezDelay = currentCheckPoint.getSpinemator( ).getAnimationDuration( ) / 10f;
		player.setVisible( true );
		player.setRezzing( true );
		animTime = 0f;
	}

	/**
	 * respawn the player at the check point
	 * 
	 * @param player
	 */
	private void spawnAtCheckPoint( Player player ) {
		player.setRezzing( false );
		// tele-port to checkpoint with velocity
		// float frameRate = 1 / deltaTime;
		// bring the player back to life
		// remove the instance of the rez screw

		// move the player to the current checkpoint
		// tele-port to checkpoint with velocity
		// move the player to checkpoint with transform collision problems
		// player.body.setType( BodyType.DynamicBody );
		// player.body.setTransform( rezPoint, 0.0f );
		// player.body.setLinearVelocity( Vector2.Zero );
		Vector2 rezPoint = new Vector2( currentCheckPoint.body.getPosition( ) );
		rezPoint.add( -60 * Util.PIXEL_TO_BOX, 36f * Util.PIXEL_TO_BOX );
		player.activateAnchors( );
		player.setMoverAtCurrentState( null );
		player.body.setTransform( rezPoint, 0.0f );
		player.body.setType( BodyType.DynamicBody );
		player.body.setLinearVelocity( Vector2.Zero );
		player.setVisible( true );
		Filter filter = new Filter( );
		for ( Fixture f : player.body.getFixtureList( ) ) {
			if ( f != player.rightSensor && f != player.leftSensor
					&& f != player.topSensor ) {
				f.setSensor( false );
			}
			filter.categoryBits = Util.CATEGORY_PLAYER;
			filter.maskBits = Util.CATEGORY_EVERYTHING;
			f.setFilterData( filter );
		}
	}

	/**
	 * removes the current instance of an resurrect screw
	 */
	private void removeRezScrew( ) {
		for ( String key : new ArrayList< String >( rezScrewMap.keySet( ) ) ) {
			if ( !rezScrewMap.get( key ).getDeadPlayer( ).isPlayerDead( ) ) {
				rezScrewMap.get( key ).remove( );
				if ( rezScrewMap.get( key ).isRemoved( ) ) {
					rezScrewMap.remove( key );
				}
			}
		}
	}

	/**
	 * old functions use addPlayer Instead
	 * 
	 * @param p1
	 */
	public void addPlayerOne( Player p1 ) {
		if ( !players.containsKey( p1.name ) ) {
			@SuppressWarnings( "unused" )
			TextureAtlas atlas = WereScrewedGame.manager
					.getAtlas( "common-textures" );
			this.players.put( p1.name, p1 );
			// ghostTextures.put(
			// p1.name,
			// atlas.findRegion( "player_r_m_ghost" ) );
		}
	}

	/**
	 * old functions use addPlayer Instead
	 * 
	 * @param p2
	 */
	public void addPlayerTwo( Player p2 ) {
		if ( !players.containsKey( p2.name ) ) {
			@SuppressWarnings( "unused" )
			TextureAtlas atlas = WereScrewedGame.manager
					.getAtlas( "common-textures" );
			this.players.put( p2.name, p2 );
			// ghostTextures
			// .put( p2.name, atlas.findRegion( "player_female_idle_ghost" ) );
		}
	}

	/**
	 * 
	 * @param player
	 */
	public void addPlayer( Player player ) {
		if ( !players.containsKey( player.name ) ) {
			this.players.put( player.name, player );
		}
	}

	// public void addGhostTexture( Player player, TextureRegion ghostTexture )
	// {
	// if ( ghostTexture != null && !ghostTextures.containsKey( player.name )
	// && players.containsKey( player.name ) ) {
	// // ghostTextures.put( player.name, ghostTexture );
	// }
	// }
}
