package com.blindtigergames.werescrewed.entity.hazard;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.graphics.particle.ParticleEffect;
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
	private int maxConcurrentFixtures;
	
	//iteration variables
	private int currStep;
	private float stepAccum;
	
	private Array< Fixture > fixtureList;
	
	
	/**
	 * Mouth fire object for dragon level.
	 * @param name
	 * @param pos source of fire
	 * @param destinationPix fire will go to get here
	 * @param secondsToComplete 1s is quick
	 * @param startHeightPix at source of fire, how tall will it be
	 * @param endHeightPix at end of fire, max height it reaches
	 * @param world
	 */
	public MouthFire( String name, Vector2 pos, Vector2 destinationPix, float secondsToComplete, float startHeightPix, float endHeightPix, World world) {
		super( name, pos, null, world, true );
		
		float startHeightM = 10*Util.PIXEL_TO_BOX, endHeightM = 600*Util.PIXEL_TO_BOX;
		Vector2 posMeter = pos.cpy( ).mul( Util.PIXEL_TO_BOX );
		Vector2 destinationM = destinationPix.cpy().mul(Util.PIXEL_TO_BOX);
		this.angle = Util.angleBetweenPoints( posMeter, destinationM );
		this.totalSteps = 20;
		this.posStep = destinationM.cpy( ).sub( posMeter );
		this.totalLength = posStep.len( );
		this.widthMeter = totalLength/totalSteps/2; //because set as box doubles width
		posStep = posStep.nor( ).mul( widthMeter*2 );
		
		this.heightMStep = (endHeightM-startHeightM)/totalSteps;
		
		this.currStep = 0;
		this.stepAccum = 0;
		this.stepTime = secondsToComplete/totalSteps;
		
		this.activeHazard = false;
		
		this.fixtureList = new Array< Fixture >(totalSteps);
		
		this.maxConcurrentFixtures = totalSteps/3;
		//Gdx.app.log( "posStep", posStep.toString( )+", maxFix:"+this.maxConcurrentFixtures );
	
		constructBody( posMeter );
		
		float milliComplete = secondsToComplete * 1000; 
		float emitLength = widthMeter * 2 * maxConcurrentFixtures;
		int emitTime = (int) (milliComplete * (emitLength / totalLength));

		addFrontParticleEffect( "mouth_fire", false, false ); 
		ParticleEffect e = getEffect( "mouth_fire" ); 
		e.setDuration( emitTime - 175 ); 
		e.setPosition( pos.x, pos.y );
		e.setLifeTime( milliComplete - 300, 500 );
		e.setVelocity( (totalLength*Util.BOX_TO_PIXEL-50)/secondsToComplete, 50 );
		e.setSize( endHeightPix / 3, 0 );
		float angleDiff = (float) Math.atan( endHeightPix/(totalLength*Util.BOX_TO_PIXEL) ); 
		e.setAngleDiff( angleDiff, 0 ); 
		float ang = angle - (float) Math.PI / 2; 
		e.setEffectAngle( ang );
	}
	
	/**
	 * For debug
	 * @param name
	 * @param pos
	 * @param secondsToComplete
	 * @param world
	 */
	public MouthFire( String name, Vector2 pos, float secondsToComplete, World world ){
		this( name, pos, pos.cpy().add(600,600), secondsToComplete, 10, 600, world );
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
		
		if(fixtureList.size > maxConcurrentFixtures) {
			body.destroyFixture( fixtureList.first( ) );
			fixtureList.removeIndex( 0 );
		}
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
		getEffect("mouth_fire").reset( ); 
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
