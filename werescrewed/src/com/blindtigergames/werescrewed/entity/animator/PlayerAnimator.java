package com.blindtigergames.werescrewed.entity.animator;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.graphics.TextureAtlas.AtlasRegion;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.ConcurrentState;
import com.blindtigergames.werescrewed.player.Player.PlayerDirection;

public class PlayerAnimator implements IAnimator {
	public enum PlayerAnim{
		IDLE("idle", 0)
		,RUN("run", 0)
		,HANG("hang", 0)
		,JUMP_UP("jump_up", LoopBehavior.STOP, 1)
		,JUMP_DOWN("jump_down", LoopBehavior.STOP, 1)
		;
		String text;
		LoopBehavior loop;
		int atlas;
		PlayerAnim(String t){
			this(t, 0);
		}
		PlayerAnim(String t, int a){
			this(t, LoopBehavior.LOOP, a);
		}
		PlayerAnim(String t, LoopBehavior l, int a){
			text = t;
			loop = l;
			atlas = a;
		}
		public String toString(){
			return text;
		}
		public static PlayerAnim fromString(String t){
			for (PlayerAnim a: PlayerAnim.values( )){
				if (a.text.equals( t ) )
					return a;
			}
			return IDLE;
		}
	}
	
	public EnumMap<PlayerAnim, IAnimator> anims;
	public PlayerAnim current;
	public Player player;
	public String prefix;
	
	public PlayerAnimator(Array<TextureAtlas> atlases, Player p){
		player = p;
		SimpleFrameAnimator anim;
		Array<AtlasRegion> regions;
		anims = new EnumMap<PlayerAnim, IAnimator>(PlayerAnim.class);
		current = PlayerAnim.IDLE;
		TextureAtlas atlas;
		for (PlayerAnim a: PlayerAnim.values( )){
			anim = null;
			for (int i = 0; i < atlases.size && anim == null; i++){
				atlas = atlases.get( i );
				if (atlas != null){
					regions = atlas.findRegions( a.text );
					if (regions != null && regions.size > 0){
						anim = new SimpleFrameAnimator()
								.atlas( a.atlas )
								.maxFrames( regions.size );
								//.loop( a.loop );
					}
				}				
			}
			if (anim != null){
				anims.put( a, anim);
			} else {
				Gdx.app.log( "PlayerAnimator", "Failed to find corresponding texture atlas for "+a.text+"." );
			}
		}
	}
	
	@Override
	public void update( float dT ) {
		if (current != getCurrentAnim()){
			current = getCurrentAnim();
			anims.get(current).reset();
		}
		anims.get(current).update( dT );
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
		default:
			return PlayerAnim.IDLE;			
		}
	}
	
	@Override
	public String getRegion( ) {
		return current.text;
	}
	
	@Override
	public void setRegion( String r ) {
		current = PlayerAnim.fromString( r );
		anims.get( current ).reset( );
	}

	@Override
	public int getIndex( ) {
		return anims.get( current ).getFrame();
	}

	@Override
	public int getFrame( ) {
		return anims.get( current ).getFrame();
	}

	@Override
	public void setIndex( int f ) {
	}

	@Override
	public void setFrame( int f ) {
		anims.get( current ).setFrame( f );
	}
	
	@Override
	public void setAtlas( int a ){
	}

	@Override
	public int getAtlas( ){
		return current.atlas;
	}
	
	@Override
	public void reset(){
		current = PlayerAnim.IDLE;
		anims.get( current ).reset( );
	}
}
