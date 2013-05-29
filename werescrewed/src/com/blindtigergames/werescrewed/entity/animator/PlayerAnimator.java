package com.blindtigergames.werescrewed.entity.animator;

import java.util.EnumMap;

import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.graphics.TextureAtlas.AtlasRegion;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.ConcurrentState;
import com.blindtigergames.werescrewed.player.Player.PlayerDirection;
import com.blindtigergames.werescrewed.player.Player.PlayerState;

public class PlayerAnimator implements IAnimator {

	public EnumMap< PlayerAnim, IAnimator > anims;
	public EnumMap< PlayerAnim, Integer > atlasNums;
	public PlayerAnim current;
	public Player player;
	public String prefix;
	public boolean deathBegin;

	public PlayerAnimator( Array< TextureAtlas > atlases, Player p ) {
		player = p;
		SimpleFrameAnimator anim;
		Array< AtlasRegion > regions;
		anims = new EnumMap< PlayerAnim, IAnimator >( PlayerAnim.class );
		atlasNums = new EnumMap< PlayerAnim, Integer >( PlayerAnim.class );
		current = PlayerAnim.IDLE;
		deathBegin = true;
		TextureAtlas atlas;
		for ( PlayerAnim a : PlayerAnim.values( ) ) {
			anim = null;
			for ( int i = 0; i < atlases.size && anim == null; i++ ) {
				atlas = atlases.get( i );
				if ( atlas != null ) {
					regions = atlas.findRegions( a.getText() );
					if ( regions != null && regions.size > 0 ) {
						anim = new SimpleFrameAnimator( ).atlas( i )
								.maxFrames( regions.size ).loop( a.loop );
						// Gdx.app.log( "PlayerAnimator",
						// "Found "+a.text+" in atlas "+i+"." );
					}
				}
			}
			if ( anim != null ) {
				anims.put( a, anim );
				atlasNums.put( a, anim.atlas );
			} else {
				// Gdx.app.log( "PlayerAnimator",
				// "Failed to find corresponding texture atlas for "+a.text+"."
				// );
			}
		}
	}

	@Override
	public void update( float dT ) {
		if ( player.getState( ) != PlayerState.Dead )
			deathBegin = true;
		if ( current != getCurrentAnim( ) ) {
			current = getCurrentAnim( );
			anims.get( current ).reset( );
		}
		anims.get( current ).update( dT );
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
			return PlayerAnim.FALL_IDLE;
		case HeadStand:
			if ( player.getExtraState( ) == ConcurrentState.ExtraJumping ) {
				return PlayerAnim.JUMP_UP;
			} else if ( player.getExtraState( ) == ConcurrentState.ExtraFalling ) {
				return PlayerAnim.FALL_IDLE;
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
	public String getRegion( ) {
		return current.getText();
	}

	@Override
	public void setRegion( String r ) {
		current = PlayerAnim.fromString( r );
		anims.get( current ).reset( );
	}

	@Override
	public int getIndex( ) {
		return anims.get( current ).getFrame( );
	}

	@Override
	public int getFrame( ) {
		return anims.get( current ).getFrame( );
	}

	@Override
	public void setIndex( int f ) {
	}

	@Override
	public void setFrame( int f ) {
		anims.get( current ).setFrame( f );
	}

	@Override
	public void setAtlas( int a ) {
	}

	@Override
	public int getAtlas( ) {
		return atlasNums.get( current );
	}

	@Override
	public float getTime( ) {
		return anims.get( current ).getTime( );
	}

	@Override
	public void reset( ) {
		current = PlayerAnim.IDLE;
		anims.get( current ).reset( );
	}
}
