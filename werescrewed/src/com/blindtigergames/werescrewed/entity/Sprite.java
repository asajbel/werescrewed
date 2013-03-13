package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.animator.IAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;

/**
 * @author Nick Patti/Kevin Cameron
 * 
 * @notes Users can interchange AnimatedSprites for libGDX sprites. They are
 *        called the exact same way, and animations are handled in the draw
 *        method. Additional utility methods have been added to alter additional
 *        data required for animation, such as frame rate and frame number.
 */

public class Sprite extends com.badlogic.gdx.graphics.g2d.Sprite implements I_Drawable {
	/* These constants are identical to the ones in LibGDX's sprite class
	 * I just changed their visibility to protected instead of private.
	 */
	protected static final int VERTEX_SIZE = 2 + 1 + 2;
	protected static final int SPRITE_SIZE = 4 * VERTEX_SIZE;

	//Set this to whatever global constant we eventually use.
	protected static final float FPS = 60.0f;
	
	TextureAtlas atlas;
	IAnimator animator;
	TextureRegion currentFrame;
	
	public Sprite(){
		super();
    	initialize();
	}
	
    public Sprite ( TextureAtlas a, IAnimator anim){
    	super();
    	atlas = a;
    	animator = anim;
    	String regionName = anim.getRegion( );
    	int regionIndex = anim.getIndex( );
    	Gdx.app.log( "Sprite (Animated)", "Region:"+regionName+" Index:"+regionIndex );
    	currentFrame = atlas.findRegion( regionName, regionIndex );
    	this.setRegion(currentFrame);
    	this.setBounds( 0.0f, 0.0f, currentFrame.getRegionWidth( ), currentFrame.getRegionHeight( ) );
    }
    
    public Sprite (TextureAtlas a){
    	this(a,  new SimpleFrameAnimator()
					 .maxFrames( a.getRegions( ).size )
    				 .speed( FPS / a.getRegions( ).size ));
    }
    
    public Sprite (Texture tex){
    	super(tex);
    	initialize();
    }
    
    public Sprite (TextureRegion tex){
    	super(tex);
    	initialize();
    	currentFrame = tex;
    }
    
    public void setAnimator(IAnimator a){
    	animator = a;
    }
    
    public IAnimator getAnimator(){
    	return animator;
    }
    
    public Sprite (String atlasName){
    	this(WereScrewedGame.manager.getAtlas( atlasName ));
    }
    
    protected void initialize(){
		atlas = null;
		animator = null;
		currentFrame = null;
    }
    
    public void update( float deltaTime ){
    	if (animator != null)
    		animator.update( deltaTime );
    }
    
    /** 
     * draw 
     * Draws the animated sprite on the screen.
     * 
     * @author Nick Patti
     * 
	 * @param batch
	 *            The SpriteBatch found in the current screen that is displayed
	 * @return void
	 */
	@Override
	public void draw( SpriteBatch batch ) {
		if (atlas != null && animator != null){
			currentFrame = atlas.findRegion( animator.getRegion( ) , animator.getIndex( ) );
		}
		if (currentFrame != null){
			this.setRegion( currentFrame );
			//We only need to update when the frame changes.
			currentFrame = null;
		}
		super.draw( batch );
	}

	/**
	 * reset
	 * A method which resets the animation to the first frame. Useful for
	 * swapping back and forth between different animated sprites that do not
	 * loop without having to create a new one.
	 * 
	 * @author Nick Patti
	 * 
	 * @return void
	 */
	public void reset( ) {
		Gdx.app.log( "AnimatedSprite.reset()", "reset called" );
		animator.reset();
	}

	public void setPosition( Vector2 pos ) {
		super.setPosition(pos.x, pos.y);
	}
}
