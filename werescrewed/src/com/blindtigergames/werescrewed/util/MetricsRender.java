package com.blindtigergames.werescrewed.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.blindtigergames.werescrewed.WereScrewedGame;

public class MetricsRender {
	private enum Type {
		NONE, JUMP, DIE, SCREW, UNSCREW, ATTACH, TIME
	}

	private class Place {
		float x;
		float y;
		Vector3 color = new Vector3( );
		int num;
	}

	private final int LIMIT = 5; 
	private final float RADIUS = 64;
	private boolean fileExists = false;
	private boolean render = false;
	private boolean cycleForward = true;
	private boolean cycleBackward = true;
	private String mode;
	private BitmapFont fancyFont = null;
	private ArrayList< MetricsOutput > runs;
	private Map< String, Place > parsedJump;
	private Map< String, Place > parsedDeath;
	private Map< String, Place > parsedScrew;
	private Map< String, Place > parsedUnscrew;
	private Map< String, Place > parsedAttach;
	private ShapeRenderer shapeRenderer;
	private Type whatToRender;
	private float alpha;

	public MetricsRender( String LevelName ) {
		whatToRender = Type.NONE;
		alpha = 0.5f;
		shapeRenderer = new ShapeRenderer( );
		runs = new ArrayList< MetricsOutput >( );
		fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		File file = new File( LevelName + Metrics.FILE_APPEND );
		Json json = new Json( );
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
				read.close( );
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
			heatmap(parsedJump);
			heatmap(parsedDeath);
			heatmap(parsedScrew);
			heatmap(parsedUnscrew);
			heatmap(parsedAttach); 
		}
	}

	public void render( OrthographicCamera camera ) {
		if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT_BRACKET ) ) {
			if ( cycleForward ) {
				cycleRenderForward( );
			}
			cycleForward = false;
		} else
			cycleForward = true;

		if ( Gdx.input.isKeyPressed( Input.Keys.LEFT_BRACKET ) ) {
			if ( cycleBackward ) {
				cycleRenderBackward( );
			}
			cycleBackward = false;
		} else
			cycleBackward = true;

		Map< String, Place > parsed;
		switch ( whatToRender ) {
		case ATTACH:
			parsed = parsedAttach;
			mode = "Attaching";
			break;
		case DIE:
			parsed = parsedDeath;
			mode = "Dieing";
			break;
		case JUMP:
			parsed = parsedJump;
			mode = "Jumping";
			break;
		case NONE:
			parsed = new HashMap< String, Place >( );
			mode = "";
			break;
		case SCREW:
			parsed = parsedScrew;
			mode = "Screwings";
			break;
		case TIME:
			parsed = new HashMap< String, Place >( );
			mode = "Time Spent in Sections";
			break;
		case UNSCREW:
			parsed = parsedUnscrew;
			mode = "Unscrewings";
			break;
		default:
			parsed = new HashMap< String, Place >( );
			mode = "";
			break;

		}

		if ( render && fileExists ) {
//			fancyFont.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
//			fancyFont.draw( batch, mode, camera.position.x, camera.position.y );

			Gdx.gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
			Gdx.gl.glEnable( GL10.GL_BLEND );
			
			for ( Map.Entry< String, Place > rend : parsed.entrySet( ) ) {
				shapeRenderer.setProjectionMatrix( camera.combined );
				shapeRenderer.begin( ShapeType.FilledCircle );
				shapeRenderer.setColor( rend.getValue( ).color.x,
						rend.getValue( ).color.y, rend.getValue( ).color.z,
						alpha );
				shapeRenderer.filledCircle( rend.getValue( ).x,
						rend.getValue( ).y, RADIUS );
				shapeRenderer.end( );
			}

			Gdx.gl.glDisable( GL10.GL_BLEND );
		}
	}

	private void cycleRenderForward( ) {
		switch ( whatToRender ) {
		case NONE:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Jumping" );
			whatToRender = Type.JUMP;
			break;
		case JUMP:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Dieing" );
			whatToRender = Type.DIE;
			break;
		case DIE:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Screwing" );
			whatToRender = Type.SCREW;
			break;
		case SCREW:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Unscrewing" );
			whatToRender = Type.UNSCREW;
			break;
		case UNSCREW:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Attaching" );
			whatToRender = Type.ATTACH;
			break;
		case ATTACH:
			render = false;
			Gdx.app.log( "Metric Shown: ", "Nothing" );
			whatToRender = Type.NONE;
			break;
		case TIME:
			render = false;
			break;
		default:
			break;

		}
	}

	private void cycleRenderBackward( ) {
		switch ( whatToRender ) {
		case NONE:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Attaching" );
			whatToRender = Type.ATTACH;
			break;
		case JUMP:
			render = false;
			Gdx.app.log( "Metric Shown: ", "Nothing" );
			whatToRender = Type.NONE;
			break;
		case DIE:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Jumping" );
			whatToRender = Type.JUMP;
			break;
		case SCREW:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Dieing" );
			whatToRender = Type.DIE;
			break;
		case UNSCREW:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Screwing" );
			whatToRender = Type.SCREW;
			break;
		case ATTACH:
			render = true;
			Gdx.app.log( "Metric Shown: ", "Unscrewing" );
			whatToRender = Type.UNSCREW;
			break;
		case TIME:
			render = false;
			break;
		default:
			break;

		}
	}

	// DON'T REMOVE -Anders
	// Trying to figure out better way of writing this code
	// private void parseMap( Map< String, Place > parsed ) {
	// parsed = new HashMap< String, Place >( );
	// for ( MetricsOutput out : runs ) {
	// parse( parsed, out.jump );
	// }
	// }
	//
	// private void parse( Map< String, Place > parsed, ArrayList< Vector2 >
	// parser ) {
	// float startX, startY, endX, endY;
	// for ( Vector2 pos : parser ) {
	// startX = pos.x;
	// startY = pos.y;
	// endX = round( startX );
	// endY = round( startY );
	// String key = endX + ", " + endY;
	// if ( parsed.containsKey( key ) ) {
	// parsed.get( key ).num++;
	// } else {
	// Place p = new Place( );
	// p.x = endX;
	// p.y = endY;
	// p.r = 0.0f;
	// p.g = 0.0f;
	// p.b = 1.0f;
	// p.a = 0.5f;
	// p.num = 1;
	// parsed.put( key, p );
	// }
	// }
	// }

	private void addToMap( Vector2 pos, Map< String, Place > parsed ) {
		float startX, startY, endX, endY;
		startX = pos.x;
		startY = pos.y;
		endX = round( startX );
		endY = round( startY );
		String key = endX + ", " + endY;
		if ( parsed.containsKey( key ) ) {
			parsed.get( key ).num++;
		} else {
			Place p = new Place( );
			p.x = endX;
			p.y = endY;
			p.color.x = 0.0f;
			p.color.y = 0.0f;
			p.color.z = 1.0f;
			p.num = 1;
			parsed.put( key, p );
		}
	}
	
	private void heatmap (Map< String, Place > parsed) {
		for ( Map.Entry< String, Place > heat : parsed.entrySet( ) ) {
			interpolateColor( heat.getValue( ) );
		}
	}

	private void interpolateColor( Place p ) {
		float runsTimes = runs.size( );
		float value = p.num / runsTimes;
		float t = Math.min( value / LIMIT, 1.0f); 
		Vector3 blue = new Vector3(0.0f, 0.0f, 1.0f);
		Vector3 green = new Vector3(0.0f, 1.0f, 0.0f);
		Vector3 red = new Vector3(1.0f, 0.0f, 0.0f); 
		
		Vector3 p1 = blue.mul( (1 - t) * (1 - t) );
		Vector3 p2 = green.mul( 2 * (1 - t) * t );
		Vector3 p3 = red.mul( t * t );
		Vector3 color = p1.add( p2.add( p3 ) ); 
		
		p.color = color; 
	}

	private float round( float start ) {
		float end = 0;
		end = start / 64;
		end = Math.round( end );
		end = end * 64;
		return end;
	}

	private void parseJump( ) {
		parsedJump = new HashMap< String, Place >( );
		for ( MetricsOutput out : runs ) {
			for ( Vector2 pos : out.jump ) {
				addToMap( pos, parsedJump );
			}
		}

	}

	private void parseDeath( ) {
		parsedDeath = new HashMap< String, Place >( );
		for ( MetricsOutput out : runs ) {
			for ( Vector2 pos : out.death ) {
				addToMap( pos, parsedDeath );
			}
		}
	}

	private void parseScrew( ) {
		parsedScrew = new HashMap< String, Place >( );
		for ( MetricsOutput out : runs ) {
			for ( Vector2 pos : out.screwed ) {
				addToMap( pos, parsedScrew );
			}
		}
	}

	private void parseUnscrew( ) {
		parsedUnscrew = new HashMap< String, Place >( );
		for ( MetricsOutput out : runs ) {
			for ( Vector2 pos : out.unscrewed ) {
				addToMap( pos, parsedUnscrew );
			}
		}
	}

	private void parseAttach( ) {
		parsedAttach = new HashMap< String, Place >( );
		for ( MetricsOutput out : runs ) {
			for ( Vector2 pos : out.attach ) {
				addToMap( pos, parsedAttach );
			}
		}
	}
}
