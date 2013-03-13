package com.blindtigergames.werescrewed.entity.builders;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.MoverType;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.util.Util;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;

public class MoverBuilder {

	protected ArrayList<Float> floats;
	protected MoverType type;
	protected Entity entity;
	
	public MoverBuilder( ) {
		floats = new ArrayList<Float>();
		type = null;
	}

	public MoverBuilder type( MoverType t ) {
		type = t;
		return this;
	}
	
	public MoverBuilder applyTo( Entity e){
		entity = e;
		return this;
	}
	
	public IMover build(){
		switch(type){
		case ROCKING:
			return buildRockingMover();
		case ROTATETWEEN:
			return buildRotateTweenMover();
		case LERP:
			return buildLerpMover();
		default:
			break;
		}
		return null;
	}
	
	public RockingMover buildRockingMover(){
		if (floats.size( ) < 2)
			return null;
		return new RockingMover(floats.get(0), floats.get(1));
	}

	public PuzzleRotateTweenMover buildRotateTweenMover(){
		return new PuzzleRotateTweenMover( 1,
				Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );
	}
	
	public LerpMover buildLerpMover(){
		LerpMover lm = new LerpMover( new Vector2( entity.body.getPosition( ).x
				* Util.BOX_TO_PIXEL, entity.body.getPosition( ).y
				* Util.BOX_TO_PIXEL ), new Vector2( entity.body.getPosition( ).x,
				entity.body.getPosition( ).y + 1.5f ).mul( Util.BOX_TO_PIXEL ),
				LinearAxis.VERTICAL );
		
		return lm;
	}
	public MoverBuilder fromString( String text ) {
		Array<String> blocks = new Array<String>(text.split( "\\s+" ));
		this.type = MoverType.fromString( blocks.get(0) );
		floats.clear( );
		try{
			for (int i = 1; i < blocks.size; i++){
				floats.add( Float.parseFloat( blocks.get( i ) ) );
			}
		} catch (NumberFormatException err){
			Gdx.app.log( "MoverBuilder", "The string \""+text+"\" should consist of a name followed by floats.", err );
		}
		return this;
	}


}
