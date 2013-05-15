package com.blindtigergames.werescrewed.entity.builders;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.platforms.Pipe;

public class PipeBuilder extends GenericEntityBuilder< PipeBuilder > {
	private ArrayList< Vector2 > path;
	private float gravScale;
	private BodyType bodyType;
	private float restitution = 0;
	private float density = 1f;
	private float friction = 1.0f;
	private boolean open = false;

	/**
	 * 
	 * @param world
	 *            - Box2d world, only needs to be set once
	 * @return PlatformBuilder
	 */
	public PipeBuilder( World world ) {
		super( );
		reset( );
		super.world( world );
	}

	public Pipe build( ) {
		Pipe out = new Pipe( this.name, this.pos, this.path, this.tex,
				this.world, open );

		out.body.setType( bodyType );
		out.setDensity( this.density );
		out.setFriction( this.friction );
		out.setRestitution( this.restitution );
		out.setGravScale( this.gravScale );
		out.body.setFixedRotation( false );
		prepareEntity( out );
		return out;
	}

	public PipeBuilder path( ArrayList< Vector2 > path ) {
		this.path = path;
		return this;
	}

	public PipeBuilder gravityScale( float gravScale ) {
		this.gravScale = gravScale;
		return this;

	}

	public PipeBuilder restitution( float restitution ) {
		this.restitution = restitution;
		return this;
	}

	public PipeBuilder density( float density ) {
		this.density = density;
		return this;
	}

	public PipeBuilder friction( float friction ) {
		this.friction = friction;
		return this;
	}

	public PipeBuilder dynamic( boolean d ) {
		if ( d ) {
			return this.dynamic( );
		}
		return this.kinematic( );
	}

	public PipeBuilder dynamic( ) {
		bodyType = BodyType.DynamicBody;
		return this;
	}

	public PipeBuilder staticBody( ) {
		bodyType = BodyType.StaticBody;
		return this;
	}

	public PipeBuilder kinematic( ) {
		bodyType = BodyType.KinematicBody;
		return this;
	}

	public PipeBuilder openEnded( ) {
		open = true;
		return this;
	}
}
