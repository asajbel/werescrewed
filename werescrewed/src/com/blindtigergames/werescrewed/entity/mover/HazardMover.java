package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.util.Util;
import com.blindtigergames.werescrewed.hazard.*;

public class HazardMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private Orientation ori;
	private float alpha = 0;
	private boolean done = false;
	private float speed;
	private final float negSpeed = -0.001f;
	private final float posSpeed = 0.001f;
	
	public HazardMover ( Vector2 endingPoint, Orientation ori ) {
		this.endPoint = endingPoint.cpy( );
		this.ori = ori;
		
		done = false;
		alpha = 0;
	}
	
	@Override
	public void move( float deltaTime, Body body ) {
		//setSpeed( body );
		alpha += speed;
		if ( alpha >= 1 ) {
			done = true;
			alpha = 1;
		}
		else if ( alpha <= 0 ) {
			done = true;
			alpha = -1;
		}
		
		beginningPoint = body.getPosition( ).cpy( );
		beginningPoint.lerp( endPoint, alpha );
		
		if ( ori == Orientation.LEFT || ori == Orientation.RIGHT ) {
				body.setTransform( beginningPoint.x * Util.PIXEL_TO_BOX, body.getPosition( ).y, 0.0f );	
		}
		else if ( ori == Orientation.UP || ori == Orientation.DOWN ) {
				body.setTransform( body.getPosition( ).x, beginningPoint.y * Util.PIXEL_TO_BOX, 0.0f );	
		}
	}

	public boolean atEnd( ) {
		return done;
	}

	public boolean atStart( ) {
		return alpha == 0;
	}
	
	private void setSpeed( Body body ) {
		Hazard temp = ( Hazard ) body.getUserData( );
		
		if ( temp.isActive( ) ) { // Hazard, like spikes, pop out.
			if ( ori == Orientation.LEFT || ori == Orientation.UP ) {
				speed = negSpeed;
			}
			else if ( ori == Orientation.RIGHT || ori == Orientation.DOWN ) {
				speed = posSpeed;
			}
		}
		else { // Hazard, like spikes, retract.
			if ( ori == Orientation.LEFT || ori == Orientation.UP ) {
				speed = posSpeed;
			}
			else if ( ori == Orientation.RIGHT || ori == Orientation.DOWN ) {
				speed = negSpeed;
			}
		}
	}
	
	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		
	}

	@Override
	public PuzzleType getMoverType( ) {
		return null;
	}
	
}
