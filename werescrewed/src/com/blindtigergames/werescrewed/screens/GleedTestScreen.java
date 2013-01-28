package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.input.InputHandlerPlayer1;
import com.blindtigergames.werescrewed.level.GleedLoader;
import com.blindtigergames.werescrewed.level.Level;

public class GleedTestScreen extends Screen {
	
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new GleedLoader().load( filename );
		level.world.setContactListener( MCL );
	}

}