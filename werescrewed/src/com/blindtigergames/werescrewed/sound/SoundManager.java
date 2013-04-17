package com.blindtigergames.werescrewed.sound;

import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
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

	public void playSound( String id ) {
		if (hasSound(id)) {
			sounds.get( id ).play( );
		}
	}

	public void loopSound( String id ) {
		if (hasSound(id)) {
			sounds.get( id ).loop( );
		}
	}

	public void setSoundVolume(String id, float v){
		if (hasSound(id)){
			sounds.get(id).volume = v;
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
		
		public SoundRef(Sound s){
			volume = 1.0f;
			pitch = 1.0f;
			pan = 0.0f;
			soundIds = new Array<Long>();
			loopId = -1;
			sound = s;
		}
		
		protected long play(){
			long id = sound.play( getSoundVolume() * volume, pitch, pan);
			soundIds.add( id );
			return id;
		}
		
		protected long loop(){
			if (loopId >= 0){
				sound.stop(loopId);
			}
			loopId = sound.loop( getNoiseVolume() * volume);
			return loopId;
		}
		
		protected void stop(){
			sound.stop( );
			loopId = -1;
		}
		
		protected void update(){
			if (loopId >= 0){
				sound.setVolume( loopId, getNoiseVolume() * volume );
				sound.setPitch( loopId, pitch );
				sound.setPan( loopId, pan, volume );
			}
		}
	}
	
}
