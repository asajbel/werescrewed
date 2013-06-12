package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.CheckBox;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.OptionButton;
import com.blindtigergames.werescrewed.gui.Slider;
import com.blindtigergames.werescrewed.gui.SwitchButton;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundType;

class OptionsScreen extends MenuScreen {

	private SpriteBatch batch = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private ScreenType backScreen = null;
	// private Sprite fade = null;
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
	//private Button controls = null;
	private OptionButton music = null;
	private OptionButton sound = null;
	private OptionButton noise = null;
	// private OptionButton subtitles = null;
	private TextButton backButton = null;
	private CheckBox fullBox;
	private SwitchButton fullCheck;

	public static boolean restart = false;
	private final String restartMessage = "Restart the game for changes to take effect.";

	/*
	 * Things needed... Controls: Shows a visual map of the controls depending
	 * on what inputs are being used. Music: Changes the volume of the music.
	 * Sound Effects: Changes the volume of the sound effects. Noise: Changes
	 * the volume of the noise work. Subtitles: Turns subtitle on and off for
	 * the noise work.
	 */
	public OptionsScreen( ScreenType screen ) {
		super( );
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		this.backScreen = screen;
	}

	public OptionsScreen( ) {
		this( null );
	}

	@Override
	public void load( ) {
		super.load( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		// Texture fadeScreen = WereScrewedGame.manager.get(
		// WereScrewedGame.dirHandle
		// + "/menu/transition.png", Texture.class );
		// fade = new Sprite( fadeScreen );
		Texture transition = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle + "/transitions/trans-gear.png",
				Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;

		fancyFont.setScale( 1.0f );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 50 );
		screenLabel = new Label( "OPTIONS", fancyFont );

		loadButtons( );
		setClearColor( 40, 40, 40, 255 );
	}

	@Override
	public void show(){
		super.show( );
		SoundManager.setEnableLoops( false );
	}

	@Override	
	public void hide(){
		super.hide( );
		SoundManager.setEnableLoops( true );
	}
	
	@Override
	public void render( float delta ) {
		super.render( delta );
		batch.begin( );
		fancyFont.setScale( 1.0f );
		screenLabel.draw( batch );
		//controls.draw( batch, camera );
		music.draw( batch, camera );
		sound.draw( batch, camera );
		noise.draw( batch, camera );
		// subtitles.draw( batch, camera );
		backButton.draw( batch, camera );
		if ( Gdx.app.getType( ) == ApplicationType.Desktop ) {
			fullCheck.draw( batch, camera );
			fullBox.draw( batch );
		}

		if ( restart ) {
			fancyFont.setScale( 1f );
			fancyFont
					.draw( batch,
							restartMessage,
							width
									/ 2
									- fancyFont.getBounds( restartMessage ).width
									/ 2,
							100 + fancyFont.getBounds( restartMessage ).height );
		}
		if ( !transInEnd ) {
			drawTransIn( batch );
		}

		if ( !transOutEnd ) {
			drawTransOut( batch );
			if ( transOutEnd )
				buttonIndex = 0;
		}

		// if ( !alphaFinish )
		// setAlpha( -0.02f );
		// fade.draw( batch, alpha );

		batch.end( );
	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, WereScrewedGame.getWidth( ),
				WereScrewedGame.getHeight( ) );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int leftX = width / 4;
		int centerY = height / 2;

		// fade.setPosition( 0, 0 );

		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 6 * ( lineHeight - 25 ) );
		//controls.setX( leftX - controls.getWidth( ) / 2 );
		//controls.setY( centerY + 4 * lineHeight );
		music.setX( leftX - music.getWidth( ) / 2 );
		music.setY( centerY + 3 * lineHeight );
		sound.setX( leftX - sound.getWidth( ) / 2 );
		sound.setY( centerY + 2 * lineHeight );
		noise.setX( leftX - noise.getWidth( ) / 2 );
		noise.setY( centerY + lineHeight );
		// subtitles.setX( leftX - subtitles.getWidth( ) / 2 );
		// subtitles.setY( centerY );
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

		if ( Gdx.app.getType( ) == ApplicationType.Desktop ) {
			fullCheck.setX( leftX - fullCheck.getWidth( ) / 2 );
			fullCheck.setY( centerY );
			fullBox.setX( fullCheck.getX( ) * 4 );
			fullBox.setY( fullCheck.getY( ) - fullCheck.getHeight( ) * 2 + 20 );
		}
	}

	private void loadButtons( ) {
		buttonTex = WereScrewedGame.manager.getAtlas( "menu-textures" )
				.findRegion( "button" );
		TextureRegion slidTex = WereScrewedGame.manager.getAtlas(
				"menu-textures" ).findRegion( "slider" );
		TextureRegion screwTex = WereScrewedGame.manager.getAtlas(
				"menu-textures" ).findRegion( "screw" );

		ScreenType back = ScreenType.MAIN_MENU;
		if ( backScreen != null )
			back = backScreen;

		musicSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2,
				SoundType.MUSIC, slidTex, screwTex );
		soundSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2,
				SoundType.SFX, slidTex, screwTex );
		noiseSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2,
				SoundType.NOISE, slidTex, screwTex );
		//controls = new Button( "Controls", fancyFont, buttonTex );
		music = new OptionButton( "Music", fancyFont, buttonTex, musicSlider );
		sound = new OptionButton( "Sound", fancyFont, buttonTex, soundSlider );
		noise = new OptionButton( "Noise", fancyFont, buttonTex, noiseSlider );
		// subtitles = new OptionButton( "Subtitles", fancyFont,
		// subBox );

		backButton = new TextButton( "Back", fancyFont, buttonTex,
				new ScreenSwitchHandler( back ) );

		music.setColored( true );

		Buttons = new ArrayList< Button >( );
		//Buttons.add( controls );
		Buttons.add( music );
		Buttons.add( sound );
		Buttons.add( noise );

		if ( Gdx.app.getType( ) == ApplicationType.Desktop ) {
			int val = WereScrewedGame.getPrefs( ).isFullScreen( ) ? 1 : 0;
			fullBox = new CheckBox( 0, 1, val );
			fullCheck = new SwitchButton( "Fullscreen", fancyFont, buttonTex,
					new FullHandler( fullBox ) );

			Buttons.add( fullCheck );
		}

		Buttons.add( backButton );
	}
}