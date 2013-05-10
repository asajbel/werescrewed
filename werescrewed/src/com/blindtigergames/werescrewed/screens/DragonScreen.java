package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.CannonLaunchAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.hazard.builders.HazardBuilder;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TargetImpulseMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.util.Util;

public class DragonScreen extends Screen {

	PuzzleScrew puzzle_screw_balloon1;
	Platform balloon1;
	Skeleton balloon1_super;
	
	public DragonScreen( ) {
		super( );
		String filename = "data/levels/dragonlevel.xml";
		level = new LevelFactory( ).load( filename );
		
		buildBalloon();
		
		
		Skeleton balloon3CannonSkeleton = (Skeleton) LevelFactory.entities.get( "balloon3_cannon_skeleton" );
		balloon3CannonSkeleton.setFgFade( false );
		balloon3CannonSkeleton.setLocalRot( -Util.PI/4 );
		
		buildCannon(balloon3CannonSkeleton, balloon3CannonSkeleton.getPositionPixel( ),
				200, 200);
	}
	
	void buildBalloon(){
		balloon1 = (Platform) LevelFactory.entities.get( "balloon1" );
		Platform balloon2 = (Platform) LevelFactory.entities.get( "balloon2" );
		//Platform balloon3 = (Platform) LevelFactory.entities.get( "balloon3" );
		//Platform balloon4 = (Platform) LevelFactory.entities.get( "balloon4" );
		
		Skeleton balloon1_skeleton = ( Skeleton ) LevelFactory.entities.get( "balloon1_skeleton" );
		balloon1_super = (Skeleton) LevelFactory.entities.get( "balloon1_super" );
		
		puzzle_screw_balloon1 = (PuzzleScrew) LevelFactory.entities.get( "puzzle_screw_balloon1" );
		
		
	//	balloon1.addMover( balloonMover(balloon1, 800, Util.PI/32, 0) );
	//	balloon2.addMover( balloonMover(balloon2, 800, 0, 4) );
//		balloon3.addMover( balloonMover(balloon3, 700, 0, 2) );
//		balloon4.addMover( balloonMover(balloon4, 600, 0, 0) );
//		balloon3_skeleton.addMover( balloonMover(balloon3_skeleton, 600, Util.PI/8, 4) );
//		balloon4_skeleton.addMover( balloonMover(balloon4_skeleton, 700, Util.PI/16, 2) );
//		balloon5_skeleton.addMover( balloonMover(balloon5_skeleton, 800, Util.PI/32, 0) );
		

		
	}



	


	
	float time;
	boolean restart = false;
	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		//time += deltaTime * 1000;
		
		if(time > 5000){
			//balloon2.body.applyForce( new Vector2(0f, 100f), balloon2.body.getWorldCenter( ));
			time = 0;
		}

		if(puzzle_screw_balloon1.getDepth( ) == puzzle_screw_balloon1.getMaxDepth( )){
			if(balloon1_super.currentMover() == null){
				Timeline t = Timeline.createSequence( );
				
				
				t.beginParallel( );
				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_POS_XY, 8f )
						.delay( 0f ).target( 0, 800 )
						.ease( TweenEquations.easeNone ).start( ) );
				
				t.push( Tween.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						   .ease(TweenEquations.easeNone)
						   .target( Util.PI / 32 ).delay( 0f )
						   .start()
						   );
				
				t.end( );
				
				
				t.beginParallel( );
				
				t.push( Tween
						.to( balloon1_super, PlatformAccessor.LOCAL_POS_XY, 8f )
						.delay( 0f ).target( 0, 1600f )
						.ease( TweenEquations.easeNone ).start( ) );
				
				t.push( Tween.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						   .ease(TweenEquations.easeNone)
						   .target( -Util.PI / 32 ).delay( 0f )
						   .start()
						   );
				
				t.end( );

