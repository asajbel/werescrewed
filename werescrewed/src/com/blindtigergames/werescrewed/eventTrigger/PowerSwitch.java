package com.blindtigergames.werescrewed.eventTrigger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.util.Util;


public class PowerSwitch extends EventTrigger{

	private boolean state = false;
	//private static Texture onTex =  WereScrewedGame.manager.get(
	//		WereScrewedGame.dirHandle + "/common/powerswitches/on.png" );
	//private static Texture offTex = WereScrewedGame.manager.get( 
	//		WereScrewedGame.dirHandle + "/common/powerswitches/off.png" );
	
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
		TextureAtlas commonTextureAtlas = WereScrewedGame.manager.getAtlas( "common-textures" );
		onState = commonTextureAtlas.createSprite( "steam_engine_lever_on" );//new Sprite(onTex);
		offState = commonTextureAtlas.createSprite( "steam_engine_lever_off" );//new Sprite(offTex);
		//onState.setOrigin( onState.getWidth( )/2, 32 );
		//offState.setOrigin( offState.getWidth( )/2, 32 );
		addBGDecal( onState );
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
		//float xpos =  body.getPosition( ).x - (32f * Util.PIXEL_TO_BOX);
		//float ypos =  body.getPosition( ).y - (32f * Util.PIXEL_TO_BOX);
		
		Sprite currentSprite = (state)?onState:offState;
		if(currentSprite.equals(bgDecals.get( 0 ))){
			bgDecals.clear( );
			bgDecalAngles.clear( );
			bgDecalOffsets.clear( );
			addBGDecal( currentSprite );
		}
		updateDecals( deltaTime );

		super.draw( batch, deltaTime );
		
		//currentSprite.setPosition( xpos * Util.BOX_TO_PIXEL, ypos * Util.BOX_TO_PIXEL);
		//currentSprite.setRotation(  MathUtils.radiansToDegrees
		//			* body.getAngle( ) );
		
	}	
	
	public boolean isTurnedOn(){
		return state;
	}
	
	public void setState( boolean boo ){
		state = boo;
	}
}



