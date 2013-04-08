package com.blindtigergames.werescrewed.screens;

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
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.hazard.builders.HazardBuilder;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TargetImpulseMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.util.Util;

public class DragonScreen extends Screen {

	public DragonScreen( ) {
		super( );
		// String filename = "data/levels/dragon.xml";
		// level = new LevelFactory( ).load( filename );
		level = new Level( );

		level.camera = new Camera( new Vector2( Gdx.graphics.getWidth( ) * 5f,
				Gdx.graphics.getHeight( ) * 5f ), Gdx.graphics.getWidth( ),
				Gdx.graphics.getHeight( ), level.world );
		level.player1 = new PlayerBuilder( ).name( "player1" )
				.world( level.world ).position( 0, 100 ).buildPlayer( );
		level.player2 = new PlayerBuilder( ).name( "player2" )
				.world( level.world ).position( 0, 100 ).buildPlayer( );

		level.root = new SkeletonBuilder( level.world ).buildRoot( );

		buildGround( );
		// buildDynamicSkeleton();
		buildHazardSkeleton( );
		complexPlatform( );
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
				.build( );
		level.root.addSkeleton( skeleton );
		skeleton.setMoverAtCurrentState( new RotateTweenMover( skeleton, 10,
				Util.TWO_PI, 0, true ) );

		// HazardBuilder hb = new HazardBuilder(level.world);
		Fire f = new Fire( "fire1", new Vector2( 500, 350 ), 100, 100,
				level.world, true );
		skeleton.addHazard( f );

	}

	void buildDynamicSkeleton( ) {
		SkeletonBuilder sb = new SkeletonBuilder( level.world );
		Skeleton dyn_skeleton = sb.name( "dyn_skeleton" ).position( -100, 300 )
				.density( 0.1f ).dynamic( ).build( );
		PlatformBuilder pb = new PlatformBuilder( level.world );

		Platform tp = pb.name( "kin_on_dyn_skele" ).dimensions( 5, 1 )
				.position( 0, 50 ).buildTilePlatform( );
		dyn_skeleton.addKinematicPlatform( tp );

		Platform balloon = buildBalloon( 0, 700 );
		dyn_skeleton.addDynamicPlatform( balloon );

		StructureScrew s = new StructureScrew( "ss",
				dyn_skeleton.getPositionPixel( ), 100, level.world );
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
				.getPositionPixel( ).add( 0, 250 ), Vector2.Zero, .4f, true,
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
}
