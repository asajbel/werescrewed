package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.debug.FPSLoggerS;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.util.Util;

public class Screen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	protected Level level;
	protected SpriteBatch batch;
	protected SBox2DDebugRenderer debugRenderer;
	private Color clearColor = new Color( 0, 0, 0, 1 );
	protected ArrayList< Button > Buttons = new ArrayList< Button >( );
	protected int controllerTimer = 10;
	protected int controllerMax = 10;
	protected int buttonIndex = 0;
	protected static int width = WereScrewedGame.getWidth( );
	protected static int height = WereScrewedGame.getHeight( );
	protected float alpha = 1.0f;
	protected boolean finish = false;
	
	private float accum = 0f;               
	private final float step = 1f / 60f;    
	private final float maxAccum = 1f / 17f;
	protected static int screenWidth;
	protected static int screenHeight;
	private int x;
	private int y;
	private int bX;
	private int bY;
	private ShapeRenderer shapeRenderer;
	protected float scale = 0.0f;
	protected final float SCALE_MIN = 0.0f;
	protected final float SCALE_MAX = 1.0f;
	protected final float SCALE_SIZE = 10.0f;
	protected final float SCALE_ADJUST = 0.03f;
	protected float maxScale = 0.0f;
	protected Sprite trans = null;
	protected boolean alphaFinish = false;
	protected boolean transInEnd = true;
	protected boolean transOutEnd = true;
	protected static boolean fullscreen = false; 
	protected boolean assetsLoaded = false;
	protected boolean once = true;
	
	BitmapFont debug_font;
	Camera uiCamera;

	public FPSLoggerS logger;
	
	public Music bgm;
	public SoundManager sounds;
	
	public Screen( ) {

		// Gdx.app.log( "Screen", "Turning log level to none. SHHH" );
		// Gdx.app.setLogLevel( Application.LOG_NONE );


		shapeRenderer = new ShapeRenderer( );
		batch = new SpriteBatch( );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		level = null;
		// if(WereScrewedGame.manager.isLoaded( "debug_font" ))
		debug_font = WereScrewedGame.manager.getFont( "debug_font" );

		logger = new FPSLoggerS( );
		uiCamera = new OrthographicCamera( WereScrewedGame.getWidth( ),
				WereScrewedGame.getHeight( ) );
		uiCamera.position.set( 0, 0, 0 ); // -Gdx.graphics.getWidth( ),
											// -Gdx.graphics.getHeight( )
		setClearColor( 0f, 0f, 0f, 1f );
		bgm = null;
		sounds = null;
	}

	public void load(){ }
	
	@Override
	public void render( float delta ) {
		if (sounds != null){
			sounds.update( delta );
		}
		if ( WereScrewedGame.debug && Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}
		Gdx.gl.glViewport(
				x,
				y,  
				screenWidth, 
				screenHeight);
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( clearColor.r, clearColor.g, clearColor.b,
					clearColor.a );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}
		
		shapeRenderer.begin( ShapeType.FilledRectangle );
		shapeRenderer.filledRect(bX, bY, screenWidth, screenHeight );
		shapeRenderer.end( );
		
		if ( once && trans != null ) {
			trans.setSize( maxScale, maxScale );
			once = false;
		}
		
		if (level != null){			
			updateStep(delta);
			
			// background stuff
			if ( level.backgroundRootSkeleton != null ) {
				if(level.bgCamZoomScale!=0.0f){
					level.backgroundCam.zoom= Util.clamp( 1f+level.camera.camera.zoom*level.bgCamZoomScale,
							level.bgCamZoomMin, level.bgCamZoomMax );
					
				}
				level.backgroundCam.update( );
				level.backgroundRootSkeleton.update( delta );
				
				level.backgroundBatch
						.setProjectionMatrix( level.backgroundCam.combined );
				level.backgroundBatch.begin( );
				level.backgroundRootSkeleton.drawBGDecals(
						level.backgroundBatch, level.camera );
				level.backgroundRootSkeleton
						.draw( level.backgroundBatch, delta, level.camera );
				level.backgroundBatch.end( );
			}

			level.draw( batch, debugRenderer, delta );

			level.camera.update( delta );

			int FPS = logger.getFPS( );
			
			if ( level.debug && debug_font != null ) {
				batch.setProjectionMatrix( uiCamera.combined );
				batch.begin( );
				debug_font.draw( batch, "FPS: " + FPS,
						-WereScrewedGame.getWidth( ) / 2,
						WereScrewedGame.getHeight( ) / 2 );// -Gdx.graphics.getWidth(
														// )/4,
														// Gdx.graphics.getHeight(
														// )/4
				// debug_font.draw(batch, "ALPHA BUILD", -Gdx.graphics.getWidth(
				// )/2, Gdx.graphics.getHeight( )/2);
				batch.end( );
			}
			
		}
		
		if ( WereScrewedGame.debug &&  Gdx.input.isKeyPressed( Input.Keys.BACKSPACE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.TROPHY );
		}
	}
	
	protected void drawTransIn ( SpriteBatch batch ) {
		scale -= SCALE_ADJUST;
		trans.setOrigin( trans.getWidth( ) / 2, trans.getHeight( ) / 2 );
		trans.rotate( 5.0f );
		//trans.setSize( scale, scale );
		//trans.setPosition( width / 2 - trans.getWidth( ) / 2, height / 2 - trans.getHeight( ) / 2 );
		trans.setScale( scale );
		trans.setPosition( width / 2 - trans.getWidth( ) / 2, 
				height / 2 - trans.getHeight( ) / 2 );
		trans.draw( batch );
		if ( scale < SCALE_MIN ) {
			transInEnd = true;
			scale = SCALE_MIN;
		}
	}
	
	protected void drawTransOut ( SpriteBatch batch ) {
		scale += SCALE_ADJUST;
		trans.setOrigin( trans.getWidth( ) / 2, trans.getHeight( ) / 2 );
		trans.rotate( 5.0f );
		trans.setScale( scale );
		trans.setPosition( width / 2 - trans.getWidth( ) / 2, 
				height / 2 - trans.getHeight( ) / 2 );
		trans.draw( batch );
		if ( scale > SCALE_MAX ) {
			//transOutEnd = true;
			//scale = SCALE_MAX;
			if ( Buttons.size( ) > 0 ) 
				Buttons.get( buttonIndex ).setSelected( true );
		}
	}
	
	protected void drawTransOut ( SpriteBatch batch, ScreenType screen ) {
		scale += SCALE_ADJUST;
		trans.setOrigin( trans.getWidth( ) / 2, trans.getHeight( ) / 2 );
		trans.rotate( 5.0f );
		trans.setScale( scale );
		trans.setPosition( width / 2 - trans.getWidth( ) / 2, 
				height / 2 - trans.getHeight( ) / 2 );
		trans.draw( batch );
		if ( scale > SCALE_MAX ) {
			//transOutEnd = true;
			//scale = SCALE_MAX;
			ScreenManager.getInstance( ).show( screen );
		}
	}
	
	private void updateStep(float delta) {   
		accum += delta;  
		accum = Math.min( accum, maxAccum );
		while (accum >= step) {    
			level.update( step );
		    accum -= step;                  
		}                                        
	}         

	protected void addFGSkeleton( Skeleton skel ) {
		addToSkelList( level.skelFGList, skel, false );
	}

	protected void addFGSkeletonBack( Skeleton skel ) {
		addToSkelList( level.skelFGList, skel, true );
	}

	protected void addBGSkeleton( Skeleton skel ) {
		addToSkelList( level.skelBGList, skel, false );
	}

	protected void addBGSkeletonBack( Skeleton skel ) {
		addToSkelList( level.skelBGList, skel, true );
	}

	protected void addFGEntity( Entity entity ) {
		addToEntityList( level.entityFGList, entity, false );
	}
	
	protected void addFGEntity( Entity... entities ) {
		for(Entity e : entities){
			addToEntityList( level.entityFGList, e, false );
		}
	}

	protected void addFGEntityToBack( Entity entity ) {
		addToEntityList( level.entityFGList, entity, true );
	}

	protected void addBGEntity( Entity entity ) {
		addToEntityList( level.entityBGList, entity, false );
	}

	protected void addBGEntityToBack( Entity entity ) {
		addToEntityList( level.entityBGList, entity, true );
	}

	private void addToEntityList( ArrayList< Entity > list, Entity e,
			boolean isBack ) {
		if ( !list.contains( e ) ) {
			if ( isBack )
				list.add( 0, e );
			else
				list.add( e );
		}
	}

	private void addToSkelList( ArrayList< Skeleton > list, Skeleton s,
			boolean isBack ) {
		if ( !list.contains( s ) ) {
			if ( isBack )
				list.add( 0, s );
			else
				list.add( s );
		}
	}

	@Override
	public void resize( int _width, int _height ) {
		float _scale = 1.0f;
		if (_width > WereScrewedGame.getWidth( )) 
			_scale = (float)_width/(float)WereScrewedGame.getWidth( ); 
		screenWidth = (int) (_scale * WereScrewedGame.getWidth( )); 
		screenHeight = (int) (_scale * WereScrewedGame.getHeight( ));
		x = _width / 2 - screenWidth / 2; 
		y = _height / 2 - screenHeight / 2; 
		bX = -screenWidth/2;
		bY = -screenHeight/2;
		shapeRenderer.setProjectionMatrix( uiCamera.combined );
		shapeRenderer.setColor( clearColor.r,
				clearColor.g, clearColor.b,
				clearColor.a );

	}
	@Override
	public void show( ) {
		if (!assetsLoaded){
			load();
		}
		if (bgm != null){
			bgm.setLooping( true );
			bgm.setVolume( SoundManager.getMusicVolume( ) );
			try {
				bgm.play( );
			} catch (com.badlogic.gdx.utils.GdxRuntimeException audioFail){
				Gdx.app.log( "Audio Fail", "", audioFail );
			}
		}
	}

	@Override
	public void hide( ) {
		if (bgm != null){
			bgm.stop();
		}
		if (sounds != null){
			sounds.stopAll( );
		}
	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose( ) {
		if ( level != null )
			level.resetPhysicsWorld( );
		
		if (bgm != null){
			bgm.stop( );
			bgm.dispose( );
			bgm = null;
		}
		if (sounds != null){
			sounds.stopAll( );
			sounds.dispose( );
			sounds = null;
		}
		assetsLoaded = false;
	}

	/**
	 * Set clear color with values in 0-1 range
	 */
	public void setClearColor( float r, float g, float b, float a ) {
		clearColor = new Color( r, g, b, a );
	}

	/**
	 * Set clear color with values in 0-255 range
	 */
	public void setClearColor( int r, int g, int b, int a ) {
		setClearColor( r / 255f, g / 255f, b / 255f, a / 255f );
	}

	public float getAlpha( ) {
		return alpha;
	}

	public void setAlpha( float value ) {
		alpha += value;

		if ( alpha >= 1.0f ) {
			alpha = 1.0f;
			alphaFinish = true;
		} else if ( alpha < 0.0f ) {
			alpha = 0.0f;
			alphaFinish = true;
		}
	}

	public boolean isAlphaFinished( ) {
		return alphaFinish;
	}

	public void setAlphaFinish( boolean value ) {
		alphaFinish = value;
	}

	public boolean transInFinish( ) {
		return transInEnd;
	}

	public void setTransInEnd( boolean value ) {
		transInEnd = value;
	}
	
	public boolean transOutFinish( ) {
		return transOutEnd;
	}

	public void setTransOutEnd( boolean value ) {
		transOutEnd = value;
	}
}
