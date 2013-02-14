package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.util.Util;

public class LerpForceMover implements IMover {

		private Vector2 beginningPoint;
		private Vector2 currentPoint;
		private Vector2 endPoint;
		private Vector2 speed;
		private boolean loop;
		private boolean done = false;

		public LerpForceMover( Vector2 bp, Vector2 ep, Vector2 speed, boolean loop ) {
			if( speed.x > 0 ) {
				if ( ep.x > bp.x )  {
					beginningPoint = bp.cpy( );
					endPoint = ep.cpy( );
				} else {
					beginningPoint = ep.cpy( );
					endPoint = bp.cpy( );					
				}
			} else if ( speed.x < 0 ){
				if ( bp.x > ep.x )  {
					beginningPoint = bp.cpy( );
					endPoint = ep.cpy( );
				} else {
					beginningPoint = ep.cpy( );
					endPoint = bp.cpy( );					
				}
			} else if ( speed.y > 0 ) {
				if ( ep.y > bp.y )  {
					beginningPoint = bp.cpy( );
					endPoint = ep.cpy( );
				} else {
					beginningPoint = ep.cpy( );
					endPoint = bp.cpy( );					
				}
			} else if ( speed.y < 0 ) { 
				if ( bp.y > ep.y )  {
					beginningPoint = bp.cpy( );
					endPoint = ep.cpy( );
				} else {
					beginningPoint = ep.cpy( );
					endPoint = bp.cpy( );					
				}
			}
			currentPoint = beginningPoint.cpy( );
			this.speed = speed;
			this.loop = loop;
		}

		@Override
		public void move( float deltaTime, Body body ) {
			if ( speed.x > 0 ) {
				if ( currentPoint.x > endPoint.x ) {
					if ( loop ) {
						speed.x *= -1;
					} else { 
						done = true;
					}
				}
			} else if ( speed.x < 0 ) {
				if ( currentPoint.x < beginningPoint.x ) {
					if ( loop ) {
						speed.x *= -1;
					} else { 
						done = true;
					}
				}				
			} else if ( speed.y > 0 ) {
				if ( currentPoint.x < beginningPoint.x ) {
					if ( loop ) {
						speed.y *= -1;
					} else { 
						done = true;
					}
				}		
			} else if ( speed.y < 0 ) {
				if ( currentPoint.x < beginningPoint.x ) {
					if ( loop ) {
						speed.y *= -1;
					} else { 
						done = true;
					}
				}		
			}
			if ( !done ) { 
				body.setLinearVelocity( speed );
			}
			currentPoint = body.getPosition( );
		}

		@Override
		public void move( float deltaTime, Body body, SteeringOutput steering ) {
			move( deltaTime, body );
		}

		public boolean atEnd( ) {
			return done;
		}
		
		@Override
		public void runPuzzleMovement( float screwVal, Platform p ) {

		}
}
