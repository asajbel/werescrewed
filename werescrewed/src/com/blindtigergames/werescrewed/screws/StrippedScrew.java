package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * blah blah
 * 
 * @author Dennis
 * 
 */

public class StrippedScrew extends Screw {
	public StrippedScrew( String name, Vector2 pos, Skeleton skeleton,
			World world ) {
		super( name, pos, null );
		this.world = world;

		sprite.setColor( Color.ORANGE );
		sprite.setOrigin( 0.0f, 0.0f );
		// create the screw body
		BodyDef screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.DynamicBody;
		screwBodyDef.position.set( pos );
		screwBodyDef.gravityScale = 0.07f;
		body = world.createBody( screwBodyDef );
		CircleShape screwShape = new CircleShape( );
		screwShape.setRadius( ( sprite.getWidth( ) / 2.0f )
				* GameScreen.PIXEL_TO_BOX );
		FixtureDef screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = CATEGORY_SCREWS;
		screwFixture.filter.maskBits = 0x0001 | 0x0002;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		screwShape.dispose( );
		body.setUserData( this );

		// add radar sensor to screw
		CircleShape radarShape = new CircleShape( );
		radarShape.setRadius( sprite.getWidth( ) * 1.25f
				* GameScreen.PIXEL_TO_BOX );
		FixtureDef radarFixture = new FixtureDef( );
		radarFixture.shape = radarShape;
		radarFixture.isSensor = true;
		radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw
															// Radar...
		radarFixture.filter.maskBits = 0x0001 | 0x0002;// radar collides with
														// player 1 & 2
		body.createFixture( radarFixture );
		System.out.print( this.getClass( ) + ": " );
		if ( body != null )
			System.out.print( "Body center - " + this.body.getWorldCenter( )
					+ ", " );
		else
			System.out.print( "No body, " );
		if ( sprite != null ) {
			float centerX = this.sprite.getOriginX( );
			centerX *= GameScreen.PIXEL_TO_BOX;
			centerX += this.sprite.getX( );

			float centerY = this.sprite.getOriginY( );
			centerY *= GameScreen.PIXEL_TO_BOX;
			centerY += this.sprite.getY( );
			System.out.println( "Sprite center - " + "[" + centerX + ":"
					+ centerY + "]" );
		} else
			System.out.println( "No sprite" );
	}

	@Override
	public void screwLeft( ) {
	}

	@Override
	public void screwRight( ) {
	}

	private RevoluteJoint platformToScrew;
}
