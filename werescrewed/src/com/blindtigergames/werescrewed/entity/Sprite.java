package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.animator.IAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;

/**
 * @author Nick Patti/Kevin Cameron
 * 
 * @notes Users can interchange AnimatedSprites for libGDX sprites. They are
 *        called the exact same way, and animations are handled in the draw
 *        method. Additional utility methods have been added to alter additional
 *        data required for animation, such as frame rate and frame number.
 */

public class Sprite extends com.badlogic.gdx.graphics.g2d.Sprite implements
		I_Drawable {
	/*
	 * These constants are identical to the ones in LibGDX's sprite class I just
	 * changed their visibility to protected instead of private.
	 */
	protected static final int VERTEX_SIZE = 2 + 1 + 2;
	protected static final int SPRITE_SIZE = 4 * VERTEX_SIZE;

	// Set this to whatever global constant we eventually use.
	protected static final float FPS = 30.0f;

	Array< TextureAtlas > atlases;
	IAnimator animator;
	TextureRegion currentFrame;

	protected float alpha = 1.0f;

	public Sprite( ) {
		super( );
		initialize( );
	}

	public Sprite( Array< TextureAtlas > a, IAnimator anim ) {
		this( );
		addAtlases( a );
		setAnimator( anim );
		updateFrame( );
		this.setRegion( currentFrame );
		this.setBounds( 0.0f, 0.0f, currentFrame.getRegionWidth( ),
				currentFrame.getRegionHeight( ) );
	}

	public Sprite( TextureAtlas a, IAnimator anim ) {
		this( );
		addAtlas( a );
		setAnimator( anim );
		updateFrame( );
		this.setRegion( currentFrame );
		this.setBounds( 0.0f, 0.0f, currentFrame.getRegionWidth( ),
				currentFrame.getRegionHeight( ) );
	}

	public Sprite( TextureAtlas a ) {
		this( a, new SimpleFrameAnimator( ).maxFrames( a.getRegions( ).size )
				.speed( FPS / a.getRegions( ).size ) );
	}

	public Sprite( Texture tex ) {
		super( tex );
		initialize( );
	}

	public Sprite( TextureRegion tex ) {
		super( tex );
		initialize( );
		currentFrame = tex;
	}

	public void setAnimator( IAnimator a ) {
		animator = a;
	}

	public IAnimator getAnimator( ) {
		return animator;
	}

	public void addAtlas( TextureAtlas a ) {
		atlases.add( a );
	}

	public void addAtlases( Array< TextureAtlas > a ) {
		atlases.addAll( a );
	}

	public Sprite( String atlasName ) {
		this( WereScrewedGame.manager.getAtlas( atlasName ) );
	}

	protected void initialize( ) {
		atlases = new Array< TextureAtlas >( );
		animator = null;
		currentFrame = null;
	}

	public void update( float deltaTime ) {
		if ( animator != null )
			animator.update( deltaTime );
		updateFrame( );
	}

	public void updateFrame( ) {
		if ( animator != null && animator.getAtlas( ) < atlases.size ) {
			currentFrame = atlases.get( animator.getAtlas( ) ).findRegion(
					animator.getRegion( ), animator.getIndex( ) );
		}
	}

	/**
	 * draw Draws the animated sprite on the screen.
	 * 
	 * @author Nick Patti
	 * 
	 * @param batch
	 *            The SpriteBatch found in the current screen that is displayed
	 * @return void
	 */
	@Override
	public void draw( SpriteBatch batch ) {
		if ( currentFrame != null ) {
			this.setRegion( currentFrame );
			// We only need to update when the frame changes.
			currentFrame = null;
		}
		// Color c = batch.getColor( );
		// float oldAlpha = c.a;
		// batch.setColor( c.r, c.g, c.b, alpha );
		super.draw( batch, alpha );
		// batch.setColor( c.r, c.g, c.b, oldAlpha );
	}

	public void setAlpha( float newAlpha ) {
		alpha = newAlpha;
	}

	public float getAlpha( ) {
		return alpha;
	}

	/**
	 * reset A method which resets the animation to the first frame. Useful for
	 * swapping back and forth between different animated sprites that do not
	 * loop without having to create a new one.
	 * 
	 * @author Nick Patti
	 * 
	 * @return void
	 */
	public void reset( ) {
		// Gdx.app.log( "AnimatedSprite.reset()", "reset called" );
		animator.reset( );
	}

	public void setPosition( Vector2 pos ) {
		super.setPosition( pos.x, pos.y );
	}

	/**
	 * Scale the sprite XY equally if you pass in 1 scale param, or X/Y
	 * separately if you pass in 2
	 * 
	 * @param s
	 * @param scale
	 *            pass in 1
	 * @return the sprite you passed in
	 */
	public static Sprite scale( Sprite s, float... scale ) {
		if ( scale.length > 1 ) {
			s.setScale( scale[ 0 ], scale[ 1 ] );
		} else if ( scale.length > 0 ) {
			s.setScale( scale[ 0 ] );
		}
		return s;
	}
}
