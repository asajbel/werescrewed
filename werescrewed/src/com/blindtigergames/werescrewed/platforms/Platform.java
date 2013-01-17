package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.mover.IMover;


/**
 * @param name blah blah
 * 
 * @author Ranveer
 *
 */

public class Platform extends Entity{
	
	//List of Structural Screws
	//List of joints

	IMover mover;
	
	protected World world;
	protected int width, height;
	protected boolean dynamicType = false;

	public Platform( String n, Vector2 pos, Texture tex, World world ){
		super( n, pos, tex , null);
		this.world = world;
	}
	

	public Platform(String n, EntityDef d, World w, Vector2 pos, float rot, Vector2 sca) {
		super(n, d, w, pos, rot, sca);
	}


	public void setMover(IMover _mover){
		this.mover = _mover;
	}

	
	public void update(){
		body.setActive(true);
	}
	public void setDensity( float d ){
		body.getFixtureList().get(0).setDensity(d);
	}
	public void setFriction( float f ){
		body.getFixtureList().get(0).setFriction(f);
	}
	public void setRestitution( float r ){
		body.getFixtureList().get(0).setRestitution(r);
	}
	
	public void changeType(){
		dynamicType = !dynamicType;
		if( dynamicType ){
			body.setType( BodyType.DynamicBody );
		}
		else
			body.setType( BodyType.KinematicBody );
		
		body.setActive(false);
	}
	
	//This function sets the platform to 180* no matter what angle it currently is
	public void setHorizontal(){
		body.setTransform( body.getPosition(), (float) Math.toRadians(90) );
	}
	
	//This function sets platform to 90*
	public void setVertical(){
		body.setTransform( body.getPosition(), (float) Math.toRadians(180) );
	}
	
	protected void rotate(){
		body.setAngularVelocity(1f);
	}
	
	protected void rotateBy90(){
		float bodyAngle = body.getAngle();
		body.setTransform( body.getPosition(), bodyAngle + 90 );
	}
}