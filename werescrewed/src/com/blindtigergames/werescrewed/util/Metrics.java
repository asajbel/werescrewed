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

	/**
	 * Adds position data to the list of player deaths. Should be called when a
	 * player dies.
	 * 
	 * @param position
	 *            The pixel position at which the player dies
	 */
	public static void addPlayerDeathPosition( Vector2 position ) {
		Gdx.app.log( "player died ", "" + position );
		playerDeathPositions.add( position );
	}

	/**
	 * Adds position data to the list of player jump positions. Should be called
	 * when a player jumps.
	 * 
	 * @param position
	 *            The pixel position where the player jumps
	 */
	public static void addPlayerJumpPosition( Vector2 position ) {
		Gdx.app.log( "player jump ", "" + position );
		playerJumpPositions.add( position );
	}

	/**
	 * Adds position data to the list of player attach to screw positions.
	 * Should be called when a player attaches to a screw.
	 * 
	 * @param position
	 *            The pixel position where the player attaches to a screw.
	 */
	public static void addPlayerAttachToScrewPosition( Vector2 position ) {
		Gdx.app.log( "player attached to screw ", "" + position );
		playerAttachToScrewPositions.add( position );
	}

	/**
	 * Adds position data to the list of player unscrewed a screw positions.
	 * Should be called when a player unscrews a screw or activates a puzzle
	 * screw in the unscrewed direction.
	 * 
	 * @param position
	 *            The pixel position where the player unscrews or activates a
	 *            screw.
	 */
	public static void addPlayerUnscrewedScrewPosition( Vector2 position ) {
		Gdx.app.log( "player unscrewed screw ", "" + position );
		playerUnscrewedPositions.add( position );
	}

	/**
	 * Adds position data to the list of player screws in a screw positions.
	 * Should be called when a player screws in a screw or activates a puzzle
	 * screw in the screw direction.
	 * 
	 * @param position
	 *            The pixel position where the player screws in or activates a
	 *            screw.
	 */
	public static void addPlayerScrewedScrewPosition( Vector2 position ) {
		Gdx.app.log( "player screwed screw ", "" + position );
		playerScrewedPositions.add( position );
	}

	/**
	 * Adds start time data to the list of time the players spend in a section
	 * of a level. Should be called when a player enters a section using an
	 * event trigger passed MetricsStartTimeAction( String Name ).
	 * 
	 * @param position
	 *            The time when the player starts the section.
	 */
	public static void addPlayerBeginTime( float time ) {
		Gdx.app.log( "begin time ", "" + time );
		playerTime.add( time );
	}

	/**
	 * Adds end time data to the list of time the players spend in a section of
	 * a level. Should be called when a player enters a section using an event
	 * trigger passed MetricsEndTimeAction( ).
	 * 
	 * @param position
	 *            The time when the player ends the section.
	 */
	public static void addPlayerEndTime( float time ) {
		Gdx.app.log( "end time ", "" + time );
		playerTime.add( time );
	}

	/**
	 * Adds a name to the list of section names. Should be called when a player
	 * enters a section using an event trigger passed MetricsStartTimeAction(
	 * String Name ).
	 * 
	 * @param name
	 *            The name of the section.
	 */
	public static void addSectionName( String name ) {
		Gdx.app.log( "section name ", name );
		sectionNames.add( name );
	}

	/**
	 * Prints the metrics to a json file saved in the root of
	 * werescrewed-desktop. Should be called at the end of a level.
	 * 
	 * @param levelName
	 *            The name of the level from which the metrics come from. Should
	 *            be the name of the file that the level is built from if
	 *            available.
	 */
	public static void printMetrics( String levelName ) {
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
			File file = new File( levelName + "_Metrics.json" );
			if ( !file.exists( ) ) {
				file.createNewFile( );
			}
			FileWriter fileWritter = new FileWriter( file.getName( ), true );
			BufferedWriter bufferWritter = new BufferedWriter( fileWritter );
			bufferWritter.write( json.toJson( putout )
					+ System.getProperty( "line.separator" ) );
			bufferWritter.close( );
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace( );
		}

	}
}