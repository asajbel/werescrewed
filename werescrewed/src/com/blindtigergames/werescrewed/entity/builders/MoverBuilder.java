package com.blindtigergames.werescrewed.entity.builders;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.MoverType;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;

public class MoverBuilder {

	protected ArrayList<Float> floats;
	protected MoverType type;
	
	public MoverBuilder( ) {
		floats = new ArrayList<Float>();
		type = null;
	}

	public MoverBuilder type( MoverType t ) {
		type = t;
		return this;
	}
	
	public IMover build(){
		switch(type){
		case ROCKING:
			return buildRockingMover();
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
