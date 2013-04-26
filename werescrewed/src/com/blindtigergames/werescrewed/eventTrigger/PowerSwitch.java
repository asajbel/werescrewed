package com.blindtigergames.werescrewed.eventTrigger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.util.Util;


public class PowerSwitch extends EventTrigger{

	private boolean state = false;
	private Texture onTex =  WereScrewedGame.manager.get(
			WereScrewedGame.dirHandle + "/common/powerswitches/on.png" );
	private Texture offTex = WereScrewedGame.manager.get( 
			WereScrewedGame.dirHandle + "/common/powerswitches/off.png" );
	
	private Sprite onState, offState;
	/**
	 * creates a PowerSwitch at location position
	 * 
	 * @param name String
	 * @param position Vector2
	 * @param world World
	 */
	public PowerSwitch( String name, Vector2 position, World world ) {
		super( name, world );
		contructRectangleBody(64, 64, position);
		entityType = EntityType.POWERSWITCH;
		
		onState = new Sprite(onTex);
		offState = new Sprite(offTex);
		
		
	}
	
	public void doAction(){
		if(repeatable){
			if(state == false){

				runBeginAction();
				state = true;
			}
			else{

				runEndAction();
				state = false;
			}
		}else{
			if(!this.beginTriggeredOnce){
				runBeginAction();
				state = true;
			}
		}

	
		
	}
	
	@Override
	public void draw(SpriteBatch batch, float deltaTime){
		float xpos =  body.getPosition( ).x - (32f * Util.PIXEL_TO_BOX);
		float ypos =  body.getPosition( ).y - (32f * Util.PIXEL_TO_BOX);
		

		if(state){
			onState.setOrigin( 32f, 32f);
			onState.setPosition( xpos * Util.BOX_TO_PIXEL, ypos * Util.BOX_TO_PIXEL);
			onState.setRotation(  MathUtils.radiansToDegrees
					* body.getAngle( ) );
			onState.draw( batch );
		}else{
			offState.setOrigin( 32f, 32f);
			offState.setPosition( xpos * Util.BOX_TO_PIXEL, ypos * Util.BOX_TO_PIXEL);
			offState.setRotation(  MathUtils.radiansToDegrees
					* body.getAngle( ) );
			offState.draw( batch );
		}
		
	}
	
	public boolean isTurnedOn(){
		return state;
	}
}



