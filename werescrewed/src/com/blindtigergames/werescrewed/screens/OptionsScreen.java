package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.OptionButton;
import com.blindtigergames.werescrewed.gui.Slider;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundType;

class OptionsScreen extends Screen {

	private SpriteBatch batch = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	@SuppressWarnings( "unused" )
	private Sprite menuBG = null;
	private Sprite fade = null;
	private int lineHeight = 0;
	private final int VOLUME_MAX = 100;
	private final int VOLUME_MIN = 0;
	private final float SHIFT = 1.2f;
	// private boolean subs = false;

	private OrthographicCamera camera = null;

	private Label screenLabel = null;
	private Slider musicSlider = null;
	private Slider soundSlider = null;
	private Slider noiseSlider = null;
	// private CheckBox subBox = null;
	private Button controls = null;
	private OptionButton music = null;
	private OptionButton sound = null;
	private OptionButton noise = null;
	// private OptionButton subtitles = null;
	private TextButton creditsButton = null;
	private TextButton backButton = null;

	/*
	 * Things needed... Controls: Shows a visual map of the controls depending
	 * on what inputs are being used. Music: Changes the volume of the music.
	 * Sound Effects: Changes the volume of the sound effects. Noise: Changes
	 * the volume of the noise work. Subtitles: Turns subtitle on and off for
	 * the noise work.
	 */
	public OptionsScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );

		Texture fadeScreen = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/transition.png", Texture.class );
		fade = new Sprite( fadeScreen );
		/*@SuppressWarnings( "unused" )
		int width = Gdx.graphics.getWidth( );
		@SuppressWarnings( "unused" )
		int height = Gdx.graphics.getHeight( );*/
		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		scale = trans.getHeight( ) * SCALE_MAX;
		scaleMax = scale;;
		transInEnd = false;
		
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 40 );
		screenLabel = new Label( "OPTIONS", fancyFont );

		loadButtons( );
	}

	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		screenLabel.draw( batch );
		controls.draw( batch, camera );
		music.draw( batch, camera );
		sound.draw( batch, camera );
		noise.draw( batch, camera );
		// subtitles.draw( batch, camera );
		creditsButton.draw( batch, camera );
		backButton.draw( batch, camera );

		if ( !transInEnd ) {
			trans.setPosition( width / 2 - trans.getWidth( ) / 2, height / 2 - trans.getHeight( ) / 2 );
			drawTransIn( batch );
			trans.setSize( scale, scale );
		}
		
		if ( !transOutEnd ) {
			trans.setPosition( width / 2 - trans.getWidth( ) / 2, height / 2 - trans.getHeight( ) / 2 );
			drawTransOut( batch );
			trans.setSize( scale, scale );
		}
		
		//if ( !alphaFinish )
			//setAlpha( -0.02f );
		//fade.draw( batch, alpha );

		batch.end( );
	}

	@Override
	public void resize( int width, int height ) {
		this.width = width;
		this.height = height;
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int leftX = width / 4;
		int centerY = height / 2;

		fade.setPosition( 0, 0 );

		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 7 * ( lineHeight - 20 ) );
		controls.setX( leftX - controls.getWidth( ) / 2 );
		controls.setY( centerY + 4 * lineHeight );
		music.setX( leftX - music.getWidth( ) / 2 );
		music.setY( centerY + 3 * lineHeight );
		sound.setX( leftX - sound.getWidth( ) / 2 );
		sound.setY( centerY + 2 * lineHeight );
		noise.setX( leftX - noise.getWidth( ) / 2 );
		noise.setY( centerY + lineHeight );
		// subtitles.setX( leftX - subtitles.getWidth( ) / 2 );
		// subtitles.setY( centerY );
		creditsButton.setX( leftX - creditsButton.getWidth( ) / 2 );
		creditsButton.setY( centerY );
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 100 + backButton.getHeight( ) );

		// subBox.setX( subtitles.getX( ) * 4 );
		// subBox.setY( subtitles.getY( ) );

		musicSlider
				.setXPos( ( float ) musicSlider.getMinPos( )
						+ ( SoundManager.globalVolume.get( SoundType.MUSIC ) * 100 * SHIFT ) );
		musicSlider.setYPos( ( float ) musicSlider.getY( ) );
		soundSlider
				.setXPos( ( float ) soundSlider.getMinPos( )
						+ ( SoundManager.globalVolume.get( SoundType.SFX ) * 100 * SHIFT ) );
		soundSlider.setYPos( ( float ) soundSlider.getY( ) );
		noiseSlider
				.setXPos( ( float ) noiseSlider.getMinPos( )
						+ ( SoundManager.globalVolume.get( SoundType.NOISE ) * 100 * SHIFT ) );
		noiseSlider.setYPos( ( float ) noiseSlider.getY( ) );
	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub

	}

	private void loadButtons( ) {
		musicSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2,
				SoundType.MUSIC );
		soundSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2,
				SoundType.SFX );
		noiseSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2,
				SoundType.NOISE );
		// subBox = new CheckBox ( 0, 1, 0 );
		controls = new Button( "Controls", fancyFont );
		music = new OptionButton( "Music", fancyFont, musicSlider );
		sound = new OptionButton( "Sound", fancyFont, soundSlider );
		noise = new OptionButton( "Noise", fancyFont, noiseSlider );
		// subtitles = new OptionButton( "Subtitles", fancyFont,
		// subBox );
		creditsButton = new TextButton( "Credits", fancyFont,
				new ScreenSwitchHandler( ScreenType.CREDITS ) );
		backButton = new TextButton( "Back", fancyFont,
				new ScreenSwitchHandler( ScreenType.MAIN_MENU ) );

		controls.setColored( true );

		Buttons = new ArrayList< Button >( );
		Buttons.add( controls );
		Buttons.add( music );
		Buttons.add( sound );
		Buttons.add( noise );
		// Buttons.add( subtitles );
		Buttons.add( creditsButton );
		Buttons.add( backButton );
	}
}
