package com.blindtigergames.werescrewed.sound;

import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.I_Updateable;
import com.blindtigergames.werescrewed.util.ArrayHash;

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
	public HashMap<String, SoundRef> sounds;
	
	static {
		globalVolume = new EnumMap<SoundType, Float>(SoundType.class);
		for (SoundType type: SoundType.values( )){
			globalVolume.put( type, 1.0f );
		}
	}
	
	protected Camera camera;
	
	public SoundManager( ) {
		sounds = new HashMap<String, SoundRef>();
	}

	public SoundRef getSound(String id, String assetName){
		if (!hasSound(id)){
			Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
			sounds.put( id, new SoundRef(s));
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

	public void playSound( String id ){
		playSound(id, 0.0f);
	}
	
	public void playSound( String id, float delay ) {
		if (hasSound(id)) {
			sounds.get( id ).play( delay );
		}
	}

	public void loopSound( String id ){
		loopSound(id, true);
	}
	
	public void loopSound( String id , boolean override) {
		if (hasSound(id)) {
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
	
	public void handleSoundPosition(String id, Vector2 cameraPos, Vector2 soundPos, float zoom){
		if (hasSound(id)){
			float dist = soundPos.dst( cameraPos )*zoom;
			float xPan = soundPos.sub( cameraPos ).nor().x;
			float vol = sounds.get(id).falloff/(dist*dist);
			setSoundVolume(id, vol);
			setSoundPan(id, xPan);
			Gdx.app.log( "Handle Sound Position", "Dist:"+dist+" Vol:"+vol+" Pan:"+xPan );
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
			sounds.get(id).falloff = v;
		}		
	}
	
	public void update(float dT){
		for (SoundRef ref : sounds.values( )){
			ref.update( dT );
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
		protected float pan;
		protected float delay;
		protected float falloff;
		
		protected static final float DELAY_MINIMUM = 0.0001f;
		
		public SoundRef(Sound s){
			volume = 1.0f;
			pitch = 1.0f;
			pan = 0.0f;
			soundIds = new Array<Long>();
			loopId = -1;
			sound = s;
			falloff = 1.0f;
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
