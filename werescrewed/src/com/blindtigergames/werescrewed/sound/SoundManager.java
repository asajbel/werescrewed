package com.blindtigergames.werescrewed.sound;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.util.ArrayHash;

public class SoundManager implements Disposable {
	public enum SoundType {
		/* Background music, as you can expect. */
		MUSIC,
		/*
		 * Sound effects that are expected to play only a single instance at
		 * least semi-constantly, and therefore are treated as music internally.
		 */
		NOISE,
		/*
		 * Regular sound effects should be expected to play more than one
		 * instance at a time. Will probably be used for collisions.
		 */
		SFX,
		/*
		 * Still not sure if speech should be implemented as music or sound.
		 * We'll have to see how far we can break up each sound file.
		 */
		SPEECH,
		/*
		 * Manually adjust volume.
		 */
		MANUAL
	}
	
	public static EnumMap< SoundType, Float > globalVolume;
	public ArrayHash< String, SoundRef > sounds;
	public static PriorityQueue<SoundRef> loopSounds;
	protected static int maxLoopChannels;
	protected static boolean allowLoopSounds;
	
	static {
		globalVolume = new EnumMap< SoundType, Float >( SoundType.class );
		for ( SoundType type : SoundType.values( ) ) {
			float val = WereScrewedGame.getPrefs( ).getSoundValue( type.name() ); 
			globalVolume.put( type, val );
		}
		maxLoopChannels = 4;
		allowLoopSounds = true;
		loopSounds = new PriorityQueue<SoundRef>(maxLoopChannels, new CompareByVolume());
	}

	public static void updateLoops(){
		int activeLoops = 0;
		for (SoundRef ref: loopSounds){
			if (allowLoopSounds && activeLoops < maxLoopChannels){
				if (ref.loopId < 0){
					ref.loopId = ref.mid.loop( ref.finalVolume * getNoiseVolume(), ref.finalPitch, ref.pan );
				}
				activeLoops++;
			} else {
				if (ref.loopId >= 0){
					ref.stop( ref.loopId );
				}
			}
		}
	}
	
	public static void clearLoops(){
		for (SoundRef ref: loopSounds){
			ref.stop();
		}
		loopSounds.clear( );
	}
	
	public static void setEnableLoops(boolean v){allowLoopSounds = v;}
	
	protected Camera camera;

	public SoundManager( ) {
		sounds = new ArrayHash< String, SoundRef >( );
	}

	public SoundRef getSound( String id, int index, String assetName ) {
		if (index < 0){
			return getSound( id, assetName );
		}
		if ( !hasSound( id , index) ) {
			Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
			SoundRef ref = new SoundRef(s);
			ref.assetName = assetName;
			sounds.set( id, index, ref );
		}
		return sounds.get( id , index );
	}
	
	public SoundRef loadMultiSound(String id, int index, float sDelay, String sName,  float mDelay, String mName, float lDelay, String eName, float eDelay){
		Sound start = WereScrewedGame.manager.get( sName, Sound.class );
		Sound mid = WereScrewedGame.manager.get( mName, Sound.class );
		Sound end = WereScrewedGame.manager.get( eName, Sound.class );
		SoundRef ref = new SoundRef(mid);
		ref.start = start;
		ref.end = end;
		ref.startDelay = sDelay;
		ref.midDelay = mDelay;
		ref.loopDelay = lDelay;
		ref.endDelay = eDelay;
		sounds.put( id, ref );
		return ref;
	}
	
	public SoundRef setSound( String id, int index, String assetName){
		Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
		SoundRef ref = new SoundRef(s);
		ref.assetName = assetName;
		sounds.set( id, index, ref );
		return ref;
	}

	public void clearSounds( String id ){
		if (hasSound(id)){
			for (int i = 0; i < sounds.getAll( id ).size; i++){
				sounds.remove( id, i );
			}
		}
	}
	
	public SoundRef getSound( String id, String assetName ) {
		Sound s = WereScrewedGame.manager.get( assetName, Sound.class );
		SoundRef ref = new SoundRef(s);
		ref.assetName = assetName;
		return sounds.put( id , ref );
	}

	public SoundRef getSound( String id ) {
		if ( hasSound( id ) ) {
			return sounds.get( id );
		}
		return null;
	}
	
	public SoundRef getSound( String tag , int id) {
		if ( hasSound( tag , id ) ) {
			return sounds.get( tag , id );
		}
		return null;
	}
	
	public SoundRef getSoundWithProperties( String line ){
		return getSoundWithProperties(getSoundTags(line));
	}
	
