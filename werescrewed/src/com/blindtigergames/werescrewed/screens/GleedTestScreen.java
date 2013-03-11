package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.RopeBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.rope.Rope;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;

public class GleedTestScreen extends Screen {
	
	Music music;
	public GleedTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";
		level = new LevelFactory().load( filename );

		music = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/sounds/TrainJob.mp3" );
		SkeletonBuilder skeleBuilder = new SkeletonBuilder( level.world );
		// skeleton at shoulder joint
		Skeleton skel = skeleBuilder.position( 800, 0 ).name( "background" )
				.texBackground( WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_tile_suit_main.png",Texture.class ))
				.vert( -1000, -1000 ).vert( -1000, 2000 )
				.vert( 10000, 2000 ).vert( 10000, -1000 ).build( );

		level.root.addSkeleton( skel );

		SkeletonBuilder skeleBuilder2 = new SkeletonBuilder( level.world );
		// skeleton at shoulder joint
		Skeleton skel2 = skeleBuilder2.position( 5000f, 700f ).name( "debug" )
				.texBackground( WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_tux.png",Texture.class ))
				.vert( -10, -10 ).vert( -10, 10 )
				.vert( 10, 10).vert( 10, -10 ).build( );

		SkeletonBuilder skeleBuilder3 = new SkeletonBuilder( level.world );
		// skeleton at shoulder joint
		Skeleton skel3 = skeleBuilder3.position( 6000f, 700f ).name( "debug2" )
				.texBackground( WereScrewedGame.manager.get(WereScrewedGame.dirHandle+"/common/robot/alphabot_texture_tux.png",Texture.class ))
				.vert( -10, -10 ).vert( -10, 10 )
				.vert( 10, 10).vert( 10, -10 ).build( );
	
		PlatformBuilder platBuilder = new PlatformBuilder( level.world ).tileSet( "autumn" );
		TiledPlatform plat = platBuilder.position( 4900, 1000 ).name( "plat141" )
				.dimensions( 3, 10) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		
		TiledPlatform plat2 = platBuilder.position( 4900, 400 ).name( "plat142" )
				.dimensions( 3, 10) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel2.addKinematicPlatform( plat2 );
		
		TiledPlatform plat3 = platBuilder.position( 5400, 500 ).name( "plat143" )
				.dimensions( 35, 3) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel2.addKinematicPlatform( plat3 );
		
		TiledPlatform plat4 = platBuilder.position( 5400, 1000 ).name( "plat144" )
				.dimensions( 30, 3) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel2.addKinematicPlatform( plat4);
		
		skel2.addMover( new PathBuilder().begin( skel2 ).target( 100, 0, 1 )
				.target( 100, 100, 1 ).target( 0, 100, 1 ).target( 0, 0, 1 )
				.build( ), RobotState.IDLE );
		skel2.setActive( true );
		

		TiledPlatform plat5 = platBuilder.position( 5400, 700 ).name( "plat145" )
				.dimensions( 1, 8) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel3.addKinematicPlatform( plat5);
		plat5.setActive( false );
		
		PuzzleScrew elevatorscrew1 = new PuzzleScrew( "elevatorControlinside",
				plat5.getPositionPixel( ).add( 64, -64 ), 200,
				plat5, level.world, 0, false );
		elevatorscrew1.puzzleManager.addEntity( plat5 );
		elevatorscrew1.puzzleManager.addMover( new LerpMover( plat5
				.getPositionPixel( ), plat5.getPositionPixel( ).add( 0, 150 ),
				LinearAxis.VERTICAL ) );
		skel3.addScrewForDraw( elevatorscrew1 );
		
		TiledPlatform plat6 = platBuilder.position( 6700, 500 ).name( "plat146" )
				.dimensions( 10, 1) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel3.addKinematicPlatform( plat6 );
		
		TiledPlatform plat7 = platBuilder.position( 6300, 200 ).name( "plat146" )
				.dimensions( 20, 1) .kinematic( )
				.friction( 1.0f )
				.buildTilePlatform( );
		skel3.addKinematicPlatform( plat7 );
		
		TiledPlatform plat8 = platBuilder.position( 6100, 350 ).name( "plat146" )
				.dimensions( 3, 1) .kinematic( )
				.friction( 1.0f ).oneSided( true )
				.buildTilePlatform( );
		skel3.addKinematicPlatform( plat8 );
		
		RopeBuilder ropeBuilder = new RopeBuilder( level.world );
		ropeBuilder.name( "rope1" ).position(6100, 1000 ).links( 5 )
				.createScrew( );


		Rope rope = ropeBuilder.buildRope( );
		skel3.addRope( rope );
		

		
		level.root.addSkeleton( skel2 );
		level.root.addSkeleton( skel3 );
	}
	
	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		
		if(!music.isPlaying( )){
			music.play( );
			music.setLooping( true);
			music.setVolume( 0.3f );
		}
		//music.setLooping( true);
		

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
