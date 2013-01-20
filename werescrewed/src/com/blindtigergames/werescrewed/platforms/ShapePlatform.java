package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Player.PlayerState;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

public class ShapePlatform extends Platform {

	public ShapePlatform( String n, Vector2 pos, Texture tex, World world,
			Shapes shape, float width, float height, boolean flip ) {
		super( n, pos, tex, null );
		this.world = world;

		switch ( shape ) {
		case rhombus:
			constructRhombus( pos, width, height, flip );
			break;
		case cross:
			System.out.println( "trap" );
			break;
		case plus:
			constructPlus( pos, width, height, 1.0f, 1.0f );
			break;
		case trapezoid:
			System.out.println( "trap" );
			break;
		case Lshaped:
			System.out.println( "trap" );
			break;
		case Tshaped:
			System.out.println( "trap" );
			break;
		case dumbbell:
			System.out.println( "trap" );
			break;

		}
	}

	// TODO: constuct bodies for each shape
	// trapezoid, cross, plus, rhombus, Lshaped, Tshaped, dumbbell

	//The vertices used in this function specifically create a rhombus
	//there is also a scale factor for both x and y direction
	public void constructRhombus( Vector2 pos, float width, float height, boolean flip ) {

		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( pos.x, pos.y ) );
		body = world.createBody( groundBodyDef );

		Vector2[ ] vertices = new Vector2[ 4 ];
		Vector2 point1;
		Vector2 point2;
		Vector2 point3;
		Vector2 point4;
		float ptb = GameScreen.PIXEL_TO_BOX;
		point1 = new Vector2( 0.0f, 0.0f );
		point2 = new Vector2( 32.0f * ptb * width, 0.0f );
		if ( !flip ) {
			point3 = new Vector2( 48.0f * ptb * width, 32.0f * ptb * height );
			point4 = new Vector2( 16.0f * ptb * width, 32.0f * ptb * height );
		} else {
			point3 = new Vector2( 16.0f * ptb * width, 32.0f * ptb * height );
			point4 = new Vector2( -16.0f * ptb * width, 32.0f * ptb * height );
		}

		vertices[ 0 ] = point1;
		vertices[ 1 ] = point2;
		vertices[ 2 ] = point3;
		vertices[ 3 ] = point4;

		PolygonShape polygon = new PolygonShape( );
		polygon.set( vertices );

		FixtureDef platformFixtureDef = new FixtureDef( );
		platformFixtureDef.shape = polygon;
		platformFixtureDef.density = 1.9f;
		platformFixtureDef.friction = 0.5f;
		platformFixtureDef.restitution = 0.0f;
		body.setGravityScale( .1f );
		body.createFixture( platformFixtureDef );

	}
	
	public void constructPlus( Vector2 pos, float width, float height, float thickX, float thickY ) {
		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( pos.x, pos.y ) );
		body = world.createBody( groundBodyDef );
		
		PolygonShape ps = new PolygonShape( );
		FixtureDef fd = new FixtureDef( );
		fd.density = 1f;
		fd.restitution = 0.0f;

		// Smallest Plus should be 3x3
		if ( width < 2.0f )
			width = 3.0f;
		if ( height < 2.0f )
			height = 3.0f;
		
		float hori = width * tileConstant * GameScreen.PIXEL_TO_BOX;
		float vert = height * tileConstant * GameScreen.PIXEL_TO_BOX;
		Vector2 z = new Vector2( );

		// Creating 2 fixtures to make a plus 
		ps.setAsBox( hori, thickY * tileConstant * GameScreen.PIXEL_TO_BOX , z, 0 );
		fd.shape = ps;
		body.createFixture( fd );
		
		ps.setAsBox( thickX * tileConstant * GameScreen.PIXEL_TO_BOX, vert, z, 0 );
		fd.shape = ps;
		body.createFixture( fd );

		
	}

	public void update( ) {
		super.update( );
	}
}