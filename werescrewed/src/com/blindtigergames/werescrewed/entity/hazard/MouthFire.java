package com.blindtigergames.werescrewed.entity.hazard;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.util.Util;

public class MouthFire extends Hazard {

	private float totalLength;
	private Vector2 posStep;
	//private Vector2 destination;
	private float angle;
	private float stepTime;
	private float heightMStep;
	private float widthMeter;
	private int totalSteps;
	
	//iteration variables
	private int currStep;
	private float stepAccum;
	
	private Array< Fixture > fixtureList;
	
	
	
	public MouthFire( String name, Vector2 pos, float secondsToComplete, World world) {
		super( name, pos, null, world, true );
		
		float startHeight = 10*Util.PIXEL_TO_BOX, endHeight = 600*Util.PIXEL_TO_BOX;
		Vector2 posMeter = pos.cpy( ).mul( Util.PIXEL_TO_BOX );
		Vector2 destinationM = posMeter.cpy().add(600*Util.PIXEL_TO_BOX,600*Util.PIXEL_TO_BOX);
		this.angle = Util.angleBetweenPoints( posMeter, destinationM );
		this.totalSteps = 20;
		int stepLength;
		this.posStep = destinationM.cpy( ).sub( posMeter );
		this.totalLength = posStep.len( );
		this.widthMeter = totalLength/totalSteps/2; //because set as box doubles width
		posStep = posStep.nor( ).mul( widthMeter*2 );
		this.heightMStep = (endHeight-startHeight)/totalSteps;
		
		
		this.currStep = 0;
		this.stepAccum = 0;
		this.stepTime = secondsToComplete/totalSteps;
		
		this.activeHazard = false;
		
		this.fixtureList = new Array< Fixture >(totalSteps);
		
		constructBody( posMeter );
		
	}
	
	private void constructBody( Vector2 positionM ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( positionM.cpy() );
		this.body = world.createBody( bodyDef );
		
		this.body.setUserData( this );
	}
	
	
	private void addFixture(){
		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( widthMeter, heightMStep*currStep, posStep.cpy( ).mul( currStep ), angle );
		
		FixtureDef fixture = new FixtureDef( );
		fixture.isSensor = true;
		fixture.filter.categoryBits = Util.CATEGROY_HAZARD;
		fixture.filter.maskBits = Util.CATEGORY_PLAYER;
		fixture.shape = polygon;
		
		fixtureList.add( this.body.createFixture( fixture ) );
		
		polygon.dispose( );
	}
	
	@Override
	public void update(float deltaTime){
		super.update( deltaTime );
		if(activeHazard){
			stepAccum += deltaTime;
			if(stepAccum >= stepTime){
				stepAccum=0;
				++currStep;
				addFixture( );
				if(currStep>totalSteps){
					resetFire();
				}
			}
		}
	}
	
	@Override
	public void setActiveHazard(boolean isActive){
		super.setActiveHazard( isActive );
		if(!activeHazard){
			resetFire( );
		}
	}
	
	private void resetFire(){
		activeHazard=false;
		currStep = 0;
		for ( Fixture f : fixtureList ) {
			//f.getFilterData( );
			body.destroyFixture( f );
		}
		fixtureList.clear( );
	}
}
