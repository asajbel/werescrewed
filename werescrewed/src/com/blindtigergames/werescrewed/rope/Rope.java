package com.blindtigergames.werescrewed.rope;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

public class Rope {

	private StrippedScrew screw;
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

		Link topPiece = new Link( "top", world, pos, null, widthHeight );
		topPiece.body.setType( BodyType.StaticBody );
		linkParts.add( topPiece );

		for ( int i = 0; i < links; ++i ) {

			Link temp = new Link( "link" + i, world, new Vector2(
					getEnd( ).body.getWorldCenter( ).x,
					getEnd( ).body.getWorldCenter( ).y - widthHeight.y
							* Util.PIXEL_TO_BOX ), null, widthHeight );

			getEnd( ).createLinkJoint( temp );
			linkParts.add( temp );

		}

		screw = new StrippedScrew( "rope screw", world, new Vector2(
				getEnd( ).body.getWorldCenter( ).x * Util.BOX_TO_PIXEL,
				getEnd( ).body.getWorldCenter( ).y * Util.BOX_TO_PIXEL
						- widthHeight.y ), getEnd( ) );
		screw.body.getFixtureList( ).get( 0 ).setSensor( false );
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

	/**
	 * 
	 * @return the screw attached at the end of the rope
	 */
	public Screw getEndAttachment( ) {
		return screw;
	}
	
	/**
	 * 
	 * @return the first link in the rope
	 */
	public Link getFirstLink ( ) {
		return linkParts.get( 0 );
	}
	
	/**
	 * 
	 * @return the last link in the rope
	 */
	public Link getLastLink( ) {
		return linkParts.get( linkParts.size( ) - 1 );
	}
	
	/**
	 * 
	 * @param index - the index of the link in the rope
	 * @return the link at this index of the rope
	 */
	public Link getLink( int index ) {
		if ( index < linkParts.size( ) ) {
			return linkParts.get( index );
		}
		return null;
	}
	
	private Link getEnd( ) {
		return linkParts.get( linkParts.size( ) - 1 );
	}
}
