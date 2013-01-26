package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.level.GleedLoader;
import com.blindtigergames.werescrewed.level.Level;

public class GleedTestScreen extends Screen {

	Level level;
	
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = GleedLoader.loadLevelFromFile(filename);
		level.world.setContactListener( MCL );
	}
	
	
	@Override
	public void render( float delta ) {
		super.render(delta);

		level.update( delta );
		level.draw( batch, debugRenderer );
	}

	@Override
	public void resize( int width, int height ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub
		
	}
	
}