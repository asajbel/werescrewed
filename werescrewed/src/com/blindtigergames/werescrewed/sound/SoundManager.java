package com.blindtigergames.werescrewed.sound;

import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.I_Updateable;
import com.blindtigergames.werescrewed.util.ArrayHash;
import com.blindtigergames.werescrewed.util.Util;

public class SoundManager {
	public enum SoundType{
		/* Background music, as you can expect. */
		MUSIC,
		/* Sound effects that are expected to play only a single instance at least semi-constantly, 
		 * and therefore are treated as music internally.*/
		NOISE,
		/* Regular sound effects should be expected to play more than one instance at a time.
		 * Will probably be used for collisions.*/
		SFX,
		/* Still not sure if speech should be implemented as music or sound. We'll have to see
		 * how far we can break up each sound file.*/
		SPEECH
	}
	
	public static EnumMap<SoundType, Float> globalVolume;
	public ArrayHash<String, SoundRef> sounds;
	
	static {
		globalVolume = new EnumMap<SoundType, Float>(SoundType.class);
		for (SoundType type: SoundType.values( )){
			globalVolume.put( type, 1.0f );
		}
	}
	
	protected Camera camera;
	
	public SoundManager( ) {
		sounds = new ArrayHash<String, SoundRef>();
	}

	public SoundRef getSound(String id, String assetName){
		if (!hasSound(id)){
			Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
			sounds.add( id, new SoundRef(s));
		}
		return sounds.get( id );
	}
	
	public SoundRef getSound(String id){
		if (hasSound(id)){
			return sounds.get( id );
		}
		return null;
	}
	
	public boolean hasSound(String tag){
		return sounds.containsKey( tag );
	}
	
	public boolean hasSound(String tag, int index){
		return (sounds.containsKey( tag ) && sounds.getAll( tag ).size > index);
	}

	public void playSound( String id ){ playSound(id, 0, 0f); }
	public void playSound( String id , float delay){ playSound(id, 0, delay); }
	
	public void playSound( String id, int index, float delay ) {
		if (hasSound(id , index)) {
			sounds.get( id, index ).play( delay );
		} else {
			Gdx.app.log( "SoundManager", "No sound loaded for tag: "+id+"/"+index );
		}
	}

	public void loopSound( String id ){ loopSound(id, 0, true); }
	public void loopSound( String id , int index){ loopSound(id, index, true); }
	
	public void loopSound( String id , int index, boolean override) {
		if (hasSound(id, index)) {
			sounds.get( id ).loop( override );
		}
	}

	public void stopSound( String id ) {
		if (hasSound(id)) {
			sounds.get( id ).stop( );
			if (isLooping(id)){
				sounds.get( id ).loopId = -1;
			}
		}
	}

	public boolean isLooping( String id ){
		if (hasSound(id)){
			if (sounds.get(id).loopId >= 0)
				return true;
		}
		return false;
	}
	
	public void setSoundVolume(String id, float v){
		if (hasSound(id)){
			sounds.get(id).volume = (float)(Math.min( 2.0f, Math.max( v, 0.0f) ));
		}
	}
	
	public void handleSoundPosition(String id, Vector2 soundPos, Rectangle cameraBox){
		if (hasSound(id)){
			Vector2 camPos = new Vector2(
					cameraBox.getX(),
					cameraBox.getY()
				);
			Vector2 scale = new Vector2(
					cameraBox.getWidth( ),
					cameraBox.getHeight( )
				);
			float zoom = scale.len( )/Camera.SCREEN_TO_ZOOM;
			Vector2 center = camPos.cpy().add( scale.cpy( ).mul( 0.5f ) );
			Vector3 center3 = new Vector3(
						camPos.x + 0.5f*scale.x,
						camPos.y + 0.5f*scale.y,
						(float)Math.pow( zoom, 2.0f )
			);
			Vector3 sound3 = new Vector3(
					soundPos.x,
					soundPos.y,
					Camera.MIN_ZOOM
			);
			float dist = center3.dst( sound3 );
			float xPan = (float)Math.max( Math.min((Math.pow(center.cpy( ).sub( soundPos ).x/cameraBox.width, 2.0)), 1.0), -1.0);
			float vol = (float)Math.pow( Math.max((1f - dist/sounds.get( id ).range), 0f), sounds.get(id).falloff );
			setSoundVolume(id, vol);
			setSoundPan(id, xPan);
			//Gdx.app.log( "Handle Sound Position", center.toString( )+"->"+soundPos.toString()+"="+dist );
			//Gdx.app.log( "Handle Sound Position", "Pan:"+xPan+" Vol:"+vol );
			sounds.get( id ).update(0.0f);
		}
	}
	
