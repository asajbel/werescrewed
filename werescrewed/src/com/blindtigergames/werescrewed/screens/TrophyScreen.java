package com.blindtigergames.werescrewed.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.Metrics.TrophyMetric;

public class TrophyScreen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private int trophyLength = 5;
	private int lineHeight = 0;
	private int trophyMax = 28; // Current number of possible trophies
	private Label[ ] player1 = new Label[ trophyLength ];
	private Label[ ] player2 = new Label[ trophyLength ];
	private Texture[ ] trophies1 = new Texture[ trophyLength ]; // trophy images
																// that go next
																// to player
																// label
	private Texture[ ] trophies2 = new Texture[ trophyLength ];
	private Label player1Name = null;
	private Label player2Name = null;
	private Label next = null;

	public TrophyScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		player1Name = new Label( "Player 1", font );
		player2Name = new Label( "Player 2", font );
		next = new Label( "Press ENTER to continue", font );

		emptyTrophies( );
		addTrophies( );
		Metrics.resetTrophyMetric( );
	}

	/**
	 * Fail safe to ensure trophy lists are null
	 * 
	 */
	private void emptyTrophies( ) {
		for ( int k = 0; k < trophyLength; k++ ) {
			player1[ k ] = null;
			player2[ k ] = null;
			trophies1[ k ] = null;
			trophies2[ k ] = null;
		}
	}

	/**
	 * Randomly gives both players trophies based on actions taken in the level
	 * 
	 */
	private void addTrophies( ) {
		Random r = new Random( );
		int trophyNum = r.nextInt( trophyMax ) + 1;

		for ( int i = 0; i < trophyLength; i++ ) {
			while ( player1[ i ] == null ) {
				trophyCheck( 1, trophyNum, i );
				trophyNum = r.nextInt( trophyMax ) + 1;
			}
		}

		trophyNum = r.nextInt( trophyMax ) + 1;

		for ( int j = 0; j < trophyLength; j++ ) {
			while ( player2[ j ] == null ) {
				trophyCheck( 2, trophyNum, j );
				trophyNum = r.nextInt( trophyMax ) + 1;
			}
		}
	}

	/**
	 * Keeps track of the different types of trophies. Consider rewriting as a
	 * hashmap or stack or something to optimize and prevent repeat trophies.
	 * 
	 * @param playerNum
	 *            the player ( 1 or 2 ) who is receiving a trophy
	 * @param trophyNum
	 *            the randomly generated trophy number to look at
	 * @param index
	 *            the trophy slot the trophy is being added to
	 */
	private void trophyCheck( int playerNum, int trophyNum, int index ) {
		switch ( trophyNum ) {
		case 1: // Longest Running Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1RUNDIST ) > Metrics
							.getTrophyMetric( TrophyMetric.P2RUNDIST ) )
				player1[ index ] = new Label( "Marathon Runner", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2RUNDIST ) > Metrics
							.getTrophyMetric( TrophyMetric.P1RUNDIST ) )
				player2[ index ] = new Label( "Marathon Runner", font );
			break;
		case 2: // Most Struct Screws Unscrewed
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1UNSCREWED ) > Metrics
							.getTrophyMetric( TrophyMetric.P2UNSCREWED ) )
				player1[ index ] = new Label( "You Got A Screw Loose", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2UNSCREWED ) > Metrics
							.getTrophyMetric( TrophyMetric.P1UNSCREWED ) )
				player2[ index ] = new Label( "You Got A Screw Loose", font );
			break;
		case 3: // Longest Air Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1AIRTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2AIRTIME ) )
				player1[ index ] = new Label( "Hang Time", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2AIRTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1AIRTIME ) )
				player2[ index ] = new Label( "Hang Time", font );
			break;
		case 4: // Most Fall Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1FALLDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2FALLDEATHS ) )
				player1[ index ] = new Label( "Fall Guy", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2FALLDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1FALLDEATHS ) )
				player2[ index ] = new Label( "Fall Guy", font );
			break;
		case 5: // Most Time Spent On Puzzle Screws
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1PUZZLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2PUZZLETIME ) )
				player1[ index ] = new Label( "Inventor's Apprentice", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2PUZZLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1PUZZLETIME ) )
				player2[ index ] = new Label( "Inventor's Apprentice", font );
			break;
		case 6: // Most Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2DEATHS ) )
				player1[ index ] = new Label( "Call The Suicide Hotline", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1DEATHS ) )
				player2[ index ] = new Label( "Call The Suicide Hotline", font );
			break;
		case 7: // Most Head Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1HEADSTANDS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2HEADSTANDS ) )
				player1[ index ] = new Label( "Always On Top", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2HEADSTANDS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1HEADSTANDS ) )
				player2[ index ] = new Label( "Always On Top", font );
			break;
		case 8: // Most Revives
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1REVIVES ) > Metrics
							.getTrophyMetric( TrophyMetric.P2REVIVES ) )
				player1[ index ] = new Label( "I Help Dead People", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2REVIVES ) > Metrics
							.getTrophyMetric( TrophyMetric.P1REVIVES ) )
				player2[ index ] = new Label( "I Help Dead People", font );
			break;
		case 9: // Most Secondary Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1TEAMDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2TEAMDEATHS ) )
				player1[ index ] = new Label( "Well, He Jumped First", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2TEAMDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1TEAMDEATHS ) )
				player2[ index ] = new Label( "Well, He Jumped First", font );
			break;
		case 10: // Longest Idle Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1IDLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2IDLETIME ) )
				player1[ index ] = new Label( "I'm Waaaaiting!!", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2IDLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1IDLETIME ) )
				player2[ index ] = new Label( "I'm Waaaaiting!!", font );
			break;
		case 11: // No Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEATHS ) == 0 )
				player1[ index ] = new Label( "Are You Using God Mode?", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEATHS ) == 0 )
				player2[ index ] = new Label( "Are You Using God Mode?", font );
			break;
		case 12: // Most Crush Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1CRUSHDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2CRUSHDEATHS ) )
				player1[ index ] = new Label( "Crushing Defeat", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2CRUSHDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1CRUSHDEATHS ) )
				player2[ index ] = new Label( "Crushing Defeat", font );
			break;
		case 13: // Most Electrocution Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1ELECDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2ELECDEATHS ) )
				player1[ index ] = new Label( "A Shocking Revelation", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2ELECDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1ELECDEATHS ) )
				player2[ index ] = new Label( "A Shocking Revelation", font );
			break;
		case 14: // Longest Time Grounded
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1GROUNDTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2GROUNDTIME ) )
				player1[ index ] = new Label( "Landlubber", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2GROUNDTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1GROUNDTIME ) )
				player2[ index ] = new Label( "Landlubber", font );
			break;
		case 15: // Most Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1JUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2JUMPS ) )
				player1[ index ] = new Label( "Jumpin' Jack", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2JUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1JUMPS ) )
				player2[ index ] = new Label( "Jumpin' Jack", font );
			break;
		case 16: // Most Spike Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1SPIKEDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2SPIKEDEATHS ) )
				player1[ index ] = new Label( "Vlad the Impaled", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2SPIKEDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1SPIKEDEATHS ) )
				player2[ index ] = new Label( "Vlad the Impaled", font );
			break;
		case 17: // Most Fire Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1FIREDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2FIREDEATHS ) )
				player1[ index ] = new Label( "Hot And Bothered", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2FIREDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1FIREDEATHS ) )
				player2[ index ] = new Label( "Hot And Bothered", font );
			break;
		case 18: // Longest Time Dead
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEADTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2DEADTIME ) )
				player1[ index ] = new Label( "Ghostly Gamer", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEADTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1DEADTIME ) )
				player2[ index ] = new Label( "Ghostly Gamer", font );
			break;
		case 19: // Most Steam Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1STEAMJUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2STEAMJUMPS ) )
				player1[ index ] = new Label( "Steam Powered", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2STEAMJUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1STEAMJUMPS ) )
				player2[ index ] = new Label( "Steam Powered", font );
			break;
		case 20: // Most Strip Screws Attached To
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1STRIPATTACH ) > Metrics
							.getTrophyMetric( TrophyMetric.P2STRIPATTACH ) )
				player1[ index ] = new Label( "Crafty Climber", font );
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2STRIPATTACH ) > Metrics
							.getTrophyMetric( TrophyMetric.P1STRIPATTACH ) )
				player2[ index ] = new Label( "Crafty Climber", font );
			break;
		case 21: // Random 1
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Best 'Stache", font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Best Dressed", font );
			}
			break;
		case 22: // Random 2
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "My Screwdriver's Bigger", font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "My Screwdriver's Bigger", font );
			}
			break;
		case 23: // Random 3
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "You Unlocked An Achievement!!",
						font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "You Unlocked An Achievement!!",
						font );
			}
			break;
		case 24: // Random 4
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Most Popular", font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Most Popular", font );
			}
			break;
		case 25: // Random 5
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Gives Awesome Hugs", font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Gives Awesome Hugs", font );
			}
			break;
		case 26: // Random 6
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Better Than Bacon Ice Cream",
						font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Better Than Bacon Ice Cream",
						font );
			}
			break;
		case 27: // Random 7
			if ( playerNum == 1 ) {
				player1[ index ] = new Label(
						"You Played The Best Game Ever!!", font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label(
						"You Played The Best Game Ever!!", font );
			}
			break;
		case 28: // Random 8
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Free Cookie", font );
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Free Cookie", font );
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.8f, 0.6f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

		batch.begin( );
		player1Name.draw( batch );
		player2Name.draw( batch );
		for ( int i = 0; i < trophyLength; i++ ) {
			player1[ i ].draw( batch );
			player2[ i ].draw( batch );
		}
		next.draw( batch );
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			ScreenManager.getInstance( ).show( ScreenType.MAIN_MENU );
		}
	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		player1Name.setX( centerX / 2 - player1Name.getWidth( ) / 2 );
		player1Name.setY( centerY + lineHeight * ( trophyLength + 1 ) );
		player2Name.setX( centerX - player1Name.getWidth( ) / 2 );
		player2Name.setY( centerY + lineHeight * ( trophyLength + 1 ) );
		for ( int j = 0; j < trophyLength; j++ ) {
			player1[ j ].setX( centerX / 2 - player1[ j ].getWidth( ) / 2 );
			player1[ j ].setY( centerY + lineHeight * ( j + 1 ) );

			player2[ j ].setX( centerX - player2[ j ].getWidth( ) / 2 );
			player2[ j ].setY( centerY + lineHeight * ( j + 1 ) );
		}
		next.setX( centerX - next.getWidth( ) / 2 );
		next.setY( 20 + next.getHeight( ) );
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
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub

	}
}
