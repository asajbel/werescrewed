package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
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
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.util.Util;

public class DragonScreen extends Screen {

	Platform balloon1 = null;
	Skeleton balloon3_skeleton = null;
	Platform balloon2 = null;
	
	public DragonScreen( ) {
		super( );
		String filename = "data/levels/dragonlevel.xml";
		level = new LevelFactory( ).load( filename );
		
		buildBalloon();
		
	}
	
	void buildBalloon(){
		balloon2 = (Platform) LevelFactory.entities.get( "balloon2" );
		balloon2.body.setGravityScale( 0 );
		
		balloon3_skeleton = ( Skeleton ) LevelFactory.entities.get( "balloon3_skeleton" );
		Skeleton balloon4_skeleton = ( Skeleton ) LevelFactory.entities.get( "balloon4_skeleton" );
		Skeleton balloon5_skeleton = ( Skeleton ) LevelFactory.entities.get( "balloon5_skeleton" );
		//balloon2_skeleton.body.setGravityScale( 0.0f );
		//balloon1_skeleton.body.setLinearVelocity( new Vector2(0,5f) );
		
		
		PathBuilder pb = new PathBuilder( );
		//balloon3_skeleton.setActive( true );
		
		
		balloon3_skeleton.addMover( balloonMover(balloon3_skeleton, 600, Util.PI/8, 4) );
		balloon4_skeleton.addMover( balloonMover(balloon4_skeleton, 700, Util.PI/16, 2) );
		balloon5_skeleton.addMover( balloonMover(balloon5_skeleton, 800, Util.PI/32, 0) );
		
//		balloon2_skeleton.addMover( pb.begin( balloon2_skeleton ).target( 300, 0, 5 )
//				.target( 300, 300, 5 ).target( 0, 300, 5 ).target( 0, 0, 5 )
//				.build( ), RobotState.IDLE );
		//balloon2_skeleton.addMover(new RockingMover(1f, 1f), RobotState.IDLE);
		//balloon2_skeleton.body.setAngularVelocity( 5 );
		
//		balloon2_skeleton.addMover( new RotateTweenMover(balloon2_skeleton, 5f, Util.PI / 2, 5f, true),
//				RobotState.IDLE); 
		//balloon2.body.applyForce( new Vector2(0, 200f), balloon2.body.getWorldCenter( ) );
		
		//balloon1.body.setLinearVelocity( new Vector2(0, 50f) );
		
		
//		TargetImpulseMover tim = new TargetImpulseMover(new Vector2(0, 600f), Vector2.Zero,
//				5f, true, 100f);
//		balloon2_skeleton.addMover( tim );
		
	}

	void buildGround( ) {
		SkeletonBuilder sb = new SkeletonBuilder( level.world );
		Skeleton skeleton = sb.name( "skeleton" ).build( );
		PlatformBuilder pb = new PlatformBuilder( level.world );
		TiledPlatform tp = pb.name( "ground" ).dimensions( 100, 3 )
				.buildTilePlatform( );
		skeleton.addPlatform( tp );
		level.root.addSkeleton( skeleton );
	}

	void buildHazardSkeleton( ) {
		SkeletonBuilder sb = new SkeletonBuilder( level.world );
		Skeleton skeleton = sb.name( "hazard_skel" ).position( 500, 300 )
				.vert( -50, -50 ).vert( 50, -50 ).vert( 50, 50 ).vert( -50, 50 )
				.fg( ).vert( -100, -100 ).vert( 100, -100 ).vert( 100, 100 )
				.vert( -100, 100 ).build( );
		level.root.addSkeleton( skeleton );
		skeleton.setMoverAtCurrentState( new RotateTweenMover( skeleton, 10,
				Util.TWO_PI, 0, true ) );

		// HazardBuilder hb = new HazardBuilder(level.world);
//		Fire f = new Fire( "fire1", new Vector2( 500, 350 ), 100, 100,
//				level.world, true );
//		skeleton.addHazard( f );

	}

	void buildDynamicSkeleton( ) {
		SkeletonBuilder sb = new SkeletonBuilder( level.world );
		Skeleton dyn_skeleton = sb.name( "dyn_skeleton" ).position( -100, 400 )
				.dynamic( ).build( );
		PlatformBuilder pb = new PlatformBuilder( level.world );

		Platform tp = pb.name( "kin_on_dyn_skele" ).dimensions( 5, 1 )
				.position( 0, 50 ).buildTilePlatform( );
		dyn_skeleton.addKinematicPlatform( tp );

		Platform balloon = buildBalloon( 0, 700 );
		dyn_skeleton.addDynamicPlatform( balloon );

		StructureScrew s = new StructureScrew( "ss",
				dyn_skeleton.getPositionPixel( ), 100, level.world, Vector2.Zero );
		s.addWeldJoint( dyn_skeleton );
		s.addWeldJoint( balloon );
		dyn_skeleton.addScrewForDraw( s );

		level.root.addSkeleton( dyn_skeleton );
	}

