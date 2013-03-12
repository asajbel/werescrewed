package com.blindtigergames.werescrewed.rope;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

/** A chain of box fixtures that 
 * are jointed together to act as a rope.
 * 
 * @author Edward Boning, Ranveer Dhaliwal
 * 
 */
public class Rope {

	public String name;
	private StrippedScrew screw;
	private ArrayList< Link > linkParts;
	private World world;

	/**
	 * Constructs a rope at a given position
	 * 
	 * @param name name of the rope entity
	 * 
	 * @param pos desired position of the top of the rope
	 * 
	 * @param widthHeight width and height of each link
	 * 
	 * @param links number of links of chain
	 * 
	 * @param texture texture of rope
	 * 
	 * @param world world the rope exists in
	 */
	public Rope( String name, Vector2 pos, Vector2 widthHeight, int links,
			Texture texture, World world ) {
		this.name = name;
		this.world = world;
		linkParts = new ArrayList< Link >( );
		constructRope( name, pos, widthHeight, links, texture, world );

//		screw = new StrippedScrew( "ropescrew",
//				new Vector2( pos.x, pos.y
//						- widthHeight.y * Util.PIXEL_TO_BOX * links )
//						.mul( Util.BOX_TO_PIXEL ), linkParts.get( linkParts
//						.size( ) - 1 ), world );

	}

	/*
	 * Constructs a rope at the position of a given entity
	 * 
	 * @param name name of the rope entity
	 * 
	 * @param entity entity where the rope will be created
	 * 
	 * @param widthHeight width and height of each link
	 * 
	 * @param links number of links of chain
	 * 
	 * @param texture texture of rope
	 * 
	 * @param world world the rope exists in
	 */
	public Rope( String name, Entity entity, Vector2 widthHeight, int links,
			Texture texture, World world ) {
		this.name = name;
		this.world = world;
		linkParts = new ArrayList< Link >( );
		constructRope( name, entity.getPosition( ), widthHeight, links,
				texture, world );

//		screw = new StrippedScrew( "ropescrew",
//				new Vector2( entity.getPosition( ).x, entity.getPosition( ).y
//						- widthHeight.y * Util.PIXEL_TO_BOX * links )
//						.mul( Util.BOX_TO_PIXEL ), linkParts.get( linkParts
//						.size( ) - 1 ), world );

	}

	/*
	 * Constructs rope fixtures given parameters from the constructor
	 */
	private void constructRope( String name, Vector2 pos, Vector2 widthHeight,
			int links, Texture texture, World world ) {

		Link topPiece = new Link( "top", world, pos, texture, widthHeight );
		topPiece.body.setType( BodyType.DynamicBody );
		linkParts.add( topPiece );

		for ( int i = 0; i < links; ++i ) {

			Link temp = new Link( "link" + i, world, new Vector2(
					getEnd( ).body.getWorldCenter( ).x,
					getEnd( ).body.getWorldCenter( ).y - widthHeight.y
							* Util.PIXEL_TO_BOX ), texture, widthHeight );

			getEnd( ).createLinkJoint( temp );
			linkParts.add( temp );

		}

	}

	/*
	 * Creates a joint between the top link and a given entity
	 * 
	 * @param entity entity to which the rope will be jointed
	 * 
	 * @param move if true, will move the given entity's position to the top of
	 * the rope
	 */
	public void attachEntityToTop( Entity entity, boolean move ) {
		if ( move ) {
			entity.setPosition( getFirstLink( ).getPosition( ) );
		}
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( getFirstLink( ).body, entity.body,
				getFirstLink( ).body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}

	/*
	 * Creates a joint between the bottom link and a given entity
	 * 
	 * @param entity entity to which the rope will be jointed
	 * 
	 * @param move if true, will move the given entity's position to the bottom
	 * of the rope
	 */
	public void attachEntityToBottom( Entity entity, boolean move ) {
		if ( move ) {
			entity.setPosition( getLastLink( ).getPosition( ) );
		}
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( getLastLink( ).body, entity.body,
				getLastLink( ).body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}

	public void update( float deltaTime ) {
		screw.update( deltaTime );
		// if(Gdx.input.isKeyPressed( Keys.O ))
		// pieces.get( pieces.size( )-1 ).applyLinearImpulse( new Vector2(0.5f,
		// 0.0f),
		// pieces.get( pieces.size( )-1 ).getWorldCenter( ) );

	}

	/*
	 * Draws all links of the rope
	 * 
	 * @param batch sprite batch used for drawing the rope
	 */
	public void draw( SpriteBatch batch ) {
		if ( screw != null )
			screw.draw( batch );
		for ( int i = 0; i < linkParts.size( ); i++ ) {
			getLink( i ).draw( batch );
		}
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
	public Link getFirstLink( ) {
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
	 * @param index
	 *            - the index of the link in the rope
	 * @return the link at this index of the rope
	 */
	public Link getLink( int index ) {
		if ( index < linkParts.size( ) ) {
			return linkParts.get( index );
		}
		return null;
	}

	/*
	 * @return the last link in the rope
	 */
	public Link getEnd( ) {
		return linkParts.get( linkParts.size( ) - 1 );
	}

	public void createScrew( ) {
		screw = new StrippedScrew( "ropeScrew", new Vector2(
				getLastLink( ).body.getPosition( ).x * Util.BOX_TO_PIXEL,
				( getLastLink( ).body.getPosition( ).y * Util.BOX_TO_PIXEL )
						- ( getLastLink( ).getHeight( ) ) ), getLastLink( ), world );

	}

}
