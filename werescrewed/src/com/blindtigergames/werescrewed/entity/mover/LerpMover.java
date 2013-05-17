package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.util.Util;

public class LerpMover implements IMover {

	private Vector2 beginningPoint;
	private Vector2 endPoint;
	private float speed;
	private float alpha = 0;
	private float onValue;
	private int loopTimes;
	private int offValue;
	private boolean loop;
	private boolean done = false;
	private boolean reverse = false;
	private PuzzleType puzzleType;
	private LinearAxis axis;

	/**
	 * use this contructor of lerp mover to create a auto moving lerp either an
	 * initial mover or turned on/off by a puzzle screw with a certain amount of
	 * loops or continuosly looping
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param loop
	 *            continuosly looping or not
	 * @param LinearAxis
	 *            which axis this moves on
	 * @param loopTime
	 *            how many loops this goes through
	 */
	public LerpMover( Vector2 beginningPoint, Vector2 endingPoint, float speed,
			boolean loop, LinearAxis axis, int loopTimes ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed = speed;
		this.loop = loop;
		this.loopTimes = loopTimes;
		this.axis = axis;
		puzzleType = PuzzleType.OVERRIDE_ENTITY_MOVER;
	}

	/**
	 * use this contructor of lerp mover to create a auto moving lerp that turns
	 * on/off by a puzzle screw with a certain amount of loops or continuosly
	 * looping
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param loop
	 *            continuosly looping or not
	 * @param LinearAxis
	 *            which axis this moves on
	 * @param loopTime
	 *            how many loops this goes through
	 * @param offValue
	 *            when the screw turns off
	 * @param onValue
	 *            when the screw turns on
	 */
	public LerpMover( Vector2 beginningPoint, Vector2 endingPoint, float speed,
			boolean loop, LinearAxis axis, int loopTimes, int offVal,
			float onVal ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.speed = speed;
		this.loop = loop;
		this.loopTimes = loopTimes;
		this.axis = axis;
		if ( offVal >= 1 ) {
			this.offValue = 1;
		} else {
			this.offValue = 0;
		}
		if ( onVal <= 1 && onVal >= 0 && onVal != offVal ) {
			onValue = onVal;
		} else {
			throw new IllegalArgumentException(
					"onValue has to be in the range 0-1 and cannot be equal to the off value" );
		}
		puzzleType = PuzzleType.ON_OFF_MOVER;
	}