	Platform buildBalloon( float x, float y ) {
		Vector2 posPix = new Vector2( x, y );
		Texture texture = WereScrewedGame.manager.get(
				"data/common/sawblade.png", Texture.class );
		Platform balloon = new Platform( "balloon", new Vector2( x, y ),
				texture, level.world );
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set( posPix.cpy( ).mul( Util.PIXEL_TO_BOX ) );
		balloon.body = level.world.createBody( bodyDef );

		CircleShape circle = new CircleShape( );
		circle.setRadius( 150 * Util.PIXEL_TO_BOX );

		FixtureDef fixture = new FixtureDef( );
		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixture.isSensor = true;
		fixture.shape = circle;
		fixture.density = .1f;

		balloon.body.createFixture( fixture );
		balloon.body.setFixedRotation( true );
		balloon.body.setUserData( this );
		// balloon.body.setGravityScale( 0 );

		circle.dispose( );

		balloon.setMoverAtCurrentState( new TargetImpulseMover( balloon
				.getPositionPixel( ).add( 0, 0 ), Vector2.Zero, .4f, true,
				100 ) );

		return balloon;
	}

	void complexPlatform( ) {
		// SkeletonBuilder sb = new SkeletonBuilder( level.world );
		// Skeleton dyn_skeleton = sb.name( "dyn_skeleton" ).position( -100,300
		// ).density( 0.1f ).dynamic( ).build( );
		PlatformBuilder pb = new PlatformBuilder( level.world );
		Array< Vector2 > verts = new Array< Vector2 >( );
		verts.add( new Vector2( 0, 0 ) );
		verts.add( new Vector2( 1, 1 ) );
		verts.add( new Vector2( 100, 100 ) );
		verts.add( new Vector2( 0, 200 ) );
		verts.add( new Vector2( -100, 100 ) );

		Platform tp = pb
				.name( "custom_Plat" )
				.texture(
						WereScrewedGame.manager.get(
								"data/common/robot/alphabot_tile_interior.png",
								Texture.class ) ).position( 200, 700 )
				.setVerts( verts ).buildCustomPlatform( );
		level.root.addKinematicPlatform( tp );
	}
	
	float time;
	boolean restart = false;
	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );
		time += deltaTime * 1000;
		
		if(time > 5000){
			//balloon2.body.applyForce( new Vector2(0f, 100f), balloon2.body.getWorldCenter( ));
			time = 0;
		}
		
		
//		if(balloon1.body.getLinearVelocity( ).y < 5 && !restart){
//			balloon1.body.applyForce( new Vector2(0, 20f), balloon1.body.getWorldCenter( ) );
//			time = 0;
//		} else {
//			restart = true;
//			time += deltaTime * 1000;
//			if(time > 2000) restart = false;
//		}
//		
//		System.out.println( balloon1.body.getLinearVelocity( ) );
	}
	
	IMover balloonMover( Platform skel, float yPos, float angle, float initPause){
		Timeline t = Timeline.createSequence( );
		
		t.delay( initPause );
		
		t.beginParallel( );
		t.push( Tween
				.to( skel, PlatformAccessor.LOCAL_POS_XY, 3f )
				.delay( 0f ).target( 0, yPos )
				.ease( TweenEquations.easeNone ).start( ) );
		
		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 2f )
				   .ease(TweenEquations.easeNone)
				   .target( angle ).delay( 0f )
				   .start()
				   );
		
		t.end( );
		
		
		t.beginSequence( );
		
		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 3f )
				   .ease(TweenEquations.easeNone)
				   .target( 0 ).delay( 0f )
				   .start()
				   );
		t.end();
		
		
		t.beginParallel( );
		
		t.push( Tween
				.to( skel, PlatformAccessor.LOCAL_POS_XY, 3f )
				.delay( 0f ).target( 0, 0f )
				.ease( TweenEquations.easeNone ).start( ) );
		
		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 2f )
				   .ease(TweenEquations.easeNone)
				   .target( -angle ).delay( 0f )
				   .start()
				   );
		
		t.end( );
		
		
		t.beginSequence( );

		
		t.push( Tween.to( skel, PlatformAccessor.LOCAL_ROT, 3f )
				   .ease(TweenEquations.easeNone)
				   .target( 0 ).delay( 0f )
				   .start()
				   );
		
		t.end( );
		
		t = t.repeat( Tween.INFINITY, 0f );
		return new TimelineTweenMover( t.start( ) );
	}
}
