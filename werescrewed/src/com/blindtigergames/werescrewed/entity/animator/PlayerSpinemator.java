package com.blindtigergames.werescrewed.entity.animator;

/**
 * Holds and changes the spine animations for the player
 * 
 * @author Anders Sajbel
 */
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.ConcurrentState;
import com.blindtigergames.werescrewed.player.Player.PlayerDirection;
import com.blindtigergames.werescrewed.player.Player.PlayerState;
import com.blindtigergames.werescrewed.util.Util;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class PlayerSpinemator implements ISpinemator {

	protected EnumMap< PlayerAnim, Animation > anims;
	protected Animation anim;
	protected Skeleton skel;
	protected SkeletonRenderer skelDraw = new SkeletonRenderer( );
	protected PlayerAnim current;
	protected PlayerAnim previous;
	protected PlayerAnim next;
	protected Animation mixer;
	private Animation draw;
	private Animation stow;
	protected static final String READY = "_screw_ready";
	protected String addScrewReady = "";
	protected boolean mixerLoop;
	protected Player player;
	protected float time = 0f;
	protected float mixTime = 0f;
	protected float startTime = 0f;
	protected float readyTime = 0f;
	protected float mixRatio = 0f;
	protected Bone root;
	protected Vector2 position = null;
	protected Vector2 scale = null;
	protected boolean flipX = false;
	private TextureAtlas bodyAtlas;

	private enum ScrewState {
		IGNORE, READY, DRAW, STOW
	}

	ScrewState animState = ScrewState.IGNORE;

	/**
	 * Constructor
	 * 
	 * @param thePlayer
	 *            Player the animations will belong to.
	 */
	public PlayerSpinemator( Player thePlayer ) {
		bodyAtlas = WereScrewedGame.manager.getAtlas( thePlayer.type
				.getAtlasName( ) );
		SkeletonBinary sb = new SkeletonBinary( bodyAtlas );
		SkeletonData sd = sb.readSkeletonData( Gdx.files
				.internal( "data/common/spine/" + thePlayer.type.getSkeleton( )
						+ ".skel" ) );
		this.player = thePlayer;
		current = PlayerAnim.IDLE;
		previous = current;

		anims = new EnumMap< PlayerAnim, Animation >( PlayerAnim.class );

		for ( PlayerAnim a : PlayerAnim.values( ) ) {
			Animation temp = sd.findAnimation( a.text );
			if ( temp != null ) {
				anims.put( a, temp );
			}
		}

		draw = anims.get( PlayerAnim.SCREW_DRAW );
		stow = anims.get( PlayerAnim.SCREW_STOW );

		anim = anims.get( current );
		mixer = anim;
		mixerLoop = current.loopBool;
		skel = new com.esotericsoftware.spine.Skeleton( sd );
		skel.setToBindPose( );
		root = skel.getRootBone( );
		skel.updateWorldTransform( );
	}

	@Override
	public void draw( SpriteBatch batch ) {
		skelDraw.draw( batch, skel );
	}

	@Override
	public void update( float delta ) {
		time += delta;
		mixTime += delta;

		if (!current.singleTick || mixTime >= mixer.getDuration( ) / 2 )
			next = getCurrentAnim( );

		if ( current != next ) {
			current = next;
			time = mixTime;
			startTime = 0f;
			mixTime = 0;
		}
		anim = anims.get( previous );

		if ( current.start != null && startTime < mixer.getDuration( ) ) {
			mixer = anims.get( current.start );
			startTime += delta;
		} else {
			mixer = anims.get( current );
		}

		switch ( current ) {
		case RUN:
			mixRatio = player.getAbsAnalogXRatio( );
			anims.get( PlayerAnim.IDLE ).apply( skel, time,
					PlayerAnim.IDLE.loopBool );
			mixer.mix( skel, time, current.loopBool, mixRatio );
			break;
		case RUN_SCREW:
			mixRatio = player.getAbsAnalogXRatio( );
			anims.get( PlayerAnim.IDLE_SCREW ).apply( skel, time,
					PlayerAnim.IDLE_SCREW.loopBool );
			mixer.mix( skel, time, current.loopBool, mixRatio );
			break;
		case SCREWING_GROUND:
		case SCREWING_HANG:
			float screwAmount = ( float ) player.getCurrentScrew( ).getDepth( )
					/ ( float ) player.getCurrentScrew( ).getMaxDepth( )
					* mixer.getDuration( );
			if ( flipX ) {
				screwAmount = mixer.getDuration( ) - screwAmount;
			}
			if ( !Float.isNaN( screwAmount ) ) {
				mixer.apply( skel, screwAmount, next.loopBool );
				break;
			} else
				anims.get( PlayerAnim.HANG ).apply( skel, time,
						PlayerAnim.HANG.loopBool );
			break;
		default:
			mixRatio = mixTime / anim.getDuration( );
			mixer.mix( skel, time, next.loopBool, mixRatio );
			if ( mixTime >= anim.getDuration( ) / 2 ) {
			
				previous = current;
				mixTime = 0;
			}
			break;
		}

		if ( player.getExtraState( ) == ConcurrentState.ScrewReady
				&& player.getState( ) != PlayerState.Screwing ) {
			switch ( animState ) {
			case IGNORE:
				readyTime = 0f;
				draw.apply( skel, readyTime, PlayerAnim.SCREW_DRAW.loopBool );
				animState = ScrewState.DRAW;
				break;
			case DRAW:
				if ( readyTime < draw.getDuration( ) ) {
					draw.apply( skel, readyTime, PlayerAnim.SCREW_DRAW.loopBool );
					readyTime += delta;
				} else {
					animState = ScrewState.READY;
				}
				break;
			case READY:
				readyTime = 0f;
				break;
			case STOW:
				float alpha = 1 - readyTime / stow.getDuration( );
				readyTime = draw.getDuration( ) * alpha;
				draw.mix( skel, readyTime, PlayerAnim.SCREW_DRAW.loopBool,
						alpha );
				animState = ScrewState.DRAW;
				break;
			default:
				break;

			}
		} else {
			switch ( animState ) {
			case IGNORE:
				readyTime = 0f;
				break;
			case DRAW:
				float alpha = 1 - readyTime / draw.getDuration( );
				readyTime = stow.getDuration( ) * alpha;
				stow.mix( skel, readyTime, PlayerAnim.SCREW_STOW.loopBool,
						alpha );
				animState = ScrewState.STOW;
				break;
			case READY:
				readyTime = 0f;
				stow.apply( skel, readyTime, PlayerAnim.SCREW_STOW.loopBool );
				animState = ScrewState.STOW;
				break;
			case STOW:
				if ( readyTime < draw.getDuration( ) ) {
					readyTime += delta;
					stow.apply( skel, readyTime, PlayerAnim.SCREW_STOW.loopBool );
				} else {
					animState = ScrewState.IGNORE;
				}
				break;
			default:
				break;

			}
		}

		if (current != PlayerAnim.TURN && current != PlayerAnim.TURN_SCREW)
			skel.setFlipX( flipX );
		if ( position != null ) {
			root.setX( position.x );
			root.setY( position.y );
		} else {
			root.setX( player.body.getWorldCenter( ).x * Util.BOX_TO_PIXEL );
			root.setY( player.body.getWorldCenter( ).y * Util.BOX_TO_PIXEL - 39 );
		}
		if ( scale != null ) {
			root.setScaleX( scale.x );
			root.setScaleY( scale.y );
		} else {
			root.setScaleX( 1f );
			root.setScaleY( 1f );
		}
		skel.updateWorldTransform( );
		position = null;
	}

	/**
	 * Returns the player animation that is currently used
	 * 
	 * @return The PlayerAnim value that is the current animation
	 */
	protected PlayerAnim getCurrentAnim( ) {
		switch ( player.getState( ) ) {
		case Standing:
			if ( player.getMoveState( ) == PlayerDirection.Left
					|| player.getMoveState( ) == PlayerDirection.Right ) {
				switch ( player.getMoveState( ) ) {
				case Idle:
					break;
				case Left:
//					if (!flipX) {
//						flipX = true;
//						if (player.getExtraState( ) == ConcurrentState.ScrewReady)
//							return PlayerAnim.TURN_SCREW; 
//						return PlayerAnim.TURN; 
//					}
					flipX = true;
					break;
				case Right:
//					if (flipX) {
//						flipX = false;
//						if (player.getExtraState( ) == ConcurrentState.ScrewReady)
//							return PlayerAnim.TURN_SCREW; 
//						return PlayerAnim.TURN; 
//					}
					flipX = false;
					break;
				default:
					break;
				}
				if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
					return PlayerAnim.RUN_SCREW;
				}
				return PlayerAnim.RUN;
			}
			if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
				return PlayerAnim.IDLE_SCREW;
			}
			return PlayerAnim.IDLE;
		case Landing:
			if ( player.getMoveState( ) == PlayerDirection.Left
					|| player.getMoveState( ) == PlayerDirection.Right ) {
				switch ( player.getMoveState( ) ) {
				case Idle:
					break;
				case Left:
					flipX = true;
					break;
				case Right:
					flipX = false;
					break;
				default:
					break;
				}
				if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
					return PlayerAnim.LAND_SCREW;
				}
				return PlayerAnim.LAND;
			}
			if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
				return PlayerAnim.LAND_SCREW;
			}
			return PlayerAnim.LAND;
		case Jumping:
			switch ( player.getMoveState( ) ) {
			case Idle:
				break;
			case Left:
				flipX = true;
				break;
			case Right:
				flipX = false;
				break;
			default:
				break;
			}
			if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
				return PlayerAnim.JUMP_UP_SCREW;
			}
			return PlayerAnim.JUMP_UP;
		case Falling:
			if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
				return PlayerAnim.FALL_SCREW;
			}
			return PlayerAnim.FALL_IDLE;
		case HeadStand:
			if ( player.getExtraState( ) == ConcurrentState.ExtraJumping ) {
				return PlayerAnim.JUMP_UP;
			} else if ( player.getExtraState( ) == ConcurrentState.ExtraFalling ) {
				return PlayerAnim.FALL_IDLE;
			} else if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
				return PlayerAnim.IDLE_SCREW;
			}
			return PlayerAnim.IDLE;
		case Screwing:
			return PlayerAnim.SCREWING_HANG;
		case Dead:
			return PlayerAnim.DEATH;
		default:
			if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
				return PlayerAnim.IDLE_SCREW;
			}
			return PlayerAnim.IDLE;
		}
	}

	@Override
	public void setPosition( Vector2 pos ) {
		position = pos;
	}

	@Override
	public void setScale( Vector2 scale ) {
		this.scale = scale;
	}

	/**
	 * Returns atlas that has all the body parts for this player
	 * 
	 * @return
	 */
	public TextureAtlas getBodyAtlas( ) {
		return bodyAtlas;
	}

}
