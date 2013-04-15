package com.blindtigergames.werescrewed.entity.animator;

/**
 * Holds and changes the spine animations for the player
 * 
 * @author Anders Sajbel
 */
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.ConcurrentState;
import com.blindtigergames.werescrewed.player.Player.PlayerDirection;
import com.blindtigergames.werescrewed.util.Util;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;

public class PlayerSpinemator implements ISpinemator {

	protected EnumMap< PlayerAnim, Animation > anims;
	protected Animation anim;
	protected Skeleton skel;
	protected PlayerAnim current;
	protected PlayerAnim previous;
	protected PlayerAnim next;
	protected Animation mixer;
	protected boolean mixerLoop;
	protected Player player;
	protected float time = 0f;
	protected float mixTime = 0f;
	protected float mixRatio = 0f;
	protected Bone root;
	protected Vector2 position = null;
	protected Vector2 scale = null;

	/**
	 * Constructor
	 * 
	 * @param thePlayer
	 *            Player the animations will belong to.
	 */
	public PlayerSpinemator( Player thePlayer ) {
		TextureAtlas atlas = WereScrewedGame.manager.getAtlas( thePlayer.type
				.getAtlasName( ) );
		SkeletonBinary sb = new SkeletonBinary( atlas );
		SkeletonData sd = sb.readSkeletonData( Gdx.files
				.internal( "data/common/spine/" + thePlayer.type.getSkeleton( )
						+ ".skel" ) );
		this.player = thePlayer;
		current = PlayerAnim.IDLE;
		previous = current;

		anims = new EnumMap< PlayerAnim, Animation >( PlayerAnim.class );

		for ( PlayerAnim a : PlayerAnim.values( ) ) {
			anims.put( a, sd.findAnimation( a.text ) );
		}

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
		skel.draw( batch );
	}

	@Override
	public void update( float delta ) {
		time += delta;
		mixTime += delta;
		skel.setFlipX( player.flipX );

		next = getCurrentAnim( );
		anim = anims.get( current );

		mixer = anims.get( next );

		if ( mixTime < anim.getDuration( ) / 2 ) {
			mixRatio = mixTime / anim.getDuration( );
			mixer.mix( skel, time, mixerLoop, mixRatio );
		} else {
			current = next;
			mixTime = 0;
		}
		if ( position != null ) {
			root.setX( position.x );
			root.setY( position.y );
		} else {
			root.setX( player.body.getWorldCenter( ).x * Util.BOX_TO_PIXEL );
			root.setY( player.body.getWorldCenter( ).y * Util.BOX_TO_PIXEL - 36 );
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
				return PlayerAnim.RUN;
			}
			return PlayerAnim.IDLE;
		case Landing:
			if ( player.getMoveState( ) == PlayerDirection.Left
					|| player.getMoveState( ) == PlayerDirection.Right ) {
				return PlayerAnim.RUN;
			}
			return PlayerAnim.IDLE;
		case Jumping:
			return PlayerAnim.JUMP_UP;
		case Falling:
			return PlayerAnim.JUMP_DOWN;
		case HeadStand:
			if ( player.getExtraState( ) == ConcurrentState.ExtraJumping ) {
				return PlayerAnim.JUMP_UP;
			} else if ( player.getExtraState( ) == ConcurrentState.ExtraFalling ) {
				return PlayerAnim.JUMP_DOWN;
			}
			return PlayerAnim.IDLE;
		case Screwing:
			return PlayerAnim.HANG;
		case Dead:
			return PlayerAnim.DEATH;
		default:
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

}
