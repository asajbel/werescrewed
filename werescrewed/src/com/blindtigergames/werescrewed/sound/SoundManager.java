package com.blindtigergames.werescrewed.sound;

import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.I_Updateable;

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
	
	public EnumMap<SoundType, Float> volume;
	public EnumMap<SoundType, HashMap<String, SoundRef>> refs;
	
	protected Camera camera;
	
	public SoundManager( ) {
		volume = new EnumMap<SoundType, Float>(SoundType.class);
		refs = new EnumMap<SoundType, HashMap<String, SoundRef>>(SoundType.class);
		for (SoundType type: SoundType.values( )){
			volume.put( type, 1.0f );
			refs.put( type, new HashMap<String, SoundRef>() );
		}
		camera = null;
	}

	public void update( float deltaTime ){
	}
	
	public SoundRef getSound(SoundType t, String n){
		return new SoundRef(this, t, n);
	}
	
	public class SoundRef implements I_Updateable, Disposable {
		public float volume;
		public float frequency;
		public float pan;
		SoundManager manager;
		
		Sound sound;
		Music music;
		boolean looping;
		
		SoundType type;
		
		protected SoundRef(SoundManager m, SoundType t, String name){
			manager = m;
			type = t;
			volume = 1.0f;
			frequency = 1.0f;
			pan = 0.0f;
			looping = false;
			sound = null; music = null;
			switch (type){
				case SPEECH:
				case NOISE:
				case MUSIC:
					music = WereScrewedGame.manager.get( name, Music.class );
					break;
				default:
					sound = WereScrewedGame.manager.get( name, Sound.class );
			}
		}
		
		public long play(){
			if (music != null){
				music.setVolume( getVolume() );
				music.play( );
				return -1;
			} else {
				return sound.play( getVolume(), getFrequency(), getPan() );		
			}
		}
		
		public long loop(){
			if (music != null){
				music.setLooping( true );
			}
			looping = true;
			return this.play();
		}
				
		public float getVolume(){
			float vol = manager.volume.get( type ) * volume;
			return Math.min( 2.0f, Math.max( vol, 0f ) );
		}
		
		public float getFrequency(){
			return Math.min( 2f, Math.max( frequency, 0.01f));
		}
		
		public float getPan(){
			return Math.min( 1f, Math.max( pan, -1f));
		}
		
		public boolean isMusic(){
			return music != null;
		}

		@Override
		public void update( float dT ) {
			if (music != null){
				music.setVolume( getVolume() );
			}
		}

		public long play( float v, float f, float p ) {			
			volume = v;
			frequency = f;
			pan = p;
			return play();
		}

		public long loop( float v) {
			volume = v;
			return loop();
		}

		public long loop( float v, float f, float p ) {
			volume = v;
			frequency = f;
			pan = p;
			return 0;
		}

		public void stop( ) {
			sound.stop( );
			music.stop( );
		}

		public void dispose( ) {
			sound.dispose( );
			music.dispose( );
		}

		public void stop( long soundId ) {
			sound.stop(soundId);
		}

		public void setLooping(boolean l) {
			music.setLooping( l );
			looping = l;
		}

		public void setPitch( long soundId, float pitch ) {
			sound.setPitch( soundId, pitch );
		}

		public void setVolume( long soundId, float volume ) {
			sound.setPan( soundId, soundId, volume );
		}

		public void setPan( long soundId, float pan, float volume ) {
			sound.setPan( soundId, pan, volume );
		}

		public void setPriority( long soundId, int priority ) {
			sound.setPriority(soundId, priority);
		}

		public void pause( ) {
			music.pause( );
		}

		public boolean isPlaying( ) {
			if (isMusic()){return music.isPlaying( );}
			return false;
		}

		public boolean isLooping( ) {
			return looping;
		}

		public void setVolume( float volume ) {
			this.volume = volume;
		}

		public float getPosition( ) {
			if (isMusic()){return music.getPosition( );}
			return 0f;
		}
	}
}
