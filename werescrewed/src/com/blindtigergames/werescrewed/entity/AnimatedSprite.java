package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.blindtigergames.werescrewed.WereScrewedGame;

/**
 * @author Nick Patti
 * 
 * @notes Users can interchange AnimatedSprites for libGDX sprites. They are
 *        called the exact same way, and animations are handled in the draw
 *        method. Additional utility methods have been added to alter additional
 *        data required for animation, such as frame rate and frame number.
 */

public class AnimatedSprite extends Sprite implements I_Drawable {

	private static int FRAMES = 1;
	private static int ROWS = 1;
	private static int COLUMNS = 1;
	Animation animation;
	Texture spriteSheet;
	TextureRegion[ ] spriteSheetFrames;
	TextureRegion currentFrame;
	float stateTime;

	/**
	 * Create an animating sprite using a sprite sheet. Must know the number of
	 * rows and columns of the sprite sheet in order to construct properly.
	 * 
	 * @author Nick Patti
	 * 
	 * @param f
	 *            The number of frames in the sprite sheet. TODO: What to do if
	 *            you have an odd number of sprites in a sheet, but more than
	 *            one row?
	 * @param r
	 *            The number of rows in the sprite sheet. Default value is 1.
	 * @param c
	 *            The number of columns in the sprite sheet. Default value is 1.
	 * @param fr
	 *            The frame rate of the animation
	 * @param spriteSheetName
	 *            The name of the sprite sheet, including the parent files below
	 *            data, and the extension
	 * @param loopType
	 *            The loop type specified by a constant provided by libGDX
	 */
	public AnimatedSprite( int f, int r, int c, float fr,
			String spriteSheetName, int loopType ) {
		FRAMES = f;
		ROWS = r;
		COLUMNS = c;
		String spriteSheetFullName = WereScrewedGame.dirHandle.path( )  + "/" + spriteSheetName;
		spriteSheet = WereScrewedGame.manager.get(spriteSheetFullName, Texture.class);

		// temporary frames for placing each frame into spriteSheetFrames
		TextureRegion[ ][ ] tmpFrame = TextureRegion.split( spriteSheet,
				spriteSheet.getWidth( ) / COLUMNS, spriteSheet.getHeight( )
						/ ROWS );

		spriteSheetFrames = new TextureRegion[ COLUMNS * ROWS ];

		// populate each of the frames with the correct image
		int index = 0;
		for ( int i = 0; i < ROWS; ++i ) {
			for ( int j = 0; j < COLUMNS; ++j ) {
				spriteSheetFrames[ index ] = tmpFrame[ i ][ j ];
				if ( index++ >= FRAMES )
					Gdx.app.log( "AnimatedSprite",
							"You're gonna have some sprite flickering..." );
			}
		}

		// create the animation and set the play mode
		animation = new Animation( fr, spriteSheetFrames );
		animation.setPlayMode( loopType );
		stateTime = 0f;
	}

	/**
	 * Create an animating sprite using a Texture of a sprite sheet. May or may
	 * not be here in a later build, since it's extremely similar to the sprite
	 * sheet constructor
	 * 
	 * @author Nick Patti
	 * 
	 * @param f
	 *            The number of frames in the sprite sheet
	 * @param r
	 *            The number of rows in the sprite sheet. Default value is 1.
	 * @param c
	 *            The number of columns in the sprite sheet. Default value is 1.
	 * @param fr
	 *            The frame rate of the animation. The smaller the number, the
	 *            faster the animation.
	 * @param spriteSheetTexture
	 *            The Texture containing the sprite sheet
	 * @param loopType
	 *            The loop type specified by a constant provided by libGDX
	 */
	public AnimatedSprite( int f, int r, int c, float fr,
			Texture spriteSheetTexture, int loopType ) {
		ROWS = r;
		COLUMNS = c;
		spriteSheet = spriteSheetTexture;

		// temporary frames for placing each frame into spriteSheetFrames
		TextureRegion[ ][ ] tmpFrame = TextureRegion.split( spriteSheet,
				spriteSheet.getWidth( ) / COLUMNS, spriteSheet.getHeight( )
						/ ROWS );

		spriteSheetFrames = new TextureRegion[ COLUMNS * ROWS ];

		// populate each of the frames with the correct image
		int index = 0;
		for ( int i = 0; i < ROWS; ++i ) {
			for ( int j = 0; j < COLUMNS; ++j ) {
				spriteSheetFrames[ index++ ] = tmpFrame[ i ][ j ];
			}
		}

		// create the animation and set the play mode
		animation = new Animation( fr, spriteSheetFrames );
		animation.setPlayMode( loopType );
		stateTime = 0f;
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
		// find the change in time and pick the correct frame to draw
		stateTime += Gdx.graphics.getDeltaTime( );
		currentFrame = animation.getKeyFrame( stateTime );

		// batch.begin has already been called, so just draw away!
		batch.draw( currentFrame, 1.0f, 1.0f );
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
		stateTime = 0;
	}

}
q
