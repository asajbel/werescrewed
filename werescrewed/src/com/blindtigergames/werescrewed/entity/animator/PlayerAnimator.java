package com.blindtigergames.werescrewed.entity.animator;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.PlayerState;

public class PlayerAnimator implements IAnimator {
	public enum PlayerAnim{
		IDLE("idle")
		,RUN("run")
		,JUMP_UP("jump_up")
		,JUMP_DOWN("jump_down")
		;
		String text;
		PlayerAnim(String t){
			text = t;
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
		for (PlayerAnim a: PlayerAnim.values( )){
			anim = new SimpleFrameAnimator()
					.prefix( a.toString( ) )
					.maxFrames( atlas.findRegions( a.text ).size );
			anims.put( a, anim);
			
		}
	}
	
	@Override
	public void update( float dT ) {
		current = getCurrentAnim();
		anims.get(current).update( dT );
	}

	protected PlayerAnim getCurrentAnim(){
		switch (player.getState( )){
		case Running:
			return PlayerAnim.RUN;
		case Jumping:
			return PlayerAnim.JUMP_UP;
		case Falling:
			return PlayerAnim.JUMP_DOWN;
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
		anims.get( current ).setIndex( f );
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
