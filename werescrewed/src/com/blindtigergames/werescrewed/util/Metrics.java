package com.blindtigergames.werescrewed.util;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Metrics{
	
	public static boolean turnOnMetrics = false;
	private static ArrayList<Vector2> playerDeathPositions = new ArrayList<Vector2>();
	private static ArrayList<Vector2> playerJumpPositions = new ArrayList<Vector2>();
	private static ArrayList<Vector2> playerAttachToScrewPositions = new ArrayList<Vector2>();
	private static ArrayList<Vector2> playerUnscrewedPositions = new ArrayList<Vector2>();
	private static ArrayList<Float> playerTime = new ArrayList<Float>();

	public static void addPlayerDeathPosition(Vector2 position){
		Gdx.app.log( "player died ", ""+ position );
		playerDeathPositions.add( position );
	}
	public static void addPlayerJumpPosition(Vector2 position){
		Gdx.app.log( "player jump ", ""+ position );
		playerJumpPositions.add( position );
	}
	public static void addPlayerAttachToScrewPosition(Vector2 position){
		Gdx.app.log( "player attached to screw ", ""+ position );
		playerAttachToScrewPositions.add(position);
	}
	
	public static void addPlayerBeginTime(float time){
		Gdx.app.log( "begin time ", ""+ time );
		playerTime.add( time );
	}
	public static void addPlayerEndTime(float time){
		Gdx.app.log( "end time ", ""+ time );
		playerTime.add( time );
	}
}