	public SoundRef getSoundWithProperties( HashMap<String, String> subSounds ){
		if (!(subSounds.get( "asset" ).equals( "" ) || subSounds.get("name").equals(""))){
			String name = subSounds.get("name");
			int id = Integer.parseInt( subSounds.get("index") );
			SoundRef sound = getSound( name, id, WereScrewedGame.dirHandle + subSounds.get( "asset" ) );
			if (subSounds.containsKey( "volume" ))
				sound.setInternalVolume(Float.parseFloat( subSounds.get("volume") ));
			if (subSounds.containsKey( "pitch" ))
				sound.setInternalPitch(Float.parseFloat( subSounds.get("pitch") ));
			if (subSounds.containsKey( "pan" ))
				sound.setPan(Float.parseFloat( subSounds.get("pan") ));
			if (subSounds.containsKey( "range" ))
				sound.setRange(Float.parseFloat( subSounds.get("range") ));
			if (subSounds.containsKey( "falloff" ))
				sound.setFalloff(Float.parseFloat( subSounds.get("falloff") ));
			if (subSounds.containsKey( "volumerange" ))
				sound.setVolumeRange(Float.parseFloat( subSounds.get("volumerange") ));
			if (subSounds.containsKey( "pitchrange" ))
				sound.setPitchRange(Float.parseFloat( subSounds.get("pitchrange") ));
			if (name.contains("collision")){
				sound.endDelay = 1.0f;
			}
			return sound;
		}
		return null;
	}
	
	public int randomSoundId( String tag ) {
		return WereScrewedGame.random.nextInt( sounds.getAll( tag ).size );
	}

	public boolean hasSound( String tag ) {
		return sounds.containsKey( tag );
	}

	public boolean hasSound( String tag, int index ) {
		if ( sounds.containsKey( tag ) && sounds.getAll( tag ).size > index ) {
			return true;
		}
		// Gdx.app.log( "SoundManager", "No sounds loaded for tag:"+tag );
		return false;
	}

	public void playSound( String tag ) {
		if ( hasSound( tag ) ) {
			// Gdx.app.log( "SoundManager", "Playing sound "+ index
			// +" out of "+sounds.getAll( id ).size +"." );
			int id = randomSoundId( tag );
			SoundRef ref = getSound(tag, id);
			playSound( tag, id, ref.getDefaultDelay( ), 1f, 1f );
		}
	}

	public void playSound( String id, float delay ) {
		if ( hasSound( id ) ) {
			int index = WereScrewedGame.random
					.nextInt( sounds.getAll( id ).size );
			// Gdx.app.log( "SoundManager", "Playing sound "+ index
			// +" out of "+sounds.getAll( id ).size +"." );
			playSound( id, index, delay, 1f, 1f );
		}
	}

	public void playSound( String id, int index, float delay, float extVol,
			float extPitch ) {
		if ( hasSound( id, index ) ) {
			SoundRef ref = 	sounds.get( id, index );
			ref.endDelay = delay;
			ref.setVolume( extVol );
			ref.setPitch( extPitch );
			ref.play( false );
		} else {
			// Gdx.app.log( "SoundManager",
			// "No sound loaded for tag: "+id+"/"+index );
		}
	}

	public void addSoundToLoops( String id ) {
		addSoundToLoops( id, 0 );
	}

	public void addSoundToLoops( String id, int index ) {
		if ( hasSound( id, index ) ) {
			//sounds.get( id ).loop( override, extVol, extPitch );
			addSoundToLoops(sounds.get( id ));
		}
	}

	public static void addSoundToLoops( SoundRef ref ){
		loopSounds.add( ref );		
	}
	
	public static void removeFromLoops( SoundRef ref ) {
		if (SoundManager.loopSounds.contains(ref)){
			SoundManager.loopSounds.remove(ref);
		}
	}
	public void stopSound( String id ){
		if (hasSound(id)){
			Array<SoundRef> refs = sounds.getAll( id );
			for (int i = 0; i < refs.size; i++){
				stopSound(id, i);
			}
		}
	}
	
	public void stopSound( String id , int index) {
		if ( hasSound( id , index) ) {
			SoundRef ref = sounds.get(id, index);
			ref.stop( );
			if (loopSounds.contains( ref )){
				loopSounds.remove( ref );
			}
		}
	}

	public boolean isLooping( String id ) {
		if ( hasSound( id ) ) {
			if ( sounds.get( id ).looping )
				return true;
		}
		return false;
	}

	public void setSoundVolume( String id, float v ) {
		if ( hasSound( id ) ) {
			sounds.get( id ).setVolume( v );
		}
	}

