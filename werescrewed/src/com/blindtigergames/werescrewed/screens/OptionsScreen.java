package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

class OptionsScreen extends Screen{
//implements com.badlogic.gdx.Screen
	private SpriteBatch batch = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Texture logo = null;
	private int lineHeight = 0;
	private OrthographicCamera camera = null;
	private Label screenLabel = null;
	private Label controls = null;
	private Label music = null;
	private Label sound = null;
	private Label voice = null;
	private Label subtitles = null;
	private Button creditsButton = null;
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
		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );

		logo =  WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				 + "/common/title_background.png", Texture.class );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		//the following are placeholder displays. Add actual option buttons here later
		screenLabel = new Label("OPTIONS", fancyFont);
		controls = new Label( "Controls", fancyFont );
		music = new Label("Music", fancyFont);
		sound = new Label("Sound", fancyFont);
		voice = new Label("Voice", fancyFont);
		subtitles = new Label("Subtitles", fancyFont);
		creditsButton = new Button("Credits", fancyFont, 
				new ScreenSwitchHandler(ScreenType.CREDITS));
		backButton = new Button( "Back", fancyFont, new ScreenSwitchHandler(
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
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		// TODO Auto-generated method stub
		batch.begin( );
		batch.draw(logo, 0, 0);
		screenLabel.draw( batch );
		controls.draw( batch );
		music.draw( batch );
		sound.draw( batch );
		voice.draw( batch );
		subtitles.draw( batch );
		creditsButton.draw(batch, camera);
		backButton.draw( batch, camera );
		batch.end( );

	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		screenLabel.setX( centerX - screenLabel.getWidth()/2);
		screenLabel.setY( centerY + 6 * lineHeight);
		controls.setX( centerX - controls.getWidth( )/2);
		controls.setY( centerY + 2 * lineHeight  );
		music.setX( centerX - music.getWidth( )/2);
		music.setY( centerY + lineHeight  );
		sound.setX( centerX - sound.getWidth( )/2);
		sound.setY( centerY  );
		voice.setX( centerX - voice.getWidth( )/2);
		voice.setY( centerY - lineHeight  );
		subtitles.setX( centerX - subtitles.getWidth( )/2);
		subtitles.setY( centerY - 2 * lineHeight  );
		creditsButton.setX( centerX - subtitles.getWidth( )/2);
		creditsButton.setY( centerY - 3 * lineHeight  );
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
