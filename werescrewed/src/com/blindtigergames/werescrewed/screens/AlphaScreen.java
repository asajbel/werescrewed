package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
		
		Skeleton skel = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		
		Skeleton skel2 = ( Skeleton ) LevelFactory.entities.get( "kneeSkeleton" );
		
		skel.body.setType( BodyType.KinematicBody );
		skel2.body.setType( BodyType.KinematicBody );
		
		
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		
		characterSelect.update( );
		
		characterSelect.draw( batch, deltaTime );
		
		

		
		
	}
	

	
	
	
	
	
}