	public void setSoundInternalVolume( String id, float v ) {
		if ( hasSound( id ) ) {
			sounds.get( id ).setInternalVolume( v );
		}
	}

	public void handleSoundPosition( String id, Vector2 soundPos,
			Rectangle cameraBox ) {
		if ( hasSound( id ) ) {
			SoundRef ref = sounds.get( id );
			float xPan = calculatePositionalPan( soundPos, cameraBox );
			float vol = calculatePositionalVolume( soundPos, cameraBox,
					ref.range, ref.depthFactor, ref.falloff );
			setSoundVolume( id, vol );
			setSoundPan( id, xPan );
			// Gdx.app.log( "Handle Sound Position", center.toString(
			// )+"->"+soundPos.toString()+"="+dist );
			// Gdx.app.log( "Handle Sound Position", "Pan:"+xPan+" Vol:"+vol );
			sounds.get( id ).update( 0.0f );
		}
	}

	public float calculatePositionalVolume( String id, Vector2 soundPos,
			Rectangle cameraBox ) {
		if ( hasSound( id ) ){
			SoundRef ref = sounds.get( id );
			return ref.calculatePositionalVolume( soundPos, cameraBox );
		}
		return 0.0f;
	}

	public static float calculatePositionalVolume( Vector2 soundPos,
			Rectangle cameraBox, float range, float depth, float falloff ) {
		Vector2 camPos = new Vector2( cameraBox.getX( ), cameraBox.getY( ) );
		Vector2 scale = new Vector2( cameraBox.getWidth( ),
				cameraBox.getHeight( ) );
		float zoom = scale.len( ) / Camera.SCREEN_TO_ZOOM;
		Vector3 center3 = new Vector3( camPos.x + 0.5f * scale.x, camPos.y
				+ 0.5f * scale.y, ( float ) Math.pow( zoom * depth, 2.0f ) );
		Vector3 sound3 = new Vector3( soundPos.x, soundPos.y, Camera.MIN_ZOOM );
		float dist = center3.dst( sound3 );
		float vol = ( float ) Math.pow( Math.max( ( 1f - dist / range ), 0f ),
				falloff );
		return vol;
	}

	public static float calculatePositionalPan( Vector2 soundPos,
			Rectangle cameraBox ) {
		Vector2 camPos = new Vector2( cameraBox.getX( ), cameraBox.getY( ) );
		Vector2 scale = new Vector2( cameraBox.getWidth( ),
				cameraBox.getHeight( ) );
		Vector2 center = camPos.cpy( ).add( scale.cpy( ).mul( 0.5f ) );
		float xPan = ( float ) Math.max(
				Math.min(
						( Math.pow( center.cpy( ).sub( soundPos ).x
								/ cameraBox.width, 2.0 ) ), 1.0 ), -1.0 );
		return xPan;
	}

	public void setSoundPitch( String id, float v ) {
		if ( hasSound( id ) ) {
			sounds.get( id ).setPitch( v );
		}
	}

	public void setSoundInternalPitch( String id, float v ) {
		if ( hasSound( id ) ) {
			sounds.get( id ).setInternalPitch( v );
		}
	}

	public void setSoundPan( String id, float v ) {
		if ( hasSound( id ) ) {
			sounds.get( id ).pan = v;
		}
	}

	public void setSoundFalloff( String id, float v ) {
		if ( hasSound( id ) ) {
			sounds.get( id ).range = v;
		}
	}

	public void update( float dT ) {
		for ( Array< SoundRef > refs : sounds.arrays( ) ) {
			for ( SoundRef ref : refs ) {
				ref.update( dT );
			}
		}
	}

	public void dispose( ) {
		for ( Array< SoundRef > refs : sounds.arrays( ) ) {
			for ( SoundRef ref : refs ) {
				ref.stop( );
				if (loopSounds.contains( ref )){
					loopSounds.remove(ref);
				}
			}
		}
	}

	public void copyRefs( SoundManager that ) {
		for ( String name : that.sounds.keySet( ) ) {
			sounds.put( name, that.sounds.get( name ) );
		}
	}

	public Sound getGDXSound( String id, int index ) {
		if ( hasSound( id, index ) ) {
			return sounds.get( id, index ).getSound();
		}
		return null;
	}

	public static float getSoundVolume( ) {
		return globalVolume.get( SoundType.SFX );
	}

	public static float getNoiseVolume( ) {
		return globalVolume.get( SoundType.NOISE );
	}

	public static float getMusicVolume( ) {
		return globalVolume.get( SoundType.MUSIC );
	}

