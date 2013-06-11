package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundRef;
import com.blindtigergames.werescrewed.util.Util;

public class CannonLaunchMover implements IMover {

	Skeleton cannon;
	float impulseStrength;
	float delay;
	SoundManager sounds;
	
	public CannonLaunchMover( Skeleton cannon, float impulseStrength,
			float delaySeconds ) {
		this.impulseStrength = impulseStrength;
		this.cannon = cannon;
		this.delay = delaySeconds;
		loadSounds();
	}

	@Override
	public void move( float deltaTime, Body body ) {
		delay -= deltaTime;
		if ( delay <= 0f ) {
			// Gdx.app.log( "CannonLaunchMover", "LAUNCHING!" );
			Vector2 impulseDirection = Util.PointOnCircle( impulseStrength,
					cannon.body.getAngle( ) + Util.HALF_PI, new Vector2( ) );
			body.applyLinearImpulse( impulseDirection, body.getWorldCenter( ) );
			( ( Entity ) ( body.getUserData( ) ) ).setMoverNullAtCurrentState( ); // delete
																					// this
																					// mover!
			SoundRef launch = sounds.getSound( "launch" );
			launch.setVolume(1.0f);
			launch.play( true );
			cannon.addBehindParticleEffect( "cannon", true, true ).start( );
		}
		sounds.update(deltaTime);
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		// TODO Auto-generated method stub

	}

	@Override
	public PuzzleType getMoverType( ) {
		// TODO Auto-generated method stub
		return null;
	}

	public void loadSounds(){
		sounds = new SoundManager();
		sounds.getSound( "launch" , WereScrewedGame.dirHandle + "/levels/dragon/sounds/cannon.ogg");
		
	}
}