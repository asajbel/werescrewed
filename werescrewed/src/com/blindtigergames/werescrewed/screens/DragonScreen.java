package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TargetImpulseMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.hazard.Fire;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.particles.Steam;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.util.Util;

public class DragonScreen extends Screen {

	public DragonScreen( ) {
		super( );
		String filename = "data/levels/dragon.xml";
		//level = new LevelFactory( ).load( filename );
		level = new Level( );
		
		level.camera = new Camera( Gdx.graphics.getWidth( ), Gdx.graphics.getHeight( ), level.world);
		level.player1 = new PlayerBuilder( ).name( "player1" ).world( level.world )
				.position( 0, 100 ).buildPlayer( );
		level.player2 = new PlayerBuilder( ).name( "player2" ).world( level.world )
				.position( 0, 100  ).buildPlayer( );
		
		level.root = new SkeletonBuilder(level.world).buildRoot( );
		
		buildGround();
		buildDynamicSkeleton();
	}
	
	void buildGround(){
		SkeletonBuilder sb = new SkeletonBuilder( level.world );
		Skeleton skeleton = sb.name( "skeleton" ).build( );
		PlatformBuilder pb = new PlatformBuilder( level.world );
		TiledPlatform tp = pb.name( "ground" ).dimensions( 100,3 ).buildTilePlatform( );
		skeleton.addPlatform( tp );
		level.root.addSkeleton( skeleton );
	}
	
	void buildDynamicSkeleton(){
		SkeletonBuilder sb = new SkeletonBuilder( level.world );
		Skeleton dyn_skeleton = sb.name( "dyn_skeleton" ).position( -100,300 ).density( 0.1f ).dynamic( ).build( );
		PlatformBuilder pb = new PlatformBuilder( level.world );
		
		Platform tp = pb.name( "kin_on_dyn_skele" ).dimensions( 5,1 ).position( 0,50 ).buildTilePlatform( );
		dyn_skeleton.addKinematicPlatform( tp );
		
		Platform balloon = buildBalloon( 0, 700 );
		dyn_skeleton.addDynamicPlatform( balloon );
		
		StructureScrew s = new StructureScrew( "ss", dyn_skeleton.getPositionPixel( ),
				100, level.world );
		s.addWeldJoint( dyn_skeleton );
		s.addWeldJoint( balloon );
		dyn_skeleton.addScrewForDraw( s );
		
		
		level.root.addSkeleton( dyn_skeleton );
	}
	
	Platform buildBalloon(float x, float y){
		Vector2 posPix = new Vector2(x, y );
		Texture texture = WereScrewedGame.manager.get( "data/common/sawblade.png", Texture.class );
		Platform balloon = new Platform( "balloon", new Vector2(x, y ), texture, level.world );
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set( posPix.cpy().mul( Util.PIXEL_TO_BOX ));
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
		//balloon.body.setGravityScale( 0 );

		circle.dispose( );
		
		balloon.setMoverAtCurrentState( new TargetImpulseMover( balloon.getPositionPixel( ).add( 0,250 ), Vector2.Zero, .4f, true, 100 ) );
		
		return balloon;
	}
}
