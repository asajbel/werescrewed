package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.rope.Rope;

public class GleedTestScreen extends Screen {
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new LevelFactory().load( filename );

	}
}
/*
SkeletonBuilder b = new SkeletonBuilder( level.world );
Skeleton s = b.name( "skelelele" ).position( 0,100 ).texBackground( WereScrewedGame.manager.get("data/common/robot/alphabot_tile_interior.png",Texture.class) ).vert( -100,-100 ).vert( -100,100 ).vert( 100,0 ).build( );
s.addMover( new PathBuilder().begin( s ).target( 100, 0, 1 )
		.target( 100, 100, 1 ).target( 0, 100, 1 ).target( 0, 0, 1 )
		.build( ), RobotState.IDLE );
level.root.addSkeleton( s );
s.setActive( true );
*/