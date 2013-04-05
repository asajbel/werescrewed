package com.blindtigergames.werescrewed.screens;

import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.level.CharacterSelect;
import com.blindtigergames.werescrewed.level.LevelFactory;

public class AlphaScreen extends Screen {

	public ScreenType screenType;

	
	private CharacterSelect characterSelect;

	public AlphaScreen( ) {
		super( );
		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );
		
		
		characterSelect = new CharacterSelect(level);
		
		
	//	Skeleton skel2 = ( Skeleton ) LevelFactory.entities.get( "skeleton2" );
		
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		
		characterSelect.update( );
		
		characterSelect.draw( batch, deltaTime );
		
		

		
		
	}
	

	
	
	
	
	
}
