package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.animator.IAnimator;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;

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

	TextureAtlas atlas;
	IAnimator animator;
	TextureRegion currentFrame;
	
	public Sprite(){
		super();
    	initialize();
	}
	
    public Sprite ( TextureAtlas a, String initialRegion ){
    	this();
    	atlas = a;
    	currentFrame = atlas.findRegion( initialRegion );
    	animator = new SimpleFrameAnimator()
    				.maxFrames( atlas.getRegions( ).size );
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
    
    public Sprite (String atlasName, String initialRegion){
    	this(WereScrewedGame.manager.get( atlasName, TextureAtlas.class ), initialRegion);
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
			currentFrame = atlas.findRegion( animator.getRegion( ) );
			this.setRegion( currentFrame );
		}
		if (currentFrame != null){
			this.setRegion( currentFrame );
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
	 * 
	 * TODO: currently does not do anything. fix this method
	 */
	public void reset( ) {
		// TODO: find a way to have stateTime count from zero and up again.
		Gdx.app.log( "AnimatedSprite.reset()", "reset called" );
	}

	public void setPosition( Vector2 pos ) {
		super.setPosition(pos.x, pos.y);
	}

	/**
	 * toString A handy util method which displays the name of the sprite that
	 * is animating
	 * 
	 * public String toString(){ return animation.toString(); }
	 */
}

/**** Below is code that is not completed yet *****/
/**
 * TODO: Finish this constructor! A constructor which mimics the animation
 * class. TextureRegions, such as sprite sheet's n' stuff, go in here.
 * 
 * @param frameDuration
 * @param keyFrames
 */

/**           public AnimatedSprite(float frameDuration, TextureRegion
 *            keyFrames){ this.animation = new Animation(frameDuration,
 *            keyFrames); }
 * 
 */
