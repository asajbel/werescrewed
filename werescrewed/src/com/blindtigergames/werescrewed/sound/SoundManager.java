package com.blindtigergames.werescrewed.sound;

import java.util.EnumMap;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;

public class SoundManager {
	public enum SOUND_TYPE{
		MUSIC,
		SFX,
		SPEECH
	}
	
	public EnumMap<SOUND_TYPE, Float> volume;
	
	protected Camera camera;
	
	public SoundManager( ) {
		volume = new EnumMap<SOUND_TYPE, Float>(SOUND_TYPE.class);
		for (SOUND_TYPE type: SOUND_TYPE.values( )){
			volume.put( type, 1.0f );
		}
		camera = null;
	}

	public class SoundRef{
		SoundMod amplitude;
		SoundMod frequency;
		SoundMod pan;
		SoundManager manager;
		
		Sound sound;
		Music music;
		
		SOUND_TYPE type;
		
		protected SoundRef(SoundManager m, SOUND_TYPE t, String name){
			manager = m;
			type = t;
			amplitude = new SoundMod(this);
			frequency = new SoundMod(this);
			pan = new SoundMod(this);
			pan.flat = 0f;
			sound = null; music = null;
			switch (type){
				case MUSIC:
					music = WereScrewedGame.manager.get( name, Music.class );
				default:
					sound = WereScrewedGame.manager.get( name, Sound.class );
			}
		}
		
		public void play(){
			if (music != null){
				music.setVolume( getVolume() );
				music.play( );
			} else {
				sound.play( getVolume(), getFrequency(), getPan() );
			}
		}
		
		public void loop(boolean v){
			if (music != null){
				music.setLooping( v );
				if (v) {play();}
			} else {
				sound.loop( getVolume(), getFrequency(), getPan() );
			}
		}
				
		public float getVolume(){
			float vol = manager.volume.get( type ) * amplitude.calculate( );
			return Math.min( 1.0f, Math.max( vol, 0f ) );
		}
		
		public float getFrequency(){
			return Math.min( 2f, Math.max( frequency.calculate(), 0.01f));
		}
		
		public float getPan(){
			return Math.min( 1f, Math.max( pan.calculate(), -1f));
		}
		
		public class SoundMod{
			SoundRef ref;
			float dX; //Horizontal distance from camera.
			float dY; //Vertical distance from camera.
			float c;  //Collision force
			float cV; //Camera velocity
			float wV; //World velocity
			float s;  //Screwing speed
			float aV; //Angular velocity
			float flat; //Constant
			
			
			SoundMod(SoundRef nRef){
				ref = nRef;
				dX = 0f;
				dY = 0f;
				c = 0f;
				cV = 0f;
				wV = 0f;
				s = 0f;
				aV = 0f;
				flat = 1f;
			}
			
			public float calculate(){
				return flat;
			}
			
			public float calculate(float DX, float DY, float C, float CV, float WV, float S, float AV){
				return (DX * dX) + (DY * dY) + (C * c) + (CV * cV) + (WV * wV) + (S * s) + (AV * aV) + flat;
			}
		}
	}
}
