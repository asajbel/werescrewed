package com.blindtigergames.werescrewed.rope;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

public class Rope {

	StrippedScrew screw;
	private ArrayList< Link > linkParts;

	public Rope( String name, Vector2 pos, Vector2 widthHeight, int links,
			Texture texture, World world ) {

		linkParts = new ArrayList< Link >( );
		constructRope( name, pos, widthHeight, links, world );

		// screw = new StrippedScrew ( "rope screw", world, new Vector2 (pos.x,
		// pos.y - widthHeight.y * Util.PIXEL_TO_BOX * links) );

	}

	public Rope( String name, Entity entity, Vector2 widthHeight, int links,
			Texture texture, World world ) {

		linkParts = new ArrayList< Link >( );
		constructRope( name, entity.getPosition( ), widthHeight, links, world );

		// screw = new StrippedScrew ( "rope screw", world, new Vector2 (pos.x,
		// pos.y - widthHeight.y * Util.PIXEL_TO_BOX * links) );

	}

	private void constructRope( String name, Vector2 pos, Vector2 widthHeight,
			int links, World world ) {

		Link topPiece = new Link( "top", world, pos, null );
		topPiece.body.setType( BodyType.StaticBody );
		linkParts.add( topPiece );

		for ( int i = 0; i < links; ++i ) {

			Link temp = new Link( "link" + i, world, new Vector2(
					getEnd( ).body.getWorldCenter( ).x,
					getEnd( ).body.getWorldCenter( ).y - widthHeight.y
							* Util.PIXEL_TO_BOX ), null );

			getEnd( ).createLinkJoint( temp );
			linkParts.add( temp );

		}

		//this is the same as..
		screw = new StrippedScrew( "rope screw", world, new Vector2(
				getEnd( ).body.getWorldCenter( ).x,
				getEnd( ).body.getWorldCenter( ).y - widthHeight.y
						* Util.PIXEL_TO_BOX ) );
		screw.connectScrewToEntity( getEnd( ) );
		//this
		screw = new StrippedScrew( "rope screw", world, new Vector2(
				getEnd( ).body.getWorldCenter( ).x,
				getEnd( ).body.getWorldCenter( ).y - widthHeight.y
						* Util.PIXEL_TO_BOX ), getEnd( ) );
		//screw.connectScrewToEntity( getEnd( ) );
	}

	public void update( float deltatime ) {
		// if(Gdx.input.isKeyPressed( Keys.O ))
		// pieces.get( pieces.size( )-1 ).applyLinearImpulse( new Vector2(0.5f,
		// 0.0f),
		// pieces.get( pieces.size( )-1 ).getWorldCenter( ) );

	}

	public void draw( SpriteBatch batch ) {
		screw.draw( batch );
	}

	private Link getEnd( ) {
		return linkParts.get( linkParts.size( ) - 1 );
	}
}
