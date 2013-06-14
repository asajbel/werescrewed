package com.blindtigergames.werescrewed.screens;

import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.tween.LabelAccessor;
import com.blindtigergames.werescrewed.entity.tween.SpriteAccessor;
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
	private BitmapFont smallerFont;
	private int trophyLength = 5;
	private int lineHeight = 0;
	private int trophyMax = 30; // Current number of possible trophies
	private float offSet = 256;
	private float outTime = 0f;
	private ScreenType screenTag = null;
	private Label[ ] player1 = new Label[ trophyLength ];
	private Label[ ] player1Desc = new Label[ trophyLength ];
	private Label[ ] player2 = new Label[ trophyLength ];
	private Label[ ] player2Desc = new Label[ trophyLength ];
	//private TextureRegion[ ] trophyIcon = new TextureRegion[ trophyMax ]; // Holds ALL trophy icons
	private Sprite[ ] trophies1 = new Sprite[ trophyLength ]; // trophy images
																// that go next
																// to player
																// label
	private Sprite[ ] trophies2 = new Sprite[ trophyLength ];
	private int[] trophyIndices = new int[ 2 * trophyLength ];
	private Label player1Name = null;
	private Label player2Name = null;
	private TextButton next = null;
	private TextureAtlas texAtlas;
	private TextureAtlas bgAtlas;
	private Sprite bg;
	private Sprite bgFront;
	
	private TweenManager tweenManager;

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
		smallerFont = WereScrewedGame.manager.getFont( "longdon" );
		fancyFont.setScale( 1.0f );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		player1Name = new Label( "Player 1", fancyFont );
		player2Name = new Label( "Player 2", fancyFont );

		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
		
		// Button classes now require you to send in a texture for the button image.
		TextureRegion buttonTex = WereScrewedGame.manager.
				getAtlas( "menu-textures" ).findRegion( "button" );
		
		next = new TextButton( "Next Level", fancyFont, buttonTex, 
				new ScreenSwitchHandler( screenTag ) );
		next.setColored( true );

		texAtlas = WereScrewedGame.manager.loadAtlas( WereScrewedGame.dirHandle + "/common/trophies/trophyAtlas.pack" );
		bgAtlas = WereScrewedGame.manager.loadAtlas( WereScrewedGame.dirHandle + "/common/trophies/bg.pack" );
		
		bg = bgAtlas.createSprite("bg");
		bg.setPosition( 0, 0 );
		bgFront = bgAtlas.createSprite("front");
		bg.setPosition( 0, 0 );
		
		
		Tween.registerAccessor( Sprite.class, new SpriteAccessor( ) );
		Tween.registerAccessor( Label.class, new LabelAccessor( ) );
		tweenManager = new TweenManager( );
		
		emptyTrophies( );
		addTrophies( );
		Metrics.resetTrophyMetric( );
		
		WereScrewedGame.manager.load(
				"data/common/slides/slide1_dragonOut.png", Texture.class );
		
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
			player1Desc[k]=null;
			player2Desc[k]=null;
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
//		smallerFont = WereScrewedGame.manager.getFont( "longdon-small" );
		for (int i = 0; i < trophyIndices.length ; i++){
			if ( trophyIndices[i] == trophyNum ) return;
		}
		
		Label[] player = (playerNum==1)?player1:player2;
		Label[] playerDesc = (playerNum==1)?player1Desc:player1Desc;
		
		switch ( trophyNum ) {
		case 1: // Longest Running Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1RUNDIST ) > Metrics
							.getTrophyMetric( TrophyMetric.P2RUNDIST ) ){
				player1[ index ] = new Label( "Marathon Runner", fancyFont );
				player1Desc[index] = new Label("Longest running time", smallerFont);
				trophies1[ index ] =  texAtlas.createSprite("trophy1");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2RUNDIST ) > Metrics
							.getTrophyMetric( TrophyMetric.P1RUNDIST ) ){
				player2[ index ] = new Label( "Marathon Runner", fancyFont );
				player2Desc[index] = new Label("Longest running time", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy1");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 2: // Most Struct Screws Unscrewed
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1UNSCREWED ) > Metrics
							.getTrophyMetric( TrophyMetric.P2UNSCREWED ) ){
				player1[ index ] = new Label( "You Got A Screw Loose", fancyFont );
				player1Desc[index] = new Label("Most structure screws unscrewed", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy2");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2UNSCREWED ) > Metrics
							.getTrophyMetric( TrophyMetric.P1UNSCREWED ) ){
				player2[ index ] = new Label( "You Got A Screw Loose", fancyFont );
				player2Desc[index] = new Label("Most structure screws unscrewed", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy2");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 3: // Longest Air Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1AIRTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2AIRTIME ) ){
				player1[ index ] = new Label( "Hang Time", fancyFont );
				player1Desc[index] = new Label("Longest air time", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy3");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2AIRTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1AIRTIME ) ){
				player2[ index ] = new Label( "Hang Time", fancyFont );
				player2Desc[index] = new Label("Longest air time", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy3");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 4: // Most Fall Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1FALLDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2FALLDEATHS ) ){
				player1[ index ] = new Label( "Fall Guy", fancyFont );
				player1Desc[index] = new Label("Most fall deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy4");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2FALLDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1FALLDEATHS ) ){
				player2[ index ] = new Label( "Fall Guy", fancyFont );
				player2Desc[index] = new Label("Most fall deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy4");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 5: // Most Time Spent On Puzzle Screws
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1PUZZLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2PUZZLETIME ) ){
				player1[ index ] = new Label( "Inventor's Apprentice", fancyFont );
				player1Desc[index] = new Label("Most time on puzzle screws", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy5");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2PUZZLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1PUZZLETIME ) ){
				player2[ index ] = new Label( "Inventor's Apprentice", fancyFont );
				player2Desc[index] = new Label("Most time on puzzle screws", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy5");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 6: // Most Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2DEATHS ) ){
				player1[ index ] = new Label( "Call The Suicide \n Hotline", fancyFont );
				player1Desc[index] = new Label("Most deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy6");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1DEATHS ) ){
				player2[ index ] = new Label( "Call The Suicide \n Hotline", fancyFont );
				player2Desc[index] = new Label("Most deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy6");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 7: // Most Head Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1HEADSTANDS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2HEADSTANDS ) ){
				player1[ index ] = new Label( "Always On Top", fancyFont );
				player1Desc[index] = new Label("Most head jumps", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy7");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2HEADSTANDS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1HEADSTANDS ) ){
				player2[ index ] = new Label( "Always On Top", fancyFont );
				player2Desc[index] = new Label("Most head jumps", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy7");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 8: // Most Revives
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1REVIVES ) > Metrics
							.getTrophyMetric( TrophyMetric.P2REVIVES ) ){
				player1[ index ] = new Label( "I Help Dead People", fancyFont );
				player1Desc[index] = new Label("Most revives", smallerFont);
				Texture icon = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
						+ "/common/trophies/trophy.png", Texture.class );
				trophies1[ index ] = new Sprite ( icon );
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2REVIVES ) > Metrics
							.getTrophyMetric( TrophyMetric.P1REVIVES ) ){
				player2[ index ] = new Label( "I Help Dead People", fancyFont );
				player2Desc[index] = new Label("Most revives", smallerFont);

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
				player1Desc[index] = new Label("Most deaths while partner was dead", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy9");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2TEAMDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1TEAMDEATHS ) ){
				player2[ index ] = new Label( "Well, He Jumped First", fancyFont );
				player2Desc[index] = new Label("Most deaths while partner was dead", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy9");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 10: // Longest Idle Time
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1IDLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2IDLETIME ) ){
				player1[ index ] = new Label( "I'm Waaaaiting!!", fancyFont );
				player1Desc[index] = new Label("Most idle time", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy10");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2IDLETIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1IDLETIME ) ){
				player2[ index ] = new Label( "I'm Waaaaiting!!", fancyFont );
				player2Desc[index] = new Label("Most idle time", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy10");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 11: // No Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEATHS ) == 0 ){
				player1[ index ] = new Label( "Are You Using \n God Mode?", fancyFont );
				player1Desc[index] = new Label("No deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy11");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEATHS ) == 0 ){
				player2[ index ] = new Label( "Are You Using \n God Mode?", fancyFont );
				player2Desc[index] = new Label("No deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy11");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 12: // Most Crush Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1CRUSHDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2CRUSHDEATHS ) ){
				player1[ index ] = new Label( "Crushing Defeat", fancyFont );
				player1Desc[index] = new Label("Most crush deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy12");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2CRUSHDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1CRUSHDEATHS ) ){
				player2[ index ] = new Label( "Crushing Defeat", fancyFont );
				player2Desc[index] = new Label("Most crush deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy12");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 13: // Most Electrocution Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1ELECDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2ELECDEATHS ) ){
				player1[ index ] = new Label( "A Shocking Revelation", fancyFont );
				player1Desc[index] = new Label("Most electricity deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy13");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2ELECDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1ELECDEATHS ) ){
				player2[ index ] = new Label( "A Shocking Revelation", fancyFont );
				player2Desc[index] = new Label("Most electricity deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy13");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 14: // Longest Time Grounded
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1GROUNDTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2GROUNDTIME ) ){
				player1[ index ] = new Label( "Landlubber", fancyFont );
				player1Desc[index] = new Label("Longest time grounded", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy14");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2GROUNDTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1GROUNDTIME ) ){
				player2[ index ] = new Label( "Landlubber", fancyFont );
				player2Desc[index] = new Label("Longest time grounded", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy14");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 15: // Most Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1JUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2JUMPS ) ){
				player1[ index ] = new Label( "Jumpin' Jack", fancyFont );
				player1Desc[index] = new Label("Most jumps", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy15");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2JUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1JUMPS ) ){
				player2[ index ] = new Label( "Jumpin' Jack", fancyFont );
				player2Desc[index] = new Label("Most jumps", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy15");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 16: // Most Spike Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1SPIKEDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2SPIKEDEATHS ) ){
				player1[ index ] = new Label( "Vlad the Impaled", fancyFont );
				player1Desc[index] = new Label("Most spike deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy16");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2SPIKEDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1SPIKEDEATHS ) ){
				player2[ index ] = new Label( "Vlad the Impaled", fancyFont );
				player2Desc[index] = new Label("Most spike deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy16");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 17: // Most Fire Deaths
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1FIREDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2FIREDEATHS ) ){
				player1[ index ] = new Label( "Hot And Bothered", fancyFont );
				player1Desc[index] = new Label("Most fire deaths", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy17");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2FIREDEATHS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1FIREDEATHS ) ){
				player2[ index ] = new Label( "Hot And Bothered", fancyFont );
				player2Desc[index] = new Label("Most fire deaths", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy17");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 18: // Longest Time Dead
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1DEADTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P2DEADTIME ) ){
				player1[ index ] = new Label( "Ghostly Gamer", fancyFont );
				player1Desc[index] = new Label("Longest time dead", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy18");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2DEADTIME ) > Metrics
							.getTrophyMetric( TrophyMetric.P1DEADTIME ) ){
				player2[ index ] = new Label( "Ghostly Gamer", fancyFont );
				player2Desc[index] = new Label("Longest time dead", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy18");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 19: // Most Steam Jumps
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1STEAMJUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P2STEAMJUMPS ) ){
				player1[ index ] = new Label( "Steam Powered", fancyFont );
				player1Desc[index] = new Label("Most steam jumps", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy19");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2STEAMJUMPS ) > Metrics
							.getTrophyMetric( TrophyMetric.P1STEAMJUMPS ) ){
				player2[ index ] = new Label( "Steam Powered", fancyFont );
				player2Desc[index] = new Label("Most steam jumps", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy19");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 20: // Most Strip Screws Attached To
			if ( playerNum == 1
					&& Metrics.getTrophyMetric( TrophyMetric.P1STRIPATTACH ) > Metrics
							.getTrophyMetric( TrophyMetric.P2STRIPATTACH ) ){
				player1[ index ] = new Label( "Crafty Climber", fancyFont );
				player1Desc[index] = new Label("Most stripped screws attached to", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy20");
				trophyIndices[ index ] = trophyNum;
			}
			else if ( playerNum == 2
					&& Metrics.getTrophyMetric( TrophyMetric.P2STRIPATTACH ) > Metrics
							.getTrophyMetric( TrophyMetric.P1STRIPATTACH ) ){
				player2[ index ] = new Label( "Crafty Climber", fancyFont );
				player2Desc[index] = new Label("Most stripped screws attached to", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy20");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 21: // Random 1
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Best 'Stache", fancyFont );
				player1Desc[index] = new Label("Most luxurious bristles", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy21a");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Best Dressed", fancyFont );
				player2Desc[index] = new Label("Most lacey doodads", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy21b");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 22: // Random 2
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "My Screwdriver's Bigger", fancyFont );
				player1Desc[index] = new Label("Most well-equipped", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy22");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "My Screwdriver's Bigger", fancyFont );
				player2Desc[index] = new Label("Most well-equipped", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy22");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 23: // Random 3
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "You Unlocked \n An Achievement!!", fancyFont );
				player1Desc[index] = new Label("Highest gamerscore", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy23");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "You Unlocked \n An Achievement!!", fancyFont );
				player2Desc[index] = new Label("Highest gamerscore", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy23");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 24: // Random 4
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Most Popular", fancyFont );
				player1Desc[index] = new Label("Largest fanbase", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy24");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Most Popular", fancyFont );
				player2Desc[index] = new Label("Largest fanbase", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy24");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 25: // Random 5
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Gives Awesome Hugs", fancyFont );
				player1Desc[index] = new Label("Most cuddly", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy25");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Gives Awesome Hugs", fancyFont );
				player2Desc[index] = new Label("Most cuddly", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy25");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 26: // Random 6
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Better Than \n Bacon Ice Cream", fancyFont );
				player1Desc[index] = new Label("Most delicious", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy26");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Better Than \n Bacon Ice Cream", fancyFont );
				player2Desc[index] = new Label("Most delicious", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy26");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 27: // Random 7
			if ( playerNum == 1 ) {
				player1[ index ] = new Label(
						"You Played \n The Best Game Ever!!", fancyFont );
				player1Desc[index] = new Label("Most spectacular gameplay", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy27");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label(
						"You Played \n The Best Game Ever!!", fancyFont );
				player2Desc[index] = new Label("Most spectacular gameplay", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy27");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 28: // Random 8
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Free Cookie", fancyFont );
				player1Desc[index] = new Label("Most chocolatey", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy28");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Free Cookie", fancyFont );
				player2Desc[index] = new Label("Most chocolatey", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy28");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 29: // Random 9
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Supremely Screwy", fancyFont );
				player1Desc[index] = new Label("Most threaded", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy29");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Supremely Screwy", fancyFont );
				player2Desc[index] = new Label("Most threaded", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy29");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		case 30: // Random 10
			if ( playerNum == 1 ) {
				player1[ index ] = new Label( "Antidisestablishmentarianist", fancyFont );
				player1Desc[index] = new Label("Best vocabulary", smallerFont);
				trophies1[ index ] = texAtlas.createSprite("trophy30");
				trophyIndices[ index ] = trophyNum;
			} else if ( playerNum == 2 ) {
				player2[ index ] = new Label( "Antidisestablishmentarianist", fancyFont );
				player2Desc[index] = new Label("Best vocabulary", smallerFont);
				trophies2[ index ] = texAtlas.createSprite("trophy30");
				trophyIndices[ trophyLength + index ] = trophyNum;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		tweenManager.update( delta );
		batch.begin( );
		fancyFont.setScale( 1.0f );
		bg.draw( batch );
		player1Name.draw( batch );
		player2Name.draw( batch );
		smallerFont.setScale( 0.5f );
		for ( int i = 0; i < trophyLength; i++ ) {
			player1[ i ].draw( batch );
			trophies1[ i ].draw( batch );
			if( player1Desc[ i ] != null ){
				player1Desc[ i ].draw( batch );
			}
			player2[ i ].draw( batch );
			trophies2[ i ].draw( batch );
			if( player2Desc[ i ] != null ){
				player2Desc[ i ].draw( batch );
			}
		}		
		bgFront.draw( batch );
		next.draw( batch, camera );

		if ( !transInEnd ) {
			drawTransIn( batch );
		}
		
		if ( !transOutEnd ) {
			drawTransOut( batch, screenTag, delta );
		}
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.ENTER ) && transOutEnd ) {
			//ScreenManager.getInstance( ).show( screenTag );
			transOutEnd = false;
		}
		if ( WereScrewedGame.p1Controller != null && transOutEnd ) {
			if ( WereScrewedGame.p1ControllerListener.jumpPressed( )
					|| WereScrewedGame.p1ControllerListener
							.pausePressed( ) ) {
				transOutEnd = false;
			}
		}
		if ( WereScrewedGame.p2Controller != null && transOutEnd ) {
			if ( WereScrewedGame.p2ControllerListener.jumpPressed( )
					|| WereScrewedGame.p2ControllerListener
							.pausePressed( ) ) {
				transOutEnd = false;
			}
		}
	}

	@Override
	public void resize( int _width, int _height ) {
		//Camera setup
		super.resize( _width, _height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		
		int centerX = width / 2;
		int centerY = height / 2;
		
		//Tween constants
		float delay = 0.25f;
		int offScreenY = -100;
		
		player1Name.setX( 0 + ( int ) offSet );
		player1Name.setY( offScreenY );
		Tween.to( player1Name, LabelAccessor.POSITION_Y, 1 )
		.target( centerY / 2 + lineHeight * 3 * ( trophyLength + 1 ) - 64 )
		.start( tweenManager );
		player2Name.setX( centerX + ( int ) offSet );
		player2Name.setY( offScreenY );
		Tween.to( player2Name, LabelAccessor.POSITION_Y, 1 )
		.target( centerY / 2 + lineHeight * 3 * ( trophyLength + 1 ) - 64 )
		.start( tweenManager );
		for ( int j = 0; j < trophyLength; j++ ) {
			player1[ j ].setX( 0 + ( int ) offSet );
			player1[ j ].setY( offScreenY );
			Tween.to( player1[ j ], LabelAccessor.POSITION_Y, 1 )
			.target( centerY / 2 + lineHeight * 3 * ( j + 1 ) - 64 ).delay( delay*j )
			.start( tweenManager );
			
			trophies1[ j ].setX(  184  );
			trophies1[ j ].setY( offScreenY  );
			Tween.to( trophies1[ j ], SpriteAccessor.POSITION_Y, 1 )
			.target( centerY / 2 + lineHeight * 3 * ( j ) + trophies1[ j ].getHeight( ) / 2 - 64 ).delay( delay*j )
			.start( tweenManager );
			
			if (player1Desc[ j ] != null){
				player1Desc[ j ].setX( 0 + ( int ) offSet );
				player1Desc[ j ].setY( offScreenY );
				Tween.to( player1Desc[ j ], LabelAccessor.POSITION_Y, 1 )
				.target( centerY / 2 + lineHeight * 3 * ( j + 1 ) - player1[ j ].getHeight( ) / 2 - 64 ).delay( delay*j )
				.start( tweenManager );
			}
			
			
			player2[ j ].setX( centerX + ( int ) offSet );
			player2[ j ].setY( offScreenY );
			Tween.to( player2[ j ], LabelAccessor.POSITION_Y, 1 )
			.target( centerY / 2 + lineHeight * 3 * ( j + 1 ) - 64 ).delay( delay*j )
			.start( tweenManager );
			
			trophies2[ j ].setX( centerX + 184);
			trophies2[ j ].setY( offScreenY  );
			Tween.to( trophies2[ j ], SpriteAccessor.POSITION_Y, 1 )
			.target( centerY / 2 + lineHeight * 3 * ( j ) + trophies2[ j ].getHeight( ) / 2 - 64 ).delay( delay*j )
			.start( tweenManager );
			
			if (player2Desc[ j ] != null){
				player2Desc[ j ].setX( centerX + ( int ) offSet );
				player2Desc[ j ].setY( offScreenY );
				Tween.to( player2Desc[ j ], LabelAccessor.POSITION_Y, 1 )
				.target( centerY / 2 + lineHeight * 3 * ( j + 1 ) - player2[ j ].getHeight( ) / 2 - 64 ).delay( delay*j )
				.start( tweenManager );
			}
		}
		next.setX( centerX - next.getWidth( ) / 2 );
		next.setY( 125 + next.getHeight( ) );
	}

	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub
		super.dispose();
		texAtlas.dispose();

	}
	
	@Override
	public void load(){
		super.load();		
		if (bgm == null){
			bgm = Gdx.audio.newMusic( Gdx.files.internal(WereScrewedGame.dirHandle + "/menu/trophy-credits.mp3") );
		}
	}
	
	protected void drawTransOut ( SpriteBatch batch, ScreenType screen, float deltaTime ) {
		scale += SCALE_ADJUST;
		trans.setOrigin( trans.getWidth( ) / 2, trans.getHeight( ) / 2 );
		trans.rotate( 5.0f );
		trans.setScale( scale );
		trans.setPosition( width / 2 - trans.getWidth( ) / 2, 
				height / 2 - trans.getHeight( ) / 2 );
		trans.draw( batch );
		if ( scale > SCALE_MAX ) {
			if(screenTag == ScreenType.CREDITS){
				int posX = width / 2
						- WereScrewedGame.manager.get(
								"data/common/slides/slide1_dragonOut.png", Texture.class ).getWidth( ) / 2;
				int posY = height / 2
						- WereScrewedGame.manager.get(
								"data/common/slides/slide1_dragonOut.png", Texture.class ).getHeight( ) / 2;
				batch.draw( WereScrewedGame.manager.get(
						"data/common/slides/slide1_dragonOut.png", Texture.class ), posX, posY );
				outTime += deltaTime;
				//scale = SCALE_MAX;
				if(outTime > 5f) ScreenManager.getInstance( ).show( screen );
			} 
			else ScreenManager.getInstance( ).show( screen );
		}
	}
}