	public float getRange( String id, int index ) {
		return sounds.get( id, index ).range;
	}

	public void setRange( String id, int index, float r ) {
		sounds.get( id, index ).range = r;
	}

	public float getVolumeInRange(String id, int index, float a){
		if (hasSound(id)){
			SoundRef ref = sounds.get( id , index);
			return (1.0f - ref.volumeRange) + (ref.volumeRange * a);
		}
		return 1.0f;
	}
	
	public float getPitchInRange(String id, int index, float a){
		if (hasSound(id)){
			SoundRef ref = sounds.get( id , index);
			return (1.0f - ref.pitchRange) + (ref.pitchRange * a);
		}
		return 1.0f;
	}
	
	public static HashMap<String, String> getSoundTags(String line){
		String[ ] tokens = line.split( "\\s*\\:\\s*" );
		if ( tokens.length >= 2 ) {
			HashMap< String, String > sound = new HashMap< String, String >( );
			sound.put( "name", tokens[0].toLowerCase( ) );
			sound.put( "asset", tokens[ 1 ] );
			sound.put( "index", "-1" );
			String[ ] optTokens;
			for ( int opts = 2; opts < tokens.length; opts++ ) {
				optTokens = tokens[ opts ].toLowerCase( )
						.split( "\\s+" );
				if ( optTokens.length >= 2 ) {
					sound.put( optTokens[ 0 ], optTokens[ 1 ] );
				}
			}
			return sound;
		}
		return null;
	}
	
	public class SoundRef implements Disposable{
		protected Sound mid = null;
		protected Sound start = null;
		protected Sound end = null;
		protected Array< Long > soundIds;
		protected long loopId = -1;
		protected boolean looping = false;
		protected float volume = 1.0f;
		protected float volumeRange = 0.0f;
		protected float pitch = 1.0f;
		protected float pitchRange = 0.0f;
		protected float pan = 0.0f;
		protected float time = 0.0f;
		protected float startDelay = -0.1f;
		protected float midDelay = -0.1f;
		protected float loopDelay = -0.1f;
		protected float endDelay = INITIAL_DELAY;
		protected float range = 500.0f;
		protected float depthFactor = 3.0f;
		protected int state = 4;
		
		protected SoundManager.SoundType type = SoundType.SFX;
		
		protected float falloff = 2.0f;
		protected Vector2 offset = new Vector2(0,0);
		protected String assetName = "dkdc";
		
		protected float finalVolume = 1.0f;
		protected float finalPitch = 1.0f;
		
		public static final float VOLUME_MINIMUM = 0.00001f;
		protected static final float DELAY_MINIMUM = 0.0001f;
		/*
		 * Puts an initial delay on all sounds when they're first loaded. This
		 * is meant to keep collision or idle sounds from playing immediately on
		 * startup.
		 */
		public static final float INITIAL_DELAY = 0.01f;

		protected SoundRef( Sound s ) {
			soundIds = new Array< Long >( );
			mid = s;
			end = s;
			offset = new Vector2(0f,0f);
		}
		
		protected void calculateFinalVolume(float extVol){
			finalVolume = Math.max(
			Math.min( volume * extVol, 1.0f ),
			0.0f );
		}
		
		protected void calculateFinalPitch(float extPitch){
			finalPitch = pitch * extPitch;
		}
		
		public void play( boolean override ) {
			if ( override || state == 0 ) {
				if (looping){
					stop();
				}
				setState(1);
			}
		}
		

		public void loop( boolean override ) {
			if (override || state == 0 || state == 4){
				looping = true;
				setState(1);
			}
		}
		
		protected void stop (){
			stop(true);
		}
		
		public void stop( boolean hard ) {
			if (hard){
				if (start != null)
					start.stop();
				if (mid != null)
					mid.stop();
				if (end != null)
					end.stop();
				loopId = -1;
				setState(0);
			}
			looping = false;
		}
		
		public void stop( long thatId ) {
			if (loopId == thatId){
				mid.stop( loopId );
				loopId = -1;
				setState(0);
				looping = false;
			}
		}
		
