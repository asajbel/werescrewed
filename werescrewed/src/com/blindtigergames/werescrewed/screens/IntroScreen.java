package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.input.mappings.Mapping;

class IntroScreen extends Screen {
	// implements com.badlogic.gdx.Screen
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private TextButton backButton = null;
	private Sprite creditSprite=null;
	private float timer = 0;
	private Sound growl;
	private boolean playOnce = true;
	
	public IntroScreen( ) {
		super( );
		batch = new SpriteBatch( );
		font = new BitmapFont( );
	}
	
	@Override
	public void load( ){
		super.load( );
		
		//logo = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		//		+ "/common/title_background.png", Texture.class );
		WereScrewedGame.manager.load("data/transitions/trans-gear.png", Texture.class );
		growl = Gdx.audio.newSound( Gdx.files.internal( "data/common/sounds/growl.ogg" ) );
		
		
		WereScrewedGame.manager.finishLoading( );
		
		Texture transition = WereScrewedGame.manager.get("data/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
		

		
		
		
		WereScrewedGame.manager.loadAtlas( "data/menu/menu-textures.pack" );
		
		
		
		
		
		creditSprite = WereScrewedGame.manager.getAtlas( "menu-textures" ).createSprite( "btg_logo720" );
		
	}
	
	

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		creditSprite.draw( batch );
		
		timer += delta;
		if(timer > 5){
			ScreenManager.getInstance( ).show( ScreenType.LOADING_MENU );
		}
		if(timer > 2){
			if(playOnce){
				growl.play();
				playOnce = false;
			}
		}
		if ( !transInEnd ) {
			drawTransIn( batch );
		}
		
		if ( !transOutEnd ) {
			drawTransOut( batch, ScreenType.LOADING_MENU );
		}
		
		batch.end( );
		
		if ( WereScrewedGame.p1Controller != null ) {
			if ( WereScrewedGame.p1ControllerListener.jumpPressed( ) && transOutEnd ) {
				transOutEnd = false;
			}
		}
		if ( WereScrewedGame.p2Controller != null ) {
			if ( WereScrewedGame.p2ControllerListener.jumpPressed( ) && transOutEnd ) {
				transOutEnd = false;
			}
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) && transOutEnd ) {
			transOutEnd = false;
		} 
	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		
		float xScale = width/creditSprite.getWidth( );
		float YScale = height/creditSprite.getHeight( );
		creditSprite.setScale( xScale, YScale );
		
		
	}
}
