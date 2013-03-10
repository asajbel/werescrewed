package com.blindtigergames.werescrewed.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class Metrics {

	public static boolean activated = false;
	public static boolean addToUnscrewListOnce = false;
	private static ArrayList< String > sectionNames = new ArrayList< String >( );
	private static ArrayList< Vector2 > playerDeathPositions = new ArrayList< Vector2 >( );
	private static ArrayList< Vector2 > playerJumpPositions = new ArrayList< Vector2 >( );
	private static ArrayList< Vector2 > playerAttachToScrewPositions = new ArrayList< Vector2 >( );
	private static ArrayList< Vector2 > playerUnscrewedPositions = new ArrayList< Vector2 >( );
	private static ArrayList< Vector2 > playerScrewedPositions = new ArrayList< Vector2 >( );
	private static ArrayList< Float > playerTime = new ArrayList< Float >( );

	public static void addPlayerDeathPosition( Vector2 position ) {
		Gdx.app.log( "player died ", "" + position );
		playerDeathPositions.add( position );
	}

	public static void addPlayerJumpPosition( Vector2 position ) {
		Gdx.app.log( "player jump ", "" + position );
		playerJumpPositions.add( position );
	}

	public static void addPlayerAttachToScrewPosition( Vector2 position ) {
		Gdx.app.log( "player attached to screw ", "" + position );
		playerAttachToScrewPositions.add( position );
	}

	public static void addPlayerUnscrewedScrewPosition( Vector2 position ) {
		Gdx.app.log( "player unscrewed screw ", "" + position );
		playerUnscrewedPositions.add( position );
	}

	public static void addPlayerScrewedScrewPosition( Vector2 position ) {
		Gdx.app.log( "player screwed screw ", "" + position );
		playerScrewedPositions.add( position );
	}

	public static void addPlayerBeginTime( float time ) {
		Gdx.app.log( "begin time ", "" + time );
		playerTime.add( time );
	}

	public static void addPlayerEndTime( float time ) {
		Gdx.app.log( "end time ", "" + time );
		playerTime.add( time );
	}

	public static void addSectionName( String name ) {
		Gdx.app.log( "section name ", name );
		sectionNames.add( name );
	}

	public static void printMetrics( ) {
		MetricsOutput putout = new MetricsOutput( );
		putout.attach = playerAttachToScrewPositions;
		putout.death = playerDeathPositions;
		putout.jump = playerJumpPositions;
		putout.screwed = playerScrewedPositions;
		putout.time = playerTime;
		putout.unscrewed = playerUnscrewedPositions;
		putout.names = sectionNames;

		Json json = new Json( );
		try {
			File file = new File( "metrics.json" );
			if ( !file.exists( ) ) {
				file.createNewFile( );
			}
			FileWriter fileWritter = new FileWriter( file.getName( ), true );
			BufferedWriter bufferWritter = new BufferedWriter( fileWritter );
			bufferWritter.write( json.toJson( putout ) + System.getProperty( "line.separator" ));
			bufferWritter.close( );
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace( );
		}

	}
}