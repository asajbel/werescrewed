package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

public class SquareMover implements IMover {

	private Vector2 origin;
	private float width;
	private float height;
	private float speed;
	private float alpha = 0;
	private boolean loop;
	private boolean done = false;
	private PuzzleType puzzleType;
	private int path = 0;

	/**
	 * 
	 * @param beginningPoint
	 * @param endingPoint
	 * @param speed
	 * @param loop
	 * @param type
	 *            does the puzzle override the platforms mover or just move once
	 */
	public SquareMover( Vector2 beginningPoint, float width, float height,
			float speed, boolean loop, PuzzleType type ) {
		this.origin = beginningPoint.cpy( );
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.loop = loop;
		puzzleType = type;
	}

	@Override
	public void move( float deltaTime, Body body ) {
		alpha += speed;
		if ( alpha >= 1 ) {
			if ( loop ) {
				alpha = 0;
				path = 0;
			} else {
				done = true;
				alpha = 1;
			}
		} else if ( alpha > 0.75 ) {
			path = 3;
		} else if ( alpha > 0.5 ) {
			path = 2;
		} else if ( alpha > 0.25 ) {
			path = 1;
		}
		if ( !done || loop ) {
			Vector2 temp;
			switch ( path ) {
			case 0:
				temp = new Vector2( origin.x, origin.y );
				temp.lerp( new Vector2( origin.x + width, origin.y ), alpha );
				body.setTransform( temp, 0.0f );
				break;
			case 1:
				temp = new Vector2( origin.x + width, origin.y );
				temp.lerp( new Vector2( origin.x + width, origin.y + height ),
						alpha );
				body.setTransform( temp, 0.0f );
				break;
			case 2:
				temp = new Vector2( origin.x + width, origin.y + height );
				temp.lerp( new Vector2( origin.x, origin.y + height ), alpha );
				body.setTransform( temp, 0.0f );
				break;
			case 3:
				temp = new Vector2( origin.x, origin.y + height );
				temp.lerp( new Vector2( origin.x + width, origin.y ), alpha );
				body.setTransform( temp, 0.0f );
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void move( float deltaTime, Body body, SteeringOutput steering ) {
		move( deltaTime, body );
	}

	public boolean atEnd( ) {
		return done;
	}

	@Override
	public void runPuzzleMovement( PuzzleScrew screw, float screwVal, Platform p ) {
		if ( puzzleType == PuzzleType.PUZZLE_SCREW_CONTROL ) {
			alpha = screwVal;
			if ( alpha > 0.75 ) {
				path = 3;
			} else if ( alpha > 0.5 ) {
				path = 2;
			} else if ( alpha > 0.25 ) {
				path = 1;
			} else {
				path = 0;
			}
			Vector2 temp;
			switch ( path ) {
			case 0:
				temp = new Vector2( origin.x, origin.y );
				temp.lerp( new Vector2( origin.x + width, origin.y ), alpha );
				p.setLocalPos( temp );
				break;
			case 1:
				temp = new Vector2( origin.x + width, origin.y );
				temp.lerp( new Vector2( origin.x + width, origin.y + height ),
						alpha );
				p.setLocalPos( temp );
				break;
			case 2:
				temp = new Vector2( origin.x + width, origin.y + height );
				temp.lerp( new Vector2( origin.x, origin.y + height ), alpha );
				p.setLocalPos( temp );
				break;
			case 3:
				temp = new Vector2( origin.x, origin.y + height );
				temp.lerp( new Vector2( origin.x + width, origin.y ), alpha );
				p.setLocalPos( temp );
				break;
			default:
				break;
			}
		} else if ( puzzleType == PuzzleType.OVERRIDE_ENTITY_MOVER ) {
			if ( p.mover == null ) {
				p.mover = this;
			}
		}
	}

	@Override
	public PuzzleType getMoverType( ) {
		return puzzleType;
	}

}
