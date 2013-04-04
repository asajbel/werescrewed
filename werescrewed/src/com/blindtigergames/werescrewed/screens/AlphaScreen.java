package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.player.Player;

public class AlphaScreen extends Screen {

	public ScreenType screenType;
	Music music;
	Music intro, loop;
	boolean introPlayed = false;
	
	private Controller controller1;
	private Controller controller2;
	private MyControllerListener controllerListener1, controllerListener2;
	
	private boolean player1Spawned = false, player2Spawned = false,
			player1HitStart = false, player2HitStart = false;
	private ArrayList<Player> players;
	private Texture arrowSelection = WereScrewedGame.manager.get(
			WereScrewedGame.dirHandle.path( ) + "/common/screw/screw.png",
			Texture.class);
	private boolean screwDraw = false;
	private int screwIndex = 0;
	
	private boolean p1LeftHit, p1RightHit, p2LeftHit, p2RightHit;
	private boolean noControllersAttached;

	public AlphaScreen( ) {
		super( );
		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );
		music = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/TrainJob.mp3" );
		loop = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/introTrain.mp3" );
		intro = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/loopTrain.mp3" );
		
		controllerSetUp( );
		audience( );
		
		Skeleton skel2 = ( Skeleton ) LevelFactory.entities.get( "skeleton2" );
		
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		//updateMusic();
		
		batch.setProjectionMatrix( level.camera.combined( ) );
		batch.begin( );
		for( int i = 0; i < players.size( ); i++){
			Player p = players.get( i );
			p.update( deltaTime );
			p.draw( batch );
			
		}
		
		if(screwDraw){
			Vector2 pos = players.get( screwIndex % players.size( ) ).getPositionPixel( );
			batch.draw( arrowSelection, pos.x + 50f, pos.y + 150f );
		}
		
		batch.end( );
		
		controller1update();
		controller2update();
		
		//keyboard + one controller, controller becomes player 1, keyboard becomes player2
		updateKeyboardWithOneControllerAttached();
		
