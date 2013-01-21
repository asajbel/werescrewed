package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
			constructRhombus( pos, width, height, flip, 1.0f );
			break;
		case cross:
			constructCross( pos, width, height, width, height, 1.0f );
			break;
		case plus:
			constructPlus( pos, width, height, 1.0f, 1.0f );
			break;
		case trapezoid:
			constructTrapezoid( pos, width, height, 1.0f );
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
	// I figure the level loader stuff can use the following functions

	// The vertices used in this function specifically create a rhombus
	// there is also a scale factor for both x and y direction
	public void constructRhombus( Vector2 pos, float width, float height,
			boolean flip, float scale ) {

		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( pos.x, pos.y ) );
		body = world.createBody( groundBodyDef );

		float horizontal = width * tileConstant * 2 * GameScreen.PIXEL_TO_BOX
				* scale;
		float vertical = height * tileConstant * 2 * GameScreen.PIXEL_TO_BOX
				* scale;
		Vector2[ ] vertices = new Vector2[ 4 ];
		Vector2 point1 = new Vector2( );
		Vector2 point2 = new Vector2( horizontal, 0.0f );
		Vector2 point3;
		Vector2 point4;

		if ( flip ) {
			point3 = new Vector2( -( horizontal * 1.5f ), vertical );
			point4 = new Vector2( -( horizontal / 2 ), vertical );
		} else {
			point3 = new Vector2( horizontal * 1.5f, vertical );
			point4 = new Vector2( horizontal / 2, vertical );
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

	public void constructCross( Vector2 pos, float innerWidth,
			float innerHeight, float outerWidth, float outerHeight, float scale ) {
		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( pos.x, pos.y ) );
		body = world.createBody( groundBodyDef );
		body.setGravityScale( .1f );

		PolygonShape ps = new PolygonShape( );
		FixtureDef fd = new FixtureDef( );
		fd.density = 1f;
		fd.restitution = 0.0f;

		// Smallest width/height should be 1
		// if ( width < 2.0f )
		// width = 3.0f;
		// if ( height < 2.0f )
		// height = 3.0f;

		float iW = innerWidth * tileConstant * GameScreen.PIXEL_TO_BOX * scale;
		float iH = innerHeight * tileConstant * GameScreen.PIXEL_TO_BOX * scale;
		float oW = outerWidth * tileConstant * GameScreen.PIXEL_TO_BOX * scale;
		float oH = outerHeight * tileConstant * GameScreen.PIXEL_TO_BOX * scale;

		Vector2 z = new Vector2( );
		Vector2 p1 = new Vector2( -( iW + oW ), -( iH + oH ) );
		Vector2 p2 = new Vector2( -( iW + oW ), ( iH + oH ) );
		Vector2 p3 = new Vector2( ( iW + oW ), ( iH + oH ) );
		Vector2 p4 = new Vector2( ( iW + oW ), -( iH + oH ) );

		ps.setAsBox( iW, iH, z, 0 );
		fd.shape = ps;
		body.createFixture( fd );

		ps.setAsBox( oW, oH, p1, 0 );
		fd.shape = ps;
		body.createFixture( fd );

		ps.setAsBox( oW, oH, p2, 0 );
		fd.shape = ps;
		body.createFixture( fd );

		ps.setAsBox( oW, oH, p3, 0 );
		fd.shape = ps;
		body.createFixture( fd );

		ps.setAsBox( oW, oH, p4, 0 );
		fd.shape = ps;
		body.createFixture( fd );

	}

	public void constructPlus( Vector2 pos, float width, float height,
			float thickX, float thickY ) {
		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( pos.x, pos.y ) );
		body = world.createBody( groundBodyDef );
		body.setGravityScale( .1f );

		PolygonShape ps = new PolygonShape( );
		FixtureDef fd = new FixtureDef( );
		fd.density = 1f;
		fd.restitution = 0.0f;

		// Smallest Plus should be 3x3
		if ( width < 2.0f )
			width = 3.0f;
		if ( height < 2.0f )
			height = 3.0f;

		float horizontal = width * tileConstant * GameScreen.PIXEL_TO_BOX;
		float vertical = height * tileConstant * GameScreen.PIXEL_TO_BOX;
		Vector2 z = new Vector2( );

		// Creating 2 fixtures to make a plus
		ps.setAsBox( horizontal, thickY * tileConstant
				* GameScreen.PIXEL_TO_BOX, z, 0 );
		fd.shape = ps;
		body.createFixture( fd );

		ps.setAsBox( thickX * tileConstant * GameScreen.PIXEL_TO_BOX, vertical,
				z, 0 );
		fd.shape = ps;
		body.createFixture( fd );

	}

	public void constructTrapezoid( Vector2 pos, float width, float height,
			float scale ) {

		BodyDef groundBodyDef = new BodyDef( );
		groundBodyDef.type = BodyType.KinematicBody;
		groundBodyDef.position.set( new Vector2( pos.x, pos.y ) );
		body = world.createBody( groundBodyDef );

		float horizontal = width * tileConstant * 2 * GameScreen.PIXEL_TO_BOX
				* scale;
		float vertical = height * tileConstant * 2 * GameScreen.PIXEL_TO_BOX
				* scale;

		Vector2[ ] vertices = new Vector2[ 4 ];
		Vector2 point1;
		Vector2 point2;
		Vector2 point3;
		Vector2 point4;

		point1 = new Vector2( 0.0f, 0.0f );
		point2 = new Vector2( horizontal, 0.0f );
		point3 = new Vector2( horizontal * 1.5f, vertical );
		point4 = new Vector2( -( horizontal * 0.5f ), vertical );

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

	public void update( ) {
		super.update( );
	}
}