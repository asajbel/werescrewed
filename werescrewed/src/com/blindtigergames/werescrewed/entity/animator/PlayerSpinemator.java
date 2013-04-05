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

	public EnumMap< PlayerAnim, Animation > anims;
	public Animation anim;
	public Skeleton skel;
	public PlayerAnim current;
	public Player player;
	protected float totalTime = 0f;
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

		anims = new EnumMap< PlayerAnim, Animation >( PlayerAnim.class );

		for ( PlayerAnim a : PlayerAnim.values( ) ) {
			anims.put( a, sd.findAnimation( a.text ) );
		}

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
		totalTime += delta;
		Gdx.gl.glClearColor( 1, 1, 1, 1 );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		current = getCurrentAnim(); 
		anim = anims.get( current );
		anim.apply( skel, totalTime, true );
		root.setX( player.body.getWorldCenter( ).x * Util.BOX_TO_PIXEL );
		root.setY( player.body.getWorldCenter( ).y * Util.BOX_TO_PIXEL );
		skel.updateWorldTransform( );
		skel.update( delta );

//		// Position each attachment body.
//		for (Slot slot : skel.getSlots()) {
//			if (!(slot.getAttachment() instanceof Box2dAttachment)) continue;
//			Box2dAttachment attachment = (Box2dAttachment)slot.getAttachment();
//			if (attachment.body == null) continue;
//			attachment.body.setTransform(slot.getBone().getWorldX(), slot.getBone().getWorldY(), slot.getBone().getWorldRotation()
//				* MathUtils.degRad);
//		}
	}
	
	protected PlayerAnim getCurrentAnim(){
		switch (player.getState( )){
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
	
//	static class Box2dAttachment extends RegionAttachment {
//		Body body;
//
//		public Box2dAttachment (String name) {
//			super(name);
//		}
//	}

}