	public void setSoundPitch(String id, float v){
		if (hasSound(id)){
			sounds.get(id).pitch = v;
		}
	}
	public void setSoundPan(String id, float v){
		if (hasSound(id)){
			sounds.get(id).pan = v;
		}
	}
	
	public void setSoundFalloff(String id, float v){
		if (hasSound(id)){
			sounds.get(id).range = v;
		}		
	}
	
	public void update(float dT){
		for (Array<SoundRef> refs : sounds.arrays( )){
			for (SoundRef ref: refs){
				ref.update( dT );
			}
		}
	}
	
	public float getDelay(String id){
		if (hasSound(id)){
			return sounds.get(id).delay;
		}
		return 0.0f;
	}
	
	public boolean isDelayed(String id){
		if (hasSound(id)){
			return sounds.get(id).delay >= SoundRef.DELAY_MINIMUM;
		}
		return false;
	}
	
	public void addDelay(String id, float amount){
		if (hasSound(id)){
			if (hasSound(id)){
				sounds.get(id).delay += amount;
			}			
		}		
	}
	
	public void setDelay(String id, float amount){
		if (hasSound(id)){
			sounds.get(id).delay = amount;
		}
	}
	
	public void delay(String id, float amount){
		if (hasSound(id)){
			if (sounds.get(id).delay < amount){
				sounds.get(id).delay = amount;				
			}
		}
	}
	
	public void copyRefs(SoundManager that){
		for (String name: that.sounds.keySet()){
			sounds.add( name, that.sounds.get(name) );
		}
	}
	
	public static float getSoundVolume(){
		return globalVolume.get( SoundType.SFX );
	}

	public static float getNoiseVolume(){
		return globalVolume.get( SoundType.NOISE );
	}
	
	protected class SoundRef{
		public Sound sound;
		protected Array<Long> soundIds;
		protected long loopId;
		protected float volume;
		protected float pitch;
		protected float pitchVariance;
		protected float pan;
		protected float delay;
		protected float range;
		protected float falloff;
		
		protected static final float DELAY_MINIMUM = 0.0001f;
		
		public SoundRef(Sound s){
			volume = 1.0f;
			pitch = 1.0f;
			pan = 0.0f;
			soundIds = new Array<Long>();
			loopId = -1;
			sound = s;
			range = 500.0f;
			falloff = 2.0f;
		}
		
		protected long play( float delayAmount){
			long id = -1;
			if (delay < DELAY_MINIMUM){
				id = sound.play( getSoundVolume() * volume, pitch, pan);
				soundIds.add( id );
				delay += delayAmount;
			}
			return id;
		}
		
		protected long loop( boolean override ){
			if (override && loopId >= 0){
				sound.stop(loopId);
				loopId = sound.loop( getNoiseVolume() * volume);
			} else if (loopId < 0){
				loopId = sound.loop( getNoiseVolume() * volume);
			}
			return loopId;
		}
		
		protected void stop(){
			sound.stop( );
			loopId = -1;
			delay = 0.0f;
		}
		
		protected void update( float dT ){
			if (loopId >= 0){
				sound.setVolume( loopId, getNoiseVolume() * volume );
				sound.setPitch( loopId, pitch );
				sound.setPan( loopId, pan, volume );
			}
			delay = (float)(Math.max( delay - dT, 0.0f ));
		}
	}
	
}
