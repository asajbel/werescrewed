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
import com.blindtigergames.werescrewed.gui.CheckBox;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.OptionButton;
import com.blindtigergames.werescrewed.gui.Slider;
import com.blindtigergames.werescrewed.gui.TextButton;

class OptionsScreen extends Screen {

	private SpriteBatch batch = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	@SuppressWarnings( "unused" )
	private Sprite menuBG = null;
	private Sprite transition = null;
	private int lineHeight = 0;
	private final int VOLUME_MAX = 100;
	private final int VOLUME_MIN = 0;
	@SuppressWarnings( "unused" )
	private boolean subs = false;
	private OrthographicCamera camera = null;

	private Label screenLabel = null;
	private Slider musicSlider = null;
	private Slider soundSlider = null;
	private Slider voiceSlider = null;
	private CheckBox subBox = null;
	private Button controls = null;
	private OptionButton music = null;
	private OptionButton sound = null;
	private OptionButton voice = null;
	private OptionButton subtitles = null;
	private TextButton creditsButton = null;
	private TextButton backButton = null;

	/*
	 * Things needed... Controls: Shows a visual map of the controls depending
	 * on what inputs are being used. Music: Changes the volume of the music.
	 * Sound Effects: Changes the volume of the sound effects. Voice: Changes
	 * the volume of the voice work. Subtitles: Turns subtitle on and off for
	 * the voice work.
	 */
	public OptionsScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );

		Texture trans = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/transition.png", Texture.class );
		transition = new Sprite( trans );

		@SuppressWarnings( "unused" )
		int width = Gdx.graphics.getWidth( );
		@SuppressWarnings( "unused" )
		int height = Gdx.graphics.getHeight( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 40 );
		// the following are placeholder displays. Add actual option buttons
		// here later
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
		voice.draw( batch, camera );
		subtitles.draw( batch, camera );
		creditsButton.draw( batch, camera );
		backButton.draw( batch, camera );

		if ( !finish )
			setAlpha( -0.02f );
		transition.draw( batch, alpha );

		batch.end( );
	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int leftX = width / 4;
		int centerY = height / 2;

		transition.setPosition( 0, 0 );

		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 7 * ( lineHeight - 20 ) );
		controls.setX( leftX - controls.getWidth( ) / 2 );
		controls.setY( centerY + 4 * lineHeight );
		music.setX( leftX - music.getWidth( ) / 2 );
		music.setY( centerY + 3 * lineHeight );
		sound.setX( leftX - sound.getWidth( ) / 2 );
		sound.setY( centerY + 2 * lineHeight );
		voice.setX( leftX - voice.getWidth( ) / 2 );
		voice.setY( centerY + lineHeight );
		subtitles.setX( leftX - subtitles.getWidth( ) / 2 );
		subtitles.setY( centerY );
		creditsButton.setX( leftX - subtitles.getWidth( ) / 2 );
		creditsButton.setY( centerY - 1 * lineHeight );
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 100 + backButton.getHeight( ) );

		subBox.setX( subtitles.getX( ) * 4 );
		subBox.setY( subtitles.getY( ) );
		musicSlider.setXPos( ( float ) musicSlider.getMinPos( )
				+ ( float ) musicSlider.getCurrentValue( ) );
		musicSlider.setYPos( ( float ) musicSlider.getY( ) );
		soundSlider.setXPos( ( float ) soundSlider.getMinPos( )
				+ ( float ) soundSlider.getCurrentValue( ) );
		soundSlider.setYPos( ( float ) soundSlider.getY( ) );
		voiceSlider.setXPos( ( float ) voiceSlider.getMinPos( )
				+ ( float ) voiceSlider.getCurrentValue( ) );
		voiceSlider.setYPos( ( float ) voiceSlider.getY( ) );
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
		musicSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2 );
		soundSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2 );
		voiceSlider = new Slider( VOLUME_MIN, VOLUME_MAX, VOLUME_MAX / 2 );
		subBox = new CheckBox( 0, 1, 0 );
		controls = new Button( "Controls", fancyFont );
		music = new OptionButton( "Music", fancyFont, musicSlider );
		sound = new OptionButton( "Sound", fancyFont, soundSlider );
		voice = new OptionButton( "Voice", fancyFont, voiceSlider );
		subtitles = new OptionButton( "Subtitles", fancyFont, subBox );
		creditsButton = new TextButton( "Credits", fancyFont,
				new ScreenSwitchHandler( ScreenType.CREDITS ) );
		backButton = new TextButton( "Back", fancyFont,
				new ScreenSwitchHandler( ScreenType.MAIN_MENU ) );

		controls.setColored( true );

		Buttons = new ArrayList< Button >( );
		Buttons.add( controls );
		Buttons.add( music );
		Buttons.add( sound );
		Buttons.add( voice );
		Buttons.add( subtitles );
		Buttons.add( creditsButton );
		Buttons.add( backButton );
	}
}
