package com.blindtigergames.werescrewed.entity.animator;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	Animation walkAnimation;
	Animation jumpAnimation;
	protected Player player;
	protected float time = 0f;
	protected Bone root;

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
		
		jumpAnimation = sd.findAnimation( PlayerAnim.JUMP_UP.text );
		walkAnimation = sd.findAnimation( PlayerAnim.RUN.text ); 

		anim = anims.get( current );
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
		current = getCurrentAnim( );
		anim = anims.get( previous );
		skel.setFlipX( player.flipX );
		anim.apply( skel, time, true );
//		if ( current != previous ) {
//			float jumpTime = time - 1;
//			float mixTime = anims.get( current ).getDuration( );
//			if ( jumpTime > mixTime )
//				anims.get( current ).apply( skel, jumpTime, false );
//			else
//				anims.get( current ).mix( skel, jumpTime, false,
//						jumpTime / mixTime );
//			if ( time > 4 )
//				time = 0;
//		}
		
		if ( current != previous ) {
			anim = anims.get( current );
			anim.mix( skel, time, current.loopBool, 0.5f );
		} else {
			anim.apply( skel, time, true );
		}

//		walkAnimation.apply(skel, time, true);
//		if (time > 1) {
//			float jumpTime = time - 1;
//			float mixTime = 0.4f;
//			if (jumpTime > mixTime)
//				jumpAnimation.apply(skel, jumpTime, false);
//			else
//				jumpAnimation.mix(skel, jumpTime, false, jumpTime / mixTime);
//			if (time > 4) time = 0;
//		}
		root.setX( player.body.getWorldCenter( ).x * Util.BOX_TO_PIXEL );
		root.setY( player.body.getWorldCenter( ).y * Util.BOX_TO_PIXEL - 40 );
		root.setScaleX( 1f );
		root.setScaleY( 1f );
		skel.updateWorldTransform( );
		previous = current;
		

		// // Position each attachment body.
		// for (Slot slot : skel.getSlots()) {
		// if (!(slot.getAttachment() instanceof Box2dAttachment)) continue;
		// Box2dAttachment attachment = (Box2dAttachment)slot.getAttachment();
		// if (attachment.body == null) continue;
		// attachment.body.setTransform(slot.getBone().getWorldX(),
		// slot.getBone().getWorldY(), slot.getBone().getWorldRotation()
		// * MathUtils.degRad);
		// }
	}

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
		root.setX( pos.x );
		root.setY( pos.y );
	}

	@Override
	public void setScale( Vector2 scale ) {
		root.setScaleX( scale.x );
		root.setScaleY( scale.y );
	}

	// static class Box2dAttachment extends RegionAttachment {
	// Body body;
	//
	// public Box2dAttachment (String name) {
	// super(name);
	// }
	// }

}
