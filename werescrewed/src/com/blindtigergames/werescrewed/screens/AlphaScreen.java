package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.input.MyControllerListener;
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
			p.draw( batch, deltaTime );
			
		}
		
		if(screwDraw){
			Vector2 pos = players.get( screwIndex % players.size( ) ).getPositionPixel( );
			batch.draw( arrowSelection, pos.x + 50f, pos.y + 150f );
		}
		
		batch.end( );
		
		controller1update();
		controller2update();
		
		if( controller1 == null && controller2 == null){
			//TODO keyboard selection
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
	
	private void spawnPlayer( int controllerNum, int index ){
		
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
		
		if( controllerNum == 0){
			level.player1 = pb.buildPlayer( );
			level.player1.setControllerIndex( 0 );
		}
		else if( controllerNum == 1 ){
			level.player2 = pb.buildPlayer( );
			level.player2.setControllerIndex( 1 );
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
					spawnPlayer( 0, screwIndex % players.size( ) );
					screwDraw = false;
					screwIndex = 0;
					player1Spawned = true;
				}
			}
		}
	}
	
	public void controller2update(){
		if(controller2 != null && !player2Spawned ){
			if(!player2HitStart&& !player1HitStart){
				if(controllerListener2.pausePressed( )){
					player2HitStart = true;
					System.out.println( player2HitStart );
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
					spawnPlayer( 1, screwIndex % players.size( ) );
					screwDraw = false;
					screwIndex = 0;
					player2Spawned = true;
				}

			}
		}
	}
	
	
	
}