	/**
	 * use this constructor of lerp mover to create a puzzle screw control lerp
	 * mover
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param puzzleType
	 * @param LinearAxis
	 *            which axis this moves on
	 */
	public LerpMover( Vector2 beginningPoint, Vector2 endingPoint,
			LinearAxis axis ) {
		this.beginningPoint = beginningPoint.cpy( );
		this.endPoint = endingPoint.cpy( );
		this.axis = axis;
		this.loopTimes = 0;
		alpha = 0;
		puzzleType = PuzzleType.PUZZLE_SCREW_CONTROL;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		if ( puzzleType != PuzzleType.PUZZLE_SCREW_CONTROL ) {
			alpha += speed;
			if ( alpha >= 1 ) {
				if ( loop ) {
					speed *= -1;
				} else {
					if ( loopTimes == 0 ) {
						done = true;
						reverse = false;
						alpha = 1;
					} else {
						loopTimes--;
						speed *= -1;
					}
				}
			} else if ( alpha < 0 ) {
				if ( loop ) {
					speed *= -1;
				} else {
					if ( loopTimes == 0 ) {
						done = true;
						reverse = false;
						alpha = 0;
					} else {
						loopTimes--;
						speed *= -1;
					}
				}
			}
			Vector2 temp = beginningPoint.cpy( );
			beginningPoint.lerp( endPoint, alpha );
			if ( axis == LinearAxis.VERTICAL ) {
				if ( body.getUserData( ) instanceof Platform ) {
					Platform p = ( Platform ) body.getUserData( );
					float newPos = Math.abs( p.getOriginPos( ).y
							- beginningPoint.y );
					p.setLocalPos( p.getLocalPos( ).x, newPos );
				} else {
					body.setTransform( body.getPosition( ).x, beginningPoint.y
							* Util.PIXEL_TO_BOX, 0.0f );
				}
			} else if ( axis == LinearAxis.HORIZONTAL ) {
				if ( body.getUserData( ) instanceof Platform ) {
					Platform p = ( Platform ) body.getUserData( );
					float newPos = Math.abs( p.getOriginPos( ).x
							- beginningPoint.x );
					p.setLocalPos( newPos, p.getLocalPos( ).y );
				} else {
					body.setTransform( beginningPoint.x * Util.PIXEL_TO_BOX,
							body.getPosition( ).y, body.getAngle( ) );
				}
			} else {
				if ( body.getUserData( ) instanceof Platform ) {
					Platform p = ( Platform ) body.getUserData( );
					float newX = Math.abs( p.getOriginPos( ).x
							- beginningPoint.x );
					float newY = Math.abs( p.getOriginPos( ).y
							- beginningPoint.y );
					p.setLocalPos( newX, newY );
				} else {
					body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ),
							0.0f );
				}
			}
			beginningPoint = temp;
		}
	}

	public boolean atEnd( ) {
		return done;
	}

	public boolean atStart( ) {
		return alpha == 0;
	}

	public void setAlpha( float value ) {
		alpha = value;
		if ( alpha > 1 ) {
			alpha = 1;
		} else if ( alpha < 0 ) {
			alpha = 0;
		}
	}

	/**
	 * take a step
	 */
	public void moveStep( ) {
		alpha += speed;
	}

	/**
	 * set the speed
	 * 
	 * @param speed
	 */
	public void setSpeed( float speed ) {
		this.speed = speed;
	}

	/**
	 * set the end position and reset to the beginning pos
	 */
	public void changeEndPos( Vector2 endPos ) {
		this.endPoint = endPos.cpy( );
	}

	/**
	 * set the beginning position
	 */
	public void changeBeginPos( Vector2 beginPos ) {
		this.beginningPoint = beginPos.cpy( );
	}

	/**
	 * get current location
	 */
	public Vector2 getPos( ) {
		if ( alpha >= 1 ) {
			done = true;
			reverse = false;
			alpha = 1;
		} else if ( alpha < 0 ) {
			done = true;
			reverse = false;
			alpha = 0;
		}
		Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y );
		temp.lerp( endPoint, alpha );
		return temp;
	}

	/**
	 * analog placement along a linear path
	 */
	public void moveAnalog( Screw screw, float screwVal, Body body ) {
		Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y );
		beginningPoint.lerp( endPoint, screwVal );
		body.setTransform( beginningPoint.mul( Util.PIXEL_TO_BOX ), 0.0f );
		beginningPoint = temp;
	}

	@Override
	public void runPuzzleMovement( Screw screw, float screwVal, Platform p ) {
		switch ( puzzleType ) {
		case PUZZLE_SCREW_CONTROL:
			Vector2 temp = new Vector2( beginningPoint.x, beginningPoint.y );
			beginningPoint.lerp( endPoint, screwVal );
			if ( axis == LinearAxis.VERTICAL ) {
				float newPos = Math
						.abs( p.getOriginPos( ).y - beginningPoint.y );
				if ( temp.y > endPoint.y ) {
					p.setLocalPos( p.getLocalPos( ).x, -newPos );
				} else {
					p.setLocalPos( p.getLocalPos( ).x, newPos );
				}
			} else if ( axis == LinearAxis.HORIZONTAL ) {
				float newPos = Math
						.abs( p.getOriginPos( ).x - beginningPoint.x );
				if ( temp.x > endPoint.x ) {
					p.setLocalPos( -newPos, p.getLocalPos( ).y );
				} else {
					p.setLocalPos( newPos, p.getLocalPos( ).y );
				}
			} else {
				float newX = Math.abs( p.getOriginPos( ).x - beginningPoint.x );
				float newY = Math.abs( p.getOriginPos( ).y - beginningPoint.y );
				if ( temp.x > endPoint.x ) {
					newX = -newX;
				}
				if ( temp.y > endPoint.y ) {
					newY = -newY;
				}
				p.setLocalPos( newX, newY );
			}
			beginningPoint = temp;
			break;
		case OVERRIDE_ENTITY_MOVER:
			if ( p.currentMover( ) instanceof LerpMover ) {
				LerpMover lm = ( LerpMover ) p.currentMover( );
				if ( lm.atEnd( ) ) {
					p.setMoverAtCurrentState( this );
				}
			} else {
				p.setMoverAtCurrentState( this );
			}
			break;
		case ON_OFF_MOVER:
			// if the value is on the low side of the screw
			// the screw value has to be greater than the on value
			if ( offValue == 0 ) {
				// if its a lerp mover dont overwrite it
				if ( p.currentMover( ) instanceof LerpMover ) {
					LerpMover lm = ( LerpMover ) p.currentMover( );
					if ( lm == this ) {
						// if the same mover is already applied but the screwval
						// is now off reverese the speed
						if ( screwVal < onValue && !reverse ) {
							speed *= -1;
							done = false;
							reverse = true;
							loopTimes++;
						}
					} else if ( lm.atEnd( ) ) {
						if ( screwVal >= onValue ) {
							p.addMover( this, RobotState.IDLE );
							reverse = false;
						} else {
							alpha = 0f;
							done = false;
							p.setMoverNullAtCurrentState( );
						}
					}
				} else {
					if ( screwVal >= onValue ) {
						p.setMoverAtCurrentState( this );
						reverse = false;
					} else {
						alpha = 0f;
						done = false;
						p.setMoverNullAtCurrentState( );
					}
				}
			} else { // offvalue = 1
				// if the value is on the high side of the screw
				// the screw value has to be lower than the on value
				if ( p.currentMover( ) instanceof LerpMover ) {
					LerpMover lm = ( LerpMover ) p.currentMover( );
					if ( lm == this ) {
						// if the same mover is already applied but the screwval
						// is now off reverese the speed
						if ( screwVal > onValue && !reverse ) {
							speed *= -1;
							done = false;
							reverse = true;
							loopTimes++;
						}
					} else if ( lm.atEnd( ) ) {
						if ( screwVal <= onValue ) {
							p.setMoverAtCurrentState( this );
							reverse = false;
						} else {
							alpha = 0f;
							done = false;
							p.setMoverNullAtCurrentState( );
						}
					}
				} else {
					if ( screwVal <= onValue ) {
						p.setMoverAtCurrentState( this );
						reverse = false;
					} else {
						alpha = 0f;
						done = false;
						p.setMoverNullAtCurrentState( );
					}
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}
}
