package com.blindtigergames.werescrewed.util;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Metrics{
	
	private static ArrayList<Vector2> playerDeathPositions;
	private static ArrayList<Vector2> playerJumpPositions;
	private static ArrayList<Vector2> playerAttachToScrewPositions;
	private static ArrayList<Vector2> playerUnscrewedPositions;
	// Every pair is a start time and end time
	private static ArrayList<Float> playerTime;
	//time spent in areas
	public Metrics(){
		playerDeathPositions = new ArrayList<Vector2>();
		playerJumpPositions = new ArrayList<Vector2>();
		playerAttachToScrewPositions = new ArrayList<Vector2>();
		playerUnscrewedPositions = new ArrayList<Vector2>();
		
		playerTime = new ArrayList<Float>();
	}
	public static void addPlayerDeathPosition(Vector2 position){
		Gdx.app.log( "player died ", ""+ position );
		playerDeathPositions.add( position );
	}
	public static void addPlayerJumpPosition(Vector2 position){
		playerJumpPositions.add( position );
	}
	public static void addPlayerAttachToScrewPosition(Vector2 position){
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