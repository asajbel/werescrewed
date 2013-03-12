package com.blindtigergames.werescrewed.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class MetricsRender {
	private enum type {
		JUMP, DIE, SCREW, UNSCREW, TIME
	}

	private class Place {
		float x;
		float y;
		float r;
		float g;
		float b;
		float a;
	}

	private boolean fileExists = false;
	private boolean render = false;
	private ArrayList< MetricsOutput > runs;
	private ArrayList< Place > parsedJump;
	private ArrayList< Place > parsedDeath;
	private ArrayList< Place > parsedScrew;
	private ArrayList< Place > parsedUnscrew;
	private ArrayList< Place > parsedAttach;

	public MetricsRender( String LevelName ) {
		Json json = new Json( );
		File file = new File( LevelName + "_Metrics.json" );
		if ( file.exists( ) ) {
			fileExists = true;
			try {
				FileReader fileReader = new FileReader( file.getName( ) );
				BufferedReader read = new BufferedReader( fileReader );
				String line = null;
				while ( ( line = read.readLine( ) ) != null ) {
					MetricsOutput temp = json.fromJson( MetricsOutput.class,
							line );
					runs.add( temp );
				}
			} catch ( FileNotFoundException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace( );
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace( );
			}
		}
		if ( fileExists ) {
			parseJump( );
			parseDeath( );
			parseScrew( );
			parseUnscrew( );
			parseAttach( );
		}
	}

	private void parseJump( ) {
		parsedJump = new ArrayList< Place >( );
		float startX, startY, endX, endY;
		for ( MetricsOutput out : runs ) {
			for ( Vector2 pos : out.jump ) {
				startX = pos.x;
				startY = pos.y;
				endX = round( startX );
				endY = round( startY );
				Place p = new Place( );
				p.x = endX;
				p.y = endY;
				p.r = 1.0f;
				p.g = 0.0f;
				p.b = 0.0f;
				p.a = 0.5f;
				parsedJump.add( p );
			}
		}

	}

	private void parseDeath( ) {

	}

	private void parseScrew( ) {

	}

	private void parseUnscrew( ) {

	}

	private void parseAttach( ) {

	}

	private float round( float start ) {
		float end = 0;
		end = start * Util.PIXEL_TO_BOX;
		end = Math.round( end ); 
		return end;
	}

	public void render( OrthographicCamera camera ) {

	}
}
