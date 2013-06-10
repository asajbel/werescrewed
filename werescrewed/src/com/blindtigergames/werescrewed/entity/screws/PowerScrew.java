package com.blindtigergames.werescrewed.entity.screws;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.player.Player.ConcurrentState;
import com.blindtigergames.werescrewed.util.Util;

/**
 * screws that are used as power buttons either one or two players must be
 * pushing it to power it
 * 
 * @author Dennis
 * 
 */
public class PowerScrew extends Screw {
	private SimpleFrameAnimator powerAnimator;

	public PowerScrew( String name, Vector2 pos, Entity entity, World world ) {
		super( name, pos, null );
		this.world = world;
		this.entity = entity;
		maxDepth = 1;
		depth = 1;
		rotation = 0;
		extraJoints = new ArrayList< Joint >( );
		screwType = ScrewType.SCREW_STRUCTURAL;
		entityType = EntityType.SCREW;
		sprite = this.constructSprite( WereScrewedGame.manager
				.getAtlas( "pushButton" ).findRegion( "0" ) );
		constuctBody( pos );
		addStructureJoint( entity );
	}

	public PowerScrew( String name, Vector2 pos, World world ) {
		super( name, pos, null );
		this.world = world;
		maxDepth = 1;
		depth = 1;
		rotation = 0;
		extraJoints = new ArrayList< Joint >( );
		screwType = ScrewType.SCREW_STRUCTURAL;
		entityType = EntityType.SCREW;
		sprite = this.constructSprite( WereScrewedGame.manager
				.getAtlas( "pushButton" ).findRegion( "0" ) );
	}

	public void hitPlayer( Player player ) {
		if ( player.getExtraState( ) == ConcurrentState.ScrewReady ) {
			depth = 0;
			powerAnimator.speed( 1f );
		}
	}

	private void constuctBody( Vector2 pos ) {
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.gravityScale = 0.07f;
		screwBodyDef.fixedRotation = false;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape
				.setRadius( ( sprite.getWidth( ) / 2.0f ) * Util.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		screwFixture.density = 0.5f;
		screwFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		screwFixture.filter.maskBits = Util.CATEGORY_PLAYER
				| Util.CATEGORY_SUBPLAYER;
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );
	}
}
