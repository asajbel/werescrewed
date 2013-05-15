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

	public SoundRef getSound(String id, int index, String assetName){
		if (!hasSound(id)){
			Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
			sounds.set( id, index, new SoundRef(s));
		}
		return sounds.get( id );
	}
	
	public SoundRef getSound(String id, String assetName){
		Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
		return sounds.add( id, new SoundRef(s));
	}
	
	public SoundRef getSound(String id){
		if (hasSound(id)){
			return sounds.get( id );
		}
		return null;
	}
	
	public int randomSoundId(String tag){
		return WereScrewedGame.random.nextInt( sounds.getAll( tag ).size );
	}
	
	public boolean hasSound(String tag){
		return sounds.containsKey( tag );
	}
	
	public boolean hasSound(String tag, int index){
		if (sounds.containsKey( tag ) && sounds.getAll( tag ).size > index){
			return true;
		}
		//Gdx.app.log( "SoundManager", "No sounds loaded for tag:"+tag );
		return false;
	}

	public void playSound( String id ){ 
		if (hasSound(id)){
			//Gdx.app.log( "SoundManager", "Playing sound "+ index +" out of "+sounds.getAll( id ).size +"." );
			playSound(id, randomSoundId(id), 0f, 1f, 1f); 
		}
	}
	
	public void playSound( String id , float delay){
		if (hasSound(id)){
			int index = WereScrewedGame.random.nextInt( sounds.getAll( id ).size );
			//Gdx.app.log( "SoundManager", "Playing sound "+ index +" out of "+sounds.getAll( id ).size +"." );
			playSound(id, index, delay, 1f, 1f); 
		}
	}
	
	public void playSound( String id, int index, float delay , float extVol, float extPitch) {
		if (hasSound(id , index)) {
			sounds.get( id, index ).play( delay, extVol, extPitch );
		} else {
			Gdx.app.log( "SoundManager", "No sound loaded for tag: "+id+"/"+index );
		}
	}

	public void loopSound( String id ){ loopSound(id, 0, true, 1.0f, 1.0f); }
	public void loopSound( String id , int index){ loopSound(id, index, true, 1.0f, 1.0f); }
	
	public void loopSound( String id , int index, boolean override, float extVol, float extPitch) {
		if (hasSound(id, index)) {
			sounds.get( id ).loop( override , extVol, extPitch);
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
			sounds.get(id).setVolume( v );
		}
	}
	
	public void setSoundInternalVolume(String id, float v){
		if (hasSound(id)){
			sounds.get(id).setInternalVolume( v );
		}
	}
	
	public void handleSoundPosition(String id, Vector2 soundPos, Rectangle cameraBox){
		if (hasSound(id)){
			float xPan = calculatePositionalPan(soundPos, cameraBox);
			float vol = calculatePositionalVolume(soundPos, cameraBox, sounds.get( id ).range, sounds.get( id ).falloff);
			setSoundVolume(id, vol);
			setSoundPan(id, xPan);
			//Gdx.app.log( "Handle Sound Position", center.toString( )+"->"+soundPos.toString()+"="+dist );
			//Gdx.app.log( "Handle Sound Position", "Pan:"+xPan+" Vol:"+vol );
			sounds.get( id ).update(0.0f);
		}
	}
	
	public float calculatePositionalVolume(String id, Vector2 soundPos, Rectangle cameraBox){
		if (hasSound(id))
			return calculatePositionalVolume(soundPos, cameraBox, sounds.get( id ).range, sounds.get(id).falloff);
		return 0.0f;
	}
	
	public static float calculatePositionalVolume(Vector2 soundPos, Rectangle cameraBox, float range, float falloff){
		Vector2 camPos = new Vector2(
				cameraBox.getX(),
				cameraBox.getY()
			);
		Vector2 scale = new Vector2(
				cameraBox.getWidth( ),
				cameraBox.getHeight( )
			);
		float zoom = scale.len( )/Camera.SCREEN_TO_ZOOM;
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
		float vol = (float)Math.pow( Math.max((1f - dist/range), 0f), falloff );
		return vol;		
	}
	
	public static float calculatePositionalPan(Vector2 soundPos, Rectangle cameraBox){
		Vector2 camPos = new Vector2(
				cameraBox.getX(),
				cameraBox.getY()
			);
		Vector2 scale = new Vector2(
				cameraBox.getWidth( ),
				cameraBox.getHeight( )
			);
		Vector2 center = camPos.cpy().add( scale.cpy( ).mul( 0.5f ) );
		float xPan = (float)Math.max( Math.min((Math.pow(center.cpy( ).sub( soundPos ).x/cameraBox.width, 2.0)), 1.0), -1.0);
		return xPan;
	}
	
	public void setSoundPitch(String id, float v){
		if (hasSound(id)){
			sounds.get(id).setPitch( v );
		}
	}
	
	public void setSoundInternalPitch(String id, float v){
		if (hasSound(id)){
			sounds.get(id).setInternalPitch( v );
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
	public void dispose(){
		for (Array<SoundRef> refs : sounds.arrays( )){
			for (SoundRef ref: refs){
				ref.stop( );
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
				for (SoundRef sound : sounds.getAll(id)){
					sound.delay += amount;
				}
			}			
		}		
	}
	
	public void setDelay(String id, float amount){
		if (hasSound(id)){
			for (SoundRef sound : sounds.getAll(id)){
				sound.delay = amount;
			}
		}
	}
	
	public void delay(String id, float amount){
		if (hasSound(id)){
			if (sounds.get(id).delay < amount){
				for (SoundRef sound : sounds.getAll(id)){
					sound.delay = amount;
				}				
			}
		}
	}
	
	public void copyRefs(SoundManager that){
		for (String name: that.sounds.keySet()){
			sounds.add( name, that.sounds.get(name) );
		}
	}

	public Sound getGDXSound(String id, int index){
		if (hasSound(id, index)){
			return sounds.get(id, index).sound;
		}
		return null;
	}
	
	public static float getSoundVolume(){
		return globalVolume.get( SoundType.SFX );
	}

	public static float getNoiseVolume(){
		return globalVolume.get( SoundType.NOISE );
	}
	public static float getMusicVolume( ) {
		return globalVolume.get( SoundType.MUSIC);
	}

	public float getRange( String id, int index ) {
		return sounds.get( id, index ).range;
	}	

	public void setRange( String id, int index , float r) {
		sounds.get( id, index ).range = r;
	}	

	public class SoundRef{
		public Sound sound;
		protected Array<Long> soundIds;
		protected long loopId;
		protected float volume;
		protected float volumeRange;
		protected float pitch;
		protected float pitchRange;
		protected float pan;
		protected float delay;
		protected float range;
		protected float falloff;
		protected Vector2 offset;
		protected static final float DELAY_MINIMUM = 0.0001f;
		/*
		 * Puts an initial delay on all sounds when they're first loaded.
		 * This is meant to keep collision or idle sounds from playing immediately on startup.
		 */
		public static final float INITIAL_DELAY = 0.1f;
		
		protected SoundRef(Sound s){
			volume = 1.0f;
			volumeRange = 0.0f;
			pitch = 1.0f;
			pitchRange = 0.0f;
			pan = 0.0f;
			delay = INITIAL_DELAY;
			soundIds = new Array<Long>();
			loopId = -1;
			sound = s;
			range = 500.0f;
			falloff = 2.0f;
		}
		
		protected long play( float delayAmount, float extVol, float extPitch){
			long id = -1;
			if (delay < DELAY_MINIMUM){
				float finalVol = Math.max( Math.min(getSoundVolume() * volume * extVol, 1.0f) , 0.0f);
				float finalPitch = pitch * extPitch;
				id = sound.play( finalVol, finalPitch, pan);
				soundIds.add( id );
				delay += delayAmount;
			}
			return id;
		}
		
		protected long loop( boolean override , float extVol, float extPitch){
			if (override && loopId >= 0){
				sound.stop(loopId);
				loopId = sound.loop( getNoiseVolume() * volume * extVol);
			} else if (loopId < 0){
				loopId = sound.loop( getNoiseVolume() * volume * extVol);
			}
			setVolume(extVol);
			setPitch(extPitch);
			return loopId;
		}
		
		protected void stop(){
			sound.stop( );
			loopId = -1;
			delay = 0.0f;
		}
		
		protected void update( float dT ){
			delay = (float)(Math.max( delay - dT, 0.0f ));
		}
		
		public Sound getSound(){
			return sound;
		}

		public void setInternalVolume( float value ) {
			volume = Math.min( Math.max(value, 0.0f), 1.0f );
		}
		
		public void setVolume( float extVol ){
			if (loopId >= 0){
				sound.setVolume( loopId, getNoiseVolume() * volume * extVol);
			}
		}

		public void setInternalPitch( float value ) {
			pitch = value;
		}

		public void setPitch( float extPitch ){
			if (loopId >= 0){
				sound.setPitch( loopId, pitch * extPitch);
			}
		}

		public void setPan( float value ) {
			pan = value;
		}
		
		public void setRange( float value ) {
			range = value;
		}

		public void setFalloff( float value ) {
			falloff = value;
		}

	}
}
