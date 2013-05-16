package com.blindtigergames.werescrewed.level;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.input.MyControllerListener;
import com.blindtigergames.werescrewed.input.PlayerInputHandler;

public class CharacterSelect {

	private Level level;

	private Controller controller1;
	private Controller controller2;
	private MyControllerListener controllerListener1, controllerListener2;

	private boolean player1Spawned = false, player2Spawned = false,
			player1HitStart = false, player2HitStart = false;
	private ArrayList< Sprite > players;
	private TextureRegion arrowSelection = WereScrewedGame.manager.getAtlas(
			"common-textures" ).findRegion( "screw-flathead" );
	private Texture male = WereScrewedGame.manager.get(
			WereScrewedGame.dirHandle.path( )
					+ "/levels/level1/player_male_idle.png", Texture.class );
	private Texture female = WereScrewedGame.manager.get(
			WereScrewedGame.dirHandle.path( )
					+ "/levels/level1/player_female_idle.png", Texture.class );
	private boolean screwDraw = false;
	private int screwIndex = 0;

	private boolean p1LeftHit, p1RightHit, p2LeftHit, p2RightHit;
	private boolean noControllersAttached;

	public CharacterSelect( Level level ) {
		this.level = level;

		controllerSetUp( );
		audience( );

	}

	public void update( ) {
		controller1update( );
		controller2update( );

		// keyboard + one controller, controller becomes player 1, keyboard
		// becomes player2
		updateKeyboardWithOneControllerAttached( );

		// no controllers
		if ( controller1 == null && controller2 == null ) {
			noControllersAttached = true;
			if ( level.player1 == null ) {
				level.player1 = new PlayerBuilder( ).world( level.world )
						.position( 100f, 100f ).name( "player1" )
						.definition( "ref_male" ).buildPlayer( );
			}
			if ( level.player2 == null ) {
				level.player2 = new PlayerBuilder( ).world( level.world )
						.position( 100f, 100f ).name( "player2" )
						.definition( "red_female" ).buildPlayer( );
			}
		}

	}

	public void draw( SpriteBatch batch, float deltaTime ) {
		batch.setProjectionMatrix( level.camera.combined( ) );
		batch.begin( );
		for ( int i = 0; i < players.size( ); i++ ) {
			// Player p = players.get( i );
			// p.update( deltaTime );
			// p.draw( batch, deltaTime );
			Sprite s = players.get( i );
			s.draw( batch );

		}

		if ( screwDraw ) {
			Vector2 pos = new Vector2( players
					.get( screwIndex % players.size( ) ).getX( ), players.get(
					screwIndex % players.size( ) ).getY( ) );
			batch.draw( arrowSelection, pos.x + 50f, pos.y + 150f );
		}

		batch.end( );
	}

	private void controllerSetUp( ) {

		if ( Controllers.getControllers( ).size > 0 ) {
			controllerListener1 = new MyControllerListener( );
			controller1 = Controllers.getControllers( ).get( 0 );
			controller1.addListener( controllerListener1 );
		}
		if ( Controllers.getControllers( ).size > 1 ) {
			controllerListener2 = new MyControllerListener( );
			controller2 = Controllers.getControllers( ).get( 1 );
			controller2.addListener( controllerListener2 );
		}
	}

	private void audience( ) {
		players = new ArrayList< Sprite >( );

		Sprite p1 = new Sprite( male );
		p1.setPosition( new Vector2( -200f, -250f ) );
		players.add( p1 );

		Sprite p2 = new Sprite( female );
		p2.setPosition( new Vector2( -100f, -250f ) );
		players.add( p2 );

		Sprite p3 = new Sprite( male );
		p3.setPosition( new Vector2( 0f, -250f ) );
		players.add( p3 );

		Sprite p4 = new Sprite( female );
		p4.setPosition( new Vector2( 100f, -250f ) );
		players.add( p4 );

	}

