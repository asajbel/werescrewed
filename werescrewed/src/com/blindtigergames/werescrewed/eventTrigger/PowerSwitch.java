package com.blindtigergames.werescrewed.eventTrigger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.util.Util;

public class PowerSwitch extends EventTrigger {

	private boolean state = false;

	private Sprite onState, offState;

	/**
	 * creates a PowerSwitch at location position
	 * 
	 * @param name
	 *            String
	 * @param position
	 *            Vector2
	 * @param world
	 *            World
	 */
	public PowerSwitch( String name, Vector2 position, World world ) {
		super( name, world );
		contructRectangleBody( 128, 128, position );
		entityType = EntityType.POWERSWITCH;
		TextureAtlas commonTextureAtlas = WereScrewedGame.manager
				.getAtlas( "common-textures" );
		onState = commonTextureAtlas.createSprite( "switch_on" );
		offState = commonTextureAtlas.createSprite( "switch_off" );
		this.sprite = offState;
		onState.setOrigin( onState.getWidth( ) / 2, onState.getHeight( ) / 2 );
		offState.setOrigin( offState.getWidth( ) / 2, offState.getHeight( ) / 2 );
		loadSounds();
	}

	public void doAction( ) {
		if ( repeatable ) {
			if ( state == false ) {

				runBeginAction( null );
				state = true;
				sounds.playSound( "on" );
			} else {
				runEndAction( );
				state = false;
				sounds.playSound( "off" );
			}
		} else {
			if ( !this.beginTriggeredOnce ) {
				runBeginAction( null );
				state = true;
				sounds.playSound( "on" );
			}
		}

	}
	
	@Override
	public void draw( SpriteBatch batch, float deltaTime ) {
		update(deltaTime);
		float xpos = body.getPosition( ).x;
		float ypos = body.getPosition( ).y - ( 64f * Util.PIXEL_TO_BOX );

		sprite = ( state ) ? onState : offState;
		// if(currentSprite.equals(bgDecals.get( 0 ))){
		// bgDecals.clear( );
		// bgDecalAngles.clear( );
		// bgDecalOffsets.clear( );
		// addBGDecal( currentSprite );
		// }
		// updateDecals( deltaTime );

		sprite.setPosition( xpos * Util.BOX_TO_PIXEL - sprite.getWidth( )
				/ 2.0f, ypos * Util.BOX_TO_PIXEL );
		sprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );

		super.draw( batch, deltaTime );

	}
	
	public boolean isTurnedOn( ) {
		return state;
	}

	public void setState( boolean boo ) {
		state = boo;
	}
	
	public void loadSounds(){
		if (sounds == null)
			sounds = new SoundManager();
		sounds.getSound( "on" , WereScrewedGame.dirHandle + "/common/sounds/switchOn.ogg");
		sounds.getSound( "off" , WereScrewedGame.dirHandle + "/common/sounds/switchOff.ogg");
	}
}