		protected void update( float dT ) {
			if (state != 0)
				time += dT;
			if (state == 1 && time > startDelay){//Start Delay
				//Play start sound, if present
				if (start != null){
					start.play(finalVolume, finalPitch, pan);
				}
				setState(2);
			}
			if (state == 2 && time > midDelay){//Mid Delay
				if (looping || loopDelay > 0.0f){
					//If looping, loop middle sound and go to state 3
					SoundManager.addSoundToLoops(this);
					setState(3);
				} else {
					//Else, play end sound if present and go to state 4
					if (end != null){
						end.play(finalVolume, finalPitch, pan);
					}
					setState(4);
				}
			}
			if (state == 3 && (time > loopDelay && !looping)){//Looping/Loop Delay
				if (loopId >= 0){
					SoundManager.removeFromLoops(this);
					mid.stop(loopId);
					loopId = -1;
				}
				if (end != null){
					end.play(finalVolume, finalPitch, pan);
				}
				setState(4);
			}
			if (state == 4 && time > endDelay){
				setState(0);
			}
		}
		
		protected void setState(int s){
			time = 0.0f;
			state = s;
		}
		
		public Sound getSound( ) {
			return mid;
		}

		public void setInternalVolume( float value ) {
			volume = Math.min( Math.max( value, 0.0f ), 1.0f );
		}

		public void setVolume( float extVol ) {
			finalVolume = Math.min( Math.max(volume * extVol , 0.0f) , 1.0f);
			switch (type){
				case MUSIC:
					finalVolume *= getMusicVolume();
					break;
				case SFX:
					finalVolume *= getSoundVolume();
					break;
				case NOISE:
					finalVolume *= getNoiseVolume();
					break;
				default:
			}
			if (loopSounds.contains( this )){
				loopSounds.remove(this);
				loopSounds.add( this );
			}
			if ( loopId >= 0 ) {
				mid.setVolume( loopId, finalVolume);
			}
		}

		public void setType(SoundManager.SoundType t){
			type = t;
		}
		
		public void setVolumeRange(float value){
			volumeRange = value;
		}

		public void setInternalPitch( float value ) {
			pitch = value;
		}

		public void setPitch( float extPitch ) {
			finalPitch = pitch * extPitch;
			if ( loopId >= 0 ) {
				mid.setPitch( loopId,  finalPitch );
			}
		}

		public void setPitchRange(float value){
			pitchRange = value;
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

		public void setOffset( Vector2 vec ){
			offset = vec;
		}
		
		public void setEndDelay( float value ){
			endDelay = value;
		}
		
		public float getDefaultDelay(){
			return endDelay;
		}
		
		public float getFinalVolume(){
			return finalVolume;
		}
		
		public float getFinalPitch(){
			return finalPitch;
		}
		
		public String getAssetName(){
			return assetName;
		}
		
		public void dispose(){
			stop();
			mid.stop( );
			SoundManager.removeFromLoops(this);
		}

		public long getLoopID( ) {
			return loopId;
		}

		public boolean isDelayed( ) {
			return state != 0;
		}

		public float getRange( ) {
			return range;
		}
		public float getDepth( ) {
			return depthFactor;
		}
		public float getFalloff( ) {
			return falloff;
		}

		public int getState( ) {
			return state;
		}
		
		public void setTime(float t){
			time = t;
		}

		public float calculatePositionalVolume( Vector2 position_pixel,
				Rectangle camera_rect ) {
			return SoundManager.calculatePositionalVolume( position_pixel, camera_rect,
					range, depthFactor, falloff );
		}

		public void delay( ) {
			if (state == 0){
				setState(4);
			}
		}
	}
	public static class CompareByVolume implements Comparator<SoundRef>{

		@Override
		public int compare( SoundRef ref1, SoundRef ref2){
			if (ref1.finalVolume <= ref2.finalVolume){
				return 1;
			} else {
				return -1;
			}
		}
		
	}
	public void stopAll( ) {
		for (String tag: sounds.keySet( )){
			this.stopSound( tag );
		}
	}

	public static void stopLoops( ) {
		for (SoundRef ref: loopSounds){
			ref.stop( );
		}
		loopSounds.clear( );
	}

	public void loopSound(String tag){
		loopSound(tag, 0, false);
	}
	
	public void loopSound( String tag , int index, boolean override) {
		if (hasSound (tag, index)){
			sounds.get( tag, index ).loop(override);
		}
	}

	public boolean isDelayed( String string ) {
		// TODO Auto-generated method stub
		return getSound(string).isDelayed();
	}

	public void setDelay( String string, float delay ) {
		if (sounds.containsKey( string )){
			for (SoundRef ref: sounds.getAll( string )){
				ref.endDelay = delay;
			}
		}
	}
	
	public void delaySounds(String string){
		if (sounds.containsKey( string )){
			for (SoundRef ref: sounds.getAll( string )){
				if (ref.getState() == 0)
					ref.setState( 4 );
			}
		}		
	}

	public Array<SoundRef> getAllSounds( String string ) {
		return sounds.getAll( string );
	}
}
