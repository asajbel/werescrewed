package com.blindtigergames.werescrewed.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Player.PlayerState;
import com.blindtigergames.werescrewed.screens.GameScreen;

/**
 * @param name blah blah
 * 
 * @author Ranveer
 *
 */

public class ShapePlatform extends Platform{
	
	
	
	public ShapePlatform(String n, Vector2 pos, Texture tex, World world, Shapes shape, float scale ){
		super( n, pos, tex , null);
		this.world = world;
		
		switch(shape){
		case trapezoid:
			constructTrapezoid( pos, scale );
			break;
		case cross:
			System.out.println("trap");
			break;
		case plus:
			System.out.println("trap");
			break;
		case rhombus:
			System.out.println("trap");
			break;
		case Lshaped:
			System.out.println("trap");
			break;
		case Tshaped:
			System.out.println("trap");
			break;
		case dumbbell:
			System.out.println("trap");
			break;
		
		}
	}
	
	//constuct bodies for each shape
	//trapezoid, cross, plus, rhombus, Lshaped, Tshaped, dumbbell
	
	public void constructTrapezoid(Vector2 pos, float scale){
		//ArrayList<Vector2> vectices = new ArrayList<Vector2>();
		
		BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.KinematicBody;
        groundBodyDef.position.set(new Vector2(pos.x * GameScreen.PIXEL_TO_BOX, 
	        		              pos.y * GameScreen.PIXEL_TO_BOX));  
        body = world.createBody( groundBodyDef );  

		Vector2[] vertices = new Vector2[4];
		Vector2 point1 = new Vector2( 0.0f, 0.0f );
		Vector2 point2 = new Vector2( 1.0f, 0.0f );
		Vector2 point3 = new Vector2( 1.5f, 1.0f );
		Vector2 point4 = new Vector2( 0.5f, 1.0f );
		vertices[0] = point1.mul(scale);
		vertices[1] = point2.mul(scale);
		vertices[2] = point3.mul(scale);
		vertices[3] = point4.mul(scale);

		Vector2 z = new Vector2();

		PolygonShape polygon = new PolygonShape();
		polygon.set(vertices);
		
		FixtureDef platformFixtureDef = new FixtureDef();
		platformFixtureDef.shape = polygon;
		platformFixtureDef.density = 1.9f;
		platformFixtureDef.friction = 0.5f;
		platformFixtureDef.restitution = 0.0f;
		body.setGravityScale(.1f);
		body.createFixture(platformFixtureDef);
	}
}