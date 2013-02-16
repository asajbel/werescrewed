package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Base class for the various types of screws. Defines basic behavior.
 * 
 * @param name - id of screw
 * @param pos - position in the world of the screw
 * @param tex - texture applied to the screw
 * 
 * @author Dennis
 * 
 */
public class Screw extends Entity {
	public enum ScrewType {
		STATIC, STRIPPED, PUZZLE, STRUCTURAL, BOSS
	}
	protected int rotation;
	protected int depth;
	protected int maxDepth;
	protected int screwStep;
	protected ScrewType screwType;

	/**
	 * constructor to use if you want a static screw
	 * @param name
	 * @param pos
	 * @param max
	 * @param entity
	 * @param skeleton
	 * @param world
	 */
	public Screw( String name, Vector2 pos, int max, Entity entity,
			Skeleton skeleton, World world ) {
		super( name, pos, 
				WereScrewedGame.manager.get(WereScrewedGame.dirHandle.path( ) +"/common/screw.png", Texture.class ), null, false );
		screwType = ScrewType.STATIC;
		constructBody( pos );
		connectScrewToEntity( entity );
	}
	
	public Screw( String name, Vector2 pos, Texture tex ) {
		super( name, pos, ( tex == null ? 
				WereScrewedGame.manager.get(WereScrewedGame.dirHandle.path( ) +"/common/screw.png", Texture.class) : tex ), null, false );
	}

	/**
	 * destroys everything contained within the screw instance
	 */
	public void remove( ) {
		world.destroyBody( body );
	}

	/**
	 * Turns structural and puzzle screws to the left
	 * which decreases depth
	 * structural screws will eventually fall out
	 * @param
	 */
	public void screwLeft( ) {
	}
	
	/**
	 * Turns structural and puzzle screws to the right
	 * which increases depth and tightens structural screws
	 * @param
	 */
	public void screwRight( ) {
	}
	/**
	 * Turns structural and puzzle screws to the left
	 * structural screws will eventually fall out
	 * @param
	 */
	public int getDepth( ) {
		return depth;
	}

	public boolean endLevelFlag( ) {
		return false;
	}
		
	/**
	 * returns the ScrewType of the screw
	 * @return ScrewType type of screw
	 */
	public ScrewType getScrewType( ) { 
		return screwType;
	}
	
	private void constructBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape
				.setRadius( ( sprite.getWidth( ) / 2.0f ) * Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		body.setUserData( this );

		// You dont dispose the fixturedef, you dispose the shape
		screwShape.dispose( );
	}

	private void connectScrewToEntity( Entity entity ) {
		// connect the screw to the entity
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( body, entity.body, body.getPosition( ) );
		revoluteJointDef.enableMotor = false;
		world.createJoint( revoluteJointDef );
	}
}
