package com.blindtigergames.werescrewed.entity.builders;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.MoverType;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.util.Util;

public class MoverBuilder {

	protected ArrayList< Float > floats;
	protected MoverType type;
	protected Entity entity;
	protected boolean vertical, horizontal;
	protected float distance;
	protected World world;

	public void reset( ) {
		floats = new ArrayList< Float >( );
		type = null;
		entity = null;
		vertical = false;
		horizontal = false;
		distance = 0;
		world = null;
	}

	public MoverBuilder(World world ) {
		floats = new ArrayList< Float >( );
		type = null;
		this.world = world;
	}

	public MoverBuilder type( MoverType t ) {
		type = t;
		return this;
	}

	public MoverBuilder applyTo( Entity e ) {
		entity = e;
		return this;
	}

	public MoverBuilder vertical( ) {
		this.vertical = true;
		return this;
	}

	public MoverBuilder horizontal( ) {
		this.horizontal = true;
		return this;
	}

	public MoverBuilder distance( float dist ) {
		this.distance = dist;
		return this;
	}

	public IMover build( ) {
		switch ( type ) {
		case ROCKING:
			return buildRockingMover( );
		case ROTATETWEEN:
			return buildRotateTweenMover( );
		case PUZZLEROTATETWEEN:
			return buildPuzzleRotateTweenMover( );
		case LERP:
			return buildLerpMover( );
		case ANALOG_ROTATE:
			return buildAnalogRotateMover();
		default:
			break;
		}
		return null;
	}

	public AnalogRotateMover buildAnalogRotateMover(){
		AnalogRotateMover anlgRot = new AnalogRotateMover( .6f, world );
		return anlgRot;
		
	}
	public RockingMover buildRockingMover( ) {
		if ( floats.size( ) < 2 )
			return null;
		return new RockingMover( floats.get( 0 ), floats.get( 1 ) );
	}

	public PuzzleRotateTweenMover buildPuzzleRotateTweenMover( ) {
		return new PuzzleRotateTweenMover( 1, Util.PI / 2, true,
				PuzzleType.ON_OFF_MOVER );
	}

	public RotateTweenMover buildRotateTweenMover( ) {
		return new RotateTweenMover( ( Platform ) entity, 2f, Util.PI, 1f, true );
	}

	public LerpMover buildLerpMover( ) {
		LerpMover lm = null;

		if ( horizontal ) {
			lm = new LerpMover( new Vector2( entity.getPositionPixel( ) ),
					new Vector2( entity.getPositionPixel( ).x + this.distance,
							entity.getPositionPixel( ).y ),
					LinearAxis.HORIZONTAL );
		} else if ( vertical ) {
			lm = new LerpMover( entity.getPositionPixel( ), new Vector2(
					entity.getPositionPixel( ).x, entity.getPositionPixel( ).y
							+ this.distance ), LinearAxis.VERTICAL );
		} else {
			lm = new LerpMover(
					new Vector2( entity.body.getPosition( ).x
							* Util.BOX_TO_PIXEL, entity.body.getPosition( ).y
							* Util.BOX_TO_PIXEL ),
					new Vector2( entity.body.getPosition( ).x, entity.body
							.getPosition( ).y + 1.3f ).mul( Util.BOX_TO_PIXEL ),
					LinearAxis.VERTICAL );
		}

		return lm;
	}

	public MoverBuilder fromString( String text ) {
		Array< String > blocks = new Array< String >( text.split( "\\s+" ) );
		this.type = MoverType.fromString( blocks.get( 0 ) );
		floats.clear( );
		try {
			for ( int i = 1; i < blocks.size; i++ ) {
				floats.add( Float.parseFloat( blocks.get( i ) ) );
			}
		} catch ( NumberFormatException err ) {
			// Gdx.app.log( "MoverBuilder",
			// "The string \""+text+"\" should consist of a name followed by floats.",
			// err );
		}
		return this;
	}

}