				t.beginSequence( );
				t.push( Tween.to( balloon1_super, PlatformAccessor.LOCAL_ROT, 4f )
						   .ease(TweenEquations.easeNone)
						   .target( 0 ).delay( 0f )
						   .start()
						   );
				 t.end( );
				 balloon1_super.addMover( new TimelineTweenMover( t.start( ) ) );
			}
			
		}
		
	}
	
	IMover balloonMover( Platform skel, float yPos, float angle, float initPause){
		Timeline t = Timeline.createSequence( );
		
		t.delay( initPause );
		
//		t.beginParallel( );
		t.push( Tween
				.to( skel, PlatformAccessor.LOCAL_POS_XY, 8f )
				.delay( 0f ).target( 0, yPos )
				.ease( TweenEquations.easeInOutQuad ).start( ) );
		
//		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 2f )
//				   .ease(TweenEquations.easeInOutQuad)
//				   .target( angle ).delay( 0f )
//				   .start()
//				   );
//		
//		t.end( );
		
		
//		t.beginSequence( );
		
//		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 3f )
//				   .ease(TweenEquations.easeNone)
//				   .target( 0 ).delay( 0f )
//				   .start()
//				   );
//		t.end();
		
		
//		t.beginParallel( );
		
		t.push( Tween
				.to( skel, PlatformAccessor.LOCAL_POS_XY, 8f )
				.delay( 0f ).target( 0, 0f )
				.ease( TweenEquations.easeInOutQuad ).start( ) );
		
//		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 2f )
//				   .ease(TweenEquations.easeInOutQuad)
//				   .target( -angle ).delay( 0f )
//				   .start()
//				   );
//		
//		t.end( );
		
		
//		t.beginSequence( );
//
//		
//		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 3f )
//				   .ease(TweenEquations.easeInOutQuad)
//				   .target( 0 ).delay( 0f )
//				   .start()
//				   );
//		
//		t.end( );
		
//		t.delay( initPause );
		t = t.repeat( Tween.INFINITY, 0f );
		return new TimelineTweenMover( t.start( ) );
	}
	
	void buildCannon(Skeleton skel, Vector2 pos, int widthPix, int heightPix){
		if(widthPix<=64)throw new RuntimeException( "Cannon width needs to be greater than 64 (2tiles) to work properly" );
		PlatformBuilder pb = new PlatformBuilder( level.world ).tileSet( "TilesetTest" );

		
		Vector2 dim = new Vector2(((int)(widthPix/32))-2,((int)(heightPix/32)));
		Vector2 left = new Vector2(pos.x-dim.x/2*32-16, pos.y-16+dim.y*16);
		Vector2 right = new Vector2(pos.x+dim.x/2*32+16, pos.y-16+dim.y*16);
		

		
		//base
		skel.addPlatform( pb.name( "cannon-base" ).dimensions( dim.x,1 ).position( pos.cpy() ).buildTilePlatform( ) );
		//left
		skel.addPlatform( pb.name( "cannon-left" ).dimensions( 1,dim.y ).position( left.cpy() ).buildTilePlatform( ) );
		//right
		skel.addPlatform( pb.name( "cannon-right" ).dimensions( 1,dim.y ).position( right.cpy() ).buildTilePlatform( ) );
		
		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
		
		int quarter = ( int ) ( dim.y*32/4 );
		Vector2 eventPos = new Vector2(pos.x,pos.y+16+quarter);
		
		Array< Vector2 > triggerVerts = new Array< Vector2 >(4);
		//triggerVerts.add( new Vector2 )
		
		triggerVerts.add( new Vector2( quarter,-quarter) );
		triggerVerts.add( new Vector2( quarter, quarter) );
		triggerVerts.add( new Vector2(-quarter, quarter) );
		triggerVerts.add( new Vector2(-quarter,-quarter) );
		triggerVerts.add( new Vector2(-quarter,-quarter) );
		
		
		EventTrigger et = etb.name( "cannon-trigger" ).setVerts( triggerVerts )
				.extraBorder( 0 )
				.position( eventPos )//.addEntity( s )
				.beginAction( new CannonLaunchAction( skel, 0.5f, 1 ) )
				.repeatable( )
				.build( );
		skel.addEventTrigger( et );
		
	}
}
