package com.blindtigergames.werescrewed.entity.builders;

import java.util.ArrayList;

import com.blindtigergames.werescrewed.entity.mover.*;

public class MoverBuilder {

	protected ArrayList<Float> floats;
	protected MoverType type;
	
	public MoverBuilder( ) {
		floats = new ArrayList<Float>();
		type = null;
	}
	
	public IMover build(){
		switch(type){
		case ROCKING:
			return buildRockingMover();
		}
		return null;
	}
	
	public RockingMover buildRockingMover(){
		if (floats.size( ) < 2)
			return null;
		return new RockingMover(floats.get(0), floats.get(1));
	}

}
