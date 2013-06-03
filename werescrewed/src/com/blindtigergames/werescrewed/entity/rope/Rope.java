package com.blindtigergames.werescrewed.entity.rope;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.util.Util;

/** A chain of box fixtures that 
 * are jointed together to act as a rope.
 * 
 * @author Edward Boning, Ranveer Dhaliwal
 * 
 */
public class Rope {

	public String name;
	private ArrayList< StrippedScrew > screws = new ArrayList<StrippedScrew>();
	private ArrayList< Link > linkParts;
	private World world;
	private final float ROPE_DEACCELERATION_RATE = 0.6f;

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



	}

	/*
	 * Constructs rope fixtures given parameters from the constructor
	 */
	private void constructRope( String name, Vector2 pos, Vector2 widthHeight,
			int links, Texture texture, World world ) {

		Link topPiece = new Link( "top", world, pos, texture, widthHeight );
		topPiece.body.setType( BodyType.DynamicBody );
		linkParts.add( topPiece );
		
		Link temp = null, prev = null;
		for ( int i = 0; i < links; ++i ) {
			temp = new Link( "link" + i, world, new Vector2(
					getEnd( ).body.getWorldCenter( ).x,
					getEnd( ).body.getWorldCenter( ).y - widthHeight.y
							* Util.PIXEL_TO_BOX ), texture, widthHeight );
			if (prev != null){
				temp.setParent(prev);
			}
			prev = temp;
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
		if(screws.size( ) != 0){
			boolean playerAttached = false;
			for(StrippedScrew s : screws){
				if(s.isPlayerAttached( )) playerAttached = true;
				s.update( deltaTime );
			}
			if(!playerAttached)
				stopRope();
		}
		
//		if ( screw != null ) {	
//			
//			if ( !screw.isPlayerAttached( ) ){
//				stopRope( );
//			}
//			
//			screw.update( deltaTime );
//		}
	

	}

	/*
	 * Draws all links of the rope
	 * 
	 * @param batch sprite batch used for drawing the rope
	 */
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		
		for ( int i = 0; i < linkParts.size( ); i++ ) {
			getLink( i ).draw( batch, deltaTime, camera );
		}
		if(screws.size( ) != 0){

			for(StrippedScrew s : screws){
				s.draw( batch, deltaTime, camera );
			}
		}
	}


	/**
	 * 
	 * @return the screw attached at the end of the rope
	 */
	public Screw getEndAttachment( ) {
		if ( screws.size( ) -1 >= 0 )
			return screws.get( screws.size( ) - 1 );
		return null;
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
	
	public Link getSecondedToLastLink( ) {
		return linkParts.get( linkParts.size( ) - 2 );
	}

	public Link getThirdToLastLink( ) {
		return linkParts.get( linkParts.size( ) - 3 );
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
		StrippedScrew screw = new StrippedScrew( "ropeScrew", new Vector2(
				getLastLink( ).body.getPosition( ).x * Util.BOX_TO_PIXEL,
				( getLastLink( ).body.getPosition( ).y * Util.BOX_TO_PIXEL )
						- ( getLastLink( ).getHeight( ) ) ), getLastLink( ), world, Vector2.Zero );
		screw.setPlayerNotSensor( );
		
		screws.add( screw );

	}
	public void createScrewAll(){
		for(int i = 1; i < linkParts.size( ); i += 2){
			Link link = linkParts.get( i );
			
			StrippedScrew screw = new StrippedScrew( "ropeScrew", new Vector2(
					link.body.getPosition( ).x * Util.BOX_TO_PIXEL,
					( link.body.getPosition( ).y * Util.BOX_TO_PIXEL )
							 ), link, world, Vector2.Zero );
			screw.setPlayerNotSensor( );
			
			screws.add(screw);
		}
		
	}
	public void createScrewThirdToLastLink( ) {
		StrippedScrew screw = new StrippedScrew( "ropeScrew", new Vector2(
				getThirdToLastLink( ).body.getPosition( ).x * Util.BOX_TO_PIXEL,
				( getThirdToLastLink( ).body.getPosition( ).y * Util.BOX_TO_PIXEL )
						 ), getThirdToLastLink( ), world, Vector2.Zero );
		screw.setPlayerNotSensor( );
		
		screws.add(screw);

	}
	
	public void createScrewSecondToLastLink( ) {
		StrippedScrew screw = new StrippedScrew( "ropeScrew", new Vector2(
				getSecondedToLastLink( ).body.getPosition( ).x * Util.BOX_TO_PIXEL,
				( getSecondedToLastLink( ).body.getPosition( ).y * Util.BOX_TO_PIXEL )
						 ), getSecondedToLastLink( ), world, Vector2.Zero );
		screw.setPlayerNotSensor( );

		screws.add(screw);
	}
	
	public void stopRope( ) {
		getLastLink().body.setLinearVelocity( getLastLink().body.getLinearVelocity( ).x * ROPE_DEACCELERATION_RATE,
				getLastLink().body.getLinearVelocity( ).y );
		
//		float velocity = getLastLink().body.getLinearVelocity( ).x;
//		if ( velocity != 0.0f ) {
//			if ( velocity < -0.1f )
//				getLastLink( ).body.applyLinearImpulse( new Vector2( 0.01f, 0.0f ),
//						getLastLink ( ).body.getWorldCenter( ) );
//			else if ( velocity > 0.1f )
//				getLastLink( ).body.applyLinearImpulse( new Vector2( -0.01f, 0.0f ),
//						getLastLink( ).body.getWorldCenter( ) );
//			else if ( velocity >= -0.1 && velocity <= 0.1f && velocity != 0.0f )
//				getLastLink( ).body.setLinearVelocity( 0.0f, getLastLink( ).body.getLinearVelocity( ).y );
//		}
	}
	
	public void dispose(){
		for ( Link l : linkParts ){
			l.dispose( );
		}
		linkParts.clear( );
		for(Screw s : screws)
			s.dispose();
		screws.clear( );
	}

}
