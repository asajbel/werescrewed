package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.screws.Screw;

/**
 * @param name
 * 
 * 
 * @author Ranveer
 * 
 */

public class Platform extends Entity {

	IMover mover;

	protected float width, height;
	protected boolean dynamicType = false;
	protected boolean rotate = false;
	protected boolean oneSided = false;
	protected ArrayList< Screw > screws;
	// tileConstant is 16 for setasbox function which uses half width/height
	// creates 32x32 objects
	protected final int tileConstant = 16;
	
	/**
	 * Used for kinematic movement connected to skeleton
	 */
	protected Vector2 origin;

	public Platform( String n, Vector2 pos, Texture tex, World world ) {
		super( n, pos, tex, null, true );
		this.world = world;
		screws = new ArrayList< Screw >( );
	}

	public Platform( String n, EntityDef d, World w, Vector2 pos, float rot,
			Vector2 sca ) {
		super( n, d, w, pos, rot, sca, null, true );
		screws = new ArrayList< Screw >( );
	}

	public Platform( String n, EntityDef d, World w, Vector2 pos, float rot,
			Vector2 sca, Texture tex ) {
		super( n, d, w, pos, rot, sca, tex, true );
		screws = new ArrayList< Screw >( );
	}

	public void addScrew( Screw s ) {
		screws.add( s );
	}

	@Override
	public void setAwake( ) {
		body.setAwake( true );
		for ( Screw s : screws )
			s.body.setAwake( true );
	}

	@Override
	public void update( float deltaTime ) {

		body.setActive( true );

		super.update( deltaTime );

		if ( Gdx.input.isKeyPressed( Keys.T ) ) {
			// Turned off because ground will fall
			// rotate( );
		}

		if ( Gdx.input.isKeyPressed( Keys.Y ) ) {
			body.setAngularVelocity( 0 );
		}
		if ( Gdx.input.isKeyPressed( Keys.O ) ) {
			changeType( );
		}

		if ( Gdx.input.isKeyPressed( Keys.N ) ) {
			// rotateBy90();
			rotate = !rotate;
		}
		if ( Gdx.input.isKeyPressed( Keys.L ) ) {
			setHorizontal( );
		}
		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			setOneSided( !getOneSided( ) );
			System.out.println( getOneSided( ) );
		}
	}

	public void setDensity( float d ) {
		for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
			body.getFixtureList( ).get( i ).setDensity( d );

	}

	public void setFriction( float f ) {
		for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
			body.getFixtureList( ).get( i ).setFriction( f );
	}

	public void setRestitution( float r ) {
		for ( int i = 0; i < body.getFixtureList( ).size( ); ++i )
			body.getFixtureList( ).get( i ).setRestitution( r );
	}

	public void setGravScale( float g ) {
		body.setGravityScale( g );
	}

	public void changeType( ) {
		dynamicType = !dynamicType;
		if ( dynamicType ) {
			body.setType( BodyType.DynamicBody );
		} else
			body.setType( BodyType.KinematicBody );

		body.setActive( false );
	}

	// This function sets the platform to 180* no matter what angle it currently
	// is
	public void setHorizontal( ) {
		body.setTransform( body.getPosition( ), ( float ) Math.toRadians( 90 ) );
	}

	// This function sets platform to 90*
	public void setVertical( ) {
		body.setTransform( body.getPosition( ), ( float ) Math.toRadians( 180 ) );
	}

	public boolean getOneSided( ) {
		return oneSided;
	}

	public void setOneSided( boolean value ) {
		oneSided = value;
	}

	protected void rotate( ) {
		body.setAngularVelocity( 1f );
	}

	protected void rotateBy90( ) {
		float bodyAngle = body.getAngle( );
		body.setTransform( body.getPosition( ), bodyAngle + 90 );
	}
}