package com.blindtigergames.werescrewed.screws;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @descrip: blah blah
 * 
 * @author Dennis
 *
 */

public class PuzzleScrew extends Screw {
	private int maxDepth;
	public PuzzleScrew( String n, Vector2 pos, Texture tex, int max ){
		super( n, pos, tex);
		maxDepth = max;
		depth = max;
		
		//create the screw body
	    BodyDef screwBodyDef = new BodyDef();
	    screwBodyDef.type = BodyType.StaticBody;
	    screwBodyDef.position.set(pos);
	    body = world.createBody(screwBodyDef);
	    CircleShape screwShape = new CircleShape();
	    screwShape.setRadius(sprite.getWidth());
	    body.createFixture(screwShape,0.0f);
	    screwShape.dispose();
		sprite.setScale(GameScreen.PIXEL_TO_BOX);
	    
		//add radar sensor to screw
		CircleShape radarShape = new CircleShape();
		radarShape.setRadius(sprite.getWidth()*2);
	    FixtureDef radarFixture = new FixtureDef();
	    radarFixture.shape = radarShape;
	    radarFixture.isSensor = true;
	    radarFixture.filter.categoryBits = CATEGORY_SCREWS; // category of Screw Radar...
	    radarFixture.filter.maskBits = 0x0001;//radar only collides with player (player category bits 0x0001)
		body.createFixture(radarFixture);
	}
	
	public void update() {
		if ( depth < 0 ) {
			depth = 0;
		} else if ( depth > maxDepth ) {
			depth = maxDepth;
		}
	}

}
