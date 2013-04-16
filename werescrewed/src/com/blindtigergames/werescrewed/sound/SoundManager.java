package com.blindtigergames.werescrewed.sound;

import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.I_Updateable;
import com.blindtigergames.werescrewed.util.ArrayHash;

public class SoundManager implements I_Updateable {
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
	public HashMap<String, Sound> sounds;
	public ArrayHash<Sound, Long> soundIds;
	public HashMap<Sound, Long> loopIds;
	
	static {
		globalVolume = new EnumMap<SoundType, Float>(SoundType.class);
		for (SoundType type: SoundType.values( )){
			globalVolume.put( type, 1.0f );
		}
	}
	
	protected Camera camera;
	
	public SoundManager( ) {
		sounds = new HashMap<String, Sound>();
		soundIds = new ArrayHash<Sound, Long>();
		loopIds = new HashMap<Sound, Long>();
	}

	public Sound getSound(String id, String assetName){
		if (!sounds.containsKey( id )){
			Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
			sounds.put( id, s);
		}
		return sounds.get( id );
	}
	
	public Sound getSound(String id){
		if (sounds.containsKey( id )){
			return sounds.get( id );
		}
		return null;
	}
	
	public void update( float deltaTime ){
	}
	
	public boolean hasSound(String tag){
		return sounds.containsKey( tag );
	}

	public void playSound( String id ) {
		if (sounds.containsKey( id )) {
			Sound s = sounds.get( id );
			soundIds.add( s, s.play( getSoundVolume() ) );
		}
	}

	public void loopSound( String id ) {
		if (sounds.containsKey( id )) {
			Sound s = sounds.get( id );
			if (loopIds.containsKey(s)){
				s.stop(loopIds.get( s ));
			}
			loopIds.put(s, s.play( getNoiseVolume() ));
		}
	}

	public float getSoundVolume(){
		return globalVolume.get( SoundType.SFX );
	}

	public float getNoiseVolume(){
		return globalVolume.get( SoundType.NOISE );
	}
	
}
