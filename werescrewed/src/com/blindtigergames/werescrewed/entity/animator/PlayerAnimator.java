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
import com.blindtigergames.werescrewed.player.Player.PlayerState;

public class PlayerAnimator implements IAnimator {
	public enum PlayerAnim{
		IDLE("idle")
		,RUN("run")
		,JUMP_UP("jump_up", LoopBehavior.STOP)
		,JUMP_DOWN("jump_down", LoopBehavior.STOP)
		;
		String text;
		LoopBehavior loop;
		PlayerAnim(String t){
			this(t, LoopBehavior.LOOP);
		}
		PlayerAnim(String t, LoopBehavior l){
			text = t;
			loop = l;
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
	
	public PlayerAnimator(TextureAtlas atlas, Player p){
		player = p;
		SimpleFrameAnimator anim;
		Array<AtlasRegion> regions;
		anims = new EnumMap<PlayerAnim, IAnimator>(PlayerAnim.class);
		current = PlayerAnim.IDLE;
		for (PlayerAnim a: PlayerAnim.values( )){
			regions = atlas.findRegions( a.text );
			if (regions != null && regions.size > 0){
				anim = new SimpleFrameAnimator()
						.prefix( a.toString( ) )
						.maxFrames( regions.size );
						//.loop( a.loop );
				anims.put( a, anim);
			} else {
				  Gdx.app.log( "PlayerAnimator", "No region found for ["+a.text+"]." );
			}			
		}
	}
	
	@Override
	public void update( float dT ) {
		current = getCurrentAnim();
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
			if ( player.getExtraState( ) == ConcurrentState.HeadStandJumping ) {
				return PlayerAnim.JUMP_UP;
			} else if ( player.getExtraState( ) == ConcurrentState.HeadStandJumping ) {
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
	public void reset(){
		current = PlayerAnim.IDLE;
		anims.get( current ).reset( );
	}
}
