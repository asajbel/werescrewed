package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.RopeBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.level.LevelFactory;

public class GleedTestScreen extends Screen {
	

	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new LevelFactory().load( filename );

		SkeletonBuilder skeleBuilder = new SkeletonBuilder( level.world );
		// skeleton at shoulder joint
		Skeleton skel = skeleBuilder.position( 800, 0 )
				.texBackground( WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_tile_suit_main.png",Texture.class ))
				.vert( -1000, -1000 ).vert( -1000, 1000 )
				.vert( 10000, 1000 ).vert( 10000, -1000 ).build( );
		PathBuilder pb = new PathBuilder( );
		skel.addMover(  new RockingMover( -0.1f, 0.5f ), RobotState.IDLE );
		skel.setActive( true );
		level.rootSkeleton.addSkeleton( skel );
		

	}
	
	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		
		
	}
}