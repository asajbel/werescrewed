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
	
	
	
	public ShapePlatform(String n, Vector2 pos, Texture tex, World world, Shapes shape, 
			float scale, boolean flip ){
		super( n, pos, tex , null);
		this.world = world;
		
		switch(shape){
		case rhombus:
			constructRhombus( pos, scale, flip );
			break;
		case cross:
			System.out.println("trap");
			break;
		case plus:
			System.out.println("trap");
			break;
		case trapezoid:
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
	
	public void constructRhombus(Vector2 pos, float scale, boolean flip){
		
		
		BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.KinematicBody;
        groundBodyDef.position.set(new Vector2( pos.x ,pos.y ));  
        body = world.createBody( groundBodyDef );  

		Vector2[] vertices = new Vector2[4];
		Vector2 point1;
		Vector2 point2;
		Vector2 point3;
		Vector2 point4;
		float ptb = GameScreen.PIXEL_TO_BOX;
		point1 = new Vector2( 0.0f, 0.0f );
		point2 = new Vector2( 32.0f * ptb, 0.0f );
		if(!flip){
			point3 = new Vector2( 48.0f * ptb, 32.0f * ptb );
			point4 = new Vector2( 16.0f * ptb, 32.0f * ptb );
		} else {
			point3 = new Vector2( 16.0f * ptb, 32.0f * ptb );
			point4 = new Vector2( -16.0f * ptb, 32.0f * ptb );
		}
		
		/*		Vector2 point1 = new Vector2( 0.0f, 0.0f );
		Vector2 point2 = new Vector2( 1.0f, 0.0f );
		Vector2 point3 = new Vector2( 1.5f, 1.0f );
		Vector2 point4 = new Vector2( 0.5f, 1.0f );
		*/
		
		vertices[0] = point1.mul(scale);
		vertices[1] = point2.mul(scale);
		vertices[2] = point3.mul(scale);
		vertices[3] = point4.mul(scale);
		
		//vertices[0] = new Vector2( 0.0f, 0.0f );
		//vertices[1] = new Vector2( 1.0f, 0.0f );
		//vertices[2] = new Vector2( 1.5f, 1.0f );
		//vertices[3] = new Vector2( 0.5f, 1.0f );
		//Vector2 z = new Vector2();

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
	
	public void update(){
		super.update();
	}
}