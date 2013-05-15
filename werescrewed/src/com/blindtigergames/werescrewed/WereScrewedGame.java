package com.blindtigergames.werescrewed;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.blindtigergames.werescrewed.asset.AssetManager;
import com.blindtigergames.werescrewed.screens.ScreenManager;
import com.blindtigergames.werescrewed.screens.ScreenType;
import com.blindtigergames.werescrewed.util.Metrics;

public class WereScrewedGame extends Game {

	public static final float targetFrameRate = 30f;
	public static final float oneOverTargetFrameRate = 1f/targetFrameRate;
	
	public static AssetManager manager;
	public static FileHandle dirHandle;

	public FPSLogger logger;
	
	private boolean restartFlag = false;
	private boolean metricsFlag = false;
	
	private static boolean mouseJustClicked = false;
	public static boolean isMouseClicked(){return mouseJustClicked;}
	
	public static ShaderProgram defaultShader;
	
	public static Random random;
	
	public static Controller p1Controller, p2Controller;
	public static MyControllerListener p1ControllerListener, p2ControllerListener;
	
	// used to decide which player is which character, if player1female is false then
	// player 1 is the male character
	public static boolean player1Female = false;
	@SuppressWarnings( "unused" )
	private float fpsTime = 0;

	@Override
	public void create( ) {
		random = new Random(0);
		manager = new AssetManager( );
		
		ScreenManager.getInstance( ).initialize( this );
		
        //used to stop auto call of render
        //Gdx.graphics.setContinuousRendering(false);
        
		if (Gdx.graphics.isGL20Available( ))
			defaultShader = SpriteBatch.createDefaultShader( );
		else
			defaultShader = null;

		ScreenManager.getInstance( ).show( ScreenType.LOADING );

		logger = new FPSLogger( );

		manager.loadDummyAssets( );
		
		setUpControllers( );
	}

	@Override
	public void dispose( ) {
		super.dispose( );
		ScreenManager.getInstance( ).dispose( );
	}

	@Override
	public void render( ) {
		//update( 0 );
		if ( Gdx.input.isKeyPressed( Keys.SHIFT_LEFT ) && Gdx.input.isKeyPressed( Keys.ESCAPE ) ){
			if ( !restartFlag ){
				restartFlag = true;
				restart( );
			}
		}else{
			restartFlag = false;
		}
		
		if(Gdx.input.isKeyPressed( Keys.SEMICOLON )){
			if( !metricsFlag ){
				metricsFlag = true;
				Metrics.activated = !Metrics.activated;
				Gdx.app.log( "Metrics activated", "" + Metrics.activated );
			}
		}else{
			metricsFlag = false;
		}
		
		
		
		//super.render( );
		if (Gdx.app.getType() == ApplicationType.Android) {
			logger.log( ); 
		}
//		logger.log( );
		
		super.render( );
		
//		float deltaTime = Gdx.graphics.getDeltaTime( );
//		fpsTime += deltaTime;
//		if ( fpsTime >= oneOverTargetFrameRate ){
//			fpsTime = fpsTime - oneOverTargetFrameRate;
//			//request a render
//			Gdx.graphics.requestRendering();
//			super.render( );
//		}
		
		if (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Buttons.LEFT)){
			mouseJustClicked = true;
		} else {
			mouseJustClicked = false;
		}
		return;
		
	}
	
	public void restart(){
		this.dispose();
		manager = new AssetManager( );
		ScreenManager.getInstance( ).initialize( this );
		ScreenManager.getInstance( ).show( ScreenType.LOADING );
	}

	public void update( float dT ) {
	}

	private void setUpControllers( ) {
		for ( Controller controller : Controllers.getControllers( ) ) {
			Gdx.app.log( "controllers", controller.getName( ) );
		}
		if ( Controllers.getControllers( ).size >= 1 ) {
			
			p1ControllerListener = new MyControllerListener( );
			p1Controller = Controllers.getControllers( ).get( 0 );
			p1Controller.addListener( p1ControllerListener );
			
		}
		if ( Controllers.getControllers( ).size >= 2 ) {

			p2ControllerListener = new MyControllerListener( );
			p2Controller = Controllers.getControllers( ).get( 1 );
			p2Controller.addListener( p2ControllerListener );
			
		}

	}
}
