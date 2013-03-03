package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.level.GleedLoader;

public class GleedTestScreen extends Screen {
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new GleedLoader().load( filename );
		level.world.setContactListener( MCL );
	}
}