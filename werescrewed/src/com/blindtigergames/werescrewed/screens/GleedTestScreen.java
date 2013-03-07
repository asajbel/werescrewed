package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.level.LevelFactory;

public class GleedTestScreen extends Screen {
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new LevelFactory().load( filename );

	}
}