		// no controllers
		if( controller1 == null && controller2 == null){
			noControllersAttached = true;
			if(level.player1 == null){
				level.player1 = new PlayerBuilder( ).world( level.world )
				.position( 100f, 100f ).name( "player1" ).buildPlayer( );
			}
			if(level.player2 == null){
				level.player2 = new PlayerBuilder( ).world( level.world )
				.position( 100f, 100f ).name( "player2" ).buildPlayer( );
			}
		}

		
		
	}
	
	private void updateMusic(){
		// Doesn't work perfectly, but its okay
		if(!introPlayed){
			intro.play( );
			introPlayed = true;
		} else {
			if(!intro.isPlaying( )){
				if(!loop.isPlaying( )){
					loop.play( );
					loop.setLooping( true );
				}
			}
		}
	}
	
	
	
	private void controllerSetUp( ){
		
		if( Controllers.getControllers( ).size > 0){
			controllerListener1 = new MyControllerListener( );
			controller1 = Controllers.getControllers( ).get( 0 );
			controller1.addListener( controllerListener1 );
		}
		if( Controllers.getControllers( ).size > 1){
			controllerListener2 = new MyControllerListener( );
			controller2 = Controllers.getControllers( ).get( 1 );
			controller2.addListener( controllerListener2 );
		}
	}
	
	
	private void audience(){
		players = new ArrayList<Player>();
		Player p1 =  new PlayerBuilder( ).name( "player1" ).world( level.world )
				.position( -200.0f, -150.0f ).buildPlayer( );
		p1.setInputNull();
		players.add( p1 );
		
		Player p2 =  new PlayerBuilder( ).name( "player2" ).world( level.world )
				.position( -100.0f, -150.0f ).buildPlayer( );
		p2.setInputNull();
		players.add( p2 );
		
		Player p3 =  new PlayerBuilder( ).name( "player1" ).world( level.world )
				.position( 0.0f, -150.0f ).buildPlayer( );
		p3.setInputNull();
		players.add( p3 );
		
		Player p4 =  new PlayerBuilder( ).name( "player2" ).world( level.world )
				.position( 100.0f, -150.0f ).buildPlayer( );
		p4.setInputNull();
		players.add( p4 );

	}
	
	private void spawnPlayer( int playerNumber, int index, boolean controllerActive ){
		
		PlayerBuilder pb = new PlayerBuilder( ).world( level.world )
				.position( 100f, 100f );
		
		switch(index){
		case 0:
			pb.name( "player1" );
			break;
		case 1:
			pb.name( "player2" );
			break;
		case 2: 
			pb.name( "player1" );
			break;
		case 3:
			pb.name( "player2" );
			break;
		}
		//Player 1
		if( playerNumber == 0 ){
			level.player1 = pb.buildPlayer( );
			if(controllerActive)
				level.player1.setControllerIndex( playerNumber );
			else{
				level.player1.setController( null );
			}
		}
		//Player 2
		else if( playerNumber == 1 ){
			level.player2 = pb.buildPlayer( );
			if(controllerActive)
				level.player2.setControllerIndex( playerNumber );
			else{
				level.player2.setController( null );
				
				//If there is a controller, but we are spawning a player using keyboard to play
				// then we want the player to use WASD not IJKL (as player 2)
				if(!noControllersAttached)
					level.player2.inputHandler = new PlayerInputHandler("player1");
			}
		}
	}
	

	private void controller1update(){
		if(controller1 != null && !player1Spawned)
		{
			if(!player1HitStart && !player2HitStart){
				if(controllerListener1.pausePressed( )){
					player1HitStart = true;
					screwDraw = true;
					screwIndex = 0;
					
				}
			}else{
				if(controllerListener1.leftPressed( )){
					if(!p1LeftHit){
						p1LeftHit = true;
						if(screwIndex == 0){
							screwIndex = players.size()-1;
						}else
							screwIndex--;
					}
				}else{
					p1LeftHit = false;
				}
				if(controllerListener1.rightPressed( )){
					if(!p1RightHit){
						p1RightHit = true;
						screwIndex++;
					}
				}else{
					p1RightHit = false;
				}
				
				if( controllerListener1.jumpPressed( )){
					spawnPlayer( 0, screwIndex % players.size( ), true );
					screwDraw = false;
					screwIndex = 0;
					player1Spawned = true;
					player1HitStart = false;
				}
			}
		}
	}
	
	public void controller2update(){
		if(controller2 != null && !player2Spawned ){
			if(!player2HitStart&& !player1HitStart){
				if(controllerListener2.pausePressed( )){
					player2HitStart = true;
					screwDraw = true;
					screwIndex = 0;
				}
			}else{
				if(controllerListener2.leftPressed( )){
					if(!p2LeftHit){
						p2LeftHit = true;
						if(screwIndex == 0){
							screwIndex = players.size()-1;
						}else
							screwIndex--;
					}
				}else{
					p2LeftHit = false;
				}
				if(controllerListener2.rightPressed( )){
					if(!p2RightHit){
						p2RightHit = true;
						screwIndex++;
					}
				}else{
					p2RightHit = false;
				}
				
				if( controllerListener2.jumpPressed( )){
					spawnPlayer( 1, screwIndex % players.size( ), true );
					screwDraw = false;
					screwIndex = 0;
					player2Spawned = true;
					player2HitStart = false;
				}

			}
		}
	}
	
	private void updateKeyboardWithOneControllerAttached(){
		if(controller1 != null && controller2 == null && !player2Spawned){
			if(!player1HitStart && !player2HitStart){
				if(Gdx.input.isKeyPressed( Keys.ENTER )){
					player2HitStart = true;
					screwDraw = true;
					screwIndex = 0;
				}
			}else{
				//Left
				if(Gdx.input.isKeyPressed( Keys.A )){
					if(!p2LeftHit){
						p2LeftHit = true;
						if(screwIndex == 0){
							screwIndex = players.size()-1;
						}else
							screwIndex--;
					}
				}else{
					p2LeftHit = false;
				}
				if(Gdx.input.isKeyPressed( Keys.D )){
					if(!p2RightHit){
						p2RightHit = true;
						screwIndex++;
					}
				}else{
					p2RightHit = false;
				}
				
				if( Gdx.input.isKeyPressed( Keys.SPACE)){
					spawnPlayer( 1, screwIndex % players.size( ), false );
					screwDraw = false;
					screwIndex = 0;
					player2Spawned = true;
					player2HitStart = false;
				}

			}
		}
	}
	
}