	private void spawnPlayer( int playerNumber, int index,
			boolean controllerActive ) {

		PlayerBuilder pb = new PlayerBuilder( ).world( level.world ).position(
				100f, 100f );

		switch ( index ) {
		case 0:
			pb.definition( "red_male" );
			break;
		case 1:
			pb.definition( "red_female" );
			break;
		case 2:
			pb.definition( "red_male" );
			break;
		case 3:
			pb.definition( "red_female" );
			break;
		}
		// Player 1
		if ( playerNumber == 0 ) {
			level.player1 = pb.buildPlayer( );
			if ( controllerActive )
				level.player1.setControllerIndex( playerNumber );
			else {
				level.player1.setController( null );
			}
		}
		// Player 2
		else if ( playerNumber == 1 ) {
			level.player2 = pb.buildPlayer( );
			if ( controllerActive )
				level.player2.setControllerIndex( playerNumber );
			else {
				level.player2.setController( null );

				// If there is a controller, but we are spawning a player using
				// keyboard to play
				// then we want the player to use WASD not IJKL (as player 2)
				if ( !noControllersAttached )
					level.player2.inputHandler = new PlayerInputHandler(
							"player1" );
			}
		}
	}

	private void controller1update( ) {
		if ( controller1 != null && !player1Spawned ) {
			if ( !player1HitStart && !player2HitStart ) {
				if ( controllerListener1.pausePressed( ) ) {
					player1HitStart = true;
					screwDraw = true;
					screwIndex = 0;

				}
			} else {
				if ( controllerListener1.leftPressed( ) ) {
					if ( !p1LeftHit ) {
						p1LeftHit = true;
						if ( screwIndex == 0 ) {
							screwIndex = players.size( ) - 1;
						} else
							screwIndex--;
					}
				} else {
					p1LeftHit = false;
				}
				if ( controllerListener1.rightPressed( ) ) {
					if ( !p1RightHit ) {
						p1RightHit = true;
						screwIndex++;
					}
				} else {
					p1RightHit = false;
				}

				if ( controllerListener1.jumpPressed( ) ) {
					spawnPlayer( 0, screwIndex % players.size( ), true );
					screwDraw = false;
					screwIndex = 0;
					player1Spawned = true;
					player1HitStart = false;
				}
			}
		}
	}

	public void controller2update( ) {
		if ( controller2 != null && !player2Spawned ) {
			if ( !player2HitStart && !player1HitStart ) {
				if ( controllerListener2.pausePressed( ) ) {
					player2HitStart = true;
					screwDraw = true;
					screwIndex = 0;
				}
			} else {
				if ( controllerListener2.leftPressed( ) ) {
					if ( !p2LeftHit ) {
						p2LeftHit = true;
						if ( screwIndex == 0 ) {
							screwIndex = players.size( ) - 1;
						} else
							screwIndex--;
					}
				} else {
					p2LeftHit = false;
				}
				if ( controllerListener2.rightPressed( ) ) {
					if ( !p2RightHit ) {
						p2RightHit = true;
						screwIndex++;
					}
				} else {
					p2RightHit = false;
				}

				if ( controllerListener2.jumpPressed( ) ) {
					spawnPlayer( 1, screwIndex % players.size( ), true );
					screwDraw = false;
					screwIndex = 0;
					player2Spawned = true;
					player2HitStart = false;
				}

			}
		}
	}

	private void updateKeyboardWithOneControllerAttached( ) {
		if ( controller1 != null && controller2 == null && !player2Spawned ) {
			if ( !player1HitStart && !player2HitStart ) {
				if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
					player2HitStart = true;
					screwDraw = true;
					screwIndex = 0;
				}
			} else {
				// Left
				if ( Gdx.input.isKeyPressed( Keys.A ) ) {
					if ( !p2LeftHit ) {
						p2LeftHit = true;
						if ( screwIndex == 0 ) {
							screwIndex = players.size( ) - 1;
						} else
							screwIndex--;
					}
				} else {
					p2LeftHit = false;
				}
				if ( Gdx.input.isKeyPressed( Keys.D ) ) {
					if ( !p2RightHit ) {
						p2RightHit = true;
						screwIndex++;
					}
				} else {
					p2RightHit = false;
				}

				if ( Gdx.input.isKeyPressed( Keys.SPACE ) ) {
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