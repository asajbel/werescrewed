package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.debug.FPSLoggerS;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.util.Util;

public class Screen implements com.badlogic.gdx.Screen {
	
	public ScreenType screenType;
	protected Level level;
	protected SpriteBatch batch;
	protected SBox2DDebugRenderer debugRenderer;
	private Color clearColor = new Color(0,0,0,1);
	
	BitmapFont debug_font;
	Camera uiCamera;
	
	public FPSLoggerS logger;
	
	public Screen( ){
		
		//Gdx.app.log( "Screen", "Turning log level to none. SHHH" );
		//Gdx.app.setLogLevel( Application.LOG_NONE );

		batch = new SpriteBatch( );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		level = null;
		//if(WereScrewedGame.manager.isLoaded( "debug_font" ))
			debug_font = WereScrewedGame.manager.getFont( "debug_font" );
			
	
		logger = new FPSLoggerS( );
		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth( ), Gdx.graphics.getHeight( ));
		uiCamera.position.set(0,0 , 0); //-Gdx.graphics.getWidth( ), -Gdx.graphics.getHeight( )
	}
	
	@Override
	public void render( float delta ) {
		if(Gdx.gl20 != null){
			Gdx.gl20.glClearColor( clearColor.r, clearColor.g, clearColor.b, clearColor.a );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( clearColor.r, clearColor.g, clearColor.b, clearColor.a );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}


		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}
		if (level != null){			
			level.update( delta );

			//background stuff
			if ( level.backgroundRootSkeleton != null ) {
				level.backgroundCam.update( );
				level.backgroundRootSkeleton.update( delta );
			}
			
			if ( level.backgroundRootSkeleton != null ) {
				level.backgroundBatch.setProjectionMatrix( level.backgroundCam.combined );
				level.backgroundBatch.begin( );
				level.backgroundRootSkeleton.drawBGDecals( level.backgroundBatch, level.camera );
				level.backgroundRootSkeleton.draw( level.backgroundBatch, delta );
				level.backgroundBatch.end( );
			}
			
			level.draw( batch, debugRenderer, delta );
			
			int FPS = logger.getFPS( );
			batch.setProjectionMatrix( uiCamera.combined );
			batch.begin( );
			if(debug_font != null){
				debug_font.draw(batch, "FPS: "+FPS, -Gdx.graphics.getWidth( )/2, Gdx.graphics.getHeight( )/2);//-Gdx.graphics.getWidth( )/4, Gdx.graphics.getHeight( )/4
				//debug_font.draw(batch, "ALPHA BUILD", -Gdx.graphics.getWidth( )/2, Gdx.graphics.getHeight( )/2);
			}
			batch.end( );
		}
		
		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			if(!ScreenManager.escapeHeld){
				ScreenManager.getInstance( ).show( ScreenType.PAUSE );
			}
		} else
			ScreenManager.escapeHeld = false;
	}

	protected void addForeGroundSkeleton( Skeleton skel ) {
//		if ( !level.skelFGList.contains( skel ) ) {
//			level.skelFGList.add( skel );
//		}
		addToSkelList(level.skelFGList,skel,false);
	}
	
	protected void addForeGroundSkeletonToBack( Skeleton skel ) {
//		if ( !level.skelFGList.contains( skel ) ) {
//			level.skelFGList.add( skel );
//		}
		addToSkelList(level.skelFGList,skel,true);
	}
	
	protected void addBackGroundSkeleton( Skeleton skel ) {
//		if ( !level.skelBGList.contains( skel ) ) {
//			level.skelBGList.add( skel );
//		}
		addToSkelList(level.skelBGList,skel,false);
	}
	
	protected void addBackGroundSkeletonToBack( Skeleton skel ) {
//		if ( !level.skelBGList.contains( skel ) ) {
//			level.skelBGList.add( 0, skel );
//		}
		addToSkelList(level.skelBGList,skel,true);
	}
	
	protected void addForeGroundEntity( Entity entity ) {
//		if ( !level.entityFGList.contains( entity ) ) {
//			level.entityFGList.add( entity );
//		}
		addToEntityList(level.entityFGList,entity,false);
	}
	
	protected void addForeGroundEntityToBack( Entity entity ) {
//		if ( !level.entityFGList.contains( entity ) ) {
//			level.entityFGList.add( 0, entity );
//		}
		addToEntityList(level.entityFGList,entity,true);
	}
	
	protected void addBackGroundEntity( Entity entity ) {
//		if ( !level.entityBGList.contains( entity ) ) {
//			level.entityBGList.add( entity );
//		}
		addToEntityList(level.entityBGList,entity,false);
	}
	
	protected void addBackGroundEntityToBack( Entity entity ) {
//		if ( !level.entityBGList.contains( entity ) ) {
//			level.entityBGList.add( 0, entity );
//		}
		addToEntityList(level.entityBGList,entity,true);
	}
	
	private void addToEntityList(ArrayList< Entity > list, Entity e, boolean isBack){
		if(!list.contains( e )){
			if(isBack)list.add( 0,e );
			else list.add(e);
		}
	}
	
	private void addToSkelList(ArrayList< Skeleton > list, Skeleton s, boolean isBack){
		if(!list.contains( s )){
			if(isBack)list.add( 0,s );
			else list.add(s);
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show( ) {
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
	public void resume( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose( ) {
		level.resetPhysicsWorld( );
		
	}
	
	/**
	 * Set clear color with values in 0-1 range
	 */
	public void setClearColor(float r, float g, float b, float a){
		clearColor = new Color(r,g,b,a);
	}
	
	/**
	 * Set clear color with values in 0-255 range
	 */
	public void setClearColor(int r, int g, int b, int a){
		setClearColor(r/255f,g/255f,b/255f,a/255f);
	}

	
}
