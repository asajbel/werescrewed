package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

class OptionsScreen implements com.badlogic.gdx.Screen{

	private SpriteBatch batch = null;
	private BitmapFont font = null;
	private int lineHeight = 0;
	private OrthographicCamera camera = null;
	private Label controls = null;
	private Label music = null;
	private Label sound = null;
	private Label voice = null;
	private Label subtitles = null;
	private Button backButton = null;
	
	/* Things needed...
	 * Controls: Shows a visual map of the controls depending on what inputs are being used.
	 * Music: Changes the volume of the music.
	 * Sound Effects: Changes the volume of the sound effects.
	 * Voice: Changes the volume of the voice work.
	 * Subtitles: Turns subtitle on and off for the voice work.
	 */
	public OptionsScreen(){
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		//the following are placeholder displays. Add actual option buttons here later
		controls = new Label( "Controls", font );
		music = new Label("Music", font);
		sound = new Label("Sound",font);
		voice = new Label("Voice",font);
		subtitles = new Label("Subtitles",font);
		
		backButton = new Button( "Back", font, new ScreenSwitchHandler(
				ScreenType.MAIN_MENU ) );
	}

	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub
		font.dispose( );
		batch.dispose( );
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
		Gdx.gl.glClearColor( 0.1f, 0.1f, 0.1f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		// TODO Auto-generated method stub
		batch.begin( );
		controls.draw( batch );
		music.draw( batch );
		sound.draw( batch );
		voice.draw( batch );
		subtitles.draw( batch );
		batch.end( );

	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		controls.setX( centerX - controls.getWidth( )/2);
		controls.setY( centerY + 3 * lineHeight  );
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 20 + backButton.getHeight( ) );
		
	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub
		
	}
}
