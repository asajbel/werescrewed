package com.blindtigergames.werescrewed.checkpoints;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
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
	private Anchor chkptAnchor;
	private final float MAX_FALL_POS = -5000;

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
		chkptAnchor = new Anchor( Vector2.Zero, new Vector2( 0, 0 ),
				Player.ANCHOR_BUFFER_SIZE );
		AnchorList.getInstance( ).addAnchor( chkptAnchor );
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
			for ( Player movingPlayer : players.values( ) ) {
				if ( movingPlayer.currentMover( ) != null
						&& movingPlayer.body.getType( ) == BodyType.KinematicBody
						&& movingPlayer.currentMover( ) instanceof FollowEntityWithVelocity ) {
					FollowEntityWithVelocity playerMover = ( FollowEntityWithVelocity ) movingPlayer
							.currentMover( );
					playerMover.changeEntityToFollow( checkPoint );
				}
			}
		}
	}

	public boolean isPlayerCollidingWithCurrentChkpt( Player player ) {
		return currentCheckPoint.body.getFixtureList( ).get( 0 )
				.testPoint( player.getPosition( ) );
	}

	public void setNextChkpt( CheckPoint chkpt ) {
		if ( chkpt == this.currentCheckPoint ) {
			int chkptIndex = checkPoints.indexOf( currentCheckPoint );
			if ( chkptIndex+1 < checkPoints.size( ) ) {
				currentCheckPoint = checkPoints.get( chkptIndex+1 );
			} else{
				currentCheckPoint = checkPoints.get( 0 );				
			}
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
			for ( Player movingPlayer : players.values( ) ) {
				if ( movingPlayer.currentMover( ) != null
						&& movingPlayer.body.getType( ) == BodyType.KinematicBody
						&& movingPlayer.currentMover( ) instanceof FollowEntityWithVelocity ) {
					FollowEntityWithVelocity playerMover = ( FollowEntityWithVelocity ) movingPlayer
							.currentMover( );
					playerMover.changeEntityToFollow( currentCheckPoint );
				}
			}
		}
	}
	
	/**
	 * 
	 * @param deltaTime
	 */
	public void update( float deltaTime ) {
		animTime += deltaTime;
		noPlayersDead = true;
		int index = 0;
		while ( (this.currentCheckPoint.isRemoved( ) || this.currentCheckPoint == null ) && index < checkPoints.size( ) ) {
			if ( checkPoints.get( index ).body == null && checkPoints.get( index ).isRemoved( ) ) {
				checkPoints.remove( index );
			} else {
				index++;
			}
		}
		chkptAnchor.setPosition( currentCheckPoint.getPositionPixel( ) );
		for ( Player player : players.values( ) ) {
			if ( player.isPlayerDead( )
					&& player.getPositionPixel( ).y < MAX_FALL_POS ) {
				player.setAutoRezzing( );
			}
			if ( player.isAutoRezzing( ) ) {
				player.body.setLinearVelocity( Vector2.Zero );
				player.body.setType( BodyType.KinematicBody );
				this.startSpawn( player );
			}
			if ( player.body.getType( ) == BodyType.KinematicBody
					&& !player.isPlayerDead( )
					&& isPlayerCollidingWithCurrentChkpt( player ) ) {
				wait( player );
			}
			if ( !player.isPlayerDead( ) && ghostMap.containsKey( player.name ) ) {
				ghostMap.get( player.name ).clearAnchors( );
				ghostMap.remove( player.name );
			} else if ( player.isPlayerDead( ) ) {
				if ( !rezScrewMap.containsKey( player.name ) ) {
					handleDeadPlayer( player );
				}
				noPlayersDead = false;
			}
			if ( player.isRezzing( ) ) {
				if ( player.getRezTime( ) < rezDelay ) {
					player.setRezTime( player.getRezTime( ) + deltaTime );
				} else {
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
		player.deactivateAnchors( );

		Entity ghost;
		// build ghost entity
		SimpleSpinemator spine = new SimpleSpinemator( player.getSpinemator( )
				.getSkeletonData( ), "Ghost", true );

		ghost = new Entity( player.name + "Ghost", player.getPositionPixel( )
				.cpy( ).add( -64f, 64f ), false, spine, null );
		ghost.addBehindParticleEffect( "ghost_fount", false, true ).start( );
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
		ghost.addAnchor( anchor );
		anchor.activate( );
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
//		player.body.setType( BodyType.KinematicBody );
		rezScrewMap.put( player.name, rezzBuilder.playerOffset( true )
				.lerpMover( screwMover ).position( screwPos ).entity( currentCheckPoint.getParentSkeleton( ).getRoot( ) )
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
		player.body.setType( BodyType.KinematicBody ); 

		Filter filter = new Filter( );
		for ( Fixture f : player.body.getFixtureList( ) ) {
			f.setSensor( true );
			filter.categoryBits = Util.CATEGORY_SUBPLAYER;
			filter.maskBits = Util.CATEGORY_CHECKPOINTS  | Util.CATEGORY_SCREWS;
			f.setFilterData( filter );
		}

		player.setMoverAtCurrentState( new FollowEntityWithVelocity( player
				.getPositionPixel( ), currentCheckPoint ) );
		chkptAnchor.setPosition( currentCheckPoint.getPositionPixel( ) );
		chkptAnchor.activate( );
		player.setVisible( false, true );
		player.activateAnchors( );

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
		player.setRezzing( true );
		player.setMoverAtCurrentState( null );
		player.body.setLinearVelocity( Vector2.Zero );
		animTime = 0f;
	}

	/**
	 * respawn the player at the check point
	 * 
	 * @param player
	 */
	private void spawnAtCheckPoint( Player player ) {
		player.setRezzing( false );

		chkptAnchor.deactivate( );
		Vector2 rezPoint = new Vector2( currentCheckPoint.body.getPosition( ) );
		rezPoint.add( -60 * Util.PIXEL_TO_BOX, 36f * Util.PIXEL_TO_BOX );
		// player.activateAnchors( );
		player.body.setType( BodyType.DynamicBody );
		player.body.setTransform( rezPoint, 0.0f );
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
				if ( !rezScrewMap.get( key ).isRemoved( ) ) {
					rezScrewMap.get( key ).remove( );
				}
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
			//TextureAtlas atlas = WereScrewedGame.manager
			//		.getAtlas( "common-textures" );
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
			//TextureAtlas atlas = WereScrewedGame.manager
			//		.getAtlas( "common-textures" );
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
