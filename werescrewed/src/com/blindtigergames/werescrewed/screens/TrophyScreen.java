package com.blindtigergames.werescrewed.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.Metrics.TrophyMetric;

public class TrophyScreen extends Screen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont;
	private int trophyLength = 5;
	private int lineHeight = 0;
	private int trophyMax = 30; // Current number of possible trophies
	private float offSet = 96;
	private ScreenType screenTag = null;
	private Label[ ] player1 = new Label[ trophyLength ];
	private Label[ ] player2 = new Label[ trophyLength ];
	private TextureRegion[ ] trophyIcon = new TextureRegion[ trophyMax ]; // Holds ALL trophy icons
	private Sprite[ ] trophies1 = new Sprite[ trophyLength ]; // trophy images
																// that go next
																// to player
																// label
	private Sprite[ ] trophies2 = new Sprite[ trophyLength ];
	private int[] trophyIndices = new int[ 2 * trophyLength ];
	private Label player1Name = null;
	private Label player2Name = null;
	private TextButton next = null;

	public TrophyScreen( ScreenType nextLvl ) {
		if ( nextLvl == null ) {
			screenTag = ScreenType.MAIN_MENU;
		}
		else {
			screenTag = nextLvl;
		}
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		player1Name = new Label( "Player 1", fancyFont );
		player2Name = new Label( "Player 2", fancyFont );
		next = new TextButton( "Next Level", fancyFont, 
				new ScreenSwitchHandler( screenTag ) );
		next.setColored( true );

		emptyTrophies( );
		addTrophies( );
		Metrics.resetTrophyMetric( );
		
		//offSet = trophies1[ 0 ].getWidth( ) + 50;
	}
	
	public TrophyScreen( ) {
		this( null );
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
		for (int i = 0; i < trophyIndices.length ; i++){
			if ( trophyIndices[i] == trophyNum ) return;
		}
		
		switch ( trophyNum ) {
		case 1: // Longest Running Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1RUNDIST ) > Metrics
							.getTrophyMetric( TrophyMetric.P2RUNDIST ) ){
				player1[ index ] = new Label( "Marathon Runner", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2RUNDIST ) > Metrics
							.getTrophyMetric( TrophyMetric.P1RUNDIST ) ){
				player2[ index ] = new Label( "Marathon Runner", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 2: // Most Struct Screws Unscrewed
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1UNSCREWED ) > Metrics
							.getTrophyMetric( TrophyMetric.P2UNSCREWED ) ){
				player1[ index ] = new Label( "You Got A Screw Loose", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2UNSCREWED ) > Metrics
							.getTrophyMetric( TrophyMetric.P1UNSCREWED ) ){
				player2[ index ] = new Label( "You Got A Screw Loose", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 3: // Longest Air Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1AIRTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2AIRTIME ) ){
				player1[ index ] = new Label( "Hang Time", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2AIRTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1AIRTIME ) ){
				player2[ index ] = new Label( "Hang Time", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 4: // Most Fall Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1FALLDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2FALLDEATHS ) ){
				player1[ index ] = new Label( "Fall Guy", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2FALLDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1FALLDEATHS ) ){
				player2[ index ] = new Label( "Fall Guy", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 5: // Most Time Spent On Puzzle Screws
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1PUZZLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2PUZZLETIME ) ){
				player1[ index ] = new Label( "Inventor's Apprentice", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2PUZZLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1PUZZLETIME ) ){
				player2[ index ] = new Label( "Inventor's Apprentice", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 6: // Most Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2DEATHS ) ){
				player1[ index ] = new Label( "Call The Suicide \n Hotline", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1DEATHS ) ){
				player2[ index ] = new Label( "Call The Suicide \n Hotline", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 7: // Most Head Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1HEADSTANDS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2HEADSTANDS ) ){
				player1[ index ] = new Label( "Always On Top", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2HEADSTANDS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1HEADSTANDS ) ){
				player2[ index ] = new Label( "Always On Top", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 8: // Most Revives
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1REVIVES ) > Metrics
							.getTrophyMetric( TrophyMetric.P2REVIVES ) ){
				player1[ index ] = new Label( "I Help Dead People", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2REVIVES ) > Metrics
							.getTrophyMetric( TrophyMetric.P1REVIVES ) ){
				player2[ index ] = new Label( "I Help Dead People", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 9: // Most Secondary Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1TEAMDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2TEAMDEATHS ) ){
				player1[ index ] = new Label( "Well, He Jumped First", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2TEAMDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1TEAMDEATHS ) ){
				player2[ index ] = new Label( "Well, He Jumped First", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 10: // Longest Idle Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1IDLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2IDLETIME ) ){
				player1[ index ] = new Label( "I'm Waaaaiting!!", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2IDLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1IDLETIME ) ){
				player2[ index ] = new Label( "I'm Waaaaiting!!", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 11: // No Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEATHS ) == 0 ){
				player1[ index ] = new Label( "Are You Using \n God Mode?", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEATHS ) == 0 ){
				player2[ index ] = new Label( "Are You Using \n God Mode?", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 12: // Most Crush Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1CRUSHDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2CRUSHDEATHS ) ){
				player1[ index ] = new Label( "Crushing Defeat", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2CRUSHDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1CRUSHDEATHS ) ){
				player2[ index ] = new Label( "Crushing Defeat", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 13: // Most Electrocution Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1ELECDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2ELECDEATHS ) ){
				player1[ index ] = new Label( "A Shocking Revelation", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2ELECDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1ELECDEATHS ) ){
				player2[ index ] = new Label( "A Shocking Revelation", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 14: // Longest Time Grounded
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1GROUNDTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2GROUNDTIME ) ){
				player1[ index ] = new Label( "Landlubber", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2GROUNDTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1GROUNDTIME ) ){
				player2[ index ] = new Label( "Landlubber", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 15: // Most Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1JUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2JUMPS ) ){
				player1[ index ] = new Label( "Jumpin' Jack", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2JUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1JUMPS ) ){
				player2[ index ] = new Label( "Jumpin' Jack", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 16: // Most Spike Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1SPIKEDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2SPIKEDEATHS ) ){
				player1[ index ] = new Label( "Vlad the Impaled", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2SPIKEDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1SPIKEDEATHS ) ){
				player2[ index ] = new Label( "Vlad the Impaled", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 17: // Most Fire Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1FIREDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2FIREDEATHS ) ){
				player1[ index ] = new Label( "Hot And Bothered", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2FIREDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1FIREDEATHS ) ){
				player2[ index ] = new Label( "Hot And Bothered", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 18: // Longest Time Dead
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEADTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2DEADTIME ) ){
				player1[ index ] = new Label( "Ghostly Gamer", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEADTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1DEADTIME ) ){
				player2[ index ] = new Label( "Ghostly Gamer", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 19: // Most Steam Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1STEAMJUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2STEAMJUMPS ) ){
				player1[ index ] = new Label( "Steam Powered", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2STEAMJUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1STEAMJUMPS ) ){
				player2[ index ] = new Label( "Steam Powered", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 20: // Most Strip Screws Attached To
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1STRIPATTACH ) > Metrics
							.getTrophyMetric( TrophyMetric.P2STRIPATTACH ) ){
				player1[ index ] = new Label( "Crafty Climber", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2STRIPATTACH ) > Metrics
							.getTrophyMetric( TrophyMetric.P1STRIPATTACH ) ){
				player2[ index ] = new Label( "Crafty Climber", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 21: // Random 1
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Best 'Stache", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Best Dressed", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 22: // Random 2
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "My Screwdriver's Bigger", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "My Screwdriver's Bigger", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 23: // Random 3
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "You Unlocked \n An Achievement!!", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "You Unlocked \n An Achievement!!", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 24: // Random 4
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Most Popular", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Most Popular", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 25: // Random 5
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Gives Awesome Hugs", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Gives Awesome Hugs", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 26: // Random 6
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Better Than \n Bacon Ice Cream", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Better Than \n Bacon Ice Cream", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 27: // Random 7
			if ( playerNum == 1 ) {
				player1[ index ] = new Label(
						"You Played \n The Best Game Ever!!", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label(
						"You Played \n The Best Game Ever!!", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 28: // Random 8
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Free Cookie", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Free Cookie", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 29: // Random 9
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Supremely Screwy", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Supremely Screwy", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 30: // Random 10
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Antidisestablishmentarianist", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Antidisestablishmentarianist", fancyFont );
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies2[ index ] = new Sprite ( icon );
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0.4f, 0.2f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

		batch.begin( );
		player1Name.draw( batch );
		player2Name.draw( batch );
		for ( int i = 0; i < trophyLength; i++ ) {
			player1[ i ].draw( batch );
			trophies1[ i ].draw( batch );
			player2[ i ].draw( batch );
			trophies2[ i ].draw( batch );
		}
		next.draw( batch, camera );
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			ScreenManager.getInstance( ).show( screenTag );
		}
	}

	@Override
	public void resize( int width, int height ) {
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		player1Name.setX( 0 + ( int ) offSet );
		player1Name.setY( centerY / 2 + lineHeight * 3 * ( trophyLength + 1 ) );
		player2Name.setX( centerX + ( int ) offSet );
		player2Name.setY( centerY / 2 + lineHeight * 3 * ( trophyLength + 1 ) );
		for ( int j = 0; j < trophyLength; j++ ) {
			player1[ j ].setX( 0 + ( int ) offSet );
			player1[ j ].setY( centerY / 2 + lineHeight * 3 * ( j + 1 ) );
			trophies1[ j ].setPosition( 16, centerY / 2 + lineHeight * 3 * ( j ) + trophies1[ j ].getHeight( ) / 2 );

			player2[ j ].setX( centerX + ( int ) offSet );
			player2[ j ].setY( centerY / 2 + lineHeight * 3 * ( j + 1 ) );
			trophies2[ j ].setPosition( centerX, centerY / 2 + lineHeight * 3 * ( j ) + trophies2[ j ].getHeight( ) / 2 );
			
		}
		next.setX( centerX - next.getWidth( ) / 2 );
		next.setY( 100 + next.getHeight( ) );
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
