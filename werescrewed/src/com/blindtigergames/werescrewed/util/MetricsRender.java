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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

public class MetricsRender {
	private enum Type {
		NONE, JUMP, DIE, SCREW, UNSCREW, ATTACH, TIME
	}

	private class Place {
		float x;
		float y;
		Vector3 color;
		int num;
	}

	private boolean fileExists = false;
	private boolean render = false;
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
		runs = new ArrayList< MetricsOutput >( );
		Json json = new Json( );
		File file = new File( LevelName + Metrics.FILE_APPEND );
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
		}
	}

	public void render( OrthographicCamera camera ) {
		if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT_BRACKET ) ) {
			cycleRenderForward( );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.LEFT_BRACKET ) ) {
			cycleRenderBackward( );
		}

		Map< String, Place > parsed;
		switch ( whatToRender ) {
		case ATTACH:
			parsed = parsedAttach;
			break;
		case DIE:
			parsed = parsedDeath;
			break;
		case JUMP:
			parsed = parsedJump;
			break;
		case NONE:
			parsed = new HashMap< String, Place >( );
			break;
		case SCREW:
			parsed = parsedScrew;
			break;
		case TIME:
			parsed = new HashMap< String, Place >( );
			break;
		case UNSCREW:
			parsed = parsedUnscrew;
			break;
		default:
			parsed = new HashMap< String, Place >( );
			break;

		}

		if ( render && fileExists ) {
			Gdx.gl.glEnable( GL10.GL_BLEND );
			Gdx.gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );

			for ( Map.Entry< String, Place > rend : parsed.entrySet( ) ) {
				shapeRenderer.setProjectionMatrix( camera.combined );
				shapeRenderer.begin( ShapeType.FilledCircle );
				shapeRenderer.setColor( rend.getValue( ).color.x,
						rend.getValue( ).color.y, rend.getValue( ).color.z,
						alpha );
				shapeRenderer.filledCircle( rend.getValue( ).x,
						rend.getValue( ).y, 16 );
				shapeRenderer.end( );
			}

			Gdx.gl.glDisable( GL10.GL_BLEND );
		}
	}

	private void cycleRenderForward( ) {
		switch ( whatToRender ) {
		case ATTACH:
			render = true;
			whatToRender = Type.NONE;
			break;
		case DIE:
			render = true;
			whatToRender = Type.SCREW;
			break;
		case JUMP:
			render = true;
			whatToRender = Type.DIE;
			break;
		case NONE:
			render = false;
			whatToRender = Type.JUMP;
			break;
		case SCREW:
			render = true;
			whatToRender = Type.UNSCREW;
			break;
		case TIME:
			render = false;
			break;
		case UNSCREW:
			render = true;
			whatToRender = Type.ATTACH;
			break;
		default:
			break;

		}
	}

	private void cycleRenderBackward( ) {
		switch ( whatToRender ) {
		case ATTACH:
			render = true;
			whatToRender = Type.UNSCREW;
			break;
		case DIE:
			render = true;
			whatToRender = Type.JUMP;
			break;
		case JUMP:
			render = true;
			whatToRender = Type.NONE;
			break;
		case NONE:
			render = false;
			whatToRender = Type.ATTACH;
			break;
		case SCREW:
			render = true;
			whatToRender = Type.DIE;
			break;
		case TIME:
			render = false;
			break;
		case UNSCREW:
			render = true;
			whatToRender = Type.SCREW;
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

	private void addToMap( Vector2 pos, Map< String, Place > parced ) {
		float startX, startY, endX, endY;
		startX = pos.x;
		startY = pos.y;
		endX = round( startX );
		endY = round( startY );
		String key = endX + ", " + endY;
		if ( parsedJump.containsKey( key ) ) {
			parsedJump.get( key ).num++;
		} else {
			Place p = new Place( );
			p.x = endX;
			p.y = endY;
			p.color.x = 0.0f;
			p.color.y = 0.0f;
			p.color.z = 1.0f;
			p.num = 1;
			parsedJump.put( key, p );
		}
	}

	private void interpolateColor( Place p ) {
		int runsTimes = runs.size( );

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

	private float round( float start ) {
		float end = 0;
		end = start * Util.PIXEL_TO_BOX;
		end = Math.round( end );
		end = end * Util.BOX_TO_PIXEL;
		return end;
	}
}
